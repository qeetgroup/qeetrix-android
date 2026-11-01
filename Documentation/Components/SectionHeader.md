# Section header

`QeetrixSectionHeader` · `components.list` · `@qeetrix/react`: none — drawn as the Sidebar's group label
(`.qx-sidebar-group-label`) · sample: Data display → List item

The label of a group of rows: a group label in `typography.label.caps`, Qeetrix's one uppercase role (design
rules §5), with an optional description and action. A list section is Compose's own: a `LazyColumn` with an
`item` or `stickyHeader` per group, and this header in it.

```kotlin
LazyColumn {
    for ((index, group) in groups.withIndex()) {
        stickyHeader(key = "header-${group.id}") {
            QeetrixSectionHeader(group.label, Modifier.background(QeetrixTheme.colors.surfaceCanvas), ruled = index > 0)
        }
        items(group.members, key = { it.id }) { member -> QeetrixListItem(member.name) }
    }
}
```

## Look

- The label in small, tracked capitals (`label.caps`: 11 / 16 sp, weight 600, +6%) in `color.text.subtlest`.
- `space.gap.sm` and a 1 dp `color.border.subtle` rule above, `space.inset.md` from the rule to the label, 6 dp
  from the label to the rows, the rows' 16 dp inset. `ruled = false` for the first group of a list, as the
  Sidebar's first group has no rule.
- An optional `description` in `body.sm`, `color.text.subtle` (not upper-cased), and an `action` at the end —
  typically a ghost button.

## Accessibility

- Write the label in sentence case: the capitals are styling. TalkBack reads the words as written, as a heading
  with its description, so heading navigation moves between groups. The action is its own control.
- Indic text has no capitals and renders as written, without the tracking, so conjuncts stay intact (the web
  drops it for Hindi, Marathi, Bengali, Tamil and the other Indic languages; Android looks at the label's script).

## Theme and tokens

`typography.label.caps` / `body.sm`, `color.text.subtlest` / `subtle`, `color.border.subtle`,
`space.gap.sm` / `xs` / `md`, `space.inset.md` / `lg`.

## Platform differences

The web has no section header for rows. Removed since 2.x: the `title.sm`-like heading style (now a group label).

Related: [List item](ListItem.md), [Timeline](Timeline.md) (its group labels).
