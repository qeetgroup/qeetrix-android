# Radio group

`QeetrixRadioGroup` · `components.radiogroup` · `@qeetrix/react`: RadioGroup, Radio
([Radio](https://ui.qeet.in/components/radio)) · sample: Forms → Radio group

One choice from a set, always answered.

```kotlin
QeetrixRadioGroup(cycle, { cycle = it },
    listOf(QeetrixOption("monthly", "Monthly"), QeetrixOption("yearly", "Yearly", description = "Two months free.")),
    label = "Billing cycle")
```

## API

| `@qeetrix/react` | Android |
|:--|:--|
| `value` / `onValueChange` (generic) | `value: T` / `onValueChange: (T) -> Unit` |
| `Radio` children (`value`, `label`, `description`, `icon`, `disabled`) | `options: List<QeetrixOption<T>>` |
| `label` (the question), `description`, `error` | the same |
| `orientation`, `variant` (`tile`), `columns` | `QeetrixChoiceOrientation`, `QeetrixChoiceVariant`, `columns` |
| `disabled`, `readOnly`, `required` | `enabled` (inverted), `readOnly`, `required` |

Old `selected` / `onSelect` / `message` / `optional` became `value` / `onValueChange` / `error` (a radio group is
always answered, so it has no "(optional)").

## Look (design rules §9, radio.css)

A 16 dp circle with a 1 dp `color.border.strong` edge on `color.bg.input`; hover `color.border.strong-hover`;
selected, a 5 dp `color.bg.brand.bold` ring — the ring is the border, so the centre stays white; an error,
`color.border.danger`, the chosen circle's ring too and under the pointer (radio.css 0.4.0, QTRX-161); disabled `color.bg.disabled`, and a `color.icon.disabled` ring when
selected. The edge's colour and width change in `motion.duration.fast`. Rows, the wrapping row and tiles are as
[Checkbox](Checkbox.md)'s.

## Accessibility

A selectable group under its question (a heading, "required" when `required`); each option is a radio button
read with its description and "selected" or "not selected". The error is carried by every option. Keyboard focus
draws the Ember frame 2 dp outside the circle.

## Platform differences

- **Arrow keys** move focus between options (Android's focus traversal); Enter or Space selects. The web's arrows
  move and select at once.
- **Rows** are 48 dp touch targets and sit flush.

Related: [Checkbox](Checkbox.md), [Select](Select.md), [Chip](Chip.md).
