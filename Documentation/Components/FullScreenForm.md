# Full-screen form

`QeetrixFullScreenForm` · `components.dialog` · `@qeetrix/react`: none (Dialog's `fullscreen` size is the nearest) · QTRX-133

A modal screen for creating or editing something, where a dialog or a sheet is too small.

```kotlin
QeetrixFullScreenForm(open, { open = it }, title = "New account", confirmLabel = "Save", onConfirm = ::save, dirty = edited) {
    QeetrixTextField(name, label = "Account name")
}
```

## Look, behaviour and accessibility

- **The bar**: Close (`x`) at the start, the title (`title.lg`, a heading), the confirming action (a small solid button) at the end, over a 1 dp `border.default` rule. `confirmEnabled` keeps it off until the form can be saved.
- **The form** scrolls with `space.inset.lg` round it and keeps clear of the keyboard.
- **Unsaved changes**: with `dirty`, Close and Back first ask "Discard changes?" (Discard, danger; Keep editing).
- **Size**: the whole screen on a phone; from `breakpoint.medium` a 560 dp form sheet, at most 90 % of the height, over the scrim.
- **The system bars**: their icons are set for what is under them — dark over the form's surface in light mode, light in dark mode and over a tablet's scrim.

Tried on the phone emulator (`qeetrix-api36`, Android 16) on 31 October 2026, in light, dark, 200 % text and right to left, from the sample app's page. Found there and fixed: the form's own window drew white status-bar icons on its white surface, so the clock and battery disappeared; the icons are now set for the window (`DialogSystemBars`, which `QeetrixDialog` uses too).
