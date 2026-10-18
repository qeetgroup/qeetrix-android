# Module qeetrix

Qeetrix for Android: the Qeet Group design system in Kotlin and Jetpack Compose. Phase 1 provides the
foundations — semantic colours, typography, spacing, corners, sizing, elevation, motion, icons and
accessibility helpers — generated from the published `@qeetrix/ui` tokens. Components arrive in
Phase 2.

Wrap your content in [com.qeetgroup.qeetrix.theme.QeetrixTheme] and read the theme's values from the
`QeetrixTheme` object:

```kotlin
QeetrixTheme {
    BasicText(
        "Account",
        style = QeetrixTheme.typography.title.copy(color = QeetrixTheme.colors.textPrimary),
    )
}
```

Every value comes from the upstream tokens; there is no public way to define a colour, size or font of
your own. Token names are the web's (`color.text.on-brand` → `textOnBrand`), so a design reference on
ui.qeet.in maps directly to a Kotlin name.

# Package com.qeetgroup.qeetrix.theme

The theme: [com.qeetgroup.qeetrix.theme.QeetrixTheme] scopes the light or dark colours, typography,
content colour, text-selection colours and the reduced-motion setting over its content.

- **Modes.** `System` follows the device's dark-theme setting live; `Light` and `Dark` force a theme.
  The [com.qeetgroup.qeetrix.theme.QeetrixThemeMode] values (`light`, `dark`, `system`) are the web's
  and iOS's, so a stored preference reads the same everywhere.
- **Nested themes.** A `QeetrixTheme` inside another changes only its subtree's colours — a dark panel
  on a light screen, as the web's nested `.dark` scope does.
- **Content colour.** [com.qeetgroup.qeetrix.theme.LocalQeetrixContentColor] is what text and icons
  inherit, like `currentColor` on the web. The theme sets it to `textPrimary`.

The theme does not style the status and navigation bars; that is the activity's job
(`enableEdgeToEdge`).

# Package com.qeetgroup.qeetrix.foundation.color

[com.qeetgroup.qeetrix.foundation.color.QeetrixColors] holds every semantic colour of one theme. Read the
current theme's with `QeetrixTheme.colors`.

Use the role, not the look: `surfaceDefault` for a card, `textSecondary` for supporting text,
`borderControl` for a field's outline, `feedbackError` for an error fill with `textOnFeedback` on it.
The pairs qeetrix-ui designs together meet WCAG 2.2 AA in both themes; this library's tests measure
them on the Android values.

Palette primitives (`color.qeet.500` and so on) are deliberately not available, as on the web.
Colours are converted from OKLCH per CSS Color 4; the few outside sRGB are Display P3 colours, drawn
in P3 in an activity with `android:colorMode="wideColorGamut"` on a wide-gamut screen and converted to
sRGB elsewhere.

# Package com.qeetgroup.qeetrix.foundation.typography

Nine roles — `display`, `heading`, `title`, `body`, `label`, `label-compact`, `caption`, `micro` and
`code` — as Compose `TextStyle`s in [com.qeetgroup.qeetrix.foundation.typography.QeetrixTypography].

- **Fonts.** Qeet Sans (Cal Sans v2.011, the web's font) and Fira Code 6.2, bundled as variable TTFs.
  Each role sets the axes the web sets: weight (`wght`), letter style (`GEOM`, see
  [com.qeetgroup.qeetrix.foundation.typography.QeetrixFontGeometry]) and optical size (`opsz`, from
  the rendered size).
- **Scaling.** Sizes are in sp and follow the user's font size, including Android 14's non-linear
  scaling of large text. Line heights are em multiples with the extra space split above and below
  each line, as CSS does, so text keeps its proportions at every scale.
- **Bold Text.** Android's Bold Text setting raises the weight; every weight of the scale is bundled,
  so the heavier instance is real rather than synthesised.
- **Other scripts.** The Qeet fonts are Latin-only. Devanagari, Tamil, Bengali and other scripts fall
  back to the system's fonts character by character; give such text enough line height.

# Package com.qeetgroup.qeetrix.foundation.spacing

[com.qeetgroup.qeetrix.foundation.spacing.QeetrixSpacing] is the spacing scale in dp — one CSS px is
one dp — from `Xxs` (2) to `Xl5` (96). Steps whose upstream name starts with a digit move the digit to
the end: `space.2xl` is `Xl2`.

# Package com.qeetgroup.qeetrix.foundation.shape

[com.qeetgroup.qeetrix.foundation.shape.QeetrixCorner] holds the corner radii in dp, and
[com.qeetgroup.qeetrix.foundation.shape.QeetrixShapes] the same values as Compose shapes. Use the role
that matches the element — `Control` for buttons, `Field` for inputs, `Chip`, `Surface` for cards,
`Overlay` for menus and dialogs — rather than a raw step.

# Package com.qeetgroup.qeetrix.foundation.sizing

Icon sizes and stroke widths, border widths, and the control metrics of the three densities
(`default`, `comfortable`, `compact`). Density heights are drawn sizes: a control still needs a 48 dp
touch target ([com.qeetgroup.qeetrix.foundation.accessibility.qeetrixMinimumTouchTarget]).

# Package com.qeetgroup.qeetrix.foundation.elevation

[com.qeetgroup.qeetrix.foundation.elevation.QeetrixElevation] lists the web's elevations, and
[com.qeetgroup.qeetrix.foundation.elevation.qeetrixElevation] draws one behind an element the way CSS
draws `box-shadow` — offset, spread and blur per layer, outer layers outside the shape and inset layers
inside — in the current theme's shadow colours.

# Package com.qeetgroup.qeetrix.foundation.motion

Durations in milliseconds ([com.qeetgroup.qeetrix.foundation.motion.QeetrixDuration]) and easing curves
([com.qeetgroup.qeetrix.foundation.motion.QeetrixEasing]). Build animation specs with
[com.qeetgroup.qeetrix.foundation.motion.qeetrixAnimationSpec]: when the user has turned animations off
(Settings › Accessibility › Remove animations), it uses the `motion.reduced.*` tokens instead, as the web
does for `prefers-reduced-motion`.

# Package com.qeetgroup.qeetrix.foundation.accessibility

Platform accessibility rules that are not tokens: the 48 dp minimum touch target, the WCAG contrast
ratio Qeetrix's tests use, and the focus-indicator geometry ([com.qeetgroup.qeetrix.foundation.accessibility.QeetrixFocus]).
See `Documentation/Standards/ACCESSIBILITY.md` in the repository for the full standard, including what
needs testing on a device.

# Package com.qeetgroup.qeetrix.icons

[com.qeetgroup.qeetrix.icons.QeetrixIcon] draws a Qeetrix icon from vector paths in the inherited content
colour. Phase 1 carries a representative set ([com.qeetgroup.qeetrix.icons.QeetrixIconName]) in round
and sharp shapes, outline and filled where qeetrix-icons draws them.

Pass a `contentDescription` when the icon means something on its own; pass `null` when it is
decorative and the text beside it carries the meaning. Icons whose meaning has a direction, such as
`send`, mirror in right-to-left layouts.

# Package com.qeetgroup.qeetrix.tokens

[com.qeetgroup.qeetrix.tokens.QeetrixProvenance] names the exact upstream releases this build was
generated from — useful on an about screen or in a bug report, and for checking that web, iOS and
Android ship the same design-system release.
