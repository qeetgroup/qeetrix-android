# Third party

What Qeetrix for Android bundles, and under which licence. All four licence texts ship inside the AAR, in
`assets/qeetrix/licenses/`, so an app can show them in its open-source notices.

| Bundled | From | Licence |
|:--|:--|:--|
| Qeet Sans font (the file is Cal Sans 2.011) | `@qeetrix/tokens` 0.0.13, unmodified | SIL Open Font License 1.1 |
| Fira Code font | `@qeetrix/tokens` 0.0.13, unmodified | SIL Open Font License 1.1 |
| 59 icons, as path data | `@qeetrix/icons` 2.1.0, based on [Lucide](https://lucide.dev) | MIT, with Lucide's ISC notice |
| The library's own code | This repository | MIT (© 2026 Qeet Group) |

The runtime dependencies (Jetpack Compose and AndroidX) are Apache 2.0 and come to the app from Google's Maven
repository, not bundled. Build and test tools aren't redistributed.
