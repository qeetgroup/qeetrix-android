package com.qeetgroup.qeetrix.components.badge

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.qeetgroup.qeetrix.foundation.shape.QeetrixShapes
import com.qeetgroup.qeetrix.foundation.sizing.QeetrixSize
import com.qeetgroup.qeetrix.foundation.spacing.QeetrixSpace
import com.qeetgroup.qeetrix.icons.QeetrixIcon
import com.qeetgroup.qeetrix.icons.QeetrixIconName
import com.qeetgroup.qeetrix.icons.QeetrixIconShape
import com.qeetgroup.qeetrix.internal.labelDirection
import com.qeetgroup.qeetrix.theme.QeetrixTheme

/** What a badge means (`value` is `@qeetrix/react`'s `tone`). The word carries the meaning; the tone reinforces it. */
public enum class QeetrixBadgeTone(
    public val value: String,
) {
    /** The default. */
    Neutral("neutral"),
    Brand("brand"),
    Info("info"),
    Success("success"),
    Warning("warning"),
    Danger("danger"),
}

/** Emphasis (`value` is `@qeetrix/react`'s `variant`). */
public enum class QeetrixBadgeVariant(
    public val value: String,
) {
    /** The tinted pill, for most statuses. The default. */
    Subtle("subtle"),

    /** The bold fill, for the one status in a view that must stand out ("Overdue"). */
    Solid("solid"),
}

/** Heights (`value` is `@qeetrix/react`'s `size`). */
public enum class QeetrixBadgeSize(
    public val value: String,
) {
    /** 16 dp, for tight rows and beside small text. */
    Sm("sm"),

    /** 20 dp. The default. */
    Md("md"),
}

/**
 * A short, static status or count — `@qeetrix/react`'s Badge, a status tag: a pill (`radius.chip`) in
 * `label.sm`, tinted ([QeetrixBadgeVariant.Subtle]) or solid, with an optional 12 dp leading [icon] in
 * the sharp shape. One or two words ("Paid", "Overdue"), never colour alone; it ends in an ellipsis when
 * space runs out.
 *
 * Not interactive: a selectable or removable tag is [QeetrixChip]. A count needs context for TalkBack —
 * give the row around it a `contentDescription` such as "3 pending".
 */
@Composable
public fun QeetrixBadge(
    text: String,
    modifier: Modifier = Modifier,
    tone: QeetrixBadgeTone = QeetrixBadgeTone.Neutral,
    variant: QeetrixBadgeVariant = QeetrixBadgeVariant.Subtle,
    size: QeetrixBadgeSize = QeetrixBadgeSize.Md,
    icon: QeetrixIconName? = null,
) {
    val (background, content) = badgeColors(tone, variant)
    val small = size == QeetrixBadgeSize.Sm
    Row(
        modifier =
            modifier
                .background(background, QeetrixShapes.Chip)
                // md: padding 2px 8px; sm: padding 0 6px
                .padding(horizontal = if (small) 6.dp else 8.dp, vertical = if (small) 0.dp else 2.dp),
        horizontalArrangement = Arrangement.spacedBy(if (small) 2.dp else QeetrixSpace.GapXs), // gap: space.gap.xs; sm 2px
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (icon != null) {
            QeetrixIcon(icon, contentDescription = null, size = QeetrixSize.Icon2xs, shape = QeetrixIconShape.Sharp, tint = content)
        }
        BasicText(
            text = text,
            style =
                QeetrixTheme.typography.labelSm
                    .copy(color = content)
                    .labelDirection(),
            maxLines = 1, // white-space: nowrap, text-overflow: ellipsis
            softWrap = false,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

/** badge.css: fill and text per tone and variant. */
@Composable
internal fun badgeColors(
    tone: QeetrixBadgeTone,
    variant: QeetrixBadgeVariant,
): Pair<Color, Color> {
    val c = QeetrixTheme.colors
    return when (variant) {
        QeetrixBadgeVariant.Subtle -> {
            when (tone) {
                QeetrixBadgeTone.Neutral -> c.bgNeutralSubtlePressed to c.textDefault
                QeetrixBadgeTone.Brand -> c.bgBrandSubtle to c.textBrand
                QeetrixBadgeTone.Info -> c.bgInfoSubtle to c.textInfo
                QeetrixBadgeTone.Success -> c.bgSuccessSubtle to c.textSuccess
                QeetrixBadgeTone.Warning -> c.bgWarningSubtle to c.textWarning
                QeetrixBadgeTone.Danger -> c.bgDangerSubtle to c.textDanger
            }
        }

        QeetrixBadgeVariant.Solid -> {
            when (tone) {
                QeetrixBadgeTone.Neutral -> c.bgNeutralBold to c.textInverse
                QeetrixBadgeTone.Brand -> c.bgBrandBold to c.textOnBrand
                QeetrixBadgeTone.Info -> c.bgInfoBold to c.textOnBold
                QeetrixBadgeTone.Success -> c.bgSuccessBold to c.textOnBold
                QeetrixBadgeTone.Warning -> c.bgWarningBold to c.textOnWarning
                QeetrixBadgeTone.Danger -> c.bgDangerBold to c.textOnBold
            }
        }
    }
}
