package com.qeetgroup.qeetrix.components.card

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import com.qeetgroup.qeetrix.foundation.color.QeetrixOpacity
import com.qeetgroup.qeetrix.foundation.elevation.QeetrixElevation
import com.qeetgroup.qeetrix.foundation.elevation.qeetrixElevation
import com.qeetgroup.qeetrix.foundation.sizing.QeetrixStroke
import com.qeetgroup.qeetrix.internal.FontWeightMedium
import com.qeetgroup.qeetrix.internal.QeetrixFont
import com.qeetgroup.qeetrix.internal.QeetrixWebScale
import com.qeetgroup.qeetrix.internal.drawOutlineRing
import com.qeetgroup.qeetrix.internal.qeetrixFocusRing
import com.qeetgroup.qeetrix.internal.qeetrixTextStyle
import com.qeetgroup.qeetrix.theme.LocalQeetrixContentColor
import com.qeetgroup.qeetrix.theme.QeetrixTheme
import com.qeetgroup.qeetrix.tokens.generated.QeetrixCardComponentTokens

/** The surface treatments of [QeetrixCard] (`value` is the web name). */
public enum class QeetrixCardVariant(
    public val value: String,
) {
    /** The resting card: a boundary plus the faintest shadow. */
    Default("default"),

    /** No shadow, for dense layouts and repeated tiles. */
    Outline("outline"),

    /** One step forward: for the one surface on a screen that should stand out, not as a default. */
    Elevated("elevated"),
}

/** The padding and gap scale of [QeetrixCard] — not its width (`value` is the web name). */
public enum class QeetrixCardSize(
    public val value: String,
) {
    /** 16 dp padding and gaps. */
    Default("default"),

    /** 12 dp padding and gaps, and a 14 sp title. */
    Sm("sm"),
}

/**
 * A surface that groups related content: an optional header ([title], [description] and an [action]),
 * the [content], and an optional [footer] band.
 *
 * A card is static: accessibility services read its parts in order. The [title] is announced as a
 * heading. [selected] paints the selected boundary (2 dp, brand) and is visual only: the state itself
 * belongs to the control that owns it, such as a radio button inside the card. A card inside a card
 * keeps its boundary and drops its shadow, so nesting never stacks elevation.
 *
 * ```
 * QeetrixCard(title = "Workspace", description = "3 members", footer = { QeetrixButton("Manage", onClick = {}) }) {
 *     QeetrixText("Created 12 March")
 * }
 * ```
 */
@Composable
public fun QeetrixCard(
    modifier: Modifier = Modifier,
    variant: QeetrixCardVariant = QeetrixCardVariant.Default,
    size: QeetrixCardSize = QeetrixCardSize.Default,
    selected: Boolean = false,
    title: String? = null,
    description: String? = null,
    action: (@Composable () -> Unit)? = null,
    footer: (@Composable RowScope.() -> Unit)? = null,
    content: (@Composable ColumnScope.() -> Unit)? = null,
) {
    CardSurface(
        modifier = modifier,
        variant = variant,
        size = size,
        selected = selected,
        interaction = null,
        title = title,
        description = description,
        action = action,
        footer = footer,
        content = content,
    )
}

/**
 * A [QeetrixCard] that is itself the control: the whole card is one button. It lifts on hover, settles
 * on press and shows its hover boundary while pressed (touch has no hover), draws the Qeet focus ring
 * for keyboard focus, and accessibility services read its content as the button's label.
 *
 * Use it when the card does one thing (open a record, choose a plan). A card with several actions is a
 * static card with buttons inside.
 */
@Composable
public fun QeetrixCard(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    variant: QeetrixCardVariant = QeetrixCardVariant.Default,
    size: QeetrixCardSize = QeetrixCardSize.Default,
    selected: Boolean = false,
    title: String? = null,
    description: String? = null,
    footer: (@Composable RowScope.() -> Unit)? = null,
    interactionSource: MutableInteractionSource? = null,
    content: (@Composable ColumnScope.() -> Unit)? = null,
) {
    CardSurface(
        modifier = modifier,
        variant = variant,
        size = size,
        selected = selected,
        interaction = CardInteraction(onClick, enabled, interactionSource),
        title = title,
        description = description,
        action = null,
        footer = footer,
        content = content,
    )
}

private class CardInteraction(
    val onClick: () -> Unit,
    val enabled: Boolean,
    val source: MutableInteractionSource?,
)

/** Inside a card, a nested card draws no shadow (`in-data-[slot=card]:shadow-none`). */
private val LocalInsideQeetrixCard = staticCompositionLocalOf { false }

@Composable
private fun CardSurface(
    modifier: Modifier,
    variant: QeetrixCardVariant,
    size: QeetrixCardSize,
    selected: Boolean,
    interaction: CardInteraction?,
    title: String?,
    description: String?,
    action: (@Composable () -> Unit)?,
    footer: (@Composable RowScope.() -> Unit)?,
    content: (@Composable ColumnScope.() -> Unit)?,
) {
    val shape = RoundedCornerShape(QeetrixCardComponentTokens.Corner)
    val source = interaction?.let { it.source ?: remember { MutableInteractionSource() } }
    val hovered = source?.collectIsHoveredAsState()?.value ?: false
    val pressed = source?.collectIsPressedAsState()?.value ?: false
    val nested = LocalInsideQeetrixCard.current
    val elevation =
        when {
            nested -> QeetrixElevation.Flat

            interaction != null && interaction.enabled && pressed -> QeetrixCardComponentTokens.Elevation

            // active:shadow-(card-elevation)
            interaction != null && interaction.enabled && hovered -> QeetrixCardComponentTokens.ElevationHover

            // hover:shadow-(…-hover)
            variant == QeetrixCardVariant.Default -> QeetrixCardComponentTokens.Elevation

            variant == QeetrixCardVariant.Elevated -> QeetrixCardComponentTokens.ElevationElevated

            else -> QeetrixElevation.Flat
        }
    val background =
        if (variant == QeetrixCardVariant.Elevated) QeetrixCardComponentTokens.BackgroundElevated else QeetrixCardComponentTokens.Background
    val boundary =
        when {
            selected -> QeetrixCardComponentTokens.BorderSelected

            // hover:border-(…-hover). Touch has no hover, so a press shows the same boundary as feedback.
            interaction != null && interaction.enabled && (hovered || pressed) -> QeetrixCardComponentTokens.BorderHover

            else -> QeetrixCardComponentTokens.Ring
        }
    val metrics = if (size == QeetrixCardSize.Sm) SmMetrics else DefaultMetrics

    var surface =
        modifier
            .qeetrixElevation(elevation, shape, background)
            // data-selected:ring-1 — the boundary doubled to 2 dp outside the box, so nothing shifts.
            .drawOutlineRing(selected, shape, QeetrixCardComponentTokens.BorderSelected, QeetrixStroke.Hairline, Dp(0f))
    if (interaction != null && source != null) {
        surface =
            surface
                .qeetrixFocusRing(source, shape)
                .clip(shape)
                .clickable(
                    interactionSource = source,
                    indication = null,
                    enabled = interaction.enabled,
                    role = Role.Button,
                    onClick = interaction.onClick,
                ).then(if (interaction.enabled) Modifier else Modifier.alpha(QeetrixOpacity.Disabled))
    } else {
        surface = surface.clip(shape)
    }
    surface = surface.border(QeetrixStroke.Hairline, boundary, shape)

    CompositionLocalProvider(
        LocalQeetrixContentColor provides QeetrixCardComponentTokens.Foreground,
        LocalInsideQeetrixCard provides true,
    ) {
        Column(
            modifier =
                surface.padding(
                    top = metrics.padding + QeetrixStroke.Hairline,
                    bottom = if (footer != null) QeetrixStroke.Hairline else metrics.padding + QeetrixStroke.Hairline, // has-footer:pb-0
                ),
            verticalArrangement = Arrangement.spacedBy(metrics.gap), // gap-4 / data-[size=sm]:gap-3
        ) {
            if (title != null || description != null || action != null) {
                CardHeader(title, description, action, metrics, size)
            }
            if (content != null) {
                // CardContent: px-4 / px-3. Absent, it takes no slot, so no gap is added.
                Column(Modifier.fillMaxWidth().padding(horizontal = metrics.padding + QeetrixStroke.Hairline), content = content)
            }
            if (footer != null) {
                CardFooter(metrics, footer)
            }
        }
    }
}

@Composable
private fun CardHeader(
    title: String?,
    description: String?,
    action: (@Composable () -> Unit)?,
    metrics: CardMetrics,
    size: QeetrixCardSize,
) {
    // grid: title and description in the first column, the action in the second, spanning both rows.
    Row(
        Modifier.fillMaxWidth().padding(horizontal = metrics.padding + QeetrixStroke.Hairline),
        horizontalArrangement = Arrangement.spacedBy(QeetrixWebScale.spacing(2f)), // gap-x-2
    ) {
        // Title and description are block paragraphs, each as wide as the column (it also keeps a right-to-left
        // paragraph from wrapping early at its own intrinsic width). gap-1
        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(QeetrixWebScale.spacing(1f))) {
            if (title != null) {
                BasicText(
                    title,
                    Modifier.fillMaxWidth().semantics { heading() },
                    style = (if (size == QeetrixCardSize.Sm) TitleStyleSm else TitleStyle).copy(color = LocalQeetrixContentColor.current),
                )
            }
            if (description != null) {
                BasicText(
                    description,
                    Modifier.fillMaxWidth(),
                    style = DescriptionStyle.copy(color = QeetrixTheme.colors.textTertiary), // text-muted-foreground
                )
            }
        }
        if (action != null) {
            Row(verticalAlignment = Alignment.Top) { action() } // self-start justify-self-end
        }
    }
}

@Composable
private fun CardFooter(
    metrics: CardMetrics,
    footer: @Composable RowScope.() -> Unit,
) {
    val rule = QeetrixCardComponentTokens.Ring
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .background(QeetrixCardComponentTokens.FooterBackground) // bg-(--qx-component-card-footer-background)
                .drawBehind {
                    // border-t border-(--qx-component-card-ring)
                    val width = QeetrixStroke.Hairline.toPx()
                    drawLine(rule, Offset(0f, width / 2), Offset(size.width, width / 2), strokeWidth = width)
                }.padding(
                    start = metrics.padding + QeetrixStroke.Hairline,
                    end = metrics.padding + QeetrixStroke.Hairline,
                    top = metrics.padding + QeetrixStroke.Hairline,
                    bottom = metrics.padding + QeetrixStroke.Hairline,
                ), // p-4 / p-3
        horizontalArrangement = Arrangement.spacedBy(QeetrixWebScale.spacing(2f)), // gap-2
        verticalAlignment = Alignment.CenterVertically, // items-center
        content = footer,
    )
}

private class CardMetrics(
    val padding: Dp,
    val gap: Dp,
)

private val DefaultMetrics = CardMetrics(padding = QeetrixWebScale.spacing(4f), gap = QeetrixWebScale.spacing(4f))
private val SmMetrics = CardMetrics(padding = QeetrixWebScale.spacing(3f), gap = QeetrixWebScale.spacing(3f))

// CardTitle: font-heading text-base leading-snug font-medium; data-[size=sm]: text-sm.
private val TitleStyle =
    qeetrixTextStyle(QeetrixFont.Heading, QeetrixWebScale.TextBase.size, QeetrixWebScale.LEADING_SNUG, FontWeightMedium)
private val TitleStyleSm =
    qeetrixTextStyle(QeetrixFont.Heading, QeetrixWebScale.TextSm.size, QeetrixWebScale.LEADING_SNUG, FontWeightMedium)

// CardDescription: text-sm text-muted-foreground.
private val DescriptionStyle = qeetrixTextStyle(QeetrixFont.Sans, QeetrixWebScale.TextSm)
