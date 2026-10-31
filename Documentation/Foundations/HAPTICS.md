# Haptics

`QeetrixHapticRole`, `QeetrixHaptics`, `rememberQeetrixHaptics` · `foundation.haptics` · QTRX-134

Qeetrix's feedback roles: what a moment means, which each platform plays as its own haptic. Components ask for a role,
never a pattern, so every Qeetrix app feels the same, and the system's touch-feedback setting is always respected:
with it off, nothing is felt.

```kotlin
val haptics = rememberQeetrixHaptics()
haptics.perform(QeetrixHapticRole.Success)
```

| Role (`value`, shared with qeetrix-ios) | When | Android |
|:--|:--|:--|
| `Selection` (`selection`) | a choice moved: a switch toggled, a segment or date chosen | `SEGMENT_TICK` |
| `Success` (`success`) | something finished as hoped: a refresh done, a form saved | `CONFIRM` |
| `Warning` (`warning`) | a second look before it goes on: a swipe past the point where it deletes | `GESTURE_THRESHOLD_ACTIVATE` |
| `Error` (`error`) | something failed: a copy that couldn't reach the clipboard | `REJECT` |
| `LightImpact` (`light-impact`) | a touch landed: a code copied, a swipe action revealed | `KEYBOARD_TAP` |

Played through Compose's `HapticFeedback` (the view's `performHapticFeedback`).

## Used by Qeetrix's own components

[Switch](../Components/Switch.md) (`Selection` on a toggle), [Copy](../Components/Copy.md) (`LightImpact`, `Error`
when the clipboard refuses), and the swipe actions and pull to refresh as they arrive. The web has no haptics.
