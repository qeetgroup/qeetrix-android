# Section header

`QeetrixSectionHeader` · `components.list` · web: none (Android and iOS only) ·
sample: Data display → List item

The heading of a group of rows: a title, an optional description and an optional action. A list section is
Compose's own: a `LazyColumn` with an `item` or `stickyHeader` per group, and this header in it.

```kotlin
LazyColumn {
    for ((team, people) in membersByTeam) {
        stickyHeader(key = "team-$team") {
            QeetrixSectionHeader(team, Modifier.background(QeetrixTheme.colors.surfaceCanvas), description = "${people.size} people")
        }
        items(people, key = { it.id }) { person -> QeetrixListItem(person.name, description = person.email) }
    }
}
```

## Usage

- **Sections are a pattern, not a component.** Qeetrix adds no section container: Compose's lazy list
  already groups, scrolls, recycles and keeps headers sticky. Give each header a key of its own that can't
  clash with an item's (`"team-$team"`).
- **Sticky headers** draw over the rows that scroll under them, so give them the surface they sit on through
  `modifier`, as above.
- **The action** — typically a link-style [button](Button.md) ("Invite", "See all") — is its own control.
- Outside a lazy list, put the header above a `Column` of rows.

## Accessibility

The title is a heading, so TalkBack's heading navigation moves between groups; the description is read
with it. The action is a separate control.

## Theme and tokens

font-heading text-sm semibold title in `text.primary`, text-sm description in `text.tertiary`; the rows'
16 dp sides, 16 dp above and 8 dp below.

## Platform differences

iOS puts it in a native `Section` header, which would otherwise upper-case it. Android has no list section
style to override.

Related: [List item](ListItem.md).
