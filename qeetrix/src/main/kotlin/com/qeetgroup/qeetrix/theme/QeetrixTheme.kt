package com.qeetgroup.qeetrix.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalDensity
import com.qeetgroup.qeetrix.foundation.color.QeetrixColors
import com.qeetgroup.qeetrix.foundation.elevation.QeetrixShadow
import com.qeetgroup.qeetrix.foundation.elevation.QeetrixShadowLayer
import com.qeetgroup.qeetrix.foundation.motion.rememberSystemReduceMotion
import com.qeetgroup.qeetrix.foundation.sizing.QeetrixDensity
import com.qeetgroup.qeetrix.foundation.typography.QeetrixTypography
import com.qeetgroup.qeetrix.tokens.generated.QeetrixDarkColors
import com.qeetgroup.qeetrix.tokens.generated.QeetrixLightColors

/**
 * Draws [content] in the Qeetrix theme: semantic colours for the resolved light or dark theme, the
 * typography, the content colour, text-selection colours and the reduced-motion setting.
 *
 * Use it once at the root of a screen or app. To draw part of a screen in a fixed theme — a dark
 * panel on a light page, as the web's nested `data-qx-color-scheme="dark"` does — nest another
 * `QeetrixTheme` with an explicit [mode]; only the colours and shadows change. The density is set the
 * same way, per region, with `LocalQeetrixDensity`.
 *
 * Nothing is global: the theme follows the composition, so configuration changes, live dark-mode
 * switches and previews need no extra plumbing. Qeet Group has one brand across every product, so a
 * theme cannot be built from arbitrary colours; new themes come from upstream through the generator.
 *
 * Status and navigation bar styling belongs to the activity (`enableEdgeToEdge`), not to this function.
 *
 * @param mode [QeetrixThemeMode.System] follows the device setting; `Light` and `Dark` force a theme.
 */
@Composable
public fun QeetrixTheme(
    mode: QeetrixThemeMode = QeetrixThemeMode.System,
    content: @Composable () -> Unit,
) {
    val dark =
        when (mode) {
            QeetrixThemeMode.Light -> false
            QeetrixThemeMode.Dark -> true
            QeetrixThemeMode.System -> isSystemInDarkTheme()
        }
    val colors = if (dark) QeetrixDarkColors else QeetrixLightColors
    val selection =
        remember(colors) {
            TextSelectionColors(handleColor = colors.borderFocus, backgroundColor = colors.selection)
        }
    // The optical sizes follow the font scale, so the typography does (QeetrixFonts).
    val density = LocalDensity.current
    val typography = remember(density.fontScale) { QeetrixTypography.at(density) }
    CompositionLocalProvider(
        LocalQeetrixColors provides colors,
        LocalQeetrixIsDark provides dark,
        LocalQeetrixTypography provides typography,
        LocalQeetrixReduceMotion provides rememberSystemReduceMotion(),
        LocalQeetrixContentColor provides colors.textDefault,
        LocalTextSelectionColors provides selection,
        content = content,
    )
}

/** The current Qeetrix theme's values. Read them inside [QeetrixTheme]. */
public object QeetrixTheme {
    /** The semantic colours of the current colour scheme. */
    public val colors: QeetrixColors
        @Composable @ReadOnlyComposable
        get() = LocalQeetrixColors.current

    /** The typography roles. */
    public val typography: QeetrixTypography
        @Composable @ReadOnlyComposable
        get() = LocalQeetrixTypography.current

    /** Whether the current theme is the dark theme. */
    public val isDark: Boolean
        @Composable @ReadOnlyComposable
        get() = LocalQeetrixIsDark.current

    /**
     * Whether the user has turned animations off (Settings › Accessibility › Remove animations). When
     * true, every Qeetrix duration is 0, as the web's `motion.duration.*` are under
     * `prefers-reduced-motion: reduce`.
     */
    public val reduceMotion: Boolean
        @Composable @ReadOnlyComposable
        get() = LocalQeetrixReduceMotion.current

    /** The density of this subtree ([LocalQeetrixDensity]); `default` unless a parent sets one. */
    public val density: QeetrixDensity
        @Composable @ReadOnlyComposable
        get() = LocalQeetrixDensity.current

    /** A shadow's layers in the current colour scheme, in CSS order (the first is drawn on top). */
    @Composable
    @ReadOnlyComposable
    public fun shadowLayers(shadow: QeetrixShadow): List<QeetrixShadowLayer> = shadow.layers(dark = isDark)
}
