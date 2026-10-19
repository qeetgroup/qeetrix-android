# Progress

`QeetrixProgress` · `components.progress` · web: [Progress](https://ui.qeet.in/docs/components/progress) ·
sample: Feedback → Progress

Task progress: a filled track for a known fraction, or a moving segment while the total is unknown.

```kotlin
QeetrixProgress(uploaded / total, label = "Uploading report.pdf")
QeetrixProgress(null, label = "Preparing export")                       // indeterminate
QeetrixProgress(0.75f, size = QeetrixProgressSize.Sm, showValue = false, contentDescription = "Storage used")
```

## Usage

- `progress` is a fraction from 0 to 1; values outside are clamped and NaN is rejected. `null` is
  indeterminate.
- `label` shows above the track; the value (a locale-formatted percentage) shows opposite it unless
  `showValue = false`.
- Sizes are track thicknesses: `Sm` 4, `Md` 8 (default), `Lg` 12 dp.
- For a short wait of unknown length with no layout space, use a [Spinner](Spinner.md).

## States

- Determinate: the fill eases to each new value (the slow motion role).
- Indeterminate: a 40% segment sweeps along the reading direction. With animations turned off it is a
  still, hatched fill — busy, not done.

## Accessibility

- Compose's progress semantics: TalkBack reads a progress bar with its value. The label names it;
  without a label, pass `contentDescription`.
- Right to left, the fill starts at the right and the sweep runs leftwards.

## Theme and tokens

`component.progress.*` (`QeetrixProgressComponentTokens`): track and indicator colours.

## Platform differences

- The percentage is formatted for the locale on all three platforms ("40 %" in French).
- The hatch under reduced motion is drawn, not a CSS gradient; it keeps the web's "/" direction in both
  layout directions, as the web's background does.

Related: [Spinner](Spinner.md).
