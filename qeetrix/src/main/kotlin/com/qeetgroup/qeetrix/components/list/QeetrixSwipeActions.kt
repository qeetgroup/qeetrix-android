package com.qeetgroup.qeetrix.components.list

import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.absoluteOffset
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.semantics.CustomAccessibilityAction
import androidx.compose.ui.semantics.customActions
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.qeetgroup.qeetrix.components.dialog.QeetrixConfirmationDialog
import com.qeetgroup.qeetrix.components.dialog.QeetrixDialogIntent
import com.qeetgroup.qeetrix.foundation.color.QeetrixColors
import com.qeetgroup.qeetrix.foundation.haptics.QeetrixHapticRole
import com.qeetgroup.qeetrix.foundation.haptics.rememberQeetrixHaptics
import com.qeetgroup.qeetrix.foundation.motion.QeetrixMotion
import com.qeetgroup.qeetrix.foundation.motion.qeetrixAnimationSpec
import com.qeetgroup.qeetrix.foundation.sizing.QeetrixSize
import com.qeetgroup.qeetrix.foundation.spacing.QeetrixSpace
import com.qeetgroup.qeetrix.icons.QeetrixIcon
import com.qeetgroup.qeetrix.icons.QeetrixIconName
import com.qeetgroup.qeetrix.icons.QeetrixIconShape
import com.qeetgroup.qeetrix.internal.labelDirection
import com.qeetgroup.qeetrix.theme.QeetrixTheme
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.roundToInt

/** A swipe action's colour (`value` is the name shared with qeetrix-ios). */
public enum class QeetrixSwipeActionTone(
    public val value: String,
) {
    /** `color.bg.neutral.bold`: archive, mark as read. The default. */
    Neutral("neutral"),

    /** `color.bg.brand.bold`: the one constructive action. */
    Brand("brand"),

    /** `color.bg.danger.bold`: remove, delete — confirmed before it runs. */
    Danger("danger"),
}

/**
 * One action behind a list row: its [label] and [icon], its [tone], and what it does. A danger action asks first,
 * in a [QeetrixConfirmationDialog] titled [confirmTitle] with [confirmDescription].
 */
public class QeetrixSwipeAction(
    public val label: String,
    public val icon: QeetrixIconName,
    public val onAction: () -> Unit,
    public val tone: QeetrixSwipeActionTone = QeetrixSwipeActionTone.Neutral,
    public val confirmTitle: String? = null,
    public val confirmDescription: String? = null,
)

/**
 * Actions on a list row, revealed by swiping it — Qeet Auth's Remove on an account's row. `@qeetrix/react` has no
 * counterpart (touch only). Swiping toward the start reveals [endAction] at the row's end; toward the end, [startAction]
 * at its start (mirrored right to left). The action fills the space it uncovers in its tone with its icon and label;
 * tap it, or swipe all the way (past 60 % of the row) to run it. A danger action is confirmed in a dialog first.
 *
 * - **Never only by swiping**: every action is also an accessibility action of the row ("Remove") and should be in the
 *   row's menu, so it is reachable with TalkBack, Switch Access, a keyboard, or no swiping at all.
 * - **Feel**: [QeetrixHapticRole.LightImpact] as an action is revealed, [QeetrixHapticRole.Warning] as a swipe passes
 *   the point where it will run. The row settles in `motion.duration.normal`, at once under reduced motion.
 *
 * ```
 * QeetrixSwipeActions(
 *     endAction = QeetrixSwipeAction("Remove", QeetrixIconName.Trash, ::remove, QeetrixSwipeActionTone.Danger,
 *         confirmTitle = "Remove Acme from this phone?"),
 * ) { QeetrixListItem("Acme", description = "priya@acme.in") }
 * ```
 */
@Composable
public fun QeetrixSwipeActions(
    modifier: Modifier = Modifier,
    startAction: QeetrixSwipeAction? = null,
    endAction: QeetrixSwipeAction? = null,
    content: @Composable () -> Unit,
) {
    val colors = QeetrixTheme.colors
    val haptics = rememberQeetrixHaptics()
    val scope = rememberCoroutineScope()
    val rtl = LocalLayoutDirection.current == LayoutDirection.Rtl
    val offset = remember { Animatable(0f) }
    var confirming by remember { mutableStateOf<QeetrixSwipeAction?>(null) }
    val reveal = with(LocalDensity.current) { REVEAL.toPx() }
    val settle = qeetrixAnimationSpec<Float>(QeetrixMotion.EasingStandard, QeetrixMotion.DurationNormal)

    fun run(action: QeetrixSwipeAction) {
        scope.launch { offset.animateTo(0f, settle) }
        if (action.tone == QeetrixSwipeActionTone.Danger && action.confirmTitle != null) confirming = action else action.onAction()
    }
    BoxWithConstraints(
        modifier
            .fillMaxWidth()
            .semantics {
                customActions =
                    listOfNotNull(startAction, endAction).map { action ->
                        CustomAccessibilityAction(action.label) {
                            run(action)
                            true
                        }
                    }
            },
    ) {
        val width = constraints.maxWidth.toFloat()
        // The offset is in screen pixels, as the drag's deltas are: positive moves the row right. Toward the end is
        // right in left to right and left in right to left; moving the row toward the end uncovers the start action.
        val toEnd = if (rtl) -1f else 1f
        val shown = offset.value * toEnd
        val revealed =
            if (shown > 0) {
                startAction
            } else if (shown < 0) {
                endAction
            } else {
                null
            }
        if (revealed != null) {
            val toward = if (shown > 0) Alignment.CenterStart else Alignment.CenterEnd
            Box(Modifier.matchParentSize().background(revealed.tone.fill(colors)), contentAlignment = toward) {
                Column(
                    Modifier
                        .width(with(LocalDensity.current) { abs(offset.value).toDp() }.coerceAtLeast(REVEAL))
                        .fillMaxHeight()
                        .clickable { run(revealed) },
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(QeetrixSpace.GapXs, Alignment.CenterVertically),
                ) {
                    val on = revealed.tone.content(colors)
                    QeetrixIcon(
                        revealed.icon,
                        contentDescription = null,
                        size = QeetrixSize.IconMd,
                        shape = QeetrixIconShape.Sharp,
                        tint = on,
                    )
                    BasicText(
                        revealed.label,
                        style =
                            QeetrixTheme.typography.labelSm
                                .copy(
                                    color = on,
                                    textAlign = TextAlign.Center,
                                ).labelDirection(alignToLayout = true),
                    )
                }
            }
        }
        Box(
            Modifier
                // Screen space, as the drag's deltas are: offset {} would mirror it in right to left, moving the row
                // against the finger and uncovering the wrong side.
                .absoluteOffset { IntOffset(offset.value.roundToInt(), 0) }
                .background(colors.surfaceDefault)
                .draggable(
                    rememberDraggableState { delta ->
                        val next = offset.value + delta
                        val visual = next * toEnd
                        val bounded =
                            when {
                                visual > 0 && startAction == null -> 0f
                                visual < 0 && endAction == null -> 0f
                                else -> next.coerceIn(-width, width)
                            }
                        val before = abs(offset.value)
                        scope.launch { offset.snapTo(bounded) }
                        val after = abs(bounded)
                        if (before < reveal && after >= reveal) haptics.perform(QeetrixHapticRole.LightImpact)
                        if (before < width * FULL && after >= width * FULL) haptics.perform(QeetrixHapticRole.Warning)
                    },
                    Orientation.Horizontal,
                    onDragStopped = {
                        val distance = abs(offset.value)
                        val visual = offset.value * toEnd
                        val action = if (visual > 0) startAction else endAction
                        when {
                            action != null && distance >= width * FULL -> run(action)
                            distance >= reveal / 2 -> offset.animateTo(if (offset.value > 0) reveal else -reveal, settle)
                            else -> offset.animateTo(0f, settle)
                        }
                    },
                ),
        ) { content() }
    }
    val pending = confirming
    if (pending != null) {
        QeetrixConfirmationDialog(
            open = true,
            onOpenChange = { if (!it) confirming = null },
            title = pending.confirmTitle.orEmpty(),
            confirmLabel = pending.label,
            onConfirm = {
                confirming = null
                pending.onAction()
            },
            description = pending.confirmDescription,
            intent = QeetrixDialogIntent.Danger,
        )
    }
}

private fun QeetrixSwipeActionTone.fill(colors: QeetrixColors): Color =
    when (this) {
        QeetrixSwipeActionTone.Neutral -> colors.bgNeutralBold
        QeetrixSwipeActionTone.Brand -> colors.bgBrandBold
        QeetrixSwipeActionTone.Danger -> colors.bgDangerBold
    }

private fun QeetrixSwipeActionTone.content(colors: QeetrixColors): Color =
    when (this) {
        QeetrixSwipeActionTone.Neutral -> colors.textInverse
        QeetrixSwipeActionTone.Brand -> colors.textOnBrand
        QeetrixSwipeActionTone.Danger -> colors.textOnBold
    }

/** How far a row opens to show one action. */
private val REVEAL = 88.dp

/** The share of the row's width past which a swipe runs the action. */
private const val FULL = 0.6f
