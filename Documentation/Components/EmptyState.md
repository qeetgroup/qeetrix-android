# Empty state

`QeetrixEmptyState`, `QeetrixEmptyStateVariant`, `QeetrixEmptyStateSize` · `components.emptystate` ·
web: [Empty State](https://ui.qeet.in/docs/components/empty-state) · sample: Feedback → Empty state

What a surface shows when it has nothing to show: an icon on a tinted tile, a title, a description and
actions.

```kotlin
QeetrixEmptyState(
    title = "No invoices yet",
    description = "Invoices appear here once you bill a customer.",
    variant = QeetrixEmptyStateVariant.FirstUse,
    icon = QeetrixIconName.FileText,
) {
    QeetrixButton("Create invoice", onClick = ::createInvoice)
}

QeetrixEmptyState(
    title = "No results",
    description = "Nothing matches “${query.text}”.",
    variant = QeetrixEmptyStateVariant.NoResults,
    size = QeetrixEmptyStateSize.Sm,
) {
    QeetrixButton("Clear search", onClick = { query.clearText() }, variant = QeetrixButtonVariant.Outline)
}
```

## Usage

- **The variant says why**, and the app decides which applies: `Default` (an empty collection), `FirstUse`
  (an invitation to create the first thing — the only brand tile; pair it with a primary button),
  `NoResults` (a search or filter excluded everything — offer a way to clear), `NoPermission` (say who can
  grant access) and `Error` (loading failed — say what to do).
- `icon` replaces the variant's own: search-x, lock and triangle-alert for no results, no permission and an
  error; none for the others.
- **Sizes:** `Sm` for a section or a list's body, `Default` for a collection's area, `Lg` for a screen.
- **Words, not codes.** Never put an exception's message, a status code or a stack trace in the description.
- For the loading, failure and empty branches of one surface together, use the [data state](DataState.md).

## Accessibility

The title is a heading, the description follows it, and actions are their own controls. The tile is
decorative. Actions wrap onto more lines when they don't fit; text follows the font scale.

## Theme and tokens

Tiles: `surface.sunken` with `text.tertiary` (default, no results, no permission), `surface.brand-subtle`
with `text.brand` (first use), `feedback.error-subtle` with `text.danger` (error), all edged with
`border.subtle`, `corner.surface`. Title in the heading font, semibold, 14/16/18 sp by size; description
text-sm `text.tertiary`, at most 384 dp wide.

## Platform differences

None in what it draws. iOS also draws its own rather than the system's `ContentUnavailableView`.

Related: [Data state](DataState.md), [Search field](SearchField.md).
