package com.qeetgroup.qeetrix.components.tabs

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import com.qeetgroup.qeetrix.foundation.accessibility.QeetrixAccessibility
import com.qeetgroup.qeetrix.foundation.accessibility.QeetrixFocus
import com.qeetgroup.qeetrix.foundation.color.QeetrixOpacity
import com.qeetgroup.qeetrix.foundation.elevation.QeetrixElevation
import com.qeetgroup.qeetrix.foundation.elevation.qeetrixElevation
import com.qeetgroup.qeetrix.foundation.motion.QeetrixDuration
import com.qeetgroup.qeetrix.foundation.motion.QeetrixEasing
import com.qeetgroup.qeetrix.foundation.motion.qeetrixAnimationSpec
import com.qeetgroup.qeetrix.foundation.shape.QeetrixCorner
import com.qeetgroup.qeetrix.foundation.sizing.QeetrixStroke
import com.qeetgroup.qeetrix.icons.QeetrixIcon
import com.qeetgroup.qeetrix.icons.QeetrixIconName
import com.qeetgroup.qeetrix.internal.FontWeightMedium
import com.qeetgroup.qeetrix.internal.QeetrixFont
import com.qeetgroup.qeetrix.internal.QeetrixWebScale
import com.qeetgroup.qeetrix.internal.labelDirection
import com.qeetgroup.qeetrix.internal.qeetrixFocusRing
import com.qeetgroup.qeetrix.internal.qeetrixTextStyle
import com.qeetgroup.qeetrix.theme.QeetrixTheme
import com.qeetgroup.qeetrix.tokens.generated.QeetrixInputComponentTokens

/** The two treatments of the Tabs family, one selection language (`value` is the web name). */
public enum class QeetrixTabsVariant(
    public val value: String,
) {
    /**
     * Contained: a sunken well holds the tabs and the selected one is the raised surface inside it — a view
     * switch for a card, a toolbar or a panel.
     */
    Default("default"),

    /**
     * Underline: tabs sit on a 1 dp track and the selected one carries a 2 dp Qeet indicator — section-level
     * navigation within a screen.
     */
    Line("line"),
}

/** One tab: the app's own [value] for it, its [label], an optional [icon], and whether it is [enabled]. */
@Immutable
public class QeetrixTab<out T>(
    public val value: T,
    public val label: String,
    public val icon: QeetrixIconName? = null,
    public val enabled: Boolean = true,
) {
    override fun equals(other: Any?): Boolean =
        other is QeetrixTab<*> && value == other.value && label == other.label && icon == other.icon && enabled == other.enabled

    override fun hashCode(): Int {
        var result = value.hashCode()
        result = 31 * result + label.hashCode()
        result = 31 * result + (icon?.hashCode() ?: 0)
        result = 31 * result + enabled.hashCode()
        return result
    }

    override fun toString(): String = "QeetrixTab($value, $label)"
}

/**
 * A row of tabs that switches between views of related content in one place — the web's `TabsList` and
 * `TabsTrigger`, in its [QeetrixTabsVariant.Default] or [QeetrixTabsVariant.Line] treatment.
 *
 * - **The app owns the selection and draws the panel.** [selected] is the app's value; [onSelect] is called
 *   with a newly chosen one. Show the panel for the selected value below the tabs — a `when`, or
 *   `AnimatedContent` for a transition. There is no pager and no page framework.
 * - **Not top-level navigation.** Tabs switch content within a screen; an app's own destinations belong in
 *   [com.qeetgroup.qeetrix.components.navigation.QeetrixNavigationBar].
 * - **Overflow.** Tabs that do not fit scroll sideways, and the selected tab is kept in view. Labels never
 *   truncate.
 * - **Accessibility.** One selectable group; each tab is a tab, read with its label and whether it is
 *   selected, and a disabled tab is read as unavailable. The drawn row keeps the web's height and a 48 dp
 *   touch area is reserved around it. Keyboard and D-pad focus visit each tab (the web makes the row one
 *   stop with arrow keys between tabs; on Android each tab is its own stop, as on iOS).
 *
 * ```
 * var section by rememberSaveable { mutableStateOf(Section.Overview) }
 * QeetrixTabs(
 *     tabs = listOf(QeetrixTab(Section.Overview, "Overview"), QeetrixTab(Section.Activity, "Activity", QeetrixIconName.Bell)),
 *     selected = section,
 *     onSelect = { section = it },
 * )
 * when (section) {
 *     Section.Overview -> OverviewPanel()
 *     Section.Activity -> ActivityPanel()
 * }
 * ```
 */
@Composable
public fun <T> QeetrixTabs(
    tabs: List<QeetrixTab<T>>,
    selected: T,
    onSelect: (T) -> Unit,
    modifier: Modifier = Modifier,
    variant: QeetrixTabsVariant = QeetrixTabsVariant.Default,
) {
    require(tabs.map { it.value }.toSet().size == tabs.size) { "Each tab needs a distinct value." }
    val scroll = rememberScrollState()
    // The 48 dp touch area: taps in the band around the drawn row reach the nearest tab.
    Box(modifier.heightIn(min = QeetrixAccessibility.MinimumTouchTarget), contentAlignment = Alignment.CenterStart) {
        if (variant == QeetrixTabsVariant.Default) {
            // inline-flex w-fit max-w-full gap-1 rounded-lg bg-surface-sunken p-0.75 h-(--qx-control-height)
            Row(
                Modifier
                    .horizontalScroll(scroll)
                    .background(QeetrixTheme.colors.surfaceSunken, RoundedCornerShape(QeetrixCorner.Control))
                    // The control height, growing with the text size rather than clipping it.
                    .heightIn(min = QeetrixInputComponentTokens.height(QeetrixTheme.density))
                    .height(IntrinsicSize.Min)
                    .padding(QeetrixWebScale.spacing(0.75f))
                    .width(IntrinsicSize.Max)
                    .selectableGroup(),
                horizontalArrangement = Arrangement.spacedBy(QeetrixWebScale.spacing(1f)),
            ) {
                for (tab in tabs) {
                    key(tab.value) {
                        val cell = Modifier.weight(1f).fillMaxHeight()
                        Tab(tab, tab.value == selected, { onSelect(tab.value) }, QeetrixTabsVariant.Default, cell)
                    }
                }
            }
        } else {
            val track = QeetrixTheme.colors.borderDefault
            // w-full gap-1 border-b border-border
            Row(
                Modifier
                    .fillMaxWidth()
                    .drawBehind {
                        val y = size.height - QeetrixStroke.Hairline.toPx() / 2
                        drawLine(track, Offset(0f, y), Offset(size.width, y), QeetrixStroke.Hairline.toPx())
                    }.horizontalScroll(scroll)
                    .selectableGroup(),
                horizontalArrangement = Arrangement.spacedBy(QeetrixWebScale.spacing(1f)),
            ) {
                for (tab in tabs) {
                    key(tab.value) {
                        Tab(
                            tab,
                            tab.value == selected,
                            { onSelect(tab.value) },
                            QeetrixTabsVariant.Line,
                            // h-[calc(var(--qx-control-height)+var(--spacing)*2)], growing with the text size
                            Modifier.heightIn(min = QeetrixInputComponentTokens.height(QeetrixTheme.density) + QeetrixWebScale.spacing(2f)),
                        )
                    }
                }
            }
        }
    }
}

/**
 * One tab. Default: the raised surface when selected, the muted label otherwise, brightening under the
 * pointer. Line: the 2 dp indicator along the bottom — the brand colour when selected, a neutral preview
 * under the pointer. Both draw the inset focus ring for keyboard focus.
 */
@Composable
private fun Tab(
    tab: QeetrixTab<*>,
    selected: Boolean,
    onClick: () -> Unit,
    variant: QeetrixTabsVariant,
    modifier: Modifier,
) {
    val source = remember { MutableInteractionSource() }
    val hovered by source.collectIsHoveredAsState()
    val pressed by source.collectIsPressedAsState()
    val colors = QeetrixTheme.colors
    val shape = RoundedCornerShape(QeetrixCorner.Chip) // rounded-md
    val lit = tab.enabled && (hovered || pressed)
    val spec = qeetrixAnimationSpec<Color>(QeetrixEasing.Standard, QeetrixDuration.Fast) // duration-fast ease-standard
    // text-muted-foreground; hover:text-foreground; data-[active]:text-foreground
    val content by animateColorAsState(if (selected || lit) colors.textPrimary else colors.textTertiary, spec, label = "tab-content")
    val indicatorColor = if (selected) colors.borderBrand else colors.borderStrong
    val indicator by animateFloatAsState(
        if (selected || lit) 1f else 0f,
        qeetrixAnimationSpec(QeetrixEasing.Standard, QeetrixDuration.Fast),
        label = "tab-indicator",
    )
    // A newly selected tab scrolls into view; the first selection does not, so a screen does not scroll to its tabs.
    val requester = remember { BringIntoViewRequester() }
    var settled by remember { mutableStateOf(false) }
    LaunchedEffect(selected) {
        if (selected && settled) requester.bringIntoView()
        settled = true
    }

    val surface =
        if (variant == QeetrixTabsVariant.Default && selected) {
            // data-[active]:bg-surface-elevated data-[active]:shadow-rest
            Modifier.qeetrixElevation(QeetrixElevation.Raised, shape, colors.surfaceElevated)
        } else {
            Modifier
        }
    val line =
        if (variant == QeetrixTabsVariant.Line) {
            Modifier.drawBehind {
                if (indicator == 0f) return@drawBehind
                val thickness = QeetrixWebScale.spacing(0.5f).toPx() // after:h-0.5
                drawRoundRect(
                    indicatorColor.copy(alpha = indicatorColor.alpha * indicator),
                    Offset(0f, size.height - thickness),
                    Size(size.width, thickness),
                    CornerRadius(thickness / 2),
                )
            }
        } else {
            Modifier
        }
    Row(
        modifier
            .bringIntoViewRequester(requester)
            .then(surface)
            .then(line)
            .qeetrixFocusRing(source, shape, offset = -QeetrixFocus.OutlineWidth) // focus-visible:focus-ring-inset
            .selectable(selected, enabled = tab.enabled, role = Role.Tab, interactionSource = source, indication = null, onClick = onClick)
            .then(if (tab.enabled) Modifier else Modifier.alpha(QeetrixOpacity.Disabled))
            // px-2.5 (default) / px-3 (line)
            .padding(horizontal = QeetrixWebScale.spacing(if (variant == QeetrixTabsVariant.Line) 3f else 2.5f)),
        horizontalArrangement = Arrangement.spacedBy(QeetrixWebScale.spacing(1.5f), Alignment.CenterHorizontally), // gap-1.5
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (tab.icon != null) QeetrixIcon(tab.icon, contentDescription = null, size = QeetrixWebScale.spacing(4f), tint = content) // size-4
        BasicText(tab.label, style = LabelStyle.copy(color = content).labelDirection(), softWrap = false) // whitespace-nowrap
    }
}

/** text-sm font-medium. */
private val LabelStyle = qeetrixTextStyle(QeetrixFont.Ui, QeetrixWebScale.TextSm, FontWeightMedium)
