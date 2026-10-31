# Description list

`QeetrixDescriptionList`, `QeetrixDescriptionItem`, `QeetrixDescriptionListLayout` · `components.descriptionlist` ·
`@qeetrix/react`: [DescriptionList](https://ui.qeet.in/components/description-list) (experimental) ·
sample: Data display → Description list

Facts about one record, as term and value pairs: a user's email and ID, a payment's amount and status.

```kotlin
QeetrixDescriptionList {
    QeetrixDescriptionItem("Email", "priya@acme.in")
    QeetrixDescriptionItem("Status") { QeetrixBadge("Active", tone = QeetrixBadgeTone.Success) }
    QeetrixDescriptionItem("Phone", null) // "—", read as "Not set"
    QeetrixDescriptionItem(
        "Display name",
        "Acme",
        description = "Shown in the top bar and in invitations.",
        action = { QeetrixButton("Change", onClick = ::edit, variant = QeetrixButtonVariant.Ghost, size = QeetrixButtonSize.Sm) },
    )
}
```

## Layouts

| `layout` | Look |
|:--|:--|
| `Rows` (default) | Each term beside its value: the term column `minmax(8rem, 30%)`, `space.gap.md` before the value, aligned on the value's first baseline; rows at least `size.row` tall, `space.gap.sm` above and below, a 1 dp `color.border.subtle` rule between. Narrower than 24 rem (384 dp at the default text size) the term moves above its value — so on a phone. With an `action`, the row centres and the action ends it (below the value when narrow). |
| `Columns` | Term above value, `space.gap.xs` apart, in up to `columns` (1–4, default 2) columns of at least 8 rem, `space.gap.xl` apart, rows `space.gap.lg` apart. |

Terms are `label.sm` in `color.text.subtle`; values `body.md` in `color.text.default`, wrapping; a value's
`description` is `body.sm` in `color.text.subtle` under it. A rows list placed directly in a
[card](Card.md)'s content runs edge to edge, its rows keeping the card's padding.

## Accessibility

- Each pair is read as one element ("Email, priya@acme.in"); a control in a value or an `action` stays its own
  focusable element.
- An empty value (null or "") shows "—" in `color.text.subtlest`, read as `emptyLabel` ("Not set").
- A text value and a term take their own text's direction, so an email or an amount stays left to right in a
  right-to-left layout, aligned to the layout's start.

## Theme and tokens

`typography.label.sm` / `body.md` / `body.sm`, `color.text.subtle` / `default` / `subtlest`,
`color.border.subtle`, `size.row`, `space.gap.xs` / `sm` / `md` / `lg` / `xl`.

## Platform differences

- **rem** is 16 sp, so the 8 rem term column and the 24 rem breakpoint grow with the user's text size, as the
  web's do with the browser's font size. The container query is the list's own width.
- **Semantics.** Android has no `dl`/`dt`/`dd`; each pair is one merged element.
- **Strings.** `emptyLabel` defaults to `qx_not_set` ("Not set", the web's `descriptionList.empty`).
- **Removed since 2.x:** `Horizontal`, `Vertical` and `Grid` (now `Rows` and `Columns`), `divided` (rows are
  always ruled).

Related: [Card](Card.md), [List item](ListItem.md).
