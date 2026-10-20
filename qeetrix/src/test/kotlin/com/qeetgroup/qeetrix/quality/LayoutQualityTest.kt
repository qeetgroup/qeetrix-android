package com.qeetgroup.qeetrix.quality

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.semantics.SemanticsNode
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.getOrNull
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.qeetgroup.qeetrix.theme.QeetrixTheme
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

/**
 * Layout checks at a phone's density (xhdpi) over every catalog specimen, and the localized ones (Hindi, Telugu, Arabic, pseudo-expanded): every
 * control's touch area is at least 48 × 48 dp, and no text is clipped — at the default size, at twice the size
 * (the sample's linear 2×), and at Android 14's non-linear 200 % system setting, in a compact phone width and
 * right to left.
 */
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(qualifiers = "w360dp-h2000dp-xhdpi")
class LayoutQualityTest {
    @get:Rule
    val compose = createComposeRule()

    private val actionable =
        SemanticsMatcher("is actionable") {
            it.config.contains(SemanticsActions.OnClick) && !it.config.contains(SemanticsProperties.Disabled)
        }

    /**
     * Every control reserves a 48 × 48 dp touch area in layout (ADR 0010) — measured on the node's laid-out
     * bounds, not on Compose's hit-test expansion, which a neighbouring control hit first can take away. The
     * controls drawn smaller inside another control are listed in [SmallByDesign], each with its own test of where
     * a touch lands.
     */
    @Test
    fun everyControlReservesA48DpTouchArea() {
        var specimen by mutableStateOf(QualityCatalog.first())
        compose.setContent { QeetrixTheme { Box(Modifier.padding(16.dp)) { specimen.content() } } }
        val small = mutableListOf<String>()
        for (each in QualityCatalog + LocalizedCatalog) {
            compose.runOnIdle { specimen = each }
            for (node in compose.onAllNodes(actionable).fetchSemanticsNodes()) {
                val bounds = node.boundsInRoot
                val (width, height) = with(compose.density) { bounds.width.toDp() to bounds.height.toDp() }
                if (width < 47.5.dp || height < 47.5.dp) small += "${each.name}: ${node.describe()}"
                if (width < 47.5.dp || height < 47.5.dp) println("TOUCH ${each.name}: ${node.describe()} is $width × $height")
            }
        }
        assertEquals(SmallByDesign.sorted(), small.distinct().sorted())
    }

    @Test
    fun noTextIsClippedAtTheDefaultOrALinearDoubleSize() {
        var scale by mutableStateOf(1f)
        var specimen by mutableStateOf(QualityCatalog.first())
        compose.setContent {
            val base = LocalDensity.current
            CompositionLocalProvider(LocalDensity provides Density(base.density, scale)) {
                QeetrixTheme { Box(Modifier.width(CompactWidth).padding(16.dp)) { specimen.content() } }
            }
        }
        val clipped = mutableListOf<String>()
        for (each in listOf(1f, 2f)) {
            compose.runOnIdle { scale = each }
            clipped += scan("linear $each×") { specimen = it }
        }
        assertTrue(clipped.joinToString("\n"), clipped.isEmpty())
    }

    /** Composed right to left from the start, as a right-to-left app renders: not switched from left to right. */
    @Test
    fun noTextIsClippedRightToLeft() {
        var scale by mutableStateOf(1f)
        var specimen by mutableStateOf(QualityCatalog.first())
        compose.setContent {
            val base = LocalDensity.current
            CompositionLocalProvider(
                LocalDensity provides Density(base.density, scale),
                LocalLayoutDirection provides LayoutDirection.Rtl,
            ) {
                QeetrixTheme { Box(Modifier.width(CompactWidth).padding(16.dp)) { specimen.content() } }
            }
        }
        val clipped = mutableListOf<String>()
        for (each in listOf(1f, 2f)) {
            compose.runOnIdle { scale = each }
            clipped += scan("right to left, $each×") { specimen = it }
        }
        assertTrue(clipped.joinToString("\n"), clipped.isEmpty())
    }

    @Test
    fun noTextIsClippedAtAndroidsNonLinear200PercentSetting() {
        // The system font scale: Compose converts sp with Android 14's non-linear curve from this configuration.
        RuntimeEnvironment.setFontScale(2f)
        var specimen by mutableStateOf(QualityCatalog.first())
        compose.setContent { QeetrixTheme { Box(Modifier.width(CompactWidth).padding(16.dp)) { specimen.content() } } }
        val clipped = scan("system 200 %") { specimen = it }
        assertTrue(clipped.joinToString("\n"), clipped.isEmpty())
    }

    /** The scans detect what they look for: a 24 dp control, and text cut off by a box too small for it. */
    @Test
    fun theScansCatchKnownProblems() {
        compose.setContent {
            QeetrixTheme {
                Column {
                    Box(Modifier.size(24.dp).clickable {}.semantics { contentDescription = "Tiny" })
                    Box(Modifier.size(width = 40.dp, height = 12.dp)) { BasicText("Text that cannot fit in this box") }
                }
            }
        }
        val tiny = compose.onAllNodes(actionable).fetchSemanticsNodes().single()
        assertTrue(with(compose.density) { tiny.boundsInRoot.width.toDp() } < 47.5.dp)
        assertTrue(scan("control", listOf(QualitySpecimen("control") {})) { }.isNotEmpty())
    }

    /**
     * Shows each specimen through [show] and reports every text that doesn't show in full. A navigation bar's
     * labels share the bar's width and end in an ellipsis by design (ADR 0015); they are read in full.
     */
    private fun scan(
        configuration: String,
        specimens: List<QualitySpecimen> = QualityCatalog + LocalizedCatalog,
        show: (QualitySpecimen) -> Unit,
    ): List<String> {
        val clipped = mutableListOf<String>()
        for (each in specimens) {
            compose.runOnIdle { show(each) }
            for (node in compose
                .onAllNodes(SemanticsMatcher.keyIsDefined(SemanticsActions.GetTextLayoutResult), useUnmergedTree = true)
                .fetchSemanticsNodes()) {
                val layouts = mutableListOf<TextLayoutResult>()
                node.config[SemanticsActions.GetTextLayoutResult].action?.invoke(layouts)
                val layout = layouts.firstOrNull() ?: continue
                val byDesign = each.name == "navigation-bar" && (0 until layout.lineCount).any { layout.isLineEllipsized(it) }
                if (layout.isClipped(with(compose.density) { 1.5.dp.toPx() }) && !byDesign) {
                    clipped += "${each.name} [$configuration]: \"${layout.layoutInput.text}\" ${layout.clipping()}"
                }
            }
        }
        println(clipped.joinToString("\n", prefix = "CLIPPING [$configuration] (${clipped.size})\n"))
        return clipped
    }

    private fun SemanticsNode.describe(): String {
        val text = config.getOrNull(SemanticsProperties.Text)?.joinToString { it.text }
        val description = config.getOrNull(SemanticsProperties.ContentDescription)?.joinToString()
        val role = config.getOrNull(SemanticsProperties.Role)
        return listOfNotNull(role?.toString(), text, description).joinToString(" / ").ifEmpty { "node $id" }
    }
}

/** A compact phone: 360 dp less the 16 dp margins of a screen. */
private val CompactWidth = 328.dp

/**
 * Controls drawn smaller than 48 dp inside or beside another control, at the web's size, which take touches within
 * 48 dp through Compose's hit-test expansion — each proven by `TouchLandingTest`.
 */
private val SmallByDesign: List<String> =
    listOf(
        "chip: Button / Remove Owner: Ada",
        "number-field: Button / Decrease",
        "number-field: Button / Increase",
        "password-input: Button / Show password",
        "search-field: Button / Clear",
        "tabs: Tab / Activity",
        "tabs: Tab / Overview",
        "arabic: Tab / النشاط",
        "arabic: Tab / نظرة عامة",
        "pseudo-expanded: Tab / [Åçtîvîtý on]",
        "pseudo-expanded: Tab / [Övérvîéw on]",
    )
