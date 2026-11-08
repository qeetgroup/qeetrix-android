package com.qeetgroup.qeetrix.components.permission

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.getUnclippedBoundsInRoot
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import com.qeetgroup.qeetrix.components.setQeetrix
import com.qeetgroup.qeetrix.components.typography.QeetrixText
import com.qeetgroup.qeetrix.components.typography.QeetrixTextSize
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

/** A primer's footnote, under its actions, before the system is asked and after it was refused. */
@RunWith(RobolectricTestRunner::class)
class QeetrixPermissionPrimerTest {
    @get:Rule
    val compose = createComposeRule()

    @Test
    fun theFootnoteSitsUnderTheActionsInBothStates() {
        var status by mutableStateOf(QeetrixPermissionStatus.NotAsked)
        compose.setQeetrix {
            QeetrixPermissionPrimer(
                QeetrixPermission.Notifications,
                title = "Get sign-in requests on this phone",
                description = "Approve or deny each sign-in with a tap.",
                onContinue = {},
                onNotNow = {},
                status = status,
                deniedDescription = "Turn on notifications for Qeet ID in Settings.",
                footer = { QeetrixText("Only sign-in requests. Nothing else, no marketing.", size = QeetrixTextSize.Sm) },
            )
        }
        for ((shown, action) in listOf(QeetrixPermissionStatus.NotAsked to "Continue", QeetrixPermissionStatus.Denied to "Open Settings")) {
            compose.runOnIdle { status = shown }
            val footnote = compose.onNodeWithText("Only sign-in requests. Nothing else, no marketing.").getUnclippedBoundsInRoot()
            val notNow = compose.onNodeWithText("Not now").getUnclippedBoundsInRoot()
            val primary = compose.onNodeWithText(action).getUnclippedBoundsInRoot()
            assertTrue("$shown: under the actions", footnote.top >= notNow.bottom && footnote.top >= primary.bottom)
        }
    }
}
