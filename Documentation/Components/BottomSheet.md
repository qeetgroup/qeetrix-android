# Bottom sheet

`QeetrixBottomSheet` · `components.drawer` · web: [Drawer](https://ui.qeet.in/docs/components/drawer) (the
mobile bottom sheet) · sample: Overlays → Bottom sheet

A modal sheet that rises from the bottom edge, for supplementary content and actions: details, filters, a
short form, a list of choices.

```kotlin
var filtering by rememberSaveable { mutableStateOf(false) }
QeetrixBottomSheet(
    visible = filtering,
    onDismissRequest = { filtering = false },
    title = "Filter invoices",
    footer = { QeetrixButton("Show results", onClick = { filtering = false }, modifier = Modifier.fillMaxWidth()) },
) {
    QeetrixRadioGroup(status, { status = it }, statusOptions, label = "Status")
}
```

## Usage

- **Presented by `visible`**, from the app's state. Setting it to `false` slides the sheet away and removes
  it from the composition; opening it again while it leaves turns it round. A person's swipe, tap outside,
  Back or the close button calls `onDismissRequest`, which should set `visible` to `false`.
- **Height.** Its content's, at most 85 % of the window's, and at most 640 dp wide on a large window. With
  `partiallyExpandable`, a tall sheet opens half-way and expands when dragged up.
- **`dismissOnGesture = false`** refuses a swipe and a tap outside, for a step the person must finish or
  cancel deliberately. Back and the close button still close it: Android never traps Back.
- The footer stacks its buttons full width, in order.

## Accessibility

Compose's modal bottom sheet in its own window: focus stays in it, it moves up with the keyboard, and it
animates with predictive back. The handle ("Drag handle", `qx_drag_handle`) carries expand, collapse and
dismiss actions for TalkBack users, who cannot drag. The title is a heading; the close button is "Close".

## Theme and tokens

`component.dialog.*` rounded on top, with a rule along its top edge; a 40 × 4 dp handle in
`border.strong`; 16 dp header and footer padding; the dialog scrim.

## Platform differences

- Android's sheet is Material 3's `ModalBottomSheet` underneath — the one Material primitive Qeetrix uses,
  with every colour and shape Qeetrix's ([ADR 0015](../Decisions/0015-navigation-overlays-and-feedback.md)).
  Its motion is the platform's, following the finger and the fling, rather than the web drawer's curves.
- The web hides its handle from screen readers; Android exposes it, because it carries the sheet's actions.
- No shadow (the web's `elevation.modal`): Material's sheet cannot draw one, and over the scrim it would not be
  seen. Until Phase 7 the shadow and rule were passed to Material as a modifier, which drew them as an empty
  panel at the top of the screen; `QeetrixBottomSheetDeviceTest` now guards against it. The `modifier`
  parameter is for semantics and test tags for the same reason.
- The web's side `Sheet` has no Android counterpart yet.

Related: [Dialog](Dialog.md), [Menu](Menu.md).
