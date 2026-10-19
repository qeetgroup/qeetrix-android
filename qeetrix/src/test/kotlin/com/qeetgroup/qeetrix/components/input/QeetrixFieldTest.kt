package com.qeetgroup.qeetrix.components.input

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.getUnclippedBoundsInRoot
import androidx.compose.ui.test.hasContentDescription
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.unit.dp
import com.qeetgroup.qeetrix.components.checkbox.QeetrixCheckbox
import com.qeetgroup.qeetrix.components.setQeetrix
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

/** The field parts: who names a control, how required, errors and groups reach accessibility services. */
@RunWith(RobolectricTestRunner::class)
class QeetrixFieldTest {
    @get:Rule
    val compose = createComposeRule()

    private val error = QeetrixFieldMessage("Choose a colour.")

    @Test
    fun aControlThatTakesTheFieldIsNamedByItsLabelAndCarriesItsError() {
        compose.setQeetrix {
            QeetrixField(label = "Brand colour", description = "Used on receipts.", message = error, required = true) {
                Box(Modifier.size(48.dp).qeetrixFieldControl().clickable {})
            }
        }
        compose
            .onNodeWithContentDescription("Brand colour, required")
            .assert(SemanticsMatcher.expectValue(SemanticsProperties.Error, "Choose a colour."))
        // The visible label is not read a second time. (The merged tree is what accessibility services get;
        // the unmerged one also lists the cleared subtree.)
        compose.onAllNodesWithText("Brand colour").assertCountEquals(0)
        compose.onNodeWithText("Used on receipts.").assertExists()
        compose.onNodeWithText("Choose a colour.").assert(SemanticsMatcher.keyIsDefined(SemanticsProperties.LiveRegion))
    }

    @Test
    fun withoutTheModifierTheLabelIsReadAsText() {
        compose.setQeetrix { QeetrixField(label = "Brand colour") { Box(Modifier.size(48.dp).clickable {}) } }
        compose.onNodeWithText("Brand colour").assertExists()
    }

    @Test
    fun aRequiredFieldSaysRequiredWithItsLabel() {
        compose.setQeetrix { QeetrixTextField(TextFieldState(), label = "Work email", required = true) }
        compose.onNodeWithText("Work email").assert(hasContentDescription("required"))
    }

    @Test
    fun aFieldSetLegendIsAHeadingAndItsErrorReachesEveryCheckbox() {
        compose.setQeetrix {
            QeetrixFieldSet("Notifications", message = QeetrixFieldMessage("Choose at least one.")) {
                QeetrixCheckbox(false, {}, label = "Email")
                QeetrixCheckbox(false, {}, label = "Push")
            }
        }
        compose.onNodeWithText("Notifications").assert(SemanticsMatcher.keyIsDefined(SemanticsProperties.Heading))
        for (choice in listOf("Email", "Push")) {
            compose.onNodeWithText(choice).assert(SemanticsMatcher.expectValue(SemanticsProperties.Error, "Choose at least one."))
        }
    }

    @Test
    fun aDisabledFieldSetDisablesTheControlsInIt() {
        compose.setQeetrix {
            QeetrixFieldSet("Billing", enabled = false) {
                QeetrixCheckbox(true, {}, label = "Send invoices")
                QeetrixTextField(TextFieldState(), label = "Billing email")
            }
        }
        compose.onNodeWithText("Send invoices").assertIsNotEnabled()
        compose.onNodeWithText("Billing email").assertIsNotEnabled()
    }

    @Test
    fun selectionRowsSitFlushAndFieldsKeepTheFieldGap() {
        compose.setQeetrix {
            QeetrixFieldSet("Preferences") {
                QeetrixCheckbox(false, {}, label = "Email")
                QeetrixCheckbox(false, {}, label = "Push")
                QeetrixTextField(TextFieldState(), label = "Phone")
            }
        }
        val email = compose.onNode(hasText("Email")).getUnclippedBoundsInRoot()
        val push = compose.onNode(hasText("Push")).getUnclippedBoundsInRoot()
        val phone = compose.onNodeWithText("Phone").getUnclippedBoundsInRoot()
        assertEquals("rows flush", email.bottom, push.top)
        assertEquals("field gap after the rows", 16f, (phone.top - push.bottom).value, 0.5f)
    }
}
