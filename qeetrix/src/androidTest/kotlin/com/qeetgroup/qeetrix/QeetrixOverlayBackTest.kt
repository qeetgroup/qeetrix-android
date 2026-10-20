package com.qeetgroup.qeetrix

import android.view.KeyEvent
import android.view.View
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.isHeading
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.qeetgroup.qeetrix.components.button.QeetrixButton
import com.qeetgroup.qeetrix.components.dialog.QeetrixDialog
import com.qeetgroup.qeetrix.components.drawer.QeetrixBottomSheet
import com.qeetgroup.qeetrix.components.dropdownmenu.QeetrixMenu
import com.qeetgroup.qeetrix.components.dropdownmenu.QeetrixMenuItem
import com.qeetgroup.qeetrix.theme.QeetrixTheme
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * System Back on a device, which the JVM tests cannot route to a popup window: each overlay closes on Back and
 * reports it once, and the topmost one closes first. Run with `./gradlew :qeetrix:connectedDebugAndroidTest`.
 */
@RunWith(AndroidJUnit4::class)
class QeetrixOverlayBackTest {
    @get:Rule
    val compose = createComposeRule()

    /** A Back key press, delivered by the system to the focused window — a popup's or a dialog's when one is open. */
    private fun pressBack() {
        InstrumentationRegistry.getInstrumentation().sendKeyDownUpSync(KeyEvent.KEYCODE_BACK)
        compose.waitForIdle()
    }

    /**
     * Waits until the window holding [view] has input focus, so the next Back goes to it. Focus moves between
     * windows asynchronously, after Compose is idle: in Phase 6 a second Back sent as soon as the dialog closed
     * reached no window once in a full device run (8 of 8 passed alone).
     */
    private fun awaitWindowFocus(view: () -> View?) {
        compose.waitUntil(5_000) { compose.runOnUiThread { view()?.hasWindowFocus() == true } }
    }

    @Test
    fun backClosesAMenu() {
        var open by mutableStateOf(true)
        val changes = mutableListOf<Boolean>()
        var menuView: View? = null
        compose.setContent {
            QeetrixTheme {
                QeetrixMenu(
                    expanded = open,
                    onExpandedChange = {
                        changes += it
                        open = it
                    },
                    trigger = { QeetrixButton("Options", onClick = { open = true }) },
                ) {
                    menuView = LocalView.current
                    QeetrixMenuItem("Rename", onClick = {})
                }
            }
        }
        compose.onNodeWithText("Rename").assertExists()
        awaitWindowFocus { menuView }
        pressBack()
        compose.onNodeWithText("Rename").assertDoesNotExist()
        compose.runOnIdle { assertEquals(listOf(false), changes) }
    }

    @Test
    fun backClosesTheTopmostOverlayFirst() {
        var sheet by mutableStateOf(true)
        var dialog by mutableStateOf(false)
        val dismissed = mutableListOf<String>()
        var sheetView: View? = null
        var dialogView: View? = null
        compose.setContent {
            QeetrixTheme {
                QeetrixBottomSheet(
                    visible = sheet,
                    onDismissRequest = {
                        dismissed += "sheet"
                        sheet = false
                    },
                    title = "Invoice",
                ) {
                    sheetView = LocalView.current
                    QeetrixButton("Edit details", onClick = { dialog = true })
                }
                if (dialog) {
                    QeetrixDialog(
                        title = "Edit details",
                        onDismissRequest = {
                            dismissed += "dialog"
                            dialog = false
                        },
                    ) { dialogView = LocalView.current }
                }
            }
        }
        compose.runOnIdle { dialog = true }
        // The dialog's title, not the sheet's button of the same name (Phase 6: the first device run found the
        // matcher ambiguous).
        compose.onNode(isHeading() and hasText("Edit details")).assertExists()
        awaitWindowFocus { dialogView }
        pressBack()
        compose.runOnIdle { assertEquals(listOf("dialog"), dismissed) }
        awaitWindowFocus { sheetView }
        pressBack()
        compose.runOnIdle { assertEquals(listOf("dialog", "sheet"), dismissed) }
    }
}
