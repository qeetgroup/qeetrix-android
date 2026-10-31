# Accessibility

Qeetrix for Android follows Android's accessibility guidance and aims at WCAG 2.2 AA, as `@qeetrix/react`'s design
rules do (§20). This page says what the foundations already provide, what components must add, and what is verified
where.

## What the foundations provide

| Need | Provided by | Verified |
|:--|:--|:--|
| Contrast — 4.5:1 text; 3:1 icons, borders, field lines and the focus frame | The pairings the design rules rely on, from `@qeetrix/tokens` | JVM: 145 pairs × 2 themes (`ContrastTest`), including the field lines and frames on `color.bg.neutral.subtle` and `-hover` |
| Font scaling, including Android 14's non-linear scaling | sp sizes, em line heights | JVM, real text layout (`TypographyTest`) |
| Bold Text | Every role weight bundled; the platform raises the weight | Device check needed |
| Script fallback (Devanagari, Tamil, …) | Platform per-glyph fallback | Device check needed |
| Touch targets of 48 dp | `Modifier.qeetrixMinimumTouchTarget()` | JVM (`MinimumTouchTargetTest`) |
| Decorative vs meaningful icons | `QeetrixIcon(contentDescription = null / "…")` | JVM semantics (`IconTest`) |
| Right-to-left | Directional icons mirror (`autoMirror`) | JVM (`IconTest`); visual check in the sample |
| Reduced motion | `qeetrixAnimationSpec`, from Settings › Remove animations: every Qeetrix duration is 0 | JVM (`MotionTest`) |
| Focus frame | `QeetrixFocus` (`focus.ring.width`, `focus.ring.offset`) in `colors.borderFocus`; every control draws it for keyboard focus where design rules §10 place it | Values: JVM; drawing: device check needed |

## What every component must add

- **Semantics.** A role (`Role.Button`, `Role.Checkbox`, …), a state description where the state is not
  in the label, and merged semantics so TalkBack reads one element per control. Translate the web's ARIA
  intent into Compose semantics; do not copy ARIA attributes.
- **Touch target.** At least 48 × 48 dp, even when the drawn control uses a compact density height.
- **Focus.** Keyboard and D-pad focus order matches visual order; focused controls draw the 2 dp Ember frame
  (`focus.ring.width`, `colors.borderFocus`) where §10 places it — inside buttons with a 1 dp
  `color.surface.default` gap, inside fields in place of the bottom line, `focus.ring.offset` outside checkboxes,
  radios, switches and tags, inside rows. It is never removed and never replaced by colour alone.
- **Text.** Reflow at large font scales instead of truncating; never fix a text container's height.
- **Motion.** Every animation goes through `qeetrixAnimationSpec` (or `QeetrixMotion.spec`).
- **Colour.** Never the only signal: pair status colours with an icon or text; mark the current item with the
  Ember bar and a selection with a check as well as the tint (§11).

## Actions, display and feedback

Verified on the JVM through Compose semantics (`qeetrix/src/test/.../components`); TalkBack's actual speech is a
device check.

| Component | Semantics | Also |
|:--|:--|:--|
| Button, Icon button | Button role; disabled is read as disabled, stays focusable and cannot be activated; loading keeps focus, ignores activation and adds the state "Loading" (`qx_loading`) | The label stays while loading; the icon button requires a `contentDescription`; 48 dp target at every size |
| Text field | The label names the field; an error sets the error state and is announced at once (assertive); it replaces the help text | Read-only stays focusable; disabled does not; "required" is read with the label; `hideLabel` keeps the name |
| Switch | Switch role with on/off state; the labelled row toggles as one control | Read-only (`readOnly`, or `onCheckedChange = null`) exposes the state without the action |
| Card | The title is a heading; a clickable card is one button named by its text | `selected` only draws; the owner states selection (`Modifier.semantics { selected = … }`) |
| Avatar, avatar item | One image named by `name`; `decorative` hides it; an avatar item is one element | Initials keep their size at any font scale |
| Progress, Meter | Progress: progress semantics with its range and value, in progress without a value; a meter is read as its label and value, with its target | Named by `label` or `contentDescription` |
| Spinner | Indeterminate progress named "Loading" (`qx_loading`) or `label`; `decorative` hides it | Not announced when it appears |
| Text, Heading | `QeetrixHeading` is a heading; `QeetrixText` is not | Text in sp; wraps; a truncated text is read in full |
| Badge, Separator | Badge text is read; its icon is decorative; the separator is hidden | Status never rests on colour alone |

## Forms

Every input is built on the shared field parts ([FORMS.md](FORMS.md)): the label is drawn inside the
control, so the control is named by it; "required" is read with the label (`qx_required`); an error sets
the control's error state and is announced at once. Verified on the JVM through the merged semantics tree, the tree
the accessibility delegate reads.

| Component | Semantics | Also |
|:--|:--|:--|
| Field, Field set | `qeetrixFieldControl()` names a custom control "Label, required" and carries the error; the visible label is then hidden so it is read once | The legend is a heading; the set is one traversal group; a disabled set disables its controls |
| Textarea | One editable multi-line field named by its label | Grows from `rows` to `maxRows`, then scrolls inside itself |
| Password input | One password field (Compose's secure field: password semantics, copy and cut unavailable) | Show password is a toggle named "Show password" (`qx_show_password`), its state "Password shown" or "Password hidden"; drawn 28 dp, reached by touches within 48 dp; no node's text or description contains the password (`QeetrixPasswordInputTest` scans them) |
| OTP input | One field named by its label; the slots only draw it | AutoFill type SMS one-time code; never marked a password |
| Checkbox, checkbox group | Checkbox role with its state, "partially checked" when indeterminate; the labelled row is one control | A group's question is a heading and its error is carried by every option; 48 dp rows |
| Radio group | A selectable group; each option a radio button with "selected" / "not selected" | The question is a heading ("required" when `required`); the group's error is carried by every option; 48 dp rows |
| Select | Trigger: drop-down list named by the label, its text the chosen label | The list is a pane titled by the label; options are selectable rows of at least 48 dp, read with their meta; group labels are headings; opened from a keyboard it focuses the chosen option; Escape, Back and a tap outside close it |
| Number field | One editable field with Decrease and Increase custom actions (`qx_decrease`, `qx_increase`) | Arrow keys step; the steppers are named the same but are not focus stops; their touch area runs the field's height |
| Form | The error summary is announced as an alert; its title is a heading read "Error: Fix these to continue" (`qx_alert_danger`, `qx_error_summary_title`); it takes focus | Each item is a 48 dp row that runs its action (focus the field) |

## Navigation, overlays and feedback

Rules in [NAVIGATION_AND_OVERLAYS.md](NAVIGATION_AND_OVERLAYS.md). Verified on the JVM through the merged
semantics tree, including the semantics of dialog and popup windows.

| Component | Semantics | Also |
|:--|:--|:--|
| Navigation bar, rail | A selectable group of tabs, each read with its label, badge and selection | 48 dp items; the current item is marked by the Ember bar, the weight and a filled icon as well as the tint; bar labels ellipsize but are read in full |
| Top app bar | Title and description are one heading; `QeetrixBackButton` is a button named "Back" (`qx_back`) | The back arrow turns round in right-to-left layouts |
| Tabs | A selectable group of tabs with their selection and count; disabled tabs unavailable | 48 dp touch area; each tab its own focus stop, its frame inside; the row grows with the text |
| Alert, confirmation dialog | A dialog window titled by the dialog; the title is a heading and the pane title | Keyboard focus starts on the first (cancelling) action; actions run once; Back closes without an action |
| Dialog | As the alert dialog; the close button is "Close" (`qx_close`) | Keyboard focus starts on the first field, else the first action; the body scrolls between the header and footer at large text sizes |
| Bottom sheet | The handle ("Drag handle", `qx_drag_handle`) offers expand, collapse and dismiss actions; the title is a heading and the pane title | Back always closes it |
| Menu | Items read with their labels and descriptions; checkbox and radio items with their roles and states; disabled items unavailable; group labels are headings; shortcuts and separators are skipped | Keyboard focus starts on the first available item; arrow keys move; Escape closes and returns focus to the trigger; 48 dp rows |
| Snackbar | One live region (assertive for `Danger`) with a Dismiss action; its buttons are their own controls | An action has no time limit while TalkBack is on; the timeout follows the accessibility setting |

## Data display and collections

Rules in [DATA_DISPLAY_AND_COLLECTIONS.md](DATA_DISPLAY_AND_COLLECTIONS.md). Verified on the JVM through the
merged and unmerged semantics trees.

| Component | Semantics | Also |
|:--|:--|:--|
| List item | One element read title, description, metadata, then text in the slots; clickable with an optional named long press; `selected` only when selected | A trailing control stays its own element; at least the row height and 48 dp; from 150 % text the trailing content moves below |
| Section header | Title and description are one heading | The action is its own control |
| Description list | Each pair is one element ("Email, priya@acme.in"); an empty value is read as "Not set" (`qx_not_set`) | A control in a value or an `action` stays its own element; values wrap |
| Accordion, collapsible | Each trigger is a button — an accordion's also a heading — with "Expanded" / "Collapsed" (`qx_expanded`, `qx_collapsed`) and expand or collapse actions | Touch reaches a trigger within 48 dp; a disabled trigger offers no action; opens at once with animations off |
| Empty state | The title is read first, a heading when it has a `level`; the icon is decorative | Actions are their own controls and wrap |
| Data state | Loading is one element, "Loading"; the error is announced as it appears (assertive) | "Try again" (`qx_try_again`); the default words are `qx_data_error` and `qx_data_error_description` |
| Skeleton | Invisible to accessibility services | Holds still with animations off |
| Chip, chip group | A toggling chip is a checkbox; a single-choice group is a selectable group of radio buttons named by its label; a multiple group's chips are checkboxes | 48 dp touch areas; the remove button is named for its chip ("Remove Design", `qx_remove`); selection is a check mark as well as a tint |
| Timeline | Each event is one element — title, description, time; the marker is decorative | From 150 % text the time moves below the description |
| Search field | One field named by its label, or "Search" (`qx_search`); the keyboard's search key | The clear button is "Clear search" (`qx_clear_search`) and returns focus to the field |

## What Phase 6 verified

Automated, over every in-place component through the quality catalog
([ACCESSIBILITY-REPORT.md](../Quality/ACCESSIBILITY-REPORT.md), [0017](../Decisions/0017-quality-gates-and-verification.md)):
the Accessibility Test Framework on an API 36 emulator in light and dark; 48 dp touch areas in layout, or a
proven tap landing for the kinds of control drawn inside another; no clipped text at 100 % and 200 %
(linear and Android's non-linear scale), left to right and right to left; contrast of the colours components
draw (WCAG 1.4.3, 1.4.11); stillness with animations removed. Each check has a negative control proving it can
fail. ATF does not run under Robolectric or on API 29, so a pass there is not counted.

Phase 6 ran these scans on the components as they were before the move to `@qeetrix/react`; the re-specified
components need them run again before a result is quoted. None of this replaces the manual checks below; none of
them has been done.

## What only a device can verify

Unit tests do not prove these; each needs a manual or instrumented check on a device before a component
is marked stable:

- TalkBack reading order, labels and actions; Switch Access and keyboard navigation. In particular:
  - the button's "Loading" state, the text field's error announcement, the labelled switch row, a clickable
    card's name, and the avatar's name;
  - "required" read once with each label, and a field set's legend read as a heading;
  - the password field read as a password, masked unless spoken passwords are on, and the Show password toggle's
    state changing;
  - the OTP field read as one code (not slot by slot), and the SMS code suggestion offered by AutoFill on
    a device that supports it;
  - the select list announced as a pane and its options' selection, closing back to the trigger;
  - the number field's Decrease and Increase actions in TalkBack's actions menu;
  - group errors heard on each checkbox and radio, and the error summary announced and focused after a
    failed submit;
  - each navigation item read as a tab with its badge and selection, and the bar's ellipsized labels read in
    full (`QeetrixOverlayBackTest` covers Back on overlays; the rest is manual);
  - a dialog's and a sheet's title announced as they open, and focus returning to the control that opened
    them once they close;
  - the sheet handle's expand, collapse and dismiss actions, and the sheet's predictive-back animation;
  - menu traversal with arrow keys and a D-pad, and Switch Access over menu items;
  - a snackbar announced once (not again on a repeat), a `Danger` one at once, and an actionable snackbar staying
    while TalkBack is on;
  - edge-to-edge bars clear of gesture navigation and display cutouts, in portrait and landscape, on a
    tablet or resizable window;
  - a list row read as one element in title, description, metadata order, its long-press label in
    TalkBack's actions, and a selected row read as selected;
  - an accordion trigger's "Expanded" / "Collapsed" and its expand and collapse actions;
  - chips read as checkboxes and radio buttons, a chip group's label, and a removable chip's "Remove …"
    button reachable by touch exploration and Switch Access;
  - a data state's "Loading" read once, and an error announced when it replaces the content;
  - TalkBack's list navigation in a long lazy list with sticky section headers, and focus kept on a row
    while items around it are inserted or removed.
- The focus frame under a hardware keyboard and D-pad.
- Bold Text and the largest font scales on real fonts.
- Display P3 colours on a wide-gamut screen.
- Glyph fallback for Indic scripts with Bold Text on a physical device (complete glyphs at 100 % and 200 % on the
  JVM and an emulator, Phase 6).
- High-contrast text (Settings › Accessibility); `@qeetrix/tokens` has no increased-contrast scheme, so
  nothing in the design system changes under it.

`qeetrix/src/androidTest` holds the device tests; run them with
`./gradlew :qeetrix:connectedDebugAndroidTest`. CI runs them on every push and pull request, on API 29 and 36.
