# Segmented control

`QeetrixSegmentedControl` · `components.segmented` · `@qeetrix/react`: ToggleGroup `variant="segmented"` · QTRX-105

One choice from a few, side by side: All / Pending, Light / Dark / System.

```kotlin
QeetrixSegmentedControl(filter, { filter = it }, listOf(QeetrixOption("all", "All"), QeetrixOption("pending", "Pending")))
```

## Look, behaviour and accessibility

- **Look** (toggle.css): a `border.strong` strip with `radius.control` corners at `size.control.*` height; the chosen segment `bg.neutral.bold` / `text.inverse`; hover `bg.neutral.subtle`.
- Each segment is a 48 × 48 dp target with the strip drawn in its middle; radio buttons for accessibility; a choice plays the Selection haptic.

Tried on the phone emulator (`qeetrix-api36`, Android 16) on 31 October 2026, in light, dark, 200 % text and right to left, from the sample app's page: a choice plays the Selection haptic (seen in the vibrator's log). A disabled control keeps its chosen segment's fill, as toggle.css does, so it looks enabled (QTRX-214).
