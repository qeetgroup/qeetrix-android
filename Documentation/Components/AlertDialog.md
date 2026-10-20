# Alert dialog

`QeetrixAlertDialog`, `QeetrixDialogAction`, `QeetrixDialogActionRole` · `components.dialog` · web:
[Alert Dialog](https://ui.qeet.in/docs/components/alert-dialog) · sample: Overlays → Alert dialog

A decision the person has to make before going on: a title, an optional message and icon, and the answers.

```kotlin
if (confirmDelete) {
    QeetrixAlertDialog(
        title = "Delete this workspace?",
        message = "Its projects and files are removed for everyone.",
        actions = listOf(QeetrixDialogAction("Delete workspace", QeetrixDialogActionRole.Destructive) { deleteWorkspace() }),
        onDismissRequest = { confirmDelete = false },
    )
}
```

## Usage

- **Shown while composed**, from the app's state; `onDismissRequest` asks the app to stop showing it.
- **Actions have roles.** `Default` is the primary button, `Cancel` the outline button, `Destructive` the
  destructive one. A destructive action without a Cancel gets one (`qx_cancel`). Cancelling actions come
  first — at the bottom when the buttons stack on a narrow window, at the start of the row on a wide one.
- **Exactly once.** Choosing an action calls `onDismissRequest`, then the action, once, however fast it is
  tapped; the action may itself show another dialog.
- **Dismissal.** Back and Escape dismiss without an action; a tap outside does nothing unless
  `dismissOnClickOutside` — a stray tap must not answer.
- For a plain two-answer question use the [confirmation dialog](ConfirmationDialog.md); for content, the
  [dialog](Dialog.md). Keep alerts for what needs attention.

## Accessibility

A separate window: focus stays inside and the screen behind cannot be reached. The title is announced as
the window opens (its window title and pane title) and is a heading. Opened from a keyboard, focus starts on
the first action — Cancel where there is one. The title and message scroll at very large text sizes while
the actions stay in reach.

## Theme and tokens

`component.dialog.*`: `surface.overlay`, `border.default`, `elevation.modal`, the dialog corner
(radius-xl), `overlay.scrim` as the scrim; at most 448 dp wide (`max-w-md`) with 16 dp to spare; the
`heading` role for the title. A destructive alert's icon takes `text.danger`.

## Platform differences

- iOS uses the system alert, drawn in the system's style; Android draws the Qeetrix surface in Compose's
  dialog window, which animates as the app's theme animates dialogs.
- The web footer is written Cancel-first for its focus order; Android orders by role the same way.

Related: [Confirmation dialog](ConfirmationDialog.md), [Dialog](Dialog.md).
