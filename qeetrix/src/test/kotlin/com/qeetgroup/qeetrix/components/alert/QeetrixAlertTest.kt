package com.qeetgroup.qeetrix.components.alert

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.qeetgroup.qeetrix.components.setQeetrix
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

/** Alert: the tone said in words before the title, the description, Dismiss, the web's names. */
@RunWith(RobolectricTestRunner::class)
class QeetrixAlertTest {
    @get:Rule
    val compose = createComposeRule()

    @Test
    fun theToneIsReadBeforeTheTitleAndDismissIsThere() {
        var dismissed = false
        compose.setQeetrix {
            QeetrixAlert(
                "Push isn't set up",
                tone = QeetrixAlertTone.Warning,
                description = "Requests won't reach this phone.",
                onDismiss = { dismissed = true },
            )
        }
        compose.onNodeWithContentDescription("Warning: Push isn't set up").assertExists()
        compose.onNodeWithText("Requests won't reach this phone.").assertExists()
        compose.onNodeWithContentDescription("Dismiss").performClick()
        assertEquals(true, dismissed)
        assertEquals(listOf("info", "success", "warning", "danger"), QeetrixAlertTone.entries.map { it.value })
        assertEquals(listOf("default", "banner"), QeetrixAlertVariant.entries.map { it.value })
    }
}
