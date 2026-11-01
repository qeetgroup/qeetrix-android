package com.qeetgroup.qeetrix.components.checklist

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.qeetgroup.qeetrix.R
import com.qeetgroup.qeetrix.components.spinner.QeetrixSpinner
import com.qeetgroup.qeetrix.components.spinner.QeetrixSpinnerSize
import com.qeetgroup.qeetrix.foundation.shape.QeetrixShapes
import com.qeetgroup.qeetrix.foundation.sizing.QeetrixBorder
import com.qeetgroup.qeetrix.foundation.sizing.QeetrixSize
import com.qeetgroup.qeetrix.foundation.spacing.QeetrixSpace
import com.qeetgroup.qeetrix.icons.QeetrixIcon
import com.qeetgroup.qeetrix.icons.QeetrixIconName
import com.qeetgroup.qeetrix.icons.QeetrixIconShape
import com.qeetgroup.qeetrix.internal.labelDirection
import com.qeetgroup.qeetrix.theme.QeetrixTheme

/** A check's result (`value` is `@qeetrix/react`'s CheckStatus). */
public enum class QeetrixCheckStatus(
    public val value: String,
) {
    /** Met: `circle-check` in `color.icon.success`, "Passed". */
    Passed("passed"),

    /** Not met: `circle-x` in `color.icon.danger`, "Failed". */
    Failed("failed"),

    /** Not checked yet: `circle-dashed` in `color.icon.subtle`, "Not run". */
    Pending("pending"),

    /** Being checked: a spinner, "Running". */
    Running("running"),
}

/** The checks of a [QeetrixCheckList]. */
public interface QeetrixCheckListScope

private object CheckListScopeInstance : QeetrixCheckListScope

/**
 * Requirements and their results — `@qeetrix/react`'s CheckList: password rules that tick as they're met, setup steps, a
 * connection's checks. A ruled sheet (`color.surface.raised`, a 1 dp `color.border.default` edge, `radius.surface`) of
 * [QeetrixCheckItem]s 1 dp `color.border.subtle` apart, with an optional [footer] under them (when they ran, Run again).
 * Results that change are announced politely.
 *
 * ```
 * QeetrixCheckList {
 *     QeetrixCheckItem("At least 12 characters", if (password.length >= 12) QeetrixCheckStatus.Passed else QeetrixCheckStatus.Pending)
 * }
 * ```
 */
@Composable
public fun QeetrixCheckList(
    modifier: Modifier = Modifier,
    footer: (@Composable () -> Unit)? = null,
    content: @Composable QeetrixCheckListScope.() -> Unit,
) {
    val colors = QeetrixTheme.colors
    val shape = QeetrixShapes.Surface
    Column(
        modifier
            .fillMaxWidth()
            .clip(shape)
            .background(colors.surfaceRaised)
            .border(QeetrixBorder.WidthDefault, colors.borderDefault, shape),
    ) {
        com.qeetgroup.qeetrix.internal
            .RuledColumn(colors.borderSubtle) { CheckListScopeInstance.content() }
        if (footer != null) {
            val rule = colors.borderSubtle
            Row(
                Modifier
                    .fillMaxWidth()
                    .drawBehind { drawRect(rule, Offset.Zero, Size(size.width, QeetrixBorder.WidthDefault.toPx())) }
                    .padding(horizontal = QeetrixSpace.InsetMd, vertical = QeetrixSpace.InsetSm),
                verticalAlignment = Alignment.CenterVertically,
            ) { footer() }
        }
    }
}

/**
 * One check — `@qeetrix/react`'s CheckItem: its status icon, [title] (`label.md`) and [detail] (`body.sm`,
 * `color.text.subtle`, wrapping anywhere), and its status in words at the end (`label.sm`, the tone's text colour), so
 * the result never rests on the icon's colour. At least 48 dp tall, `space.inset.sm` × `space.inset.md` padding.
 * Read as one element: "At least 12 characters, Passed".
 */
@Composable
public fun QeetrixCheckListScope.QeetrixCheckItem(
    title: String,
    status: QeetrixCheckStatus,
    modifier: Modifier = Modifier,
    detail: String? = null,
) {
    val colors = QeetrixTheme.colors
    val typography = QeetrixTheme.typography
    val words =
        stringResource(
            when (status) {
                QeetrixCheckStatus.Passed -> R.string.qx_check_passed
                QeetrixCheckStatus.Failed -> R.string.qx_check_failed
                QeetrixCheckStatus.Pending -> R.string.qx_check_pending
                QeetrixCheckStatus.Running -> R.string.qx_check_running
            },
        )
    Row(
        modifier
            .fillMaxWidth()
            .heightIn(min = ROW)
            .semantics(mergeDescendants = true) { liveRegion = LiveRegionMode.Polite }
            .padding(horizontal = QeetrixSpace.InsetMd, vertical = QeetrixSpace.InsetSm),
        horizontalArrangement = Arrangement.spacedBy(QeetrixSpace.GapMd),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        when (status) {
            QeetrixCheckStatus.Running -> {
                QeetrixSpinner(size = QeetrixSpinnerSize.Md, decorative = true)
            }

            else -> {
                val (icon, tint) =
                    when (status) {
                        QeetrixCheckStatus.Passed -> QeetrixIconName.CircleCheck to colors.iconSuccess
                        QeetrixCheckStatus.Failed -> QeetrixIconName.CircleX to colors.iconDanger
                        else -> QeetrixIconName.CircleDashed to colors.iconSubtle
                    }
                QeetrixIcon(icon, contentDescription = null, size = QeetrixSize.IconSm, shape = QeetrixIconShape.Sharp, tint = tint)
            }
        }
        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(QeetrixSpace.GapXs)) {
            BasicText(title, style = typography.labelMd.copy(color = colors.textDefault).labelDirection(alignToLayout = true))
            if (detail !=
                null
            ) {
                BasicText(detail, style = typography.bodySm.copy(color = colors.textSubtle).labelDirection(alignToLayout = true))
            }
        }
        val wordColor =
            when (status) {
                QeetrixCheckStatus.Passed -> colors.textSuccess
                QeetrixCheckStatus.Failed -> colors.textDanger
                else -> colors.textSubtle
            }
        BasicText(words, style = typography.labelSm.copy(color = wordColor).labelDirection(alignToLayout = true))
    }
}

/** `.qx-check-item { min-block-size: 3rem }`. */
private val ROW = 48.dp
