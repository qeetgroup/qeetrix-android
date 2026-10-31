# Swipe actions

`QeetrixSwipeActions`, `QeetrixSwipeAction`, `QeetrixSwipeActionTone` · `components.list` · `@qeetrix/react`: none · QTRX-106

Actions on a list row, revealed by swiping it.

```kotlin
QeetrixSwipeActions(endAction = QeetrixSwipeAction("Remove", QeetrixIconName.Trash, ::remove, QeetrixSwipeActionTone.Danger, confirmTitle = "Remove Acme?")) {
    QeetrixListItem("Acme", description = "priya@acme.in")
}
```

## Look, behaviour and accessibility

- The action fills the space it uncovers in its tone with its icon and label; tap it or swipe past 60 % to run it. A danger action is confirmed in a [confirmation dialog](ConfirmationDialog.md).
- **Never only by swiping**: each action is an accessibility action of the row; put it in the row's menu too.
- LightImpact as an action is revealed, Warning past the point where it runs.

Tried on the phone emulator (`qeetrix-api36`, Android 16) on 31 October 2026, in light, dark, 200 % text and right to left, from the sample app's page, swiping rows for real. Found there and fixed: in right to left the row moved against the finger and uncovered a bare strip, the action hidden under the row — the row is offset in screen space now (`inRightToLeftTheRowFollowsTheFingerAndTheActionIsWhereItLeft`).
