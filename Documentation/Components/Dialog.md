# Dialog

`QeetrixDialog`, `QeetrixDialogIntent`, `QeetrixDialogSize` · `components.dialog` · `@qeetrix/react`:
[Dialog](https://ui.qeet.in/components/dialog) (`Dialog`, `DialogContent`, `DialogBody`, `DialogFooter`) ·
sample: Overlays → Dialog

A focused task or decision over the screen: the 3 dp intent line on top, a title and description, a body that
scrolls, and a footer of buttons.

```kotlin
QeetrixDialog(
    open = renaming,
    onOpenChange = { renaming = it },
    title = "Rename workspace",
    description = "Everyone in the workspace sees the new name.",
    footer = {
        QeetrixButton("Cancel", onClick = { renaming = false }, variant = QeetrixButtonVariant.Ghost)
        QeetrixButton("Save", onClick = ::save, loading = saving)
    },
) {
    // the form's fields
}
```

## Usage

- **The app owns `open`.** Back, the close button and a tap on the scrim call `onOpenChange(false)`; the dialog
  stays until the app sets `open = false`, so a form validates — and an action finishes — before it closes.
- **`intent`** is the kind of decision: `Standard` (Ember line, the default), `Danger` (red), `Warning` (amber).
  `Danger` and `Warning` are alert dialogs: a tap outside never closes them.
- **`dismissible`** (default `true`, `false` for `Danger`): the close button at the top end ("Close",
  `qx_close`) and, for a standard dialog, a tap on the scrim. A dialog that is not dismissible has no close
  button; only its actions — and Back — close it.
- **Footer.** End-aligned, `space.gap.sm` apart, wrapping: the tertiary (ghost) action first, then the primary
  or destructive one — at most one primary and one secondary (design rules §7). `footerStart` puts a quiet link
  or a one-line summary at the footer's start; it takes its own line when there is no room.
- **`size`**: `Sm` 400, `Md` 560 (default), `Lg` 800, `Xl` 1120 dp, or `Fullscreen` (square corners, no height
  cap, still under its intent line). Never wider than the window less 24 dp each side; at most 85 % of the
  window tall, the body scrolling between the header and the footer.
- For a decision made only with buttons use the [alert dialog](AlertDialog.md) or the
  [confirmation dialog](ConfirmationDialog.md); for supplementary content on a phone, the
  [bottom sheet](BottomSheet.md).

## Accessibility

- A separate window: focus stays inside and the screen behind cannot be reached. The title is the window's pane
  title, announced as it opens, and a heading.
- Opened from a keyboard, focus goes to the first field, or else the footer's first — least destructive —
  action; the close button comes last, as on the web.
- Back always asks to close, so treat it as the least destructive answer.

## Theme and tokens

dialog.css: `color.surface.overlay`, `shadow.modal`, `radius.modal` (4 dp); the intent line is
`border.width.accent` in `color.border.brand`, `color.border.danger` or `color.icon.warning`; the backdrop is
`color.overlay.scrim`. Header, body and footer padding `space.inset.xl`; title `title.md`, description `body.md`
`color.text.subtle`, `space.gap.xs` apart; 56 dp kept at the header's end for the close button, a neutral ghost
`sm` icon button 12 dp from the top and end. Motion: fade and an 8 dp rise over `motion.duration.slow` on
`motion.easing.enter`; out over `motion.duration.normal` on `motion.easing.exit`; none under reduced motion.

## Platform differences

- **Back.** Android never traps Back: it asks to close even a dialog that is not dismissible (the web's Escape
  does nothing then). Qeetrix registers no Back handler; the dialog window does.
- **No alert-dialog role.** Android has none; a danger or warning dialog is announced by its pane title like any
  other, and its words say what it asks.
- **Touch targets.** Footer buttons keep their 48 dp touch areas in layout, so the footer stands up to 8 dp
  taller than the web's above and below a `md` button; the close button's touch area spreads around it without
  moving it.
- **Scrolling body.** The web makes a scrolling body with nothing focusable a Tab stop; on Android, TalkBack
  scrolls it with its scroll actions and a keyboard user moves through what it holds.
- **Exit.** The web closes when Base UI's state does; here the window stays while it fades out, and ignores
  taps meanwhile.

Related: [Alert dialog](AlertDialog.md), [Confirmation dialog](ConfirmationDialog.md),
[Bottom sheet](BottomSheet.md).
