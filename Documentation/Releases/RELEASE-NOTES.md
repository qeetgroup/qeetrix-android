# Qeetrix for Android 0.1.0 — release-candidate notes (draft)

**Status: release candidate `0.1.0-rc.2`, not published.** These notes describe the candidate; the version, the
registry and the release need the owner's approval ([RELEASE-CHECKLIST.md](RELEASE-CHECKLIST.md)). The candidate is
built from `@qeetrix/tokens` 0.0.13, `@qeetrix/react` 0.4.0 and `@qeetrix/icons` 2.1.0, and its components follow
`@qeetrix/react`; it supersedes `0.1.0-rc.1`, which was built from `@qeetrix/ui` 2.1.6 and never published
([RELEASE-CANDIDATE-REPORT.md](RELEASE-CANDIDATE-REPORT.md)).

Qeetrix for Android is Qeet Group's design system for Jetpack Compose: the Qeetrix tokens, light and dark themes,
Qeet typography and icons, and components that look and behave like their ui.qeet.in counterparts, as Android apps
expect. It is the first version: everything below is new.

## Install

```kotlin
implementation("com.qeetgroup:qeetrix:0.1.0")
```

From the registry the owner approves ([MAVEN-DISTRIBUTION.md](MAVEN-DISTRIBUTION.md)). Wrap the app's content in
`QeetrixTheme { … }`.

| Requires | |
|:--|:--|
| Android | 10 (API 29) or newer, phones and tablets |
| An app's build | Compile SDK 37, Android Gradle Plugin 9.1 or newer (required by Compose 1.12), Kotlin 2.3 or newer |
| Brings | Compose runtime, UI, foundation, animation-core 1.12.1 (public API types); Material 3 1.4.0 (internally, for the date and time pickers, pull to refresh and the bottom sheet; no Material type is public); Kotlin standard library 2.4.21 |
| Keep rules | None |

## What is in it

- **Foundations** generated from `@qeetrix/tokens` 0.0.13: 109 semantic colours in light and dark, 18 type roles in
  Qeet Sans and Fira Code (bundled, with the font size following the system), spacing, sizes, radii, borders,
  shadows, motion with springs, breakpoints, focus and three densities. 41 `@qeetrix/icons` icons in two shapes,
  outline and filled.
- **Theme:** `QeetrixTheme` with light, dark and system modes, nestable.
- **Formatting and haptics:** `QeetrixFormat` (numbers, currency, percent, dates, times, relative time and durations, by
  the web's rules on Android's ICU) and haptic roles.
- **Actions:** button, icon button (its name in a tooltip), copy button, bottom action bar.
- **Forms:** text field, textarea, password input, one-time-code input and code, search field, number field (exact
  decimals, currency and percent, locale-aware), select, segmented control, checkbox and checkbox group, radio group,
  switch, slider and range slider, date, date-range and time fields, attachment picker and file rows, copy field,
  field and field set, form, form actions and an error summary; a full-screen form.
- **Navigation:** adaptive navigation scaffold (a bar below 600 dp of window width, a rail from 600 dp), navigation
  bar and rail, top app bar (count, status, tabs and a collapsing large title), tabs and tab panels, steps, page
  indicator.
- **Overlays and feedback:** alert; alert, confirmation and custom dialogs; bottom sheet; menu with checkbox and radio
  items; snackbar; permission primer; progress, progress ring, meter, spinner, skeleton.
- **Data display:** list item and section header for lazy lists, swipe actions, pull to refresh, description list,
  accordion and collapsible, chips and chip groups, timeline, badge, avatar, avatar item and avatar group, card, stat
  and stat group, check list, QR code and QR scanner, relative time, separator, text and heading, empty state and data
  state.

Not in this version, by decision: a navigation drawer, a popover, a toast, a data table and image presentation ([Components](../Components/README.md)). Not every ui.qeet.in component has an Android
counterpart.

## Quality

- **Accessibility, automated:** semantics tests for every component; the Accessibility Test Framework over every
  component on Android 16; 48 dp touch areas; no clipped text at 200 % or right to left; WCAG contrast for the colours
  components draw; stillness with animations removed. **Not yet:** a manual TalkBack, Switch Access or keyboard pass
  ([RELEASE-READINESS-GAPS.md](../Quality/RELEASE-READINESS-GAPS.md)).
- **Localization:** right-to-left layouts; labels take their direction from their text; locale-aware numbers,
  including Indian grouping and native digits. Built-in strings (85, and one plural) are English; apps override them to translate.
- **Verified** on an Android 16 phone emulator — every component added for the Android tickets tried one by one in
  light, dark, 200 % text and right to left — and in an independent app that consumes this artifact in debug and in
  an R8-optimised release ([CONSUMER-INTEGRATION.md](CONSUMER-INTEGRATION.md)). `0.1.0-rc.1` was also run on
  Android 10, a tablet and in landscape.
- **Performance:** no measurements are claimed. Every restartable composable is skippable; animations read their
  values in the draw or layout phase.

## Fixed during release preparation

- Qeet Sans's optical size kept the font scale of whichever text first loaded the font, for the whole process; the
  theme now sets it for each font scale.
- Found on the device pass, each with a test: the QR scanner's permission states and controls; swipe actions and the
  slider moving against the finger in right to left; the date pickers cutting off Saturday on a phone; steps breaking
  a word at 200 % text; the avatar group's count; the page indicator's tap room; tooltips at the window's edge; money
  losing its decimals in the number field; white system-bar icons on the full-screen form.
- In `0.1.0-rc.1`: the bottom sheet drew an empty panel the size of the sheet at the top of the screen (found by the
  independent consumer).
- Earlier, in Phase 6: the number field's value on a change of locale; left-to-right labels cut or misordered in
  right-to-left layouts; fields without a visible label had a 40 dp touch target. See the CHANGELOG.

## Known limitations

- Emulators only: no physical device, foldable or resizable window has been tested.
- Dialogs, sheets and menus take right to left from the system locale, not from an app's composition.
- The `IntRange` parameter of the number field is unstable to the Compose compiler (an equal range passed afresh
  re-runs that function).

## Compatibility

Pre-1.0: the API has a reviewed baseline and changes only on purpose, but may change in a minor release, with
migration steps in the CHANGELOG ([RELEASE-POLICY.md](RELEASE-POLICY.md)).

## Licences

Qeet Sans (Cal Sans) and Fira Code, from `@qeetrix/tokens`, under the SIL Open Font License 1.1, and the Qeetrix icons under MIT with Lucide's
ISC notice; the texts ship inside the library ([LICENSE-COMPLIANCE.md](LICENSE-COMPLIANCE.md)).
