# 0015 — Navigation, overlays and feedback: native windows, state owned by the app

**Status:** Accepted, 2026-10-19 (Phase 4). Builds on [0010](0010-component-api-conventions.md) and
[0011](0011-component-tokens-and-web-metrics.md); replaces [0002](0002-gradle-build-and-dependency-policy.md)'s
"no Material" for one primitive (decision 4).

## Context

Phase 4 adds the components that move people around an app and interrupt them: the navigation bar and
rail, the top app bar, tabs, dialogs, the bottom sheet, menus and transient feedback.

On the web, Qeetrix builds these itself: dialogs and drawers are portals with a scrim and a focus trap,
menus are positioned layers, and `toast()` feeds one `Toaster`. Android already provides the hard parts —
dialog and popup windows that own focus, Back and the screen behind; a modal bottom sheet with drag,
fling, nested scrolling and predictive back; the system's window size classes — but most of them are
drawn by Material 3, which must not become Qeetrix's design authority. qeetrix-ios settled the same
questions in its ADR 0014; this record takes its positions where Android allows.

Two ways to go wrong: rebuilding the machinery (a router, an overlay layer, a global toast service), or
exposing the bare platform pieces, so that products share no contract.

## Decision

1. **The app owns navigation.** No router, graph, back stack or `NavController` in Qeetrix. The bar, the
   rail and the scaffold take the app's `selected` value and report `onSelect(value)` — typed, as
   `QeetrixOption<T>` is (0014) — also for the current destination, so the app can return to its top.
   They work with Navigation Compose, Navigation 3 or plain state. Back between screens is the app's
   navigation's; Qeetrix registers no Back handler of its own.
2. **Navigation chrome is drawn by Qeetrix on foundation.** `QeetrixNavigationBar`,
   `QeetrixNavigationRail`, `QeetrixTopAppBar`, `QeetrixTabs` are foundation layouts in the web sidebar's
   navigation signature (a quiet Qeet tint, the brand icon, the medium label, a 3 dp brand indicator on the
   edge facing the content — `component.sidebar.indicator-width`) and the web Tabs' two variants.
   `QeetrixNavigationScaffold` picks the bar below 600 dp of window width and the rail from 600 dp — the
   `WindowSizeClass` compact boundary — by window width, never by device type, and keeps the content in one
   composition slot so its state survives the switch. Material's `NavigationSuiteScaffold` is not used: it
   draws Material's bar, rail and drawer, and restyling them would mean overriding most of it.
3. **Overlays open in the platform's windows.** Dialogs use Compose's `Dialog` window (focus contained,
   the screen behind unreachable, Back and Escape handled, the window title announced), edge to edge with
   the Qeetrix scrim (`component.dialog.scrim`) in place of the platform dim. Menus use Compose's `Popup`,
   sharing the select list's anchored placement and motion. The bottom sheet is Material 3's
   `ModalBottomSheet`, in its own window. There is no Qeetrix overlay layer or modal manager.
4. **Material 3, for one primitive.** `material3` is an `implementation` dependency so that
   `QeetrixBottomSheet` can wrap `ModalBottomSheet` — the only foundation-level gap: dragging between
   anchors, nested scrolling, predictive back, and the handle's expand, collapse and dismiss actions.
   - No Material type is public (`SheetState` stays inside; the sheet is driven by a `visible` Boolean).
   - Nothing is drawn from `MaterialTheme`: container, content and scrim colours, shape, elevation and the
     drag handle are Qeetrix's.
   - Only `components/drawer/QeetrixBottomSheet.kt` may import it, and the API baseline may not name it;
     `MaterialBoundaryTest` enforces both.
   - `ModalBottomSheet` is still `@ExperimentalMaterial3Api` in 1.4.0. The opt-in is confined to that file.
     Material keeps hidden binary-compatible overloads when it changes the function, which limits the risk
     of an app resolving a newer Material 3 than Qeetrix's; if that ever breaks, the sheet moves to
     foundation's stable `AnchoredDraggableState` in a `Dialog`, behind the same API.
5. **Presentation state is the app's.** A dialog is shown while it is composed, as Compose's dialogs are.
   The sheet (`visible`) and the menu (`expanded`) take a Boolean, because they animate out and must stay
   composed until they are gone; a sheet asked to open while it closes turns round. A person's dismissal
   calls `onDismissRequest` / `onExpandedChange(false)`, and the app sets the state.
6. **Dialogs are typed and act once.**
   - `QeetrixDialogAction` carries a role (`Default`, `Cancel`, `Destructive`). A destructive action without a
     Cancel gets one (`qx_cancel`); cancelling actions come first in reading order (bottom of a stacked
     footer, start of a row). Opened from a keyboard, focus starts on the first action.
   - Choosing an action calls `onDismissRequest` first and then the action, once, however fast it is tapped.
     Back and Escape dismiss without an action. A tap outside does nothing for an alert (a stray tap must
     not answer it) and dismisses a `QeetrixDialog`.
   - `QeetrixConfirmationDialog` is the alert with its two roles fixed, not a second renderer. A
     confirmation records a choice; it authorises nothing.
7. **No Back traps.** `QeetrixBottomSheet(dismissOnGesture = false)` refuses a swipe and a tap outside, for a
   step to be finished deliberately; Back and the close button still close it. Unsaved-change handling is
   the app's, in `onDismissRequest`.
8. **Menus are drawn by Qeetrix** with the adopted `component.menu` tokens (iOS leaves its menu surface to
   the system and does not). Choosing an item closes the menu and then runs it once; Escape and Back close
   it; opened from a keyboard, focus starts on the first available item. Rows are 48 dp.
9. **Transient feedback is the snackbar.** `QeetrixSnackbar` is Android's counterpart of the web Toast and
   the iOS toast, in the Toast family's package (`components.toast`):
   - a host per screen (`QeetrixSnackbarHost` with a remembered `QeetrixSnackbarHostState`) — no global
     service, no root class;
   - one at a time: a new snackbar replaces the one on screen and nothing queues; showing the one on
     screen again restarts its time without announcing it twice;
   - the web's timing (5 s; 10 s for warnings, errors and anything with an action), lengthened by the
     person's accessibility timeout, and with no time limit on an action while TalkBack is on;
   - `show()` suspends in the caller's coroutine and ends exactly once; a cancelled caller takes its
     snackbar with it, so no message outlives its screen;
   - errors are announced at once, others politely.
10. **Deferred, not built.**
    - **Navigation drawer.** The bar and rail cover three to seven destinations; no Qeet Android consumer
      has more. Revisit with a product that needs one.
    - **Popover.** Anchored actions are a menu, rich content a dialog, and contextual panels on a phone a
      bottom sheet — the form iOS's popover takes on a phone too. A fourth modal surface would duplicate them.
    - **`QeetrixToast`.** In-app feedback is the snackbar. A message that must outlive the app's window
      uses the platform's `android.widget.Toast`; Qeetrix does not imitate it.
11. **Packages.** Web families where the web has one: `components.tabs`, `components.dialog` (alert,
    confirmation and custom dialogs), `components.drawer` (the web Drawer is the mobile bottom sheet),
    `components.dropdownmenu`, `components.toast`. App chrome the web has no family for — bar, rail, top app
    bar, scaffold, which share one item contract — is one Android family, `components.navigation`.

## Consequences

- Dialogs, sheets and menus behave as Android users expect, including Back, Escape, focus containment, the
  sheet's predictive-back animation and TalkBack's window announcements, while Qeetrix draws every surface
  from its tokens.
- Material 3 and Activity Compose reach apps as runtime dependencies (`qeetrix/gradle.lockfile` records
  them); R8 removes what the app does not use. Updating Material is a reviewed change with the sheet's tests.
- Tests follow the split: unit tests for the action rules and the snackbar lifecycle; Compose UI tests on
  the JVM for presentation, dismissal, roles, exactly-once actions and keyboard focus; screenshots of every
  surface drawn in place (dialog and popup windows cannot be captured on the JVM); and an instrumented test
  for Back on a popup, which Robolectric does not route (`QeetrixOverlayBackTest`, still to run on a device).
- Products build their own navigation and present their own overlays; the sample's workspace screen shows
  the idiom.
