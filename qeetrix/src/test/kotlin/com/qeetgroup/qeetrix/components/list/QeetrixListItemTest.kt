package com.qeetgroup.qeetrix.components.list

import androidx.compose.foundation.layout.width
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.semantics.getOrNull
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertHeightIsAtLeast
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.getUnclippedBoundsInRoot
import androidx.compose.ui.test.hasClickAction
import androidx.compose.ui.test.isHeading
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.longClick
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import com.qeetgroup.qeetrix.components.badge.QeetrixBadge
import com.qeetgroup.qeetrix.components.button.QeetrixButton
import com.qeetgroup.qeetrix.components.button.QeetrixButtonVariant
import com.qeetgroup.qeetrix.components.button.QeetrixIconButton
import com.qeetgroup.qeetrix.components.setQeetrix
import com.qeetgroup.qeetrix.foundation.sizing.QeetrixDensity
import com.qeetgroup.qeetrix.icons.QeetrixIconName
import com.qeetgroup.qeetrix.theme.LocalQeetrixDensity
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

/** List rows: one element in reading order, the app's clicks and selection, sizes, large text; section headers. */
@RunWith(RobolectricTestRunner::class)
class QeetrixListItemTest {
    @get:Rule
    val compose = createComposeRule()

    @Test
    fun aRowIsOneElementReadTitleDescriptionThenMetadata() {
        compose.setQeetrix {
            QeetrixListItem(
                "Quarterly report.pdf",
                Modifier.testTag("row"),
                description = "Shared by Ada",
                metadata = "2.4 MB",
                leading = { QeetrixListItemIcon(QeetrixIconName.FileText) },
            ) { QeetrixBadge("Shared") }
        }
        val text =
            compose
                .onNodeWithTag("row")
                .fetchSemanticsNode()
                .config
                .getOrNull(SemanticsProperties.Text)
                ?.map { it.text }
        // The icon tile is decorative; the badge's word is part of the row.
        assertEquals(listOf("Quarterly report.pdf", "Shared by Ada", "2.4 MB", "Shared"), text)
        // A static row is not clickable and not selected.
        compose.onNodeWithTag("row").assert(SemanticsMatcher.keyNotDefined(SemanticsActions.OnClick))
        compose.onNodeWithTag("row").assert(SemanticsMatcher.keyNotDefined(SemanticsProperties.Selected))
    }

    @Test
    fun clicksAndLongClicksAreTheApps() {
        val events = mutableListOf<String>()
        var enabled by mutableStateOf(true)
        compose.setQeetrix {
            QeetrixListItem(
                "Ada Lovelace",
                Modifier.testTag("row"),
                enabled = enabled,
                onClick = { events += "click" },
                onLongClick = { events += "long" },
                onLongClickLabel = "Select",
            )
        }
        compose.onNodeWithTag("row").performClick()
        compose.onNodeWithTag("row").performTouchInput { longClick() }
        assertEquals(listOf("click", "long"), events)
        val longLabel =
            compose
                .onNodeWithTag("row")
                .fetchSemanticsNode()
                .config
                .getOrNull(SemanticsActions.OnLongClick)
                ?.label
        assertEquals("Select", longLabel)

        compose.runOnIdle { enabled = false }
        compose.onNodeWithTag("row").assertIsNotEnabled().performClick()
        assertEquals(listOf("click", "long"), events)
    }

    @Test
    fun aLongClickNeedsAClick() {
        assertThrows(IllegalArgumentException::class.java) {
            compose.setQeetrix { QeetrixListItem("Ada", onLongClick = {}) }
        }
    }

    @Test
    fun selectionIsDrawnAndReadOnlyWhenSelected() {
        var selected by mutableStateOf(true)
        compose.setQeetrix { QeetrixListItem("Ada", Modifier.testTag("row"), selected = selected, onClick = {}) }
        compose.onNodeWithTag("row").assertIsSelected()
        compose.runOnIdle { selected = false }
        // Rows that can't be selected aren't read as "not selected".
        compose.onNodeWithTag("row").assert(SemanticsMatcher.keyNotDefined(SemanticsProperties.Selected))
    }

    @Test
    fun aRowIsAtLeastTheRowHeightAndA48DpTarget() {
        var density by mutableStateOf(QeetrixDensity.Compact)
        compose.setQeetrix {
            CompositionLocalProvider(LocalQeetrixDensity provides density) {
                QeetrixListItem("Ada", Modifier.testTag("row"), onClick = {})
            }
        }
        for (each in QeetrixDensity.entries) {
            compose.runOnIdle { density = each }
            compose.onNodeWithTag("row").assertHeightIsAtLeast(maxOf(each.rowHeight, 48.dp))
        }
    }

    @Test
    fun aTrailingControlStaysItsOwnElement() {
        val events = mutableListOf<String>()
        compose.setQeetrix {
            QeetrixListItem("Weekly digest", Modifier.testTag("row"), onClick = { events += "row" }) {
                QeetrixIconButton(QeetrixIconName.Ellipsis, "More for Weekly digest", onClick = { events += "more" })
            }
        }
        compose.onNodeWithContentDescription("More for Weekly digest").assert(hasClickAction()).performClick()
        assertEquals(listOf("more"), events)
        val rowText =
            compose
                .onNodeWithTag("row")
                .fetchSemanticsNode()
                .config
                .getOrNull(SemanticsProperties.ContentDescription)
        assertTrue("the button isn't merged into the row: $rowText", rowText.isNullOrEmpty())
    }

    @Test
    fun fromLargeTextTheTrailingContentMovesBelowTheText() {
        var scale by mutableStateOf(1f)
        compose.setQeetrix {
            val base = LocalDensity.current
            CompositionLocalProvider(LocalDensity provides Density(base.density, scale)) {
                QeetrixListItem("Storage", Modifier.width(360.dp), description = "Used this month") {
                    QeetrixBadge("80 %", Modifier.testTag("trailing"))
                }
            }
        }
        val title = compose.onNodeWithText("Storage", useUnmergedTree = true)
        val beside = compose.onNodeWithTag("trailing", useUnmergedTree = true).getUnclippedBoundsInRoot()
        assertTrue("beside the text at 100 %", beside.top < title.getUnclippedBoundsInRoot().bottom)
        compose.runOnIdle { scale = 1.5f }
        val below = compose.onNodeWithTag("trailing", useUnmergedTree = true).getUnclippedBoundsInRoot()
        val description = compose.onNodeWithText("Used this month", useUnmergedTree = true).getUnclippedBoundsInRoot()
        assertTrue("below the text at 150 %", below.top >= description.bottom)
    }

    @Test
    fun metadataSitsOnTheTitleLineAtTheEnd() {
        compose.setQeetrix { QeetrixListItem("Report", Modifier.width(360.dp), description = "Shared by Ada", metadata = "2 MB") }
        val title = compose.onNodeWithText("Report", useUnmergedTree = true).getUnclippedBoundsInRoot()
        val metadata = compose.onNodeWithText("2 MB", useUnmergedTree = true).getUnclippedBoundsInRoot()
        val description = compose.onNodeWithText("Shared by Ada", useUnmergedTree = true).getUnclippedBoundsInRoot()
        assertTrue("same line", metadata.top < title.bottom && metadata.bottom > title.top)
        assertTrue("after the title", metadata.left > title.right)
        assertTrue("above the description", metadata.bottom <= description.top)
    }

    @Test
    fun aSectionHeaderIsAHeadingWithItsDescriptionAndASeparateAction() {
        val invited = mutableListOf<Unit>()
        compose.setQeetrix {
            QeetrixSectionHeader("Members", description = "People with access") {
                QeetrixButton("Invite", onClick = { invited += Unit }, variant = QeetrixButtonVariant.Link)
            }
        }
        val heading = compose.onNode(isHeading()).fetchSemanticsNode()
        assertEquals(listOf("Members", "People with access"), heading.config.getOrNull(SemanticsProperties.Text)?.map { it.text })
        assertFalse(heading.config.contains(SemanticsActions.OnClick))
        compose.onNodeWithText("Invite").performClick()
        assertEquals(1, invited.size)
    }
}
