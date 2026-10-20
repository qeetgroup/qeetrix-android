# Dialog

`QeetrixDialog`, `QeetrixDialogSize` · `components.dialog` · web: [Dialog](https://ui.qeet.in/docs/components/dialog) ·
sample: Overlays → Dialog

A modal window for a focused task: a title and description, content that scrolls, and a footer of buttons.

```kotlin
if (renaming) {
    QeetrixDialog(
        title = "Rename workspace",
        description = "Everyone in the workspace sees the new name.",
        onDismissRequest = { renaming = false },
        footer = {
            QeetrixButton("Cancel", onClick = { renaming = false }, variant = QeetrixButtonVariant.Outline)
            QeetrixButton("Save", onClick = ::save)
        },
    ) {
        QeetrixTextField(name, label = "Name", required = true, message = nameMessage)
    }
}
```

## Usage

- **Content** is Qeetrix components; the dialog does nothing with their values. The footer's buttons are the
  app's and close the dialog only when the app says so, so a form validates before it closes.
- **Footer** buttons are laid out as [form actions](Form.md): stacked with the last on top on a narrow
  window, a row at the end on a wide one. The header and footer stay put while the content scrolls.
- **Dismissal.** Back, Escape, the close button ("Close", `qx_close`, `showCloseButton`) and a tap outside
  (`dismissOnClickOutside`, on by default) call `onDismissRequest`. Ask about unsaved work there rather than
  refusing to close.
- **Size.** `Sm` 384, `Default` 512, `Lg` 672, `Xl` 896 dp, or `Full`; always 16 dp from the window's edges.
- Dialog versus [bottom sheet](BottomSheet.md): a dialog takes the person's whole attention; a sheet holds
  supplementary content on a phone, where it is easier to reach and to swipe away.

## Accessibility

A separate window that contains focus; the title is announced as it opens and is a heading; controls stay
reachable at large text sizes because the content scrolls between the pinned header and footer.

## Theme and tokens

`component.dialog.*` as for the alert dialog; `p-6`, 16 dp between parts; the ghost `icon-sm` close button.

## Platform differences

iOS deferred a centred dialog and uses a sheet (a centred form sheet on iPad). Android has a centred dialog
on every window size, as the web does.

Related: [Alert dialog](AlertDialog.md), [Bottom sheet](BottomSheet.md), [Form](Form.md).
