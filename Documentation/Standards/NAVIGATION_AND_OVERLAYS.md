# Navigation, overlays and feedback

How Qeetrix's navigation chrome, overlays and transient feedback work and how apps use them. The decision
record is [0015](../Decisions/0015-navigation-overlays-and-feedback.md); each component's page is in
[Components](../Components/README.md).

## State ownership

Qeetrix presents; the app decides. Every value below is the app's, usually `rememberSaveable` state or a
navigation library's.

| Component | The app owns | Qeetrix reports |
|:--|:--|:--|
| Navigation bar, rail, scaffold | The selected destination (any type: an enum, a route) | `onSelect(value)`, also for the current destination |
| Top app bar | Title, actions, whether content is scrolled | The back button's and actions' clicks |
| Tabs | The selected tab, and the panel shown for it | `onSelect(value)` for a new choice |
| Alert, confirmation, custom dialog | Whether it is composed | `onDismissRequest`; then the chosen action, once |
| Bottom sheet | `visible` | `onDismissRequest` when the person closes it |
| Menu | `expanded` | `onExpandedChange(false)`; then the chosen item, once |
| Snackbar | A `QeetrixSnackbarHostState` per screen | The `QeetrixSnackbarResult` of each `show()` |

There is no router, navigation graph, back stack, `NavController` or global service in Qeetrix, and no
component needs one. Qeetrix works with Navigation Compose, Navigation 3 or plain state.

## Navigation items

- `QeetrixNavigationItem(value, label, icon, badge, enabled)` — one shape for the bar, the rail and the
  scaffold. Values must be distinct.
- Three to five destinations in a bar; up to seven in a rail. Labels are short nouns; the badge is short
  text ("3", "99+") read with the label.
- Selection is drawn in the web sidebar's signature: tint, brand icon, medium label and the brand indicator
  on the edge facing the content — never by colour alone.

## Adaptive layout

- **Window width, not device type.** `QeetrixNavigationScaffold` shows the bar below 600 dp of window width
  and the rail from 600 dp (`QeetrixNavigationLayout.forWindowWidth`, the `WindowSizeClass` compact
  boundary), and switches as the window is resized, folded or rotated, keeping the content's state.
- **Insets.** Bars draw edge to edge and pad for the system bars through their `windowInsets`; the scaffold
  marks those insets consumed for its content. The top app bar pads for the status bar and cutouts. Apps call
  `enableEdgeToEdge()` and pad other content for `WindowInsets.safeDrawing`.
- **Text size.** Bars and the rail grow with the text; bar labels that cannot fit end in an ellipsis and are
  read in full; the rail widens instead.

## Presentation and dismissal

| | Shown by | Dismissed by | Not dismissed by |
|:--|:--|:--|:--|
| Alert / confirmation dialog | Composition | An action, Back, Escape | A tap outside (unless asked) |
| Dialog | Composition | Back, Escape, Close, a tap outside | — (a tap outside is opt-out) |
| Bottom sheet | `visible` | Swipe down, a tap outside, Back, Close | Swipe and tap outside with `dismissOnGesture = false` |
| Menu | `expanded` | An item, a tap outside, Back, Escape | — |
| Snackbar | `show()` | Its time, its action, Close, TalkBack's Dismiss, a newer snackbar, its caller's cancellation | — |

- **Exactly once.** An action or menu item closes its overlay first, then runs once, however fast it is
  tapped again. An action may present another overlay.
- **Roles.** Dialog actions are `Default`, `Cancel` or `Destructive`; a destructive choice always comes with
  Cancel; cancelling actions come first in reading order.
- **Which overlay.** A decision → alert dialog; "are you sure?" → confirmation dialog; a focused task with
  content → dialog; supplementary content or actions on a phone → bottom sheet; actions on a control → menu;
  feedback about something that happened → snackbar.
- **Confirmation is not authorisation.** Whatever performs the action checks permission.

## Back

- Overlays are platform windows (Compose `Dialog` and `Popup`, Material's sheet window). Back and Escape go
  to the topmost one, which closes; the bottom sheet animates with predictive back.
- Qeetrix registers no `BackHandler`. Screen-to-screen Back is the app's navigation's; a screen that opens a
  detail view handles Back for it (`BackHandler(enabled = detailOpen)`), as the sample's workspace does.
- Nothing traps Back: a sheet that refuses gestures still closes on Back. To protect unsaved work, ask in
  `onDismissRequest` (a confirmation, or keep a draft) rather than refusing.

## Snackbar lifecycle

- One host per screen, at the bottom of its layout; `show()` from that screen's coroutine scope.
- One at a time; a new snackbar replaces the current one; a repeat restarts the time without a second
  announcement; nothing queues, so nothing appears late.
- 5 s, or 10 s for warnings, errors and actions, then the person's accessibility timeout; no time limit on
  an action while TalkBack is on; `persistent` for messages that need closing.
- The caller's cancellation removes its snackbar, so a message never outlives its screen. Each `show()`
  returns exactly once.
- Never put a secret in a snackbar. Persistent failures belong inline.
- Outside the app's window (after its last screen closes), use the platform's `android.widget.Toast`.

## Accessibility

The full table is in [ACCESSIBILITY.md](ACCESSIBILITY.md). For this family:

- Bar, rail and tab items are tabs in a selectable group, read with their selection; at least 48 dp.
- Dialog and sheet titles are headings and are announced as the window opens; focus stays in the window.
  Opened from a keyboard, dialogs focus their first action and menus their first item.
- The top app bar's title is a heading; icon buttons carry content descriptions.
- Snackbars are live regions (assertive for errors), one node with a Dismiss action; their buttons are
  their own controls.
- Destructive meaning is in the words as well as the colour.

## Testing

- **Unit:** dialog action rules; the snackbar state machine under virtual time (timing, replacement,
  repeats, cancellation, exactly one result, accessibility timing).
- **Compose UI (JVM):** selection and reporting, roles and semantics, dismissal paths, exactly-once
  actions, keyboard focus, insets-free layout positions, the scaffold's switch keeping content state.
- **Screenshots:** every surface drawn in place in light, dark, 200 % text and right to left; dialog, popup
  and sheet windows cannot be captured on the JVM.
- **Integration:** the sample's `WorkspaceFlowTest` — destinations, a detail with system Back, a menu, a
  validating dialog, a confirmation, a snackbar with Undo and a bottom sheet.
- **Device:** `QeetrixOverlayBackTest` (Back on a popup and on stacked overlays) and the TalkBack checks in
  ACCESSIBILITY.md.
