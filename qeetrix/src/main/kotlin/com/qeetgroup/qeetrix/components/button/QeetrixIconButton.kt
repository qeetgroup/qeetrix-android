package com.qeetgroup.qeetrix.components.button

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.unit.dp
import com.qeetgroup.qeetrix.components.tooltip.TooltipAnchor
import com.qeetgroup.qeetrix.components.tooltip.rememberTooltipState
import com.qeetgroup.qeetrix.icons.QeetrixIconName

/**
 * A square, icon-only button — `@qeetrix/react`'s IconButton. [contentDescription] is required and is
 * what accessibility services read: name the action ("Close dialog"), not the icon ("X"). [loading]
 * swaps the icon for the spinner in the same square, so a toolbar never reflows while one of its actions
 * is busy.
 *
 * With [tooltip] (the default, as the web's IconButton), the name also shows in a tooltip — after a 500 ms hover,
 * while it has keyboard focus, and for 1.5 s after a long press — so sighted people can learn what an icon does.
 *
 * [QeetrixButtonVariant.Ghost] by default, as on the web; utility actions in toolbars and headers take
 * [QeetrixButtonTone.Neutral], so their icons stay neutral. The square is the control height of [size]; a
 * 48 × 48 dp touch area is reserved around it.
 *
 * ```
 * QeetrixIconButton(QeetrixIconName.X, contentDescription = "Close", onClick = ::dismiss, tone = QeetrixButtonTone.Neutral)
 * ```
 */
@Composable
public fun QeetrixIconButton(
    icon: QeetrixIconName,
    contentDescription: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    variant: QeetrixButtonVariant = QeetrixButtonVariant.Ghost,
    tone: QeetrixButtonTone = QeetrixButtonTone.Brand,
    size: QeetrixButtonSize = QeetrixButtonSize.Md,
    enabled: Boolean = true,
    loading: Boolean = false,
    interactionSource: MutableInteractionSource? = null,
    iconMirrorsInRightToLeft: Boolean = icon.mirrorsInRightToLeft,
    tooltip: Boolean = true,
) {
    val metrics = buttonMetrics(size)
    val source = interactionSource ?: remember { MutableInteractionSource() }
    val tooltipState = rememberTooltipState()
    ButtonFrame(
        onClick = onClick,
        onLongClick = if (tooltip) ({ tooltipState.pressed = true }) else null,
        modifier = modifier,
        variant = variant,
        tone = tone,
        metrics = metrics,
        square = true, // .qx-icon-button: inline-size size.control.*, padding-inline 0, centred
        enabled = enabled,
        loading = loading,
        interactionSource = source,
        horizontalArrangement = Arrangement.Center,
        startPadding = 0.dp,
        semantics = { this.contentDescription = contentDescription },
    ) { color ->
        ZoneContent(icon, loading, metrics, color, iconMirrorsInRightToLeft)
        // IconButton's tooltip (QTRX-156): its name on long press, hover or keyboard focus, anchored to this button.
        if (tooltip) TooltipAnchor(contentDescription, tooltipState, source)
    }
}
