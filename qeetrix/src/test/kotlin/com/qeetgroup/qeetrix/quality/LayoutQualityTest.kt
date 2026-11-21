package com.qeetgroup.qeetrix.quality

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.toRect
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.findRootCoordinates
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toSize
import com.qeetgroup.qeetrix.foundation.accessibility.QeetrixAccessibility
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
 * Layout checks at a phone's density (xhdpi) over every catalog specimen, the localized ones (Hindi, Telugu, Arabic,
 * pseudo-expanded) and the overlays in their own windows: every control's touch area is at least 48 × 48 dp, and no
 * text is clipped — at the default size, at twice the size (the sample's linear 2×), at Android 14's non-linear
 * 200 % system setting, in a compact phone width and right to left. A window does not take a density given to the
 * composition, so the overlays' windows are checked at the default size, at the system's 200 % and right to left; the
 * same surfaces drawn in place ([QualityCatalog]) are checked at the linear 2× too.
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
     * Every control has a 48 × 48 dp touch area (ADR 0010): the area Compose sends its taps to — its laid-out bounds,
     * grown to 48 dp each way by touch-target expansion where it is drawn smaller, cut by any clipping ancestor and by the
     * window's edge. A control whose area is cut under 48 dp, or
     * falls off the window, is reported. Neighbours' areas may overlap (Compose sends a tap there to the nearer one), so
     * overlap is not checked. A control a scrolling list has partly scrolled away is not measured. The ones that are by
     * design are listed in [SmallByDesign], each with its own test of where a touch lands (`TouchLandingTest`); the
     * ones that are findings, in [SmallFindings].
     */
    @Test
    fun everyControlHasA48DpTouchArea() {
        var specimen by mutableStateOf(QualityCatalog.first())
        compose.setContent { QeetrixTheme { Box(Modifier.padding(16.dp)) { specimen.content() } } }
        val small = mutableListOf<String>()
        for (each in QualityCatalog + LocalizedCatalog + OverlayCatalog) {
            compose.runOnIdle { specimen = each }
            compose.waitForIdle()
            for ((node, area) in smallTouchAreas(compose.onAllNodes(actionable).fetchSemanticsNodes())) {
                small += "${each.name}: ${node.describe()}"
                println("TOUCH ${each.name}: ${node.describe()} has ${area.width} × ${area.height} px to tap")
            }
        }
        assertEquals((SmallByDesign + SmallFindings.keys).sorted(), small.distinct().sorted())
    }

    /**
     * The controls among [nodes] whose touch area is under 48 dp in either direction, with that area: the laid-out
     * bounds grown to 48 dp each way, centred, as Compose's touch-target expansion grows them, then cut by the window and
     * by every ancestor that clips its content and is itself at least 48 dp each way (a clip Compose's hit test doesn't
     * reach past; a smaller one it does). Controls partly scrolled out of view are left out.
     */
    private fun smallTouchAreas(nodes: List<SemanticsNode>): List<Pair<SemanticsNode, Rect>> {
        val target = with(compose.density) { QeetrixAccessibility.MinimumTouchTarget.toPx() }
        val minimum = with(compose.density) { (QeetrixAccessibility.MinimumTouchTarget - 0.5.dp).toPx() }
        return nodes.mapNotNull { node ->
            val laidOut = Rect(node.positionInRoot, node.size.toSize())
            val shown = node.boundsInRoot
            // Partly scrolled away by a list: its area is the list's business, not the control's.
            if (shown.width < laidOut.width - 1f || shown.height < laidOut.height - 1f) return@mapNotNull null
            val grow = { length: Float -> ((target - length) / 2).coerceAtLeast(0f) }
            var area =
                Rect(
                    laidOut.left - grow(laidOut.width),
                    laidOut.top - grow(laidOut.height),
                    laidOut.right + grow(laidOut.width),
                    laidOut.bottom + grow(laidOut.height),
                ).intersect(
                    node.layoutInfo.coordinates
                        .findRootCoordinates()
                        .size
                        .toSize()
                        .toRect(),
                )
            var ancestor = node.layoutInfo.parentInfo
            while (ancestor != null) {
                for (info in ancestor.getModifierInfo()) {
                    val clips = "clip=true" in info.modifier.toString()
                    val big = info.coordinates.size.width >= target && info.coordinates.size.height >= target
                    if (clips && big) area = area.intersect(info.coordinates.boundsInRoot())
                }
                ancestor = ancestor.parentInfo
            }
            if (area.width >= minimum && area.height >= minimum) null else node to area
        }
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
        compose.runOnIdle { scale = 1f }
        clipped += scan("linear 1×", QualityCatalog + LocalizedCatalog + OverlayCatalog) { specimen = it }
        compose.runOnIdle { scale = 2f }
        clipped += scan("linear 2×") { specimen = it }
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
        compose.runOnIdle { scale = 1f }
        clipped += scan("right to left, 1×", QualityCatalog + LocalizedCatalog + OverlayCatalog) { specimen = it }
        compose.runOnIdle { scale = 2f }
        clipped += scan("right to left, 2×") { specimen = it }
        assertTrue(clipped.joinToString("\n"), clipped.isEmpty())
    }

    @Test
    fun noTextIsClippedAtAndroidsNonLinear200PercentSetting() {
        // The system font scale: Compose converts sp with Android 14's non-linear curve from this configuration, in
        // the app's window and in every overlay's.
        RuntimeEnvironment.setFontScale(2f)
        var specimen by mutableStateOf(QualityCatalog.first())
        compose.setContent { QeetrixTheme { Box(Modifier.width(CompactWidth).padding(16.dp)) { specimen.content() } } }
        val clipped = scan("system 200 %", QualityCatalog + LocalizedCatalog + OverlayCatalog) { specimen = it }
        assertTrue(clipped.joinToString("\n"), clipped.isEmpty())
        // Every recorded finding still happens here; one that no longer does is fixed, and leaves the list.
        assertEquals(ClippingFindings.values.sorted(), findingsSeen.sorted())
    }

    /**
     * The scans detect what they look for: a 24 dp control, text cut off by a box too small for it, and text that
     * ends in an ellipsis.
     */
    @Test
    fun theScansCatchKnownProblems() {
        compose.setContent {
            QeetrixTheme {
                Column {
                    Box(Modifier.size(24.dp).clickable {}.semantics { contentDescription = "Tiny" })
                    Box(Modifier.size(width = 40.dp, height = 12.dp)) { BasicText("Text that cannot fit in this box") }
                    Box(
                        Modifier.width(40.dp),
                    ) { BasicText("Text that ends in an ellipsis", maxLines = 1, overflow = TextOverflow.Ellipsis) }
                }
            }
        }
        val tiny = compose.onAllNodes(actionable).fetchSemanticsNodes().single()
        assertTrue(with(compose.density) { tiny.size.width.toDp() } < 47.5.dp)
        val found = scan("control", listOf(QualitySpecimen("control") {})) { }
        assertEquals(found.joinToString("\n"), 2, found.size)
    }

    /**
     * The touch scan catches a 24 dp control at the window's corner, whose 48 dp area falls off the window, and one at a
     * clipping box's corner, whose area the box cuts; it passes two 24 dp controls 8 dp apart, whose areas overlap but
     * are each 48 dp.
     */
    @Test
    fun theTouchScanCatchesCutAreasButNotOverlap() {
        compose.setContent {
            QeetrixTheme {
                Box(Modifier.size(360.dp, 600.dp)) {
                    Box(Modifier.size(24.dp).clickable {}.semantics { contentDescription = "Corner" })
                    Box(Modifier.offset(100.dp, 100.dp).size(200.dp, 100.dp).clipToBounds()) {
                        Box(Modifier.size(24.dp).clickable {}.semantics { contentDescription = "Clipped" })
                    }
                    Row(Modifier.offset(100.dp, 300.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Box(Modifier.size(24.dp).clickable {}.semantics { contentDescription = "First" })
                        Box(Modifier.size(24.dp).clickable {}.semantics { contentDescription = "Second" })
                    }
                }
            }
        }
        val found = smallTouchAreas(compose.onAllNodes(actionable).fetchSemanticsNodes()).map { it.first.describe() }
        assertEquals(listOf("Corner", "Clipped"), found)
    }

    /** The recorded clipping findings the scans have come across. */
    private val findingsSeen = mutableSetOf<String>()

    /**
     * Shows each specimen through [show] and reports every text that doesn't show in full, but for the texts that end
     * in an ellipsis by design and the recorded findings (`EllipsizedByDesign` and `ClippingFindings`, which the
     * device's scan shares).
     */
    private fun scan(
        configuration: String,
        specimens: List<QualitySpecimen> = QualityCatalog + LocalizedCatalog,
        show: (QualitySpecimen) -> Unit,
    ): List<String> {
        val clipped = mutableListOf<String>()
        for (each in specimens) {
            compose.runOnIdle { show(each) }
            compose.waitForIdle()
            for (node in compose
                .onAllNodes(SemanticsMatcher.keyIsDefined(SemanticsActions.GetTextLayoutResult), useUnmergedTree = true)
                .fetchSemanticsNodes()) {
                val layouts = mutableListOf<TextLayoutResult>()
                node.config[SemanticsActions.GetTextLayoutResult].action?.invoke(layouts)
                val layout = layouts.firstOrNull() ?: continue
                if (!layout.isClipped(with(compose.density) { 1.5.dp.toPx() }) || layout.isEllipsizedByDesign(each.name)) continue
                val finding = layout.clippingFinding(each.name)
                if (finding != null) {
                    findingsSeen += finding
                } else {
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
        // A link in text is a child of its text, with no words of its own: named by the sentence it is in.
        val sentence = parent?.config?.getOrNull(SemanticsProperties.Text)?.joinToString { it.text }
        return listOfNotNull(role?.toString(), text, description).joinToString(" / ").ifEmpty {
            sentence?.let { "Link in $it" } ?: "node $id"
        }
    }
}

/** A compact phone: 360 dp less the 16 dp margins of a screen. */
private val CompactWidth = 328.dp

/**
 * Controls whose touch area is cut under 48 dp by design, each with a test of where a tap beside it lands
 * (`TouchLandingTest`). None: every control laid out under 48 dp — a field's tools, a number field's steppers, a chip's
 * remove button, tabs in their band, links in text, buttons, icon buttons, the bare switch, checkbox and slider — takes
 * a whole 48 dp area through touch-target expansion, overlapping a neighbour's where they sit closer than that.
 */
private val SmallByDesign: List<String> = emptyList()

/**
 * Controls under 48 dp that are recorded findings, not by design, each excluded here by name until the component
 * changes: the control, and the finding. None now: Q-6, Material's 12 dp clickable handle row on the bottom sheet, went
 * with Material's sheet.
 */
private val SmallFindings: Map<String, String> = emptyMap()
