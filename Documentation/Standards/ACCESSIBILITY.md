# Accessibility

Qeetrix for Android follows Android's accessibility guidance and aims at WCAG 2.2 AA. This page says
what the foundations already provide, what components must add, and what is verified where.

## What the foundations provide

| Need | Provided by | Verified |
|:--|:--|:--|
| Contrast — 4.5:1 text, 3:1 boundaries and large brand text | The token pairs qeetrix-ui designs together | JVM: 142 pairs × 2 themes (`ContrastTest`) |
| Font scaling, including Android 14's non-linear scaling | sp sizes, em line heights | JVM, real text layout (`TypographyTest`) |
| Bold Text | Every weight bundled; the platform raises the weight | Device check needed |
| Script fallback (Devanagari, Tamil, …) | Platform per-glyph fallback | Device check needed |
| Touch targets of 48 dp | `Modifier.qeetrixMinimumTouchTarget()` | JVM (`MinimumTouchTargetTest`) |
| Decorative vs meaningful icons | `QeetrixIcon(contentDescription = null / "…")` | JVM semantics (`IconTest`) |
| Right-to-left | Directional icons mirror (`autoMirror`) | JVM (`IconTest`); visual check in the sample |
| Reduced motion | `qeetrixAnimationSpec`, from Settings › Remove animations | JVM (`MotionTest`) |
| Focus indicator | `QeetrixFocus`, `colors.focusRing`; every Phase 2 and Phase 3 control draws it for keyboard focus | Values: JVM; drawing: device check needed |

## What every component must add

- **Semantics.** A role (`Role.Button`, `Role.Checkbox`, …), a state description where the state is not
  in the label, and merged semantics so TalkBack reads one element per control. Translate the web's ARIA
  intent into Compose semantics; do not copy ARIA attributes.
- **Touch target.** At least 48 × 48 dp, even when the drawn control uses a compact density height.
- **Focus.** Keyboard and D-pad focus order matches visual order; focused controls draw the focus ring
  (`focus.ring-width`, `focus.offset`, `colors.focusRing`).
- **Text.** Reflow at large font scales instead of truncating; never fix a text container's height.
- **Motion.** Every animation goes through `qeetrixAnimationSpec` (or `QeetrixMotion.spec`).
- **Colour.** Never the only signal: pair status colours with an icon or text.

## What the Phase 2 components provide

Verified on the JVM through Compose semantics (`qeetrix/src/test/.../components`); TalkBack's actual
speech is a device check.

| Component | Semantics | Also |
|:--|:--|:--|
| Button, Icon button | Button role; disabled not activatable; loading keeps focus, ignores taps, state "Loading"; a loading label becomes the name | Icon button requires a `contentDescription`; 48 dp target at every size |
| Text field | The label names the field; an error sets the error state; messages are live regions (assertive for errors, polite otherwise) | Read-only stays focusable; disabled does not; "required" is read with the label (since Phase 3) |
| Switch | Switch role with on/off state; the labelled row toggles as one control | Read-only form (`onCheckedChange = null`) exposes the state without the action |
| Card | Title is a heading; a clickable card is one button named by its content | `selected` is visual; the owner states selection (`Modifier.semantics { selected = … }`) |
| Avatar | One image named by the person ("Ada Lovelace, Online") | Initials keep their size at any font scale |
| Progress | Progress semantics with value and range; indeterminate without a value | Named by its label or `contentDescription` |
| Spinner | Indeterminate progress named "Loading" (`qx_loading`) or a label | Not announced when it appears |
| Text | `H1`–`H4` are headings | Text in sp; wraps |
| Badge, Separator | Badge text is read; icons are decorative; the separator is hidden | Status never colour alone |

## What the Phase 3 components provide

Every input is built on the shared field parts ([FORMS.md](FORMS.md)): the label is drawn inside the
control, so the control is named by it; "required" is read with the label (`qx_required`); an error sets
the control's error state; messages are live regions (assertive for errors). Verified on the JVM through
the merged semantics tree, the tree the accessibility delegate reads.

| Component | Semantics | Also |
|:--|:--|:--|
| Field, Field set | `qeetrixFieldControl()` names a custom control "Label, required" and carries the error; the visible label is then hidden so it is read once | The legend is a heading; the set is one traversal group; a set's message is carried by each checkbox and radio in it; a disabled set disables its controls |
| Textarea | One editable multi-line field named by its label | Grows to 384 dp, then scrolls inside itself |
| Password input | One password field (Compose's secure field: password semantics, copy and cut unavailable) | Reveal button named "Show password" / "Hide password", 24 dp drawn, 48 dp hit area; no node's text or description contains the password (`QeetrixPasswordInputTest` scans them) |
| OTP input | One field named by its label, or "One-time code" (`qx_one_time_code`); the cells are hidden | AutoFill type SMS one-time code; never marked a password |
| Checkbox | Checkbox role with its state, "Partially checked" for mixed; the labelled row is one control | 48 dp rows, flush in a field set; bare box reserves 48 dp |
| Radio group | A selectable group; each option a radio button with "selected" / "not selected" | The label is a heading; the group's error is carried by every option; 48 dp rows |
| Select | Trigger: drop-down list role named by the label, text the selection | The list is a pane with a title; options are selectable 48 dp rows; opened from a keyboard it focuses the selection; Escape and Back close it |
| Number field | One editable field with Decrease and Increase custom actions | Arrow keys step; the steppers are buttons named "Decrease" / "Increase" but not focus stops |
| Form | The error summary is an assertive live region with a heading title, focusable for a programmatic move | Each error is a 48 dp button that runs its action (focus the field) |

## What only a device can verify

Unit tests do not prove these; each needs a manual or instrumented check on a device before a component
is marked stable:

- TalkBack reading order, labels and actions; Switch Access and keyboard navigation. For the Phase 2
  components in particular: the button's "Loading" state, the text field's error and live-region
  announcements, the labelled switch row, a clickable card's name, and the avatar's description. For
  Phase 3:
  - "required" read once with each label, and a field set's legend read as a heading;
  - the password field read as a password, masked unless spoken passwords are on, and the reveal button's
    name changing;
  - the OTP field read as one code (not cell by cell), and the SMS code suggestion offered by AutoFill on
    a device that supports it;
  - the select list announced as a pane and its options' selection, closing back to the trigger;
  - the number field's Decrease and Increase actions in TalkBack's actions menu;
  - group errors heard on each checkbox and radio, and the error summary announced and focused after a
    failed submit.
- The focus ring under a hardware keyboard and D-pad.
- Bold Text and the largest font scales on real fonts.
- Display P3 colours on a wide-gamut screen.
- Glyph fallback for Indic scripts.
- High-contrast text (Settings › Accessibility); Qeetrix has no increased-contrast tokens yet, so
  nothing in the design system changes under it.

`qeetrix/src/androidTest` holds the device tests; run them with
`./gradlew :qeetrix:connectedDebugAndroidTest` or the CI's manual `instrumented` job.
