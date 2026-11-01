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
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.style.ResolvedTextDirection
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.qeetgroup.qeetrix.components.accordion.QeetrixCollapsibleTrigger
import com.qeetgroup.qeetrix.components.badge.QeetrixBadge
import com.qeetgroup.qeetrix.components.badge.QeetrixChip
import com.qeetgroup.qeetrix.components.button.QeetrixButton
import com.qeetgroup.qeetrix.components.checkbox.QeetrixCheckbox
import com.qeetgroup.qeetrix.components.descriptionlist.QeetrixDescriptionItem
import com.qeetgroup.qeetrix.components.descriptionlist.QeetrixDescriptionList
import com.qeetgroup.qeetrix.components.dropdownmenu.MenuSurface
import com.qeetgroup.qeetrix.components.dropdownmenu.QeetrixMenuItem
import com.qeetgroup.qeetrix.components.feed.QeetrixTimeline
import com.qeetgroup.qeetrix.components.feed.QeetrixTimelineItem
import com.qeetgroup.qeetrix.components.input.QeetrixOption
import com.qeetgroup.qeetrix.components.input.QeetrixTextField
import com.qeetgroup.qeetrix.components.list.QeetrixListItem
import com.qeetgroup.qeetrix.components.navigation.QeetrixNavigationBar
import com.qeetgroup.qeetrix.components.navigation.QeetrixNavigationItem
import com.qeetgroup.qeetrix.components.select.OptionListSurface
import com.qeetgroup.qeetrix.components.select.SelectSection
import com.qeetgroup.qeetrix.components.tabs.QeetrixTab
import com.qeetgroup.qeetrix.components.tabs.QeetrixTabs
import com.qeetgroup.qeetrix.icons.QeetrixIconName
import com.qeetgroup.qeetrix.theme.QeetrixTheme
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
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
 * screenshots). Labels now take their direction from their text; a label wider than its text — a menu item, a
 * choice's label, a select option, a description list's term and value — also aligns to the layout's start.
 */
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(qualifiers = "w411dp-h900dp-xhdpi")
class RightToLeftLabelTest {
    @get:Rule
    val compose = createComposeRule()

    private fun layoutOf(text: String): TextLayoutResult {
        val layouts =
            compose
                .onAllNodes(SemanticsMatcher.keyIsDefined(SemanticsActions.GetTextLayoutResult), useUnmergedTree = true)
                .fetchSemanticsNodes()
                .mapNotNull { node ->
                    val results = mutableListOf<TextLayoutResult>()
                    node.config[SemanticsActions.GetTextLayoutResult].action?.invoke(results)
                    results.firstOrNull()
                }.filter { it.layoutInput.text.text == text }
        assertEquals("one text \"$text\"", 1, layouts.size)
        return layouts.single()
    }

    @Test
    fun labelsTakeTheirDirectionFromTheirText() {
        val labels =
            listOf(
                pseudo("Cancel"),
                pseudo("Closed"),
                pseudo("Overview"),
                pseudo("Shared"),
                "Inbox",
                "Work email",
                pseudo("Advanced options"),
                "2.4 MB",
            )
        compose.setContent {
            CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                QeetrixTheme {
                    Column {
                        QeetrixButton(labels[0], onClick = {})
                        QeetrixChip(labels[1], selected = false, onSelectedChange = {})
                        QeetrixTabs(listOf(QeetrixTab("a", labels[2])), value = "a", onValueChange = {})
                        QeetrixBadge(labels[3])
                        QeetrixNavigationBar(
                            listOf(QeetrixNavigationItem("inbox", labels[4], QeetrixIconName.Inbox)),
                            value = "inbox",
                            onValueChange = {},
                        )
                        QeetrixTextField(TextFieldState(), label = labels[5])
                        QeetrixCollapsibleTrigger(labels[6], open = false, onOpenChange = {})
                        QeetrixListItem("تقرير", metadata = labels[7])
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
     * Labels wider than their text start at the layout's start, so English and Arabic ones line up: a menu item, a
     * choice's label, a select option, a description list's term and value. The menu's and the select's surfaces are
     * drawn in place, as in their screenshots: a popup window takes its direction from the configuration, not from the
     * composition.
     */
    @Test
    fun aLabelWiderThanItsTextAlignsToTheLayoutsStart() {
        val english = listOf("Rename", "Remember this device", "India", "Plan", "Team")
        compose.setContent {
            CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                QeetrixTheme {
                    Column(Modifier.width(320.dp)) {
                        MenuSurface(Modifier.width(240.dp), focusFirst = false, onDismiss = {}) {
                            QeetrixMenuItem("Rename", onClick = {})
                            QeetrixMenuItem("حذف", onClick = {})
                        }
                        QeetrixCheckbox(true, {}, label = "Remember this device", modifier = Modifier.width(300.dp))
                        OptionListSurface(
                            listOf(SelectSection(null, listOf(QeetrixOption("in", "India"), QeetrixOption("ae", "الإمارات")))),
                            { it == "in" },
                            focusSelected = false,
                            title = "Country",
                            onDismiss = {},
                            onChoose = {},
                            modifier = Modifier.width(240.dp),
                        )
                        QeetrixDescriptionList { QeetrixDescriptionItem("Plan", "Team") }
                    }
                }
            }
        }
        for (label in english) {
            val layout = layoutOf(label)
            assertEquals(label, TextAlign.Right, layout.layoutInput.style.textAlign)
            assertEquals(label, ResolvedTextDirection.Ltr, layout.getParagraphDirection(0))
        }
        // Right-to-left labels stay right to left, and start at the same edge as the English ones.
        for (label in listOf("حذف", "الإمارات")) {
            assertEquals(label, ResolvedTextDirection.Rtl, layoutOf(label).getParagraphDirection(0))
            assertEquals(label, TextAlign.Right, layoutOf(label).layoutInput.style.textAlign)
        }
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

    /**
     * A timeline's title and description fill the event's column and start at the layout's start: measured to its own
     * width, an Arabic description in a left-to-right screen ("بتحويل مصرفي.") broke onto a second line it did not
     * need, as Android lays a right-to-left paragraph out a little wider than it measures it (finding Q-11).
     */
    @Test
    fun anArabicDescriptionInALeftToRightTimelineTakesOneLine() {
        compose.setContent {
            QeetrixTheme {
                Column(Modifier.width(379.dp)) {
                    QeetrixTimeline { QeetrixTimelineItem("تم استلام الدفعة", time = "اليوم", description = "بتحويل مصرفي.") }
                    QeetrixTimeline { QeetrixTimelineItem("Payment received", time = "Today", description = "By bank transfer.") }
                }
            }
        }
        val english =
            compose
                .onNode(hasText("By bank transfer."), useUnmergedTree = true)
                .fetchSemanticsNode()
                .size.height
        val arabic =
            compose
                .onNode(hasText("بتحويل مصرفي."), useUnmergedTree = true)
                .fetchSemanticsNode()
                .size.height
        // One line of Arabic is a little taller than one of English (the fallback font's metrics); two are twice as tall.
        assertTrue("the Arabic description is $arabic px tall, one English line $english px", arabic < english * 1.5f)
        for (text in listOf("تم استلام الدفعة", "بتحويل مصرفي.")) {
            assertEquals(text, TextAlign.Left, layoutOf(text).layoutInput.style.textAlign)
            assertEquals(text, ResolvedTextDirection.Rtl, layoutOf(text).getParagraphDirection(0))
        }
    }
}
