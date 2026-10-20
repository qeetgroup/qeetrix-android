# Decision records

One decision per file: its context, the decision and its consequences. A record is never rewritten
after it is accepted; a later record supersedes it.

| Record | Decision | Status |
|:--|:--|:--|
| [0001](0001-single-library-module.md) | One library module, `:qeetrix`, plus the `:sample` app | Accepted |
| [0002](0002-gradle-build-and-dependency-policy.md) | Gradle build and dependency policy | Accepted; "no Material" replaced by 0015 for the bottom sheet |
| [0003](0003-repository-layout.md) | Repository layout | Accepted |
| [0004](0004-tokens-from-the-published-qeetrix-ui-build.md) | Tokens come from the published `@qeetrix/ui` build | Accepted |
| [0005](0005-colour-conversion-per-css-color-4.md) | Colours are converted per CSS Color 4 | Accepted |
| [0006](0006-theming-through-composition-locals.md) | Theming through composition locals | Accepted |
| [0007](0007-fonts-are-the-webs-fonts-in-native-format.md) | Fonts are the web's fonts, as Android font resources | Accepted |
| [0008](0008-icons-are-generated-vector-paths.md) | Icons are generated vector paths | Accepted |
| [0009](0009-testing-and-documentation-strategy.md) | Testing and documentation strategy | Accepted; screenshot paragraph superseded by 0012 |
| [0010](0010-component-api-conventions.md) | Component API conventions | Accepted |
| [0011](0011-component-tokens-and-web-metrics.md) | Component tokens, and web metrics that are not tokens | Accepted |
| [0012](0012-visual-regression-testing.md) | Visual regression testing with Roborazzi; baselines stay local | Accepted |
| [0013](0013-public-api-baseline.md) | Public API baseline | Accepted |
| [0014](0014-form-components.md) | Form components: one field, native inputs, the app's validation | Accepted |
| [0015](0015-navigation-overlays-and-feedback.md) | Navigation, overlays and feedback: native windows, state owned by the app | Accepted |
| [0016](0016-data-display-and-content-states.md) | Data display and content states: Compose's collections, the app's data | Accepted |
| [0017](0017-quality-gates-and-verification.md) | Quality gates and verification: proven checks, on the platform that runs them | Accepted |
| [0018](0018-maven-distribution.md) | Maven distribution: one release variant, built and verified here, published only with approval | Accepted (build); registry, version and licence open |

Records 0004, 0005, 0007 and 0008 take the same positions as qeetrix-ios's 0002–0005, so both native
libraries derive identical values from the same inputs; 0010–0013 correspond to qeetrix-ios's 0008–0011
(API baseline, visual regression, component API, component tokens), 0014 to its 0012 (form components),
0015 to its 0014 (navigation, overlays and feedback) and 0016 to its 0015 (data display and content
states). See [CROSS_PLATFORM_CONTRACT.md](../CROSS_PLATFORM_CONTRACT.md).
