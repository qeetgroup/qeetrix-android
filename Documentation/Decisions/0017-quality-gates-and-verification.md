# 0017 — Quality gates and verification: proven checks, on the platform that runs them

**Status:** Accepted, 2026-10-20 (Phase 6). Builds on [0009](0009-testing-and-documentation-strategy.md),
[0010](0010-component-api-conventions.md), [0012](0012-visual-regression-testing.md) and
[0013](0013-public-api-baseline.md).

## Context

Phase 6 hardens the existing library instead of adding components. Two things shaped its verification.

- **Some checks are silent where they don't apply.** Compose's `enableAccessibilityChecks()` (the Accessibility
  Test Framework, ATF) ran on Robolectric and reported nothing wrong with any component — and nothing wrong with an
  unlabelled 24 dp button either: under Robolectric the checks do not run. On an API 29 emulator they did not run
  either; on API 36 they did. Text-layout flags also misled: `hasVisualOverflow` is true for any single-line text
  narrower than its parent. A green check that cannot fail is not evidence.
- **The machine now has emulators** (API 29 and API 36, arm64, installed for Phase 6), so the device-only tests
  written in Phases 1–5 can run for the first time. There is still no physical device.

## Decision

1. **Every automated check proves it can fail.** Each scan has a negative control in the same test class — a
   24 dp unlabelled button, text cut off by a box too small for it, an animation that moves with animations on —
   and a check whose control is not caught is reported as not run, never as passed.
2. **One quality catalog.** `qeetrix/src/sharedTest` holds every in-place component in its risky states (disabled,
   selected, invalid, loading, long text) and localized specimens (Hindi, Telugu, Arabic, pseudo-expanded English).
   The JVM scans and the device checks run over the same catalog, so a new component joins every scan by joining the
   catalog.
3. **Accessibility automation runs on a device.** `QeetrixAccessibilityDeviceTest` runs ATF over the catalog in
   light and dark, from API 36, where the checks were verified to run, and fails if its control is not caught there.
   Below API 36 the runner leaves it out (`@SdkSuppress`): on API 29 the checks do not run, and Android's device
   reports count a JUnit assumption failure as a failure, so a skip could not be reported as one. On the JVM,
   semantics assertions remain the accessibility tests.
4. **Layout scans on the JVM, at a phone's density.** `LayoutQualityTest` (xhdpi) checks that every control
   reserves 48 dp in layout, and that no text is clipped at 100 %, at the sample's linear 200 % and at Android's
   non-linear 200 % setting, left to right and composed right to left from the start. Controls drawn smaller inside
   another one are listed by name and each has a `TouchLandingTest` proving a tap within 48 dp reaches it.
   `ComponentContrastTest` measures the colour pairs components draw — translucent fills composited over the canvas
   as drawn — against WCAG 1.4.3 and 1.4.11.
5. **A field without a visible label reserves a 48 dp touch height**, as a button does (0010): ATF measured an
   unlabelled search field at 40 dp on a device. Labelled fields already exceed it.
6. **Device tests run in CI on every push and pull request**, on API 29 and 36 (a matrix), instead of only on
   demand, so the accessibility checks run in CI too. Locally they ran on the same two levels (arm64 images).
7. **The sample's release is minified** (R8 and resource shrinking, signed with the debug key, never distributed),
   so every CI build checks that an app using the library shrinks and optimises under R8 without consumer rules;
   Phase 6 also ran the minified build on a device. The sample's debug build enables Android's pseudolocales (en-XA,
   ar-XB) for checks on a device.
8. **The API check stays the Binary Compatibility Validator's engine** (0013). Kotlin's built-in ABI validation is
   still experimental; running both on one baseline would duplicate the gate.
9. **Performance evidence is structural until there is representative hardware.** The Compose compiler's
   stability report, recomposition counts in tests, and allocation review of animation drawing. No Macrobenchmark or
   Baseline Profile module: Macrobenchmark refuses emulators by default and emulator frame times are not
   representative, and a profile module without measurements is a checklist item, not an optimisation.
10. **CI's third-party actions are pinned to commit SHAs**, with the tag in a comment.

## Consequences

- A check that silently stops working — a toolchain update that disables ATF, a scan that matches nothing —
  shows up as a failing control instead of a reassuring pass.
- The catalog has to be maintained: a component missing from it escapes every scan. The component contract says so.
- Unlabelled fields are 16 dp taller in layout than in Phases 3–5 (the drawn field is unchanged). Screens that
  aligned an unlabelled field with a 32 dp element now align it with a 48 dp touch area, as buttons are.
- Manual TalkBack, Switch Access and Voice Access passes, frame timing on real hardware, and API levels other than
  29 and 36 remain to be done; [RELEASE-READINESS-GAPS.md](../Quality/RELEASE-READINESS-GAPS.md) tracks them.
