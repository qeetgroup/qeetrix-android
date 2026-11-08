package com.qeetgroup.qeetrix.components.feed

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.layout.AlignmentLine
import androidx.compose.ui.layout.FirstBaseline
import androidx.compose.ui.layout.HorizontalAlignmentLine
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.Measurable
import androidx.compose.ui.layout.ParentDataModifier
import androidx.compose.ui.layout.Placeable
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.qeetgroup.qeetrix.components.list.LARGE_FONT_SCALE
import com.qeetgroup.qeetrix.components.list.QeetrixGroupLabel
import com.qeetgroup.qeetrix.foundation.shape.QeetrixShapes
import com.qeetgroup.qeetrix.foundation.sizing.QeetrixBorder
import com.qeetgroup.qeetrix.foundation.sizing.QeetrixSize
import com.qeetgroup.qeetrix.foundation.spacing.QeetrixSpace
import com.qeetgroup.qeetrix.icons.QeetrixIcon
import com.qeetgroup.qeetrix.icons.QeetrixIconName
import com.qeetgroup.qeetrix.icons.QeetrixIconShape
import com.qeetgroup.qeetrix.internal.QeetrixTextWeight
import com.qeetgroup.qeetrix.internal.firstLineHeight
import com.qeetgroup.qeetrix.internal.labelDirection
import com.qeetgroup.qeetrix.theme.LocalQeetrixContentColor
import com.qeetgroup.qeetrix.theme.QeetrixTheme
import com.qeetgroup.qeetrix.tokens.generated.QeetrixTimelineComponentTokens
import kotlin.math.max

/** The two looks of a [QeetrixTimeline] (`value` is `@qeetrix/react`'s `variant`). */
public enum class QeetrixTimelineVariant(
    public val value: String,
) {
    /** A 28 dp square marker per event, holding its icon, joined to the next by a 1 dp line. The default. */
    Default("default"),

    /** A 6 dp dot per event, centred on its title's line, with no line: for a side panel or a short summary. */
    Compact("compact"),
}

/**
 * An event's colour (`value` is `@qeetrix/react`'s `tone`, Badge's names): it colours the icon or the dot
 * (`color.icon.<tone>`), and [Danger] — a failure, such as a refused sign-in — the title too. A second channel only:
 * the title says what happened.
 */
public enum class QeetrixTimelineTone(
    public val value: String,
) {
    /** Routine history: `color.icon.subtle` (a `color.border.strong` dot). The default. */
    Neutral("neutral"),

    /** `color.icon.brand`: a step the product took, such as an invoice sent. */
    Brand("brand"),

    /** `color.icon.info`: something to know. */
    Info("info"),

    /** `color.icon.success`: a completion, such as a payment received. */
    Success("success"),

    /** `color.icon.warning`: something to check. */
    Warning("warning"),

    /** A failure: the icon or dot and the title in the danger colour. */
    Danger("danger"),
}

/**
 * What happened, newest first — `@qeetrix/react`'s Timeline: a user's recent activity, a payment's history, a
 * delivery's attempts. Put [QeetrixTimelineItem]s in it, or [QeetrixTimelineGroup]s of them (one per day).
 *
 * ```
 * QeetrixTimeline {
 *     QeetrixTimelineGroup("Today") {
 *         QeetrixTimelineItem("Signed in with a passkey", description = "Bengaluru · Chrome on macOS", time = "2 min ago", icon = QeetrixIconName.Lock)
 *         QeetrixTimelineItem("Sign-in refused", time = "1 h ago", icon = QeetrixIconName.CircleX, tone = QeetrixTimelineTone.Danger)
 *     }
 * }
 * ```
 *
 * In the [QeetrixTimelineVariant.Default] look each event has a 28 dp square marker (`color.bg.neutral.subtle`,
 * a 1 dp `color.border.subtle` edge, 4 dp corners) holding its 16 dp icon, and a 1 dp `color.border.subtle` line runs
 * from it to the next event, broken between groups. Events are `space.gap.lg` apart ([QeetrixTimelineVariant.Compact]:
 * `space.gap.sm`), with nothing after the last.
 *
 * The events, their order, their wording and their times are the app's: the timeline fetches, groups and classifies
 * nothing. Format times before passing them. The full audit log stays a list or a table; this is the short summary on
 * a detail screen. For a long or growing history in a lazy list, use
 * [com.qeetgroup.qeetrix.components.list.QeetrixListItem] rows: a line can't join separate lazy items.
 */
@Composable
public fun QeetrixTimeline(
    modifier: Modifier = Modifier,
    variant: QeetrixTimelineVariant = QeetrixTimelineVariant.Default,
    content: @Composable () -> Unit,
) {
    CompositionLocalProvider(
        LocalTimelineVariant provides variant,
        LocalQeetrixContentColor provides QeetrixTheme.colors.textDefault, // .qx-timeline color
    ) {
        Events(modifier.fillMaxWidth(), variant, content)
    }
}

/**
 * A heading and the events under it, such as one day's — `@qeetrix/react`'s TimelineGroup. Its [label] is a group label
 * in `typography.label.caps` (`color.text.subtle`), `space.gap.sm` above its events; groups are `space.gap.md` apart,
 * and the line between events breaks between them.
 */
@Composable
public fun QeetrixTimelineGroup(
    label: String,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    val variant = LocalTimelineVariant.current
    Layout(
        content = {
            // .qx-timeline-group-label: label.caps, color.text.subtle, margin-block-end space.gap.sm
            QeetrixGroupLabel(label, QeetrixTheme.colors.textSubtle, Modifier.padding(bottom = QeetrixSpace.GapSm))
            Events(Modifier.fillMaxWidth(), variant, content)
        },
        modifier = modifier.fillMaxWidth().then(GroupMarker),
    ) { measurables, constraints ->
        val label = measurables[0].measure(constraints.copy(minWidth = 0, minHeight = 0))
        val events = measurables[1].measure(constraints.copy(minHeight = 0))
        layout(constraints.maxWidth, label.height + events.height) {
            label.placeRelative(0, 0)
            events.placeRelative(0, label.height)
        }
    }
}

/**
 * One event — `@qeetrix/react`'s TimelineItem: its marker, what happened ([title]), where, by whom or how
 * ([description]), and when ([time], as shown: "2 min ago").
 *
 * The title is `body.md` at weight 500 (`color.text.danger` for [QeetrixTimelineTone.Danger]); the description and
 * the time are `body.sm` in `color.text.subtle`, the time at the end of the event on one line. The first lines are
 * centred on the marker. Accessibility services read the event as one element — title, description, time — and the
 * marker not at all. From 150 % text the time moves below the description.
 *
 * @param icon A 16 dp icon for the kind of event, in the marker (the sharp shape). The compact look has no icons.
 */
@Composable
public fun QeetrixTimelineItem(
    title: String,
    modifier: Modifier = Modifier,
    description: String? = null,
    time: String? = null,
    icon: QeetrixIconName? = null,
    tone: QeetrixTimelineTone = QeetrixTimelineTone.Neutral,
) {
    val variant = LocalTimelineVariant.current
    val compact = variant == QeetrixTimelineVariant.Compact
    val colors = QeetrixTheme.colors
    val typography = QeetrixTheme.typography
    val timeBelow = LocalDensity.current.fontScale >= LARGE_FONT_SCALE
    val titleStyle =
        typography.bodyMd.copy(
            // .qx-timeline-title font-weight: label.md's (500); compact: inherit (400)
            fontWeight = if (compact) QeetrixTextWeight.Regular else QeetrixTextWeight.Medium,
            color = if (tone == QeetrixTimelineTone.Danger) colors.textDanger else colors.textDefault,
        )
    val smallStyle = typography.bodySm.copy(color = colors.textSubtle) // .qx-timeline-description, .qx-timeline-time
    val titleLine = firstLineHeight(titleStyle)
    val timeLine = firstLineHeight(smallStyle)
    Layout(
        content = {
            Marker(icon, tone, compact)
            // .qx-timeline-body: a grid of the title and the description.
            Box {
                Layout(
                    content = {
                        // Each line is as wide as the column, as grid items stretch, and starts at the layout's start
                        // whatever its script (`text-align: start` follows the container's direction). Measured to its
                        // own width, an Arabic description in a left-to-right screen broke onto two lines: Android lays
                        // a right-to-left paragraph out a little wider than it measures it (quality finding Q-11).
                        BasicText(title, style = titleStyle.labelDirection(alignToLayout = true))
                        if (description != null) BasicText(description, style = smallStyle.labelDirection(alignToLayout = true))
                        if (time != null && timeBelow) BasicText(time, style = smallStyle.labelDirection(alignToLayout = true))
                    },
                ) { measurables, constraints ->
                    val placeables = measurables.map { it.measure(constraints.copy(minWidth = constraints.maxWidth, minHeight = 0)) }
                    layout(constraints.maxWidth, placeables.sumOf { it.height }) {
                        var y = 0
                        for (placeable in placeables) {
                            placeable.placeRelative(0, y)
                            y += placeable.height
                        }
                    }
                }
            }
            // white-space: nowrap; a short label, it takes its own text's direction ("2 min ago" in a right-to-left layout).
            if (time != null && !timeBelow) BasicText(time, style = smallStyle.labelDirection(), maxLines = 1, softWrap = false)
        },
        modifier = modifier.fillMaxWidth().then(ItemMarker).semantics(mergeDescendants = true) {},
    ) { measurables, constraints ->
        val width = constraints.maxWidth
        val gap = (if (compact) QeetrixSpace.GapSm else QeetrixSpace.GapMd).roundToPx() // column-gap
        val markerPlaceable = measurables[0].measure(Constraints())
        val timePlaceable = measurables.getOrNull(2)?.measure(Constraints(maxWidth = width / 2))
        val bodyWidth = (width - markerPlaceable.width - gap - (timePlaceable?.let { it.width + gap } ?: 0)).coerceAtLeast(0)
        val body = measurables[1].measure(Constraints.fixedWidth(bodyWidth))
        val markerY: Int
        val bodyY: Int
        val timeY: Int
        if (compact) {
            // align-items: baseline; the dot's margin-block-start .5lh − 3px centres it on the title's line.
            bodyY = 0
            markerY = (titleLine.roundToPx() - markerPlaceable.height) / 2
            timeY = timePlaceable?.let { body.baselineOr(0) - it.baselineOr(0) }?.coerceAtLeast(0) ?: 0
        } else {
            // padding-block-start: calc(14px − .5lh) — each first line centred on the 28 dp marker. At large text, where
            // the line outgrows the marker, the marker moves down to the line's centre instead.
            val centre = max(markerPlaceable.height / 2, titleLine.roundToPx() / 2)
            markerY = centre - markerPlaceable.height / 2
            bodyY = centre - titleLine.roundToPx() / 2
            timeY = (centre - timeLine.roundToPx() / 2).coerceAtLeast(0)
        }
        val height = maxOf(markerY + markerPlaceable.height, bodyY + body.height, timePlaceable?.let { timeY + it.height } ?: 0)
        layout(width, height, mapOf(MarkerBottom to markerY + markerPlaceable.height)) {
            markerPlaceable.placeRelative(0, markerY)
            body.placeRelative(markerPlaceable.width + gap, bodyY)
            timePlaceable?.placeRelative(width - timePlaceable.width, timeY)
        }
    }
}

/**
 * `.qx-timeline-marker`: a 28 dp square (`radius.control`, `color.bg.neutral.subtle`, a 1 dp `color.border.subtle`
 * edge inside) holding the icon in `color.icon.<tone>`; in the compact look a 6 dp dot in `color.border.strong` or
 * `color.icon.<tone>`. Decorative (`aria-hidden`).
 */
@Composable
private fun Marker(
    icon: QeetrixIconName?,
    tone: QeetrixTimelineTone,
    compact: Boolean,
) {
    val colors = QeetrixTheme.colors
    val toneColor =
        when (tone) {
            QeetrixTimelineTone.Neutral -> if (compact) colors.borderStrong else colors.iconSubtle
            QeetrixTimelineTone.Brand -> colors.iconBrand
            QeetrixTimelineTone.Info -> colors.iconInfo
            QeetrixTimelineTone.Success -> colors.iconSuccess
            QeetrixTimelineTone.Warning -> colors.iconWarning
            QeetrixTimelineTone.Danger -> colors.iconDanger
        }
    if (compact) {
        Box(Modifier.size(DOT).background(toneColor, QeetrixShapes.Full)) // block-size 6px; radius.full
    } else {
        val shape = QeetrixShapes.Control
        Box(
            Modifier
                .size(MARKER)
                .background(colors.bgNeutralSubtle, shape)
                .border(QeetrixBorder.WidthDefault, colors.borderSubtle, shape), // box-shadow: inset 0 0 0 1px border.subtle
            contentAlignment = Alignment.Center,
        ) {
            if (icon != null) {
                // drawIcon(Icon, { size: 16 })
                QeetrixIcon(icon, contentDescription = null, size = QeetrixSize.IconSm, shape = QeetrixIconShape.Sharp, tint = toneColor)
            }
        }
    }
}

/**
 * The `ol` of events (or of groups): stacked, an item `space.gap.lg` (compact `space.gap.sm`) above the next child, a
 * group `space.gap.md` below the one before it. In the default look a 1 dp `color.border.subtle` line runs from 4 dp
 * under each marker to 4 dp above the end of its item's space (`inset-block: calc(28px + gap.xs) gap.xs`), centred on
 * the marker; none after the last.
 */
@Composable
private fun Events(
    modifier: Modifier,
    variant: QeetrixTimelineVariant,
    content: @Composable () -> Unit,
) {
    val line = QeetrixTheme.colors.borderSubtle
    var lines by remember { mutableStateOf(emptyList<Pair<Int, Int>>()) }
    Layout(
        content,
        modifier.drawBehind {
            if (variant == QeetrixTimelineVariant.Compact) return@drawBehind
            val width = QeetrixBorder.WidthDefault.toPx()
            val centre = MARKER.toPx() / 2
            val x = (if (layoutDirection == LayoutDirection.Rtl) size.width - centre else centre) - width / 2
            for ((from, to) in lines) drawRect(line, Offset(x, from.toFloat()), Size(width, (to - from).toFloat()))
        },
    ) { measurables, constraints ->
        val itemGap = (if (variant == QeetrixTimelineVariant.Compact) QeetrixSpace.GapSm else QeetrixSpace.GapLg).roundToPx()
        val groupGap = QeetrixSpace.GapMd.roundToPx()
        val inset = QeetrixSpace.GapXs.roundToPx()
        val placeables = measurables.map { it.measure(constraints.copy(minHeight = 0)) }
        val tops = IntArray(placeables.size)
        val segments = ArrayList<Pair<Int, Int>>()
        var y = 0
        placeables.forEachIndexed { index, placeable ->
            if (index > 0 && measurables[index].isGroup && measurables[index - 1].isGroup) y += groupGap
            tops[index] = y
            y += placeable.height
            val last = index == placeables.lastIndex
            if (measurables[index].isItem && !last) {
                y += itemGap // padding-block-end; :last-child 0
                val markerBottom = placeable[MarkerBottom].takeIf { it != AlignmentLine.Unspecified } ?: 0
                segments += (tops[index] + markerBottom + inset) to (y - inset)
            }
        }
        if (segments != lines) lines = segments
        layout(constraints.maxWidth, y.coerceIn(constraints.minHeight, constraints.maxHeight)) {
            placeables.forEachIndexed { index, placeable -> placeable.placeRelative(0, tops[index]) }
        }
    }
}

/**
 * `component.timeline.marker-size`, 28px: timeline.css's `grid-template-columns: 28px …` and `.qx-timeline-marker`
 * block-size 28px, which the token agrees with since @qeetrix/tokens 0.0.13.
 */
private val MARKER = QeetrixTimelineComponentTokens.MarkerSize

/** The compact dot: `grid-template-columns: 6px …`, block-size 6px. */
private val DOT = 6.dp

/** Where an item's marker ends, for the line to the next event. */
private val MarkerBottom = HorizontalAlignmentLine(::minOf)

private fun Placeable.baselineOr(fallback: Int): Int = this[FirstBaseline].takeIf { it != AlignmentLine.Unspecified } ?: fallback

private val LocalTimelineVariant = staticCompositionLocalOf { QeetrixTimelineVariant.Default }

/** Whether a child of the events list is an event or a group: its parent data. */
private enum class TimelineChild { Item, Group }

private val Measurable.isItem: Boolean get() = parentData == TimelineChild.Item
private val Measurable.isGroup: Boolean get() = parentData == TimelineChild.Group

private class TimelineChildModifier(
    val kind: TimelineChild,
) : ParentDataModifier {
    override fun Density.modifyParentData(parentData: Any?): Any = kind

    override fun equals(other: Any?): Boolean = other is TimelineChildModifier && other.kind == kind

    override fun hashCode(): Int = kind.hashCode()
}

private val ItemMarker: Modifier = Modifier.then(TimelineChildModifier(TimelineChild.Item))
private val GroupMarker: Modifier = Modifier.then(TimelineChildModifier(TimelineChild.Group))
