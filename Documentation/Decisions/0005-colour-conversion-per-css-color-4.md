# 0005 — Colours are converted per CSS Color 4

**Status:** Accepted, 2026-10-18

## Context

Qeetrix colours are authored in OKLCH and mixed with `color-mix(in oklab, …)`. A few, from the data
palette and rating, lie outside sRGB; browsers draw them in Display P3 on P3 screens, and many Android
phones have P3 screens.

## Decision

- **Conversion.** The CSS Color 4 sample-code matrices (OKLCH → OKLab → XYZ → RGB) and CSS Color 5
  premultiplied mixing — the same code as qeetrix-ios.
- **Output.** sRGB when the colour fits, emitted as the packed 8-bit value Compose stores
  (`Color(0xFFRRGGBB)`); Display P3 when it fits P3 but not sRGB (`ColorSpaces.DisplayP3`); the CSS
  Color 4 gamut-mapping algorithm when it fits neither (none in 2.1.6).
- **Precision.** Components rounded to six decimals before emission, so output is byte-stable.

## Consequences

- Verified for 2.1.6: all 168 colour values (84 tokens × 2 themes) equal qeetrix-ios's generated
  components, and their 8-bit sRGB renditions equal culori's — the converter qeetrix-ui's own contrast
  gate uses — for every input (`Tools/QeetrixGen` tests). The brand values #F26D0E, #D04800 and #B73D10
  are exact.
- Seven tokens (nine values) are Display P3. They render in P3 only in a window with
  `colorMode="wideColorGamut"`; elsewhere Android converts them to sRGB.
