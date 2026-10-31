# Empty state

`QeetrixEmptyState`, `QeetrixEmptyStateSize`, `QeetrixEmptyStateTone` · `components.emptystate` ·
`@qeetrix/react`: [EmptyState](https://ui.qeet.in/components/empty-state) (experimental) · sample: Feedback →
Empty state

The message where content would be: nothing yet, no results, or an error.

```kotlin
QeetrixEmptyState(
    title = "No invoices yet",
    icon = QeetrixIconName.FileText,
    description = "Invoices appear here once you bill a customer.",
    footer = { QeetrixText("Or import them from a CSV file", size = QeetrixTextSize.Sm) },
) {
    QeetrixButton("Create invoice", onClick = ::createInvoice, icon = QeetrixIconName.Plus)
}
```

## Usage

- `title` (required) says what is missing or what went wrong; `description` is one or two sentences — why, and
  what to do. Never a raw error, a status code or a stack trace.
- `icon`: a 24 dp sharp icon in `color.icon.subtle`; `tone = Danger` colours it `color.icon.danger`, for an error.
- `actions`: buttons, one primary at most, in a centred row that wraps. `footer`: under the actions, in
  `color.text.subtle` — a link to the docs or a second way in.
- `size`: `Page` (default; `title.md`, 48 dp above and below) for a whole view, `Section` (`title.sm`, 24 dp) inside
  a card or table.
- `level`: makes the title a heading, when this is the screen's main message.
- For loading, error and empty together, use [Data state](DataState.md).

## Accessibility

The title is read first, then the description; with a `level` it is a heading. The icon is decorative; actions
are their own controls. Text follows the font scale and wraps.

## Theme and tokens

empty-state.css: centred, at most 480 dp wide, `space.gap.sm` between parts, `space.inset.lg` at the sides; the
icon `space.gap.xs` above the title; title `color.text.default`; description `body.md` `color.text.subtle`; actions
`space.gap.sm` apart and below the description; footer `body.sm` `color.text.subtle`.

## Platform differences

- Android has no heading levels: any `level` makes the title a heading; the look comes from `size`.
- The earlier Android variants (first use, no results, no permission, error) and their tinted icon tiles are gone:
  the web's empty state has an icon and a tone, and the words say the reason.

Related: [Data state](DataState.md), [Skeleton](Skeleton.md).
