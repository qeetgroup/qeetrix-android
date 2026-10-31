# ui.qeet.in handoff: Android navigation, overlays and feedback

For whoever maintains the component pages on ui.qeet.in (`qeetrix-docs`). It lists what Qeetrix for
Android provides for each `@qeetrix/react` component in the Navigation, Overlays, Feedback and Console frame
groups, so a page can gain an **Android** section. Nothing here has been published: qeetrix-android has no release,
and nothing should be documented as installable until it does.

The source of truth for wording is each component's page in `Documentation/Components/` and its KDoc
(`./gradlew :qeetrix:dokkaGenerate`). The decisions are in
[ADR 0015](../Decisions/0015-navigation-overlays-and-feedback.md); the rules for state, dismissal, Back and
the snackbar lifecycle are in [Standards/NAVIGATION_AND_OVERLAYS.md](../Standards/NAVIGATION_AND_OVERLAYS.md).

## What to add, page by page

| ui.qeet.in page | `@qeetrix/react` | Android API | One-line summary for the page |
|:--|:--|:--|:--|
| Tabs | Tabs, TabsList, Tab | `QeetrixTabs`, `QeetrixTab<T>`, `QeetrixTabsVariant`, `QeetrixTabsOrientation` | The same `line` and `contained` looks and both orientations; the app owns `value` and draws the panel. |
| Dialog | Dialog, DialogContent, DialogBody, DialogFooter | `QeetrixDialog`, `QeetrixDialogIntent`, `QeetrixDialogSize`; `QeetrixAlertDialog` with `QeetrixDialogAction`s; `QeetrixConfirmationDialog` | The intent line, the web's sizes, a scrolling body between the header and footer, in Compose's dialog window; for decisions, typed actions with roles that run once. |
| Drawer | Drawer, DrawerContent, DrawerBody, DrawerFooter | `QeetrixBottomSheet`, `QeetrixBottomSheetSize` | The same header, body and footer as a bottom sheet, opened by the app's `open`; drags, flings and predictive back. |
| Menu | Menu, MenuItem, MenuCheckboxItem, MenuRadioGroup, MenuRadioItem, MenuGroupLabel, MenuSeparator | `QeetrixMenu`, `QeetrixMenuItem`, `QeetrixMenuCheckboxItem`, `QeetrixMenuRadioGroup`, `QeetrixMenuRadioItem`, `QeetrixMenuGroupLabel`, `QeetrixMenuSeparator` | The menu and its items; choosing an item closes the menu, then runs it once. |
| Toast | ToastProvider, `useToast` | `QeetrixSnackbarHostState`, `QeetrixSnackbarHost`, `QeetrixSnackbar` | Android's form of the toast: a host per screen, one at a time, the web's tones and timing. |
| Sidebar | Sidebar, SidebarItem (experimental) | `QeetrixNavigationRail`, `QeetrixNavigationBar`, `QeetrixNavigationItem<T>` | The sidebar item's current mark — the Ember bar, a filled icon — on a rail, and on a bottom bar on compact windows. |
| App shell | AppShell (experimental) | `QeetrixNavigationScaffold` | A bottom bar below 600 dp of window width and a rail from it, keeping the content's state. |
| Page header | PageHeader, PageHeaderBack (experimental) | `QeetrixTopAppBar`, `QeetrixBackButton` | A phone's app bar in PageHeader's band, with a back button at its start: say so if the page gains an Android note. |

Not on Android — say so on those pages if they gain an Android section:

- **Popover** and **Tooltip**: use a bottom sheet, a dialog or a menu; icon buttons have no tooltip.
- **Context menu**: open a `QeetrixMenu` from the app's long-press handler; submenus are not ported either.
- **Top bar**, **Breadcrumb**, **Pagination**, **Steps** and **Command palette**: not provided.
- A navigation drawer: the bar and the rail cover Qeet apps' destinations.

## Examples to show

Use the examples from the component pages verbatim; every overlay in them is shown by
`sample/src/test/kotlin/com/qeetgroup/qeetrix/sample/NavigationDocumentationExamplesTest.kt`, which compiles and
composes them. Shortened forms, for a page that needs only one line:

```kotlin
// Tabs
QeetrixTabs(tabs, value = section, onValueChange = { section = it }, variant = QeetrixTabsVariant.Contained)

// Dialog (a confirmation)
QeetrixConfirmationDialog(open = confirmRemoval, onOpenChange = { confirmRemoval = it }, title = "Remove Ada from the team?",
    confirmLabel = "Remove", onConfirm = { removeMember(ada) }, intent = QeetrixDialogIntent.Danger)

// Toast
scope.launch { snackbars.show("Conversation archived", actionLabel = "Undo") }

// App shell
QeetrixNavigationScaffold(items, value = destination, onValueChange = { destination = it }) { CurrentScreen() }
```

## Differences each page should state

Write each of these as a platform note, not as a limitation:

- **State.** The app owns every selection and every `open` flag; there are no trigger components (`DialogTrigger`,
  `MenuTrigger`) or global functions, and Qeetrix has no router or back stack.
- **Windows and Back.** Dialogs, sheets and menus open in the platform's windows: focus stays inside, Back
  and Escape close the topmost, and the sheet animates with predictive back. Back closes even a dialog that is not
  `dismissible` (the web's Escape does nothing for a danger dialog).
- **Dialog.** Android has no alert-dialog role: a danger or warning dialog is announced by its title like any
  other, and its words say what it asks. The alert dialog keeps its actions as a list, so roles, order and "exactly
  once" are the dialog's.
- **Toast.** Android calls it a snackbar; one shows at a time (the web stacks up to three), a repeat restarts its
  time without a second announcement, and an action has no time limit while TalkBack is on. `toast.promise`,
  `update` and swipe to dismiss are not ported.
- **Drawer.** A bottom sheet, not a side panel: there is no `side`. Its handle carries expand, collapse and
  dismiss actions for TalkBack; the sheet draws no shadow.
- **Menu.** Rows are 48 dp; long labels wrap; keyboard focus is shown by the highlight, as on the web.
- **Navigation.** Bar or rail is chosen by window width, never by device type. The rail keeps each label under its
  icon (touch has no hover for the collapsed sidebar's tooltips), at Android's 80 dp rail width.

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
  - TalkBack speech, focus return after an overlay closes, and Switch Access;
  - predictive back, gesture navigation and display cutouts on a physical device;
  - tablets, foldables and resizable windows (the switch at 600 dp is tested on the JVM only).

Don't describe these as tested until they are.
