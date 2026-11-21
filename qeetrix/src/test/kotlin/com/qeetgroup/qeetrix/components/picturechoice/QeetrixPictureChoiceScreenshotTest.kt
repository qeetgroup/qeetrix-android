package com.qeetgroup.qeetrix.components.picturechoice

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import com.qeetgroup.qeetrix.components.input.QeetrixOption
import com.qeetgroup.qeetrix.screenshot.SCREENSHOT_QUALIFIERS
import com.qeetgroup.qeetrix.screenshot.ScreenshotConfig
import com.qeetgroup.qeetrix.screenshot.captureQeetrix
import com.qeetgroup.qeetrix.theme.QeetrixTheme
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

/** Baselines for [QeetrixPictureChoiceGroup] beyond its specimen, in `src/test/screenshots/picture-choice/`. */
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(qualifiers = SCREENSHOT_QUALIFIERS)
class QeetrixPictureChoiceScreenshotTest {
    /** `small_<config>.png`: six 36 dp round background tones (`sm`), one chosen with its 16 dp tick. */
    @Test
    fun small() {
        for (config in ScreenshotConfig.entries) {
            captureQeetrix("picture-choice", "small", config) {
                val colors = QeetrixTheme.colors
                val tones =
                    listOf(
                        colors.datavizCategorical1,
                        colors.datavizCategorical2,
                        colors.datavizCategorical3,
                        colors.datavizCategorical4,
                        colors.bgNeutralSubtle,
                        colors.surfaceDefault,
                    )
                QeetrixPictureChoiceGroup(
                    2,
                    {},
                    tones.indices.map { QeetrixOption(it, "Tone ${it + 1}") },
                    contentDescription = "Background",
                    size = QeetrixPictureChoiceSize.Sm,
                ) { option -> Box(Modifier.fillMaxSize().background(tones[option.value])) }
            }
        }
    }
}
