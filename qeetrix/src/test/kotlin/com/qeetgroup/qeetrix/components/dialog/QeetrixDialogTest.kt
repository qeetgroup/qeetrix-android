package com.qeetgroup.qeetrix.components.dialog

import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.test.click
import androidx.compose.ui.test.hasAnyAncestor
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.isDialog
import androidx.compose.ui.test.isHeading
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.performTouchInput
import com.qeetgroup.qeetrix.components.button.QeetrixButton
import com.qeetgroup.qeetrix.components.button.QeetrixButtonVariant
import com.qeetgroup.qeetrix.components.input.QeetrixTextField
import com.qeetgroup.qeetrix.components.setQeetrix
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

/** The custom dialog and the confirmation dialog: content, footer, close button, taps outside. */
@RunWith(RobolectricTestRunner::class)
class QeetrixDialogTest {
    @get:Rule
    val compose = createComposeRule()

    @Test
    fun aFormInADialogKeepsItsValuesUntilTheAppCloses() {
        var renaming by mutableStateOf(true)
        var saved = ""
        compose.setQeetrix {
            if (renaming) {
                val name = rememberTextFieldState()
                QeetrixDialog(
                    title = "Rename workspace",
                    description = "Everyone in the workspace sees the new name.",
                    onDismissRequest = { renaming = false },
                    footer = {
                        QeetrixButton("Cancel", onClick = { renaming = false }, variant = QeetrixButtonVariant.Outline)
                        QeetrixButton("Save", onClick = {
                            saved = name.text.toString()
                            renaming = false
                        })
                    },
                ) {
                    QeetrixTextField(name, label = "Name")
                }
            }
        }
        compose.onNode(isHeading() and hasText("Rename workspace") and hasAnyAncestor(isDialog())).assertExists()
        compose.onNodeWithText("Name").performTextInput("Northwind")
        compose.onNodeWithText("Save").performClick()
        compose.runOnIdle { assertEquals("Northwind", saved) }
        compose.onNode(isDialog()).assertDoesNotExist()
    }

    @Test
    fun theCloseButtonAndATapOutsideAskToClose() {
        val requests = mutableListOf<String>()
        compose.setQeetrix {
            QeetrixDialog(title = "Details", onDismissRequest = { requests += "dismiss" }) {
                QeetrixButton("Inside", onClick = { requests += "inside" })
            }
        }
        compose.onNodeWithContentDescription("Close").performClick()
        compose.onNodeWithText("Inside").performClick()
        compose.onNode(isDialog()).performTouchInput { click(Offset(4f, 4f)) }
        compose.runOnIdle { assertEquals(listOf("dismiss", "inside", "dismiss"), requests) }
    }

    @Test
    fun withoutDismissOnClickOutsideATapOutsideDoesNothing() {
        var requests = 0
        compose.setQeetrix {
            QeetrixDialog(title = "Details", onDismissRequest = { requests++ }, dismissOnClickOutside = false, showCloseButton = false) {}
        }
        compose.onNode(isDialog()).performTouchInput { click(Offset(4f, 4f)) }
        compose.onNodeWithContentDescription("Close").assertDoesNotExist()
        compose.runOnIdle { assertEquals(0, requests) }
    }

    @Test
    fun aConfirmationRunsOnConfirmOnceAndCancelOnlyDismisses() {
        val events = mutableListOf<String>()
        var asking by mutableStateOf(true)
        compose.setQeetrix {
            if (asking) {
                QeetrixConfirmationDialog(
                    title = "Remove Ada from the team?",
                    confirmLabel = "Remove",
                    destructive = true,
                    onConfirm = { events += "remove" },
                    onDismissRequest = {
                        events += "dismiss"
                        asking = false
                    },
                )
            }
        }
        compose.onNodeWithText("Cancel").performClick()
        compose.runOnIdle {
            assertEquals(listOf("dismiss"), events)
            asking = true
        }
        compose.onNodeWithText("Remove").performClick()
        compose.runOnIdle { assertEquals(listOf("dismiss", "dismiss", "remove"), events) }
    }
}
