package com.qeetgroup.qeetrix.components.drawer

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertHeightIsAtLeast
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.getUnclippedBoundsInRoot
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.isHeading
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.unit.dp
import com.qeetgroup.qeetrix.components.dropdownmenu.QeetrixMenuItemTone
import com.qeetgroup.qeetrix.components.setQeetrix
import com.qeetgroup.qeetrix.icons.QeetrixIconName
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

/** An account's actions in a sheet — menu rows, a danger tone, groups, closing first and running once. */
@RunWith(RobolectricTestRunner::class)
class QeetrixActionSheetTest {
    @get:Rule
    val compose = createComposeRule()

    private var open by mutableStateOf(false)
    private val events = mutableListOf<String>()

    private fun showSheet() {
        compose.setQeetrix {
            QeetrixActionSheet(
                open = open,
                onOpenChange = {
                    events += "open=$it"
                    open = it
                },
                title = "GitHub · priya",
            ) {
                QeetrixSheetAction("Copy code", onClick = { events += "copy" }, icon = QeetrixIconName.Copy, closeOnClick = false)
                QeetrixSheetAction("Rename", onClick = { events += "rename" }, icon = QeetrixIconName.Pencil)
                QeetrixSheetAction("Move to folder", onClick = {}, enabled = false, description = "No folders yet")
                QeetrixSheetActionSeparator()
                QeetrixSheetAction(
                    "Delete account",
                    onClick = { events += "delete" },
                    icon = QeetrixIconName.Trash,
                    tone = QeetrixMenuItemTone.Danger,
                )
            }
        }
        compose.runOnIdle { open = true }
    }

    @Test
    fun eachActionIsAButtonReadWithItsLabel() {
        showSheet()
        compose.onNode(isHeading() and hasText("GitHub · priya")).assertExists()
        for (label in listOf("Copy code", "Rename", "Delete account")) {
            compose
                .onNodeWithText(label)
                .assert(SemanticsMatcher.expectValue(SemanticsProperties.Role, Role.Button))
                .assertHeightIsAtLeast(48.dp)
        }
        compose.onNodeWithText("Move to folder", substring = true).assertIsNotEnabled()
    }

    @Test
    fun choosingAnActionClosesTheSheetFirstAndRunsItOnce() {
        showSheet()
        compose.onNodeWithText("Delete account").performClick()
        compose.runOnIdle { assertEquals(listOf("open=false", "delete"), events) }
        compose.onNodeWithText("Delete account").assertDoesNotExist()
    }

    @Test
    fun anActionThatKeepsTheSheetOpenRunsEachTime() {
        showSheet()
        compose.onNodeWithText("Copy code").performClick()
        compose.onNodeWithText("Copy code").performClick()
        compose.runOnIdle { assertEquals(listOf("copy", "copy"), events) }
        compose.onNodeWithText("Rename").performClick()
        compose.runOnIdle { assertEquals(listOf("copy", "copy", "open=false", "rename"), events) }
    }

    /** The rows run edge to edge of the sheet, their highlight reaching both sides, as a menu's rows do. */
    @Test
    fun theRowsRunEdgeToEdge() {
        showSheet()
        val title = compose.onNode(isHeading() and hasText("GitHub · priya")).getUnclippedBoundsInRoot()
        val row = compose.onNodeWithText("Rename").getUnclippedBoundsInRoot()
        // The title sits space.inset.xl (24 dp) in from the sheet's start; the row starts at the sheet's edge.
        assertEquals((title.left - 24.dp).value, row.left.value, 0.5f)
    }
}
