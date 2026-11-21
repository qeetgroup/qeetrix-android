package com.qeetgroup.qeetrix.components.segmented

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.test.assertIsNotSelected
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.getUnclippedBoundsInRoot
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.qeetgroup.qeetrix.components.button.QeetrixButtonSize
import com.qeetgroup.qeetrix.components.input.QeetrixOption
import com.qeetgroup.qeetrix.components.setQeetrix
import com.qeetgroup.qeetrix.foundation.typography.QeetrixTypography
import com.qeetgroup.qeetrix.theme.QeetrixTheme
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.GraphicsMode
import kotlin.math.abs

/** Segmented control: radio buttons, the chosen one selected, a choice reported; full width; the web's label size. */
@RunWith(RobolectricTestRunner::class)
class QeetrixSegmentedControlTest {
    @get:Rule
    val compose = createComposeRule()

    private val who =
        listOf(QeetrixOption("everyone", "Everyone"), QeetrixOption("contacts", "My contacts"), QeetrixOption("nobody", "Nobody"))

    @Test
    fun choosingASegmentReportsIt() {
        var value = "light"
        compose.setQeetrix {
            QeetrixSegmentedControl(value, { value = it }, listOf(QeetrixOption("light", "Light"), QeetrixOption("dark", "Dark")))
        }
        compose.onNodeWithText("Light").assertIsSelected()
        compose.onNodeWithText("Dark").assertIsNotSelected().performClick()
        assertEquals("dark", value)
    }

    /**
     * toggle.css `[data-full-width]`: the strip fills a 300 dp column and its three segments are equal within 1 dp, each
     * label centred on its segment, in either direction; the strip keeps the size's height.
     */
    @Test
    @GraphicsMode(GraphicsMode.Mode.NATIVE)
    fun fullWidthSegmentsShareTheWidthEqually() {
        var direction by mutableStateOf(LayoutDirection.Ltr)
        compose.setContent {
            CompositionLocalProvider(LocalLayoutDirection provides direction) {
                QeetrixTheme {
                    Box(Modifier.width(300.dp)) {
                        QeetrixSegmentedControl(
                            "contacts",
                            {},
                            who,
                            Modifier.testTag("strip"),
                            size = QeetrixButtonSize.Sm,
                            fullWidth = true,
                        )
                    }
                }
            }
        }
        for (each in LayoutDirection.entries) {
            compose.runOnIdle { direction = each }
            val strip = compose.onNodeWithTag("strip").getUnclippedBoundsInRoot()
            assertEquals(300.dp, strip.right - strip.left)
            val segments = who.map { compose.onNodeWithText(it.label).getUnclippedBoundsInRoot() }
            for (segment in segments) assertEquals(100f, (segment.right - segment.left).value, 1f)
            for ((index, option) in who.withIndex()) {
                val label = compose.onNodeWithText(option.label, useUnmergedTree = true).getUnclippedBoundsInRoot()
                val centre = (label.left + label.right) / 2
                val segment = segments[index]
                assertTrue("$each ${option.label} centred", abs((centre - (segment.left + segment.right) / 2).value) < 1f)
            }
            val first = segments[0]
            if (each == LayoutDirection.Rtl) assertEquals(strip.right, first.right) else assertEquals(strip.left, first.left)
        }
    }

    /** A label longer than its share ends in an ellipsis instead of widening the strip. */
    @Test
    @GraphicsMode(GraphicsMode.Mode.NATIVE)
    fun aLongLabelEndsInAnEllipsis() {
        compose.setQeetrix {
            Box(Modifier.width(180.dp)) {
                QeetrixSegmentedControl(
                    "a",
                    {},
                    listOf(QeetrixOption("a", "Everyone in the workspace"), QeetrixOption("b", "Nobody")),
                    Modifier.testTag("strip"),
                    fullWidth = true,
                )
            }
        }
        compose.onNodeWithTag("strip").getUnclippedBoundsInRoot().let { assertEquals(180.dp, it.right - it.left) }
        val layouts = mutableListOf<TextLayoutResult>()
        compose
            .onNodeWithText("Everyone in the workspace", useUnmergedTree = true)
            .fetchSemanticsNode()
            .config[SemanticsActions.GetTextLayoutResult]
            .action
            ?.invoke(layouts)
        assertTrue("ellipsis", layouts.single().isLineEllipsized(0))
    }

    /** Inside the group each segment keeps its own size, as the web's toggles do: label.md at every size. */
    @Test
    fun everySizeDrawsItsLabelsInLabelMd() {
        lateinit var typography: QeetrixTypography
        compose.setQeetrix {
            typography = QeetrixTheme.typography
            QeetrixSegmentedControl("everyone", {}, who, size = QeetrixButtonSize.Sm)
        }
        val layouts = mutableListOf<TextLayoutResult>()
        compose
            .onNodeWithText(
                "Everyone",
                useUnmergedTree = true,
            ).fetchSemanticsNode()
            .config[SemanticsActions.GetTextLayoutResult]
            .action
            ?.invoke(layouts)
        assertEquals(
            typography.labelMd.fontSize,
            layouts
                .single()
                .layoutInput.style.fontSize,
        )
    }
}
