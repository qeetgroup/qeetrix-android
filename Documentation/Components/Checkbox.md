# Checkbox

`QeetrixCheckbox`, `QeetrixTriStateCheckbox` · `components.checkbox` · web: [Checkbox](https://ui.qeet.in/docs/components/checkbox) ·
sample: Forms & inputs → Checkbox

A choice that is on or off — or mixed, for a "select all" over a partly selected list.

```kotlin
QeetrixCheckbox(updates, { updates = it }, label = "Product updates", description = "About once a month.")
QeetrixTriStateCheckbox(allState, onClick = { selectAll(allState != ToggleableState.On) }, label = "All members")
```

## Usage

- The labelled overloads make the whole row the control: box at the start, label and description after.
  The bare overloads draw the box alone; give it a name from its context.
- State is yours: `checked` + `onCheckedChange`, or `state` + `onClick` for tri-state. Pass `null` to show a
  state the user cannot change.
- **Mixed.** `QeetrixTriStateCheckbox` shows the state you pass. The usual rule: a tap selects everything
  from unchecked or mixed and clears everything from checked, so mixed comes only from the items.
- Group several in a [`QeetrixFieldSet`](Field.md); its message applies to each. Consent and agreement
  rules are the app's.

## States

Unchecked, checked, mixed (a dash), invalid (`invalid = true`, or the set's error), disabled, read-only.
Checked and mixed are the Ember fill with a light glyph and the darker `border-brand` edge.

## Accessibility

A checkbox role with its state — "partially checked" for mixed. The labelled row is one control reading
label, description and state. Rows are at least 48 dp tall and sit flush in a field set; the bare box
reserves 48 dp.

## Theme and tokens

`component.input.*` at rest (the field's fill and ≥3:1 boundary), `action.primary` and `border.brand`
when chosen, `text.on-brand` for the glyph, `corner.sm`, the `check` and `minus` icons at stroke 3.

## Platform differences

- The web box carries a 24 px pointer target; Android rows are 48 dp touch targets (iOS: 44 pt).
- Android has a checkbox role; iOS has none, and VoiceOver reads its checkbox as a toggle.

Related: [Radio group](RadioGroup.md), [Switch](Switch.md), [Field](Field.md).
