package com.qeetgroup.qeetrix.components.input

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.drawOutline
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.node.ModifierNodeElement
import androidx.compose.ui.node.ParentDataModifierNode
import androidx.compose.ui.platform.InspectorInfo
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.qeetgroup.qeetrix.R
import com.qeetgroup.qeetrix.foundation.accessibility.QeetrixFocus
import com.qeetgroup.qeetrix.foundation.color.QeetrixOpacity
import com.qeetgroup.qeetrix.foundation.motion.QeetrixDuration
import com.qeetgroup.qeetrix.foundation.motion.QeetrixEasing
import com.qeetgroup.qeetrix.foundation.motion.qeetrixAnimationSpec
import com.qeetgroup.qeetrix.foundation.sizing.QeetrixStroke
import com.qeetgroup.qeetrix.foundation.typography.QeetrixTextRole
import com.qeetgroup.qeetrix.icons.QeetrixIcon
import com.qeetgroup.qeetrix.icons.QeetrixIconName
import com.qeetgroup.qeetrix.internal.FontWeightMedium
import com.qeetgroup.qeetrix.internal.QeetrixFont
import com.qeetgroup.qeetrix.internal.QeetrixWebScale
import com.qeetgroup.qeetrix.internal.drawOutlineRing
import com.qeetgroup.qeetrix.internal.qeetrixFocusRing
import com.qeetgroup.qeetrix.internal.qeetrixTextStyle
import com.qeetgroup.qeetrix.internal.windowAtLeast
import com.qeetgroup.qeetrix.theme.QeetrixTheme
import com.qeetgroup.qeetrix.tokens.generated.QeetrixInputComponentTokens

// The parts every Qeetrix field is made of — the web's `Field` family and its field recipe
// (`src/internal/field-styles.ts`) — shared by QeetrixField and each input, so every field reads the same.

/** gap-1.5: a field's label, control, description and message sit 6 dp apart. */
internal val FieldPartGap: Dp = QeetrixWebScale.spacing(1.5f)

/**
 * px-2.5 inside the 1 dp border: the padding of a single-line field. Vertically only the border: the web
 * fixes the field at the control height and centres a line that leaves no room for its `py-1`, so the
 * height here is a minimum of the control height with the content centred — exactly 32 dp at the default
 * density — and larger text grows it.
 */
internal val FieldContentPadding =
    PaddingValues(
        horizontal = QeetrixWebScale.spacing(2.5f) + QeetrixStroke.Hairline,
        vertical = QeetrixStroke.Hairline,
    )

/** The field's text: `text-base` below the web's `md` breakpoint (768 dp of window width), `text-sm` from it. */
@Composable
internal fun fieldTextStyle(): TextStyle =
    (if (windowAtLeast(QeetrixWebScale.BreakpointMd)) FieldTextSm else FieldTextBase).copy(color = QeetrixInputComponentTokens.Foreground)

/**
 * A field's layout: [control] (which places its own label, so the label is part of the control for
 * accessibility services), then the [description] and the [message], 6 dp apart — `Field
 * orientation="vertical"`.
 */
@Composable
internal fun FieldFrame(
    description: String?,
    message: QeetrixFieldMessage?,
    modifier: Modifier,
    control: @Composable ColumnScope.() -> Unit,
) {
    Column(modifier, verticalArrangement = Arrangement.spacedBy(FieldPartGap)) {
        control()
        if (description != null) FieldDescription(description)
        if (message != null) FieldMessage(message)
    }
}

/**
 * FieldLabel: text-sm leading-snug font-medium, with the required asterisk or the "(optional)" marker.
 * Accessibility services read the asterisk as "required" (`qx_required`), as the web announces the
 * control's `required`.
 */
@Composable
internal fun FieldLabel(
    label: String,
    required: Boolean,
    optional: Boolean,
    enabled: Boolean,
    modifier: Modifier = Modifier,
    style: TextStyle = FieldLabelStyle,
) {
    Row(
        modifier.then(if (enabled) Modifier else Modifier.alpha(QeetrixOpacity.Disabled)),
        horizontalArrangement = Arrangement.spacedBy(QeetrixWebScale.spacing(0.5f)), // gap-2 with -ms-1.5
        verticalAlignment = Alignment.CenterVertically,
    ) {
        BasicText(label, style = style.copy(color = QeetrixTheme.colors.textPrimary))
        if (required) {
            val spoken = stringResource(R.string.qx_required)
            BasicText(
                "*",
                Modifier.semantics { contentDescription = spoken },
                style = style.copy(color = QeetrixTheme.colors.textDanger),
            )
        } else if (optional) {
            // -ms-1 font-normal text-muted-foreground
            BasicText(
                stringResource(R.string.qx_optional),
                Modifier.padding(start = QeetrixWebScale.spacing(0.5f)),
                style = FieldOptionalStyle.copy(color = QeetrixTheme.colors.textTertiary),
            )
        }
    }
}

/** FieldDescription: text-caption font-normal text-muted-foreground, a block as wide as the field. */
@Composable
internal fun FieldDescription(
    text: String,
    modifier: Modifier = Modifier,
) {
    BasicText(text, modifier.fillMaxWidth(), style = FieldCaptionStyle.copy(color = QeetrixTheme.colors.textTertiary))
}

/** FieldError / FieldWarning / FieldSuccess: icon and caption in the tone's colour, announced as it appears. */
@Composable
internal fun FieldMessage(message: QeetrixFieldMessage) {
    val (color, icon) =
        when (message.tone) {
            QeetrixFieldMessageTone.Error -> QeetrixTheme.colors.textDanger to QeetrixIconName.CircleAlert
            QeetrixFieldMessageTone.Warning -> QeetrixTheme.colors.textWarning to QeetrixIconName.TriangleAlert
            QeetrixFieldMessageTone.Success -> QeetrixTheme.colors.textSuccess to QeetrixIconName.CircleCheck
        }
    Row(
        Modifier.semantics(mergeDescendants = true) {
            // role="alert" for an error, role="status" otherwise
            liveRegion = if (message.tone == QeetrixFieldMessageTone.Error) LiveRegionMode.Assertive else LiveRegionMode.Polite
        },
        horizontalArrangement = Arrangement.spacedBy(QeetrixWebScale.spacing(1.5f)), // gap-1.5
        verticalAlignment = Alignment.Top, // items-start
    ) {
        // The web's mt-px centres the icon on the first line because both are in rem; here the icon is in dp
        // and the text follows the font scale, so the icon is centred on the first line box instead.
        val firstLine = with(LocalDensity.current) { FieldCaptionStyle.fontSize.toDp() } * CaptionLineHeight
        Box(Modifier.height(firstLine), contentAlignment = Alignment.Center) {
            QeetrixIcon(icon, contentDescription = null, size = QeetrixWebScale.spacing(3.5f), tint = color)
        }
        BasicText(message.text, Modifier.weight(1f), style = FieldCaptionStyle.copy(color = color))
    }
}

/**
 * The field recipe's surface: the writing surface, its 1 dp boundary and every state — hover (editable
 * only), focus (the boundary turns the ring and a 1 dp outline thickens it to 2 dp), the warning, success
 * and invalid boundaries from [tone], the dashed read-only boundary with no writing surface, and the
 * disabled sunken surface, faded.
 *
 * @param holdHover Keeps the hover boundary, as a select does while its list is open.
 */
@Composable
internal fun FieldBox(
    enabled: Boolean,
    readOnly: Boolean,
    focused: Boolean,
    hovered: Boolean,
    tone: QeetrixFieldMessageTone?,
    modifier: Modifier = Modifier,
    holdHover: Boolean = false,
    shape: Shape = RoundedCornerShape(QeetrixInputComponentTokens.Corner),
    minHeight: Dp = QeetrixInputComponentTokens.height(QeetrixTheme.density),
    contentPadding: PaddingValues = FieldContentPadding,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.spacedBy(QeetrixWebScale.spacing(2f)),
    verticalAlignment: Alignment.Vertical = Alignment.CenterVertically,
    content: @Composable RowScope.() -> Unit,
) {
    val editable = enabled && !readOnly
    val invalid = tone == QeetrixFieldMessageTone.Error
    // --field-edge (rest, hover) under --field-status (warning, success, invalid); focus turns it the ring.
    val edge = if ((hovered || holdHover) && editable) QeetrixInputComponentTokens.BorderHover else QeetrixInputComponentTokens.Border
    val status =
        when (tone) {
            QeetrixFieldMessageTone.Error -> QeetrixInputComponentTokens.BorderInvalid
            QeetrixFieldMessageTone.Warning -> QeetrixInputComponentTokens.BorderWarning
            QeetrixFieldMessageTone.Success -> QeetrixInputComponentTokens.BorderSuccess
            null -> null
        }
    val ring = if (invalid) QeetrixInputComponentTokens.BorderInvalid else QeetrixInputComponentTokens.BorderFocus
    val showsFocus = focused && enabled
    val border by animateColorAsState(
        if (showsFocus) ring else status ?: edge,
        qeetrixAnimationSpec(QeetrixEasing.Standard, QeetrixDuration.Fast),
        label = "field-border",
    )
    val background =
        when {
            !enabled -> QeetrixInputComponentTokens.BackgroundDisabled
            readOnly -> QeetrixInputComponentTokens.BackgroundReadonly
            else -> QeetrixInputComponentTokens.Background
        }
    val dashed = readOnly && enabled // read-only:not-disabled:border-dashed
    Row(
        modifier
            .then(if (enabled) Modifier else Modifier.alpha(QeetrixOpacity.Disabled)) // disabled:opacity-disabled
            // focus-ring-field: a 1px outline at offset 0 thickens the boundary to 2px
            .drawOutlineRing(showsFocus, shape, ring, QeetrixStroke.Hairline, 0.dp)
            .drawWithContent {
                drawOutline(shape.createOutline(size, layoutDirection, this), background)
                drawContent()
                // border (dashed when read-only), inside the box as CSS border-box draws it
                val width = QeetrixStroke.Hairline.toPx()
                val outline = shape.createOutline(Size(size.width - width, size.height - width), layoutDirection, this)
                val dash = if (dashed) PathEffect.dashPathEffect(floatArrayOf(3 * width, 3 * width)) else null
                translate(width / 2, width / 2) { drawOutline(outline, border, style = Stroke(width, pathEffect = dash)) }
            }.heightIn(min = minHeight) // h-(input-height), growing with the font scale
            .padding(contentPadding),
        horizontalArrangement = horizontalArrangement,
        verticalAlignment = verticalAlignment,
        content = content,
    )
}

/**
 * The web's `fieldAction`: a compact icon button inside a field — reveal a password, clear a value. Drawn the
 * web's size, max(24 dp, control height − 8 dp), so the field keeps its height; Compose's hit testing still
 * sends a touch within 48 dp of it to it when nothing else is hit there. Named by [label] for accessibility
 * services; keyboard focus draws an inset ring. A press shows the hover fill, since touch has no hover.
 */
@Composable
internal fun FieldAction(
    icon: QeetrixIconName,
    label: String,
    onClick: () -> Unit,
    enabled: Boolean,
    modifier: Modifier = Modifier,
) {
    val source = remember { MutableInteractionSource() }
    val hovered by source.collectIsHoveredAsState()
    val pressed by source.collectIsPressedAsState()
    val lit = enabled && (hovered || pressed)
    // size-[max(1.5rem,calc(var(--qx-component-input-height)-0.5rem))]
    val side = maxOf(QeetrixWebScale.spacing(6f), QeetrixInputComponentTokens.height(QeetrixTheme.density) - QeetrixWebScale.spacing(2f))
    // rounded-[max(0px,calc(var(--qx-component-input-corner)-2px))]
    val shape = RoundedCornerShape((QeetrixInputComponentTokens.Corner - QeetrixStroke.Thin).coerceAtLeast(0.dp))
    Box(
        modifier
            .size(side)
            .qeetrixFocusRing(source, shape, offset = -QeetrixFocus.OutlineWidth) // focus-visible:focus-ring-inset
            .background(if (lit) QeetrixTheme.colors.surfaceInteractiveHover else Color.Transparent, shape)
            .clickable(source, indication = null, enabled = enabled, role = Role.Button, onClick = onClick)
            .semantics { contentDescription = label }
            .then(if (enabled) Modifier else Modifier.alpha(QeetrixOpacity.Disabled)), // disabled:opacity-disabled
        contentAlignment = Alignment.Center,
    ) {
        // text-muted-foreground, hover:text-foreground; [&_svg]:size-4
        QeetrixIcon(
            icon,
            contentDescription = null,
            size = QeetrixWebScale.spacing(4f),
            tint = if (lit) QeetrixTheme.colors.textPrimary else QeetrixTheme.colors.textTertiary,
        )
    }
}

/**
 * What a [QeetrixFieldSet] gives the controls inside it: its message, which each checkbox and radio takes as
 * its own (the group is invalid, so each control is), and whether the set is enabled.
 */
@Immutable
internal class FieldGroup(
    val message: QeetrixFieldMessage?,
    val enabled: Boolean,
)

internal val LocalFieldGroup = staticCompositionLocalOf { FieldGroup(message = null, enabled = true) }

/**
 * Stacks a group's content: fields [spacing] apart, and checkbox, radio and switch rows — each already a
 * 48 dp touch target — flush against each other, as qeetrix-ios does.
 */
@Composable
internal fun FieldStack(
    spacing: Dp,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    Layout(content, modifier) { measurables, constraints ->
        val placeables = measurables.map { it.measure(constraints.copy(minWidth = 0, minHeight = 0)) }
        val rows = measurables.map { it.parentData === SelectionRowData }
        val gap = spacing.roundToPx()
        val tops = IntArray(placeables.size)
        var y = 0
        placeables.forEachIndexed { index, placeable ->
            if (index > 0) y += if (rows[index - 1] && rows[index]) 0 else gap
            tops[index] = y
            y += placeable.height
        }
        val width = (placeables.maxOfOrNull { it.width } ?: 0).coerceIn(constraints.minWidth, constraints.maxWidth)
        layout(width, y.coerceIn(constraints.minHeight, constraints.maxHeight)) {
            placeables.forEachIndexed { index, placeable -> placeable.placeRelative(0, tops[index]) }
        }
    }
}

/** Marks a checkbox, radio or labelled switch row, which [FieldStack] sets flush against the next. */
internal fun Modifier.selectionRow(): Modifier = this then SelectionRowElement

private object SelectionRowData

private object SelectionRowElement : ModifierNodeElement<SelectionRowNode>() {
    override fun create() = SelectionRowNode()

    override fun update(node: SelectionRowNode) = Unit

    override fun hashCode(): Int = 0

    override fun equals(other: Any?): Boolean = other === this

    override fun InspectorInfo.inspectableProperties() {
        name = "selectionRow"
    }
}

private class SelectionRowNode :
    Modifier.Node(),
    ParentDataModifierNode {
    override fun Density.modifyParentData(parentData: Any?): Any = SelectionRowData
}

internal val FieldLabelStyle = qeetrixTextStyle(QeetrixFont.Ui, QeetrixWebScale.TextSm.size, QeetrixWebScale.LEADING_SNUG, FontWeightMedium)
internal val FieldCaptionStyle = QeetrixTextRole.Caption.spec.let { qeetrixTextStyle(QeetrixFont.Sans, it.size, it.lineHeight) }
private val FieldOptionalStyle = qeetrixTextStyle(QeetrixFont.Ui, QeetrixWebScale.TextSm.size, QeetrixWebScale.LEADING_SNUG)
private val FieldTextBase = qeetrixTextStyle(QeetrixFont.Sans, QeetrixWebScale.TextBase)
private val FieldTextSm = qeetrixTextStyle(QeetrixFont.Sans, QeetrixWebScale.TextSm)
private val CaptionLineHeight = QeetrixTextRole.Caption.spec.lineHeight
