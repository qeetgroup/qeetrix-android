# Relative time

`QeetrixRelativeTime` · `components.relativetime` · `@qeetrix/react`: RelativeTime (experimental) · QTRX-121

When something happened, as people say it: "2 minutes ago", "yesterday", "in 3 days", then the date.

```kotlin
QeetrixRelativeTime(message.sentAt, tone = QeetrixTextTone.Subtle, size = QeetrixTextSize.Sm)
```

## API

| `@qeetrix/react` | Android |
|:--|:--|
| `date` (Date, timestamp or ISO string) | `moment: Instant` |
| `threshold` (30 days) | `threshold: Duration` (30 days): beyond it, the date |
| `now` (fixes the clock, stops updates) | `now: Instant?` |
| `live` (`true`) | `live` |
| `locale` | the device's (`LocalConfiguration`) |
| — | `size`, `tone` (as [Text](Text.md)), `zone` (the device's) |

## Usage and look

The words are `QeetrixFormat.relativeTime`'s — the web's for the same locale ([Formatting](../Foundations/FORMAT.md))
— past the threshold the date (`QeetrixFormat.date`, medium: "25 Oct 2026"), in the body role with tabular figures
(relative-time.css). It refreshes while its screen is started: every 15 s within a minute of the moment, every minute
within an hour, then every 30 minutes, as the web's does, and stops when the screen stops.

## Accessibility

TalkBack reads the words, then the exact date and time ("30 October 2026 at 5:28 pm"), the web's `title`.

## Platform differences

- The text may wrap at large text sizes; the web keeps it on one line (`white-space: nowrap`).
- The zone is the device's; the web fixes `Asia/Kolkata` so its server and client agree.

Related: [Formatting](../Foundations/FORMAT.md), [Text](Text.md).
