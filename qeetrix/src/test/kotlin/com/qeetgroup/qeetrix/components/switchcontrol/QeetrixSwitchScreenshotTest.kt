package com.qeetgroup.qeetrix.components.switchcontrol

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.qeetgroup.qeetrix.components.input.rememberFocusedSource
import com.qeetgroup.qeetrix.components.input.rememberHoveredSource
import com.qeetgroup.qeetrix.components.separator.QeetrixSeparator
import com.qeetgroup.qeetrix.components.typography.QeetrixText
import com.qeetgroup.qeetrix.components.typography.QeetrixTextSize
import com.qeetgroup.qeetrix.components.typography.QeetrixTextTone
import com.qeetgroup.qeetrix.screenshot.SCREENSHOT_QUALIFIERS
import com.qeetgroup.qeetrix.screenshot.ScreenshotConfig
import com.qeetgroup.qeetrix.screenshot.ScreenshotFlow
import com.qeetgroup.qeetrix.screenshot.ScreenshotStack
import com.qeetgroup.qeetrix.screenshot.captureQeetrix
import com.qeetgroup.qeetrix.screenshot.captureStates
import com.qeetgroup.qeetrix.theme.QeetrixTheme
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

/** Baselines for [QeetrixSwitch] in `src/test/screenshots/switch/`. */
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(qualifiers = SCREENSHOT_QUALIFIERS)
class QeetrixSwitchScreenshotTest {
    @Test
    fun states() =
        captureStates("switch") {
            ScreenshotStack {
                ScreenshotFlow {
                    QeetrixSwitch(false, {})
                    QeetrixSwitch(true, {})
                    QeetrixSwitch(false, {}, invalid = true)
                    QeetrixSwitch(true, {}, invalid = true)
                    QeetrixSwitch(false, {}, interactionSource = rememberHoveredSource())
                    QeetrixSwitch(true, {}, interactionSource = rememberHoveredSource())
                    QeetrixSwitch(true, {}, interactionSource = rememberFocusedSource())
                    QeetrixSwitch(false, {}, enabled = false)
                    QeetrixSwitch(true, {}, enabled = false)
                }
                QeetrixSwitch(
                    true,
                    {},
                    label = "Auto-renew",
                    description = "Renews on the 1st of each month. A longer line that wraps under the label.",
                )
                QeetrixSwitch(false, {}, label = "Email alerts", interactionSource = rememberFocusedSource())
                QeetrixSwitch(true, {}, label = "Disabled", description = "Your admin sets this.", enabled = false)
            }
        }

    /**
     * `src/test/screenshots/switch/settingsrow_<config>.png`: settings rows as the web draws them — a body.md label and
     * a body.sm note, the bare switch at the end, 14 dp above and below and 16 dp at the sides — the switch laid out at
     * its track, so a one-line row is 52 dp and the note keeps the width beside it.
     */
    @Test
    fun settingsRow() {
        for (config in ScreenshotConfig.entries) {
            captureQeetrix("switch", "settingsrow", config) {
                Column(Modifier.width(360.dp).background(QeetrixTheme.colors.surfaceDefault)) {
                    SettingsRow("Read receipts", null, true)
                    QeetrixSeparator()
                    SettingsRow("Announcements", "Admins still see who read announcements.", false)
                }
            }
        }
    }
}

@Composable
private fun SettingsRow(
    label: String,
    note: String?,
    on: Boolean,
) {
    Row(
        Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 14.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(Modifier.weight(1f)) {
            QeetrixText(label)
            if (note != null) QeetrixText(note, size = QeetrixTextSize.Sm, tone = QeetrixTextTone.Subtle)
        }
        QeetrixSwitch(on, {}, Modifier.semantics { contentDescription = label })
    }
}
