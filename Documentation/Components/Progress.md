# Progress and Meter

`QeetrixProgress`, `QeetrixProgressTone`, `QeetrixMeter`, `QeetrixMeterTone` · `components.progress` ·
`@qeetrix/react`: [Progress](https://ui.qeet.in/components/progress) and [Meter](https://ui.qeet.in/components/meter)
· sample: Feedback → Progress

**Progress** is a task advancing (an upload, an import); **Meter** is a measurement within a range (storage used,
a quota). They look alike and are read differently.

```kotlin
QeetrixProgress(uploaded, max = total, label = "Uploading report.pdf", showValue = true)
QeetrixProgress(null, label = "Preparing export")
QeetrixMeter(used, max = quota, label = "Storage", showValue = true, tone = QeetrixMeterTone.Warning)
QeetrixMeter(40f, label = "People with two-step", showValue = true, target = 80f)
```

## Usage

- `value` within `min`…`max` (default 0…100); out-of-range values are clamped, NaN is rejected. Progress takes
  `null` for unknown: a 30 % segment slides along the track (under reduced motion, the full bar fades instead).
- `label` (field-label style) above the track; with `showValue`, the value at the header's end. Without a label,
  pass `contentDescription`.
- The value is its percentage of the range in the device's locale, unless `format` (a `NumberFormat`) writes the
  value itself ("6.2").
- `tone`: Progress `Brand` (running) · `Success` (done) · `Danger` (failed). Meter `Brand` · `Neutral` · `Info` for
  a plain amount, `Success` · `Warning` · `Danger` for how it stands.
- Meter `target`: a 2 dp tick on the track, and in what is read out ("40%, target 80%").

## Accessibility

Progress is a progress bar with its range and value (and the formatted text with a `format`); unknown progress is
read as in progress. A meter is read as its label and value, with its target.

## Theme and tokens

progress.css: track 4 dp, square ends — `color.border.default` in light, `color.surface.sunken` in dark; indicator
`color.bg.<tone>.bold`, a meter's warning `color.border.warning` in light, so every fill keeps 3:1 against its track
(0.4.0, QTRX-176); moving over
`motion.duration.normal` on `motion.easing.standard`; header `label.sm` (label `color.text.subtle`, value
`color.text.default` with tabular figures), `space.gap.md` apart, `space.gap.xs` above the track; target tick 2 dp
`color.text.default`, 3 dp beyond the track. The segment slides every `4 × motion.duration.slower`.

The dark track (`surface.sunken`, #080808) is darker than every dark surface — 1.03:1 on the canvas — so an empty or nearly empty bar, meter or ring can't be seen in dark mode; measured on the phone emulator, filed as QTRX-213.

## Platform differences

- **No meter role** on Android: a meter is not announced as a progress bar, only by its words.
- **Right to left.** The indeterminate segment slides from the start side, as the web's `qx-progress-slide-rtl`
  (0.4.0, QTRX-166).
- **Target words.** TalkBack reads a target as "{value}, target {target}" (`qx_meter_with_target`, the web's
  `meter.withTarget`).
- The earlier Android `size` (4 / 8 / 12 dp tracks) and fraction values are gone: the web's track is 4 dp and its
  value runs from `min` to `max`.

Related: [Spinner](Spinner.md), [Skeleton](Skeleton.md).
