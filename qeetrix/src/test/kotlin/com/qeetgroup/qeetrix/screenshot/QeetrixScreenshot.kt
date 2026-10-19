package com.qeetgroup.qeetrix.screenshot

import android.provider.Settings
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.FlowRowScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.github.takahirom.roborazzi.captureRoboImage
import com.qeetgroup.qeetrix.foundation.spacing.QeetrixSpacing
import com.qeetgroup.qeetrix.theme.QeetrixTheme
import com.qeetgroup.qeetrix.theme.QeetrixThemeMode
import org.robolectric.RuntimeEnvironment

/**
 * The render environments screenshot tests use: a representative matrix, not every combination
 * (Documentation/Decisions/0012-screenshot-testing.md). Each test class fixes the device with
 * [SCREENSHOT_QUALIFIERS]; theme, font scale and layout direction are set here, inside the content, so
 * one test can render several configurations.
 */
internal enum class ScreenshotConfig(
    val suffix: String,
    val mode: QeetrixThemeMode,
    val fontScale: Float,
    val direction: LayoutDirection,
) {
    Light("light", QeetrixThemeMode.Light, 1f, LayoutDirection.Ltr),
    Dark("dark", QeetrixThemeMode.Dark, 1f, LayoutDirection.Ltr),
    LargeText("font2x", QeetrixThemeMode.Light, 2f, LayoutDirection.Ltr),
    RightToLeft("rtl", QeetrixThemeMode.Light, 1f, LayoutDirection.Rtl),
}

/**
 * A fixed phone width, 411 dp at xhdpi (320 dpi), English (US). The window is taller than a phone so a
 * component's states at a 2× font scale fit in one image; the capture is the content's own size.
 */
internal const val SCREENSHOT_QUALIFIERS = "en-rUS-w411dp-h2400dp-xhdpi"

/**
 * Records or verifies `src/test/screenshots/<component>/<name>_<config>.png`: [content] drawn on the
 * theme's canvas in the given configuration. Run with `./gradlew recordRoborazziDebug` to (re)record
 * deliberately; every other test run verifies and fails on any difference.
 */
internal fun captureQeetrix(
    component: String,
    name: String,
    config: ScreenshotConfig,
    content: @Composable () -> Unit,
) {
    // Animations off: spinners and indeterminate progress hold still (their reduced-motion form), so every
    // run draws the same frame.
    Settings.Global.putFloat(RuntimeEnvironment.getApplication().contentResolver, Settings.Global.ANIMATOR_DURATION_SCALE, 0f)
    captureRoboImage(filePath = "src/test/screenshots/$component/${name}_${config.suffix}.png") {
        val density = LocalDensity.current
        CompositionLocalProvider(
            LocalDensity provides Density(density.density, config.fontScale),
            LocalLayoutDirection provides config.direction,
        ) {
            QeetrixTheme(config.mode) {
                Box(Modifier.background(QeetrixTheme.colors.surfaceCanvas).padding(QeetrixSpacing.L)) { content() }
            }
        }
    }
}

/**
 * A component's reviewed baselines: `src/test/screenshots/<component>/states_<config>.png`, [content] drawn
 * once in every [ScreenshotConfig]. One image per configuration lays out the states that change the
 * drawing; press and hover need a pointer and are covered by the behaviour tests instead.
 */
internal fun captureStates(
    component: String,
    content: @Composable () -> Unit,
) {
    for (config in ScreenshotConfig.entries) captureQeetrix(component, "states", config, content)
}

/** A gap, for screenshot layouts. */
internal val ScreenshotGap = 12.dp

/** Specimens one under another, as wide as the content area of the screenshot phone (411 dp less margins). */
@Composable
internal fun ScreenshotStack(content: @Composable ColumnScope.() -> Unit) {
    Column(Modifier.width(379.dp), verticalArrangement = Arrangement.spacedBy(ScreenshotGap), content = content)
}

/** Specimens side by side, wrapping onto new lines. */
@Composable
internal fun ScreenshotFlow(content: @Composable FlowRowScope.() -> Unit) {
    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(ScreenshotGap),
        verticalArrangement = Arrangement.spacedBy(ScreenshotGap),
        content = content,
    )
}
