# Side sheet

`QeetrixSideSheet`, `QeetrixSideSheetSide`, `QeetrixSheetSize` · `components.drawer` · `@qeetrix/react`:
[Drawer](https://ui.qeet.in/components/drawer) (`Drawer`, `DrawerContent`, `DrawerBody`, `DrawerFooter`; `side`, `size`) ·
sample: Overlays → Side sheet

A modal sheet at the window's end or start edge, full height — the Drawer as the web shows it: details or an edit form
beside the screen it belongs to, with a ruled header and a footer pinned to its bottom. On a phone it is a
[bottom sheet](BottomSheet.md) with the same content.

```kotlin
QeetrixSideSheet(
    open = editing,
    onOpenChange = { editing = it },
    title = "Edit member",
    footer = {
        QeetrixButton("Cancel", onClick = { editing = false }, variant = QeetrixButtonVariant.Ghost)
        QeetrixButton("Save", onClick = ::save)
    },
) {
    // the form
}
```

## Usage

- **Width decides.** From `breakpoint.medium` (600 dp of window width) it is a side sheet; below, a bottom sheet — one
  call for both, as the navigation scaffold chooses a bar or a rail.
- **`side`**: `End` (default, as the web) or `Start`, in the reading direction: in right to left the end is the left edge.
- **`size`**: `Sm` 400, `Md` 560 (default), `Lg` 800 dp — always leaving 48 dp of the screen beside it
  (`min(size, 100vw - 48px)`).
- **The app owns `open`**, as for the bottom sheet: a swipe or fling towards its edge and a tap on the scrim (only when
  `dismissible`), Back, Escape and the close button call `onOpenChange(false)`. Back always asks to close.
- **`title`** is required; `description` sits under it; `footerStart` holds a quiet link or a summary.

## Accessibility

Its own window: focus stays inside, and the title is announced as the pane opens. The close button is 48 dp; Back and
Escape close it; nothing in it is smaller than 48 dp.

## Theme and tokens

drawer.css, as the bottom sheet: `color.surface.overlay`, square, `shadow.modal`, the `color.overlay.scrim` backdrop;
header padding `space.inset.xl` with a 1 dp `color.border.subtle` rule, title `title.md`, description `body.md`
`color.text.subtle`; body padding `space.inset.xl`; footer `space.inset.lg` × `space.inset.xl` under a rule; the close
button 20 dp from the top and 16 dp from the end. No handle. It slides in from its edge over `motion.duration.slow` on
`motion.easing.enter` and out over `motion.duration.normal` on `motion.easing.exit`; at once under reduced motion. It
keeps clear of the system bars and the display cutout on its side.

## Platform differences

- **A bottom sheet on a phone**, the platform's pattern; the web's Drawer is a side panel at every width.
- **Gestures**: it follows the finger towards its edge and back, and a predictive back gesture shrinks it towards its
  edge.
- **Right to left** comes from the system locale, as for every window (README › Limitations).

Tried on the phone emulator in landscape (`qeetrix-api36`, 914 dp wide, Android 16) on 31 October 2026: at the end
edge, 560 dp wide and full height, the footer above the gesture area and the status-bar icons dark on its surface; a
swipe towards its edge closes it. With the sample's language set to Arabic (`cmd locale set-app-locales`) it opens at
the left edge, mirrored, keeps clear of the display cutout there, and a swipe left closes it. The library's device tests
pass in landscape, the side sheet's window included in the accessibility scan.

Related: [Bottom sheet](BottomSheet.md), [Dialog](Dialog.md).
