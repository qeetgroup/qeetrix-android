# Confirmation dialog

`QeetrixConfirmationDialog` · `components.dialog` · web: [Alert Dialog](https://ui.qeet.in/docs/components/alert-dialog)
(confirm style) · sample: Overlays → Confirmation dialog

"Are you sure?" with exactly two answers: Cancel and the action that confirms.

```kotlin
if (confirmRemoval) {
    QeetrixConfirmationDialog(
        title = "Remove Ada from the team?",
        message = "They lose access to the team's projects straight away.",
        confirmLabel = "Remove",
        destructive = true,
        onConfirm = { removeMember(ada) },
        onDismissRequest = { confirmRemoval = false },
    )
}
```

## Usage

- Write the title as the question and `confirmLabel` as its answer ("Delete workspace", not "OK").
- `destructive` draws the confirming action as the destructive button; `cancelLabel` replaces "Cancel".
- `onConfirm` runs once, after `onDismissRequest`. Cancel, Back and Escape only dismiss; a tap outside does
  nothing.
- **A confirmation is not authorisation.** It records a choice; check permission where the action runs.

## Accessibility

As the [alert dialog](AlertDialog.md); opened from a keyboard, focus starts on Cancel.

## Platform differences

iOS shows this choice as the system's confirmation dialog (an action sheet). Android has no separate
primitive, so it is the alert dialog with its two roles fixed — the same renderer, not a copy.

Related: [Alert dialog](AlertDialog.md).
