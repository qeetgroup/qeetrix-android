package com.qeetgroup.qeetrix.components.newcomponents

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.ui.unit.dp
import com.qeetgroup.qeetrix.quality.QualityCatalog
import com.qeetgroup.qeetrix.screenshot.SCREENSHOT_QUALIFIERS
import com.qeetgroup.qeetrix.screenshot.captureStates
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

/**
 * Baselines for the components added after the first set, in
 * `src/test/screenshots/<specimen>/`: each drawn from its quality-catalogue specimen, in light, dark, 2× text and right
 * to left, so the scans and the renders show the same states.
 */
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(qualifiers = SCREENSHOT_QUALIFIERS)
class NewComponentsScreenshotTest {
    private fun capture(name: String) {
        val specimen = QualityCatalog.single { it.name == name }
        captureStates(name) { Column(verticalArrangement = Arrangement.spacedBy(8.dp)) { specimen.content() } }
    }

    @Test fun otpCode() = capture("otp-code")

    @Test fun qrCode() = capture("qr-code")

    @Test fun qrScanner() = capture("qr-scanner")

    @Test fun permissionPrimer() = capture("permission-primer")

    @Test fun alert() = capture("alert")

    @Test fun steps() = capture("steps")

    @Test fun stat() = capture("stat")

    @Test fun checkList() = capture("check-list")

    @Test fun avatarGroup() = capture("avatar-group")

    @Test fun bottomActionBar() = capture("bottom-action-bar")

    @Test fun segmentedControl() = capture("segmented-control")

    @Test fun pageIndicator() = capture("page-indicator")

    @Test fun slider() = capture("slider")

    @Test fun dateField() = capture("date-field")

    @Test fun fileUpload() = capture("file-upload")

    @Test fun swipeActions() = capture("swipe-actions")

    @Test fun topAppBarLarge() = capture("top-app-bar-large")
}
