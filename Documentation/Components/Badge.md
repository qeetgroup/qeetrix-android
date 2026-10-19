# Badge

`QeetrixBadge` · `components.badge` · web: [Badge](https://ui.qeet.in/docs/components/badge) ·
sample: Data display → Badge

A small label for a count, a status or a category.

```kotlin
QeetrixBadge("Active", variant = QeetrixBadgeVariant.Success, icon = QeetrixIconName.CircleCheck)
QeetrixBadge("12", variant = QeetrixBadgeVariant.Secondary)
```

## Variants

| Variant | Use |
|:--|:--|
| `Default` | The one solid Qeet fill, for counts and "New". The loudest a badge can be; use sparingly. |
| `Secondary` | Graphite, for metadata that should not compete. |
| `Outline` | An outline only. |
| `Brand`, `Info`, `Success`, `Warning`, `Destructive` | Quiet tints for the brand and status tones. |
| `Muted` | The quietest neutral. |

One size, as on the web. An optional leading `icon`.

## Accessibility

- The text carries the meaning ("Failed", not a red dot), so a status never rests on colour alone. The
  icon is decorative.
- Badges are not interactive. Text follows the font scale; the badge grows and stays on one line.

## Theme and tokens

`component.badge.*` (`QeetrixBadgeComponentTokens`): background, foreground and border per variant.

## Platform differences

None in behaviour. At very large font scales a long badge can be wider than the screen; keep badge
text short. (iOS truncates in that case.)

Related: [Avatar](Avatar.md), [Button](Button.md) (custom content with a count).
