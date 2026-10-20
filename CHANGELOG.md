# Changelog

All notable changes to Qeetrix for Android. The library has no released version yet.

## Unreleased — Phase 7 release preparation (candidate 0.1.0-rc.1)

No public API changed (`apiCheck` passes against the Phase 6 baseline).

### Added

- **Maven publication** (`maven-publish`): `com.qeetgroup:qeetrix`, one `release` variant with a sources JAR, the
  POM and Gradle module metadata naming the locked dependency versions. The version comes from
  `-Pqeetrix.version` (default `0.1.0-SNAPSHOT`). The only repository is `build/release-candidate/maven`: nothing can
  be uploaded from this build ([0018](Documentation/Decisions/0018-maven-distribution.md)).
- **Independent consumer** (`Integration/Consumer`, a separate Gradle build): an app that resolves Qeetrix by its
  coordinates, with Compose tests on its debug build and a UI Automator smoke test on its R8-optimised release.
- **Release scripts:** `Scripts/release-candidate` (every gate, local publication, the consumer, evidence and
  checksums; fails on the first failed gate), `Scripts/verify-publication`, `Scripts/dependency-inventory`.
  `Scripts/verify-aar` also checks the AAR's SDK metadata and that `classes.jar` holds only Qeetrix's classes.
- **CI:** every build publishes to a local directory, checks the publication and builds the consumer; the
  instrumented job runs the consumer's device tests; a manual release-candidate workflow keeps a candidate and its
  evidence as an artifact, with no credentials or upload step.
- **Device test:** `QeetrixBottomSheetDeviceTest`.
- **Documentation:** `Documentation/Releases/` (policy, checklist, Maven distribution, consumer integration, AAR
  verification, compatibility matrix, licence compliance, release notes, rollback, the candidate report), decision
  0018, and the Phase 7 handoff for ui.qeet.in.

### Fixed

- **Bottom sheet:** an empty panel the size of the sheet was drawn at the top of the screen on devices. Its shadow
  and top rule were passed to Material's sheet as a modifier, which Material applies before moving the sheet into
  place. The rule is now drawn inside the sheet, and no shadow is drawn. Found by the independent consumer.

### Changed

- **Bottom sheet:** no shadow (it never rendered where the sheet is); its `modifier` is documented as for semantics
  and test tags.

## Unreleased — Phase 6 quality, accessibility and performance hardening

No public API, token or default changed. Evidence for every entry is in `Documentation/Quality/`.

### Fixed

- **Number field:** after a change of locale, or an activity recreated for one, the field read the text another
  locale wrote ("1,234" read as German is 1.234) and reported a different value. It now rewrites that text in
  the current locale (`NumberText.reads`).
- **Right-to-left labels:** a left-to-right label in a right-to-left layout was laid out as a right-to-left
  paragraph. Some labels were measured short (a full-width button's label ellipsized, chips lost characters, a
  tab wrapped), and trailing punctuation and numbers moved to the wrong end ("…Saving", "days ago 2"). Labels
  of buttons, chips, tabs, badges, navigation items, menus, fields, list rows' metadata and timeline times now
  take their direction from their text; menu items and a timeline time below its title also align to the
  layout's start.
- **Fields without a visible label** (text field, textarea, search, select, number, password) reserve a 48 dp
  touch height around the 32 dp field; the Accessibility Test Framework measured 40 dp on a device.
- **Skeleton:** the shimmer built a new gradient on every frame; it is built once per size and moved.
- **Tests:** `QeetrixOverlayBackTest` (Phase 4) had an ambiguous matcher and had never run, and sent Back before a
  window had input focus (flaky); a test typed a real mail domain. The accessibility device tests run from API 36
  (`@SdkSuppress`): a JUnit assumption skip was counted as a failure by Android's device reports.

### Added

- **Quality catalog** (`qeetrix/src/sharedTest`): every in-place component in its risky states, plus Hindi,
  Telugu, Arabic and pseudo-expanded specimens, shared by the JVM and device tests.
- **JVM quality tests:** `LayoutQualityTest` (48 dp touch areas; no clipping at 100 %, linear and non-linear
  200 %, left to right and right to left), `TouchLandingTest`, `ComponentContrastTest` (45 drawn colour pairs
  per theme), `ReducedMotionTest`, `RightToLeftLabelTest`, `QeetrixLocalizationScreenshotTest`; regression tests
  for the number field.
- **Device tests**, run for the first time (API 29 and API 36 emulators): `QeetrixAccessibilityDeviceTest`
  (ATF over the catalog, with a control proving the checks run), `QeetrixLocaleDeviceTest` (ICU number formats:
  Indian grouping, Devanagari and Arabic-Indic digits), `QeetrixLayoutDeviceTest` (clipping and a right-to-left
  capture for review).
- **Dependency (tests only):** `androidx.compose.ui:ui-test-junit4-accessibility` from the Compose BOM.
- **Sample:** a minified release build (R8 and resource shrinking, debug-signed, never distributed) and Android's
  pseudolocales in the debug build.
- **CI:** device tests on every push and pull request (API 29 and 36); `:sample:assembleRelease`; third-party
  actions pinned to commit SHAs.
- **Documentation:** decision record 0017, ten reports in `Documentation/Quality/`, and the Phase 6 handoff for
  ui.qeet.in.

## Unreleased — Phase 5 data display, collections and content states

### Added

- **List** (`components.list`, Android and iOS only): `QeetrixListItem` (title, description, metadata,
  leading and trailing slots, click and long click, selection) with `QeetrixListItemIcon`, and
  `QeetrixSectionHeader` for sections of Compose's lazy lists.
- **Description list** (`components.descriptionlist`): `QeetrixDescriptionList`, `QeetrixDescriptionItem`
  and `QeetrixDescriptionListLayout` (`Horizontal`, `Vertical`, `Grid`), divided or not.
- **Accordion** (`components.accordion`): `QeetrixAccordion` and `QeetrixAccordionItem`, expansion owned by
  the app.
- **Empty and data states** (`components.emptystate`): `QeetrixEmptyState` with `QeetrixEmptyStateVariant`
  and `QeetrixEmptyStateSize`; `QeetrixDataState` with `QeetrixDataFailure` and `QeetrixDataLoadingRows`.
- **Skeleton** (`components.spinner`): `QeetrixSkeleton`, `QeetrixSkeletonShape`, `QeetrixSkeletonText`.
- **Chips** (`components.badge`): `QeetrixChip` (toggle or removable), `QeetrixChipGroup` (one value or a
  set), `QeetrixChipSize`.
- **Timeline** (`components.feed`): `QeetrixTimeline`, `QeetrixTimelineItem`, `QeetrixTimelineTone`,
  `QeetrixTimelineEmphasis`.
- **Search field** (`components.input`): `QeetrixSearchField`.
- **Component tokens:** `accordion`, `skeleton` and `timeline` (305 tokens generated). The generator reads
  millisecond component tokens (`component.accordion.duration`) as durations.
- **Icons:** calendar, clock, file-text, folder, mail, refresh-cw, search-x and users (39 in all).
- **Strings:** `qx_expanded`, `qx_collapsed`, `qx_remove`, `qx_data_error`, `qx_try_again`, `qx_search`,
  `qx_search_placeholder`, `qx_clear`.
- **Tests:** behaviour, semantics and screenshots for every new component; collection identity (keyed rows
  through insertion, removal and reordering, selection by id, state restoration, one-row recomposition); the
  sample's `CollectionsFlowTest` and Phase 5 documentation examples; the instrumented
  `QeetrixCollectionDeviceTest` (compiled; not yet run on a device).
- **Sample:** pages for every new component and a collections screen (members: search, role chips, sticky
  team sections, selection with removal and Undo, every content state, a detail with a description list, an
  accordion and a timeline).
- **Documentation:** component pages, `Documentation/Standards/DATA_DISPLAY_AND_COLLECTIONS.md`, decision
  record 0016, and the Phase 5 handoff for ui.qeet.in (`Documentation/Website/PHASE_5_HANDOFF.md`).

### Changed

- **Sample:** the home screen's theme and density choices are a `QeetrixChipGroup` instead of hand-made
  chips; the workspace's empty project list is a `QeetrixEmptyState`.
- **Documentation:** the README's runtime dependencies name Material 3 (for the bottom sheet, since Phase 4)
  and its icon count is current.

## Unreleased — Phase 4 navigation, overlays and feedback

### Added

- **Navigation** (`components.navigation`): `QeetrixNavigationBar`, `QeetrixNavigationRail` and
  `QeetrixNavigationItem<T>`; `QeetrixNavigationScaffold` with `QeetrixNavigationLayout` (bar below 600 dp of
  window width, rail from 600 dp); `QeetrixTopAppBar` and `QeetrixBackButton`. Selection is the app's.
- **Tabs** (`components.tabs`): `QeetrixTabs`, `QeetrixTab<T>`, `QeetrixTabsVariant` (`Default`, `Line`).
- **Dialogs** (`components.dialog`): `QeetrixAlertDialog` with `QeetrixDialogAction` and
  `QeetrixDialogActionRole`, `QeetrixConfirmationDialog`, `QeetrixDialog` with `QeetrixDialogSize`.
- **Bottom sheet** (`components.drawer`): `QeetrixBottomSheet`, presented by `visible`.
- **Menu** (`components.dropdownmenu`): `QeetrixMenu`, `QeetrixMenuItem`, `QeetrixMenuLabel`,
  `QeetrixMenuSeparator`, `QeetrixMenuItemVariant`.
- **Snackbar** (`components.toast`): `QeetrixSnackbar`, `QeetrixSnackbarHost`, `QeetrixSnackbarHostState`,
  `rememberQeetrixSnackbarHostState`, `QeetrixSnackbarType`, `QeetrixSnackbarResult`.
- **Component tokens:** `dialog`, `menu` and `sidebar` (297 tokens generated).
- **Icons:** arrow-left, circle-x, copy, ellipsis, ellipsis-vertical, house, pencil, settings, share-2 and
  trash (31 in all).
- **Strings:** `qx_close`, `qx_cancel`, `qx_back`, `qx_drag_handle`.
- **Tests:** behaviour, semantics and screenshots for every new component, the snackbar lifecycle under
  virtual time, `MaterialBoundaryTest`, the sample's `WorkspaceFlowTest`, and the instrumented
  `QeetrixOverlayBackTest` (compiled; not yet run on a device).
- **Sample:** Navigation, Overlays and Feedback pages and a workspace screen that combines them; every page
  now has a `QeetrixTopAppBar`.
- **Documentation:** component pages, `Documentation/Standards/NAVIGATION_AND_OVERLAYS.md`, decision record
  0015, and the Phase 4 handoff for ui.qeet.in (`Documentation/Website/PHASE_4_HANDOFF.md`).

### Changed

- **Dependencies.** Compose foundation is now an `api` dependency: its types (`TextFieldState`,
  `KeyboardOptions`, `MutableInteractionSource`, `WindowInsets`, layout scopes) are in the public API, so an
  app no longer has to add it itself. Material 3 is a new `implementation` dependency, for the bottom sheet
  only ([0015](Documentation/Decisions/0015-navigation-overlays-and-feedback.md)); it brings Activity
  Compose 1.8.2 (Activity 1.7.0 → 1.8.2 in the lockfile).
- **Select:** a highlighted option now takes `surface.interactive` (the web's `bg-accent`), one step lighter
  than before; Escape closes the list once instead of acting on both key-down and key-up. The list's
  placement and motion are shared with the menu.
- `QeetrixFormActions`' layout is shared with the dialogs' footers (no visible change).

## Unreleased — Phase 3 advanced inputs and forms

### Added

- **Field** (`components.input`): `QeetrixField` with `QeetrixFieldScope` and
  `Modifier.qeetrixFieldControl()` for any control; `QeetrixFieldSet` with `QeetrixFieldSetLegend`;
  `QeetrixOption<T>` for typed choices. Every input is built on the same internal field parts
  ([0014](Documentation/Decisions/0014-form-components.md)).
- **Inputs:** `QeetrixTextarea`; `QeetrixPasswordInput` with `QeetrixPasswordPurpose`; `QeetrixOTPInput`
  with `QeetrixOTPCharacters`; `QeetrixCheckbox` and `QeetrixTriStateCheckbox`; `QeetrixRadioGroup`;
  `QeetrixSelect` with `QeetrixSelectSize`; `QeetrixNumberField` for `Int?` and `BigDecimal?`.
- **Form patterns** (`components.form`): `QeetrixFormActions`, `QeetrixFormErrorSummary` and
  `QeetrixFormError`.
- **`contentType`** on `QeetrixTextField` and `QeetrixTextarea`, for AutoFill types the keyboard type
  does not imply (`Username`, `PersonFullName`, `PostalAddress`, …).
- **Icons:** chevron-down, eye-off, minus and plus (21 in all).
- **Strings:** `qx_required`, `qx_show_password`, `qx_hide_password`, `qx_one_time_code`, `qx_decrease`,
  `qx_increase` and `qx_error_summary_title`.
- **Sample:** a page per new component under Forms & inputs, and an account-preferences screen; sample
  tests for the error summary's focus and a two-step sign-in with a code.
- **Documentation:** component pages, `Documentation/Standards/FORMS.md`, decision record 0014, Phase 3
  rows in the accessibility standard and the cross-platform contract, and a website handoff
  (`Documentation/Website/PHASE_3_HANDOFF.md`). The documented examples are compiled and composed by
  the sample's `DocumentationExamplesTest`.

### Changed

- **`QeetrixTextField`** is rebuilt on the shared field parts. Its signature gains only the defaulted
  `contentType` parameter (source-compatible; the JVM signature in `qeetrix.api` changes). Behaviour
  changes: "required" is now read with the label; the field is exactly the control height (it was 1–2 dp
  taller); inside a disabled `QeetrixFieldSet` it is disabled. `QeetrixFieldMessage` and
  `QeetrixFieldMessageTone` moved to `QeetrixField.kt` in the same package (no API change).
- **`QeetrixSwitch`** follows a disabled `QeetrixFieldSet`, and its labelled row sits flush in one.
- **API dump** (`apiDump` / `apiCheck`) leaves out the Compose compiler's `ComposableSingletons` classes,
  which are public on the JVM but not API.
- **Generator:** upstream downloads retry a connection timeout up to three times and try a host's
  reachable addresses first, so one unreachable CDN address no longer fails a sync. The
  `new-component` tests scaffold Slider, now that Checkbox exists.

## Unreleased — Phase 2 core components

### Added

- **Components** (`com.qeetgroup.qeetrix.components.*`): `QeetrixText`, `QeetrixSeparator`, `QeetrixCard`,
  `QeetrixBadge`, `QeetrixButton`, `QeetrixIconButton`, `QeetrixSpinner`, `QeetrixProgress`, `QeetrixAvatar`,
  `QeetrixSwitch` and `QeetrixTextField` with `QeetrixFieldMessage`, each with option enums carrying the
  web's names ([0010](Documentation/Decisions/0010-component-api-conventions.md)).
- **Component tokens** for avatar, badge, button, card, input, progress, switch and typography,
  generated as internal objects (279 tokens generated, up from 217)
  ([0011](Documentation/Decisions/0011-component-tokens-and-web-metrics.md)).
- **Theme:** `QeetrixTheme.density` and `LocalQeetrixDensity`.
- **Icons:** circle-alert, circle-check, loader-circle, triangle-alert and user-round (17 in all).
- **Strings:** `qx_loading` ("Loading") and `qx_optional` ("(optional)"), translatable by overriding them.
- **Screenshot tests** with Roborazzi; baselines are local and never committed
  ([0012](Documentation/Decisions/0012-visual-regression-testing.md)).
- **Public API baseline** `Documentation/API/qeetrix.api` with `apiDump` / `apiCheck`
  ([0013](Documentation/Decisions/0013-public-api-baseline.md)).
- **`Scripts/new-component`** and `Templates/Component/`; `component-manifest.json` pinned from
  `@qeetrix/ui` 2.1.6.
- **Sample:** a catalogue grouped as on ui.qeet.in, a page per component with previews, a composition
  screen, app-wide theme, density, right-to-left and text-size settings, and JVM tests.
- **Documentation:** `Documentation/Components/`, decision records 0010–0013, and updated standards and
  cross-platform contract.

### Changed

- The sample's foundations screen moved to `sample/…/foundations/` and no longer carries the theme
  picker (the home screen's Display card does).
- `Scripts/verify-aar` expects the library's `qx_` strings.

## Unreleased — Phase 1 foundations

### Added

- **Library module `:qeetrix`** (`com.qeetgroup:qeetrix`, namespace `com.qeetgroup.qeetrix`): Kotlin,
  Jetpack Compose, minSdk 29, compileSdk 37; an AAR that ships two fonts, three licence texts and no
  manifest entries.
- **Token pipeline** in `Tools/QeetrixGen`: syncs `@qeetrix/ui` 2.1.6, the native fonts and 12 icons
  from pinned upstream releases, verifies them, and generates Kotlin deterministically. `./gradlew check`
  fails on any drift.
- **Theme.** `QeetrixTheme(mode)` with `Light`, `Dark` and `System`; nested themes;
  `QeetrixTheme.colors`, `.typography`, `.isDark`, `.reduceMotion`, `.shadowLayers()`;
  `LocalQeetrixContentColor`.
- **Colours.** `QeetrixColors`: the 84 semantic colours of `color.*`, light and dark, converted per CSS
  Color 4 (Display P3 for the seven tokens outside sRGB); `QeetrixOpacity`.
- **Typography.** `QeetrixTextRole` and `QeetrixTypography` (nine roles), `QeetrixFontWeight`,
  `QeetrixFontGeometry`; Qeet Sans (Cal Sans v2.011) and Fira Code 6.2 as variable font resources with
  per-role `wght`, `GEOM` and `opsz`.
- **Foundations.** `QeetrixSpacing`, `QeetrixCorner`, `QeetrixShapes`, `QeetrixStroke`, `QeetrixIconSize`,
  `QeetrixIconStroke`, `QeetrixDensity`, `QeetrixFocus`, `QeetrixElevation` with
  `Modifier.qeetrixElevation`, `QeetrixDuration`, `QeetrixEasing`, `QeetrixMotion` and
  `qeetrixAnimationSpec` (honours Remove animations).
- **Accessibility.** `QeetrixAccessibility.MinimumTouchTarget` (48 dp), `Modifier.qeetrixMinimumTouchTarget()`,
  `QeetrixAccessibility.contrastRatio`.
- **Icons.** `QeetrixIcon` and `QeetrixIconName` (12 icons), `QeetrixIconShape`, `QeetrixIconVariant`:
  vector paths in the content colour, stroke token at run time, RTL mirroring, decorative or labelled.
- **Provenance.** `QeetrixProvenance`: the upstream versions, commits and font hashes this build came from.
- **Sample app `:sample`** demonstrating the foundations through the public API.
- **Tests:** 33 generator tests and 37 JVM library tests (Robolectric, Compose UI Test); 2 instrumented
  tests (compiled, not yet run on a device).
- **Engineering:** Android Lint and ktlint, Dokka, dependency locking, CI workflow, governance files,
  decision records, the cross-platform contract and the accessibility, component and public-API standards.
