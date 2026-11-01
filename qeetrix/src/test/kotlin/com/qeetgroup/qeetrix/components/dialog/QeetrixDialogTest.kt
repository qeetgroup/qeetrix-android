package com.qeetgroup.qeetrix.components.dialog

import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.click
import androidx.compose.ui.test.hasAnyAncestor
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.isDialog
import androidx.compose.ui.test.isHeading
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performKeyInput
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.pressKey
import androidx.test.espresso.Espresso
import com.qeetgroup.qeetrix.components.button.QeetrixButton
import com.qeetgroup.qeetrix.components.button.QeetrixButtonVariant
import com.qeetgroup.qeetrix.components.setQeetrix
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

/** The dialog: the app's open flag, its content and footer, the close button, taps outside, and leaving. */
@RunWith(RobolectricTestRunner::class)
class QeetrixDialogTest {
    @get:Rule
    val compose = createComposeRule()

    @Test
    fun aFormInADialogKeepsItsValuesUntilTheAppCloses() {
        var renaming by mutableStateOf(true)
        var saved = ""
        compose.setQeetrix {
            val name = rememberTextFieldState()
            QeetrixDialog(
                open = renaming,
                onOpenChange = { renaming = it },
                title = "Rename workspace",
                description = "Everyone in the workspace sees the new name.",
                footer = {
                    QeetrixButton("Cancel", onClick = { renaming = false }, variant = QeetrixButtonVariant.Ghost)
                    QeetrixButton("Save", onClick = {
                        saved = name.text.toString()
                        renaming = false
                    })
                },
            ) {
                BasicTextField(name, Modifier.testTag("name"))
            }
        }
        compose.onNode(isHeading() and hasText("Rename workspace") and hasAnyAncestor(isDialog())).assertExists()
        compose.onNodeWithTag("name").performTextInput("Northwind")
        compose.onNodeWithText("Save").performClick()
        compose.runOnIdle { assertEquals("Northwind", saved) }
        compose.onNode(isDialog()).assertDoesNotExist()
    }

    @Test
    fun theCloseButtonATapOutsideAndBackAskToClose() {
        val requests = mutableListOf<String>()
        compose.setQeetrix {
            QeetrixDialog(open = true, onOpenChange = { requests += "open=$it" }, title = "Details") {
                QeetrixButton("Inside", onClick = { requests += "inside" })
            }
        }
        compose.onNodeWithContentDescription("Close").performClick()
        compose.onNodeWithText("Inside").performClick()
        compose.onNode(isDialog()).performTouchInput { click(Offset(4f, 4f)) }
        Espresso.pressBack()
        compose.runOnIdle { assertEquals(listOf("open=false", "inside", "open=false", "open=false"), requests) }
    }

    @Test
    fun notDismissibleItHasNoCloseButtonAndIgnoresATapOutsideButNotBack() {
        var requests = 0
        compose.setQeetrix {
            QeetrixDialog(open = true, onOpenChange = { requests++ }, title = "Unsaved changes", dismissible = false)
        }
        compose.onNodeWithContentDescription("Close").assertDoesNotExist()
        compose.onNode(isDialog()).performTouchInput { click(Offset(4f, 4f)) }
        compose.runOnIdle { assertEquals(0, requests) }
        Espresso.pressBack()
        compose.runOnIdle { assertEquals(1, requests) }
    }

    @Test
    fun aDangerDialogIsNotDismissibleUnlessTheAppSaysSo() {
        compose.setQeetrix {
            QeetrixDialog(open = true, onOpenChange = {}, title = "Revoke this key?", intent = QeetrixDialogIntent.Danger)
        }
        compose.onNodeWithContentDescription("Close").assertDoesNotExist()
    }

    @Test
    fun itStaysWhileItLeavesAndIgnoresTapsThen() {
        var open by mutableStateOf(true)
        var requests = 0
        compose.setQeetrix {
            QeetrixDialog(open = open, onOpenChange = { requests++ }, title = "Details") {
                QeetrixButton("Inside", onClick = {})
            }
        }
        compose.onNodeWithText("Inside").assertExists()
        compose.mainClock.autoAdvance = false
        compose.runOnIdle { open = false }
        compose.mainClock.advanceTimeByFrame()
        compose.mainClock.advanceTimeBy(50)
        // Still on screen, fading out; the close button no longer asks again.
        compose.onNodeWithText("Inside").assertExists()
        compose.onNodeWithContentDescription("Close").performClick()
        compose.mainClock.autoAdvance = true
        compose.onNode(isDialog()).assertDoesNotExist()
        // The app closed it; nothing asked to close while it left.
        compose.runOnIdle { assertEquals(0, requests) }
    }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun escapeAsksToClose() {
        var requests = 0
        compose.setQeetrix {
            QeetrixDialog(open = true, onOpenChange = { requests++ }, title = "Details") {
                QeetrixButton("Inside", onClick = {})
            }
        }
        compose.onNodeWithText("Inside").performKeyInput { pressKey(Key.Escape) }
        compose.runOnIdle { assertEquals(1, requests) }
    }

    @Test
    fun closedItComposesNothing() {
        compose.setQeetrix {
            QeetrixDialog(open = false, onOpenChange = {}, title = "Details") { QeetrixButton("Inside", onClick = {}) }
        }
        compose.onNode(isDialog()).assertDoesNotExist()
        compose.onNodeWithText("Inside").assertDoesNotExist()
    }
}
