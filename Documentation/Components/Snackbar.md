# Snackbar

`QeetrixSnackbar`, `QeetrixSnackbarHost`, `QeetrixSnackbarHostState`, `QeetrixSnackbarTone`,
`QeetrixSnackbarResult` · `components.toast` · `@qeetrix/react`: [Toast](https://ui.qeet.in/components/toast)
(`ToastProvider`, `useToast`) · sample: Feedback → Snackbar

Brief feedback at the bottom of a screen — Android's form of the Toast.

```kotlin
val snackbars = rememberQeetrixSnackbarHostState()
val scope = rememberCoroutineScope()
Box(Modifier.fillMaxSize()) {
    InboxList(onArchive = { conversation ->
        archive(conversation)
        scope.launch {
            val result = snackbars.show("Conversation archived", actionLabel = "Undo")
            if (result == QeetrixSnackbarResult.ActionPerformed) unarchive(conversation)
        }
    })
    QeetrixSnackbarHost(snackbars, Modifier.align(Alignment.BottomCenter))
}
```

## Usage

- **A host per screen.** Remember a `QeetrixSnackbarHostState` in the screen and put the host at the bottom of
  its layout; `show()` is the web's `toast.add()`.
- **`show()`** takes the `title` (what happened, in a few words), a `tone` (`Info`, `Success`, `Warning`,
  `Danger`), an optional one-line `description`, at most one action (`actionLabel`) and a `timeout`; it suspends
  until the snackbar ends and returns `ActionPerformed` or `Dismissed`, exactly once.
- **Timing.** 5 s (`DefaultTimeout`) unless `timeout` says otherwise (`Duration.INFINITE` keeps it), lengthened by
  the person's accessibility timeout. A `Danger` snackbar stays until it is used or closed (design rules §13);
  while TalkBack is on, a snackbar with an action stays too.
- **One at a time.** A new snackbar replaces the one on screen; showing the same one again restarts its time
  without announcing it twice.
- **Lifecycle.** `show()` runs in the caller's coroutine: when the screen's scope is cancelled, its snackbar goes.
- **Never show secrets.** A snackbar is on screen and read aloud. A failure the person must fix belongs inline.

## Accessibility

Announced as it appears — at once for `Danger`, politely otherwise — as one message; its action and close buttons
are their own controls with 48 dp touch areas, and TalkBack offers a Dismiss action. The tone is in the icon and
the words as well as the colour.

## Theme and tokens

toast.css: `color.surface.overlay`, `shadow.overlay`, `radius.overlay`; a 3 dp (`border.width.accent`) start edge
and a sharp 16 dp status icon in `color.icon.info` / `-success` / `-warning` / `-danger` (info, circle-check,
triangle-alert, circle-alert); padding 12 / 8 / 12 / 14 dp, `space.gap.md` gaps; title `title.sm`, description
`body.sm` `color.text.subtle`, 2 dp apart; a ghost `sm` action and a neutral ghost `sm` close button. At most
360 dp wide, `space.inset.lg` from the bottom and end. Motion: fade and a 4 dp rise over
`motion.duration.normal`, out over `motion.duration.fast`.

## Platform differences

- **Name.** Android calls transient in-app feedback a snackbar; it is the Toast's Android form, in the Toast
  family's package.
- **No stack.** The web stacks up to three toasts, 8 dp apart; Android shows one at a time, as Android's
  snackbars and iOS's toasts do.
- **Not implemented:** `toast.promise` (the pending spinner), `update`, swipe to dismiss, and the region's
  "Notifications" landmark (Android has no landmarks).
- **Close label.** "Dismiss" (`qx_dismiss`, the web's `toast.close`).
- **System Toast.** For a message that must outlive the app's window, use `android.widget.Toast`.

Related: [Progress](Progress.md).
