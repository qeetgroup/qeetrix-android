# Timeline

`QeetrixTimeline`, `QeetrixTimelineItem`, `QeetrixTimelineTone`, `QeetrixTimelineEmphasis` ·
`components.feed` · web: [Timeline](https://ui.qeet.in/docs/components/timeline) ·
sample: Data display → Timeline

A vertical history of events — an audit log, an invoice's history — in the order the app gives them.

```kotlin
QeetrixTimeline {
    for (event in events) {
        key(event.id) {
            QeetrixTimelineItem(
                title = event.title,
                time = event.time,
                description = event.detail,
                tone = if (event.failed) QeetrixTimelineTone.Destructive else QeetrixTimelineTone.Neutral,
                markerLabel = if (event.failed) "Failed" else null,
            )
        }
    }
}
```

## Usage

- **Three marker strengths:** an `icon` in a tinted circle for what matters most, a toned dot for the rest,
  and `Minor` emphasis — a small neutral dot, a muted title, a tighter gap — for housekeeping.
- **Tone is a second channel only.** The title says what happened; `markerLabel` says what a marker means
  when the words don't ("Failed").
- **The events are the app's** — their order, wording and grouping. Format times before passing them.
- For a long or growing history in a lazy list, use [list items](ListItem.md): a connector can't join
  separate lazy items.

## Accessibility

Each event is one element — title, time, description — followed by its marker label. Markers are
decorative. From 150 % text the time moves below the title.

## Theme and tokens

`component.timeline.marker-size` (24 dp rail), `gap` after an event and `gap-minor` after a minor one,
nothing after the last; a 1 dp `border.default` connector between marker centres. Dots use the feedback
colours; icon markers a `border.*` edge, a subtle tint and the tone's text colour.

## Platform differences

None in what it draws. Like iOS, the gaps don't follow a compact density.

Related: [List item](ListItem.md), [Description list](DescriptionList.md).
