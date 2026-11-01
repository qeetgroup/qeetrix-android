package com.qeetgroup.qeetrix.components.dialog

import android.view.View
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.window.DialogWindowProvider
import com.qeetgroup.qeetrix.components.button.QeetrixButton
import com.qeetgroup.qeetrix.components.setQeetrix
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * The full-screen form: its window's dim, and closing with unsaved changes. Tablet pass, 2026-10-31: as a form sheet
 * on a tablet its window kept the platform's dim under the scrim, so the screen behind was darkened twice
 * (`#3E3E3E` where a dialog's scrim gives `#999897`).
 */
@RunWith(RobolectricTestRunner::class)
class QeetrixFullScreenFormTest {
    @get:Rule
    val compose = createComposeRule()

    private var open by mutableStateOf(true)
    private var dirty by mutableStateOf(false)
    private var formView: View? = null

    private fun showForm() {
        compose.setQeetrix {
            QeetrixFullScreenForm(
                open = open,
                onOpenChange = { open = it },
                title = "New account",
                confirmLabel = "Save",
                onConfirm = {},
                dirty = dirty,
            ) {
                formView = LocalView.current
                QeetrixButton("Account name", onClick = {})
            }
        }
    }

    private fun dimAmount(view: View?): Float =
        (view?.parent as DialogWindowProvider)
            .window.attributes.dimAmount

    @Test
    @Config(qualifiers = "w1280dp-h800dp")
    fun asAFormSheetOnATabletOnlyItsScrimDarkensTheScreen() {
        showForm()
        compose.runOnIdle { assertEquals(0f, dimAmount(formView)) }
    }

    @Test
    fun onAPhoneItsWindowHasNoDimEither() {
        showForm()
        compose.runOnIdle { assertEquals(0f, dimAmount(formView)) }
    }

    @Test
    fun aDialogsWindowHasNoDimUnderItsScrim() {
        var dialogView: View? = null
        compose.setQeetrix {
            QeetrixDialog(open = true, onOpenChange = {}, title = "Edit details") { dialogView = LocalView.current }
        }
        compose.runOnIdle { assertEquals(0f, dimAmount(dialogView)) }
    }

    @Test
    fun withUnsavedChangesCloseAsksFirst() {
        dirty = true
        showForm()
        compose.onNodeWithContentDescription("Close").performClick()
        compose.onNodeWithText("Discard changes?").assertExists()
        compose.onNodeWithText("Keep editing").performClick()
        compose.runOnIdle { assertEquals(true, open) }
        compose.onNodeWithContentDescription("Close").performClick()
        compose.onNodeWithText("Discard").performClick()
        compose.runOnIdle { assertEquals(false, open) }
    }
}
