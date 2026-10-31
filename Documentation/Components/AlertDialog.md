# Alert dialog

`QeetrixAlertDialog`, `QeetrixDialogAction`, `QeetrixDialogActionRole` · `components.dialog` · `@qeetrix/react`:
[Dialog](https://ui.qeet.in/components/dialog) with `intent="danger"` or `"warning"` and a footer of actions
(Base UI's Alert Dialog) · sample: Overlays → Alert dialog

A decision the person makes with buttons before going on: a title written as the question, an optional
description, and the answers.

```kotlin
QeetrixAlertDialog(
    open = confirmDelete,
    onOpenChange = { confirmDelete = it },
    title = "Delete this workspace?",
    description = "Its projects and files are removed for everyone.",
    actions = listOf(QeetrixDialogAction("Delete workspace", QeetrixDialogActionRole.Destructive) { deleteWorkspace() }),
)
```

## Usage

- **The app owns `open`**, as for the [dialog](Dialog.md).
- **Actions have roles**, drawn by the design rules' hierarchy (§7): `Primary` the solid Ember button,
  `Secondary` the soft graphite one, `Cancel` the ghost (tertiary) one, `Destructive` the solid danger one in the
  primary's place. At most one primary or destructive action and one secondary — the dialog checks. A
  destructive action without a Cancel gets one (`qx_cancel`). The footer orders them cancelling first, then the
  secondary, then the primary or destructive action at the end.
- **Exactly once.** Choosing an action asks to close (`onOpenChange(false)`), then runs it, once per opening,
  however fast it is tapped; nothing runs while the dialog leaves.
- **`intent`** defaults to `Danger` when an action is destructive and `Standard` otherwise; pass `Warning` for the
  amber line. A `Danger` dialog is not `dismissible` by default: no close button.
- **Dismissal.** Back dismisses without an action; a tap outside never answers an alert dialog.
- `size` defaults to `Sm` (400 dp), as the web's examples size a decision.

## Accessibility

As for the [dialog](Dialog.md). Opened from a keyboard, focus starts on the first action — the cancelling one.

## Theme and tokens

The dialog's (dialog.css); the buttons are [Button](Button.md)'s `solid`, `soft`, `ghost` and `solid` +
`tone = Danger`.

## Platform differences

- `@qeetrix/react` has no separate alert-dialog component: the web writes the same footer of Buttons inside
  `DialogFooter`. Android keeps the action list so that roles, order and "exactly once" are the dialog's.
- The web's dialog has no icon; the icon parameter the earlier Android dialog had is gone — the intent line and
  the words carry the meaning.
- Back always closes it, without an action (the web's Escape does nothing for a danger dialog).

Related: [Dialog](Dialog.md), [Confirmation dialog](ConfirmationDialog.md).
