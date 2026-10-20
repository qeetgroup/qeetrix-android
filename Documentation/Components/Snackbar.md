# Snackbar

`QeetrixSnackbar`, `QeetrixSnackbarHost`, `QeetrixSnackbarHostState`, `QeetrixSnackbarType`,
`QeetrixSnackbarResult` · `components.toast` · web: [Toast](https://ui.qeet.in/docs/components/toast) ·
sample: Feedback → Snackbar

Brief feedback at the bottom of a screen — Android's counterpart of the web Toast and the iOS toast.

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

- **A host per screen.** Remember a `QeetrixSnackbarHostState` in the screen and put the host at the bottom
  of its layout. There is no global service.
- **`show()`** takes the message (its title), a `type` (`Info`, `Success`, `Warning`, `Error`), an optional
  description and action label, and `persistent`; it suspends until the snackbar ends and returns
  `ActionPerformed` or `Dismissed`, exactly once. The app acts on the result.
- **One at a time.** A new snackbar replaces the one on screen (which returns `Dismissed`); nothing queues.
  Showing the one on screen again restarts its time without announcing it twice.
- **Timing.** 5 s, or 10 s for warnings, errors and snackbars with an action, lengthened by the person's
  accessibility timeout; while TalkBack is on a snackbar with an action stays until used or closed.
  `persistent` keeps any snackbar until it is used or closed.
- **Lifecycle.** `show()` runs in the caller's coroutine: when the screen's scope is cancelled, its snackbar
  goes with it.
- **Never show secrets.** A snackbar is on screen and read aloud.
- A failure the person must fix belongs inline, next to what failed.

## Accessibility

Announced as it appears — at once for errors, politely otherwise — as one message; its action and close
buttons are their own controls, and TalkBack offers a Dismiss action. The status is in the icon and words as
well as the colour.

## Theme and tokens

The web toast: `surface.overlay`, `border.default`, `elevation.overlay`, `corner.overlay`; the status as a
3 dp inline-start accent (`feedback.*`) and title colour (`text.*`); filled status icons; an outline action
button; at most 384 dp wide at the bottom end of a wide window.

## Platform differences

- **Name.** Android calls transient in-app feedback a snackbar; it is the Qeetrix Toast's Android form, in
  the Toast family's package.
- **No stack.** The web stacks toasts; Android and iOS show one at a time.
- **System Toast.** For a message that must outlive the app's window, use Android's `android.widget.Toast`;
  Qeetrix does not wrap or imitate it.
- No swipe to dismiss yet; the close button and TalkBack's Dismiss action close it.

Related: [Progress](Progress.md), [Form](Form.md).
