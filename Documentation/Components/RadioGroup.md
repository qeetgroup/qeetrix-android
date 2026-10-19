# Radio group

`QeetrixRadioGroup` · `components.radiogroup` · web: [Radio Group](https://ui.qeet.in/docs/components/radio-group) ·
sample: Forms & inputs → Radio group

One choice from a short, visible set of options.

```kotlin
var plan by rememberSaveable { mutableStateOf(Plan.Team) }
QeetrixRadioGroup(plan, { plan = it }, Plan.entries.map { QeetrixOption(it, it.title, it.detail) }, label = "Plan")
```

## Usage

- **Typed choice.** Options are `QeetrixOption<T>` (value, label, optional description, enabled); the
  selection is a `T`, so only one option can ever be selected, and only one of yours. "Nothing yet" is a
  value no option has — `null` with a nullable type. Values must be distinct.
- For a long list, or little room, use a [select](Select.md).

## States

Selected (Ember disc, light dot), unselected, disabled options, the group's error (every disc's boundary,
and the message after the options), a disabled group.

## Accessibility

A selectable group under its label (a heading). Each option is one radio button reading its label,
description and "selected" / "not selected". An error message is carried by every option. Rows are at
least 48 dp tall and sit flush.

## Theme and tokens

As the [checkbox](Checkbox.md), round; the dot is `size-1.5`.

## Platform differences

The web group is one tab stop with arrow keys between options. On Android, as on iOS, each option is its
own focus stop for keyboards and D-pads.

Related: [Select](Select.md), [Checkbox](Checkbox.md), [Field](Field.md).
