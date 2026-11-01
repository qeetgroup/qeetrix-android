package com.qeetgroup.qeetrix.components.alert

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.qeetgroup.qeetrix.R
import com.qeetgroup.qeetrix.components.button.QeetrixButtonSize
import com.qeetgroup.qeetrix.components.button.QeetrixButtonTone
import com.qeetgroup.qeetrix.components.button.QeetrixIconButton
import com.qeetgroup.qeetrix.foundation.color.QeetrixColors
import com.qeetgroup.qeetrix.foundation.sizing.QeetrixBorder
import com.qeetgroup.qeetrix.foundation.sizing.QeetrixSize
import com.qeetgroup.qeetrix.foundation.spacing.QeetrixSpace
import com.qeetgroup.qeetrix.foundation.typography.QeetrixTextRole
import com.qeetgroup.qeetrix.icons.QeetrixIcon
import com.qeetgroup.qeetrix.icons.QeetrixIconName
import com.qeetgroup.qeetrix.icons.QeetrixIconShape
import com.qeetgroup.qeetrix.internal.firstLineHeight
import com.qeetgroup.qeetrix.internal.labelDirection
import com.qeetgroup.qeetrix.theme.QeetrixTheme

/** An alert's status (`value` is `@qeetrix/react`'s AlertTone). */
public enum class QeetrixAlertTone(
    public val value: String,
    internal val icon: QeetrixIconName,
) {
    /** `color.bg.info.subtle` with the `info` icon: something to know. The default. */
    Info("info", QeetrixIconName.Info),

    /** `color.bg.success.subtle` with `circle-check`: it worked. */
    Success("success", QeetrixIconName.CircleCheck),

    /** `color.bg.warning.subtle` with `triangle-alert`: it needs attention soon. */
    Warning("warning", QeetrixIconName.TriangleAlert),

    /** `color.bg.danger.subtle` with `circle-alert`: it failed, or blocks the task. */
    Danger("danger", QeetrixIconName.CircleAlert),
}

/** Where an alert sits (`value` is `@qeetrix/react`'s AlertVariant). */
public enum class QeetrixAlertVariant(
    public val value: String,
) {
    /** In the page — above a form, inside a card — with the 3 dp status edge on its start side. The default. */
    Default("default"),

    /** Across the top of a screen, under the top app bar: the tint edge to edge and a 1 dp rule in the tone under it. */
    Banner("banner"),
}

/** How an alert that appears after something happens is announced (`value` is `@qeetrix/react`'s `announce`). */
public enum class QeetrixAlertAnnounce(
    public val value: String,
) {
    /** Waits its turn: "Saved". */
    Polite("polite"),

    /** Interrupts, for an error that blocks the task. */
    Urgent("urgent"),
}

/**
 * A message in the page — `@qeetrix/react`'s Alert: push notifications not set up, offline, a phone not linked. A tinted
 * surface with the tone's 3 dp edge on its start side (alert.css). It never takes focus and never disappears by itself.
 *
 * - **Look**: `color.bg.<tone>.subtle`, a `border.width.accent` start edge in `color.icon.<tone>`, `space.inset.md`
 *   padding (`space.inset.sm` at the end); the tone's 16 dp sharp icon on the title's line, `space.gap.md` before the
 *   text; the [title] in `label.md` at `title.sm`'s weight, the [description] in `body.md` `color.text.subtle`, 2 dp
 *   apart; the [actions] under the text, `space.gap.sm` apart (one primary at most). [onDismiss] adds a Dismiss icon
 *   button at the end. [QeetrixAlertVariant.Banner] spans the screen with a rule in the tone under it instead of the edge.
 * - **Never colour alone**: the icon and a word read before the title ("Warning: Push isn't set up") carry the tone.
 * - **[announce]**: only for an alert that appears after something happens — polite, or urgent for a blocking error.
 *
 * ```
 * QeetrixAlert(
 *     "Push isn't set up",
 *     tone = QeetrixAlertTone.Warning,
 *     description = "Sign-in requests won't reach this phone until you allow notifications.",
 *     actions = { QeetrixButton("Allow notifications", onClick = ::allow, size = QeetrixButtonSize.Sm) },
 * )
 * ```
 */
@Composable
public fun QeetrixAlert(
    title: String,
    modifier: Modifier = Modifier,
    tone: QeetrixAlertTone = QeetrixAlertTone.Info,
    variant: QeetrixAlertVariant = QeetrixAlertVariant.Default,
    icon: QeetrixIconName? = null,
    description: String? = null,
    announce: QeetrixAlertAnnounce? = null,
    onDismiss: (() -> Unit)? = null,
    actions: (@Composable () -> Unit)? = null,
) {
    val colors = QeetrixTheme.colors
    val typography = QeetrixTheme.typography
    val edge = tone.edge(colors)
    val rule = tone.rule(colors)
    val banner = variant == QeetrixAlertVariant.Banner
    val word = stringResource(tone.word())
    val line = firstLineHeight(typography.labelMd)
    Row(
        modifier
            .fillMaxWidth()
            .semantics(mergeDescendants = false) {
                if (announce !=
                    null
                ) {
                    liveRegion = if (announce == QeetrixAlertAnnounce.Urgent) LiveRegionMode.Assertive else LiveRegionMode.Polite
                }
            }.drawBehind {
                drawRect(tone.fill(colors))
                if (banner) {
                    // [data-variant="banner"]: border-block-end border.width.default in border.<tone>
                    val width = QeetrixBorder.WidthDefault.toPx()
                    drawRect(rule, Offset(0f, size.height - width), Size(size.width, width))
                } else {
                    // border-inline-start: border.width.accent solid icon.<tone>
                    val width = QeetrixBorder.WidthAccent.toPx()
                    val x = if (layoutDirection == LayoutDirection.Rtl) size.width - width else 0f
                    drawRect(edge, Offset(x, 0f), Size(width, size.height))
                }
            }.padding(
                start = if (banner) QeetrixSpace.LayoutMd else QeetrixSpace.InsetMd + QeetrixBorder.WidthAccent,
                end = if (banner) QeetrixSpace.LayoutMd else QeetrixSpace.InsetSm,
                top = if (banner) QeetrixSpace.InsetSm else QeetrixSpace.InsetMd,
                bottom = (if (banner) QeetrixSpace.InsetSm else QeetrixSpace.InsetMd) + if (banner) QeetrixBorder.WidthDefault else 0.dp,
            ),
        horizontalArrangement = Arrangement.spacedBy(QeetrixSpace.GapMd),
        verticalAlignment = Alignment.Top,
    ) {
        // .qx-alert-icon: icon.<tone>, margin-block-start 2px — on the title's line.
        Box(Modifier.padding(top = ((line - QeetrixSize.IconSm) / 2).coerceAtLeast(0.dp))) {
            QeetrixIcon(
                icon ?: tone.icon,
                contentDescription = null,
                size = QeetrixSize.IconSm,
                shape = QeetrixIconShape.Sharp,
                tint = edge,
            )
        }
        Column(Modifier.weight(1f).padding(end = QeetrixSpace.GapXs), verticalArrangement = Arrangement.spacedBy(2.dp)) {
            // .qx-alert-title: label.md at title.sm's weight, after the hidden status word.
            BasicText(
                title,
                Modifier.semantics { contentDescription = "$word: $title" },
                style =
                    typography.labelMd
                        .copy(
                            color = colors.textDefault,
                            fontWeight = FontWeight(QeetrixTextRole.TitleSm.spec.weight),
                        ).labelDirection(alignToLayout = true),
            )
            if (description != null) {
                BasicText(description, style = typography.bodyMd.copy(color = colors.textSubtle).labelDirection(alignToLayout = true))
            }
            if (actions != null) {
                // .qx-alert-actions: gap space.gap.sm, wrapping, margin-block-start space.gap.sm
                androidx.compose.foundation.layout.FlowRow(
                    Modifier.padding(top = QeetrixSpace.GapSm),
                    horizontalArrangement = Arrangement.spacedBy(QeetrixSpace.GapSm),
                    verticalArrangement = Arrangement.spacedBy(QeetrixSpace.GapSm),
                ) { actions() }
            }
        }
        if (onDismiss != null) {
            QeetrixIconButton(
                QeetrixIconName.X,
                stringResource(R.string.qx_dismiss),
                onClick = onDismiss,
                tone = QeetrixButtonTone.Neutral,
                size = QeetrixButtonSize.Sm,
            )
        }
    }
}

private fun QeetrixAlertTone.fill(colors: QeetrixColors): Color =
    when (this) {
        QeetrixAlertTone.Info -> colors.bgInfoSubtle
        QeetrixAlertTone.Success -> colors.bgSuccessSubtle
        QeetrixAlertTone.Warning -> colors.bgWarningSubtle
        QeetrixAlertTone.Danger -> colors.bgDangerSubtle
    }

private fun QeetrixAlertTone.edge(colors: QeetrixColors): Color =
    when (this) {
        QeetrixAlertTone.Info -> colors.iconInfo
        QeetrixAlertTone.Success -> colors.iconSuccess
        QeetrixAlertTone.Warning -> colors.iconWarning
        QeetrixAlertTone.Danger -> colors.iconDanger
    }

private fun QeetrixAlertTone.rule(colors: QeetrixColors): Color =
    when (this) {
        QeetrixAlertTone.Info -> colors.borderInfo
        QeetrixAlertTone.Success -> colors.borderSuccess
        QeetrixAlertTone.Warning -> colors.borderWarning
        QeetrixAlertTone.Danger -> colors.borderDanger
    }

/** The word read before the title (alert.info … alert.danger). */
private fun QeetrixAlertTone.word(): Int =
    when (this) {
        QeetrixAlertTone.Info -> R.string.qx_alert_info
        QeetrixAlertTone.Success -> R.string.qx_alert_success
        QeetrixAlertTone.Warning -> R.string.qx_alert_warning
        QeetrixAlertTone.Danger -> R.string.qx_alert_danger
    }
