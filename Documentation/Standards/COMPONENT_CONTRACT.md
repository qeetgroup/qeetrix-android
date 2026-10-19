# Component contract

What every Qeetrix Android component must provide, so the first component and the hundredth have the
same shape. The Phase 2 and Phase 3 components follow it ([Components](../Components/README.md)); new
ones start from `Scripts/new-component` ([Templates/Component](../../Templates/Component/README.md)).
Inputs also follow [FORMS.md](FORMS.md): they are built on the shared field parts and take the same
field parameters.

## The catalogue it follows

`@qeetrix/ui` 2.1.6 (git `7d7f651`), the version this library is pinned to, ships
`component-manifest.json` with **137 components**. Each entry's `category` is its family — the same
grouping as qeetrix-ui's `scripts/config/component-map.json` and its `src/components/<Family>/`
folders: **97 families**. For example, the `Button` category holds button, button-group, close-button,
icon-button, segmented-control, toggle and toggle-tip.

ui.qeet.in groups the same pages into navigation categories (Actions, Forms & inputs, Overlays, …).
Those do not partition the families — qeetrix-ios found the Button family spanning Actions and Overlays
— so they are used only for documentation and the sample's navigation, not for packages.

Re-check these counts whenever the `@qeetrix/ui` pin moves. The pinned manifest is
`Upstream/qeetrix-ui/component-manifest.json`.

## Where a component lives

```text
qeetrix/src/main/kotlin/com/qeetgroup/qeetrix/components/<package>/Qeetrix<Component>.kt
qeetrix/src/test/kotlin/com/qeetgroup/qeetrix/components/<package>/Qeetrix<Component>Test.kt            behaviour and semantics
qeetrix/src/test/kotlin/com/qeetgroup/qeetrix/components/<package>/Qeetrix<Component>ScreenshotTest.kt  Roborazzi
qeetrix/src/androidTest/kotlin/com/qeetgroup/qeetrix/components/<package>/                               device-only checks
sample/src/main/kotlin/com/qeetgroup/qeetrix/sample/components/<group>/<Component>Sample.kt
Documentation/Components/<Component>.md
```

`<package>` is the manifest family in lower case (`button`, `datepicker`), with `control` appended to a
Java keyword (`switch` → `switchcontrol`); IconButton lives in `components/button/` beside Button. One
file per component; internals a family shares (the button frame) live in that package.
`<group>` is the ui.qeet.in group (`actions`, `forms`, `datadisplay`, …). Packages are created with
their first component, never as placeholders, and there is no Gradle module per component or category
([0001](../Decisions/0001-single-library-module.md), [0010](../Decisions/0010-component-api-conventions.md)).

## What every component defines

1. **Public Compose API.**
   - A composable named `Qeetrix<Component>` (`QeetrixButton`), with Compose's parameter order:
     required parameters, `modifier: Modifier = Modifier`, optional parameters, then the trailing
     content slot.
   - Typed parameters — enums and sealed types, not strings. The meaning matches the web component; the
     syntax is native. A web `variant="destructive"` becomes `QeetrixButtonVariant.Destructive`.
   - State hoisted to the caller (`checked` + `onCheckedChange`), with `interactionSource` where the
     component reacts to press, hover or focus. No React-style prop bags.
   - Defaults in a `<Component>Defaults` object only when callers need them (sizes, content padding).
     No separate tokens or style class per component unless the component needs one.
2. **Variants and states.** The web component's variants, by the same names. States — enabled,
   pressed, focused, hovered, selected, error, loading — come from Compose (`enabled`,
   `InteractionSource`), never tracked twice.
3. **Customisation** limited to what the design system allows: variant, size, density. No arbitrary
   colour or font parameters; Qeetrix has one brand. Feedback is the web's colour and elevation steps,
   not a ripple (`indication = null`).
4. **Tokens.** Semantic tokens by default. Component tokens only where qeetrix-ui defines
   `component.<name>.*`, adopted in `Upstream/manifest.json` and generated as an internal
   `Qeetrix<Group>ComponentTokens` ([0011](../Decisions/0011-component-tokens-and-web-metrics.md)).
   Tailwind geometry comes from `QeetrixWebScale`, with the web class beside each use. No literal
   colours, sizes or durations in component code.
5. **Accessibility** ([ACCESSIBILITY.md](ACCESSIBILITY.md)): a 48 dp touch target, correct semantics
   (role, state, label), font scaling with reflow, RTL, reduced motion, and the contrast pairs.
6. **Native adaptations**, where Android behaviour differs from the web (a bottom sheet instead of a
   dialog on compact width, predictive back), written down in the component's KDoc and in its page's
   "Platform differences", and in [CROSS_PLATFORM_CONTRACT.md](../CROSS_PLATFORM_CONTRACT.md) when it
   also differs from iOS.
7. **Tests.** Behaviour and semantics with Compose UI Test on the JVM — what a user and TalkBack
   observe, not layout internals; device checks where only a device proves it; one screenshot test
   laying out the variants and drawing states in the four configurations (light, dark, 200% text, right
   to left), recorded and reviewed locally ([0012](../Decisions/0012-visual-regression-testing.md)).
8. **Documentation.** KDoc on every public declaration with a usage example; a page in
   `Documentation/Components/` (purpose, usage, states, accessibility, tokens, platform differences —
   not a copy of the KDoc); and a sample page with `@PreviewLightDark` previews, registered in
   `SampleCatalog`.
9. **API baseline.** `./gradlew :qeetrix:apiDump` after the API is settled; the updated
   `Documentation/API/qeetrix.api` is part of the change ([0013](../Decisions/0013-public-api-baseline.md)).

## What a component must not do

- Depend on Material 3 for its look. A Material or foundation primitive may be used underneath where
  it preserves Qeetrix's design and accessibility (for example `BasicTextField`, `Indication`).
- Contain Qeet ID or any product's logic.
- Keep a copy of, save, log or describe what a user types into a password or code field
  ([FORMS.md](FORMS.md)).
- Read palette primitives, or define a design value of its own.
