# Timeline

`QeetrixTimeline`, `QeetrixTimelineGroup`, `QeetrixTimelineItem`, `QeetrixTimelineVariant`, `QeetrixTimelineTone` ·
`components.feed` · `@qeetrix/react`: [Timeline](https://ui.qeet.in/components/timeline) (experimental) ·
sample: Data display → Timeline

What happened, newest first: a user's recent activity, a payment's history, a delivery's attempts.

```kotlin
QeetrixTimeline {
    QeetrixTimelineGroup("Today") {
        QeetrixTimelineItem("Signed in with a passkey", description = "Bengaluru · Chrome on macOS", time = "2 min ago", icon = QeetrixIconName.Lock)
        QeetrixTimelineItem("Sign-in refused", time = "1 h ago", icon = QeetrixIconName.CircleX, tone = QeetrixTimelineTone.Danger)
    }
}
```

## Look

| `variant` | Look |
|:--|:--|
| `Default` | A 28 dp square marker per event (`color.bg.neutral.subtle`, a 1 dp `color.border.subtle` edge, 4 dp corners) holding its 16 dp sharp icon; a 1 dp `color.border.subtle` line from 4 dp under the marker to 4 dp above the next event, broken between groups; events `space.gap.lg` apart, `space.gap.md` between the marker and the text. |
| `Compact` | A 6 dp dot (`color.border.strong`, or the tone's icon colour) centred on the title's line, no line, events `space.gap.sm` apart. |

- Title `body.md` at weight 500 (compact: 400) in `color.text.default`; description and time `body.sm` in
  `color.text.subtle`, the time at the end of the event, on one line. Each first line is centred on the marker.
- `tone` takes Badge's names — `Neutral` (default), `Brand`, `Info`, `Success`, `Warning`, `Danger` — and colours
  the icon or the dot (`color.icon.<tone>`); `Danger` colours the title too.
- `QeetrixTimelineGroup(label)`: the day's group label in `label.caps`, `color.text.subtle`, `space.gap.sm` above
  its events; groups `space.gap.md` apart.

## Accessibility

- Each event is one element: title, description, time. The marker is decorative, so the title must say what
  happened ("Sign-in refused"), never the colour.
- From 150 % text the time moves below the description, and the marker moves down to the title's line.

## Theme and tokens

`color.bg.neutral.subtle`, `color.border.subtle` / `strong`, `color.icon.*`, `color.text.default` / `subtle` /
`danger`, `typography.body.md` / `body.sm` / `label.caps`, `space.gap.xs` / `sm` / `md` / `lg`, `radius.control`,
`component.timeline.marker-size` (28px since `@qeetrix/tokens` 0.0.13, QTRX-185, as timeline.css draws it).

## Platform differences

- **No `dateTime`.** Android has no `<time>` element; format the time for reading and, where it helps, give the
  full date in `description`.
- The full audit log stays a list; in a lazy list use [list items](ListItem.md): a line can't join separate lazy
  items.
- **Removed since 2.x:** `Destructive` (now `Danger`), `emphasis` (`Minor`; use the compact look), `markerLabel`
  (the title carries the meaning), the toned 10 dp dots and round icon markers.

Related: [List item](ListItem.md), [Badge](Badge.md).
