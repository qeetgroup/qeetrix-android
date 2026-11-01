# Accordion

`QeetrixAccordion`, `QeetrixAccordionItem`, `QeetrixCollapsibleTrigger`, `QeetrixCollapsiblePanel` ·
`components.accordion` · `@qeetrix/react`: [Accordion](https://ui.qeet.in/components/accordion) and
[Collapsible](https://ui.qeet.in/components/collapsible) · sample: Layout → Accordion

Stacked sections that show and hide, ruled like every surface; and a single fold without the rules.

```kotlin
var open by rememberSaveable { mutableStateOf(listOf("tax")) }
QeetrixAccordion(value = open, onValueChange = { open = it }) {
    QeetrixAccordionItem("tax", "Tax settings") { QeetrixText("GST is added at 18%.") }
    QeetrixAccordionItem("numbering", "Invoice numbering") { QeetrixText("INV-YYYY-NNNN") }
}

var advanced by rememberSaveable { mutableStateOf(false) }
QeetrixCollapsibleTrigger("Advanced options", open = advanced, onOpenChange = { advanced = it })
QeetrixCollapsiblePanel(advanced) { WebhookSecret() }
```

## Usage

- **The app owns what is open.** `value` is the open sections' values — a list, even when only one can be open —
  and `onValueChange` gets the new list. Without `multiple`, opening one closes any other. `enabled = false` on the
  accordion disables every section; on an item, that section.
- Sections are found by their `value`, so inserting, removing or reordering them keeps each one's state. In a lazy
  list put the `LazyColumn` inside the accordion (`Modifier.weight(1f)` or a bounded height) and an item in each
  lazy item.
- Titles: a question or a topic in a few words, in sentence case. Keep sections short.

## Look

- A 1 dp `color.border.subtle` rule above each item and below the last.
- The trigger: `size.control.lg` tall, full width, no padding; the title in `title.sm` (`color.text.default`), then a
  16 dp sharp `chevron-down` in `color.icon.subtle` that turns 180° when open, in `motion.duration.normal`.
- The panel animates its height over `motion.duration.normal` with `motion.easing.standard`; its content has 16 dp
  below it and is `body.md` in `color.text.default`.
- Disabled: `color.text.disabled` and a `color.icon.disabled` chevron.
- Collapsible: `QeetrixCollapsibleTrigger` is a [button](Button.md) (ghost by default) with a trailing chevron in
  its icon zone that turns as the panel opens; `QeetrixCollapsiblePanel` has the same height animation, no rules,
  no padding.

## Accessibility

- Each trigger is one button and a heading, read with its title and "Expanded" or "Collapsed" (`qx_expanded`,
  `qx_collapsed`), with expand and collapse actions; the collapsible trigger the same, without the heading.
- Keyboard focus draws the 2 dp Ember frame inside the trigger; each trigger is its own focus stop; Enter and Space
  toggle. Touch reaches a trigger within 48 dp.

## Theme and tokens

`color.border.subtle`, `size.control.lg`, `typography.title.sm` / `body.md`, `color.text.default` / `disabled`,
`color.icon.subtle` / `disabled`, `space.gap.md`, `space.inset.lg`, `motion.duration.normal`, `motion.easing.standard`.

## Platform differences

- **No heading levels** (`headingLevel`): Android has one heading role.
- **A closed panel leaves the composition**; the web keeps it in the page, hidden until found, so find in page can
  open it. Keep state that must survive a close in your own state holder.
- **The trigger** is `size.control.lg` tall, and Compose extends its touch area to 48 dp.
- **Collapsible** is two composables sharing the app's `open` (the web's `Collapsible` root holds the state);
  the trigger is a button with text — an icon-only fold is an `QeetrixIconButton` and the panel.
- **Removed since 2.x:** per-item `expanded` / `onExpandedChange` (now the accordion's `value`), an item's
  `description` and `icon`, the first item's missing top rule.

Related: [Card](Card.md), [Button](Button.md).
