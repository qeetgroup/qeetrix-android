# Navigation, overlays and feedback

How Qeetrix's navigation chrome, overlays and transient feedback work and how apps use them. The decision
record is [0015](../Decisions/0015-navigation-overlays-and-feedback.md); the looks are `@qeetrix/react`'s
Dialog, Drawer, Menu, Toast, Tabs, Sidebar, AppShell and PageHeader and design rules §11–13; each component's page
is in [Components](../Components/README.md).

## State ownership

Qeetrix presents; the app decides. Every value below is the app's, usually `rememberSaveable` state or a
navigation library's, under the `@qeetrix/react` names.

| Component | The app owns | Qeetrix reports |
|:--|:--|:--|
| Navigation bar, rail, scaffold | The current destination, `value` (any type: an enum, a route) | `onValueChange(value)`, also for the current destination |
| Top app bar | Title, description, actions | The back button's and actions' clicks |
| Tabs | The selected tab, `value`, and the panel shown for it | `onValueChange(value)` for a new choice |
| Alert, confirmation, custom dialog | `open` | `onOpenChange(false)`; then the chosen action, once |
| Bottom sheet | `open` | `onOpenChange(false)` when the person closes it |
| Menu | `open` | `onOpenChange(true)` from the trigger, `onOpenChange(false)`; then the chosen item, once |
| Snackbar | A `QeetrixSnackbarHostState` per screen | The `QeetrixSnackbarResult` of each `show()` |

There is no router, navigation graph, back stack, `NavController` or global service in Qeetrix, and no
component needs one. Qeetrix works with Navigation Compose, Navigation 3 or plain state. An overlay stays until the
app sets `open = false`, so a form validates — and an action finishes — before it closes.

## Navigation items

- `QeetrixNavigationItem(value, label, icon, badge, filledWhenCurrent, enabled)` — `@qeetrix/react`'s SidebarItem,
  one shape for the bar, the rail and the scaffold. Values must be distinct.
- Three to five destinations in a bar; up to seven in a rail. Labels are short nouns; the badge is short text
  ("3", "99+", "New") read with the label. Icons are the **round** shape, the one place Qeetrix uses it (design
  rules §16).
- The current item is marked as design rules §11 mark "current": the 3 dp Ember bar (`border.width.accent`) on the
  edge facing the screen (the start edge on a rail, the top edge on a bottom bar), a `color.bg.neutral.subtle-hover`
  tint, the label at weight 600 and the icon filled in `color.icon.brand` — never by colour alone. The Ember tint
  (`color.bg.selected`) is for selections, not for the current item.
- Tabs mark the selected tab the same way: the Ember bar on its bottom edge and weight 600, over a 2 dp
  `color.border.subtle` rule under the whole list.

## Adaptive layout

- **Window width, not device type.** `QeetrixNavigationScaffold` shows the bar below `QeetrixBreakpoint.Medium`
  (600 dp of window width) and the rail from it (`QeetrixNavigationLayout.forWindowWidth`, Android's compact and
  medium width classes and the web's `--qx-viewport-medium`), and switches as the window is resized, folded or
  rotated, keeping the content's state.
- **Insets.** Bars draw edge to edge and pad for the system bars through their `windowInsets`; the scaffold
  marks those insets consumed for its content. The top app bar pads for the status bar and cutouts. Apps call
  `enableEdgeToEdge()` and pad other content for `WindowInsets.safeDrawing`.
- **Text size.** Bars and the rail grow with the text; bar labels that cannot fit end in an ellipsis and are
  read in full; the rail widens instead.

## Presentation and dismissal

| | Shown by | Dismissed by | Not dismissed by |
|:--|:--|:--|:--|
| Alert / confirmation dialog | `open` | An action, Back, Escape; the close button when `dismissible` | A tap outside |
| Dialog | `open` | Back, Escape; the close button and, for a `Standard` dialog, a tap outside, when `dismissible` | A tap outside a `Danger` or `Warning` dialog |
| Bottom sheet | `open` | Swipe down, a tap outside, Back, the close button, the handle's dismiss action | Swipe, tap outside and close button with `dismissible = false` (Back still closes it) |
| Menu | `open` | An item (unless `closeOnClick = false`), a tap outside, Back, Escape | — |
| Snackbar | `show()` | Its time, its action, its close button, TalkBack's Dismiss, a newer snackbar, its caller's cancellation | — |

- **Exactly once.** An action or menu item asks its overlay to close first, then runs once, however fast it is
  tapped again. An action may present another overlay.
- **Intent.** A dialog's `intent` is the kind of decision, drawn as the 3 dp line on its top edge (design rules
  §13): `Standard` (Ember), `Danger` (red), `Warning` (amber). A `Danger` dialog is not `dismissible` by default.
- **Roles.** Alert-dialog actions are `Primary` (solid), `Secondary` (soft), `Cancel` (ghost) or `Destructive`
  (solid danger, in the primary's place); at most one primary or destructive action and one secondary. A
  destructive choice always comes with Cancel (`qx_cancel`), added when missing. The footer is end-aligned: the
  cancelling action first, then the secondary, then the primary or destructive one.
- **Which overlay.** A decision → alert dialog; "are you sure?" → confirmation dialog; a focused task with
  content → dialog; supplementary content or a short form on a phone → bottom sheet; actions on a control → menu;
  feedback about something that happened → snackbar.
- **Shadows** only here, on the floating layers (design rules §12): `shadow.overlay` for menus, the select list and
  snackbars, `shadow.modal` for dialogs over the `color.overlay.scrim` backdrop. The bottom sheet draws none.
- **Confirmation is not authorisation.** Whatever performs the action checks permission.

## Back

- Overlays are platform windows (Compose `Dialog` and `Popup`, Material's sheet window). Back and Escape go
  to the topmost one, which asks to close; the bottom sheet animates with predictive back.
- Qeetrix registers no `BackHandler`. Screen-to-screen Back is the app's navigation's; a screen that opens a
  detail view handles Back for it (`BackHandler(enabled = detailOpen)`), as the sample's workspace does.
- Nothing traps Back: a dialog or sheet that is not `dismissible` still asks to close on Back (the web's Escape
  does nothing for a danger dialog). To protect unsaved work, ask in `onOpenChange` (a confirmation, or keep a
  draft) rather than refusing.

## Snackbar lifecycle

- One host per screen, at the bottom of its layout; `show()` from that screen's coroutine scope (the web's
  `toast.add()`).
- One at a time; a new snackbar replaces the current one; a repeat restarts the time without a second
  announcement; nothing queues, so nothing appears late.
- 5 s (`QeetrixSnackbarHostState.DefaultTimeout`) unless `timeout` says otherwise (`Duration.INFINITE` keeps it),
  lengthened by the person's accessibility timeout. A `Danger` snackbar stays until it is used or closed (design
  rules §13); while TalkBack is on, a snackbar with an action stays too.
- The caller's cancellation removes its snackbar, so a message never outlives its screen. Each `show()`
  returns exactly once.
- Never put a secret in a snackbar. A failure the person must fix belongs inline.
- Outside the app's window (after its last screen closes), use the platform's `android.widget.Toast`.

## Accessibility

The full table is in [ACCESSIBILITY.md](ACCESSIBILITY.md). For this family:

- Bar, rail and tab items are tabs in a selectable group, read with their selection; at least 48 dp.
- Dialog and sheet titles are headings and are announced as the window opens; focus stays in the window.
  Opened from a keyboard, dialogs focus their first field or else their first (least destructive) action, and
  menus their first available item.
- The top app bar's title and description are one heading; icon buttons carry content descriptions.
- Snackbars are live regions (assertive for `Danger`), one node with a Dismiss action; their buttons are
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
