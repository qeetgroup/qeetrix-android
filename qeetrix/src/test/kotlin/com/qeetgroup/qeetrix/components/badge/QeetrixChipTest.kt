package com.qeetgroup.qeetrix.components.badge

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertHeightIsAtLeast
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.assertIsNotSelected
import androidx.compose.ui.test.assertIsOff
import androidx.compose.ui.test.assertIsOn
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.getUnclippedBoundsInRoot
import androidx.compose.ui.test.hasClickAction
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onParent
import androidx.compose.ui.test.performClick
import androidx.compose.ui.unit.dp
import com.qeetgroup.qeetrix.components.input.QeetrixOption
import com.qeetgroup.qeetrix.components.setQeetrix
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.GraphicsMode

/** Chips: a checkbox that toggles, a named remove button, single and multiple groups, sizes and touch area. */
@RunWith(RobolectricTestRunner::class)
class QeetrixChipTest {
    @get:Rule
    val compose = createComposeRule()

    private val checkbox = SemanticsMatcher.expectValue(SemanticsProperties.Role, Role.Checkbox)
    private val radio = SemanticsMatcher.expectValue(SemanticsProperties.Role, Role.RadioButton)

    @Test
    fun aChipIsACheckboxThatReportsTheNewValue() {
        val changes = mutableListOf<Boolean>()
        var selected by mutableStateOf(false)
        compose.setQeetrix { QeetrixChip("Unread", selected = selected, onSelectedChange = { changes += it }) }
        compose
            .onNodeWithText("Unread")
            .assert(checkbox)
            .assertIsOff()
            .performClick()
        assertEquals(listOf(true), changes)
        // The value stays the app's until it changes it.
        compose.onNodeWithText("Unread").assertIsOff()
        compose.runOnIdle { selected = true }
        compose.onNodeWithText("Unread").assertIsOn().performClick()
        assertEquals(listOf(true, false), changes)
    }

    @Test
    fun aDisabledChipIgnoresTaps() {
        val changes = mutableListOf<Boolean>()
        compose.setQeetrix { QeetrixChip("Archived", selected = false, onSelectedChange = { changes += it }, enabled = false) }
        compose.onNodeWithText("Archived").assertIsNotEnabled().performClick()
        assertEquals(emptyList<Boolean>(), changes)
    }

    @Test
    fun everySizeHasA48DpTouchArea() {
        var size by mutableStateOf(QeetrixChipSize.Sm)
        compose.setQeetrix { QeetrixChip("Unread", selected = false, onSelectedChange = {}, size = size) }
        for (each in QeetrixChipSize.entries) {
            compose.runOnIdle { size = each }
            compose.onNodeWithText("Unread").assertHeightIsAtLeast(48.dp)
        }
        assertEquals(listOf("sm", "md", "lg"), QeetrixChipSize.entries.map { it.value })
    }

    /** The tag looks: 4 dp corners at every size, size.control.* tall inside the 48 dp touch area. */
    @Test
    @GraphicsMode(GraphicsMode.Mode.NATIVE)
    fun theDrawnChipIsTheControlHeight() {
        compose.setQeetrix {
            QeetrixChip("Unread", selected = false, onSelectedChange = {}, size = QeetrixChipSize.Sm, modifier = Modifier.testTag("c"))
        }
        val touch = compose.onNodeWithTag("c").getUnclippedBoundsInRoot()
        val label = compose.onNodeWithText("Unread", useUnmergedTree = true).getUnclippedBoundsInRoot()
        assertEquals(48f, (touch.bottom - touch.top).value, 0.5f)
        // label.sm's 16 dp line, centred in the 28 dp tag, centred in the 48 dp area.
        assertEquals(16f, label.top.value, 0.5f)
    }

    @Test
    fun aRemovableChipHasARemoveButtonNamedForIt() {
        val removed = mutableListOf<Unit>()
        compose.setQeetrix { QeetrixChip("Design", onRemove = { removed += Unit }) }
        compose.onNodeWithText("Design").assert(SemanticsMatcher.keyNotDefined(SemanticsProperties.Role))
        compose
            .onNodeWithContentDescription("Remove Design")
            .assert(SemanticsMatcher.expectValue(SemanticsProperties.Role, Role.Button))
            .assert(hasClickAction())
            .performClick()
        assertEquals(1, removed.size)
    }

    @Test
    fun aSingleChoiceGroupIsRadioButtonsNamedByItsLabel() {
        val chosen = mutableListOf<String?>()
        val options = listOf(QeetrixOption(null, "All"), QeetrixOption("open", "Open"), QeetrixOption("closed", "Closed", enabled = false))
        compose.setQeetrix { QeetrixChipGroup(label = "Status", value = null, onValueChange = { chosen += it }, options = options) }
        compose
            .onNodeWithText("All")
            .assert(radio)
            .assertIsSelected()
            .onParent()
            .assert(SemanticsMatcher.keyIsDefined(SemanticsProperties.SelectableGroup))
            .assert(SemanticsMatcher.expectValue(SemanticsProperties.ContentDescription, listOf("Status")))
        compose.onNodeWithText("All").performClick() // already chosen: nothing to report
        compose.onNodeWithText("Open").assertIsNotSelected().performClick()
        compose.onNodeWithText("Closed").assertIsNotEnabled().performClick()
        assertEquals(listOf<String?>("open"), chosen)
    }

    @Test
    fun aMultipleChoiceGroupTogglesValuesInTheSet() {
        var selected by mutableStateOf(setOf("Design"))
        val options = listOf("Design", "Finance", "Legal").map { QeetrixOption(it, it) }
        compose.setQeetrix {
            QeetrixChipGroup(
                label = "Labels",
                values = selected,
                onValuesChange = { selected = it },
                options = options,
            )
        }
        compose.onNodeWithText("Design").assert(checkbox).assertIsOn()
        compose.onNodeWithText("Finance").assertIsOff().performClick()
        compose.onNodeWithText("Design").performClick()
        compose.runOnIdle { assertEquals(setOf("Finance"), selected) }
    }

    @Test
    fun optionsNeedValuesOfTheirOwn() {
        assertThrows(IllegalArgumentException::class.java) {
            compose.setQeetrix {
                QeetrixChipGroup(
                    label = "Status",
                    value = 1,
                    onValueChange = {},
                    options = listOf(QeetrixOption(1, "One"), QeetrixOption(1, "Uno")),
                )
            }
        }
    }
}
