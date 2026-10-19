# Switch

`QeetrixSwitch` · `components.switchcontrol` · web: [Switch](https://ui.qeet.in/docs/components/switch) ·
sample: Forms & inputs → Switch

An on/off control for a setting that takes effect immediately. The package is `switchcontrol` because
`switch` is a Java keyword.

```kotlin
var alerts by rememberSaveable { mutableStateOf(true) }
QeetrixSwitch(alerts, { alerts = it }, label = "Email alerts", description = "Sent within a minute of the event.")
```

## Usage

- Prefer the overload with a `label` (and optional `description`): the whole row toggles and is read
  as one control. A bare switch needs a name from its context — a `toggleable` row, or a content
  description.
- State is hoisted: `checked` plus `onCheckedChange`. Pass `onCheckedChange = null` to show a state the
  user cannot change.
- Sizes: `Default` (32 × 18.4 dp track, 16 dp thumb) and `Sm` (24 × 14 dp, 12 dp thumb).
- In a [`QeetrixFieldSet`](Field.md) labelled switches sit flush, as checkbox rows do, and a disabled
  set disables them.

## States

- Off / on: the thumb moves to the end and the track fills with the brand colour.
- Disabled: faded. Invalid (`invalid = true`): the red boundary, for a required choice left off.
- Hovered (pointer): a checked track takes its hover colour. Keyboard focus draws the focus ring.

## Accessibility

- A switch role with its on/off state; double-tap toggles. The labelled overload reads the label, the
  description, "switch" and the state together.
- The state is shown by the thumb's side and the track's fill, not by hue alone.
- 48 × 48 dp touch target around the drawn switch; the labelled row is a larger target.
- Right to left, the thumb travels the other way.

## Theme and tokens

`component.switch.*` (`QeetrixSwitchComponentTokens`): track and thumb colours per state, and the
thumb size; `component.input.border-invalid` for the invalid boundary.

## Platform differences

- **Layout.** The web places the switch where the form puts it. Android, like iOS, puts the label at
  the start and the switch at the end, the system settings convention.
- **Target.** The web gives its switch a larger pointer target; Android reserves 48 dp, and the
  labelled row is the target, as a web `<label>` is.

Related: [Checkbox](Checkbox.md), [Field](Field.md).
