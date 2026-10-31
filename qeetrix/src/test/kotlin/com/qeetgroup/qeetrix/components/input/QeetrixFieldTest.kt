package com.qeetgroup.qeetrix.components.input

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.getUnclippedBoundsInRoot
import androidx.compose.ui.test.hasContentDescription
import androidx.compose.ui.test.isPopup
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.unit.dp
import com.qeetgroup.qeetrix.components.checkbox.QeetrixCheckbox
import com.qeetgroup.qeetrix.components.setQeetrix
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

/** The field parts: who names a control, how errors replace help text, how sets and their legends behave. */
@RunWith(RobolectricTestRunner::class)
class QeetrixFieldTest {
    @get:Rule
    val compose = createComposeRule()

    @Test
    fun aControlThatTakesTheFieldIsNamedByItsLabelAndCarriesItsError() {
        compose.setQeetrix {
            QeetrixField(label = "Brand colour", description = "Used on receipts.", error = "Choose a colour.", required = true) {
                Box(Modifier.size(48.dp).qeetrixFieldControl().clickable {})
            }
        }
        compose
            .onNodeWithContentDescription("Brand colour, required")
            .assert(SemanticsMatcher.expectValue(SemanticsProperties.Error, "Choose a colour."))
        // The visible label is not read a second time.
        compose.onAllNodesWithText("Brand colour").assertCountEquals(0)
        compose
            .onNodeWithText("Choose a colour.")
            .assert(SemanticsMatcher.expectValue(SemanticsProperties.LiveRegion, LiveRegionMode.Assertive))
    }

    /** react's FieldMessages: while an error shows, the description is not rendered, on screen or for TalkBack. */
    @Test
    fun anErrorReplacesTheDescription() {
        compose.setQeetrix {
            Column {
                QeetrixField(label = "GSTIN", description = "15 characters.", error = "Enter a GSTIN.") { Box(Modifier.size(48.dp)) }
                QeetrixField(label = "PAN", description = "10 characters.", invalid = true) { Box(Modifier.size(48.dp)) }
            }
        }
        compose.onNodeWithText("15 characters.").assertDoesNotExist()
        compose.onNodeWithText("Enter a GSTIN.").assertExists()
        // Invalid without a message keeps the description.
        compose.onNodeWithText("10 characters.").assertExists()
    }

    @Test
    fun withoutTheModifierTheLabelIsReadAsText() {
        compose.setQeetrix { QeetrixField(label = "Brand colour") { Box(Modifier.size(48.dp).clickable {}) } }
        compose.onNodeWithText("Brand colour").assertExists()
    }

    @Test
    fun aRequiredFieldSaysRequiredOnlyToAccessibilityServices() {
        compose.setQeetrix { QeetrixTextField(TextFieldState(), label = "Work email", required = true) }
        compose.onNodeWithText("Work email").assert(hasContentDescription("required", substring = true))
        // No asterisk is drawn.
        compose.onAllNodesWithText("*", useUnmergedTree = true).assertCountEquals(0)
    }

    @Test
    fun anOptionalFieldSaysSoInItsLabel() {
        compose.setQeetrix { QeetrixTextField(TextFieldState(), label = "Trade name", optional = true) }
        compose.onNodeWithText("Trade name (optional)", substring = true).assertExists()
    }

    @Test
    fun aHiddenLabelStillNamesTheField() {
        compose.setQeetrix { QeetrixTextField(TextFieldState(), label = "Filter members", hideLabel = true) }
        compose.onNodeWithText("Filter members", useUnmergedTree = true).assertDoesNotExist()
        compose.onNodeWithContentDescription("Filter members").assertExists()
    }

    @Test
    fun helpOpensAPopoverTitledWithTheLabel() {
        compose.setQeetrix {
            QeetrixTextField(TextFieldState(), label = "Token lifetime", help = "How long an access token stays valid.")
        }
        compose.onNodeWithContentDescription("About Token lifetime").performClick()
        compose.onNode(isPopup()).assertExists()
        compose.onNodeWithText("How long an access token stays valid.").assertExists()
    }

    @Test
    fun aFieldSetLegendIsAHeadingAndADisabledSetDisablesItsControls() {
        compose.setQeetrix {
            QeetrixFieldSet("Billing", description = "Where we send invoices.", enabled = false) {
                QeetrixCheckbox(true, {}, label = "Send invoices")
                QeetrixTextField(TextFieldState(), label = "Billing email")
            }
        }
        compose.onNodeWithText("Billing").assert(SemanticsMatcher.keyIsDefined(SemanticsProperties.Heading))
        compose.onNodeWithText("Send invoices").assertIsNotEnabled()
        compose.onNodeWithText("Billing email").assertIsNotEnabled()
    }

    /** fieldset.css: the header and the fields stand space.form-gap (16 dp at the default density) apart. */
    @Test
    fun fieldsInASetStandTheFormGapApart() {
        compose.setQeetrix {
            QeetrixFieldSet("Address") {
                QeetrixTextField(TextFieldState(), label = "City")
                QeetrixTextField(TextFieldState(), label = "Postcode")
            }
        }
        val city = compose.onNodeWithText("City").getUnclippedBoundsInRoot()
        val postcode = compose.onNodeWithText("Postcode").getUnclippedBoundsInRoot()
        assertEquals(16f, (postcode.top - city.bottom).value, 0.5f)
    }

    /** field.css: the label sits space.gap.xs (4 dp) above the box. */
    @Test
    fun theLabelSitsFourDpAboveTheBox() {
        compose.setQeetrix { QeetrixTextField(TextFieldState(), label = "City") }
        val label = compose.onNodeWithText("City", useUnmergedTree = true).getUnclippedBoundsInRoot()
        val field = compose.onNodeWithText("City").getUnclippedBoundsInRoot()
        // The field node spans label and box; the box is size.control.lg (40 dp) below the label and its gap.
        assertEquals(40f, (field.bottom - label.bottom - 4.dp).value, 0.5f)
    }
}
