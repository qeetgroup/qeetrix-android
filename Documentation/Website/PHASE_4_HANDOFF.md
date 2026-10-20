# ui.qeet.in handoff: Android navigation, overlays and feedback

For whoever maintains the component pages on ui.qeet.in (`qeetrix-docs`). It lists what Qeetrix for
Android now provides for each web component in the Navigation, Overlays and Feedback groups, so a page can
gain an **Android** section. Nothing here has been published: qeetrix-android has no release, and nothing
should be documented as installable until it does.

The source of truth for wording is each component's page in `Documentation/Components/` and its KDoc
(`./gradlew :qeetrix:dokkaGenerate`). The decisions are in
[ADR 0015](../Decisions/0015-navigation-overlays-and-feedback.md); the rules for state, dismissal, Back and
the snackbar lifecycle are in [Standards/NAVIGATION_AND_OVERLAYS.md](../Standards/NAVIGATION_AND_OVERLAYS.md).

## What to add, page by page

| ui.qeet.in page | Android API | One-line summary for the page |
|:--|:--|:--|
| Tabs | `QeetrixTabs`, `QeetrixTab<T>`, `QeetrixTabsVariant` | The same `default` and `line` treatments; the app owns the selection and draws the panel. |
| Alert Dialog | `QeetrixAlertDialog`, `QeetrixDialogAction`, `QeetrixDialogActionRole`, `QeetrixConfirmationDialog` | Typed actions with roles that run once; a destructive choice always comes with Cancel; a tap outside never answers. |
| Dialog | `QeetrixDialog`, `QeetrixDialogSize` | The web's sizes, a scrolling body between a pinned header and footer, in Compose's dialog window. |
| Drawer | `QeetrixBottomSheet` | The mobile bottom sheet, presented by a flag the app owns; drags, flings and predictive back. |
| Dropdown Menu | `QeetrixMenu`, `QeetrixMenuItem`, `QeetrixMenuLabel`, `QeetrixMenuSeparator` | The menu surface and items from `component.menu`; choosing an item closes the menu, then runs it once. |
| Toast | `QeetrixSnackbar`, `QeetrixSnackbarHost`, `QeetrixSnackbarHostState` | Android's form of the toast: a host per screen, one at a time, the web's timing and types. |
| Sidebar | `QeetrixNavigationRail` (and `QeetrixNavigationBar`, `QeetrixNavigationScaffold`) | The sidebar's selection signature on a rail; a bottom bar on compact windows; switched at 600 dp of window width. |
| Page Header | `QeetrixTopAppBar`, `QeetrixBackButton` | An Android top app bar, not the in-page title block: say so if the page gains an Android note. |

Not on Android, by decision — say so on those pages if they gain an Android section:

- **Sheet** (side panel), **Popover**, **Hover Card**: use a bottom sheet, a dialog or a menu.
- **Context Menu**: open a `QeetrixMenu` from the app's long-press handler.
- **Menubar**, **Navigation Menu**, **Command Palette**: not provided.
- A navigation drawer: the bar and the rail cover Qeet apps' destinations.

## Examples to show

Use the examples from the component pages verbatim. They are compiled and composed — every overlay shown —
by `sample/src/test/kotlin/com/qeetgroup/qeetrix/sample/NavigationDocumentationExamplesTest.kt`. Shortened
forms, for a page that needs only one line:

```kotlin
// Tabs
QeetrixTabs(tabs, selected = section, onSelect = { section = it }, variant = QeetrixTabsVariant.Line)

// Alert Dialog
QeetrixConfirmationDialog(title = "Remove Ada from the team?", confirmLabel = "Remove", destructive = true,
    onConfirm = { removeMember(ada) }, onDismissRequest = { confirmRemoval = false })

// Toast
scope.launch { snackbars.show("Conversation archived", actionLabel = "Undo") }

// Sidebar
QeetrixNavigationScaffold(items, selected = destination, onSelect = { destination = it }) { CurrentScreen() }
```

## Differences each page should state

Write each of these as a platform note, not as a limitation:

- **State.** The app owns every selection and presented flag; nothing has a trigger component or a global
  function, and Qeetrix has no router or back stack.
- **Windows and Back.** Dialogs, sheets and menus open in the platform's windows: focus stays inside, Back
  and Escape close the topmost, and the sheet animates with predictive back.
- **Toast.** Android calls it a snackbar; one shows at a time (the web stacks), a repeat restarts its time
  without a second announcement, and an action has no time limit while TalkBack is on.
- **Alert Dialog.** Android draws the Qeetrix surface; iOS uses the system alert.
- **Drawer.** The handle carries expand, collapse and dismiss actions for TalkBack, so it is not hidden as on
  the web.
- **Dropdown Menu.** No checkbox, radio or submenu items yet; rows are 48 dp.
- **Navigation.** Bar or rail is chosen by window width, never by device type.

## Images

Don't take screenshots from the repository: screenshot baselines are never committed
([ADR 0012](../Decisions/0012-visual-regression-testing.md)). To make images for the site, run the sample
app (`./gradlew :sample:installDebug`) on an emulator or device and open Navigation → Navigation bar,
Navigation rail, Adaptive navigation, Top app bar, Tabs; Overlays → Alert dialog, Confirmation dialog,
Dialog, Bottom sheet, Menu; Feedback → Snackbar; and the home screen's Workspace. Use the synthetic content
already on those pages.

## Verification status to quote

- **Verified on the JVM (Robolectric, Android 16 runtime):** behaviour and semantics tests for every
  component (including dialog windows and Back on dialogs and sheets), the snackbar lifecycle under virtual
  time, screenshots of every surface in light, dark, 200 % text and right to left, and the sample's
  workspace flow.
- **Not yet verified:**
  - Back on a popup menu and on stacked overlays (`QeetrixOverlayBackTest`, written for a device);
  - TalkBack speech, focus return after an overlay closes, and Switch Access;
  - predictive back, gesture navigation and display cutouts on a device;
  - tablets, foldables and resizable windows (the switch at 600 dp is tested on the JVM only).

Don't describe these as tested until they are.
