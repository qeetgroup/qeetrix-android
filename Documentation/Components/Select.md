# Select

`QeetrixSelect`, `QeetrixMultiSelect` · `components.select` · `@qeetrix/react`: Select in a Field
([Select](https://ui.qeet.in/components/select)) · sample: Forms → Select

Pick one option — or, with `QeetrixMultiSelect`, several — from a short list (up to about 15). The trigger looks like
every field; the list opens below it.

```kotlin
var cycle by rememberSaveable { mutableStateOf<String?>(null) }
QeetrixSelect(cycle, { cycle = it }, listOf(QeetrixOption("monthly", "Monthly"), QeetrixOption("yearly", "Yearly")),
    label = "Billing cycle", placeholder = "e.g. Monthly")
```

## API

| `@qeetrix/react` | Android |
|:--|:--|
| `items` (`{ value, label, disabled, meta }`) | `items: List<QeetrixOption<T>>` (`enabled`, `meta`) |
| groups `{ label, items }` | the overload taking `groups: List<QeetrixOptionGroup<T>>` |
| `value` / `onValueChange` | `value: T` / `onValueChange: (T) -> Unit` |
| `size`: `lg` (default) · `md` · `sm` | `size: QeetrixSelectSize` — `Lg`, `Md`, `Sm` |
| `placeholder`, `disabled`, `readOnly`, `required` | `placeholder`, `enabled`, `readOnly`, `required` |
| a Field around it | `label` (required), `description`, `error`, `invalid`, `optional`, `hideLabel`, `help` |
| `multiple` (value an array) | `QeetrixMultiSelect(values: Set<T>, onValuesChange: (Set<T>) -> Unit, …)` — the same parameters otherwise |
| `open` / `onOpenChange` | `open: Boolean?` / `onOpenChange: ((Boolean) -> Unit)?` — `null` leaves the state to the select |
| typing to jump (06 §4) | the same, from a hardware keyboard |

Old `selected` / `onSelect` / `options` became `value` / `onValueChange` / `items`; sizes `Default` / `Sm` became
`Lg` / `Md` / `Sm`.

## Look (select.css)

- **Trigger**: the [text field](TextField.md)'s box and states; the chosen label in `body.md` (`body.sm` at `Sm`),
  or the placeholder in `color.text.subtlest`; a trailing 16 dp sharp `chevron-down` in `color.icon.default`. The
  Ember frame shows on keyboard focus **and while the list is open**. `Md` is `size.control.md`, `Sm`
  `size.control.sm` with `space.inset.sm` padding.
- **List**: `color.surface.overlay`, a 1 dp `color.border.subtle` edge, `radius.overlay`, `shadow.overlay`, 4 dp
  block padding; at least the trigger's width, at most 320 dp tall, scrolling; 4 dp below the trigger (above when
  there is more room). It fades and slides 4 dp in (`motion.duration.normal`), out one step faster.
- **Option**: `space.inset.md` padding, `body.md`; highlighted (pressed, hovered, keyboard-focused)
  `color.bg.neutral.subtle-hover` with a 2 dp Ember bar on the start edge; chosen at `label.md`'s weight with a
  16 dp `check` in `color.icon.brand`; a `meta` in `body.sm` / `color.text.subtle`, tabular. A long label wraps and
  the row grows, the meta and the check staying on its first line (select.css 0.4.0, QTRX-177). Group labels
  `label.sm` / `color.text.subtle`.
- **Several choices** (`QeetrixMultiSelect`): the trigger lists the chosen labels in the list's order, comma-separated,
  cut with an ellipsis (06 Increment 5); each chosen option has the check; the list stays open while options are
  toggled.

## Typing to jump

On a hardware keyboard, characters typed within a second build a prefix (06 §4). In the open list, the focus moves to
the next option whose label starts with it; the same letter again moves on to the next option starting with it. On the
closed trigger of a single select, the matching option is chosen, as in a native list. Disabled options are passed
over.

## Accessibility

The trigger is one drop-down list named by the label, with the chosen label as its text; opened from the keyboard
the list focuses the chosen option. Options are selectable and read with their meta; the list's pane title is the
label; group labels are headings. Escape, Back and a tap outside close it. In a `QeetrixMultiSelect` each option is a
check box, read as checked or not, and the trigger's text is the chosen labels.

## Platform differences

- **Option height.** At least 48 dp — Android's touch target — where the web's row is `size.control.md`.
- **Unchecked options show no box** in a multiple select, as on the web: the check marks the chosen ones, and TalkBack
  reads every option as a check box.

Tried on the phone emulator (`qeetrix-api36`, Android 16) on 31 October 2026 after QTRX-190: the multiple select's list
stays open while options are toggled and the field reads "Bengaluru, Mumbai, Frankfurt"; with key events, typing S in
the open list moved the focus to Scale, S again to Starter, and Enter chose it, the app closing the list through
`onOpenChange`.

Related: [Radio group](RadioGroup.md) (a few visible options), [Field](Field.md).
