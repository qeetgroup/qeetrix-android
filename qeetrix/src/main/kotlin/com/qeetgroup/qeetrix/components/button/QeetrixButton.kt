package com.qeetgroup.qeetrix.components.button

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import com.qeetgroup.qeetrix.icons.QeetrixIcon
import com.qeetgroup.qeetrix.icons.QeetrixIconName
import com.qeetgroup.qeetrix.internal.FontWeightMedium
import com.qeetgroup.qeetrix.internal.QeetrixBusyIndicator
import com.qeetgroup.qeetrix.internal.QeetrixFont
import com.qeetgroup.qeetrix.internal.QeetrixWebScale
import com.qeetgroup.qeetrix.internal.qeetrixTextStyle
import com.qeetgroup.qeetrix.theme.QeetrixTheme
import com.qeetgroup.qeetrix.tokens.generated.QeetrixButtonComponentTokens

/**
 * The styles of the Button family (`value` is the web name). One primary action per screen.
 */
public enum class QeetrixButtonVariant(
    public val value: String,
) {
    /** The primary action, in Qeet orange. */
    Default("default"),

    /** A bordered secondary action. */
    Outline("outline"),

    /** A filled, quiet secondary action. */
    Secondary("secondary"),

    /** No surface until pressed: toolbars and low-emphasis actions. */
    Ghost("ghost"),

    /** An action that destroys data: a quiet red tint that turns solid on press. */
    Destructive("destructive"),

    /** Text styled as a link. */
    Link("link"),
}

/** The text-button sizes (`value` is the web name). Icon-only sizes are [QeetrixIconButtonSize]. */
public enum class QeetrixButtonSize(
    public val value: String,
) {
    /** The control height of the current density ([com.qeetgroup.qeetrix.theme.LocalQeetrixDensity]), 32 dp by default. */
    Default("default"),

    /** 24 dp, 12 sp text. */
    Xs("xs"),

    /** 28 dp, 12.8 sp text. */
    Sm("sm"),

    /** 36 dp. */
    Lg("lg"),
}

/**
 * A button with a text label and optional icons.
 *
 * - [enabled] `false` fades the button (the primary turns graphite) and it cannot be activated.
 * - [loading] keeps the label and the colour, shows a spinner in the leading slot (replacing
 *   [leadingIcon]), ignores activation so an action is never sent twice, keeps focus, and reports
 *   "Loading" to accessibility services. With a [loadingLabel] ("Saving…"), the label is replaced and,
 *   while loading, the button is as wide as the wider of its resting content and "spinner + loading
 *   label" — so a loading label no longer than the label keeps the width exactly.
 *
 * The drawn button keeps the web's size; a 48 × 48 dp touch area is reserved around it. Text follows the
 * font scale and the button grows with it rather than clipping.
 *
 * ```
 * QeetrixButton("Save changes", onClick = ::save, loading = saving, loadingLabel = "Saving…")
 * QeetrixButton("Delete", onClick = ::delete, variant = QeetrixButtonVariant.Destructive, leadingIcon = QeetrixIconName.X)
 * ```
 */
@Composable
public fun QeetrixButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    variant: QeetrixButtonVariant = QeetrixButtonVariant.Default,
    size: QeetrixButtonSize = QeetrixButtonSize.Default,
    enabled: Boolean = true,
    loading: Boolean = false,
    loadingLabel: String? = null,
    leadingIcon: QeetrixIconName? = null,
    trailingIcon: QeetrixIconName? = null,
    interactionSource: MutableInteractionSource? = null,
) {
    val metrics = buttonMetrics(size)
    // has-data-[icon=inline-start]:ps-*, has-data-[icon=inline-end]:pe-*
    val start = if (leadingIcon != null || (loading && loadingLabel == null)) metrics.iconSidePadding else metrics.padding
    val end = if (trailingIcon != null) metrics.iconSidePadding else metrics.padding
    ButtonFrame(
        onClick = onClick,
        modifier = modifier,
        variant = variant,
        metrics = metrics,
        enabled = enabled,
        loading = loading,
        interactionSource = interactionSource,
        startPadding = start,
        endPadding = end,
        semantics = { if (loading && loadingLabel != null) contentDescription = loadingLabel },
    ) { color, underline ->
        val style = metrics.textStyle.copy(color = color, textDecoration = if (underline) TextDecoration.Underline else null)
        if (loading && loadingLabel != null) {
            // The wider of the resting content and "spinner + loading label", so the width holds.
            Box(contentAlignment = Alignment.Center) {
                Row(
                    Modifier.alpha(0f).clearAndSetSemantics {},
                    horizontalArrangement = Arrangement.spacedBy(metrics.gap),
                    verticalAlignment = Alignment.CenterVertically,
                ) { RestingContent(text, leadingIcon, trailingIcon, metrics, style, color) }
                Row(horizontalArrangement = Arrangement.spacedBy(metrics.gap), verticalAlignment = Alignment.CenterVertically) {
                    QeetrixBusyIndicator(metrics.iconSize, color)
                    Label(loadingLabel, style)
                }
            }
        } else if (loading) {
            QeetrixBusyIndicator(metrics.iconSize, color) // replaces the leading icon
            Label(text, style)
            if (trailingIcon != null) QeetrixIcon(trailingIcon, contentDescription = null, size = metrics.iconSize, tint = color)
        } else {
            RestingContent(text, leadingIcon, trailingIcon, metrics, style, color)
        }
    }
}

/**
 * A button with any content — a label with a badge, a custom arrangement. The content is laid out in a
 * row with the size's gap, drawn in the variant's content colour ([com.qeetgroup.qeetrix.theme.LocalQeetrixContentColor]),
 * and its text should be a [com.qeetgroup.qeetrix.components.typography.QeetrixText] or use that colour.
 * [loading] adds the spinner in front of the content.
 */
@Composable
public fun QeetrixButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    variant: QeetrixButtonVariant = QeetrixButtonVariant.Default,
    size: QeetrixButtonSize = QeetrixButtonSize.Default,
    enabled: Boolean = true,
    loading: Boolean = false,
    interactionSource: MutableInteractionSource? = null,
    content: @Composable RowScope.() -> Unit,
) {
    val metrics = buttonMetrics(size)
    ButtonFrame(
        onClick = onClick,
        modifier = modifier,
        variant = variant,
        metrics = metrics,
        enabled = enabled,
        loading = loading,
        interactionSource = interactionSource,
        startPadding = if (loading) metrics.iconSidePadding else metrics.padding,
        endPadding = metrics.padding,
        semantics = {},
    ) { color, _ ->
        if (loading) QeetrixBusyIndicator(metrics.iconSize, color)
        content()
    }
}

@Composable
private fun RowScope.RestingContent(
    text: String,
    leadingIcon: QeetrixIconName?,
    trailingIcon: QeetrixIconName?,
    metrics: ButtonMetrics,
    style: TextStyle,
    color: Color,
) {
    if (leadingIcon != null) QeetrixIcon(leadingIcon, contentDescription = null, size = metrics.iconSize, tint = color)
    Label(text, style)
    if (trailingIcon != null) QeetrixIcon(trailingIcon, contentDescription = null, size = metrics.iconSize, tint = color)
}

@Composable
private fun Label(
    text: String,
    style: TextStyle,
) {
    BasicText(text, style = style, maxLines = 1, softWrap = false, overflow = TextOverflow.Ellipsis) // whitespace-nowrap
}

/** The web size classes, resolved for the current density. */
@Composable
internal fun buttonMetrics(size: QeetrixButtonSize): ButtonMetrics =
    when (size) {
        // h-(button-height) gap-1.5 px-2.5, ps-2/pe-2 beside an icon, svg size-4, text-sm
        QeetrixButtonSize.Default -> {
            ButtonMetrics(
                height = QeetrixButtonComponentTokens.height(QeetrixTheme.density),
                square = false,
                corner = QeetrixButtonComponentTokens.Corner,
                padding = QeetrixWebScale.spacing(2.5f),
                iconSidePadding = QeetrixWebScale.spacing(2f),
                gap = QeetrixWebScale.spacing(1.5f),
                iconSize = QeetrixWebScale.spacing(4f),
                textStyle = TextSm,
            )
        }

        // h-6 gap-1 rounded-(corner-xs) px-2 text-xs, ps/pe-1.5 beside an icon, svg size-3
        QeetrixButtonSize.Xs -> {
            ButtonMetrics(
                height = QeetrixWebScale.spacing(6f),
                square = false,
                corner = QeetrixButtonComponentTokens.CornerXs,
                padding = QeetrixWebScale.spacing(2f),
                iconSidePadding = QeetrixWebScale.spacing(1.5f),
                gap = QeetrixWebScale.spacing(1f),
                iconSize = QeetrixWebScale.spacing(3f),
                textStyle = TextXs,
            )
        }

        // h-7 gap-1 rounded-(corner-sm) px-2.5 text-[font-size-sm], ps/pe-1.5 beside an icon, svg size-3.5
        QeetrixButtonSize.Sm -> {
            ButtonMetrics(
                height = QeetrixWebScale.spacing(7f),
                square = false,
                corner = QeetrixButtonComponentTokens.CornerSm,
                padding = QeetrixWebScale.spacing(2.5f),
                iconSidePadding = QeetrixWebScale.spacing(1.5f),
                gap = QeetrixWebScale.spacing(1f),
                iconSize = QeetrixWebScale.spacing(3.5f),
                textStyle = TextSmall,
            )
        }

        // h-9 gap-1.5 px-3, ps/pe-2.5 beside an icon
        QeetrixButtonSize.Lg -> {
            ButtonMetrics(
                height = QeetrixWebScale.spacing(9f),
                square = false,
                corner = QeetrixButtonComponentTokens.Corner,
                padding = QeetrixWebScale.spacing(3f),
                iconSidePadding = QeetrixWebScale.spacing(2.5f),
                gap = QeetrixWebScale.spacing(1.5f),
                iconSize = QeetrixWebScale.spacing(4f),
                textStyle = TextSm,
            )
        }
    }

// font-ui text-sm font-medium; xs: text-xs; sm: text-[length:font-size-sm] keeps text-sm's unitless line height,
// which multiplies the element's own size.
private val TextSm = qeetrixTextStyle(QeetrixFont.Ui, QeetrixWebScale.TextSm, FontWeightMedium)
private val TextXs = qeetrixTextStyle(QeetrixFont.Ui, QeetrixWebScale.TextXs, FontWeightMedium)
private val TextSmall =
    qeetrixTextStyle(QeetrixFont.Ui, QeetrixButtonComponentTokens.FontSizeSm.value, QeetrixWebScale.TextSm.lineHeight, FontWeightMedium)
