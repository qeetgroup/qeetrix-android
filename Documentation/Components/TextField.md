# Text field

`QeetrixTextField` · `components.input` · web: [Input](https://ui.qeet.in/docs/components/input) ·
sample: Forms & inputs → Text field

A single-line text input with its label, help text and validation message — the web's `Input` inside a
vertical `Field`.

```kotlin
val email = rememberTextFieldState()
QeetrixTextField(
    email,
    label = "Work email",
    placeholder = "you@company.com",
    message = if (invalid) QeetrixFieldMessage("Enter an email address like name@company.com.") else null,
    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Next),
)
```

## Usage

- The text lives in a `TextFieldState` you own (`rememberTextFieldState()`), the state model of
  Compose's current text APIs; read it with `state.text`. Filter input with an `InputTransformation`.
- Slots: `label`, `placeholder`, `description` (help), `message`, and `leading` / `trailing` content
  (an icon, a clear button).
- Validation is yours: compute a `QeetrixFieldMessage` from the state. There is no form framework; see
  [Form](Form.md) for the patterns.
- `modifier` is the whole field's; `Modifier.focusRequester(requester)` on it focuses the text.
- Other text has its own component: [password](PasswordInput.md), [one-time code](OTPInput.md),
  [multi-line](Textarea.md), [number](NumberField.md). Not in Qeetrix yet: currency and search fields.
- Built on the field parts every Qeetrix input shares ([Field](Field.md)); inside a disabled
  `QeetrixFieldSet` it is disabled too.

## States

- **Message tones** — `Error` (red boundary, invalid), `Warning`, `Success` — each with its icon.
- **Read-only**: dashed boundary; the value can be focused, selected and copied.
- **Disabled**: faded; cannot be focused.
- **Focused**: the boundary thickens to 2 dp in the focus colour (red when invalid).
- `required` adds a red asterisk; `optional` adds "(optional)" (`qx_optional`).

## Accessibility

- The label is drawn inside the field's own control, so TalkBack reads it as the field's name and
  tapping it focuses the field. TalkBack reads the label, the text, then the description.
- An error sets the field's error state, and the message is a live region announced assertively;
  warnings and successes are announced politely.
- `required` is read as "required" after the label (`qx_required`, the asterisk's description).
- Height is exactly the control height of the density (32 dp by default) and grows with the font
  scale.

## Theme and tokens

`component.input.*` (`QeetrixInputComponentTokens`): background (per theme, disabled, read-only),
boundary per state and tone, placeholder, corner, and the height through the density.

## Platform differences

- **Text size.** Android follows the web: 16 sp below a 768 dp window, 14 sp from there. iOS keeps
  16 pt everywhere.
- **Required.** The web announces the control's `required`; Android and iOS add the word to the
  label that names the field (Compose has no required property).
- **Announcements.** The web uses `role="alert"` / `role="status"`; Android uses live regions of the
  same politeness; iOS posts VoiceOver announcements.
- **Dashes.** The read-only boundary's dash is approximated as 3 dp dashes and gaps.

Related: [Field](Field.md), [Textarea](Textarea.md), [Form](Form.md).
