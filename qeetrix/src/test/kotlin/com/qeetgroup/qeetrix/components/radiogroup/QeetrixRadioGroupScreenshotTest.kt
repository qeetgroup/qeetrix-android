package com.qeetgroup.qeetrix.components.radiogroup

import com.qeetgroup.qeetrix.components.input.QeetrixFieldMessage
import com.qeetgroup.qeetrix.components.input.QeetrixOption
import com.qeetgroup.qeetrix.screenshot.SCREENSHOT_QUALIFIERS
import com.qeetgroup.qeetrix.screenshot.ScreenshotStack
import com.qeetgroup.qeetrix.screenshot.captureStates
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

/** Baselines for [QeetrixRadioGroup] in `src/test/screenshots/radiogroup/`. */
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(qualifiers = SCREENSHOT_QUALIFIERS)
class QeetrixRadioGroupScreenshotTest {
    private val plans =
        listOf(
            QeetrixOption("starter", "Starter", "Up to 5 members"),
            QeetrixOption("team", "Team", "Up to 50 members, audit logs"),
            QeetrixOption("enterprise", "Enterprise", "Contact sales", enabled = false),
        )

    @Test
    fun states() =
        captureStates("radiogroup") {
            ScreenshotStack {
                QeetrixRadioGroup("team", {}, plans, label = "Plan", description = "You can change it at any time.")
                QeetrixRadioGroup<String?>(
                    null,
                    {},
                    plans.take(2),
                    label = "Billing",
                    required = true,
                    message = QeetrixFieldMessage("Choose a plan."),
                )
            }
        }
}
