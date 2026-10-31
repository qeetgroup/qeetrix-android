# Steps

`QeetrixSteps`, `QeetrixStep`, `QeetrixStepStatus`, `QeetrixStepsOrientation` · `components.steps` · `@qeetrix/react`: Steps (experimental) · QTRX-123

Where a person is in a setup of a few steps.

```kotlin
QeetrixSteps(listOf(QeetrixStep("Account"), QeetrixStep("Verify"), QeetrixStep("Done")), current = 1)
```

## Look, behaviour and accessibility

- **Look** (steps.css): 24 dp markers (`radius.control`, growing with large text): done Ember with a tick, current a 2 dp Ember edge, upcoming a `border.strong` edge, failed and attention in their tones; a 1 dp line that turns Ember behind done steps.
- Horizontal shows a compact "Step 2 of 4", the current label and a bar under 72 dp a step; vertical adds descriptions.
- Each step is read with its state and position ("Verify, Current step, 2 of 4"); with `onStepSelect` done steps are buttons.

Tried on the phone emulator (`qeetrix-api36`, Android 16) on 31 October 2026, in light, dark, 200 % text and right to left, from the sample app's page. Found there and fixed: at 200 % text a label broke mid-word ("Accou / nt") — the steps go compact when a label's word wouldn't fit its column (`aLabelThatWouldBreakMidWordMakesThemCompact`). In the compact form only the current step shows, so going back is the screen's own Back.
