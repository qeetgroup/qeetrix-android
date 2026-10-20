package com.qeetgroup.qeetrix.components.feed

import androidx.compose.foundation.layout.width
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.semantics.getOrNull
import androidx.compose.ui.test.getUnclippedBoundsInRoot
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import com.qeetgroup.qeetrix.components.setQeetrix
import com.qeetgroup.qeetrix.icons.QeetrixIconName
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

/** Timelines: one element per event, the marker's label, the timeline tokens' gaps, and the time at large text. */
@RunWith(RobolectricTestRunner::class)
class QeetrixTimelineTest {
    @get:Rule
    val compose = createComposeRule()

    @Test
    fun anEventIsReadAsOneElementWithItsMarkersLabel() {
        compose.setQeetrix {
            QeetrixTimeline {
                QeetrixTimelineItem(
                    "Card payment",
                    time = "5 days ago",
                    description = "The bank declined it.",
                    tone = QeetrixTimelineTone.Destructive,
                    icon = QeetrixIconName.CircleX,
                    markerLabel = "Failed",
                )
            }
        }
        val event = compose.onNode(hasText("Card payment")).fetchSemanticsNode()
        assertEquals(
            listOf("Card payment", "5 days ago", "The bank declined it."),
            event.config.getOrNull(SemanticsProperties.Text)?.map {
                it.text
            },
        )
        assertEquals("Failed", event.config.getOrNull(SemanticsProperties.StateDescription))
    }

    @Test
    fun eventsAreTheTimelineGapApartTheMinorGapAfterAMinorOneAndNothingAfterTheLast() {
        compose.setQeetrix {
            QeetrixTimeline(Modifier.testTag("timeline")) {
                QeetrixTimelineItem("Sent", Modifier.testTag("first"))
                QeetrixTimelineItem("Viewed", Modifier.testTag("minor"), emphasis = QeetrixTimelineEmphasis.Minor)
                QeetrixTimelineItem("Paid", Modifier.testTag("last"))
            }
        }
        val first = compose.onNodeWithTag("first", useUnmergedTree = true).getUnclippedBoundsInRoot()
        val minor = compose.onNodeWithTag("minor", useUnmergedTree = true).getUnclippedBoundsInRoot()
        val last = compose.onNodeWithTag("last", useUnmergedTree = true).getUnclippedBoundsInRoot()
        val timeline = compose.onNodeWithTag("timeline").getUnclippedBoundsInRoot()
        assertEquals(24.dp, minor.top - first.bottom) // component.timeline.gap
        assertEquals(12.dp, last.top - minor.bottom) // component.timeline.gap-minor
        assertEquals(timeline.bottom, last.bottom)
    }

    @Test
    fun fromLargeTextTheTimeMovesBelowTheTitle() {
        var scale by mutableStateOf(1f)
        compose.setQeetrix {
            val base = LocalDensity.current
            CompositionLocalProvider(LocalDensity provides Density(base.density, scale)) {
                QeetrixTimelineItem("Payment received", Modifier.width(360.dp), time = "Today")
            }
        }

        fun bounds(text: String) = compose.onNodeWithText(text, useUnmergedTree = true).getUnclippedBoundsInRoot()
        assertTrue("beside", bounds("Today").top < bounds("Payment received").bottom)
        compose.runOnIdle { scale = 1.5f }
        assertTrue("below", bounds("Today").top >= bounds("Payment received").bottom)
    }

    @Test
    fun optionsCarryTheWebsNames() {
        assertEquals(listOf("neutral", "brand", "info", "success", "warning", "destructive"), QeetrixTimelineTone.entries.map { it.value })
        assertEquals(listOf("default", "minor"), QeetrixTimelineEmphasis.entries.map { it.value })
    }
}
