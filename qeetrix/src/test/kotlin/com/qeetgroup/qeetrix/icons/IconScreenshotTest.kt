package com.qeetgroup.qeetrix.icons

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import com.qeetgroup.qeetrix.foundation.sizing.QeetrixIconSize
import com.qeetgroup.qeetrix.screenshot.SCREENSHOT_QUALIFIERS
import com.qeetgroup.qeetrix.screenshot.ScreenshotConfig
import com.qeetgroup.qeetrix.screenshot.ScreenshotGap
import com.qeetgroup.qeetrix.screenshot.captureQeetrix
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(qualifiers = SCREENSHOT_QUALIFIERS)
@OptIn(ExperimentalLayoutApi::class)
class IconScreenshotTest {
    @Test
    fun iconsInEveryDrawing() {
        for (config in listOf(ScreenshotConfig.Light, ScreenshotConfig.Dark, ScreenshotConfig.RightToLeft)) {
            captureQeetrix("icon", "drawings", config) {
                Column(verticalArrangement = Arrangement.spacedBy(ScreenshotGap)) {
                    for (shape in QeetrixIconShape.entries) {
                        for (variant in QeetrixIconVariant.entries) {
                            FlowRow(
                                horizontalArrangement = Arrangement.spacedBy(ScreenshotGap),
                                verticalArrangement = Arrangement.spacedBy(ScreenshotGap),
                            ) {
                                for (name in QeetrixIconName.entries) {
                                    if (name.hasDrawing(shape, variant)) {
                                        QeetrixIcon(
                                            name,
                                            contentDescription = null,
                                            size = QeetrixIconSize.Lg,
                                            shape = shape,
                                            variant = variant,
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
