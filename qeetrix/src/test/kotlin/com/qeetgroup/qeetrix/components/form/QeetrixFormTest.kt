package com.qeetgroup.qeetrix.components.form

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertHeightIsAtLeast
import androidx.compose.ui.test.getUnclippedBoundsInRoot
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performClick
import androidx.compose.ui.unit.dp
import com.qeetgroup.qeetrix.components.button.QeetrixButton
import com.qeetgroup.qeetrix.components.setQeetrix
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
class QeetrixFormTest {
    @get:Rule
    val compose = createComposeRule()

    @Test
    @Config(qualifiers = "w360dp-h800dp")
    fun onANarrowWindowActionsStackFullWidthWithThePrimaryOnTop() {
        compose.setQeetrix {
            QeetrixFormActions(Modifier.width(300.dp)) {
                QeetrixButton("Cancel", onClick = {})
                QeetrixButton("Save changes", onClick = {})
            }
        }
        val cancel = compose.onNodeWithText("Cancel").getUnclippedBoundsInRoot()
        val save = compose.onNodeWithText("Save changes").getUnclippedBoundsInRoot()
        assertTrue("primary above", save.bottom <= cancel.top)
        assertEquals(300f, (save.right - save.left).value, 0.5f)
        assertEquals(300f, (cancel.right - cancel.left).value, 0.5f)
    }

    @Test
    @Config(qualifiers = "w800dp-h600dp")
    fun fromSixHundredFortyDpTheyShareARowAtTheEnd() {
        compose.setQeetrix {
            QeetrixFormActions(Modifier.fillMaxWidth()) {
                QeetrixButton("Cancel", onClick = {})
                QeetrixButton("Save changes", onClick = {})
            }
        }
        val cancel = compose.onNodeWithText("Cancel").getUnclippedBoundsInRoot()
        val save = compose.onNodeWithText("Save changes").getUnclippedBoundsInRoot()
        val root = compose.onRoot().getUnclippedBoundsInRoot()
        assertEquals("one row", cancel.top, save.top)
        assertTrue("cancel first", cancel.right <= save.left)
        assertEquals("at the end", root.right.value, save.right.value, 0.5f)
    }

    @Test
    fun theSummaryIsAnnouncedAndEachErrorTakesYouToItsField() {
        val chosen = mutableListOf<String>()
        compose.setQeetrix {
            QeetrixFormErrorSummary(
                listOf(QeetrixFormError("Enter a name.") { chosen += "name" }, QeetrixFormError("Choose a plan.") { chosen += "plan" }),
            )
        }
        compose
            .onNodeWithText("There is a problem")
            .assert(SemanticsMatcher.expectValue(SemanticsProperties.LiveRegion, LiveRegionMode.Assertive))
        compose
            .onNodeWithText("Choose a plan.")
            .assert(SemanticsMatcher.expectValue(SemanticsProperties.Role, Role.Button))
            .assertHeightIsAtLeast(48.dp)
            .performClick()
        assertEquals(listOf("plan"), chosen)
    }

    @Test
    fun withNoErrorsThereIsNoSummary() {
        compose.setQeetrix { QeetrixFormErrorSummary(emptyList(), title = "Fix these") }
        compose.onNodeWithText("Fix these").assertDoesNotExist()
    }
}
