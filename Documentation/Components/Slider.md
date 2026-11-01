# Slider

`QeetrixSlider`, `QeetrixRangeSlider` · `components.slider` · `@qeetrix/react`: Slider · QTRX-130

A value, or a range, along a scale.

```kotlin
QeetrixSlider(volume, { volume = it }, label = "Volume", step = 5f, minLabel = "Quiet", maxLabel = "Loud")
```

## Look, behaviour and accessibility

- **Look** (slider.css): the label and value in `label.sm` above a 4 dp `border.default` track, the `bg.brand.bold` indicator, a 16 dp square thumb with a 2 dp `surface.default` ring; mirrored in right to left.
- The control is 48 dp tall, so each thumb is a whole touch target; a tap on the track moves there. Arrows step (1 % without a step), Page Up/Down by 10 %, Home/End to the ends.
- Each thumb is adjustable for TalkBack; a range's are "Budget, minimum" and "Budget, maximum". A step change plays the Selection haptic.

Tried on the phone emulator (`qeetrix-api36`, Android 16) on 31 October 2026, in light, dark, 200 % text and right to left, from the sample app's page. Found there and fixed: in right to left the thumb stood at its left-to-right place, away from the indicator's end (`inRightToLeftTheThumbIsAtItsValueFromTheRight`).
