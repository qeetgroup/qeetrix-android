# Select

`QeetrixSelect` · `components.select` · web: [Select](https://ui.qeet.in/docs/components/select) ·
sample: Forms & inputs → Select

Choose one of a list in a popup — a trigger drawn as a field, and a list that opens below it.

```kotlin
var region by rememberSaveable { mutableStateOf<String?>(null) }
QeetrixSelect(region, { region = it }, regions.map { QeetrixOption(it.id, it.name) },
    label = "Data region", placeholder = "Choose a region")
```

## Usage

- Typed like the [radio group](RadioGroup.md): `QeetrixOption<T>` options, a `T` selection, the
  `placeholder` while the selection is a value no option has.
- The list is as wide as the trigger (at least 144 dp), opens below — above when it fits there and not
  below — and scrolls when long. It closes on a choice, a tap outside, Back or Escape.
- Sizes: `Default` (control height) and `Sm` (one step under, smaller corner).
- Not here: search, remote loading, multiple choice.

## States

Placeholder, selected, the three message tones, read-only (dashed, does not open), disabled, open (the
boundary holds its hover colour and the chevron turns).

## Accessibility

The trigger is one drop-down list control named by the label, its text the selected option. Opened from
the keyboard (Enter on a focused trigger) the list focuses the selected option; options read "selected" /
"not selected" and are at least 48 dp tall. The list announces its title as a pane.

## Theme and tokens

Trigger: the field recipe (`component.input.*`). List: `surface.overlay`, `border.default`,
`elevation.overlay`, `corner.overlay`; options `corner.chip` (radius-md), `surface.brand-subtle` for the
selection, `surface.interactive-hover` when highlighted, a `text.brand` check.

## Platform differences

- **The list.** Android draws Qeetrix's own list in a Compose popup, as the web's `Select` does; iOS uses
  the system menu.
- **Position.** The web aligns the selected item over the trigger for a mouse; on touch (and on Android)
  the list opens below the trigger.
- Option rows take the web's coarse-pointer rule at Android's 48 dp.

Related: [Radio group](RadioGroup.md), [Field](Field.md).
