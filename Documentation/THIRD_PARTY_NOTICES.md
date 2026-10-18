# Third-party notices

What Qeetrix for Android redistributes, under which licence, and where the licence text ships. The
library has no third-party runtime code beyond AndroidX Compose (Apache 2.0, resolved by the app from
Google's Maven repository, not bundled in the AAR).

| Asset | Shipped as | Source | Licence | Licence text in the AAR |
|:--|:--|:--|:--|:--|
| Qeet Sans — Cal Sans v2.011, variable | `res/font/qx_cal_sans_vf.ttf` | [calcom/sans](https://github.com/calcom/sans) tag `v2.011`, `fonts/calsans-var-full/CalSansVF.ttf`, unmodified | SIL Open Font License 1.1, no Reserved Font Name | `assets/qeetrix/licenses/CalSans-OFL.txt` |
| Fira Code 6.2, variable | `res/font/qx_fira_code_vf.ttf` | [tonsky/FiraCode](https://github.com/tonsky/FiraCode) release `6.2`, `variable_ttf/FiraCode-VF.ttf`, unmodified | SIL Open Font License 1.1, no Reserved Font Name | `assets/qeetrix/licenses/FiraCode-OFL.txt` |
| Qeetrix icon drawings (12 icons, compiled into path data) | `classes.jar` | [qeetgroup/qeetrix-icons](https://github.com/qeetgroup/qeetrix-icons) `331cd85` (`@qeetrix/icons` 1.0.11), based on [Lucide](https://lucide.dev) 1.52.0 | MIT (Qeet Group) with Lucide's ISC licence and Feather's MIT notice | `assets/qeetrix/licenses/qeetrix-icons-LICENSE.txt` |

The fonts are the files the web ships: each TTF is accepted only if its release's WOFF2 is byte-identical
to the one inside `@qeetrix/ui` 2.1.6 (`Upstream/manifest.lock.json` records both hashes). The OFL allows
bundling and redistribution provided the licence travels with the font and the font is not sold on its
own; the web's "Qeet Sans" family name is used only in the web's CSS and in documentation, never inside
the font file.

An app that shows open-source notices can read the three texts from its own assets at
`qeetrix/licenses/`.
