# 0010 — Component API conventions

**Status:** Accepted, 2026-10-18 (Phase 2)

## Context

Phase 2 adds the first eleven components. Their public API is the hardest thing to change later, so the
shape every component takes is decided once, here, and held by the public API baseline
([0013](0013-public-api-baseline.md)). qeetrix-ios made the same decisions for SwiftUI in its ADR 0010;
this record takes the same positions where the platforms allow and the Compose idiom where they differ.

## Decision

1. **Names.** `Qeetrix<Component>` for the composable; `Qeetrix<Component><Axis>` for its options
   (`QeetrixButtonVariant`, `QeetrixButtonSize`), as top-level enums. The same names as iOS.
2. **Variant and size names are the web's** (`Default`, `Outline`, `Sm`, `Lg`), in the web's order.
   Each enum carries the web name as `value`, so a stored or logged value reads the same everywhere.
3. **Compose's parameter order**: required parameters, `modifier: Modifier = Modifier`, optional
   parameters, then the trailing content slot. Optional slots are nullable composable lambdas, so an
   absent slot takes no space.
4. **State is hoisted.** `checked` with `onCheckedChange`; text in a caller-owned `TextFieldState`; an
   optional `interactionSource` where a component reacts to press, hover or focus. Components keep no
   state of their own beyond animation, and none in singletons.
5. **No colour or font parameters.** Customisation is variant, size and density; Qeetrix has one brand.
   `QeetrixText.color` is the one exception, because text colour is the content's concern in Compose;
   it is documented to take a theme role.
6. **Feedback without ripples.** Clickables use `indication = null`; the web's pressed, hovered and
   selected colour and elevation steps are the feedback, and keyboard focus draws the Qeet focus ring.
   Where the web relies on hover (a link's underline, a card's boundary), a press shows it, since touch
   has no hover.
7. **Touch targets** of 48 × 48 dp are reserved in layout around the web-sized drawing
   (`qeetrixMinimumTouchTarget`). Compose's `clickable` cannot take input outside its bounds, so the
   iOS approach — a larger hit area without moving layout — is not available.
8. **Built-in strings are `qx_` string resources** ("Loading", "(optional)"), which an app translates by
   overriding them, and each has a parameter that replaces it where a component shows it.
9. **Packages follow the web's families.** One package per `component-manifest.json` family
   (`components.button` holds `QeetrixButton` and `QeetrixIconButton`), one file per component. A family
   whose lower-case name is a Java keyword gets `control` appended (`switch` → `switchcontrol`). The
   sample groups pages by ui.qeet.in's sidebar groups instead.
10. **Additive evolution.** New variants, sizes and defaulted parameters are additive. Removing or
    renaming one, or changing a default's look, is breaking: it needs a CHANGELOG entry and an API
    baseline update in the same pull request.

## Why family packages

ui.qeet.in's groups are editorial and have moved before; a package rename is a breaking change for every
consumer. Families are the web's own code structure (`@qeetrix/ui/components/<Family>`), so they move only
when the web's code does. A package per component would add imports for consumers (`…button.iconbutton`
beside `…button.QeetrixButtonVariant`) without encapsulation, since `internal` is module-wide in Kotlin.

## Consequences

- Compose's own components put everything in one package; Qeetrix's family packages are a middle path,
  and the KDoc package overviews in `qeetrix/Module.md` describe each.
- Adding an entry to a public enum can break a consumer's exhaustive `when`. Consumers rarely match on
  these enums, so new entries are treated as additive and the CHANGELOG says so.
- The 48 dp reservation makes small controls take more room than on the web; layouts that need the web's
  density use the `Xs`/`Sm` sizes knowing the touch target still applies.
