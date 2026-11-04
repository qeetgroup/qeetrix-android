# 🧭 Decisions

The architecture decisions behind Qeetrix for Android, as they stand today. The full records, with the alternatives
weighed, are in the git history (`git log -- Documentation/Decisions`).

| # | Decision | In short |
|:--|:--|:--|
| 0001 | **One library module** | `:qeetrix` is the only library (AAR); foundations, theme, icons and components are packages inside it. `:sample` uses its public API only. |
| 0002 | **Gradle build and dependency policy** | Gradle 9.8.2 (wrapper pinned by checksum), AGP 9.5.0, Kotlin 2.4.21, compile SDK 37, minimum SDK 29. The release classpaths are locked (`qeetrix/gradle.lockfile`); dependencies come only from Google and Maven Central. |
| 0003 | **Repository layout** | The library in `qeetrix/`, the sample in `sample/`, the generator in `Tools/QeetrixGen/`, pinned upstream inputs in `Upstream/`, scripts in `Scripts/`, docs in `docs/`. |
| 0004 | ~~Tokens from `@qeetrix/ui`~~ | Replaced by 0019. |
| 0005 | **Colours per CSS Color 4** | OKLCH converted with the CSS Color 4 matrices, as qeetrix-ios does: sRGB when it fits, Display P3 otherwise. Every colour matches the hex the tokens package records. |
| 0006 | **Theming through composition locals** | `QeetrixTheme(mode)` provides colours, type, density and reduced motion through composition locals. No global state, no Material theme. |
| 0007 | **The web's fonts** | Qeet Sans and Fira Code: the TrueType files `@qeetrix/tokens` publishes for native platforms, unmodified, with each role's weight, letter style and optical size. |
| 0008 | **Icons are generated vector paths** | The pinned SVGs become Compose paths at generation time; tinted with the content colour; mirrored in right to left where the meaning has a direction. |
| 0009 | **Testing and documentation** | JVM tests first (Robolectric, Compose UI Test), device tests for what only a device shows, KDoc on everything public. |
| 0010 | **Component API conventions** | `Qeetrix<Component>` with `Qeetrix<Component><Axis>` options, the web's variant and size names, the app owning state, `modifier` as the first optional parameter. |
| 0011 | **Component tokens and web metrics** | Component tokens are adopted per group (`sidebar`, `timeline`); every other size comes from the family's CSS, restated with the CSS beside it. |
| 0012 | **Visual regression testing** | Roborazzi on Robolectric: every component in light, dark, 200 % text and right to left. Baselines stay on the maintainer's machine and aren't committed (the owner's choice). |
| 0013 | **Public API baseline** | `qeetrix/api/qeetrix.api`, written by `apiDump` and checked by `apiCheck` on every build. |
| 0014 | **Form components** | One shared field (label, control, help, error); native text inputs; the app owns values and validation. |
| 0015 | **Navigation, overlays and feedback** | The app owns navigation and every `open` flag. Overlays are Compose windows; Material is used only for the date and time pickers and pull to refresh. |
| 0016 | **Data display and content states** | Qeetrix presents, the app owns the data. Collections are Compose's lazy lists with the app's keys. |
| 0017 | **Quality gates** | Every automated check proves it can fail (a negative control). Accessibility, touch, clipping, contrast and reduced-motion scans run in `./gradlew check`. |
| 0018 | **Maven distribution** | One `release` variant with sources, `com.qeetgroup:qeetrix`, published to GitHub Packages by `release.yml` on merge to `main`, after approval ([releases.md](releases.md)). |
| 0019 | **Built from `@qeetrix/react`, `@qeetrix/tokens` and `@qeetrix/icons`** | The three npm packages, pinned and integrity-checked; tokens generated from the DTCG build; every component re-specified to its `@qeetrix/react` counterpart. |
