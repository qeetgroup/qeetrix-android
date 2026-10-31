# Separator

`QeetrixSeparator`, `QeetrixSeparatorOrientation`, `QeetrixSeparatorTone` · `components.separator` ·
`@qeetrix/react`: [Separator](https://ui.qeet.in/components/separator) · sample: Layout → Separator

A 1 dp rule between groups of content.

```kotlin
QeetrixSeparator()
Row(Modifier.height(IntrinsicSize.Min), horizontalArrangement = Arrangement.spacedBy(QeetrixSpace.GapMd)) {
    QeetrixText("Docs")
    QeetrixSeparator(orientation = QeetrixSeparatorOrientation.Vertical)
    QeetrixText("Status")
}
```

## API

| `@qeetrix/react` | Android |
|:--|:--|
| `orientation`: `horizontal` (default) · `vertical` | `orientation: QeetrixSeparatorOrientation` — `Horizontal`, `Vertical` |
| `tone`: `subtle` (default) · `default` · `strong` | `tone: QeetrixSeparatorTone` — `Subtle`, `Default`, `Strong` |
| `decorative` | — (every separator is hidden from accessibility services) |
| `render` | — |

Removed since 2.x: `QeetrixSeparatorVariant` — its `Muted` is the default `Subtle` tone now, its `Default` is
`QeetrixSeparatorTone.Default`.

## Usage

- `Horizontal` fills the width. `Vertical` takes its parent's height, so give the row one
  (`Modifier.height(IntrinsicSize.Min)`), as the web's vertical separator stretches to its row.
- Space comes first: a rule supports grouping that spacing and headings already express. Ruled, flat surfaces are
  a Qeetrix signature (design rules §2, §12): content is separated by 1 dp lines and tone, never by shadows.

## Look (separator.css)

`border.width.default` (1 dp) in `color.border.subtle`; `Default` is `color.border.default`, `Strong`
`color.border.strong`. No margins.

## Accessibility

Invisible to accessibility services: a separator is never the only signal that content is grouped.

## Theme and tokens

`border.width.default`, `color.border.subtle` / `default` / `strong`.

## Platform differences

- **No separator role.** The web gives a separator `role="separator"` unless it is `decorative`; Android has no
  separator role, and TalkBack would only read an empty element, so every separator is decorative and there is no
  `decorative` parameter.
- **Not implemented:** the forced-colours line (Android has no forced-colours mode).

Related: [Card](Card.md), [List item](ListItem.md).
