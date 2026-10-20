package com.qeetgroup.qeetrix.components.feed

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.Measurable
import androidx.compose.ui.node.ModifierNodeElement
import androidx.compose.ui.node.ParentDataModifierNode
import androidx.compose.ui.platform.InspectorInfo
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import com.qeetgroup.qeetrix.components.list.LARGE_FONT_SCALE
import com.qeetgroup.qeetrix.foundation.sizing.QeetrixStroke
import com.qeetgroup.qeetrix.icons.QeetrixIcon
import com.qeetgroup.qeetrix.icons.QeetrixIconName
import com.qeetgroup.qeetrix.internal.FontWeightMedium
import com.qeetgroup.qeetrix.internal.QeetrixFont
import com.qeetgroup.qeetrix.internal.QeetrixWebScale
import com.qeetgroup.qeetrix.internal.labelDirection
import com.qeetgroup.qeetrix.internal.qeetrixTextStyle
import com.qeetgroup.qeetrix.theme.QeetrixTheme
import com.qeetgroup.qeetrix.tokens.generated.QeetrixTimelineComponentTokens

/**
 * The colour of a timeline event's marker (`value` is the web name) — a second channel only: the event's title
 * must still say what happened.
 */
public enum class QeetrixTimelineTone(
    public val value: String,
) {
    /** Routine history. The default. */
    Neutral("neutral"),
    Brand("brand"),
    Info("info"),
    Success("success"),
    Warning("warning"),
    Destructive("destructive"),
}

/** How much an event matters (`value` is the web name). */
public enum class QeetrixTimelineEmphasis(
    public val value: String,
) {
    /** A meaningful event: a full-size marker and a primary title. The default. */
    Default("default"),

    /**
     * Housekeeping — a label changed, a sync ran: a small neutral marker, a muted title and a tighter rhythm,
     * so a run of them reads as one quiet block.
     */
    Minor("minor"),
}

/**
 * A vertical history of events, in the order you give them — the web's `Timeline`. Put
 * [QeetrixTimelineItem]s in it.
 *
 * ```
 * QeetrixTimeline {
 *     for (event in events) {
 *         key(event.id) {
 *             QeetrixTimelineItem(
 *                 title = event.title,
 *                 time = event.relativeTime,
 *                 description = event.detail,
 *                 tone = if (event.failed) QeetrixTimelineTone.Destructive else QeetrixTimelineTone.Neutral,
 *             )
 *         }
 *     }
 * }
 * ```
 *
 * Each event has a marker on a rail one marker wide (24 dp, `component.timeline.marker-size`), joined to the
 * next by a 1 dp connector; give the events that matter most an icon. Events are `component.timeline.gap`
 * apart, `gap-minor` after a minor one, with nothing after the last.
 *
 * The events, their order, their wording and their times are the app's: the timeline fetches, groups and
 * classifies nothing. Format times before passing them. For a long or growing history in a lazy list, use
 * [com.qeetgroup.qeetrix.components.list.QeetrixListItem] rows — a connector can't join separate lazy items.
 */
@Composable
public fun QeetrixTimeline(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    val connector = QeetrixTheme.colors.borderDefault // bg-border w-px
    val markers = remember { MarkerTops() }
    Layout(
        content,
        modifier.fillMaxWidth().drawBehind {
            // From each marker's centre to the next one's, under both; the markers sit at the top of their
            // events, at the inline start.
            val centre = QeetrixTimelineComponentTokens.MarkerSize.toPx() / 2
            val width = QeetrixStroke.Hairline.toPx()
            val x = (if (layoutDirection == LayoutDirection.Rtl) size.width - centre else centre) - width / 2
            markers.tops.toList().zipWithNext { from, to ->
                drawRect(connector, Offset(x, from + centre), Size(width, (to - from).toFloat()))
            }
        },
    ) { measurables, constraints ->
        val placeables = measurables.map { it.measure(constraints.copy(minHeight = 0)) }
        val tops = IntArray(placeables.size)
        var y = 0
        placeables.forEachIndexed { index, placeable ->
            tops[index] = y
            y += placeable.height
            // pb-(--qx-component-timeline-gap) last:pb-0
            if (index < placeables.lastIndex) y += gapAfter(measurables[index]).roundToPx()
        }
        markers.update(tops)
        layout(constraints.maxWidth, y.coerceIn(constraints.minHeight, constraints.maxHeight)) {
            placeables.forEachIndexed { index, placeable -> placeable.placeRelative(0, tops[index]) }
        }
    }
}

/**
 * One event of a [QeetrixTimeline]: a marker, a [title], an optional [time] at the end of the title line and an
 * optional [description].
 *
 * Accessibility services read the event as one element — title, time, description — and then [markerLabel]
 * ("Failed", "Approved"), when you give one, for a marker whose meaning the words don't already say. From 150 %
 * text the time moves below the title.
 *
 * @param icon An icon in a tinted circle, for the events that matter most — the strongest of the three marker
 *   levels (icon, dot, minor dot).
 */
@Composable
public fun QeetrixTimelineItem(
    title: String,
    modifier: Modifier = Modifier,
    time: String? = null,
    description: String? = null,
    tone: QeetrixTimelineTone = QeetrixTimelineTone.Neutral,
    icon: QeetrixIconName? = null,
    emphasis: QeetrixTimelineEmphasis = QeetrixTimelineEmphasis.Default,
    markerLabel: String? = null,
) {
    val colors = QeetrixTheme.colors
    val minor = emphasis == QeetrixTimelineEmphasis.Minor
    val timeBelow = LocalDensity.current.fontScale >= LARGE_FONT_SCALE
    Row(
        modifier
            .then(TimelineEmphasisElement(emphasis))
            .fillMaxWidth()
            .semantics(mergeDescendants = true) { if (markerLabel != null) stateDescription = markerLabel },
        horizontalArrangement = Arrangement.spacedBy(QeetrixWebScale.spacing(3f)), // gap-3
    ) {
        Marker(tone, icon, minor)
        // gap-0.5 pt-0.5: the first line centred on the marker.
        Column(
            Modifier.weight(1f).padding(top = QeetrixWebScale.spacing(0.5f)),
            verticalArrangement = Arrangement.spacedBy(QeetrixWebScale.spacing(0.5f)),
        ) {
            val titleStyle =
                (if (minor) MinorTitleStyle else TitleStyle).copy(
                    color = if (minor) colors.textTertiary else colors.textPrimary,
                )
            // Below the title the time is as wide as the column, so it also aligns to the layout's start.
            val timeStyle = TimeStyle.copy(color = colors.textTertiary).labelDirection(alignToLayout = timeBelow)
            if (time == null || timeBelow) {
                BasicText(title, Modifier.fillMaxWidth(), style = titleStyle)
                if (time != null) BasicText(time, Modifier.fillMaxWidth(), style = timeStyle)
            } else {
                // flex flex-wrap items-baseline justify-between gap-x-3
                Row(horizontalArrangement = Arrangement.spacedBy(QeetrixWebScale.spacing(3f))) {
                    BasicText(title, Modifier.weight(1f).alignByBaseline(), style = titleStyle)
                    BasicText(time, Modifier.alignByBaseline(), style = timeStyle, maxLines = 1, overflow = TextOverflow.Ellipsis)
                }
            }
            if (description !=
                null
            ) {
                BasicText(description, Modifier.fillMaxWidth(), style = DescriptionStyle.copy(color = colors.textTertiary))
            }
        }
    }
}

/**
 * The marker in its 24 dp box: an icon in a tinted, opaque circle that hides the connector behind it; a toned
 * 10 dp dot; or a 6 dp neutral dot for a minor event. Decorative.
 */
@Composable
private fun Marker(
    tone: QeetrixTimelineTone,
    icon: QeetrixIconName?,
    minor: Boolean,
) {
    val colors = QeetrixTheme.colors
    Box(Modifier.size(QeetrixTimelineComponentTokens.MarkerSize), contentAlignment = Alignment.Center) {
        if (icon != null) {
            // ICON_TONE: the edge, the tint laid over the surface, and the icon's colour.
            val (edge, tint, foreground) =
                when (tone) {
                    QeetrixTimelineTone.Neutral -> Triple(colors.borderDefault, colors.surfaceSunken, colors.textTertiary)
                    QeetrixTimelineTone.Brand -> Triple(colors.borderBrand, colors.surfaceBrandSubtle, colors.textBrand)
                    QeetrixTimelineTone.Info -> Triple(colors.borderInfo, colors.feedbackInfoSubtle, colors.textInfo)
                    QeetrixTimelineTone.Success -> Triple(colors.borderSuccess, colors.feedbackSuccessSubtle, colors.textSuccess)
                    QeetrixTimelineTone.Warning -> Triple(colors.borderWarning, colors.feedbackWarningSubtle, colors.textWarning)
                    QeetrixTimelineTone.Destructive -> Triple(colors.borderDanger, colors.feedbackErrorSubtle, colors.textDanger)
                }
            Box(
                Modifier
                    .size(QeetrixTimelineComponentTokens.MarkerSize)
                    .background(colors.surfaceDefault, CircleShape) // bg-surface, then the tint over it: opaque
                    .background(tint, CircleShape)
                    .border(QeetrixStroke.Hairline, edge, CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                QeetrixIcon(icon, contentDescription = null, size = QeetrixWebScale.spacing(3.5f), tint = foreground) // size-3.5
            }
        } else {
            // DOT_TONE: graphics of at least 3:1 on every surface. size-2.5, or size-1.5 bg-border-strong when minor.
            val color =
                if (minor) {
                    colors.borderStrong
                } else {
                    when (tone) {
                        QeetrixTimelineTone.Neutral -> colors.textTertiary

                        // bg-muted-foreground
                        QeetrixTimelineTone.Brand -> colors.actionPrimary

                        // bg-primary
                        QeetrixTimelineTone.Info -> colors.feedbackInfo

                        QeetrixTimelineTone.Success -> colors.feedbackSuccess

                        QeetrixTimelineTone.Warning -> colors.feedbackWarning

                        QeetrixTimelineTone.Destructive -> colors.feedbackError
                    }
                }
            Box(Modifier.size(QeetrixWebScale.spacing(if (minor) 1.5f else 2.5f)).background(color, CircleShape))
        }
    }
}

/** Where each event starts, written by the timeline's layout and read when it draws the connectors. */
private class MarkerTops {
    var tops by mutableStateOf(IntArray(0))
        private set

    fun update(next: IntArray) {
        if (!next.contentEquals(tops)) tops = next
    }
}

/** The gap after an event: `timeline-gap`, or `timeline-gap-minor` after a minor one. */
private fun gapAfter(measurable: Measurable) =
    if ((measurable.parentData as? TimelineEmphasisData)?.emphasis == QeetrixTimelineEmphasis.Minor) {
        QeetrixTimelineComponentTokens.GapMinor
    } else {
        QeetrixTimelineComponentTokens.Gap
    }

private class TimelineEmphasisData(
    val emphasis: QeetrixTimelineEmphasis,
)

private class TimelineEmphasisElement(
    val emphasis: QeetrixTimelineEmphasis,
) : ModifierNodeElement<TimelineEmphasisNode>() {
    override fun create() = TimelineEmphasisNode(emphasis)

    override fun update(node: TimelineEmphasisNode) {
        node.emphasis = emphasis
    }

    override fun hashCode(): Int = emphasis.hashCode()

    override fun equals(other: Any?): Boolean = other is TimelineEmphasisElement && other.emphasis == emphasis

    override fun InspectorInfo.inspectableProperties() {
        name = "timelineEmphasis"
        value = emphasis
    }
}

private class TimelineEmphasisNode(
    var emphasis: QeetrixTimelineEmphasis,
) : Modifier.Node(),
    ParentDataModifierNode {
    override fun Density.modifyParentData(parentData: Any?): Any = TimelineEmphasisData(emphasis)
}

// text-sm font-medium text-foreground
private val TitleStyle = qeetrixTextStyle(QeetrixFont.Ui, QeetrixWebScale.TextSm, FontWeightMedium)

// A minor event's title: font-normal text-muted-foreground.
private val MinorTitleStyle = qeetrixTextStyle(QeetrixFont.Ui, QeetrixWebScale.TextSm)

// text-xs text-muted-foreground tabular-nums
private val TimeStyle = qeetrixTextStyle(QeetrixFont.Ui, QeetrixWebScale.TextXs).copy(fontFeatureSettings = "tnum")

// text-sm text-muted-foreground
private val DescriptionStyle = qeetrixTextStyle(QeetrixFont.Sans, QeetrixWebScale.TextSm)
