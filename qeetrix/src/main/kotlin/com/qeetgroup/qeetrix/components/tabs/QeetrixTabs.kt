package com.qeetgroup.qeetrix.components.tabs

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.AlignmentLine
import androidx.compose.ui.layout.FirstBaseline
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.onPlaced
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.isTraversalGroup
import androidx.compose.ui.semantics.paneTitle
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.qeetgroup.qeetrix.components.badge.QeetrixBadge
import com.qeetgroup.qeetrix.components.badge.QeetrixBadgeTone
import com.qeetgroup.qeetrix.foundation.accessibility.QeetrixAccessibility
import com.qeetgroup.qeetrix.foundation.motion.QeetrixMotion
import com.qeetgroup.qeetrix.foundation.motion.qeetrixAnimationSpec
import com.qeetgroup.qeetrix.foundation.shape.QeetrixShapes
import com.qeetgroup.qeetrix.foundation.sizing.QeetrixBorder
import com.qeetgroup.qeetrix.foundation.sizing.QeetrixSize
import com.qeetgroup.qeetrix.foundation.spacing.QeetrixSpace
import com.qeetgroup.qeetrix.icons.QeetrixIcon
import com.qeetgroup.qeetrix.icons.QeetrixIconName
import com.qeetgroup.qeetrix.icons.QeetrixIconShape
import com.qeetgroup.qeetrix.icons.QeetrixIconVariant
import com.qeetgroup.qeetrix.internal.FocusFrame
import com.qeetgroup.qeetrix.internal.QeetrixTextWeight
import com.qeetgroup.qeetrix.internal.labelDirection
import com.qeetgroup.qeetrix.internal.qeetrixFocusFrame
import com.qeetgroup.qeetrix.theme.QeetrixTheme
import kotlinx.coroutines.launch
import java.text.NumberFormat

/** The look of [QeetrixTabs] (`value` is `@qeetrix/react`'s TabsList `variant`). */
public enum class QeetrixTabsVariant(
    public val value: String,
) {
    /**
     * Tabs over a 2 dp `color.border.subtle` rule, the selected one marked by the 3 dp Ember bar: the views of a
     * screen. The default.
     */
    Line("line"),

    /**
     * A segmented strip — a 1 dp `color.border.default` edge round it and between segments, 4 dp outer corners, on
     * `color.bg.neutral.subtle` — the selected segment on `color.surface.default` with the Ember bar: a few views of
     * one card or panel.
     */
    Contained("contained"),
}

/** Which way [QeetrixTabs] run (`value` is `@qeetrix/react`'s `orientation`). */
public enum class QeetrixTabsOrientation(
    public val value: String,
) {
    /** A row, the rule and the bar along the bottom edge; it scrolls sideways when its tabs don't fit. The default. */
    Horizontal("horizontal"),

    /** A column, the rule and the bar on the start edge, as for the current navigation item (design rules §11). */
    Vertical("vertical"),
}

/**
 * One tab — `@qeetrix/react`'s Tab: the app's own [value] for it, its [label], and optionally a leading [icon], a
 * trailing [badge] count, and whether it is [enabled].
 *
 * @param icon A 16 dp leading icon, in the sharp shape.
 * @param filledWhenSelected Draw [icon]'s filled form while the tab is selected (design rules §16); an icon without a
 *   filled drawing keeps its outline.
 * @param badge A count after the label, on its baseline, formatted for the locale and shown as a [QeetrixBadge].
 * @param badgeTone The badge's tone: neutral, or e.g. [QeetrixBadgeTone.Danger] for failures to look at.
 */
@Immutable
public class QeetrixTab<out T>(
    public val value: T,
    public val label: String,
    public val icon: QeetrixIconName? = null,
    public val filledWhenSelected: Boolean = true,
    public val badge: Int? = null,
    public val badgeTone: QeetrixBadgeTone = QeetrixBadgeTone.Neutral,
    public val enabled: Boolean = true,
) {
    override fun equals(other: Any?): Boolean =
        other is QeetrixTab<*> &&
            value == other.value &&
            label == other.label &&
            icon == other.icon &&
            filledWhenSelected == other.filledWhenSelected &&
            badge == other.badge &&
            badgeTone == other.badgeTone &&
            enabled == other.enabled

    override fun hashCode(): Int {
        var result = value.hashCode()
        result = 31 * result + label.hashCode()
        result = 31 * result + (icon?.hashCode() ?: 0)
        result = 31 * result + filledWhenSelected.hashCode()
        result = 31 * result + (badge ?: 0)
        result = 31 * result + badgeTone.hashCode()
        result = 31 * result + enabled.hashCode()
        return result
    }

    override fun toString(): String = "QeetrixTab($value, $label)"
}

/**
 * Views of the same thing, one at a time — `@qeetrix/react`'s Tabs and TabsList: a user's profile, security and
 * sessions. One or two words per tab, nouns, in sentence case.
 *
 * - **The app owns the selection and draws the panel.** [value] is the selected tab's value; [onValueChange] is
 *   called with a newly chosen one. Show the panel for [value] below the tabs — a `when`, or `AnimatedContent`.
 *   There is no pager.
 * - **Look.** Tabs are `size.control.lg` tall with the density's inset (16 dp, 12 dp compact), `label.md` in
 *   `color.text.subtle`, `color.text.default` under the pointer. The selected tab is `color.text.default` at weight
 *   600 with its icon filled, and the 3 dp Ember bar (`border.width.accent`, `color.border.brand`) slides to it in
 *   `motion.duration.normal` — at once under reduced motion.
 * - **Overflow.** Horizontal tabs that don't fit scroll sideways, and a newly selected tab is kept in view. Labels
 *   never truncate.
 * - **Not app navigation.** Top-level destinations belong in
 *   [com.qeetgroup.qeetrix.components.navigation.QeetrixNavigationBar].
 * - **Accessibility.** One selectable group; each tab is a tab, read with its label, its count and whether it is
 *   selected; a disabled tab is read as unavailable. The drawn row keeps the web's height inside a 48 dp touch
 *   area; vertical tabs, which stack flush, are each 48 dp tall. Keyboard and D-pad focus visit each tab and, with
 *   [activateOnFocus] (the default, as the web's), select it as they arrive; with it `false` — panels that load slowly —
 *   Enter selects (the web makes the list one stop with arrow keys between tabs).
 * - **Panels.** Put each tab's content in a [QeetrixTabPanel], named by its tab, after the tabs.
 *
 * ```
 * var section by rememberSaveable { mutableStateOf(Section.Profile) }
 * QeetrixTabs(
 *     tabs = listOf(
 *         QeetrixTab(Section.Profile, "Profile", QeetrixIconName.UserRound),
 *         QeetrixTab(Section.Sessions, "Sessions", badge = 3),
 *     ),
 *     value = section,
 *     onValueChange = { section = it },
 * )
 * when (section) {
 *     Section.Profile -> ProfilePanel()
 *     Section.Sessions -> SessionsPanel()
 * }
 * ```
 */
@Composable
public fun <T> QeetrixTabs(
    tabs: List<QeetrixTab<T>>,
    value: T,
    onValueChange: (T) -> Unit,
    modifier: Modifier = Modifier,
    variant: QeetrixTabsVariant = QeetrixTabsVariant.Line,
    orientation: QeetrixTabsOrientation = QeetrixTabsOrientation.Horizontal,
    activateOnFocus: Boolean = true,
) {
    require(tabs.map { it.value }.toSet().size == tabs.size) { "Each tab needs a distinct value." }
    val colors = QeetrixTheme.colors
    val contained = variant == QeetrixTabsVariant.Contained
    val vertical = orientation == QeetrixTabsOrientation.Vertical
    val bar = rememberBar(value)
    val barColor = colors.borderBrand
    val content: @Composable () -> Unit = {
        tabs.forEachIndexed { index, tab ->
            key(tab.value) {
                Tab(
                    tab = tab,
                    selected = tab.value == value,
                    onClick = { onValueChange(tab.value) },
                    contained = contained,
                    vertical = vertical,
                    join = contained && index > 0,
                    activateOnFocus = activateOnFocus,
                    modifier = Modifier.onPlaced { bar.place(tab.value, it.positionInParent(), it.size.width, it.size.height, vertical) },
                )
            }
        }
    }
    // The bar: `.qx-tabs-indicator`, drawn over the rule in the tabs' own (scrolled) space.
    val drawBar =
        Modifier.drawWithContent {
            drawContent()
            val (start, length) = bar.current() ?: return@drawWithContent
            val thickness = QeetrixBorder.WidthAccent.toPx()
            if (vertical) {
                // inset-inline-start: −border.width.strong (line, over the rule) or 0 (contained, inside the edge)
                val x = if (layoutDirection == LayoutDirection.Rtl) size.width - thickness else 0f
                drawRect(barColor, Offset(x, start), Size(thickness, length))
            } else {
                drawRect(barColor, Offset(start, size.height - thickness), Size(length, thickness)) // inset-block-end: 0
            }
        }
    val rule = colors.borderSubtle
    val list =
        if (vertical) {
            if (contained) {
                Modifier
                    .width(IntrinsicSize.Max)
                    .strip()
                    .then(drawBar)
            } else {
                // border-inline-start: border.width.strong solid color.border.subtle; the bar sits over it.
                Modifier
                    .width(IntrinsicSize.Max)
                    .drawBehind {
                        val width = QeetrixBorder.WidthStrong.toPx()
                        val x = if (layoutDirection == LayoutDirection.Rtl) size.width - width else 0f
                        drawRect(rule, Offset(x, 0f), Size(width, size.height))
                    }.then(drawBar)
                    .padding(start = QeetrixBorder.WidthStrong)
            }
        } else {
            Modifier
        }
    if (vertical) {
        Column(modifier.then(list).selectableGroup()) { content() }
    } else {
        val scroll = rememberScrollState()
        // The 48 dp touch area: taps in the band round the drawn list reach the nearest tab.
        Box(modifier.heightIn(min = QeetrixAccessibility.MinimumTouchTarget), contentAlignment = Alignment.CenterStart) {
            if (contained) {
                // inline-size: fit-content; max-inline-size: 100%
                Row(
                    Modifier
                        .strip()
                        .horizontalScroll(scroll)
                        .then(drawBar)
                        .selectableGroup(),
                ) { content() }
            } else {
                // box-shadow: inset 0 −border.width.strong 0 color.border.subtle — under the whole list.
                Box(
                    Modifier.fillMaxWidth().drawBehind {
                        val height = QeetrixBorder.WidthStrong.toPx()
                        drawRect(rule, Offset(0f, size.height - height), Size(size.width, height))
                    },
                ) {
                    Row(Modifier.horizontalScroll(scroll).then(drawBar).selectableGroup()) { content() }
                }
            }
        }
    }
}

/** `.qx-tabs-list[data-variant="contained"]`: 1 dp `color.border.default`, `radius.control`, `color.bg.neutral.subtle`. */
@Composable
private fun Modifier.strip(): Modifier {
    val colors = QeetrixTheme.colors
    val shape = QeetrixShapes.Control
    return this
        .clip(shape)
        .background(colors.bgNeutralSubtle)
        .border(QeetrixBorder.WidthDefault, colors.borderDefault, shape)
        .padding(QeetrixBorder.WidthDefault)
}

/**
 * One tab. The line look brightens its label under the pointer; the contained look also tints the segment
 * (`color.bg.neutral.subtle-hover`) and puts the selected one on `color.surface.default`. Keyboard focus draws the
 * 2 dp Ember frame inside the tab: the list scrolls, and a scrolling box clips what is drawn outside it (06 §Tabs).
 */
@Composable
private fun Tab(
    tab: QeetrixTab<*>,
    selected: Boolean,
    onClick: () -> Unit,
    contained: Boolean,
    vertical: Boolean,
    join: Boolean,
    activateOnFocus: Boolean,
    modifier: Modifier,
) {
    val source = remember { MutableInteractionSource() }
    val hovered by source.collectIsHoveredAsState()
    val pressed by source.collectIsPressedAsState()
    val colors = QeetrixTheme.colors
    val density = QeetrixTheme.density
    val lit = tab.enabled && (hovered || pressed)
    val spec = qeetrixAnimationSpec<Color>(QeetrixMotion.EasingStandard, QeetrixMotion.DurationFast) // transition: color fast standard
    val content by animateColorAsState(
        when {
            !tab.enabled -> colors.textDisabled
            selected || lit -> colors.textDefault
            else -> colors.textSubtle
        },
        spec,
        label = "tab-content",
    )
    val fill by animateColorAsState(
        when {
            !contained -> Color.Transparent
            selected -> colors.surfaceDefault
            tab.enabled && pressed -> colors.bgNeutralSubtlePressed
            tab.enabled && hovered -> colors.bgNeutralSubtleHover
            else -> Color.Transparent
        },
        spec,
        label = "tab-fill",
    )
    val joinColor = colors.borderDefault
    // --qx-tabs-inset: clamp(12px, size.control.lg − 20px, 16px)
    val inset = (density.sizeControlLg - 20.dp).coerceIn(12.dp, 16.dp)
    // block-size: size.control.lg; contained: size.control.lg − 2 × border.width.default (inside the strip's edge)
    val height = if (contained) density.sizeControlLg - QeetrixBorder.WidthDefault * 2 else density.sizeControlLg
    val requester = remember { BringIntoViewRequester() }
    var settled by remember { mutableStateOf(false) }
    // A newly selected tab scrolls into view; the first selection does not, so a screen does not scroll to its tabs.
    LaunchedEffect(selected) {
        if (selected && settled) requester.bringIntoView()
        settled = true
    }
    val typography = QeetrixTheme.typography
    val label = (if (selected) typography.labelMd.copy(fontWeight = QeetrixTextWeight.Semibold) else typography.labelMd)
    Row(
        modifier
            .bringIntoViewRequester(requester)
            .background(fill)
            .drawBehind {
                if (!join) return@drawBehind
                // .qx-tab + .qx-tab: border-inline-start (vertical: border-block-start) 1 dp color.border.default
                val width = QeetrixBorder.WidthDefault.toPx()
                if (vertical) {
                    drawRect(joinColor, Offset.Zero, Size(size.width, width))
                } else {
                    val x = if (layoutDirection == LayoutDirection.Rtl) size.width - width else 0f
                    drawRect(joinColor, Offset(x, 0f), Size(width, size.height))
                }
            }.qeetrixFocusFrame(source, RectangleShape, FocusFrame.Inside)
            // TabsList activateOnFocus: keyboard or D-pad focus selects the tab. Touch moves no focus, and TalkBack's
            // focus is not input focus, so neither selects by passing over a tab.
            .onFocusChanged { if (activateOnFocus && it.isFocused && !selected && tab.enabled) onClick() }
            .selectable(selected, enabled = tab.enabled, role = Role.Tab, interactionSource = source, indication = null, onClick = onClick)
            .then(if (vertical) Modifier.fillMaxWidth() else Modifier)
            // Grows with the text size instead of clipping. Vertical tabs stack flush, so each is a whole 48 dp touch
            // target (quality finding Q-2); horizontal ones sit in the list's 48 dp band.
            .heightIn(min = if (vertical) maxOf(height, QeetrixAccessibility.MinimumTouchTarget) else height)
            .padding(
                start = inset + if (join && !vertical) QeetrixBorder.WidthDefault else 0.dp,
                end = inset,
                top = if (join && vertical) QeetrixBorder.WidthDefault else 0.dp,
            ),
        horizontalArrangement = Arrangement.spacedBy(QeetrixSpace.GapSm), // gap: space.gap.sm
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (tab.icon != null) {
            QeetrixIcon(
                tab.icon,
                contentDescription = null,
                size = QeetrixSize.IconSm, // size: 16
                shape = QeetrixIconShape.Sharp,
                variant = if (selected && tab.filledWhenSelected) QeetrixIconVariant.Filled else QeetrixIconVariant.Outline,
                tint = content,
            )
        }
        LabelWithBadge(tab.label, label.copy(color = content).labelDirection(), tab.badge, tab.badgeTone)
    }
}

/**
 * `.qx-tab-label`: the label and its count on one baseline, `space.gap.sm` apart. The count (`.qx-tab-badge`) takes
 * no more height than the label's line, so a taller Badge pill can't push the label off centre.
 */
@Composable
private fun LabelWithBadge(
    label: String,
    style: TextStyle,
    badge: Int?,
    tone: QeetrixBadgeTone,
) {
    val locale = LocalConfiguration.current.locales[0]
    val count = remember(badge, locale) { badge?.let { NumberFormat.getIntegerInstance(locale).format(it) } }
    Layout(
        content = {
            BasicText(label, style = style, softWrap = false, maxLines = 1) // white-space: nowrap
            if (count != null) QeetrixBadge(count, tone = tone)
        },
    ) { measurables, _ ->
        val loose = Constraints()
        val text = measurables[0].measure(loose)
        val badgePlaceable = measurables.getOrNull(1)?.measure(loose)
        val gap = QeetrixSpace.GapSm.roundToPx()
        val baseline = text[FirstBaseline].takeIf { it != AlignmentLine.Unspecified } ?: 0
        val badgeY = badgePlaceable?.let { baseline - (it[FirstBaseline].takeIf { line -> line != AlignmentLine.Unspecified } ?: 0) } ?: 0
        val width = text.width + (badgePlaceable?.let { gap + it.width } ?: 0)
        layout(width, text.height) {
            text.placeRelative(0, 0)
            badgePlaceable?.placeRelative(text.width + gap, badgeY)
        }
    }
}

/**
 * Where the Ember bar is: each tab's span along the list (measured as it is placed) and the selected one's, moving
 * from one to the next in `motion.duration.normal` with `motion.easing.standard` — the web's transition of the
 * indicator's `translate` and `inline-size`. The first position is taken at once.
 */
private class TabsBar {
    val spans = mutableStateMapOf<Any?, Pair<Float, Float>>()
    val start = Animatable(0f)
    val length = Animatable(0f)
    var placed by mutableStateOf(false)
    var target: Any? = null

    fun place(
        value: Any?,
        position: Offset,
        width: Int,
        height: Int,
        vertical: Boolean,
    ) {
        val span = if (vertical) position.y to height.toFloat() else position.x to width.toFloat()
        if (spans[value] != span) spans[value] = span
    }

    /** The bar's start and length now, or null before the selected tab is placed. */
    fun current(): Pair<Float, Float>? {
        val span = spans[target] ?: return null
        return if (placed) start.value to length.value else span
    }
}

@Composable
private fun rememberBar(value: Any?): TabsBar {
    val bar = remember { TabsBar() }
    bar.target = value
    val span = bar.spans[value]
    val spec = qeetrixAnimationSpec<Float>(QeetrixMotion.EasingStandard, QeetrixMotion.DurationNormal)
    LaunchedEffect(span) {
        val (start, length) = span ?: return@LaunchedEffect
        if (!bar.placed) {
            bar.start.snapTo(start)
            bar.length.snapTo(length)
            bar.placed = true
        } else {
            launch { bar.start.animateTo(start, spec) }
            launch { bar.length.animateTo(length, spec) }
        }
    }
    return bar
}

/**
 * A tab's panel — `@qeetrix/react`'s TabsPanel: the content of [tab], composed only while [tab] is the [value] of its
 * [QeetrixTabs], named by the tab's label for accessibility services (a pane titled "Sessions"), so moving into it says
 * which tab it belongs to. Place panels after the tabs, as the web's follow the list in the Tab order.
 *
 * ```
 * QeetrixTabs(tabs, section, { section = it })
 * for (tab in tabs) QeetrixTabPanel(tab, section) { SectionContent(tab.value) }
 * ```
 */
@Composable
public fun <T> QeetrixTabPanel(
    tab: QeetrixTab<T>,
    value: T,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    if (tab.value != value) return
    androidx.compose.foundation.layout.Box(
        modifier.semantics {
            paneTitle = tab.label
            isTraversalGroup = true
        },
    ) { content() }
}
