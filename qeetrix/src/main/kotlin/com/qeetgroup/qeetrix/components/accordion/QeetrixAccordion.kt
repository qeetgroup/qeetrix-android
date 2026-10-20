package com.qeetgroup.qeetrix.components.accordion

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.collapse
import androidx.compose.ui.semantics.expand
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntSize
import com.qeetgroup.qeetrix.R
import com.qeetgroup.qeetrix.foundation.accessibility.QeetrixAccessibility
import com.qeetgroup.qeetrix.foundation.color.QeetrixOpacity
import com.qeetgroup.qeetrix.foundation.motion.QeetrixEasing
import com.qeetgroup.qeetrix.foundation.motion.qeetrixAnimationSpec
import com.qeetgroup.qeetrix.foundation.shape.QeetrixCorner
import com.qeetgroup.qeetrix.icons.QeetrixIcon
import com.qeetgroup.qeetrix.icons.QeetrixIconName
import com.qeetgroup.qeetrix.internal.FontWeightMedium
import com.qeetgroup.qeetrix.internal.QeetrixFont
import com.qeetgroup.qeetrix.internal.QeetrixWebScale
import com.qeetgroup.qeetrix.internal.RuledColumn
import com.qeetgroup.qeetrix.internal.firstLineHeight
import com.qeetgroup.qeetrix.internal.qeetrixFocusRing
import com.qeetgroup.qeetrix.internal.qeetrixTextStyle
import com.qeetgroup.qeetrix.theme.QeetrixTheme
import com.qeetgroup.qeetrix.tokens.generated.QeetrixAccordionComponentTokens

/**
 * Sections whose headers show and hide their content — the web's `Accordion`. Put [QeetrixAccordionItem]s
 * in it; it rules between them (`border-b … last:border-b-0`, in `component.accordion.divider`).
 *
 * ```
 * var open by rememberSaveable { mutableStateOf<Section?>(Section.Billing) }
 * QeetrixAccordion {
 *     for (section in Section.entries) {
 *         QeetrixAccordionItem(
 *             title = section.title,
 *             expanded = open == section,
 *             onExpandedChange = { open = if (it) section else null },
 *         ) { SectionContent(section) }
 *     }
 * }
 * ```
 *
 * - **The app owns expansion.** Each item is open when its `expanded` is. One optional value, as above,
 *   opens one at a time; a set of keys (`rememberSaveable` of your items' ids) lets several open at once
 *   and keeps each item's state through insertions, removals and reordering.
 * - Use it in a scrolling column, a card or a sheet. In a lazy list, make each item a
 *   [QeetrixAccordionItem] with [com.qeetgroup.qeetrix.components.separator.QeetrixSeparator]s between them.
 */
@Composable
public fun QeetrixAccordion(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    RuledColumn(QeetrixAccordionComponentTokens.Divider, modifier.fillMaxWidth(), content = content) // w-full
}

/**
 * One section of an accordion: a header with a [title], an optional [description] and [icon], and the
 * [content] it shows and hides — the web's `AccordionItem` with its `AccordionTrigger` and
 * `AccordionContent`.
 *
 * - **The header** is one button, at least 48 dp tall, with a chevron at its end that turns as the content
 *   opens. Accessibility services read it as a heading with its title, description and "Expanded" or
 *   "Collapsed" (`qx_expanded`, `qx_collapsed`), and offer expand and collapse actions. Keyboard focus draws
 *   the focus ring; Enter and Space toggle it.
 * - **The content** opens and closes over the accordion's 200 ms (`component.accordion.duration`), at once
 *   with animations off, and leaves the composition when closed — keep state that must survive a close in
 *   your own state holder. Content is 14 sp primary text on the web; give text in it `QeetrixText` with the
 *   `Small` variant.
 * - [enabled] `false` fades the header and it cannot be toggled.
 */
@Composable
public fun QeetrixAccordionItem(
    title: String,
    expanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    description: String? = null,
    icon: QeetrixIconName? = null,
    enabled: Boolean = true,
    interactionSource: MutableInteractionSource? = null,
    content: @Composable () -> Unit,
) {
    val source = interactionSource ?: remember { MutableInteractionSource() }
    val hovered by source.collectIsHoveredAsState()
    val pressed by source.collectIsPressedAsState()
    val duration = QeetrixAccordionComponentTokens.Duration // --qx-component-accordion-duration
    val colors = QeetrixTheme.colors
    // text-muted-foreground; group-hover:text-foreground, group-data-panel-open:text-foreground rotate-180
    val chevronColor by animateColorAsState(
        if (expanded || (enabled && (hovered || pressed))) colors.textPrimary else colors.textTertiary,
        qeetrixAnimationSpec(QeetrixEasing.Standard, duration),
        label = "accordion-chevron-color",
    )
    val chevronTurn by animateFloatAsState(
        if (expanded) 180f else 0f,
        qeetrixAnimationSpec(QeetrixEasing.Standard, duration),
        label = "accordion-chevron",
    )
    val state = stringResource(if (expanded) R.string.qx_expanded else R.string.qx_collapsed)
    val line = firstLineHeight(TriggerStyle)

    Column(modifier.fillMaxWidth()) {
        Row(
            Modifier
                .fillMaxWidth()
                .clickable(source, indication = null, enabled = enabled, role = Role.Button) { onExpandedChange(!expanded) }
                .semantics {
                    heading()
                    stateDescription = state
                    if (enabled) {
                        if (expanded) {
                            collapse {
                                onExpandedChange(false)
                                true
                            }
                        } else {
                            expand {
                                onExpandedChange(true)
                                true
                            }
                        }
                    }
                }.qeetrixFocusRing(source, RoundedCornerShape(QeetrixCorner.Sm)) // rounded-sm focus-visible:focus-ring
                .then(if (enabled) Modifier else Modifier.alpha(QeetrixOpacity.Disabled))
                .heightIn(min = QeetrixAccessibility.MinimumTouchTarget)
                .padding(vertical = QeetrixAccordionComponentTokens.TriggerPaddingBlock),
            // items-start gap-4: a wrapping title keeps the chevron on its first line.
            horizontalArrangement = Arrangement.spacedBy(QeetrixWebScale.spacing(4f)),
            verticalAlignment = Alignment.Top,
        ) {
            Row(Modifier.weight(1f), horizontalArrangement = Arrangement.spacedBy(QeetrixWebScale.spacing(2f))) {
                if (icon != null) {
                    Box(Modifier.height(line), contentAlignment = Alignment.Center) {
                        QeetrixIcon(icon, contentDescription = null, size = QeetrixWebScale.spacing(4f), tint = colors.textTertiary)
                    }
                }
                Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(QeetrixWebScale.spacing(0.5f))) {
                    BasicText(title, Modifier.fillMaxWidth(), style = TriggerStyle.copy(color = colors.textPrimary))
                    if (description != null) {
                        BasicText(description, Modifier.fillMaxWidth(), style = DescriptionStyle.copy(color = colors.textTertiary))
                    }
                }
            }
            // size-4 mt-0.5: centred on the title's first line.
            Box(Modifier.height(line), contentAlignment = Alignment.Center) {
                QeetrixIcon(
                    QeetrixIconName.ChevronDown,
                    contentDescription = null,
                    modifier = Modifier.graphicsLayer { rotationZ = chevronTurn },
                    size = QeetrixWebScale.spacing(4f),
                    tint = chevronColor,
                )
            }
        }
        AnimatedVisibility(
            visible = expanded,
            modifier = Modifier.clipBelow(QeetrixAccordionComponentTokens.FocusBleed),
            enter = expandVertically(qeetrixAnimationSpec<IntSize>(QeetrixEasing.Standard, duration), Alignment.Top, clip = false),
            exit = shrinkVertically(qeetrixAnimationSpec<IntSize>(QeetrixEasing.Standard, duration), Alignment.Top, clip = false),
        ) {
            Box(Modifier.fillMaxWidth().padding(bottom = QeetrixWebScale.spacing(4f))) { content() } // pt-0 pb-4
        }
    }
}

/**
 * Clips what is drawn below the bottom edge — so the opening content is revealed by the height animation —
 * but lets [bleed] past the other edges, so a focus ring on content flush with the panel's sides is not cut
 * off: the web panel's `clip-path: inset(-bleed -bleed 0)`.
 */
private fun Modifier.clipBelow(bleed: Dp): Modifier =
    drawWithContent {
        val outside = bleed.toPx()
        clipRect(-outside, -outside, size.width + outside, size.height) { this@drawWithContent.drawContent() }
    }

// text-sm font-medium text-foreground
private val TriggerStyle = qeetrixTextStyle(QeetrixFont.Ui, QeetrixWebScale.TextSm, FontWeightMedium)

// A header's description: text-sm text-muted-foreground.
private val DescriptionStyle = qeetrixTextStyle(QeetrixFont.Sans, QeetrixWebScale.TextSm)
