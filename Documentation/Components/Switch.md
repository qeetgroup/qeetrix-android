# Switch

`QeetrixSwitch` · `components.switchcontrol` · `@qeetrix/react`: Switch
([Switch](https://ui.qeet.in/components/switch)) · sample: Forms → Switch

A setting that turns on or off at once. A setting that waits for a Save button is a [checkbox](Checkbox.md).

```kotlin
QeetrixSwitch(autoRenew, { autoRenew = it }, label = "Auto-renew", description = "Renews on the 1st of each month.")
```

## API

| `@qeetrix/react` | Android |
|:--|:--|
| `checked` / `onCheckedChange` | `checked` / `onCheckedChange: ((Boolean) -> Unit)?` |
| `label`, `description` | `label`, `description` |
| no label + `aria-label` | the overload without `label` |
| `disabled`, `readOnly` | `enabled` (inverted), `readOnly` |
| — | `invalid` (an unset required setting) |

Old `size` (`Default` / `Sm`) is gone: react has one size.

## Look (design rules §9, switch.css)

A 32 × 18 dp track with 2 dp corners and a 1 dp `color.border.strong` edge on `color.bg.input`, and a 12 dp square
thumb with 1 dp corners in `color.icon.subtle`, 2 dp in from the start. On: the track and edge
`color.bg.brand.bold`, the thumb `color.icon.on-brand` — white in dark too (switch.css 0.4.0, QTRX-162) — 16 dp in, on
the end side, mirrored right to left. An invalid switch keeps a `color.border.danger` edge, on or off. The
thumb moves in `motion.duration.normal`; colours change in `motion.duration.fast`. Disabled: `color.bg.disabled`,
`color.border.disabled`, a `color.icon.disabled` thumb. The label **follows** the switch, 8 dp after it; the
description is 40 dp in.

## Accessibility

A native switch, read with its label, description and on/off state; the whole row toggles. Keyboard focus draws
the Ember frame 2 dp outside the track. Nothing moves under reduced motion.

## Platform differences

- **Label placement.** Android settings screens usually put the switch at the end; Qeetrix puts it first, as the
  web does (design rules §9).
- **Rows** are 48 dp touch targets.

Related: [Checkbox](Checkbox.md), [Field](Field.md).

## Haptics

A toggle plays `QeetrixHapticRole.Selection` ([Haptics](../Foundations/HAPTICS.md)), nothing where the system has touch feedback off.
