package com.qeetgroup.qeetrix.components.input

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.InteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalDensity
import com.qeetgroup.qeetrix.foundation.accessibility.QeetrixAccessibility
import com.qeetgroup.qeetrix.foundation.color.QeetrixOpacity
import com.qeetgroup.qeetrix.foundation.elevation.QeetrixElevation
import com.qeetgroup.qeetrix.foundation.elevation.qeetrixElevation
import com.qeetgroup.qeetrix.foundation.motion.QeetrixDuration
import com.qeetgroup.qeetrix.foundation.motion.QeetrixEasing
import com.qeetgroup.qeetrix.foundation.motion.qeetrixAnimationSpec
import com.qeetgroup.qeetrix.foundation.sizing.QeetrixStroke
import com.qeetgroup.qeetrix.internal.QeetrixWebScale
import com.qeetgroup.qeetrix.internal.qeetrixFocusRing
import com.qeetgroup.qeetrix.theme.QeetrixTheme
import com.qeetgroup.qeetrix.tokens.generated.QeetrixInputComponentTokens

// The selection vocabulary the web's Checkbox and Radio share: the field fill and its ≥3:1 boundary at
// rest, a Qeet Ember fill with a light glyph when chosen (the edge takes the darker border-brand, which
// clears 3:1 on every surface), the foundation focus ring, and the invalid boundary.

/**
 * The 16 dp box or disc of a checkbox or radio ([shape]), with [glyph] drawn in the on-brand colour when
 * [marked]. Hover moves an unmarked, valid control's boundary one step; focus draws the ring, in the error
 * colour when [invalid].
 */
@Composable
internal fun SelectionMark(
    shape: Shape,
    marked: Boolean,
    enabled: Boolean,
    invalid: Boolean,
    source: InteractionSource,
    glyph: @Composable BoxScope.() -> Unit,
) {
    val hovered by source.collectIsHoveredAsState()
    val motion = qeetrixAnimationSpec<Color>(QeetrixEasing.Standard, QeetrixDuration.Fast)
    val border by animateColorAsState(
        when {
            invalid -> QeetrixInputComponentTokens.BorderInvalid

            // aria-invalid:border-(input-border-invalid)
            marked -> QeetrixTheme.colors.borderBrand

            // data-checked:border-border-brand
            hovered && enabled -> QeetrixInputComponentTokens.BorderHover

            else -> QeetrixInputComponentTokens.Border
        },
        motion,
        label = "selection-border",
    )
    val fill by animateColorAsState(
        if (marked) QeetrixTheme.colors.actionPrimary else QeetrixInputComponentTokens.Background, // data-checked:bg-primary
        motion,
        label = "selection-fill",
    )
    Box(
        Modifier
            .qeetrixFocusRing(
                source,
                shape,
                color = if (invalid) QeetrixInputComponentTokens.BorderInvalid else QeetrixTheme.colors.focusRing,
            ).then(if (enabled) Modifier else Modifier.alpha(QeetrixOpacity.Disabled)) // data-disabled:opacity-disabled
            .size(QeetrixWebScale.spacing(4f)) // size-4
            .qeetrixElevation(QeetrixElevation.Flat, shape, fill)
            .border(QeetrixStroke.Hairline, border, shape),
        contentAlignment = Alignment.Center,
    ) {
        if (marked) glyph()
    }
}

/**
 * A checkbox or radio with its label and description — `Field orientation="horizontal"`: the mark at the
 * start, 8 dp before a `FieldContent` of label and description. The whole row is the control and at least a
 * 48 dp touch target; [modifier] carries its toggle or selection.
 */
@Composable
internal fun SelectionRow(
    label: String,
    description: String?,
    enabled: Boolean,
    modifier: Modifier,
    mark: @Composable () -> Unit,
) {
    Box(
        modifier.selectionRow().fillMaxWidth().heightIn(min = QeetrixAccessibility.MinimumTouchTarget),
        contentAlignment = Alignment.CenterStart,
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(QeetrixWebScale.spacing(2f)), // gap-2
            verticalAlignment = Alignment.Top, // items-start, the mark on the label's first line
        ) {
            val firstLine = with(LocalDensity.current) { FieldLabelStyle.fontSize.toDp() } * QeetrixWebScale.LEADING_SNUG
            Box(Modifier.height(firstLine), contentAlignment = Alignment.Center) { mark() }
            // FieldContent: flex-1 flex-col gap-0.5
            Column(
                Modifier.weight(1f).then(if (enabled) Modifier else Modifier.alpha(QeetrixOpacity.Disabled)),
                verticalArrangement = Arrangement.spacedBy(QeetrixWebScale.spacing(0.5f)),
            ) {
                BasicText(label, Modifier.fillMaxWidth(), style = FieldLabelStyle.copy(color = QeetrixTheme.colors.textPrimary))
                if (description != null) FieldDescription(description)
            }
        }
    }
}
