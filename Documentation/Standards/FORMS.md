# Forms

How Qeetrix's form inputs are built and how apps compose them. The decision record is
[0014](../Decisions/0014-form-components.md); the look is `@qeetrix/react`'s Field and Input (field.css,
input.css) and design rules §8; each component's page is in [Components](../Components/README.md).

## Field composition

Every input is one field: the label above, the control, then the help text or the error, `space.gap.xs` (4 dp)
apart.

- **Label** — `label.sm` in `color.text.subtle` (`color.text.disabled` when disabled). An optional field says
  "(optional)" (`qx_optional`, `color.text.subtlest`); fields carry no asterisks, and `required` adds "required"
  (`qx_required`) to the name TalkBack reads. The label is drawn **inside the control's own layout** (a text
  field's decorator, a select's trigger), so it is part of the control for TalkBack and tapping it reaches the
  control. `hideLabel` (a toolbar search with a visible icon) keeps it as the name without drawing it; `help` adds
  an ⓘ button after it ("About {label}", `qx_field_help`) that opens the help in a popover.
- **Control** — the filled box every field shares (`FieldBox`, input.css `.qx-input`): `color.bg.neutral.subtle`, a
  1 dp `color.border.strong` line along the bottom, 4 dp top corners and a square bottom (`QeetrixShapes.Field`),
  `size.control.lg` tall (32 / 40 / 44 dp with the density; a minimum, so larger text grows it), 12 dp inline
  padding, 8 dp between an icon, an adornment and the value, the value in `body.md`.

  | State | Fill | Bottom line | Frame |
  |:--|:--|:--|:--|
  | Rest | `color.bg.neutral.subtle` | 1 dp `color.border.strong` | — |
  | Hover | `color.bg.neutral.subtle-hover` | 1 dp `color.border.strong` | — |
  | Focus | `color.bg.neutral.subtle` | replaced by the frame | 2 dp `color.border.focus`, inside |
  | Invalid | as rest | replaced by the frame | 2 dp `color.border.danger`, inside |
  | Read-only | none | 1 dp `color.border.subtle` | — |
  | Disabled | `color.bg.disabled` | 1 dp `color.border.disabled` | — |

- **Help text** — `body.sm` in `color.text.subtle` (`color.text.subtlest` when disabled, so a reason stays
  readable).
- **Error** — a sharp 16 dp `circle-alert` in `color.icon.danger` and the message in `body.sm` / `color.text.danger`.
  It **replaces** the help text while it shows.

Rules for a new input:

1. Build on the internal field parts in `components/input/QeetrixFieldParts.kt`: `FieldFrame` for the column and its
   help text or error, `LabelledControl` for the label inside the control, `FieldBox` for the box and its states,
   `FieldAction` for a tool inside the field (a small neutral ghost icon button). Do not lay out a label or an
   error by hand.
2. Take `label`, `placeholder`, `description`, `error`, `invalid`, `optional`, `required`, `hideLabel`, `help`,
   `enabled` and `readOnly` (where they apply), named and typed as in `QeetrixTextField`.
3. Read `LocalFieldGroup`: disable with the enclosing `QeetrixFieldSet` (`enabled && group.enabled`); an option in a
   choice group carries the group's error and turns its border `color.border.danger`.
4. A control Qeetrix does not provide goes in `QeetrixField` with `Modifier.qeetrixFieldControl()` on the element
   TalkBack focuses.
5. Checkbox, radio and switch rows are `ChoiceRow`s (`components/input/QeetrixSelectionParts.kt`): the control, then
   its label 8 dp after it, the whole row the target and at least 48 dp tall; a question with options is a
   `ChoiceGroup`.

## State ownership

The caller owns every value; Qeetrix holds only what is presentation.

| Input | Caller owns | Qeetrix holds |
|:--|:--|:--|
| Text field, textarea, password, OTP | A `TextFieldState` | Interaction state; the password's visibility when the app does not pass `visible` (saved, never the text) |
| Search field | A `TextFieldState` | Interaction state and the typing pause; no history ([DATA_DISPLAY_AND_COLLECTIONS.md](DATA_DISPLAY_AND_COLLECTIONS.md)) |
| Checkbox, switch | `checked` (and `indeterminate`) and `onCheckedChange` | Interaction state |
| Checkbox group | `value: Set<T>` and `onValueChange` | Interaction state |
| Radio group, select | `value: T` and `onValueChange` | Whether the list is open |
| Number field | `Int?` / `BigDecimal?` and `onValueChange` | The text being typed, until it is settled |

There is no form model, controller or validation engine. A form is a `QeetrixForm` — a column of fields
`space.form.gap` apart (`QeetrixTheme.density.spaceFormGap`: 12 / 16 / 20 dp) — ending in `QeetrixFormActions`.

## Validation presentation

The app decides when and what; Qeetrix decides how it looks and is announced.

- Pass the message as `error`. It frames the box in `color.border.danger`, sets the control's error state for
  accessibility services, replaces the help text and is announced at once (an assertive live region). `invalid`
  marks a field invalid without a message of its own (one message for a pair of fields). There are no warning or
  success messages: `@qeetrix/react` has errors only.
- Write the error as what to do ("Enter a work email, like asha@acme.in"), never the rule alone. A placeholder shows
  an example ("e.g. Acme Pvt Ltd"), never an instruction or the label.
- A rule about a group ("Choose at least one") is the `error` of the `QeetrixCheckboxGroup` or `QeetrixRadioGroup`;
  every option in it carries it.
- After a failed submit, pass the problems to `QeetrixForm(errors = …)`: it shows the error summary above the
  fields, one `QeetrixFormError` per problem, worded as each field's own error and in field order. The summary takes
  focus; each item's `onClick` moves focus to its field. Keep each field's own error.
- The number field corrects range and precision when it settles rather than reporting them; apps report their own
  rules as errors.

## Focus and the keyboard

- **Moving focus.** Each input's `modifier` takes a `FocusRequester`; `requestFocus()` focuses the control and a
  focused field brings itself into view. Pass the same modifier for ordering and testing.
- **IME actions.** Set `imeAction` (`Next`, `Done`, `Send`) in `keyboardOptions` and handle it in
  `onKeyboardAction`; the number field takes `imeAction` alone and settles on it. The OTP input uses `Done`, the
  search field `Search`.
- **Keyboard types** are chosen by the component where the content decides it (password, OTP, number, search);
  elsewhere pass one (`KeyboardType.Email`, `Phone`, `Uri`).
- **The on-screen keyboard** must not cover the focused field: apps draw edge to edge and pad for
  `WindowInsets.safeDrawing` (or `imePadding()`), as the sample does. Qeetrix does not resize windows.
- **Hardware keyboards and D-pads.** Focus order is layout order. A field draws the Ember frame inside it whenever
  it has focus (the caret needs context); other controls draw their frame for keyboard focus (design rules §10).
  Arrow keys move between radio options (Android's focus traversal) and Enter or Space selects. A select opens with
  Enter or Space and closes with Escape or Back. The number field steps with Up and Down, by `largeStep` with Shift,
  Page Up and Page Down, and goes to `min` and `max` with Home and End.

## Accessibility

The full table is in [ACCESSIBILITY.md](ACCESSIBILITY.md). For forms:

- One focusable element per control, named by its label; "required" read with it (`qx_required`).
- The help text or the error follows the control in reading order; the help text is not composed while an error
  shows, so the control is described by the error alone.
- Test against the **merged** semantics tree (`onNodeWithText("Label")` without `useUnmergedTree`): that
  is what the accessibility delegate exposes. The unmerged tree still contains labels a control hides.
- Built-in strings are `qx_` resources an app translates: `qx_required`, `qx_optional`, `qx_field_help`,
  `qx_show_password`, `qx_password_shown`, `qx_password_hidden`, `qx_caps_lock`, `qx_decrease`, `qx_increase`,
  `qx_search`, `qx_clear_search`, `qx_error_summary_title`, `qx_alert_danger`.

## Secure input

Passwords and one-time codes are the app's secrets. In Qeetrix:

- They live only in the caller's `TextFieldState`. Components keep no copy, never log them, never put them
  in a label, description, content description, state description, error or test tag, and never save them
  (`rememberSaveable` holds only the password's visibility).
- The password field is Compose's `BasicSecureTextField`: masked (the last character typed shows briefly where the
  device allows), copy and cut disabled, password semantics, suggestions and auto-correction off. Show password
  reveals it in the same field — text, cursor, focus and keyboard stay — never by swapping in a plain field.
- Never cap a password's length or block paste: password managers must work (WCAG 3.3.8).
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
| `QeetrixPasswordInput`, `autoComplete = CurrentPassword` (default) | `Password` |
| `QeetrixPasswordInput`, `autoComplete = NewPassword` | `NewPassword` (managers offer a generated password and save it) |
| `QeetrixPasswordInput`, `autoComplete = Off` | None: the field is marked not for AutoFill (`ContentDataType.None`) |
| `QeetrixOTPInput`, `smsCodeAutofill = true` (default) | `SmsOtpCode`; none when false |
| `QeetrixSearchField` | None: a query is not a profile value |
| `QeetrixTextField`, `QeetrixTextarea` | `contentType` parameter (`Username`, `EmailAddress`, `PersonFullName`, `PhoneNumber`, `PostalAddress`, …); otherwise Compose's type for the keyboard (an email keyboard is `EmailAddress`) |

- Pass the type through the `contentType` parameter, not `Modifier.semantics` on `modifier`: a Qeetrix
  input's `modifier` is the whole field's, and AutoFill reads only the text field's own node.
- A sign-in form pairs a `Username` (or `EmailAddress`) field with a `CurrentPassword` one, in that order, so
  a password manager offers the saved credential and saves a new one.
- Never turn AutoFill off for credentials. `Off` and `smsCodeAutofill = false` are for secrets AutoFill should
  never hold or codes that never arrive by SMS.
- Whether a provider offers anything depends on the device; it is a device check, not a unit test.

## API conventions

As [0010](../Decisions/0010-component-api-conventions.md), plus:

- The field parameters have the same names, types and meaning in every input — `label`, `placeholder`,
  `description`, `error`, `invalid`, `optional`, `required`, `hideLabel`, `help`, `enabled`, `readOnly` — the
  `@qeetrix/react` Field's names (its `disabled` is `enabled`). They come before keyboard and interaction
  parameters; a callback such as `onValueComplete` is last.
- Choices are typed: `QeetrixOption<T>` (and `QeetrixOptionGroup<T>` for a select's groups) and a `T` selection;
  "nothing selected" is a value no option has.
- Numbers are `Int?` or `BigDecimal?`, never `Float` or `Double`.

## Testing

Each input has, on the JVM (Robolectric):

- **Behaviour** — typing reaches the caller's state, transformations, settle rules, callbacks
  (`onValueComplete`, `onValueChange`, `onSearch`), disabled and read-only.
- **Semantics** — the name from the label, "required", error state, live regions, role and state, custom
  actions, content type; on the merged tree.
- **Secure input** — no semantics node exposes a password or code; synthetic values only.
- **Screenshots** — light, dark, 200 % text and right to left, with empty or synthetic values, recorded
  and reviewed locally and never committed ([0012](../Decisions/0012-visual-regression-testing.md)).
- **Integration** — the sample's form tests (`sample/src/test`): an error summary that focuses the
  invalid field, and a two-step sign-in with an OTP.

TalkBack speech, AutoFill offers and IME behaviour on real keyboards are device checks, listed in
[ACCESSIBILITY.md](ACCESSIBILITY.md).
