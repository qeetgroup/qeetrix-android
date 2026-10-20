# Accordion

`QeetrixAccordion`, `QeetrixAccordionItem` · `components.accordion` ·
web: [Accordion](https://ui.qeet.in/docs/components/accordion) · sample: Layout → Accordion

Sections whose headers show and hide their content.

```kotlin
var open by rememberSaveable { mutableStateOf<Section?>(Section.Billing) }
QeetrixAccordion {
    for (section in Section.entries) {
        QeetrixAccordionItem(
            title = section.title,
            expanded = open == section,
            onExpandedChange = { open = if (it) section else null },
        ) { QeetrixText(section.summary, variant = QeetrixTextVariant.Small) }
    }
}
```

## Usage

- **The app owns expansion.** One optional value opens one item at a time, as above. A saved set of your
  items' ids lets several open at once and keeps each one's state through insertions, removals and
  reordering.
- **Headers** take a description and an icon. Text in the content is 14 sp primary on the web: use
  `QeetrixText` with the `Small` variant.
- **Content leaves the composition when closed.** Keep what must survive a close in your state holder.
- Use it in a scrolling column, a card or a sheet. In a lazy list, make each item a `QeetrixAccordionItem`
  with [separators](Separator.md) between them.
- The web's single-section `Collapsible` is one `QeetrixAccordionItem` on its own.

## States

Collapsed, expanded (the chevron turns and takes `text.primary`), pressed or hovered (the chevron
brightens), keyboard-focused (the focus ring), disabled (faded).

## Accessibility

The header is one button and a heading, read with its title, description and "Expanded" or "Collapsed"
(`qx_expanded`, `qx_collapsed`); TalkBack also offers expand and collapse actions. Headers are at least
48 dp. The content opens over 200 ms, or at once with animations off.

## Theme and tokens

`component.accordion.divider` between items, `trigger-padding-block` above and below the header,
`duration` for the motion (ease-standard), `focus-bleed` for focus rings at the content's edges; text-sm
medium headers; a 16 dp chevron.

## Platform differences

iOS styles native `DisclosureGroup`s; Compose has no disclosure component, so the item is Qeetrix's. Like
iOS, the header padding doesn't follow a compact density.

Related: [Card](Card.md).
