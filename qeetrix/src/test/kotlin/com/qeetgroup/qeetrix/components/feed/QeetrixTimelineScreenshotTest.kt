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

/** Baselines in `src/test/screenshots/timeline/`: every dot tone, icon markers, minor events and the connector. */
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(qualifiers = SCREENSHOT_QUALIFIERS)
class QeetrixTimelineScreenshotTest {
    @Test
    fun states() =
        captureStates("timeline") {
            ScreenshotStack {
                QeetrixTimeline {
                    QeetrixTimelineItem(
                        "Payment received",
                        time = "Today, 10:24",
                        description = "By bank transfer.",
                        tone = QeetrixTimelineTone.Success,
                        icon = QeetrixIconName.CircleCheck,
                    )
                    for (tone in QeetrixTimelineTone.entries) QeetrixTimelineItem("A ${tone.value} event", time = "Yesterday", tone = tone)
                    QeetrixTimelineItem("Viewed by the customer", time = "2 days ago", emphasis = QeetrixTimelineEmphasis.Minor)
                    QeetrixTimelineItem("Due date changed", time = "2 days ago", emphasis = QeetrixTimelineEmphasis.Minor)
                    QeetrixTimelineItem(
                        "Card payment declined",
                        time = "5 days ago",
                        tone = QeetrixTimelineTone.Destructive,
                        icon = QeetrixIconName.CircleX,
                    )
                    QeetrixTimelineItem("Invoice sent", time = "1 week ago", tone = QeetrixTimelineTone.Brand, icon = QeetrixIconName.Send)
                }
            }
        }
}
