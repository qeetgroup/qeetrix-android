# Checkbox

`QeetrixCheckbox`, `QeetrixCheckboxGroup` · `components.checkbox` · `@qeetrix/react`: Checkbox, CheckboxGroup
([Checkbox](https://ui.qeet.in/components/checkbox)) · sample: Forms → Checkbox

Turn one option on or off, or pick several under one question.

```kotlin
QeetrixCheckbox(updates, { updates = it }, label = "Product updates", description = "About once a month.")

QeetrixCheckboxGroup(channels, { channels = it },
    listOf(QeetrixOption("email", "Email"), QeetrixOption("sms", "SMS")), label = "Notify me by",
    error = if (channels.isEmpty()) "Choose at least one way to reach you." else null)
```

## API

| `@qeetrix/react` | Android |
|:--|:--|
| `checked` / `onCheckedChange` | `checked` / `onCheckedChange: ((Boolean) -> Unit)?` |
| `indeterminate` | `indeterminate` — a tap calls `onCheckedChange(!checked)` |
| `label`, `description`, `icon` | `label`, `description`, `icon` |
| no label + `aria-label` | the overload without `label`; name it from its row |
| `disabled`, `readOnly` | `enabled` (inverted), `readOnly` (or `onCheckedChange = null`) |
| CheckboxGroup `value` / `onValueChange`, `label`, `description`, `error` | `value: Set<T>` / `onValueChange`, `options: List<QeetrixOption<T>>`, `label`, `description`, `error` |
| `orientation`: `vertical` · `horizontal` | `QeetrixChoiceOrientation.Vertical` / `Horizontal` |
| `variant`: `default` · `tile`, `columns` 1–4 | `QeetrixChoiceVariant.Default` / `Tile`, `columns` |
| `parent` + `allValues` | not ported: compose a select-all with `indeterminate` (see the sample) |

`QeetrixTriStateCheckbox` is gone: use `indeterminate`.

## Look (design rules §9, checkbox.css)

- A 16 dp box, 2 dp corners, a 1 dp `color.border.strong` edge on `color.bg.input`; hover
  `color.border.strong-hover`. Checked: `color.bg.brand.bold` fill and edge with a white 12 dp sharp tick
  (stroke 3); indeterminate: the same with a dash. Invalid (an `invalid` box, or inside a group or field with an
  error): `color.border.danger`, checked or not and under the pointer too (checkbox.css 0.4.0, QTRX-161). Disabled: `color.bg.disabled`, `color.border.disabled`, a `color.icon.disabled`
  mark, `color.text.disabled`.
- The label follows the box, 8 dp after it, in `body.md`; the description under the label in `body.sm`, 24 dp in.
- **Group**: the question in the field-label style, then the options — stacked, or wrapping 16 dp apart — then the
  description or the error.
- **Tiles**: `color.surface.default` with a 1 dp `color.border.subtle` edge, 4 dp corners, 12 dp padding; the
  icon in a 36 dp `color.bg.neutral.subtle` box, the title at `title.sm`'s weight, the box at the end; chosen,
  `color.bg.selected` with a 2 dp `color.border.brand` edge. As many columns as fit 192 dp tiles, up to `columns`.

## Accessibility

The whole row — label and description — is one checkbox, read with its state ("partially checked" when
indeterminate). A group's question is a heading; its error is carried by every option. Keyboard focus draws the
2 dp Ember frame 2 dp outside the box (round the tile for tiles).

## Platform differences

- **Rows** are 48 dp touch targets and sit flush; the web's rows are 24 px, 4 px apart.

Related: [Radio group](RadioGroup.md), [Switch](Switch.md), [Field](Field.md).
