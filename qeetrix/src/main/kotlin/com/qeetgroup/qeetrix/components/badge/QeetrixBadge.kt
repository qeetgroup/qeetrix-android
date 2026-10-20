package com.qeetgroup.qeetrix.components.badge

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import com.qeetgroup.qeetrix.foundation.sizing.QeetrixStroke
import com.qeetgroup.qeetrix.icons.QeetrixIcon
import com.qeetgroup.qeetrix.icons.QeetrixIconName
import com.qeetgroup.qeetrix.internal.FontWeightMedium
import com.qeetgroup.qeetrix.internal.QeetrixFont
import com.qeetgroup.qeetrix.internal.QeetrixWebScale
import com.qeetgroup.qeetrix.internal.labelDirection
import com.qeetgroup.qeetrix.internal.qeetrixTextStyle
import com.qeetgroup.qeetrix.theme.QeetrixTheme
import com.qeetgroup.qeetrix.tokens.generated.QeetrixBadgeComponentTokens

/**
 * The styles of [QeetrixBadge] (`value` is the web name): one solid Qeet fill, quiet tints for the brand
 * and status tones, and neutral styles for metadata.
 */
public enum class QeetrixBadgeVariant(
    public val value: String,
) {
    /** The one solid Qeet fill, for counts and "New". The loudest a badge can be: use it sparingly. */
    Default("default"),

    /** Graphite, for metadata that should not compete. */
    Secondary("secondary"),

    /** An outline only. */
    Outline("outline"),

    /** A quiet brand tint. */
    Brand("brand"),

    /** A quiet information tint. */
    Info("info"),

    /** A quiet success tint. */
    Success("success"),

    /** A quiet warning tint. */
    Warning("warning"),

    /** A quiet danger tint. */
    Destructive("destructive"),

    /** The quietest neutral. */
    Muted("muted"),
}

/**
 * A small label for a count, a status or a category.
 *
 * The [text] carries the meaning, so a status never rests on colour alone: write "Failed", not a red
 * dot. The [icon], if any, is decorative. Badges are not interactive. Text is in sp and follows the
 * font scale; the badge grows with it and stays on one line.
 *
 * ```
 * QeetrixBadge("Active", variant = QeetrixBadgeVariant.Success, icon = QeetrixIconName.CircleCheck)
 * ```
 */
@Composable
public fun QeetrixBadge(
    text: String,
    modifier: Modifier = Modifier,
    variant: QeetrixBadgeVariant = QeetrixBadgeVariant.Default,
    icon: QeetrixIconName? = null,
) {
    val colors = badgeColors(variant)
    val shape = RoundedCornerShape(QeetrixBadgeComponentTokens.Corner)
    Row(
        modifier =
            modifier
                .background(colors.background, shape)
                .border(QeetrixStroke.Hairline, colors.border, shape) // border (transparent where unwanted)
                // px-2 py-0.5, inside the 1 dp border (CSS sizes are border-box)
                .padding(
                    horizontal = QeetrixWebScale.spacing(2f) + QeetrixStroke.Hairline,
                    vertical = QeetrixWebScale.spacing(0.5f) + QeetrixStroke.Hairline,
                ),
        horizontalArrangement = Arrangement.spacedBy(QeetrixWebScale.spacing(1f)), // gap-1
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (icon != null) {
            QeetrixIcon(icon, contentDescription = null, size = QeetrixWebScale.spacing(3f), tint = colors.content) // [&>svg]:size-3
        }
        BasicText(
            text = text,
            style = BadgeTextStyle.copy(color = colors.content).labelDirection(),
            maxLines = 1, // whitespace-nowrap
            softWrap = false,
            overflow = TextOverflow.Clip,
        )
    }
}

// font-ui text-xs font-medium
private val BadgeTextStyle = qeetrixTextStyle(QeetrixFont.Ui, QeetrixWebScale.TextXs, FontWeightMedium)

private class BadgeColors(
    val background: Color,
    val content: Color,
    val border: Color,
)

@Composable
private fun badgeColors(variant: QeetrixBadgeVariant): BadgeColors {
    val colors = QeetrixTheme.colors
    return when (variant) {
        // border-transparent bg-(--qx-component-badge-default-background) text-(--qx-component-badge-default-foreground)
        QeetrixBadgeVariant.Default -> {
            BadgeColors(QeetrixBadgeComponentTokens.DefaultBackground, QeetrixBadgeComponentTokens.DefaultForeground, Color.Transparent)
        }

        // border-transparent bg-secondary text-secondary-foreground
        QeetrixBadgeVariant.Secondary -> {
            BadgeColors(colors.surfaceInteractive, colors.textOnSubtle, Color.Transparent)
        }

        // border-border text-foreground dark:border-border-strong
        QeetrixBadgeVariant.Outline -> {
            BadgeColors(Color.Transparent, colors.textPrimary, if (QeetrixTheme.isDark) colors.borderStrong else colors.borderDefault)
        }

        // border-(--qx-component-badge-border-brand) bg-brand-subtle text-brand
        QeetrixBadgeVariant.Brand -> {
            BadgeColors(colors.surfaceBrandSubtle, colors.textBrand, QeetrixBadgeComponentTokens.BorderBrand)
        }

        // border-(--qx-component-badge-border-info) bg-info-subtle text-info-text
        QeetrixBadgeVariant.Info -> {
            BadgeColors(colors.feedbackInfoSubtle, colors.textInfo, QeetrixBadgeComponentTokens.BorderInfo)
        }

        // border-(--qx-component-badge-border-success) bg-success-subtle text-success-text
        QeetrixBadgeVariant.Success -> {
            BadgeColors(colors.feedbackSuccessSubtle, colors.textSuccess, QeetrixBadgeComponentTokens.BorderSuccess)
        }

        // border-(--qx-component-badge-border-warning) bg-warning-subtle text-warning-text
        QeetrixBadgeVariant.Warning -> {
            BadgeColors(colors.feedbackWarningSubtle, colors.textWarning, QeetrixBadgeComponentTokens.BorderWarning)
        }

        // border-(--qx-component-badge-border-danger) bg-destructive-subtle text-destructive-text
        QeetrixBadgeVariant.Destructive -> {
            BadgeColors(colors.feedbackErrorSubtle, colors.textDanger, QeetrixBadgeComponentTokens.BorderDanger)
        }

        // border-transparent bg-muted text-muted-foreground
        QeetrixBadgeVariant.Muted -> {
            BadgeColors(colors.surfaceSubtle, colors.textTertiary, Color.Transparent)
        }
    }
}
