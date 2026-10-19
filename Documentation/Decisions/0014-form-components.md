# 0014 — Form components: one field, native inputs, the app's validation

**Status:** Accepted, 2026-10-19 (Phase 3). Builds on [0010](0010-component-api-conventions.md).

## Context

Phase 3 adds the form controls: a field and field set, a textarea, password, one-time-code and number
inputs, checkboxes, a radio group and a select, and the patterns that combine them into forms. On the web,
`Field` wraps any control with its label, description and messages and associates them by element ids.
Phase 2's `QeetrixTextField` laid out its own label and message. Two input architectures, or a form
framework, would both be easy to drift into. qeetrix-ios settled the same questions in its ADR 0012; this
record takes its positions where Compose allows.

## Decision

1. **One field.** Internal field parts (`QeetrixFieldParts.kt`) are the single implementation of the
   field layout — label with the required asterisk or "(optional)", control, description, message, 6 dp
   apart — and of the boundary every control draws (`FieldBox`: the web's `fieldSurface`, per state and
   tone). Every Qeetrix input takes `label`, `description`, `message`, `required` and `optional` and is
   built on them. `QeetrixTextField` was rebuilt on them with an unchanged signature. `QeetrixField` is the
   public form of the same parts for a control Qeetrix does not provide.
2. **The label is inside the control.** Compose has no relation between semantics nodes like
   `aria-labelledby`, and setting a text field's text or description for accessibility makes the platform
   treat it as plain text. So each input draws its label inside its own layout — a text field's decorator,
   a select's trigger — where it is part of the control TalkBack focuses, and tapping it reaches the
   control. "Required" is the asterisk's description (`qx_required`), read with the label. A custom control
   inside `QeetrixField` takes `Modifier.qeetrixFieldControl()`, which names it by the label and hides the
   visible label so it is read once. Error messages set the control's error state; messages are live
   regions after the control. A group's message (field set, radio group) is the error of each checkbox and
   radio in it.
3. **Compose foundation is the authority.** Each input is the foundation control, styled — no Material 3:
   - text, textarea, number and one-time code: `BasicTextField` on a `TextFieldState`;
   - password: `BasicSecureTextField` (masked per the system setting, copy and cut disabled, password
     semantics), revealed in place with `TextObfuscationMode.Visible`, never by swapping fields;
   - one-time code: **one** field drawn as cells — a field per cell would break paste, AutoFill and
     TalkBack;
   - checkbox and radio: `triStateToggleable` and `selectable` with their roles, in a `selectableGroup`;
   - select: Qeetrix's own list in a `Popup`. Android has no foundation-level menu, the platform `Spinner`
     is a View and `DropdownMenu` is Material 3's look.
   - AutoFill through `ContentType`: `Password` or `NewPassword` by purpose, `SmsOtpCode` for codes. A code
     field uses `KeyboardType.Number`, not `NumberPassword`, which Compose turns into `ContentType.Password`.
4. **Typed choices.** `QeetrixOption<T>` serves the radio group and the select; the selection is a `T` with
   `onSelect: (T) -> Unit`, so only a value the caller's type can hold is ever selected. "Nothing selected"
   is a value no option has (a nullable `T` and `null`), not a separate API. Option values must be distinct.
5. **The app validates.** Qeetrix presents messages; apps decide them. There is no validation engine, form
   model or controller. Focus is the app's: every input's `modifier` takes a `FocusRequester`, and requesting
   it focuses the control.
6. **Numbers are exact and forgiving.** Values are `Int?` or `BigDecimal?`. The text being typed is kept
   apart from the value, so in-between states are never rejected; committing clamps to the range and
   rounds half to even. Reading is strict and locale-aware (`java.text`'s parser accepts "12abc" as 12),
   accepts any script's digits and ignores bidi format marks.
7. **Secrets stay in the caller's state.** Password and code inputs keep no copy of the text, never log it,
   put it in a description or save it (the reveal flag is the only saved state). Qeetrix never reads SMS,
   asks for a permission or uses Play services. Tests and screenshots use empty or synthetic values only.
8. **Composition only where it adds behaviour.** `QeetrixFieldSet` (legend, group message and enabled
   state, flush selection rows), `QeetrixFormActions` (stacked under 640 dp with the primary on top, a row
   at the end above) and `QeetrixFormErrorSummary` (alert surface, announcement, error actions) are
   components. A form container is not: a `Column` and the density's field gap express it.
9. **Touch targets.** Checkbox, radio, switch and select-option rows are at least 48 dp tall and sit flush,
   instead of the web's 16 px control with a 24 px pointer target. Error-summary entries are full 48 dp
   rows, as iOS's 44 pt rows. Small actions inside a field (reveal, steppers) keep the web's drawn size; the
   reveal button relies on Compose's 48 dp nearest-target hit testing, and the steppers duplicate custom
   accessibility actions and arrow keys on the field.

## Consequences

- One field implementation to keep right; every input reads and looks the same. The rebuild corrected the
  text field's height to exactly the control height (it was 1–2 dp taller), a visual change recorded in
  `CHANGELOG.md`.
- Tests must read the **merged** semantics tree, which is what the accessibility delegate exposes; the
  unmerged tree still contains labels a control has hidden.
- Some web behaviour has a counterpart rather than a copy — per-option focus in the radio group, no pointer
  scrubbing in the number field, no drag-resize in the textarea — and each component page lists them under
  *Platform differences*; [CROSS_PLATFORM_CONTRACT.md](../CROSS_PLATFORM_CONTRACT.md) lists those that also
  differ from iOS.
- TalkBack's speech and a device's AutoFill offers cannot be asserted on the JVM; they are on the device
  checklist in `Documentation/Standards/ACCESSIBILITY.md`.
