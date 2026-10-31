package com.qeetgroup.qeetrix.components.feed

import androidx.compose.foundation.layout.width
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
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

/** Timelines: one element per event, the CSS's gaps, groups, the compact look, and the time at large text. */
@RunWith(RobolectricTestRunner::class)
class QeetrixTimelineTest {
    @get:Rule
    val compose = createComposeRule()

    @Test
    fun anEventIsReadAsOneElementAndItsMarkerNotAtAll() {
        compose.setQeetrix {
            QeetrixTimeline {
                QeetrixTimelineItem(
                    "Sign-in refused",
                    description = "Wrong one-time code",
                    time = "1 h ago",
                    icon = QeetrixIconName.CircleX,
                    tone = QeetrixTimelineTone.Danger,
                )
            }
        }
        val event = compose.onNode(hasText("Sign-in refused")).fetchSemanticsNode()
        assertEquals(
            listOf("Sign-in refused", "Wrong one-time code", "1 h ago"),
            event.config.getOrNull(SemanticsProperties.Text)?.map { it.text },
        )
        assertEquals(null, event.config.getOrNull(SemanticsProperties.ContentDescription))
    }

    /** timeline.css: items `space.gap.lg` apart (compact `space.gap.sm`), nothing after the last; groups `space.gap.md`. */
    @Test
    fun eventsAndGroupsAreTheCssGapsApart() {
        compose.setQeetrix {
            QeetrixTimeline(Modifier.testTag("timeline")) {
                QeetrixTimelineGroup("Today", Modifier.testTag("today")) {
                    QeetrixTimelineItem("Sent", Modifier.testTag("first"))
                    QeetrixTimelineItem("Paid", Modifier.testTag("last"))
                }
                QeetrixTimelineGroup("Yesterday", Modifier.testTag("yesterday")) { QeetrixTimelineItem("Created") }
            }
        }

        fun bounds(tag: String) = compose.onNodeWithTag(tag, useUnmergedTree = true).getUnclippedBoundsInRoot()
        assertEquals(16.dp, bounds("last").top - bounds("first").bottom)
        assertEquals(bounds("today").bottom, bounds("last").bottom)
        assertEquals(12.dp, bounds("yesterday").top - bounds("today").bottom)
        assertEquals(bounds("timeline").bottom, bounds("yesterday").bottom)
    }

    @Test
    fun compactEventsAreTheSmallGapApart() {
        compose.setQeetrix {
            QeetrixTimeline(variant = QeetrixTimelineVariant.Compact) {
                QeetrixTimelineItem("Sent", Modifier.testTag("first"))
                QeetrixTimelineItem("Paid", Modifier.testTag("last"))
            }
        }
        val first = compose.onNodeWithTag("first", useUnmergedTree = true).getUnclippedBoundsInRoot()
        val last = compose.onNodeWithTag("last", useUnmergedTree = true).getUnclippedBoundsInRoot()
        assertEquals(8.dp, last.top - first.bottom)
    }

    /** `padding-block-start: calc(14px − .5lh)`: the title's 20 dp line is centred on the 28 dp marker. */
    @Test
    fun theFirstLineIsCentredOnTheMarker() {
        compose.setQeetrix { QeetrixTimeline { QeetrixTimelineItem("Sent", Modifier.testTag("item")) } }
        val item = compose.onNodeWithTag("item", useUnmergedTree = true).getUnclippedBoundsInRoot()
        val title = compose.onNodeWithText("Sent", useUnmergedTree = true).getUnclippedBoundsInRoot()
        assertEquals(4.dp, title.top - item.top)
        assertEquals(28.dp + 12.dp, title.left - item.left) // the marker column, then space.gap.md
    }

    @Test
    fun fromLargeTextTheTimeMovesBelowTheText() {
        var scale by mutableFloatStateOf(1f)
        compose.setQeetrix {
            val base = LocalDensity.current
            CompositionLocalProvider(LocalDensity provides Density(base.density, scale)) {
                QeetrixTimeline(Modifier.width(360.dp)) { QeetrixTimelineItem("Payment received", time = "Today") }
            }
        }

        fun bounds(text: String) = compose.onNodeWithText(text, useUnmergedTree = true).getUnclippedBoundsInRoot()
        assertTrue("beside", bounds("Today").top < bounds("Payment received").bottom)
        compose.runOnIdle { scale = 1.5f }
        assertTrue("below", bounds("Today").top >= bounds("Payment received").bottom)
    }

    @Test
    fun optionsCarryTheWebsNames() {
        assertEquals(listOf("neutral", "brand", "info", "success", "warning", "danger"), QeetrixTimelineTone.entries.map { it.value })
        assertEquals(listOf("default", "compact"), QeetrixTimelineVariant.entries.map { it.value })
    }
}
