# Forms

How Qeetrix's form inputs are built and how apps compose them. The decision record is
[0014](../Decisions/0014-form-components.md); each component's page is in
[Components](../Components/README.md).

## Field composition

Every input is one field: label, control, description, message, 6 dp apart.

- **Label** — `text-sm font-medium`, with a red asterisk for `required` or "(optional)" for `optional`.
  It is drawn **inside the control's own layout** (a text field's decorator, a select's trigger), so it is
  part of the control for TalkBack and tapping it reaches the control.
- **Control** — the field boundary (`FieldBox`, the web's `fieldSurface`): exactly the density's control
  height for one line, `component.input.*` for background and boundary per state and message tone, a
  2 dp focus boundary, dashed when read-only.
- **Description** — help text in the caption role, after the control.
- **Message** — a `QeetrixFieldMessage` (error, warning or success) with its icon, after the description.

Rules for a new input:

1. Build on the internal field parts in `components/input/QeetrixFieldParts.kt`: `FieldFrame` for the
   column and its description and message, `LabelledControl` inside the control, `FieldBox` for the
   boundary, `FieldAction` for a button inside the field. Do not lay out a label or message by hand.
2. Take `label`, `description`, `message`, `enabled`, `readOnly` (where it applies), `required` and
   `optional`, named as in `QeetrixTextField`.
3. Read `LocalFieldGroup`: disable with the enclosing `QeetrixFieldSet` (`enabled && group.enabled`); a
   selection control carries the group's error message.
4. A control Qeetrix does not provide goes in `QeetrixField` with `Modifier.qeetrixFieldControl()` on the
   element TalkBack focuses.
5. Selection rows (checkbox, radio, switch) are at least 48 dp tall and use `Modifier.selectionRow()`, so
   a field set stacks them flush instead of at the field gap.

## State ownership

The caller owns every value; Qeetrix holds only what is presentation.

| Input | Caller owns | Qeetrix holds |
|:--|:--|:--|
| Text field, textarea, password, OTP | A `TextFieldState` | Interaction state; the password's reveal flag (saved, never the text) |
| Search field | A `TextFieldState` | Interaction state; no history ([DATA_DISPLAY_AND_COLLECTIONS.md](DATA_DISPLAY_AND_COLLECTIONS.md)) |
| Checkbox, switch | `checked` / `ToggleableState` and the change callback | Interaction state |
| Radio group, select | The selected `T` and `onSelect` | Whether the list is open |
| Number field | `Int?` / `BigDecimal?` and `onValueChange` | The text being typed, until it is committed |

There is no form model, controller or validation engine. A form is a `Column` spaced by
`QeetrixTheme.density.fieldGap`.

## Validation presentation

The app decides when and what; Qeetrix decides how it looks and is announced.

- Pass a `QeetrixFieldMessage` to the field. `Error` turns the boundary red, sets the control's error
  state for accessibility services and is announced at once (assertive live region); `Warning` and
  `Success` colour the boundary and are announced politely. Every tone has its icon and words — colour is
  never the only signal.
- A rule about a group ("Choose at least one") is the `message` of the `QeetrixFieldSet` or
  `QeetrixRadioGroup`; every checkbox and radio in it carries it as its error.
- After a failed submit, show `QeetrixFormErrorSummary` above the form with one `QeetrixFormError` per
  problem, keep each field's own message, and move focus to the summary. Each error's action focuses its
  field.
- The number field corrects range and precision when it commits rather than reporting them; apps report
  their own rules as messages.

## Focus and the keyboard

- **Moving focus.** Each input's `modifier` takes a `FocusRequester`; `requestFocus()` focuses the
  control and a focused field brings itself into view. Pass the same modifier for ordering and testing.
- **IME actions.** Set `imeAction` (`Next`, `Done`, `Send`) in `keyboardOptions` and handle it in
  `onKeyboardAction`; the number field takes `imeAction` alone and commits on it. The OTP input uses
  `Done`.
- **Keyboard types** are chosen by the component where the content decides it (password, OTP, number);
  elsewhere pass one (`KeyboardType.Email`, `Phone`, `Uri`).
- **The on-screen keyboard** must not cover the focused field: apps draw edge to edge and pad for
  `WindowInsets.safeDrawing` (or `imePadding()`), as the sample does. Qeetrix does not resize windows.
- **Hardware keyboards and D-pads.** Focus order is layout order; every control draws the focus ring for
  keyboard focus. Each radio option is its own focus stop. A select opens with Enter or Space and closes
  with Escape or Back; the number field steps with the Up and Down arrows.

## Accessibility

The full table is in [ACCESSIBILITY.md](ACCESSIBILITY.md). For forms:

- One focusable element per control, named by its label; "required" read with it (`qx_required`).
- The description and message follow the control in reading order.
- Test against the **merged** semantics tree (`onNodeWithText("Label")` without `useUnmergedTree`): that
  is what the accessibility delegate exposes. The unmerged tree still contains labels a control hides.
- Built-in strings are `qx_` resources an app translates: `qx_required`, `qx_optional`,
  `qx_show_password`, `qx_hide_password`, `qx_one_time_code`, `qx_decrease`, `qx_increase`,
  `qx_error_summary_title`.

## Secure input

Passwords and one-time codes are the app's secrets. In Qeetrix:

- They live only in the caller's `TextFieldState`. Components keep no copy, never log them, never put them
  in a label, description, content description, state description, error or test tag, and never save them
  (`rememberSaveable` holds only the reveal flag).
- The password field is Compose's `BasicSecureTextField`: masked per the device's "Show passwords"
  setting, copy and cut disabled, password semantics, suggestions and auto-correction off. It is revealed
  in place, never by swapping in a plain field.
- Compose exposes a secure field's text to accessibility services marked as a password, as Android does
  for its own password fields. That is the platform's behaviour, not a Qeetrix leak.
- Qeetrix never reads SMS, requests a permission, or uses SMS Retriever or Play services. No custom
  encryption and no credential storage.
- Tests, previews, samples and screenshots use empty or obviously synthetic values only ("synthetic",
  "012345"). `QeetrixPasswordInputTest` scans every semantics node for the typed password.

## AutoFill

Compose fills fields through their `ContentType`. Qeetrix sets it where the component knows the content
and lets the app set it everywhere else.

| Field | Content type |
|:--|:--|
| `QeetrixPasswordInput`, `purpose = Current` | `Password` |
| `QeetrixPasswordInput`, `purpose = New` | `NewPassword` (managers offer a generated password and save it) |
| `QeetrixOTPInput`, `smsCodeAutofill = true` | `SmsOtpCode`; none when false |
| `QeetrixSearchField` | None: a query is not a profile value |
| `QeetrixTextField`, `QeetrixTextarea` | `contentType` parameter (`Username`, `EmailAddress`, `PersonFullName`, `PhoneNumber`, `PostalAddress`, …); otherwise Compose's type for the keyboard (an email keyboard is `EmailAddress`) |

- Pass the type through the `contentType` parameter, not `Modifier.semantics` on `modifier`: a Qeetrix
  input's `modifier` is the whole field's, and AutoFill reads only the text field's own node.
- A sign-in form pairs a `Username` (or `EmailAddress`) field with a `Current` password, in that order, so
  a password manager offers the saved credential and saves a new one.
- Never turn AutoFill off for credentials. The OTP input's `smsCodeAutofill = false` is for codes that
  never arrive by SMS.
- Whether a provider offers anything depends on the device; it is a device check, not a unit test.

## API conventions

As [0010](../Decisions/0010-component-api-conventions.md), plus:

- The field parameters have the same names, types and meaning in every input — `label`, `placeholder`,
  `description`, `message`, `enabled`, `readOnly`, `required`, `optional` — and come before keyboard and
  interaction parameters; a callback such as `onComplete` is last.
- Choices are typed: `QeetrixOption<T>` and a `T` selection; "nothing selected" is a value no option has.
- Numbers are `Int?` or `BigDecimal?`, never `Float` or `Double`.

## Testing

Each input has, on the JVM (Robolectric):

- **Behaviour** — typing reaches the caller's state, transformations, commit rules, callbacks
  (`onComplete`, `onSelect`), disabled and read-only.
- **Semantics** — the name from the label, "required", error state, live regions, role and state, custom
  actions, content type; on the merged tree.
- **Secure input** — no semantics node exposes a password or code; synthetic values only.
- **Screenshots** — light, dark, 200 % text and right to left, with empty or synthetic values, recorded
  and reviewed locally and never committed ([0012](../Decisions/0012-visual-regression-testing.md)).
- **Integration** — the sample's form tests (`sample/src/test`): an error summary that focuses the
  invalid field, and a two-step sign-in with an OTP.

TalkBack speech, AutoFill offers and IME behaviour on real keyboards are device checks, listed in
[ACCESSIBILITY.md](ACCESSIBILITY.md).
