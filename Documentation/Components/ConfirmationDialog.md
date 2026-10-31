# Confirmation dialog

`QeetrixConfirmationDialog` · `components.dialog` · `@qeetrix/react`: [Dialog](https://ui.qeet.in/components/dialog)
with a Cancel and a confirming Button · sample: Overlays → Confirmation dialog

"Are you sure?" with exactly two answers: Cancel, and the action that confirms.

```kotlin
QeetrixConfirmationDialog(
    open = confirmRemoval,
    onOpenChange = { confirmRemoval = it },
    title = "Remove Ada from the team?",
    description = "They lose access to the team's projects straight away.",
    confirmLabel = "Remove",
    onConfirm = { removeMember(ada) },
    intent = QeetrixDialogIntent.Danger,
)
```

## Usage

- An [alert dialog](AlertDialog.md) with its actions fixed: Cancel (`cancelLabel`, `qx_cancel` by default) and the
  confirming action — destructive when `intent` is `Danger`, the primary otherwise (`Standard`, `Warning`).
- `onConfirm` runs once, after `onOpenChange(false)`; Cancel and Back only ask to close.
- Title the question; name the action on the confirm button ("Revoke key", not "OK").
- A confirmation records what the person chose; it does not authorise the action.

## Accessibility

As for the [alert dialog](AlertDialog.md): opened from a keyboard, focus starts on Cancel.

## Theme and tokens

The dialog's, at `Sm` (400 dp).

## Platform differences

iOS shows the same choice as the system's confirmation dialog; Android has no separate primitive, so this is the
alert dialog with its two roles fixed. Back always closes it without confirming.

Related: [Alert dialog](AlertDialog.md), [Dialog](Dialog.md).
