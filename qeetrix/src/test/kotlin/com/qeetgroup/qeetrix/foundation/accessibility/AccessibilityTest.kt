package com.qeetgroup.qeetrix.foundation.accessibility

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.assertHeightIsEqualTo
import androidx.compose.ui.test.assertWidthIsEqualTo
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.unit.dp
import com.qeetgroup.qeetrix.foundation.color.QeetrixColors
import com.qeetgroup.qeetrix.tokens.generated.QeetrixDarkColors
import com.qeetgroup.qeetrix.tokens.generated.QeetrixLightColors
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

/**
 * The WCAG 2.2 AA pairs qeetrix-ui's contrast gate enforces on its semantic colours
 * (`src/__tests__/token-governance.test.ts`, `AA_PAIRS`), measured on the converted Android colours —
 * the same subset qeetrix-ios measures (the web-only shadcn bridge, chart and syntax pairs excepted).
 */
class ContrastTest {
    private class Pair(
        val name: String,
        val foreground: (QeetrixColors) -> Color,
        val background: List<(QeetrixColors) -> Color>,
        val minimum: Double,
    )

    private val surfaces: List<kotlin.Pair<String, (QeetrixColors) -> Color>> =
        listOf(
            "surface.canvas" to { it.surfaceCanvas },
            "surface.default" to { it.surfaceDefault },
            "surface.elevated" to { it.surfaceElevated },
            "surface.overlay" to { it.surfaceOverlay },
            "surface.sunken" to { it.surfaceSunken },
            "surface.subtle" to { it.surfaceSubtle },
            "surface.interactive" to { it.surfaceInteractive },
            "surface.interactive-hover" to { it.surfaceInteractiveHover },
            "surface.brand-subtle" to { it.surfaceBrandSubtle },
            "surface.brand-subtle-hover" to { it.surfaceBrandSubtleHover },
            "surface.rail" to { it.surfaceRail },
        )

    private val pairs: List<Pair> =
        buildList {
            val texts: List<kotlin.Pair<String, (QeetrixColors) -> Color>> =
                listOf(
                    "text.primary" to { it.textPrimary },
                    "text.secondary" to { it.textSecondary },
                    "text.tertiary" to { it.textTertiary },
                    "text.placeholder" to { it.textPlaceholder },
                    "text.link" to { it.textLink },
                    "text.link-hover" to { it.textLinkHover },
                    "text.brand" to { it.textBrand },
                )
            for ((t, text) in texts) for ((s, surface) in surfaces) add(Pair("$t on $s", text, listOf(surface), 4.5))
            for ((s, surface) in surfaces) add(Pair("text.brand-display on $s", { it.textBrandDisplay }, listOf(surface), 3.0))
            for ((n, fill) in listOf<kotlin.Pair<String, (QeetrixColors) -> Color>>(
                "" to { it.actionPrimary },
                "-hover" to { it.actionPrimaryHover },
                "-active" to { it.actionPrimaryActive },
            )) {
                add(Pair("text.on-brand on action.primary$n", { it.textOnBrand }, listOf(fill), 4.5))
            }
            for ((n, fill) in listOf<kotlin.Pair<String, (QeetrixColors) -> Color>>(
                "" to { it.surfaceBrandSubtle },
                "-hover" to { it.surfaceBrandSubtleHover },
                "-active" to { it.surfaceBrandSubtleActive },
            )) {
                add(Pair("text.on-subtle on surface.brand-subtle$n", { it.textOnSubtle }, listOf(fill), 4.5))
            }
            val statuses: List<Triple<(QeetrixColors) -> Color, (QeetrixColors) -> Color, (QeetrixColors) -> Color>> =
                listOf(
                    Triple({ it.textSuccess }, { it.feedbackSuccessSubtle }, { it.feedbackSuccess }),
                    Triple({ it.textWarning }, { it.feedbackWarningSubtle }, { it.feedbackWarning }),
                    Triple({ it.textDanger }, { it.feedbackErrorSubtle }, { it.feedbackError }),
                    Triple({ it.textInfo }, { it.feedbackInfoSubtle }, { it.feedbackInfo }),
                )
            for ((index, status) in statuses.withIndex()) {
                val (text, subtle, solid) = status
                add(Pair("status $index text on surface.default", text, listOf({ it.surfaceDefault }), 4.5))
                add(Pair("status $index text on surface.canvas", text, listOf({ it.surfaceCanvas }), 4.5))
                add(Pair("status $index text on its subtle fill", text, listOf(subtle, { it.surfaceDefault }), 4.5))
                add(Pair("text.on-feedback on status $index fill", { it.textOnFeedback }, listOf(solid), 4.5))
            }
            add(Pair("on-feedback-strong on error-strong", { it.textOnFeedbackStrong }, listOf({ it.feedbackErrorStrong }), 4.5))
            add(Pair("on-feedback-strong on success-strong", { it.textOnFeedbackStrong }, listOf({ it.feedbackSuccessStrong }), 4.5))
            add(Pair("on-feedback-strong on info-strong", { it.textOnFeedbackStrong }, listOf({ it.feedbackInfoStrong }), 4.5))
            add(Pair("on-warning-strong on warning-strong", { it.textOnWarningStrong }, listOf({ it.feedbackWarningStrong }), 4.5))
            for ((b, boundary) in listOf<kotlin.Pair<String, (QeetrixColors) -> Color>>(
                "border.control" to { it.borderControl },
                "border.brand" to { it.borderBrand },
            )) {
                for ((s, surface) in surfaces) add(Pair("$b on $s", boundary, listOf(surface), 3.0))
            }
            for ((n, base) in listOf<kotlin.Pair<String, (QeetrixColors) -> Color>>(
                "overlay" to { it.surfaceOverlay },
                "elevated" to { it.surfaceElevated },
            )) {
                add(Pair("text.on-subtle on brand-subtle over $n", { it.textOnSubtle }, listOf({ it.surfaceBrandSubtle }, base), 4.5))
                add(Pair("text.brand on brand-subtle over $n", { it.textBrand }, listOf({ it.surfaceBrandSubtle }, base), 4.5))
                add(Pair("border.brand on brand-subtle over $n", { it.borderBrand }, listOf({ it.surfaceBrandSubtle }, base), 3.0))
            }
        }

    /** Source-over in gamma-encoded sRGB, as browsers composite and the upstream gate measures. */
    private fun over(
        top: Color,
        bottom: Color,
    ): Color {
        val a = top.alpha
        return Color(top.red * a + bottom.red * (1 - a), top.green * a + bottom.green * (1 - a), top.blue * a + bottom.blue * (1 - a), 1f)
    }

    private fun surface(
        colors: QeetrixColors,
        layers: List<(QeetrixColors) -> Color>,
    ): Color {
        val stack = layers.map { it(colors) }.toMutableList()
        var result = stack.removeAt(stack.lastIndex)
        // A translucent surface is designed to sit on a surface; with none named, the default one.
        if (result.alpha < 1f) result = over(result, colors.surfaceDefault)
        while (stack.isNotEmpty()) result = over(stack.removeAt(stack.lastIndex), result)
        return result
    }

    @Test
    fun everyPairMeetsItsMinimumInBothThemes() {
        assertEquals(142, pairs.size)
        val failures = mutableListOf<String>()
        for ((theme, colors) in listOf("light" to QeetrixLightColors, "dark" to QeetrixDarkColors)) {
            for (pair in pairs) {
                val background = surface(colors, pair.background)
                val ratio = QeetrixAccessibility.contrastRatio(pair.foreground(colors), background)
                if (ratio < pair.minimum) failures += "$theme: ${pair.name} = ${"%.2f".format(ratio)} < ${pair.minimum}"
            }
        }
        assertEquals(emptyList<String>(), failures)
    }

    @Test
    fun contrastRatioFollowsWcag() {
        assertEquals(21.0, QeetrixAccessibility.contrastRatio(Color.Black, Color.White), 1e-6)
        assertEquals(1.0, QeetrixAccessibility.contrastRatio(Color.White, Color.White), 1e-6)
        // Qeet orange on white is below the 4.5:1 text minimum — why text.brand uses qeet.700 in light.
        assertEquals(true, QeetrixAccessibility.contrastRatio(Color(0xFFF26D0E), Color.White) < 4.5)
    }
}

@RunWith(RobolectricTestRunner::class)
class MinimumTouchTargetTest {
    @get:Rule
    val compose = createComposeRule()

    @Test
    fun aSmallElementGetsA48DpTarget() {
        compose.setContent { Box(Modifier.testTag("target").qeetrixMinimumTouchTarget()) { Box(Modifier.size(20.dp)) } }
        compose.onNodeWithTag("target").assertWidthIsEqualTo(48.dp).assertHeightIsEqualTo(48.dp)
    }

    @Test
    fun aLargeElementKeepsItsSize() {
        compose.setContent { Box(Modifier.testTag("target").qeetrixMinimumTouchTarget()) { Box(Modifier.size(64.dp, 56.dp)) } }
        compose.onNodeWithTag("target").assertWidthIsEqualTo(64.dp).assertHeightIsEqualTo(56.dp)
    }
}
