package com.qeetgroup.qeetrix.components.button

import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.layout
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.qeetgroup.qeetrix.foundation.sizing.QeetrixBorder
import com.qeetgroup.qeetrix.icons.QeetrixIcon
import com.qeetgroup.qeetrix.icons.QeetrixIconName
import com.qeetgroup.qeetrix.icons.QeetrixIconShape
import com.qeetgroup.qeetrix.internal.QeetrixSpinnerRing
import com.qeetgroup.qeetrix.internal.atAlpha
import com.qeetgroup.qeetrix.internal.labelDirection

/**
 * Emphasis (`value` is `@qeetrix/react`'s `variant`). A region has at most one [Solid] and one [Soft]
 * button; every other action is [Ghost] (design rules §7).
 */
public enum class QeetrixButtonVariant(
    public val value: String,
) {
    /** Primary: Ember fill (`color.bg.brand.bold`). The one main action in a region. The default. */
    Solid("solid"),

    /** Secondary: warm graphite fill (`color.bg.neutral.bold`). The main alternative: Export, Save draft. */
    Soft("soft"),

    /** Tertiary: no fill, Ember text, an Ember tint under a press. Cancel, Back, Learn more. */
    Ghost("ghost"),
}

/** Meaning (`value` is `@qeetrix/react`'s `tone`). */
public enum class QeetrixButtonTone(
    public val value: String,
) {
    /** The default. */
    Brand("brand"),

    /**
     * The confirming action of a destructive flow, which replaces the primary — never beside it. Solid
     * and soft are a red fill; ghost is red text.
     */
    Danger("danger"),

    /** Utility actions in toolbars and headers, whose icons stay neutral. A solid or soft neutral button looks like [QeetrixButtonVariant.Soft]. */
    Neutral("neutral"),
}

/**
 * Heights from the density (`value` is `@qeetrix/react`'s `size`): `size.control.sm` / `md` / `lg`. A
 * button that submits a form or sits in a row with fields is [Lg], so it lines up with them.
 */
public enum class QeetrixButtonSize(
    public val value: String,
) {
    /** `size.control.sm` (24 / 28 / 32 dp), `label.sm`. */
    Sm("sm"),

    /** `size.control.md` (28 / 32 / 36 dp), `label.md`. The default. */
    Md("md"),

    /** `size.control.lg` (32 / 40 / 44 dp), the field height, `label.md`. */
    Lg("lg"),
}

/**
 * An action — `@qeetrix/react`'s Button. **Label first, icon last, divided:** the label starts the
 * button and an optional [icon] sits in its own zone at the far end, behind a 1 dp rule as tall as the
 * icon in the label's colour at 55% (design rules §7). Write the label as a verb ("Create invoice").
 *
 * - [enabled] `false` takes the disabled colours; the button stays focusable so the reason it is
 *   disabled can be read. Prefer explaining over disabling.
 * - [loading] puts a spinner in the icon's place — a button without an icon gains the zone — keeps the
 *   label, ignores activation so an action is never sent twice, keeps focus and reports "Loading".
 * - [fullWidth] fills the container (forms, phones) and centres the label on the whole button; an icon
 *   keeps its zone at the end, and the start keeps as much room as the zone takes, so the label is centred
 *   with or without one.
 *
 * The drawn button keeps the web's size; a 48 × 48 dp touch area is reserved around it. Text follows the
 * font scale and the button grows with it rather than clipping.
 *
 * ```
 * QeetrixButton("Create invoice", onClick = ::create, icon = QeetrixIconName.Plus)
 * QeetrixButton("Cancel", onClick = ::cancel, variant = QeetrixButtonVariant.Ghost)
 * QeetrixButton("Revoke key", onClick = ::revoke, tone = QeetrixButtonTone.Danger, loading = revoking)
 * ```
 *
 * @param icon A trailing icon, drawn in the sharp shape.
 */
@Composable
public fun QeetrixButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    variant: QeetrixButtonVariant = QeetrixButtonVariant.Solid,
    tone: QeetrixButtonTone = QeetrixButtonTone.Brand,
    size: QeetrixButtonSize = QeetrixButtonSize.Md,
    icon: QeetrixIconName? = null,
    loading: Boolean = false,
    fullWidth: Boolean = false,
    enabled: Boolean = true,
    interactionSource: MutableInteractionSource? = null,
    iconMirrorsInRightToLeft: Boolean = icon?.mirrorsInRightToLeft ?: false,
) {
    val metrics = buttonMetrics(size)
    val zone = icon != null || loading
    // A full-width label is centred on the whole button: half of what the end's span (the gap, the rule, the zone's
    // padding on both sides and the icon) takes beyond the start's padding, towards the end of the label's box.
    val shift =
        if (zone) {
            (metrics.gap + QeetrixBorder.WidthDefault + metrics.zonePadding * 2 + metrics.iconSize - metrics.labelPadding) / 2
        } else {
            0
                .dp
        }
    ButtonFrame(
        onClick = onClick,
        modifier = if (fullWidth) modifier.fillMaxWidth() else modifier,
        variant = variant,
        tone = tone,
        metrics = metrics,
        square = false,
        enabled = enabled,
        loading = loading,
        interactionSource = interactionSource,
        // justify-content: space-between: the icon at the end; a full-width label takes the free width, centred.
        horizontalArrangement = Arrangement.SpaceBetween,
        startPadding = metrics.labelPadding,
        semantics = {},
    ) { color ->
        BasicText(
            text,
            // Without a zone the label pads both sides (`:not([data-icon]):not([data-loading])`).
            modifier =
                Modifier
                    .then(if (fullWidth) Modifier.weight(1f) else Modifier)
                    .padding(end = if (zone) metrics.gap else metrics.labelPadding)
                    .then(if (fullWidth) Modifier.centredOnButton(shift) else Modifier),
            style = metrics.textStyle.copy(color = color).labelDirection(),
            maxLines = 1,
            softWrap = false,
            overflow = TextOverflow.Ellipsis,
        )
        if (zone) IconZone(metrics, color) { ZoneContent(icon, loading, metrics, color, iconMirrorsInRightToLeft) }
    }
}

/**
 * A full-width button's label (button.css `[data-full-width]`): centred on the whole button, [shift] towards the end of
 * its own box, as far as the box allows. Where it's tight the start gives way first, and the label's natural width is
 * the text's, so the button needs no more room than before. Placed relative to the layout direction, so right to left
 * mirrors it.
 */
private fun Modifier.centredOnButton(shift: Dp): Modifier =
    layout { measurable, constraints ->
        val text = measurable.measure(constraints.copy(minWidth = 0))
        val width = if (constraints.hasBoundedWidth) constraints.maxWidth else text.width
        val free = (width - text.width).coerceAtLeast(0)
        val x = (free / 2 + shift.roundToPx()).coerceIn(0, free)
        layout(width, text.height) { text.placeRelative(x, 0) }
    }

/** The icon or, while loading, the spinner — the zone's content (button.tsx `content`). */
@Composable
internal fun ZoneContent(
    icon: QeetrixIconName?,
    loading: Boolean,
    metrics: ButtonMetrics,
    color: Color,
    mirror: Boolean = icon?.mirrorsInRightToLeft ?: false,
) {
    if (loading) {
        // <Spinner decorative size={size === "lg" ? "lg" : "md"} />: 20 or 16 dp, the icon's size.
        QeetrixSpinnerRing(metrics.iconSize, color)
    } else if (icon != null) {
        QeetrixIcon(
            icon,
            contentDescription = null,
            size = metrics.iconSize,
            shape = QeetrixIconShape.Sharp,
            tint = color,
            mirrorInRightToLeft = mirror,
        )
    }
}

/**
 * `.qx-button-zone`: the icon with equal space either side, behind a 1 dp rule exactly as tall as the
 * icon size, centred on the button's height, in the label's colour at 55%. A short seam, not a full-height
 * split (design rules §7).
 */
@Composable
internal fun IconZone(
    metrics: ButtonMetrics,
    color: Color,
    content: @Composable () -> Unit,
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(Modifier.width(QeetrixBorder.WidthDefault).height(metrics.ruleHeight).background(color.atAlpha(RULE_OPACITY)))
        Box(Modifier.padding(horizontal = metrics.zonePadding), contentAlignment = Alignment.Center) { content() }
    }
}

/** `.qx-button-zone::before { opacity: .55 }`. */
private const val RULE_OPACITY = 0.55f
