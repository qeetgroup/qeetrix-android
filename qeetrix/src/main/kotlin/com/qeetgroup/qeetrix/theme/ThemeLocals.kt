package com.qeetgroup.qeetrix.theme

import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import com.qeetgroup.qeetrix.foundation.sizing.QeetrixDensity
import com.qeetgroup.qeetrix.foundation.typography.QeetrixTypography
import com.qeetgroup.qeetrix.tokens.generated.QeetrixLightColors

// The values QeetrixTheme scopes. Static locals: a theme switch is rare, and when it happens the
// whole subtree is meant to redraw, so per-reader invalidation tracking would only cost memory.
// Outside any QeetrixTheme they hold the light theme, so previews and tests render.

internal val LocalQeetrixColors = staticCompositionLocalOf { QeetrixLightColors }
internal val LocalQeetrixIsDark = staticCompositionLocalOf { false }
internal val LocalQeetrixTypography = staticCompositionLocalOf { QeetrixTypography.Default }
internal val LocalQeetrixReduceMotion = staticCompositionLocalOf { false }

/**
 * The colour content draws in — text and icons inherit it, as `currentColor` does on the web.
 * [QeetrixTheme] sets it to `colors.textDefault`; a component sets it for its own content (for
 * example `textOnBrand` inside a solid brand button). Changes often and locally, so it is a dynamic local.
 */
public val LocalQeetrixContentColor: androidx.compose.runtime.ProvidableCompositionLocal<Color> =
    compositionLocalOf { Color.Unspecified }

/**
 * The density of a subtree — the web's `data-qx-density` attribute. Set it once per region, never per
 * component (design rules §6). Control and row heights, the form gap and the icon stroke follow it;
 * everything else is the same at every density.
 *
 * ```
 * CompositionLocalProvider(LocalQeetrixDensity provides QeetrixDensity.Compact) { DataTable() }
 * ```
 */
public val LocalQeetrixDensity: androidx.compose.runtime.ProvidableCompositionLocal<QeetrixDensity> =
    compositionLocalOf { QeetrixDensity.Default }
