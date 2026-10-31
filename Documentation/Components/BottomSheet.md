# Bottom sheet

`QeetrixBottomSheet`, `QeetrixBottomSheetSize` · `components.drawer` · `@qeetrix/react`:
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

- **The app owns `open`.** A swipe down, a tap on the scrim, Back, the close button and the handle's dismiss
  action call `onOpenChange(false)`; the sheet slides away and leaves the composition. Setting `open = false`
  closes it too; opening it again while it closes turns it round.
- **`dismissible`** (default `true`): `false` removes the close button and stops a swipe down or a tap outside
  from closing it, for a panel only its own actions should close. Back still closes it.
- **`title`** is required, as on the web: it is the sheet's heading and pane title. `description` sits under it.
- **Footer.** Under a rule, end-aligned, tertiary first; `footerStart` holds a quiet link or a summary.
- **Height.** Its content's, at most 85 % of the window; with `partiallyExpandable` a tall sheet opens half-way
  and expands when dragged up. **Width**: `size` `Sm` 400, `Md` 560 (default), `Lg` 800 dp on a large window; a
  phone's full width.

## Accessibility

Its own window: focus stays inside, and the title is announced as the pane opens. The handle carries expand,
collapse and dismiss actions ("Drag handle", `qx_drag_handle`).

## Theme and tokens

drawer.css: `color.surface.overlay`, square corners, the `color.overlay.scrim` backdrop; header padding
`space.inset.xl` with a 1 dp `color.border.subtle` rule below, title `title.md`, description `body.md`
`color.text.subtle`; body padding `space.inset.xl`; footer padding `space.inset.lg` × `space.inset.xl` under a
rule; the close button 20 dp from the top and 16 dp from the end. The handle (40 × 4 dp, `color.border.strong`,
`radius.sm`) sits in the header's top padding, so the title stays 24 dp from the edge.

## Platform differences

- **Bottom, not side.** The web's Drawer is a full-height side panel (`side` start or end); Android shows the
  same content as a bottom sheet, the platform's pattern, through Material 3's `ModalBottomSheet` (the one
  Material component Qeetrix wraps). There is no `side`.
- **Handle.** The web has none; Android sheets show one and expose its actions.
- **Motion and shadow.** The slide is Material's sheet motion, not `motion.duration.slow`; the sheet draws no
  `shadow.modal` (Material's sheet cannot draw a Qeetrix shadow, and over the scrim it would not show).
- **Back** always closes it, dismissible or not.

Related: [Dialog](Dialog.md).
