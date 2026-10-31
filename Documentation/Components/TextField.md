# Text field

`QeetrixTextField` · `components.input` · `@qeetrix/react`: TextField
([Text field](https://ui.qeet.in/components/text-field)) · sample: Forms → Text field

A single-line text field with its label, help text and error — Field and Input in one, the default way to build
a form row.

```kotlin
val email = rememberTextFieldState()
QeetrixTextField(
    email,
    label = "Work email",
    placeholder = "e.g. asha@acme.in",
    icon = QeetrixIconName.Mail,
    error = emailError, // "Enter a work email, like asha@acme.in."
    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Next),
)
```

## API

| `@qeetrix/react` | Android |
|:--|:--|
| `value` / `defaultValue` / `onValueChange` | `state: TextFieldState` (Compose's controlled text model; read `state.text`) |
| `label`, `description`, `error`, `invalid`, `optional`, `hideLabel`, `help` | the same (see [Field](Field.md)) |
| `required`, `readOnly`, `disabled` | `required`, `readOnly`, `enabled` (inverted) |
| `placeholder` | `placeholder` — an example, never an instruction |
| `icon` | `icon: QeetrixIconName?` — sharp, 16 dp, `color.icon.subtle`, decorative |
| `startAdornment`, `endAdornment` | `startAdornment`, `endAdornment: (@Composable () -> Unit)?` — affixes take `color.text.subtle` |
| `type`, `autoComplete`, native attributes | `keyboardOptions`, `contentType`, `onKeyboardAction`, `inputTransformation` |

Removed from the old API: `message` with tones (react has errors only — use `error`), `leading` / `trailing`
(use `icon` and the adornments).

## Look (design rules §8, input.css)

| State | Fill | Bottom line | Frame |
|:--|:--|:--|:--|
| Rest | `color.bg.neutral.subtle` | 1 dp `color.border.strong` | — |
| Hover | `color.bg.neutral.subtle-hover` | 1 dp `color.border.strong` | — |
| Focus (any focus: the caret needs context) | `color.bg.neutral.subtle` | hidden | 2 dp `color.border.focus` inside |
| Invalid | as rest | hidden | 2 dp `color.border.danger` inside |
| Read-only | none | 1 dp `color.border.strong` (input.css 0.4.0, QTRX-175) | — |
| Disabled | `color.bg.disabled` | 1 dp `color.border.disabled` | — |

4 dp top corners and a square bottom (`QeetrixShapes.Field`), `size.control.lg` tall (32 / 40 / 44 dp with the
density, growing with the font scale), 12 dp inline padding, 8 dp between an icon, an adornment and the value, the
value in `body.md`. Focus wins over invalid, as the CSS orders them.

## Accessibility

- The label is drawn inside the field's own control, so TalkBack reads it as the field's name and tapping it
  focuses the field.
- An error sets the field's error state and is announced at once; it replaces the description.
- `contentType` tells AutoFill what the field holds; without one Compose derives it from the keyboard type.
- With `hideLabel` the label still names the field, and the field is centred in a 48 dp touch height.

## Platform differences

- **Text size.** `body.md` at every window width (the old Android port followed the old web's 16 → 14 sp).
- **State.** Compose's `TextFieldState` replaces react's `value` / `onValueChange`.
- **Required** and **help**: see [Field](Field.md).

Related: [Field](Field.md), [Textarea](Textarea.md), [Search field](SearchField.md), [Form](Form.md).

## Placeholders at large text (QTRX-197)

A single-line field's placeholder ends in an ellipsis where it doesn't fit (finding Q-5, gone); keep placeholders short examples (design rules §8).
