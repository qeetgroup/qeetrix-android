# Accessibility

Qeetrix for Android follows Android's accessibility guidance and aims at WCAG 2.2 AA. This page says
what the foundations already provide, what components must add, and what is verified where.

## What the foundations provide

| Need | Provided by | Verified |
|:--|:--|:--|
| Contrast — 4.5:1 text, 3:1 boundaries and large brand text | The token pairs qeetrix-ui designs together | JVM: 142 pairs × 2 themes (`ContrastTest`) |
| Font scaling, including Android 14's non-linear scaling | sp sizes, em line heights | JVM, real text layout (`TypographyTest`) |
| Bold Text | Every weight bundled; the platform raises the weight | Device check needed |
| Script fallback (Devanagari, Tamil, …) | Platform per-glyph fallback | Device check needed |
| Touch targets of 48 dp | `Modifier.qeetrixMinimumTouchTarget()` | JVM (`MinimumTouchTargetTest`) |
| Decorative vs meaningful icons | `QeetrixIcon(contentDescription = null / "…")` | JVM semantics (`IconTest`) |
| Right-to-left | Directional icons mirror (`autoMirror`) | JVM (`IconTest`); visual check in the sample |
| Reduced motion | `qeetrixAnimationSpec`, from Settings › Remove animations | JVM (`MotionTest`) |
| Focus indicator geometry | `QeetrixFocus`, `colors.focusRing` | Values only; components draw it |

## What every component must add

- **Semantics.** A role (`Role.Button`, `Role.Checkbox`, …), a state description where the state is not
  in the label, and merged semantics so TalkBack reads one element per control. Translate the web's ARIA
  intent into Compose semantics; do not copy ARIA attributes.
- **Touch target.** At least 48 × 48 dp, even when the drawn control uses a compact density height.
- **Focus.** Keyboard and D-pad focus order matches visual order; focused controls draw the focus ring
  (`focus.ring-width`, `focus.offset`, `colors.focusRing`).
- **Text.** Reflow at large font scales instead of truncating; never fix a text container's height.
- **Motion.** Every animation goes through `qeetrixAnimationSpec` (or `QeetrixMotion.spec`).
- **Colour.** Never the only signal: pair status colours with an icon or text.

## What only a device can verify

Unit tests do not prove these; each needs a manual or instrumented check on a device before a component
is marked stable:

- TalkBack reading order, labels and actions; Switch Access and keyboard navigation.
- Bold Text and the largest font scales on real fonts.
- Display P3 colours on a wide-gamut screen.
- Glyph fallback for Indic scripts.
- High-contrast text (Settings › Accessibility); Qeetrix has no increased-contrast tokens yet, so
  nothing in the design system changes under it.

`qeetrix/src/androidTest` holds the device tests; run them with
`./gradlew :qeetrix:connectedDebugAndroidTest` or the CI's manual `instrumented` job.
