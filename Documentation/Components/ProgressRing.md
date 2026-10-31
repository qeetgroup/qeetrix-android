# Progress ring

`QeetrixProgressRing`, `QeetrixProgressRingSize` · `components.progress` · `@qeetrix/react`: none (Progress's parts,
drawn round) · QTRX-102

How far something has gone, as a ring beside text: the seconds left on a one-time code, a small upload.

```kotlin
QeetrixProgressRing(secondsLeft / 30f, contentDescription = "Time left", stateDescription = "$secondsLeft seconds left")
```

## API

| Parameter | Values |
|:--|:--|
| `value` | the share done, 0 to 1 (clamped; NaN is rejected) |
| `size: QeetrixProgressRingSize` | `Sm` 16 dp, `Md` 20 dp (default), `Lg` 24 dp — `size.icon.*`; a 2 dp ring, 3 dp at `Lg` |
| `tone: QeetrixMeterTone` | Meter's tones; `Brand` by default, `Warning` for a countdown's last seconds |
| `contentDescription`, `stateDescription` | what it measures, and its value in words |

## Look (progress.css, drawn round)

The track is `color.border.default` in light and `color.surface.sunken` in dark; the indicator the tone's fill
(`color.border.warning` for a light warning), so every tone keeps 3:1 against the track; square ends. It fills
clockwise from 12 o'clock, a clock's way, in right to left too. A new value is reached over `motion.duration.normal`,
and at once under reduced motion: a countdown that sets the value every frame moves smoothly, and steps once a second
where it sets it once a second.

## Accessibility

Read as a progress bar named `contentDescription`, its value `stateDescription` ("12 seconds left") or the
percentage. Without a `contentDescription` it is decorative.

## Platform differences

`@qeetrix/react` has no ring; this is Android's (and qeetrix-ios's), made of Progress's tokens.

Related: [Progress](Progress.md), [Spinner](Spinner.md).
