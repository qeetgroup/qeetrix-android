package com.qeetgroup.qeetrix.components.feed

import com.qeetgroup.qeetrix.icons.QeetrixIconName
import com.qeetgroup.qeetrix.screenshot.SCREENSHOT_QUALIFIERS
import com.qeetgroup.qeetrix.screenshot.ScreenshotStack
import com.qeetgroup.qeetrix.screenshot.captureStates
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

/** Baselines in `src/test/screenshots/timeline/`: groups with the line broken between them, every tone, and the compact look. */
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(qualifiers = SCREENSHOT_QUALIFIERS)
class QeetrixTimelineScreenshotTest {
    @Test
    fun states() =
        captureStates("timeline") {
            ScreenshotStack {
                QeetrixTimeline {
                    QeetrixTimelineGroup("Today") {
                        QeetrixTimelineItem(
                            "Signed in with a passkey",
                            description = "Bengaluru, India · Chrome on macOS",
                            time = "2 min ago",
                            icon = QeetrixIconName.Lock,
                        )
                        QeetrixTimelineItem(
                            "Sign-in refused",
                            description = "Wrong one-time code, three times",
                            time = "1 h ago",
                            icon = QeetrixIconName.CircleX,
                            tone = QeetrixTimelineTone.Danger,
                        )
                        QeetrixTimelineItem(
                            "Payment received",
                            time = "10:24",
                            icon = QeetrixIconName.CircleCheck,
                            tone = QeetrixTimelineTone.Success,
                        )
                    }
                    QeetrixTimelineGroup("Yesterday") {
                        for (tone in QeetrixTimelineTone.entries) {
                            QeetrixTimelineItem(
                                "A ${tone.value} event",
                                time = "18:0${tone.ordinal}",
                                icon = QeetrixIconName.Info,
                                tone = tone,
                            )
                        }
                        QeetrixTimelineItem("An event without an icon", time = "09:12")
                    }
                }
                QeetrixTimeline(variant = QeetrixTimelineVariant.Compact) {
                    QeetrixTimelineItem("Invoice sent", time = "1 week ago", tone = QeetrixTimelineTone.Brand)
                    QeetrixTimelineItem("Viewed by the customer", time = "6 days ago")
                    QeetrixTimelineItem(
                        "Card payment declined",
                        description = "Insufficient funds",
                        time = "5 days ago",
                        tone = QeetrixTimelineTone.Danger,
                    )
                }
            }
        }
}
