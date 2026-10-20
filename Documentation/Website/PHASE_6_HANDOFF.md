# ui.qeet.in handoff: Android quality, accessibility and localization

For whoever maintains the component pages on ui.qeet.in (`qeetrix-docs`). Phase 6 added no components; it
verified and hardened the existing ones. This lists what a page's **Android** section may now say, what it must
not say, and the behaviour that changed. Nothing here has been published: qeetrix-android has no release, and
nothing should be documented as installable until it does.

The evidence is in [Documentation/Quality/](../Quality/), starting with
[PHASE-6-AUDIT.md](../Quality/PHASE-6-AUDIT.md); the verification approach is
[ADR 0017](../Decisions/0017-quality-gates-and-verification.md).

## What a page may say

- **Accessibility, automated.** Each component's names, roles, states and actions are tested; the Accessibility
  Test Framework found no issue in any in-place component, in light and dark, on an Android 16 (API 36)
  emulator; every control reserves or takes a 48 dp touch area; text is not clipped at 200 % font size or right to
  left; the colours components draw meet WCAG 1.4.3 and 1.4.11 contrast in both themes.
- **Reduced motion.** With Remove animations on, spinners, progress and skeletons hold still and accordions open at
  once.
- **Right to left.** Layouts mirror; short labels (buttons, chips, tabs, badges, navigation, menus, field labels)
  take their direction from their own text, so an English label in an Arabic screen reads and ends correctly.
- **Numbers.** The number field reads and writes the device's locale, Indian grouping and native digits included,
  and keeps its value when the locale changes.

## What a page must not say

- That Qeetrix for Android is **WCAG-compliant, accessible, certified or TalkBack-verified**. No manual TalkBack,
  Switch Access, Voice Access or keyboard pass has been done
  ([RELEASE-READINESS-GAPS.md](../Quality/RELEASE-READINESS-GAPS.md)).
- Any **performance figure**: none was measured ([PERFORMANCE-REPORT.md](../Quality/PERFORMANCE-REPORT.md)).
- That Qeetrix is **translated**: its 21 built-in strings are English; apps override them.

## Behaviour that changed in Phase 6

Write these as platform notes where a page already has an Android section:

| Page | Change |
|:--|:--|
| Input, Textarea, Select, Number Field, Search | A field **without a visible label** now reserves a 48 dp touch height (the drawn field is unchanged), as a button does |
| Number Field | Text written in one locale is rewritten, not re-read, after a change of locale |
| Button, Chip, Tabs, Badge, Menu, Field, Timeline | Labels take their paragraph direction from their text in right-to-left layouts (previously from the layout, which cut some labels and misplaced trailing punctuation) |
| Skeleton | Unchanged on screen; its shimmer no longer allocates on each frame |

## What did not change

No public API, token value, colour, size or default changed. The API baseline is unchanged
([API-COMPATIBILITY.md](../Quality/API-COMPATIBILITY.md)).
