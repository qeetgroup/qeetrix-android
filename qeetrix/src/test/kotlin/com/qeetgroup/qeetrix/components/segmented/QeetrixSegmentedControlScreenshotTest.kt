package com.qeetgroup.qeetrix.components.segmented

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.width
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.qeetgroup.qeetrix.components.button.QeetrixButtonSize
import com.qeetgroup.qeetrix.components.input.QeetrixOption
import com.qeetgroup.qeetrix.screenshot.SCREENSHOT_QUALIFIERS
import com.qeetgroup.qeetrix.screenshot.ScreenshotConfig
import com.qeetgroup.qeetrix.screenshot.captureQeetrix
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

/** Baselines for [QeetrixSegmentedControl] beyond its specimen, in `src/test/screenshots/segmented-control/`. */
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(qualifiers = SCREENSHOT_QUALIFIERS)
class QeetrixSegmentedControlScreenshotTest {
    /**
     * `fullwidth_<config>.png`: a three-choice picker across a 328 dp column at sm and md, each segment an equal share
     * with its label centred, and one whose long label ends in an ellipsis.
     */
    @Test
    fun fullWidth() {
        val who = listOf(QeetrixOption("everyone", "Everyone"), QeetrixOption("contacts", "My contacts"), QeetrixOption("nobody", "Nobody"))
        for (config in ScreenshotConfig.entries) {
            captureQeetrix("segmented-control", "fullwidth", config) {
                Column(Modifier.width(328.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    QeetrixSegmentedControl("contacts", {}, who, size = QeetrixButtonSize.Sm, fullWidth = true)
                    QeetrixSegmentedControl("everyone", {}, who, fullWidth = true)
                    QeetrixSegmentedControl(
                        "all",
                        {},
                        listOf(QeetrixOption("all", "Everyone in the workspace"), QeetrixOption("admins", "Admins only")),
                        fullWidth = true,
                    )
                }
            }
        }
    }
}
