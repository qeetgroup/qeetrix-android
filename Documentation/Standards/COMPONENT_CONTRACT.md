# Component contract

What every Qeetrix Android component must provide, so the first component and the hundredth have the
same shape. The existing components follow it ([Components](../Components/README.md)); new ones start
from `Scripts/new-component` ([Templates/Component](../../Templates/Component/README.md)). Inputs also
follow [FORMS.md](FORMS.md): they are built on the shared field parts and take the same field
parameters. Navigation, overlays and feedback follow [NAVIGATION_AND_OVERLAYS.md](NAVIGATION_AND_OVERLAYS.md);
data display, collections and content states follow
[DATA_DISPLAY_AND_COLLECTIONS.md](DATA_DISPLAY_AND_COLLECTIONS.md).

## The catalogue it follows

`@qeetrix/react` 0.4.0, the version this library is pinned to, ships its component manifest as
`dist/manifest.json`; `Scripts/sync-tokens` copies it to `Upstream/qeetrix-react/component-manifest.json`. It lists
**89 families** — 87 components and two utilities (`format`, `provider`), 39 stable and 50 experimental (0.4.0 adds
`chip`) — with 172 parts. Each entry has a `name` (`button`), a `status`, its import `entry` (`@qeetrix/react/button`, or
`@qeetrix/react/experimental`), a `kind`, its `parts` and the version it arrived in (`since`). For example, the
`button` family holds Button, IconButton and LinkButton.

An Android component is the counterpart of one or more parts: its look comes from that family's CSS (copied to
`Upstream/qeetrix-react/css/` for the families Android implements) and from the design rules (qeetrix-files
`qeetrix-react/05-design-rules.md`), its API from the part's props. A component the web does not have (list rows,
the navigation bar) says so on its page and is drawn from the nearest web rules.

ui.qeet.in groups the same pages into navigation categories. Those are used only for documentation and the
sample's navigation, not for packages.

Re-check these counts whenever the `@qeetrix/react` pin moves.

## Where a component lives

```text
qeetrix/src/main/kotlin/com/qeetgroup/qeetrix/components/<package>/Qeetrix<Component>.kt
qeetrix/src/test/kotlin/com/qeetgroup/qeetrix/components/<package>/Qeetrix<Component>Test.kt            behaviour and semantics
qeetrix/src/test/kotlin/com/qeetgroup/qeetrix/components/<package>/Qeetrix<Component>ScreenshotTest.kt  Roborazzi
qeetrix/src/androidTest/kotlin/com/qeetgroup/qeetrix/components/<package>/                               device-only checks
qeetrix/src/sharedTest/kotlin/com/qeetgroup/qeetrix/quality/QualityCatalog.kt                         a specimen for the quality scans
sample/src/main/kotlin/com/qeetgroup/qeetrix/sample/components/<group>/<Component>Sample.kt
Documentation/Components/<Component>.md
```

`<package>` is the `@qeetrix/react` family's name in lower case without hyphens (`button`, `date-picker` →
`datepicker`), with `control` appended to a Java keyword (`switch` → `switchcontrol`); IconButton lives in
`components/button/` beside Button. The packages that predate `@qeetrix/react` keep their names so the API does
not move: `dropdownmenu` (Menu), `feed` (Timeline), `typography` (Text and Heading), `radiogroup` (RadioGroup),
`input` (the text inputs and the field), `spinner` (Spinner and Skeleton), `badge` (Badge and Chip). App chrome the
web has no family for — the navigation bar, rail, top app bar and scaffold — is one Android family,
`components/navigation/` ([0015](../Decisions/0015-navigation-overlays-and-feedback.md)); so are list rows and
section headers, `components/list/` ([0016](../Decisions/0016-data-display-and-content-states.md)). One file per
component; internals a family shares (the button frame) live in that package. `<group>` is the sample's group
(`actions`, `forms`, `datadisplay`, …). Packages are created with their first component, never as placeholders, and
there is no Gradle module per component or category ([0001](../Decisions/0001-single-library-module.md),
[0010](../Decisions/0010-component-api-conventions.md)).

## What every component defines

1. **Public Compose API.**
   - A composable named `Qeetrix<Component>` (`QeetrixButton`), with Compose's parameter order:
     required parameters, `modifier: Modifier = Modifier`, optional parameters, then the trailing
     content slot.
   - Typed parameters — enums and sealed types, not strings — with the `@qeetrix/react` names: a web
     `variant="soft"` becomes `QeetrixButtonVariant.Soft` with `value = "soft"`. The meaning matches the web
     component; the syntax is native.
   - State hoisted to the caller with the web's names — `value` / `onValueChange`, `checked` / `onCheckedChange`,
     `open` / `onOpenChange` — and `interactionSource` where the component reacts to press, hover or focus. The
     web's `disabled` is Compose's `enabled`. No React-style prop bags.
   - Defaults in a `<Component>Defaults` object only when callers need them. No separate tokens or style class
     per component.
2. **Variants and states.** The web component's variants, tones and sizes, by the same names, drawn as its CSS
   draws them. States — enabled, pressed, focused, hovered, selected, invalid, loading — come from Compose
   (`enabled`, `InteractionSource`), never tracked twice.
3. **Customisation** limited to what the design system allows: variant, tone, size, density. No colour or font
   parameters; Qeetrix has one brand. Feedback is the web's colour steps, not a ripple (`indication = null`).
4. **Tokens and metrics.**
   - Semantic tokens by default, through `QeetrixTheme` and the foundation objects (`QeetrixSpace`, `QeetrixSize`,
     `QeetrixRadius`, `QeetrixBorder`, `QeetrixFocus`, `QeetrixMotion`, the density).
   - Component tokens only where `@qeetrix/tokens` defines a `component.<group>.*` group and
     `Upstream/manifest.json` adopts it (`qeetrixTokens.componentTokens`), generated as an internal
     `Qeetrix<Group>ComponentTokens` ([0011](../Decisions/0011-component-tokens-and-web-metrics.md)). 0.0.13 defines
     four groups; `sidebar` and `timeline` are adopted, as on qeetrix-ios. The timeline draws its marker from
     `component.timeline.marker-size` (28px since 0.0.13); no component reads `sidebar` yet.
   - Every other metric is the family's CSS, restated in the component file with the CSS beside each literal
     (`// button.css: gap 12px; padding-inline 14px`), so a pin bump's CSS diff points at the line to change.
     Where the CSS names a token, use the token, never its value.
   - No literal colours or durations in component code, and no value that has no source.
5. **Design rules.** Square shapes (§3); Ember only where it means something (§4); the field look (§8: filled, a
   bottom line, the label above, an error replacing the help text); the focus frame where §10 places it (inside
   buttons with a 1 dp surface gap, inside fields, 2 dp outside checkboxes, radios, switches and tags, inside
   rows, and inside a scrolling tab list, as its CSS draws it); the Ember bar for the current item and the Ember
   tint for a selection (§11); shadows only on floating layers (§12); sharp icons on controls, round in
   navigation, filled when selected (§16); productive motion (§18).
6. **Accessibility** ([ACCESSIBILITY.md](ACCESSIBILITY.md)): a 48 dp touch target, correct semantics
   (role, state, label), font scaling with reflow, RTL, reduced motion, and the contrast pairs.
7. **Native adaptations**, where Android behaviour differs from the web (a bottom sheet for the Drawer, a list row
   for a table row), written down in the component's KDoc and in its page's "Platform differences", and in
   [CROSS_PLATFORM_CONTRACT.md](../CROSS_PLATFORM_CONTRACT.md) when it also differs from iOS.
8. **Tests.** Behaviour and semantics with Compose UI Test on the JVM — what a user and TalkBack
   observe, not layout internals; device checks where only a device proves it; one screenshot test
   laying out the variants and drawing states in the four configurations (light, dark, 200% text, right
   to left), recorded and reviewed locally ([0012](../Decisions/0012-visual-regression-testing.md)); and a
   specimen in the quality catalog in its risky states (disabled, selected, invalid, loading, long text), so
   the touch, clipping, contrast and Accessibility Test Framework scans cover it. A component that opens a
   window (dialog, sheet, popup) is the exception: the scans stop at the window, so its surface goes in the
   screenshots and its Back behaviour in a device test ([0017](../Decisions/0017-quality-gates-and-verification.md)).
9. **Documentation.** KDoc on every public declaration with a usage example; a page in
   `Documentation/Components/` — its `@qeetrix/react` counterpart, an API table from the web's props to the Kotlin
   parameters, the look (from the CSS), accessibility, tokens and platform differences, not a copy of the KDoc —
   and a sample page with `@PreviewLightDark` previews, registered in `SampleCatalog`.
10. **API baseline.** `./gradlew :qeetrix:apiDump` after the API is settled; the updated
    `Documentation/API/qeetrix.api` is part of the change ([0013](../Decisions/0013-public-api-baseline.md)).

## What a component must not do

- Depend on Material 3 for its look. Foundation primitives are used underneath where they preserve
  Qeetrix's design and accessibility (`BasicTextField`, `Dialog`, `Popup`). Material 3 itself is used for
  one primitive only — `ModalBottomSheet`, inside `QeetrixBottomSheet` — with every colour and shape
  supplied by Qeetrix and no Material type in the public API; `MaterialBoundaryTest` fails on any other
  import ([0015](../Decisions/0015-navigation-overlays-and-feedback.md)).
- Own navigation or global state: no router, back stack, Back handler, singleton or global service.
- Own data: no fetching, caching, pagination, search index, filter engine or image loading, and no
  networking, database or image-loading dependency; collections are Compose's lazy layouts, not wrapped or
  re-implemented ([0016](../Decisions/0016-data-display-and-content-states.md)).
- Contain Qeet ID or any product's logic.
- Keep a copy of, save, log or describe what a user types into a password or code field
  ([FORMS.md](FORMS.md)).
- Read palette primitives, define a design value of its own, or draw a shadow on a surface in the page flow.
