package com.qeetgroup.qeetrix.components.steps

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.qeetgroup.qeetrix.R
import com.qeetgroup.qeetrix.components.progress.QeetrixProgress
import com.qeetgroup.qeetrix.foundation.accessibility.qeetrixMinimumTouchTarget
import com.qeetgroup.qeetrix.foundation.shape.QeetrixShapes
import com.qeetgroup.qeetrix.foundation.sizing.QeetrixBorder
import com.qeetgroup.qeetrix.foundation.spacing.QeetrixSpace
import com.qeetgroup.qeetrix.foundation.typography.QeetrixTextRole
import com.qeetgroup.qeetrix.icons.QeetrixIcon
import com.qeetgroup.qeetrix.icons.QeetrixIconName
import com.qeetgroup.qeetrix.icons.QeetrixIconShape
import com.qeetgroup.qeetrix.internal.labelDirection
import com.qeetgroup.qeetrix.theme.QeetrixTheme

/** How [QeetrixSteps] are laid out (`value` is `@qeetrix/react`'s StepsOrientation). */
public enum class QeetrixStepsOrientation(
    public val value: String,
) {
    /** Across the screen, for a short wizard; compact ("Step 2 of 4" and a bar) where there isn't room. The default. */
    Horizontal("horizontal"),

    /** Down the screen, with descriptions, for a longer setup. */
    Vertical("vertical"),
}

/** A step's state when `current` alone can't say it (`value` is `@qeetrix/react`'s StepStatus). */
public enum class QeetrixStepStatus(
    public val value: String,
) {
    /** Before the current step: `color.bg.brand.bold` with a `check`. */
    Done("done"),

    /** The step being done: a 2 dp `color.border.brand` edge. */
    Current("current"),

    /** Still to come: a 1 dp `color.border.strong` edge. */
    Upcoming("upcoming"),

    /** A check that didn't pass: `color.bg.danger.bold` with an `x`. */
    Failed("failed"),

    /** It needs work: `color.bg.warning.bold` with `triangle-alert`. */
    Attention("attention"),
}

/** One step of [QeetrixSteps]: a short [label], a [description] for the vertical layout, and a [status] over `current`'s. */
public class QeetrixStep(
    public val label: String,
    public val description: String? = null,
    public val status: QeetrixStepStatus? = null,
)

/**
 * Where a person is in a setup of a few steps — `@qeetrix/react`'s Steps: done steps filled Ember with a tick, the
 * current one edged in Ember, those to come grey, joined by a 1 dp line that turns Ember behind them (steps.css).
 *
 * - **[current]** is the current step's index from 0, or `steps.size` once every step is done. A step's own
 *   [QeetrixStep.status] overrides it, so a later step can stay done while an earlier one fails.
 * - **Markers**: 24 dp, `radius.control`, the number in `label.sm` at weight 600 with tabular figures; done
 *   `color.bg.brand.bold` with a `check`, current a 2 dp `color.border.brand` edge, upcoming a 1 dp `color.border.strong`
 *   edge, failed and attention filled in their tones.
 * - **[QeetrixStepsOrientation.Horizontal]**: equal columns, the label under the marker (current at weight 600, upcoming
 *   `color.text.subtle`); where each step would have under 72 dp, or a label's word wouldn't fit its column (at large
 *   text), it becomes "Step 2 of 4", the current label and a progress bar. **Vertical**: the marker, then the label and description, the line running down between markers.
 * - **[onStepSelect]** makes done steps buttons back to themselves; steps ahead never are. The compact form shows only
 *   the current step, so there going back is the screen's own Back.
 * - **Accessibility**: each step is read with its state and position — "Configure, Current step, 2 of 4".
 *
 * ```
 * QeetrixSteps(listOf(QeetrixStep("Account"), QeetrixStep("Verify"), QeetrixStep("Done")), current = 1)
 * ```
 */
@Composable
public fun QeetrixSteps(
    steps: List<QeetrixStep>,
    current: Int,
    modifier: Modifier = Modifier,
    orientation: QeetrixStepsOrientation = QeetrixStepsOrientation.Horizontal,
    onStepSelect: ((Int) -> Unit)? = null,
) {
    require(steps.isNotEmpty()) { "Steps need at least one step." }
    require(current in 0..steps.size) { "current is a step's index, or steps.size once all are done." }
    val states = steps.mapIndexed { index, step -> step.status ?: defaultStatus(index, current) }
    if (orientation == QeetrixStepsOrientation.Vertical) {
        Column(modifier.fillMaxWidth()) {
            steps.forEachIndexed { index, step -> VerticalStep(step, index, steps.size, states, onStepSelect) }
        }
        return
    }
    val measurer = rememberTextMeasurer()
    // The heaviest a label is drawn: the current step's.
    val heaviest = QeetrixTheme.typography.bodyMd.copy(fontWeight = FontWeight(QeetrixTextRole.TitleSm.spec.weight))
    BoxWithConstraints(modifier.fillMaxWidth()) {
        val column = maxWidth / steps.size
        // A word wider than its column would break mid-word ("Accou / nt" at 200 % text): go compact instead.
        val room = with(LocalDensity.current) { (column - QeetrixSpace.GapXs * 2).roundToPx() }
        val wordsFit =
            remember(steps, room, heaviest) {
                steps.all { step -> step.label.split(' ').all { word -> measurer.measure(word, heaviest).size.width <= room } }
            }
        if (column < COMPACT_STEP || !wordsFit) {
            CompactSteps(steps, current, states)
        } else {
            Row(Modifier.fillMaxWidth()) {
                steps.forEachIndexed { index, step ->
                    HorizontalStep(step, index, steps.size, states, onStepSelect, Modifier.weight(1f))
                }
            }
        }
    }
}

private fun defaultStatus(
    index: Int,
    current: Int,
): QeetrixStepStatus =
    when {
        index < current -> QeetrixStepStatus.Done
        index == current -> QeetrixStepStatus.Current
        else -> QeetrixStepStatus.Upcoming
    }

@Composable
private fun Modifier.stepSemantics(
    step: QeetrixStep,
    index: Int,
    count: Int,
    status: QeetrixStepStatus,
    select: (() -> Unit)?,
): Modifier {
    val position = stringResource(R.string.qx_steps_position, index + 1, count)
    val state =
        when (status) {
            QeetrixStepStatus.Done -> stringResource(R.string.qx_steps_done)
            QeetrixStepStatus.Current -> stringResource(R.string.qx_steps_current)
            QeetrixStepStatus.Upcoming -> stringResource(R.string.qx_steps_upcoming)
            QeetrixStepStatus.Failed -> stringResource(R.string.qx_steps_failed)
            QeetrixStepStatus.Attention -> stringResource(R.string.qx_steps_attention)
        }
    val selectable = select != null && status == QeetrixStepStatus.Done
    val clickable =
        if (selectable &&
            select != null
        ) {
            Modifier.qeetrixMinimumTouchTarget().clickable(role = Role.Button, onClick = select)
        } else {
            Modifier
        }
    return this.then(clickable).clearAndSetSemantics {
        contentDescription = listOfNotNull(step.label, step.description).joinToString(", ")
        stateDescription = "$state, $position"
        if (selectable) role = Role.Button
    }
}

@Composable
private fun HorizontalStep(
    step: QeetrixStep,
    index: Int,
    count: Int,
    states: List<QeetrixStepStatus>,
    onStepSelect: ((Int) -> Unit)?,
    modifier: Modifier,
) {
    val colors = QeetrixTheme.colors
    val status = states[index]
    val line = if (status == QeetrixStepStatus.Done) colors.borderBrand else colors.borderSubtle
    val last = index == count - 1
    val side = markerSize()
    Column(
        modifier
            .drawBehind {
                if (last) return@drawBehind
                // ::after: 1 dp border.subtle (border.brand behind a done step), from this marker's end to the next one's start.
                val half = side.toPx() / 2
                val gap = QeetrixSpace.GapSm.toPx()
                val from = size.width / 2 + half + gap
                val to = size.width * 1.5f - half - gap
                val rtl = layoutDirection == LayoutDirection.Rtl
                val x0 = if (rtl) size.width - from else from
                val x1 = if (rtl) size.width - to else to
                drawLine(line, Offset(x0, half), Offset(x1, half), QeetrixBorder.WidthDefault.toPx())
            }.stepSemantics(step, index, count, status, onStepSelect?.let { { it(index) } })
            .padding(horizontal = QeetrixSpace.GapXs),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(QeetrixSpace.GapXs),
    ) {
        Marker(index, status)
        Label(step.label, status, TextAlign.Center)
    }
}

@Composable
private fun VerticalStep(
    step: QeetrixStep,
    index: Int,
    count: Int,
    states: List<QeetrixStepStatus>,
    onStepSelect: ((Int) -> Unit)?,
) {
    val colors = QeetrixTheme.colors
    val status = states[index]
    val line = if (status == QeetrixStepStatus.Done) colors.borderBrand else colors.borderSubtle
    val last = index == count - 1
    val side = markerSize()
    Row(
        Modifier
            .fillMaxWidth()
            .drawBehind {
                if (last) return@drawBehind
                val half = side.toPx() / 2
                val rtl = layoutDirection == LayoutDirection.Rtl
                val x = if (rtl) size.width - half else half
                val top = side.toPx() + QeetrixSpace.GapXs.toPx()
                drawLine(line, Offset(x, top), Offset(x, size.height - QeetrixSpace.GapXs.toPx()), QeetrixBorder.WidthDefault.toPx())
            }.stepSemantics(step, index, count, status, onStepSelect?.let { { it(index) } })
            .padding(bottom = if (last) 0.dp else QeetrixSpace.GapLg),
        horizontalArrangement = Arrangement.spacedBy(QeetrixSpace.GapSm),
    ) {
        Marker(index, status)
        Column(Modifier.weight(1f).padding(top = 2.dp), verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Label(step.label, status, TextAlign.Start)
            if (step.description != null) {
                // .qx-step-description: body.sm, color.text.subtle
                BasicText(
                    step.description,
                    style =
                        QeetrixTheme.typography.bodySm
                            .copy(color = colors.textSubtle)
                            .labelDirection(alignToLayout = true),
                )
            }
        }
    }
}

@Composable
private fun CompactSteps(
    steps: List<QeetrixStep>,
    current: Int,
    states: List<QeetrixStepStatus>,
) {
    val colors = QeetrixTheme.colors
    val shown = current.coerceAtMost(steps.size - 1)
    val words = stringResource(R.string.qx_steps_compact, shown + 1, steps.size)
    Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(QeetrixSpace.GapXs)) {
        Row(horizontalArrangement = Arrangement.spacedBy(QeetrixSpace.GapSm)) {
            BasicText(
                words,
                style =
                    QeetrixTheme.typography.labelSm
                        .copy(color = colors.textSubtle)
                        .labelDirection(alignToLayout = true),
            )
            Label(steps[shown].label, states[shown], TextAlign.Start)
        }
        QeetrixProgress(current.toFloat(), max = steps.size.toFloat(), contentDescription = words)
    }
}

@Composable
private fun Marker(
    index: Int,
    status: QeetrixStepStatus,
) {
    val colors = QeetrixTheme.colors
    val shape = QeetrixShapes.Control // border-radius: radius.control
    val fill =
        when (status) {
            QeetrixStepStatus.Done -> colors.bgBrandBold
            QeetrixStepStatus.Failed -> colors.bgDangerBold
            QeetrixStepStatus.Attention -> colors.bgWarningBold
            else -> Color.Transparent
        }
    val content =
        when (status) {
            QeetrixStepStatus.Done -> colors.textOnBrand
            QeetrixStepStatus.Current -> colors.textBrand
            QeetrixStepStatus.Upcoming -> colors.textSubtle
            QeetrixStepStatus.Failed -> colors.textOnBold
            QeetrixStepStatus.Attention -> colors.textOnWarning
        }
    val edge =
        when (status) {
            // [data-state="current"]: box-shadow inset border.width.strong border.brand
            QeetrixStepStatus.Current -> Modifier.border(QeetrixBorder.WidthStrong, colors.borderBrand, shape)

            // box-shadow: inset 0 0 0 border.width.default border.strong
            QeetrixStepStatus.Upcoming -> Modifier.border(QeetrixBorder.WidthDefault, colors.borderStrong, shape)

            else -> Modifier
        }
    // 24 dp, growing with the number's line as text grows.
    val side = markerSize()
    Box(Modifier.size(side).background(fill, shape).then(edge), contentAlignment = Alignment.Center) {
        val icon =
            when (status) {
                QeetrixStepStatus.Done -> QeetrixIconName.Check
                QeetrixStepStatus.Failed -> QeetrixIconName.X
                QeetrixStepStatus.Attention -> QeetrixIconName.TriangleAlert
                else -> null
            }
        if (icon != null) {
            QeetrixIcon(icon, contentDescription = null, size = ICON, shape = QeetrixIconShape.Sharp, tint = content)
        } else {
            BasicText(
                (index + 1).toString(),
                style =
                    QeetrixTheme.typography.labelSm
                        .copy(
                            color = content,
                            fontWeight = FontWeight(QeetrixTextRole.TitleSm.spec.weight),
                            fontFeatureSettings = "tnum",
                        ).labelDirection(alignToLayout = true),
            )
        }
    }
}

@Composable
private fun Label(
    text: String,
    status: QeetrixStepStatus,
    align: TextAlign,
) {
    val colors = QeetrixTheme.colors
    val typography = QeetrixTheme.typography
    BasicText(
        text,
        style =
            typography.bodyMd
                .copy(
                    color = if (status == QeetrixStepStatus.Upcoming) colors.textSubtle else colors.textDefault,
                    fontWeight =
                        if (status ==
                            QeetrixStepStatus.Current
                        ) {
                            FontWeight(QeetrixTextRole.TitleSm.spec.weight)
                        } else {
                            typography.bodyMd.fontWeight
                        },
                    textAlign = align,
                ).labelDirection(alignToLayout = true),
    )
}

/** `.qx-step-marker`: 24 px. */
private val MARKER = 24.dp

/** The marker's side: 24 dp, or the number's line (`label.sm`) when large text makes it taller. */
@Composable
private fun markerSize(): androidx.compose.ui.unit.Dp =
    maxOf(
        MARKER,
        com.qeetgroup.qeetrix.internal
            .firstLineHeight(QeetrixTheme.typography.labelSm),
    )

/** The mark inside a done, failed or attention marker. */
private val ICON = 14.dp

/** Below this width per step, horizontal steps go compact. */
private val COMPACT_STEP = 72.dp
