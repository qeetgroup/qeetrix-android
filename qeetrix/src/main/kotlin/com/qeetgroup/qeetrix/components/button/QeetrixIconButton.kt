package com.qeetgroup.qeetrix.components.button

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import com.qeetgroup.qeetrix.icons.QeetrixIcon
import com.qeetgroup.qeetrix.icons.QeetrixIconName
import com.qeetgroup.qeetrix.internal.QeetrixBusyIndicator
import com.qeetgroup.qeetrix.internal.QeetrixWebScale
import com.qeetgroup.qeetrix.theme.QeetrixTheme
import com.qeetgroup.qeetrix.tokens.generated.QeetrixButtonComponentTokens

/** The icon-only button sizes (`value` is the web name): squares. */
public enum class QeetrixIconButtonSize(
    public val value: String,
) {
    /** The control height of the current density, 32 dp by default, with a 16 dp icon. */
    Default("icon"),

    /** 24 dp with a 12 dp icon. */
    Xs("icon-xs"),

    /** 28 dp with a 16 dp icon. */
    Sm("icon-sm"),

    /** 36 dp with a 16 dp icon. */
    Lg("icon-lg"),
}

/**
 * An icon-only button. [contentDescription] is required and is what accessibility services read: name
 * the action ("Close dialog"), not the icon ("X"). [loading] swaps the icon for the spinner in the same
 * square, so a toolbar never reflows while one of its actions is busy.
 *
 * Ghost by default, as on the web. The square keeps the web's size; a 48 × 48 dp touch area is reserved
 * around it.
 *
 * ```
 * QeetrixIconButton(QeetrixIconName.X, contentDescription = "Close", onClick = ::dismiss)
 * ```
 */
@Composable
public fun QeetrixIconButton(
    icon: QeetrixIconName,
    contentDescription: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    variant: QeetrixButtonVariant = QeetrixButtonVariant.Ghost,
    size: QeetrixIconButtonSize = QeetrixIconButtonSize.Default,
    enabled: Boolean = true,
    loading: Boolean = false,
    interactionSource: MutableInteractionSource? = null,
) {
    val metrics = iconButtonMetrics(size)
    ButtonFrame(
        onClick = onClick,
        modifier = modifier,
        variant = variant,
        metrics = metrics,
        enabled = enabled,
        loading = loading,
        interactionSource = interactionSource,
        startPadding = metrics.padding,
        endPadding = metrics.padding,
        semantics = { this.contentDescription = contentDescription },
    ) { color, _ ->
        if (loading) {
            QeetrixBusyIndicator(metrics.iconSize, color)
        } else {
            QeetrixIcon(icon, contentDescription = null, size = metrics.iconSize, tint = color)
        }
    }
}

@Composable
private fun iconButtonMetrics(size: QeetrixIconButtonSize): ButtonMetrics {
    val text = buttonMetrics(QeetrixButtonSize.Default).textStyle

    fun square(
        side: androidx.compose.ui.unit.Dp,
        corner: androidx.compose.ui.unit.Dp,
        icon: androidx.compose.ui.unit.Dp,
    ) = ButtonMetrics(
        height = side,
        square = true,
        corner = corner,
        padding = QeetrixWebScale.spacing(0f),
        iconSidePadding = QeetrixWebScale.spacing(0f),
        gap = QeetrixWebScale.spacing(0f),
        iconSize = icon,
        textStyle = text,
    )
    return when (size) {
        // size-(button-height), svg size-4
        QeetrixIconButtonSize.Default -> {
            square(
                QeetrixButtonComponentTokens.height(QeetrixTheme.density),
                QeetrixButtonComponentTokens.Corner,
                QeetrixWebScale.spacing(4f),
            )
        }

        // size-6 rounded-(corner-xs), svg size-3
        QeetrixIconButtonSize.Xs -> {
            square(QeetrixWebScale.spacing(6f), QeetrixButtonComponentTokens.CornerXs, QeetrixWebScale.spacing(3f))
        }

        // size-7 rounded-(corner-sm), svg size-4
        QeetrixIconButtonSize.Sm -> {
            square(QeetrixWebScale.spacing(7f), QeetrixButtonComponentTokens.CornerSm, QeetrixWebScale.spacing(4f))
        }

        // size-9, svg size-4
        QeetrixIconButtonSize.Lg -> {
            square(QeetrixWebScale.spacing(9f), QeetrixButtonComponentTokens.Corner, QeetrixWebScale.spacing(4f))
        }
    }
}
