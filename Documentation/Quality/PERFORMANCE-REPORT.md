# Performance report (Phase 6)

What is known about the library's runtime cost, how it is known, and what is not known. **No timing was
measured and no figure here is a benchmark.** The emulators available are not representative hardware, and
Macrobenchmark refuses to run on them by default ([0017](../Decisions/0017-quality-gates-and-verification.md)).
Everything below is structural evidence: what the Compose compiler reports, what tests count, and what code review
of drawing and allocation shows.

## Compose compiler report

Generated for the release variant with the compiler's `reportsDestination` and `metricsDestination` (a local
init script, not checked in; the commands are below). On the Phase 6 working tree:

| | Result |
|:--|:--|
| Composable functions in the library | 210 |
| Restartable | 126, **every one skippable** — every `Qeetrix…` component among them |
| Not restartable, by nature | 64 read-only getters (theme and token values, `labelDirection`) and 20 functions that return a value (`remember…`, metrics, styles), which have nothing to restart |
| Unstable classes | Two, both internal and never a composable's parameter: `SnackbarTiming` (holds the platform's `AccessibilityManager` for the snackbar's accessible timeout) and `NumberText` (holds the locale's `DecimalFormatSymbols`, a mutable Java class). Each is created once and remembered |
| Unstable parameters of public composables | One: `QeetrixNumberField(range: IntRange?)` (finding PF2) |
| `@Stable` / `@Immutable` added in Phase 6 | None. The existing `@Immutable` option types hold only `val`s of stable types and an app-supplied value documented as immutable ([API-COMPATIBILITY.md](API-COMPATIBILITY.md)) |

Strong skipping is on (the Kotlin 2.x default), so an unstable parameter compares by identity, not never.

To reproduce, apply an init script that sets `composeCompiler { reportsDestination = …; metricsDestination = … }`
for `:qeetrix` and run `./gradlew -I <script> :qeetrix:compileReleaseKotlin --no-configuration-cache`; the report
is `qeetrix-composables.txt` and `qeetrix-classes.txt`.

## Recomposition

| Evidence | Test | Result |
|:--|:--|:--|
| Selecting one of twenty keyed rows | `QeetrixCollectionTest.selectingOneRowRecomposesOnlyThatRow` | Only that row's body runs again |
| A long keyed list keeps identity through scrolling | `QeetrixCollectionDeviceTest` (API 29, API 36) | 500 rows scrolled far and back; selection kept by id |

## Animation and drawing

Every animation was reviewed for per-frame work:

| Animation | Where the animated value is read | Notes |
|:--|:--|:--|
| Spinner | `graphicsLayer { rotationZ }`: no recomposition or layout per frame | Holds still with animations removed |
| Progress (fill and indeterminate sweep) | `drawBehind`: draw only | As spinner |
| Skeleton shimmer | `drawWithCache … onDrawBehind`: draw only, gradient cached | Phase 6 fix PF1 |
| Switch | Thumb position in `offset {}` (layout only); track and thumb colours through composition while they change | Colour changes are short and one switch at a time |
| Tabs | Indicator in `drawBehind`; label colour through composition while it changes | As switch |
| Accordion | Compose's size animation | Opens at once with animations removed |
| Bottom sheet | Material 3's sheet | Material's own |

## Memory and allocation

- No component keeps a reference to a `Context`, `Activity` or `View` beyond composition; the one platform
  observer (Remove animations) is unregistered on dispose ([RELIABILITY-REPORT.md](RELIABILITY-REPORT.md)).
- No caches, singletons with state, or image loading.
- Bundled fonts are loaded by the platform's font machinery, once per process.

## Findings

| # | Severity | Finding | Decision | Evidence |
|:--|:--|:--|:--|:--|
| PF1 | P3 | The skeleton's shimmer built a new gradient brush and colour list on every animation frame, for every skeleton on screen | Fixed: the gradient is built once per size and colour in `drawWithCache` and translated each frame. The drawing is the same; no speed is claimed | Code review; skeleton screenshots unchanged |
| PF2 | P3 | `QeetrixNumberField(range: IntRange?)`: `IntRange` comes from a module the Compose compiler does not analyse, so it is unstable and compared by identity. A caller writing `range = 1..10` passes a new object each time it recomposes, so the `Int` overload runs again | Not changed. That overload only converts its arguments and calls the `BigDecimal` overload, whose `ClosedRange<BigDecimal>` is already stable and compared by value. No cost could be shown without a benchmark. If one is measured, a stability configuration naming `kotlin.ranges.IntRange` (immutable, value equality) meets the contract | Compiler report |

## Not measured

Frame timing, jank, startup, Baseline Profiles and memory under load. Those need representative hardware and a
benchmark module, recorded in [RELEASE-READINESS-GAPS.md](RELEASE-READINESS-GAPS.md) (G5). Until then the library
makes no performance claim.
