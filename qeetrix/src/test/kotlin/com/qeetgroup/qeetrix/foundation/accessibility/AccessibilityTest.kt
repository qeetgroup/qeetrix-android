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
 * The WCAG 2.2 AA pairings the design rules rely on, measured on the converted Android colours: text at
 * 4.5:1, icons, borders, field lines and the focus frame at 3:1 (design rules §20), on the surfaces and
 * fills they are used on — including the field-line pairs on `bg.neutral.subtle` and `-hover` that design
 * rules §19 added, and brand text on the brand tints (@qeetrix/tokens README, "What you can rely on").
 */
private typealias Role = kotlin.Pair<String, (QeetrixColors) -> Color>

class ContrastTest {
    private class Pair(
        val name: String,
        val foreground: (QeetrixColors) -> Color,
        val background: List<(QeetrixColors) -> Color>,
        val minimum: Double,
    )

    private val surfaces: List<Role> =
        listOf(
            "surface.canvas" to { it.surfaceCanvas },
            "surface.default" to { it.surfaceDefault },
            "surface.raised" to { it.surfaceRaised },
            "surface.overlay" to { it.surfaceOverlay },
            "surface.sunken" to { it.surfaceSunken },
        )

    private val pairs: List<Pair> =
        buildList {
            val texts: List<Role> =
                listOf(
                    "text.default" to { it.textDefault },
                    "text.subtle" to { it.textSubtle },
                    "text.subtlest" to { it.textSubtlest },
                    "text.brand" to { it.textBrand },
                    "text.link" to { it.textLink },
                    "text.link-hover" to { it.textLinkHover },
                    "text.danger" to { it.textDanger },
                    "text.success" to { it.textSuccess },
                    "text.warning" to { it.textWarning },
                    "text.info" to { it.textInfo },
                )
            for ((t, text) in texts) for ((s, surface) in surfaces) add(Pair("$t on $s", text, listOf(surface), 4.5))
            val icons: List<Role> =
                listOf(
                    "icon.default" to { it.iconDefault },
                    "icon.subtle" to { it.iconSubtle },
                    "icon.brand" to { it.iconBrand },
                    "icon.danger" to { it.iconDanger },
                    "icon.success" to { it.iconSuccess },
                    "icon.warning" to { it.iconWarning },
                    "icon.info" to { it.iconInfo },
                )
            for ((i, icon) in icons) for ((s, surface) in surfaces) add(Pair("$i on $s", icon, listOf(surface), 3.0))
            // Field lines, control borders and the focus frame (§8, §19, §20), on surfaces and on the field fill.
            val boundaries: List<Role> =
                listOf(
                    "border.strong" to { it.borderStrong },
                    "border.focus" to { it.borderFocus },
                    "border.danger" to { it.borderDanger },
                    "border.brand" to { it.borderBrand },
                )
            val fieldFills: List<Role> =
                listOf(
                    "bg.neutral.subtle" to { it.bgNeutralSubtle },
                    "bg.neutral.subtle-hover" to { it.bgNeutralSubtleHover },
                )
            for ((b, boundary) in boundaries) {
                for ((s, surface) in surfaces + fieldFills) {
                    add(
                        Pair("$b on $s", boundary, listOf(surface), 3.0),
                    )
                }
            }
            // Field text on the field fill.
            for ((t, text) in texts.take(3)) for ((f, fill) in fieldFills) add(Pair("$t on $f", text, listOf(fill), 4.5))
            // Labels on solid fills: buttons, solid badges, the inverse surface.
            val onFills: List<kotlin.Triple<String, (QeetrixColors) -> Color, (QeetrixColors) -> Color>> =
                listOf(
                    Triple("text.on-brand on bg.brand.bold", { it.textOnBrand }, { it.bgBrandBold }),
                    Triple("text.on-brand on bg.brand.bold-hover", { it.textOnBrand }, { it.bgBrandBoldHover }),
                    Triple("text.on-brand on bg.brand.bold-pressed", { it.textOnBrand }, { it.bgBrandBoldPressed }),
                    Triple("text.inverse on bg.neutral.bold", { it.textInverse }, { it.bgNeutralBold }),
                    Triple("text.inverse on bg.neutral.bold-hover", { it.textInverse }, { it.bgNeutralBoldHover }),
                    Triple("text.inverse on bg.neutral.bold-pressed", { it.textInverse }, { it.bgNeutralBoldPressed }),
                    Triple("text.on-bold on bg.danger.bold", { it.textOnBold }, { it.bgDangerBold }),
                    Triple("text.on-bold on bg.danger.bold-hover", { it.textOnBold }, { it.bgDangerBoldHover }),
                    Triple("text.on-bold on bg.danger.bold-pressed", { it.textOnBold }, { it.bgDangerBoldPressed }),
                    Triple("text.on-bold on bg.success.bold", { it.textOnBold }, { it.bgSuccessBold }),
                    Triple("text.on-bold on bg.info.bold", { it.textOnBold }, { it.bgInfoBold }),
                    Triple("text.on-warning on bg.warning.bold", { it.textOnWarning }, { it.bgWarningBold }),
                    Triple("text.inverse on surface.inverse", { it.textInverse }, { it.surfaceInverse }),
                )
            for ((name, text, fill) in onFills) add(Pair(name, text, listOf(fill), 4.5))
            // Status tags and ghost buttons: each status text on its own tint; brand text on the brand tints.
            val tints: List<kotlin.Triple<String, (QeetrixColors) -> Color, (QeetrixColors) -> Color>> =
                listOf(
                    Triple("text.default on bg.neutral.subtle-pressed", { it.textDefault }, { it.bgNeutralSubtlePressed }),
                    Triple("text.brand on bg.brand.subtle", { it.textBrand }, { it.bgBrandSubtle }),
                    Triple("text.brand on bg.brand.subtle-hover", { it.textBrand }, { it.bgBrandSubtleHover }),
                    Triple("text.brand on bg.brand.subtle-pressed", { it.textBrand }, { it.bgBrandSubtlePressed }),
                    Triple("text.brand on bg.selected", { it.textBrand }, { it.bgSelected }),
                    Triple("text.brand on bg.selected-hover", { it.textBrand }, { it.bgSelectedHover }),
                    Triple("text.default on bg.selected", { it.textDefault }, { it.bgSelected }),
                    Triple("text.info on bg.info.subtle", { it.textInfo }, { it.bgInfoSubtle }),
                    Triple("text.success on bg.success.subtle", { it.textSuccess }, { it.bgSuccessSubtle }),
                    Triple("text.warning on bg.warning.subtle", { it.textWarning }, { it.bgWarningSubtle }),
                    Triple("text.danger on bg.danger.subtle", { it.textDanger }, { it.bgDangerSubtle }),
                    Triple("text.subtle on bg.neutral.subtle-hover", { it.textSubtle }, { it.bgNeutralSubtleHover }),
                    Triple("text.danger on bg.neutral.subtle-hover", { it.textDanger }, { it.bgNeutralSubtleHover }),
                )
            for ((name, text, fill) in tints) add(Pair(name, text, listOf(fill), 4.5))
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
        assertEquals(145, pairs.size)
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
        // Qeet orange on white is below the 4.5:1 text minimum — why light text.brand is qeet.800.
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
