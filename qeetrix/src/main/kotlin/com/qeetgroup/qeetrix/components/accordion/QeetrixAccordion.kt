package com.qeetgroup.qeetrix.components.accordion

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.collapse
import androidx.compose.ui.semantics.expand
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.qeetgroup.qeetrix.R
import com.qeetgroup.qeetrix.components.button.ButtonFrame
import com.qeetgroup.qeetrix.components.button.QeetrixButtonSize
import com.qeetgroup.qeetrix.components.button.QeetrixButtonTone
import com.qeetgroup.qeetrix.components.button.QeetrixButtonVariant
import com.qeetgroup.qeetrix.components.button.buttonMetrics
import com.qeetgroup.qeetrix.foundation.accessibility.QeetrixAccessibility
import com.qeetgroup.qeetrix.foundation.motion.QeetrixMotion
import com.qeetgroup.qeetrix.foundation.motion.qeetrixAnimationSpec
import com.qeetgroup.qeetrix.foundation.sizing.QeetrixBorder
import com.qeetgroup.qeetrix.foundation.sizing.QeetrixSize
import com.qeetgroup.qeetrix.foundation.spacing.QeetrixSpace
import com.qeetgroup.qeetrix.icons.QeetrixIcon
import com.qeetgroup.qeetrix.icons.QeetrixIconName
import com.qeetgroup.qeetrix.icons.QeetrixIconShape
import com.qeetgroup.qeetrix.internal.FocusFrame
import com.qeetgroup.qeetrix.internal.atAlpha
import com.qeetgroup.qeetrix.internal.firstLineHeight
import com.qeetgroup.qeetrix.internal.labelDirection
import com.qeetgroup.qeetrix.internal.qeetrixFocusFrame
import com.qeetgroup.qeetrix.theme.LocalQeetrixContentColor
import com.qeetgroup.qeetrix.theme.QeetrixTheme

/**
 * Stacked sections that show and hide, ruled like every surface — `@qeetrix/react`'s Accordion. Put
 * [QeetrixAccordionItem]s in it: a 1 dp `color.border.subtle` rule runs above each and below the last.
 *
 * ```
 * var open by rememberSaveable { mutableStateOf(listOf("tax")) }
 * QeetrixAccordion(value = open, onValueChange = { open = it }) {
 *     QeetrixAccordionItem("tax", "Tax settings") { QeetrixText("GST is added at 18%.") }
 *     QeetrixAccordionItem("numbering", "Invoice numbering") { QeetrixText("INV-YYYY-NNNN") }
 * }
 * ```
 *
 * - **The app owns what is open.** [value] is the open sections' values — a list, even when only one can be open —
 *   and [onValueChange] is called with the new list when a section opens or closes. Without [multiple], opening one
 *   closes any other. Keep the list in `rememberSaveable` to survive configuration changes; sections are found by
 *   their values, so inserting, removing or reordering them keeps each one's state.
 * - [enabled] `false` disables every section: none opens or closes.
 * - Titles are a question or a topic in a few words, in sentence case. Keep each section short; a long one wants its
 *   own screen. In a lazy list, put the `LazyColumn` inside the accordion (with `Modifier.weight(1f)` or a bounded
 *   height) and a [QeetrixAccordionItem] in each lazy item.
 */
@Composable
public fun <T> QeetrixAccordion(
    value: List<T>,
    onValueChange: (List<T>) -> Unit,
    modifier: Modifier = Modifier,
    multiple: Boolean = false,
    enabled: Boolean = true,
    content: @Composable ColumnScope.() -> Unit,
) {
    val rule = QeetrixTheme.colors.borderSubtle

    @Suppress("UNCHECKED_CAST")
    val state = AccordionState(value as List<Any?>, { onValueChange(it as List<T>) }, multiple, enabled)
    CompositionLocalProvider(LocalAccordion provides state) {
        Column(
            modifier
                .fillMaxWidth()
                .drawBehind {
                    // border-block-end: border.width.default solid color.border.subtle
                    val height = QeetrixBorder.WidthDefault.toPx()
                    drawRect(rule, Offset(0f, size.height - height), Size(size.width, height))
                }.padding(bottom = QeetrixBorder.WidthDefault),
            content = content,
        )
    }
}

/**
 * One section of a [QeetrixAccordion] — `@qeetrix/react`'s AccordionItem: a full-width trigger with its [title] and a
 * turning chevron, then the [content] it shows and hides. [value] names the section in the accordion's list.
 *
 * - **The trigger** is `size.control.lg` tall, grown to Android's 48 dp touch target (triggers stack flush, so no
 *   touch-area expansion can make up the difference): the title in `title.sm` (`color.text.default`), then a 16 dp
 *   sharp `chevron-down` in `color.icon.subtle` that turns 180° when open, in `motion.duration.normal`. It is one
 *   button and a heading for accessibility services, read with its title and "Expanded" or "Collapsed"
 *   (`qx_expanded`, `qx_collapsed`), with expand and collapse actions. Keyboard focus draws the 2 dp Ember frame
 *   inside it; Enter and Space toggle it. Each trigger is its own focus stop.
 * - **[description]** is a short summary under the title in the trigger — today's choice while closed ("6 digits ·
 *   every 30 seconds · SHA-1") — in `body.sm`, `color.text.subtle`, read with the title and the state; the
 *   chevron stays on the title's first line.
 * - **The panel** opens and closes by height over `motion.duration.normal` with `motion.easing.standard` (at once
 *   under reduced motion), 16 dp of space below its content, which is `body.md` in `color.text.default` —
 *   [com.qeetgroup.qeetrix.components.typography.QeetrixText]'s default. A closed panel leaves the composition: keep
 *   state that must survive a close in your own state holder.
 * - [enabled] `false` takes the disabled colour and the section can't be toggled.
 */
@Composable
public fun <T> QeetrixAccordionItem(
    value: T,
    title: String,
    modifier: Modifier = Modifier,
    description: String? = null,
    enabled: Boolean = true,
    interactionSource: MutableInteractionSource? = null,
    content: @Composable () -> Unit,
) {
    val accordion = checkNotNull(LocalAccordion.current) { "A QeetrixAccordionItem goes inside a QeetrixAccordion." }
    val open = value in accordion.value
    val active = enabled && accordion.enabled
    val source = interactionSource ?: remember { MutableInteractionSource() }
    val colors = QeetrixTheme.colors
    val rule = colors.borderSubtle
    val motion = qeetrixAnimationSpec<Float>(QeetrixMotion.EasingStandard, QeetrixMotion.DurationNormal)
    val turn by animateFloatAsState(if (open) 180f else 0f, motion, label = "accordion-chevron") // [data-panel-open] rotate 180deg
    val state = stringResource(if (open) R.string.qx_expanded else R.string.qx_collapsed)
    val toggle = { next: Boolean -> accordion.toggle(value, next) }

    Column(
        modifier
            .fillMaxWidth()
            .drawBehind {
                // .qx-accordion-item border-block-start: border.width.default solid color.border.subtle
                drawRect(rule, Offset.Zero, Size(size.width, QeetrixBorder.WidthDefault.toPx()))
            }.padding(top = QeetrixBorder.WidthDefault),
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .clickable(source, indication = null, enabled = active, role = Role.Button) { toggle(!open) }
                .semantics {
                    heading()
                    stateDescription = state
                    if (active) {
                        if (open) {
                            collapse {
                                toggle(false)
                                true
                            }
                        } else {
                            expand {
                                toggle(true)
                                true
                            }
                        }
                    }
                }.qeetrixFocusFrame(source, RectangleShape, FocusFrame.Inside) // outline-offset: −focus.ring.width
                // min-block-size: size.control.lg, grown to the 48 dp touch target: the triggers stack flush, so a touch
                // between two can't be sent to the nearer by hit-test expansion (quality finding Q-1).
                .heightIn(min = maxOf(QeetrixTheme.density.sizeControlLg, QeetrixAccessibility.MinimumTouchTarget)),
            horizontalArrangement = Arrangement.spacedBy(QeetrixSpace.GapMd), // gap: space.gap.md
            verticalAlignment = Alignment.CenterVertically,
        ) {
            val titleStyle =
                // .qx-accordion-trigger: title.sm, color.text.default; [data-disabled] color.text.disabled
                QeetrixTheme.typography.titleSm
                    .copy(color = if (active) colors.textDefault else colors.textDisabled)
                    .labelDirection(alignToLayout = true)
            val chevron: @Composable (Modifier) -> Unit = { place ->
                QeetrixIcon(
                    QeetrixIconName.ChevronDown,
                    contentDescription = null,
                    modifier = place.graphicsLayer { rotationZ = turn },
                    size = QeetrixSize.IconSm,
                    shape = QeetrixIconShape.Sharp,
                    // .qx-accordion-icon: color.icon.subtle; [data-disabled] > .qx-accordion-icon: color.icon.disabled.
                    tint = if (active) colors.iconSubtle else colors.iconDisabled,
                )
            }
            if (description == null) {
                BasicText(title, Modifier.weight(1f), style = titleStyle)
                chevron(Modifier)
            } else {
                Column(Modifier.weight(1f).padding(vertical = QeetrixSpace.GapXs)) {
                    Row(horizontalArrangement = Arrangement.spacedBy(QeetrixSpace.GapMd)) {
                        BasicText(title, Modifier.weight(1f), style = titleStyle)
                        // On the title's first line, however many lines the title and summary take.
                        chevron(Modifier.padding(top = ((firstLineHeight(titleStyle) - QeetrixSize.IconSm) / 2).coerceAtLeast(0.dp)))
                    }
                    BasicText(
                        description,
                        Modifier.padding(end = QeetrixSize.IconSm + QeetrixSpace.GapMd),
                        style =
                            QeetrixTheme.typography.bodySm
                                .copy(color = if (active) colors.textSubtle else colors.textDisabled)
                                .labelDirection(alignToLayout = true),
                    )
                }
            }
        }
        FoldingPanel(open) {
            // .qx-accordion-content: body.md, color.text.default, padding-block-end space.inset.lg
            CompositionLocalProvider(LocalQeetrixContentColor provides colors.textDefault) {
                Box(Modifier.fillMaxWidth().padding(bottom = QeetrixSpace.InsetLg)) { content() }
            }
        }
    }
}

/** 0.1.0's signature without `description`, kept so apps compiled against it still link. */
@Deprecated("Kept for binary compatibility.", level = DeprecationLevel.HIDDEN)
@Composable
public fun <T> QeetrixAccordionItem(
    value: T,
    title: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    interactionSource: MutableInteractionSource? = null,
    content: @Composable () -> Unit,
): Unit = QeetrixAccordionItem(value, title, modifier, null, enabled, interactionSource, content)

/**
 * The trigger of a one-section fold — `@qeetrix/react`'s CollapsibleTrigger rendering a Button with a
 * `chevron-down` icon: a [com.qeetgroup.qeetrix.components.button.QeetrixButton] (ghost by default) whose trailing
 * chevron turns 180° as the panel opens, in `motion.duration.normal`. Pair it with a [QeetrixCollapsiblePanel] given
 * the same [open]: "Show more" under a list, or advanced options most people leave alone. A stack of folds is a
 * [QeetrixAccordion].
 *
 * Name what opens ("Advanced settings", "Retry policy"). Accessibility services read it as a button with
 * "Expanded" or "Collapsed" and offer expand and collapse actions.
 *
 * ```
 * var open by rememberSaveable { mutableStateOf(false) }
 * QeetrixCollapsibleTrigger("Advanced options", open = open, onOpenChange = { open = it })
 * QeetrixCollapsiblePanel(open) { WebhookSecret() }
 * ```
 */
@Composable
public fun QeetrixCollapsibleTrigger(
    text: String,
    open: Boolean,
    onOpenChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    variant: QeetrixButtonVariant = QeetrixButtonVariant.Ghost,
    tone: QeetrixButtonTone = QeetrixButtonTone.Brand,
    size: QeetrixButtonSize = QeetrixButtonSize.Md,
    enabled: Boolean = true,
    interactionSource: MutableInteractionSource? = null,
) {
    val metrics = buttonMetrics(size)
    val motion = qeetrixAnimationSpec<Float>(QeetrixMotion.EasingStandard, QeetrixMotion.DurationNormal)
    val turn by animateFloatAsState(if (open) 180f else 0f, motion, label = "collapsible-chevron") // [data-qx-fold][data-panel-open]
    val state = stringResource(if (open) R.string.qx_expanded else R.string.qx_collapsed)
    ButtonFrame(
        onClick = { onOpenChange(!open) },
        modifier = modifier,
        variant = variant,
        tone = tone,
        metrics = metrics,
        square = false,
        enabled = enabled,
        loading = false,
        interactionSource = interactionSource,
        horizontalArrangement = Arrangement.SpaceBetween,
        startPadding = metrics.labelPadding,
        semantics = {
            stateDescription = state
            if (enabled) {
                if (open) {
                    collapse {
                        onOpenChange(false)
                        true
                    }
                } else {
                    expand {
                        onOpenChange(true)
                        true
                    }
                }
            }
        },
    ) { color ->
        BasicText(
            text,
            Modifier.padding(end = metrics.gap),
            style = metrics.textStyle.copy(color = color).labelDirection(),
            maxLines = 1,
            softWrap = false,
            overflow = TextOverflow.Ellipsis,
        )
        // .qx-button-zone: a 1 dp rule as tall as the icon, in the label's colour at 55%, then the icon with equal space.
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.width(QeetrixBorder.WidthDefault).height(metrics.ruleHeight).background(color.atAlpha(ZONE_RULE_OPACITY)))
            Box(Modifier.padding(horizontal = metrics.zonePadding), contentAlignment = Alignment.Center) {
                QeetrixIcon(
                    QeetrixIconName.ChevronDown,
                    contentDescription = null,
                    modifier = Modifier.graphicsLayer { rotationZ = turn },
                    size = metrics.iconSize,
                    shape = QeetrixIconShape.Sharp,
                    tint = color,
                )
            }
        }
    }
}

/** `.qx-button-zone::before { opacity: .55 }`. */
private const val ZONE_RULE_OPACITY = 0.55f

/**
 * The content of a one-section fold — `@qeetrix/react`'s CollapsiblePanel: shown while [open], opening and closing
 * by height with the accordion's motion (`motion.duration.normal`, `motion.easing.standard`; at once under reduced
 * motion). No rules, no padding: the content's own. It leaves the composition when closed.
 */
@Composable
public fun QeetrixCollapsiblePanel(
    open: Boolean,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    FoldingPanel(open, modifier, content)
}

/** `.qx-accordion-panel, .qx-collapsible-panel`: block-size transitions over normal / standard, overflow hidden. */
@Composable
private fun FoldingPanel(
    open: Boolean,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    val spec = qeetrixAnimationSpec<IntSize>(QeetrixMotion.EasingStandard, QeetrixMotion.DurationNormal)
    AnimatedVisibility(
        visible = open,
        modifier = modifier,
        enter = expandVertically(spec, Alignment.Top),
        exit = shrinkVertically(spec, Alignment.Top),
    ) {
        content()
    }
}

/** What an accordion holds open, and how its items ask to change it. */
@Immutable
private class AccordionState(
    val value: List<Any?>,
    val onValueChange: (List<Any?>) -> Unit,
    val multiple: Boolean,
    val enabled: Boolean,
) {
    fun toggle(
        item: Any?,
        open: Boolean,
    ) {
        if (!enabled) return
        val next =
            when {
                !open -> value - item
                multiple -> if (item in value) value else value + item
                else -> listOf(item)
            }
        if (next != value) onValueChange(next)
    }
}

private val LocalAccordion = compositionLocalOf<AccordionState?> { null }
