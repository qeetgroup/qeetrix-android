# Description list

`QeetrixDescriptionList`, `QeetrixDescriptionItem`, `QeetrixDescriptionListLayout` ·
`components.descriptionlist` · web: [Description List](https://ui.qeet.in/docs/components/description-list) ·
sample: Data display → Description list

Labelled details — the properties of one thing: a plan, a device, a file. A `QeetrixDescriptionItem` on its
own is the key-value row.

```kotlin
QeetrixDescriptionList(divided = true) {
    QeetrixDescriptionItem("Plan", "Team")
    QeetrixDescriptionItem("Region", "Mumbai (ap-south-1)")
    QeetrixDescriptionItem("Status") { QeetrixBadge("Active", variant = QeetrixBadgeVariant.Success) }
}
```

## Usage

- **Three layouts**, the web's: `Horizontal` (the default) puts a 192 dp term column beside the values from
  the web's `sm` breakpoint, 640 dp of window width, and stacks each term above its value below it;
  `Vertical` always stacks; `Grid` fills the width with term-over-value cells at least 192 dp wide.
- `divided` rules between pairs.
- **Values** are text (the `String` overload) or any content — a [badge](Badge.md), an [avatar](Avatar.md),
  a link. Format dates, amounts and identifiers before they get here.
- **In a lazy list**, use one `QeetrixDescriptionItem` per item, with your own padding.
- **Never put a secret in a value.** The list displays what it is given; it never logs, copies or announces
  anything.

## Accessibility

Each pair is one element — "Plan, Team" — and a control in a value stays its own element. Long values wrap.

## Theme and tokens

text-sm terms in `text.tertiary` and values in `text.primary`; pairs `density.cell-padding-y` apart (1.5×
in a grid), so a compact density tightens them; `border.subtle` rules.

## Platform differences

The breakpoint is the window's width, as the web's is the viewport's; iOS uses the horizontal size class.
The web's bare `DescriptionTerm`/`DescriptionDetails` pairs are always items here.

Related: [List item](ListItem.md).
