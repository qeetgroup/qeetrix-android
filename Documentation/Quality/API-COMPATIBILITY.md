# Public API compatibility (Phases 6 and 7)

What qeetrix-android promises consumers, how that promise is checked, and what Phase 6 changed. The standing
rules are [PUBLIC_API.md](../Standards/PUBLIC_API.md) and [0013](../Decisions/0013-public-api-baseline.md).

## The surface

`Documentation/API/qeetrix.api`, the reviewed baseline, at the Phase 6 working tree:

| | Count |
|:--|--:|
| Packages | 40 |
| Public classes and interfaces (JVM view, including file facades) | 119 |
| Of which enums | 40 |
| Public top-level functions (composables and modifiers) | 74 |
| Baseline lines | 1,091 |

Every public declaration has explicit visibility and type (`explicitApi()`). There are **no public data
classes**, so no `copy`/`componentN` methods that would break binary compatibility as properties are added. The two
scopes (`QeetrixFieldScope`, `QeetrixMenuScope`) are `sealed` interfaces apps cannot implement. No public type is
experimental; the one opt-in in the library (`ExperimentalMaterial3Api`, for the bottom sheet) is internal to one
file and no Material type is public (`MaterialBoundaryTest`).

## How compatibility is checked

| Kind | Check | Where |
|:--|:--|:--|
| **Binary** (what compiled apps link against) | `apiCheck` compares the JVM signatures of the built classes with `qeetrix.api`, using the Binary Compatibility Validator 0.18.2's signature engine as a library (its plugin doesn't configure AGP 9's built-in Kotlin). Part of `./gradlew check` and CI. | `qeetrix/build.gradle.kts` |
| **Source** (what apps' code compiles against) | The sample uses only the public API, and four documentation-example tests compile every documented snippet as written. | `sample/`, `*DocumentationExamplesTest` |
| **Behavioural** (what the API does) | Component behaviour and semantics tests, screenshots and the integration flows; behaviour changes are listed under **Changed** in the CHANGELOG. | `qeetrix/src/test`, `CHANGELOG.md` |

Kotlin 2.x also has an experimental built-in ABI validation. Phase 6 keeps the existing BCV-based check rather than
run two validators on one baseline; the decision is in [0017](../Decisions/0017-quality-gates-and-verification.md).

## Compatibility rules for a composable

- **Adding a parameter is binary-breaking**, even with a default: the JVM signature of the function (and of its
  `$default` bridge) changes, so an app compiled against the old version fails to link. Add an overload, or accept
  the break deliberately with a CHANGELOG entry and a baseline update — never regenerate the baseline to silence an
  unexpected diff.
- **Renaming or reordering parameters is source-breaking** for named or positional callers.
- **Enum entries**: adding one is additive for the library but can break an app's exhaustive `when`; the CHANGELOG
  says so.
- **`@Immutable` option types** (`QeetrixOption`, `QeetrixTab`, `QeetrixNavigationItem`, `QeetrixDialogAction`,
  `QeetrixFieldMessage`) promise that their properties never change. Their generic `value` is the app's identity for
  an option: pass immutable values (enums, ids, strings) — a mutable value breaks the contract the annotation states.
- Nothing is published yet, so there are no consumers to migrate; these rules apply from the first release.

## Phase 6 changes to the public API

**None.** `apiCheck` passes against the Phase 5 baseline with no edits. The Phase 6 fixes are internal:

| Change | Kind | Effect on apps |
|:--|:--|:--|
| `QeetrixNumberField` rewrites text another locale wrote instead of reading it | Behavioural (fix) | A value no longer changes on a locale change or recreation |
| Fields without a visible label reserve a 48 dp touch height | Behavioural, visual | Unlabelled text, search, select, number and textarea fields are 16 dp taller in layout (8 dp above and below the drawn box); labelled fields are unchanged |
| Short labels take their paragraph direction from their text | Behavioural (fix), visual | In right-to-left layouts, left-to-right labels are no longer cut and end where English does ("Saving…", "2 days ago"); menu items and a timeline time below its title align to the layout's start. Left-to-right layouts are unchanged except for right-to-left text in them |
| The skeleton's shimmer reuses its gradient | Internal | None visible |

## Phase 7: the API for the first release

Reviewed for the first release candidate (`0.1.0-rc.1`), against the baseline above, which Phase 7 did not change
(`apiCheck` passes):

| Dimension | Finding |
|:--|:--|
| Binary (JVM) | 119 public types; `explicitApi()`; no data classes, no experimental or opt-in API, nothing internal-looking. Checked on every build |
| Source | Every public name and parameter compiled from an independent app through the Maven artifact (`Integration/Consumer`), and every documented example compiled by the sample's tests |
| Compose | One public composable parameter is unstable to the Compose compiler — `QeetrixNumberField(range: IntRange?)` — reasoned in [PERFORMANCE-REPORT.md](PERFORMANCE-REPORT.md) (PF2); all 126 restartable composables are skippable |
| Consumer dependencies | What an app must use, measured by building the consumer: compile SDK 37 and AGP 9.1 or newer (both required by Compose 1.12 itself), Kotlin 2.3 or newer (Qeetrix and its Kotlin standard library carry 2.4 metadata; Kotlin 2.2.21 fails to read it). [COMPATIBILITY-MATRIX.md](../Releases/COMPATIBILITY-MATRIX.md) |
| Behaviour | `QeetrixBottomSheet` no longer draws a shadow, and its top rule moved inside the sheet: it had drawn both as an empty panel at the top of the screen (Phase 7 P1). Its `modifier` is documented as for semantics and test tags |

Lowering the Kotlin floor to 2.2 would mean compiling with `languageVersion`/`apiVersion` 2.2 and an older standard
library: possible, but a toolchain change with no consumer asking for it, so not done.

Compatibility before 1.0 is governed by [RELEASE-POLICY.md](../Releases/RELEASE-POLICY.md): breaking changes in
minor releases, listed with migration steps.

## Accidental exposure

Reviewed for internal implementation reachable from the public API: none found. Generated token objects, field
parts, popup placement, the ruled stack and the timeline layout are `internal`; Compose's `ComposableSingletons`
classes are excluded from the dump because they are public on the JVM but not API (Phase 3).
