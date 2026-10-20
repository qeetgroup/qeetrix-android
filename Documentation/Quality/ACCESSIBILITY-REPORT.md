# Accessibility report (Phases 6 and 7)

What was verified for accessibility, how, on what, and what still needs a person with a device. The standard is
[ACCESSIBILITY.md](../Standards/ACCESSIBILITY.md); the verification approach is
[0017](../Decisions/0017-quality-gates-and-verification.md).

**This is not a claim of WCAG or Android accessibility compliance.** It records automated evidence and its limits.
No manual TalkBack, Switch Access, Voice Access or keyboard pass was performed in Phase 6.

## Methods

| Method | Runs on | Scope | Proves it can fail |
|:--|:--|:--|:--|
| Semantics assertions (roles, names, states, actions, headings, live regions) | JVM (Robolectric, Android 16 runtime) | Every component's behaviour tests, Phases 2–5 | Each assertion is specific to the component |
| Accessibility Test Framework 4.1.1 via `enableAccessibilityChecks()` (`QeetrixAccessibilityDeviceTest`) | **API 36 emulator**; left out below API 36 (`@SdkSuppress`) | The quality catalog (36 specimens) and 4 localized specimens, light and dark | A 24 dp unlabelled button is caught on API 36; on API 29 it is not, so the tests run only from API 36 |
| Touch areas in layout (`LayoutQualityTest`) | JVM, xhdpi | Every actionable node in the catalogs | A 24 dp control is caught |
| Where taps land near small controls (`TouchLandingTest`) | JVM | The controls drawn under 48 dp inside another one | Each asserts the action ran |
| Text clipping (`LayoutQualityTest`, `QeetrixLayoutDeviceTest`) | JVM at 100 %, linear 200 %, Android's non-linear 200 %, LTR and RTL; device at its own scale, LTR and RTL | Every text node in the catalogs | Text cut by a box too small for it is caught |
| Contrast as drawn (`ComponentContrastTest`) | JVM | 45 pairs components draw × 2 themes, translucent fills composited | Thresholds per WCAG 1.4.3 / 1.4.11 |
| Contrast of qeetrix-ui's designed pairs (`ContrastTest`, Phase 1) | JVM | 142 pairs × 2 themes | — |
| Reduced motion (`ReducedMotionTest`) | JVM | Spinner, skeleton, indeterminate progress | With animations on, frames differ |

ATF's checks on API 36 include touch target size, text and image contrast measured from a screenshot of the
rendered view, missing labels, duplicate descriptions, clickable spans, redundant descriptions and traversal order.

## Results

- **ATF (API 36):** every specimen in light and dark passes after one fix (A1). Before the fix: one finding.
- **Touch areas:** every control reserves 48 dp in layout except five kinds drawn smaller inside or beside another
  control — the password reveal and search clear buttons, the number field's steppers, the chip's remove button and
  the contained tabs — each of which takes a tap 14–16 dp outside its drawing (`TouchLandingTest`), and which ATF
  accepted on the device.
- **Clipping:** none at any tested scale or direction after fix L2 (localization report). The navigation bar's
  labels end in an ellipsis at 200 % when they don't fit their cell, by design (0015); they are read in full.
- **Contrast:** all 45 component pairs meet their threshold in both themes. The tightest: muted text on a pressed
  selected row (4.67, light, needs 4.5), the switch thumb on its track (3.36, dark, needs 3.0), primary-button text
  (4.56). Disabled controls are exempt (WCAG 1.4.3) and were not measured.
- **Reduced motion:** with Remove animations on, nothing moves across 700 ms of virtual time.

## Findings

| # | Severity | Component | Finding | Fix | Regression test |
|:--|:--|:--|:--|:--|:--|
| A1 | P2 | Text field, search field, select, number field, textarea, password input **without a visible label** | ATF on API 36: the touch target was 40 dp tall (32 dp drawn, expanded only partly by Compose's hit testing). | A field with no label above it reserves a 48 dp touch height, the 32 dp field centred, as a button reserves its 48 dp. Labelled fields unchanged. | `QeetrixAccessibilityDeviceTest`, `LayoutQualityTest` |
| A2 | P2 (verification) | All | ATF does not run under Robolectric or on API 29: a pass there was meaningless. | Checks moved to a device test that proves, on a known problem, that they run. | The control in `QeetrixAccessibilityDeviceTest` |
| A3 | P2 (test defect) | Bottom sheet, dialog | `QeetrixOverlayBackTest` (Phase 4) had never run; its matcher was ambiguous. | Matcher fixed. It passes on API 29 and 36: Back closes the topmost overlay first, then the next. | — |
| A4 | P2 | Labels in right-to-left layouts | See localization finding L2. | | `RightToLeftLabelTest` |

## Component matrix

**S** = semantics tests on the JVM · **A** = ATF on API 36 · **T** = touch area in layout (or proven landing) ·
**C** = no clipping at 200 % and right to left · **M** = manual TalkBack pass (none done) · — = not applicable or
not covered.

| Component | S | A | T | C | M | Notes |
|:--|:-:|:-:|:-:|:-:|:-:|:--|
| Text, Separator | ✓ | ✓ | — | ✓ | — | Separator hidden from accessibility services |
| Card | ✓ | ✓ | ✓ | ✓ | — | |
| Badge | ✓ | ✓ | — | ✓ | — | |
| Button, Icon button | ✓ | ✓ | ✓ | ✓ | — | |
| Spinner, Progress, Skeleton | ✓ | ✓ | — | ✓ | — | Still with animations off |
| Avatar | ✓ | ✓ | — | ✓ | — | |
| Switch, Checkbox, Radio group | ✓ | ✓ | ✓ | ✓ | — | |
| Text field, Textarea, Field, Field set | ✓ | ✓ | ✓ (A1) | ✓ | — | |
| Password input | ✓ | ✓ | ✓ (landing) | ✓ | — | Password never in a semantics node |
| OTP input | ✓ | ✓ | ✓ | ✓ | — | |
| Select | ✓ | ✓ (trigger) | ✓ | ✓ | — | The open list is a popup window, not in the catalog |
| Number field | ✓ | ✓ | ✓ (landing) | ✓ | — | |
| Form actions, error summary | ✓ | ✓ | ✓ | ✓ | — | |
| Navigation bar, rail | ✓ | ✓ | ✓ | ✓ | — | Bar labels ellipsize by design |
| Navigation scaffold | ✓ | — | — | — | — | A whole-screen layout; not in the catalog |
| Top app bar | ✓ | ✓ | ✓ | ✓ | — | |
| Tabs | ✓ | ✓ | ✓ (landing) | ✓ | — | |
| Alert, confirmation dialog; dialog | ✓ | — | — | — | — | Dialog windows: JVM semantics and device Back only |
| Bottom sheet | ✓ | — | — | — | — | Device Back verified (A3) |
| Menu | ✓ | — | — | — | — | Popup window; device Back verified |
| Snackbar | ✓ | ✓ | ✓ | ✓ | — | |
| List item, section header | ✓ | ✓ | ✓ | ✓ | — | |
| Description list, Accordion, Timeline | ✓ | ✓ | ✓ | ✓ | — | |
| Empty state, Data state | ✓ | ✓ | ✓ | ✓ | — | |
| Chip, chip group | ✓ | ✓ | ✓ (landing) | ✓ | — | |
| Search field | ✓ | ✓ | ✓ (A1) | ✓ | — | |
| Icon | ✓ | ✓ | — | — | — | |

## Phase 7 additions

- **The independent consumer** ran on API 29, API 36 and an API 36 tablet, in portrait and landscape, through the
  Maven artifact: its Compose tests find every control by its accessible name and role (switches as on and off,
  icons by description), and its release build is driven by UI Automator through the accessibility tree.
- **ATF over the library's catalog** passed again on API 36 in the release-candidate run.
- **A TalkBack session was attempted** on the API 36 emulator (TalkBack 16.0) with the consumer app. TalkBack ran,
  was bound with touch exploration on, spoke (text-to-speech requests in the log) and placed its first focus on the
  page heading. Driving it further from automation was not possible on this setup: `adb` touch injection bypasses
  touch exploration (a "swipe" toggled a switch instead of moving focus), the emulator's key events did not reach
  TalkBack's keyboard shortcuts, and `uiautomator`'s event recorder suppresses TalkBack while it runs. **This is not a
  manual TalkBack pass.** It shows only that TalkBack starts and focuses a heading first.
- **No new accessibility defect was found in Phase 7.** The one P1 defect Phase 7 found (the bottom sheet's empty
  panel) was drawing only — it added nothing to the accessibility tree, so the sheet's names, focus and
  announcements were unaffected, though it hid the dimmed screen behind the top of the sheet.

## What only a person can verify

Recorded, not done — each is required before a release is called accessible:

- TalkBack on a device: reading order and wording of every component, announcements (snackbars, field errors,
  data-state failures), dialog and sheet titles, focus returning after an overlay closes, the custom and expand /
  collapse actions, rows' long-press labels.
- Switch Access and Voice Access over the sample, including the small-by-design controls.
- A hardware keyboard and D-pad: traversal order and the focus ring in every component.
- Bold Text, the largest system font scale and high-contrast text on a physical device.
- ATF on API levels other than 36, and on overlay windows (dialogs, sheet, menu, select list).
