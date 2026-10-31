# Bottom sheet

`QeetrixBottomSheet`, `QeetrixSheetSize` · `components.drawer` · `@qeetrix/react`:
[Drawer](https://ui.qeet.in/components/drawer) (`Drawer`, `DrawerContent`, `DrawerBody`, `DrawerFooter`) ·
sample: Overlays → Bottom sheet

A modal sheet that rises from the bottom edge — Android's form of the Drawer: details, filters or a short form
beside the screen they belong to, with a ruled header and footer.

```kotlin
QeetrixBottomSheet(
    open = filtering,
    onOpenChange = { filtering = it },
    title = "Filter invoices",
    footer = {
        QeetrixButton("Clear", onClick = ::clear, variant = QeetrixButtonVariant.Ghost)
        QeetrixButton("Show results", onClick = { filtering = false })
    },
) {
    // the filters
}
```

## Usage

- **The app owns `open`.** A swipe or fling down, a tap on the scrim, Back, Escape and the close button call
  `onOpenChange(false)`; the sheet slides away and leaves the composition. An app that keeps `open = true` gets the
  sheet back. Setting `open = false` closes it too; opening it again while it closes turns it round.
- **`dismissible`** (default `true`): `false` removes the close button and stops a swipe down or a tap outside
  from closing it, for a panel only its own actions should close. Back still closes it.
- **`title`** is required, as on the web: it is the sheet's heading and pane title. `description` sits under it.
- **Footer.** Under a rule, end-aligned, tertiary first; `footerStart` holds a quiet link or a summary.
- **Height.** Its content's, at most 85 % of the window; with `partiallyExpandable` a tall sheet opens half-way
  and expands when dragged up, when its content is scrolled or when focus moves into it. **Width**: `size` `Sm` 400, `Md` 560 (default), `Lg` 800 dp on a large window; a
  phone's full width.

## Accessibility

Its own window: focus stays inside, and the title is announced as the pane opens. The handle is only a mark: the
close button, Back and Escape close the sheet, so there is no 12 dp control to find or to miss (once finding Q-6,
QTRX-198). A partially expandable sheet opens fully while touch exploration is on and expands when focus moves into
it, so nothing is left below the window's edge.

## Theme and tokens

drawer.css: `color.surface.overlay`, square corners, `shadow.modal`, the `color.overlay.scrim` backdrop; header padding
`space.inset.xl` with a 1 dp `color.border.subtle` rule below, title `title.md`, description `body.md`
`color.text.subtle`; body padding `space.inset.xl`; footer padding `space.inset.lg` × `space.inset.xl` under a
rule; the close button 20 dp from the top and 16 dp from the end. The handle (40 × 4 dp, `color.border.strong`,
`radius.sm`) sits in the header's top padding, so the title stays 24 dp from the edge. Motion: in over
`motion.duration.slow` on `motion.easing.enter`, out over `motion.duration.normal` on `motion.easing.exit`, the scrim
fading with it; at once under reduced motion.

## Platform differences

- **Bottom, not side.** The web's Drawer is a full-height side panel (`side` start or end); on a phone Android shows
  the same content as a bottom sheet, the platform's pattern, built on Compose foundation in its own window (until
  QTRX-198 it was Material 3's `ModalBottomSheet`). For the side panel on wider windows, use
  [`QeetrixSideSheet`](SideSheet.md) (QTRX-195), which is this sheet below `breakpoint.medium`.
- **Handle.** The web has none; Android sheets show one, as a mark only.
- **Gestures.** The sheet follows the finger and the fling, as Android's sheets do; the web's Drawer swipes too.
  A predictive back gesture shrinks it towards the bottom edge as it goes.
- **Back** always asks to close it, dismissible or not.

Tried on the phone emulator (full width, a swipe down closes it) and the tablet emulator (`qeetrix-tablet-api36`,
Android 16) on 31 October 2026 after QTRX-198 — on the tablet: 560 dp wide and
centred, the handle only a mark (nothing for UI Automator or the Accessibility Test Framework to find); a swipe down
closes it; a predictive back gesture shrinks it towards the bottom edge, a cancelled one lets it go back, a completed
one closes it; the half-way sheet opens at half the window and expands to 85 % when dragged up. The library's device
tests — the sheet's window, Back on overlays, and the overlay windows' accessibility scan with nothing excluded — pass.

Related: [Dialog](Dialog.md).
