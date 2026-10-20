package com.qeetgroup.qeetrix.quality

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.style.ResolvedTextDirection
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.qeetgroup.qeetrix.components.badge.QeetrixBadge
import com.qeetgroup.qeetrix.components.badge.QeetrixChip
import com.qeetgroup.qeetrix.components.button.QeetrixButton
import com.qeetgroup.qeetrix.components.dropdownmenu.MenuSurface
import com.qeetgroup.qeetrix.components.dropdownmenu.QeetrixMenuItem
import com.qeetgroup.qeetrix.components.feed.QeetrixTimeline
import com.qeetgroup.qeetrix.components.feed.QeetrixTimelineItem
import com.qeetgroup.qeetrix.components.input.QeetrixTextField
import com.qeetgroup.qeetrix.components.navigation.QeetrixNavigationBar
import com.qeetgroup.qeetrix.components.navigation.QeetrixNavigationItem
import com.qeetgroup.qeetrix.components.tabs.QeetrixTab
import com.qeetgroup.qeetrix.components.tabs.QeetrixTabs
import com.qeetgroup.qeetrix.icons.QeetrixIconName
import com.qeetgroup.qeetrix.theme.QeetrixTheme
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

/**
 * Phase 6 regression: in a right-to-left layout, Compose laid a left-to-right label out as a right-to-left paragraph,
 * and Android measured some of them a few pixels short — "[Çàñçél o]" ended in an ellipsis in a full-width button,
 * chips lost their first characters, a tab broke onto two lines (seen on an API 36 device and in the JVM
 * screenshots). Labels now take their direction from their text; a menu item, wider than its text, also aligns to the
 * layout's start.
 */
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(qualifiers = "w411dp-h900dp-xhdpi")
class RightToLeftLabelTest {
    @get:Rule
    val compose = createComposeRule()

    private fun layoutOf(text: String): TextLayoutResult {
        val node =
            compose
                .onAllNodes(SemanticsMatcher.keyIsDefined(SemanticsActions.GetTextLayoutResult), useUnmergedTree = true)
                .fetchSemanticsNodes()
                .first { node ->
                    val layouts = mutableListOf<TextLayoutResult>()
                    node.config[SemanticsActions.GetTextLayoutResult].action?.invoke(layouts)
                    layouts
                        .firstOrNull()
                        ?.layoutInput
                        ?.text
                        ?.text == text
                }
        val layouts = mutableListOf<TextLayoutResult>()
        node.config[SemanticsActions.GetTextLayoutResult].action?.invoke(layouts)
        return layouts.first()
    }

    @Test
    fun labelsTakeTheirDirectionFromTheirText() {
        val labels = listOf(pseudo("Cancel"), pseudo("Closed"), pseudo("Overview"), pseudo("Shared"), "Inbox", "Work email")
        compose.setContent {
            CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                QeetrixTheme {
                    Column {
                        QeetrixButton(labels[0], onClick = {})
                        QeetrixChip(labels[1], selected = false, onSelectedChange = {})
                        QeetrixTabs(listOf(QeetrixTab("a", labels[2])), selected = "a", onSelect = {})
                        QeetrixBadge(labels[3])
                        QeetrixNavigationBar(
                            listOf(QeetrixNavigationItem("inbox", labels[4], QeetrixIconName.Inbox)),
                            selected = "inbox",
                            onSelect = {},
                        )
                        QeetrixTextField(TextFieldState(), label = labels[5])
                        QeetrixButton("إلغاء", onClick = {})
                    }
                }
            }
        }
        for (label in labels) {
            val layout = layoutOf(label)
            assertEquals(label, TextDirection.Content, layout.layoutInput.style.textDirection)
            assertEquals(label, ResolvedTextDirection.Ltr, layout.getParagraphDirection(0))
            assertEquals(label, 1, layout.lineCount)
        }
        // Text that is right to left stays so.
        assertEquals(ResolvedTextDirection.Rtl, layoutOf("إلغاء").getParagraphDirection(0))
        // A centred label stays centred.
        assertEquals(TextAlign.Center, layoutOf(labels[4]).layoutInput.style.textAlign)
    }

    /**
     * A menu item is wider than its text: it starts at the layout's start, so English and Arabic items line up. The
     * surface is drawn in place, as in the menu's screenshots: a popup window takes its direction from the
     * configuration, not from the composition.
     */
    @Test
    fun aMenuItemAlignsToTheLayoutsStart() {
        compose.setContent {
            CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                QeetrixTheme {
                    MenuSurface(Modifier.width(240.dp), focusFirst = false, onDismiss = {}) {
                        QeetrixMenuItem("Rename", onClick = {})
                        QeetrixMenuItem("حذف", onClick = {})
                    }
                }
            }
        }
        assertEquals(TextAlign.Right, layoutOf("Rename").layoutInput.style.textAlign)
        assertEquals(ResolvedTextDirection.Ltr, layoutOf("Rename").getParagraphDirection(0))
        assertEquals(ResolvedTextDirection.Rtl, layoutOf("حذف").getParagraphDirection(0))
    }

    /**
     * At a large font scale a timeline's time moves below its title and fills the column: it aligns to the layout's
     * start whatever its script, as the title does (an Arabic time in an English screen stayed at the right edge).
     */
    @Test
    fun aTimeBelowItsTitleAlignsToTheLayoutsStart() {
        compose.setContent {
            val density = LocalDensity.current
            CompositionLocalProvider(LocalDensity provides Density(density.density, 2f)) {
                QeetrixTheme {
                    QeetrixTimeline { QeetrixTimelineItem("Payment received", time = "اليوم") }
                }
            }
        }
        assertEquals(TextAlign.Left, layoutOf("اليوم").layoutInput.style.textAlign)
        assertEquals(ResolvedTextDirection.Rtl, layoutOf("اليوم").getParagraphDirection(0))
    }
}
