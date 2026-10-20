# Qeetrix for Android 0.1.0 — release-candidate notes (draft)

**Status: release candidate `0.1.0-rc.1`, not published.** These notes describe the candidate; the version, the
registry and the release need the owner's approval ([RELEASE-CHECKLIST.md](RELEASE-CHECKLIST.md)).

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
| Brings | Compose runtime, UI, foundation, animation-core 1.12.1 (public API types); Material 3 1.4.0 (for the bottom sheet only); Kotlin standard library 2.4.21 |
| Keep rules | None |

## What is in it

- **Foundations** generated from `@qeetrix/ui` 2.1.6: 84 semantic colours in light and dark, nine type roles in
  Qeet Sans and Fira Code (bundled, with the font size following the system), spacing, corners, sizes, strokes,
  elevations, motion with reduced-motion values, focus and density. 39 Qeetrix icons in two shapes and two styles.
- **Theme:** `QeetrixTheme` with light, dark and system modes, nestable.
- **Actions:** button, icon button.
- **Forms:** text field, textarea, password input, one-time-code input, search field, number field (exact
  decimals, locale-aware), select, checkbox, radio group, switch, field and field set, form actions and an error
  summary.
- **Navigation:** adaptive navigation scaffold (a bar below 600 dp of window width, a rail from 600 dp), navigation
  bar and rail, top app bar, tabs.
- **Overlays and feedback:** alert, confirmation and custom dialogs, bottom sheet, menu, snackbar; progress, spinner,
  skeleton.
- **Data display:** list item and section header for lazy lists, description list, accordion, chips and chip groups,
  timeline, badge, avatar, card, separator, text, empty state and data state.

Not in this version, by decision: a navigation drawer, a popover, a toast, a data table, image presentation and a
pull-to-refresh wrapper ([Components](../Components/README.md)). Not every ui.qeet.in component has an Android
counterpart.

## Quality

- **Accessibility, automated:** semantics tests for every component; the Accessibility Test Framework over every
  component on Android 16; 48 dp touch areas; no clipped text at 200 % or right to left; WCAG contrast for the colours
  components draw; stillness with animations removed. **Not yet:** a manual TalkBack, Switch Access or keyboard pass
  ([RELEASE-READINESS-GAPS.md](../Quality/RELEASE-READINESS-GAPS.md)).
- **Localization:** right-to-left layouts; labels take their direction from their text; locale-aware numbers,
  including Indian grouping and native digits. Built-in strings (21) are English; apps override them to translate.
- **Verified** on Android 10 and Android 16 emulators (phone, tablet, landscape) and in an independent app that
  consumes this artifact in debug and in an R8-optimised release ([CONSUMER-INTEGRATION.md](CONSUMER-INTEGRATION.md)).
- **Performance:** no measurements are claimed. Every restartable composable is skippable; animations read their
  values in the draw or layout phase.

## Fixed during release preparation

- The bottom sheet drew an empty panel the size of the sheet at the top of the screen (found by the independent
  consumer); it no longer draws a shadow.
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

Qeet Sans (Cal Sans) and Fira Code under the SIL Open Font License 1.1, and the Qeetrix icons under MIT with Lucide's
ISC notice; the texts ship inside the library ([LICENSE-COMPLIANCE.md](LICENSE-COMPLIANCE.md)).
