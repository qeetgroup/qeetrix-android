# Badge

`QeetrixBadge`, `QeetrixBadgeTone`, `QeetrixBadgeVariant`, `QeetrixBadgeSize` · `components.badge` ·
`@qeetrix/react`: [Badge](https://ui.qeet.in/components/badge) · sample: Data display → Badge

A short, static status or count, in one or two words: "Paid", "Overdue", "3".

```kotlin
QeetrixBadge("Paid", tone = QeetrixBadgeTone.Success, icon = QeetrixIconName.CircleCheck)
QeetrixBadge("Overdue", tone = QeetrixBadgeTone.Danger, variant = QeetrixBadgeVariant.Solid)
QeetrixBadge("Draft", size = QeetrixBadgeSize.Sm)
```

## API

| `@qeetrix/react` | Android |
|:--|:--|
| `children` | `text: String` |
| `tone`: `neutral` (default) · `brand` · `info` · `success` · `warning` · `danger` | `tone: QeetrixBadgeTone` — `Neutral`, `Brand`, `Info`, `Success`, `Warning`, `Danger` |
| `variant`: `subtle` (default) · `solid` | `variant: QeetrixBadgeVariant` — `Subtle`, `Solid` |
| `size`: `md` 20 px (default) · `sm` 16 px | `size: QeetrixBadgeSize` — `Md`, `Sm` |
| `icon` (leading, 12 px, sharp, decorative) | `icon: QeetrixIconName?` |
| `title` (the full text where it may be cut) | — (TalkBack reads the full text) |

Removed since 2.x: the variants `Default`, `Secondary`, `Outline`, `Brand`, `Info`, `Success`, `Warning`,
`Destructive` and `Muted` — now a `tone` and a `variant`.

## Look (design rules §14, badge.css)

| `tone` | `Subtle`: fill / text | `Solid`: fill / text |
|:--|:--|:--|
| `Neutral` | `color.bg.neutral.subtle-pressed` / `color.text.default` | `color.bg.neutral.bold` / `color.text.inverse` |
| `Brand` | `color.bg.brand.subtle` / `color.text.brand` | `color.bg.brand.bold` / `color.text.on-brand` |
| `Info` | `color.bg.info.subtle` / `color.text.info` | `color.bg.info.bold` / `color.text.on-bold` |
| `Success` | `color.bg.success.subtle` / `color.text.success` | `color.bg.success.bold` / `color.text.on-bold` |
| `Warning` | `color.bg.warning.subtle` / `color.text.warning` | `color.bg.warning.bold` / `color.text.on-warning` |
| `Danger` | `color.bg.danger.subtle` / `color.text.danger` | `color.bg.danger.bold` / `color.text.on-bold` |

- A pill (`radius.chip`) in `label.sm` (weight 500), on one line, ending in an ellipsis when space runs out.
- `Md`: 2 dp above and below, 8 dp at the sides — 20 dp tall at the default text size. `Sm`: 6 dp at the sides
  only — 16 dp tall — for tight rows and beside small text.
- The icon: 12 dp (`size.icon.2xs`), sharp, in the text's colour, `space.gap.xs` (2 dp at `Sm`) before the label.
- `Subtle` for most statuses; `Solid` for the one status in a view that must stand out.

## Accessibility

- The word carries the meaning and the tone only reinforces it, so a status never rests on colour alone. The icon
  is decorative.
- Not interactive: a selectable or removable tag is a [Chip](Chip.md).
- A count needs context for TalkBack: give the row around it a `contentDescription`, such as "3 pending".
- The text follows the font scale and the badge grows with it.

## Theme and tokens

The colours above, `radius.chip`, `typography.label.sm`, `space.gap.xs`, `size.icon.2xs`. The paddings are
badge.css's, restated beside each value in the source.

## Platform differences

- **Cut text.** The web adds a `title` for the full text; on Android TalkBack reads the full text anyway.
- **Large text.** At very large font scales a long badge can fill its row; keep it to one or two words.
- **Not implemented:** the forced-colours border (Android has no forced-colours mode).

Related: [Chip](Chip.md), [Avatar](Avatar.md), [Description list](DescriptionList.md).
