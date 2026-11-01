package com.qeetgroup.qeetrix.components.input

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.autofill.ContentType
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.drawOutline
import androidx.compose.ui.layout.layout
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.contentType
import androidx.compose.ui.semantics.error
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.paneTitle
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import com.qeetgroup.qeetrix.R
import com.qeetgroup.qeetrix.components.button.QeetrixButtonSize
import com.qeetgroup.qeetrix.components.button.QeetrixButtonTone
import com.qeetgroup.qeetrix.components.button.QeetrixButtonVariant
import com.qeetgroup.qeetrix.components.button.buttonColors
import com.qeetgroup.qeetrix.components.button.buttonMetrics
import com.qeetgroup.qeetrix.foundation.accessibility.QeetrixAccessibility
import com.qeetgroup.qeetrix.foundation.accessibility.QeetrixFocus
import com.qeetgroup.qeetrix.foundation.elevation.QeetrixShadow
import com.qeetgroup.qeetrix.foundation.elevation.qeetrixShadow
import com.qeetgroup.qeetrix.foundation.motion.QeetrixMotion
import com.qeetgroup.qeetrix.foundation.motion.qeetrixAnimationSpec
import com.qeetgroup.qeetrix.foundation.shape.QeetrixShapes
import com.qeetgroup.qeetrix.foundation.sizing.QeetrixBorder
import com.qeetgroup.qeetrix.foundation.sizing.QeetrixSize
import com.qeetgroup.qeetrix.foundation.spacing.QeetrixSpace
import com.qeetgroup.qeetrix.foundation.typography.QeetrixTextRole
import com.qeetgroup.qeetrix.icons.QeetrixIcon
import com.qeetgroup.qeetrix.icons.QeetrixIconName
import com.qeetgroup.qeetrix.icons.QeetrixIconShape
import com.qeetgroup.qeetrix.internal.AnchoredPopupPosition
import com.qeetgroup.qeetrix.internal.FocusFrame
import com.qeetgroup.qeetrix.internal.PopupAlign
import com.qeetgroup.qeetrix.internal.firstLineHeight
import com.qeetgroup.qeetrix.internal.focusFrame
import com.qeetgroup.qeetrix.internal.labelDirection
import com.qeetgroup.qeetrix.internal.popupEnter
import com.qeetgroup.qeetrix.internal.popupExit
import com.qeetgroup.qeetrix.internal.qeetrixFocusFrame
import com.qeetgroup.qeetrix.theme.LocalQeetrixContentColor
import com.qeetgroup.qeetrix.theme.QeetrixTheme

// The parts every Qeetrix field is built from — @qeetrix/react's Field (field.css: label, description, error) and
// the Input box (input.css: the filled box with its bottom line) — shared by QeetrixField and every input, so every
// field reads and behaves the same (design rules §8, component specs §2).

/** `.qx-field { gap: space.gap.xs }`: the label 4 dp above the control, the help text or error 4 dp below it. */
internal val FieldPartGap: Dp = QeetrixSpace.GapXs

/** `.qx-input { padding-inline: space.inset.md }`: 12 dp inside the box; vertically the box centres its line. */
internal val FieldPadding: PaddingValues = PaddingValues(horizontal = QeetrixSpace.InsetMd)

/** `.qx-search-field`, `.qx-password-input`: `padding-inline-end: space.gap.xs`, so a tool at the end sits near the edge. */
internal val FieldPaddingWithTool: PaddingValues = PaddingValues(start = QeetrixSpace.InsetMd, end = QeetrixSpace.GapXs)

/** `.qx-input { gap: space.gap.sm }`: between an icon, an adornment and the value. */
internal val FieldContentGap: Dp = QeetrixSpace.GapSm

/** `.qx-input { font: typography.body.md }`: the value, at every window width. */
@Composable
internal fun fieldTextStyle(): TextStyle = QeetrixTheme.typography.bodyMd

/**
 * `body.md`'s line at the 1× font scale, which CSS writes as `1lh` in the box metrics (a textarea's padding
 * `(size.control.lg − 1lh) / 2`). The text itself follows the font scale; the padding stays the web's.
 */
internal val BodyMdLine: Dp = QeetrixTextRole.BodyMd.spec.let { (it.size * it.lineHeight).dp }

/**
 * `.qx-field-label`: `label.sm` in `color.text.subtle` (`color.text.disabled` when disabled), with " (optional)"
 * after it in `color.text.subtlest` at `body.sm`'s weight when [optional]. Qeetrix marks optional fields and never
 * required ones; a [required] field still says "required" to accessibility services, as the web's native
 * `required` does.
 */
@Composable
internal fun FieldLabel(
    label: String,
    optional: Boolean,
    required: Boolean,
    enabled: Boolean,
    modifier: Modifier = Modifier,
) {
    val colors = QeetrixTheme.colors
    val typography = QeetrixTheme.typography
    val optionalText = stringResource(R.string.qx_optional)
    val spoken = accessibleLabel(label, required = required, optional = false)
    val text =
        if (optional) {
            buildAnnotatedString {
                append(label)
                append(' ')
                // .qx-field-optional { font-weight: typography.body.sm-font-weight; color: text.subtlest }
                withStyle(SpanStyle(color = colors.textSubtlest, fontWeight = typography.bodySm.fontWeight)) { append(optionalText) }
            }
        } else {
            AnnotatedString(label)
        }
    BasicText(
        text,
        modifier.then(if (required) Modifier.semantics { contentDescription = spoken } else Modifier),
        style = typography.labelSm.copy(color = if (enabled) colors.textSubtle else colors.textDisabled).labelDirection(),
    )
}

/**
 * The label row: [FieldLabel], then — with [help] — the ⓘ button that opens it (07-components.md, Field help), sized
 * to the label's line so a field with help lines up with its neighbours. [labelModifier] goes on the label alone, so
 * hiding the label's text from accessibility services never hides the help button with it.
 */
@Composable
internal fun FieldLabelRow(
    label: String,
    optional: Boolean,
    required: Boolean,
    enabled: Boolean,
    help: String?,
    modifier: Modifier = Modifier,
    labelModifier: Modifier = Modifier,
) {
    if (help == null) {
        FieldLabel(label, optional, required, enabled, modifier.then(labelModifier))
        return
    }
    // .qx-field-label-row { align-items: center; gap: space.gap.xs }
    Row(modifier, horizontalArrangement = Arrangement.spacedBy(QeetrixSpace.GapXs), verticalAlignment = Alignment.CenterVertically) {
        FieldLabel(label, optional, required, enabled, Modifier.weight(1f, fill = false).then(labelModifier))
        FieldHelp(label, help)
    }
}

/** `.qx-field-description`: `body.sm` in `color.text.subtle`; disabled, `color.text.subtlest`, which stays readable. */
@Composable
internal fun FieldDescription(
    text: String,
    enabled: Boolean,
    modifier: Modifier = Modifier,
) {
    val colors = QeetrixTheme.colors
    BasicText(
        text,
        modifier.fillMaxWidth(),
        style =
            QeetrixTheme.typography.bodySm
                .copy(
                    color = if (enabled) colors.textSubtle else colors.textSubtlest,
                ).labelDirection(alignToLayout = true),
    )
}

/**
 * `.qx-field-error`: a sharp 16 dp `circle-alert` in `color.icon.danger`, `space.gap.xs` before the message in
 * `body.sm` / `color.text.danger`. Announced as it appears. The icon is centred on the first line, which the web's
 * `align-items: flex-start` gives at every zoom; here only the text follows the font scale.
 */
@Composable
internal fun FieldError(
    text: String,
    modifier: Modifier = Modifier,
) {
    val colors = QeetrixTheme.colors
    val style = QeetrixTheme.typography.bodySm.copy(color = colors.textDanger)
    Row(
        modifier.fillMaxWidth().semantics(mergeDescendants = true) { liveRegion = LiveRegionMode.Assertive },
        horizontalArrangement = Arrangement.spacedBy(QeetrixSpace.GapXs),
        verticalAlignment = Alignment.Top,
    ) {
        Box(Modifier.height(firstLineHeight(style)), contentAlignment = Alignment.Center) {
            QeetrixIcon(
                QeetrixIconName.CircleAlert,
                contentDescription = null,
                size = QeetrixSize.IconSm,
                shape = QeetrixIconShape.Sharp,
                tint = colors.iconDanger,
            )
        }
        BasicText(text, Modifier.weight(1f), style = style.labelDirection(alignToLayout = true))
    }
}

/**
 * The help text and the error under a control (react's `FieldMessages`): the [error] replaces the [description]
 * while it shows — the description is not composed meanwhile, so it is gone for accessibility services too.
 */
@Composable
internal fun FieldMessages(
    description: String?,
    error: String?,
    enabled: Boolean,
) {
    if (error != null) {
        FieldError(error)
    } else if (description != null) {
        FieldDescription(description, enabled)
    }
}

/**
 * A field's layout (`.qx-field`, a column `space.gap.xs` apart): [control] — which places its own label, so the
 * label is part of the control for accessibility services and a tap on it reaches the control — then the
 * description or the error.
 */
@Composable
internal fun FieldFrame(
    description: String?,
    error: String?,
    enabled: Boolean,
    modifier: Modifier,
    control: @Composable ColumnScope.() -> Unit,
) {
    Column(modifier, verticalArrangement = Arrangement.spacedBy(FieldPartGap)) {
        control()
        FieldMessages(description, error, enabled)
    }
}

/**
 * The label above a control, inside the control's own layout — a text field's decorator, a select's trigger — so
 * accessibility services read it as the control's name and a tap on it reaches the control.
 *
 * With [hideLabel] (react's `hideLabel`: a toolbar search with a visible icon) the label is not drawn but still names
 * the control, and the control is centred in a 48 dp touch height of its own.
 */
@Composable
internal fun LabelledControl(
    label: String,
    optional: Boolean,
    required: Boolean,
    hideLabel: Boolean,
    enabled: Boolean,
    help: String?,
    control: @Composable () -> Unit,
) {
    if (hideLabel) {
        val name = accessibleLabel(label, required, optional)
        Box(
            Modifier.heightIn(min = QeetrixAccessibility.MinimumTouchTarget).semantics { contentDescription = name },
            contentAlignment = Alignment.CenterStart,
        ) { control() }
        return
    }
    Column(verticalArrangement = Arrangement.spacedBy(FieldPartGap)) {
        FieldLabelRow(label, optional, required, enabled, help)
        control()
    }
}

/** A field box's colours in one state (input.css). */
@Immutable
internal class FieldColors(
    val fill: Color,
    /** The 1 dp bottom line, or none while the frame replaces it. */
    val line: Color?,
    /** The 2 dp frame drawn inside the box, or none. */
    val frame: Color?,
    val content: Color,
)

/**
 * input.css's states, applied in its cascade order so each later rule wins as it does there: hover
 * (`bg.neutral.subtle-hover`); invalid (a 2 dp `border.danger` frame instead of the line); focus — any focus, since
 * the caret needs context — (a 2 dp `border.focus` frame instead of the line, and the resting fill); read-only (no
 * fill and the editable field's `border.strong` line, which keeps 3:1: QTRX-175); disabled (`bg.disabled`, a `border.disabled` line, `text.disabled`).
 */
@Composable
internal fun fieldColors(
    enabled: Boolean,
    readOnly: Boolean,
    focused: Boolean,
    hovered: Boolean,
    invalid: Boolean,
): FieldColors {
    val c = QeetrixTheme.colors
    // .qx-input: fill bg.neutral.subtle; box-shadow inset 0 -1px border.strong. :hover → bg.neutral.subtle-hover.
    var fill = if (hovered) c.bgNeutralSubtleHover else c.bgNeutralSubtle
    var line: Color? = c.borderStrong
    var frame: Color? = null
    if (invalid) {
        frame = c.borderDanger
        line = null
    }
    if (focused) {
        frame = c.borderFocus
        line = null
        fill = c.bgNeutralSubtle
    }
    if (readOnly) {
        // .qx-input:has(> .qx-input-control[readonly]): box-shadow inset 0 -1px border.strong; background: none
        fill = Color.Transparent
        line = c.borderStrong
    }
    if (!enabled) {
        fill = c.bgDisabled
        line = c.borderDisabled
    }
    return FieldColors(fill, line, frame, if (enabled) c.textDefault else c.textDisabled)
}

/**
 * The filled box every field shares (`.qx-input`, design rules §8): `color.bg.neutral.subtle`, a 1 dp
 * `color.border.strong` line along the bottom, 4 dp top corners and a square bottom (`QeetrixShapes.Field`),
 * `size.control.lg` tall — a minimum, so larger text grows it — and 12 dp inline padding. Its states are
 * [fieldColors]; the fill changes in `motion.duration.fast`.
 */
@Composable
internal fun FieldBox(
    enabled: Boolean,
    readOnly: Boolean,
    focused: Boolean,
    hovered: Boolean,
    invalid: Boolean,
    modifier: Modifier = Modifier,
    minHeight: Dp = QeetrixTheme.density.sizeControlLg,
    contentPadding: PaddingValues = FieldPadding,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.spacedBy(FieldContentGap),
    verticalAlignment: Alignment.Vertical = Alignment.CenterVertically,
    content: @Composable RowScope.() -> Unit,
) {
    val colors = fieldColors(enabled, readOnly, focused, hovered, invalid)
    // transition-property: background-color; duration.fast; easing.standard
    val fill by animateColorAsState(
        colors.fill,
        qeetrixAnimationSpec(QeetrixMotion.EasingStandard, QeetrixMotion.DurationFast),
        label = "field-fill",
    )
    Row(
        modifier
            .fieldSurface(fill, colors.line, colors.frame, QeetrixShapes.Field)
            .heightIn(min = minHeight)
            .padding(contentPadding),
        horizontalArrangement = horizontalArrangement,
        verticalAlignment = verticalAlignment,
    ) {
        CompositionLocalProvider(LocalQeetrixContentColor provides colors.content) { content() }
    }
}

/**
 * Draws a field's surface: the [fill] in [shape], the 1 dp [line] along the bottom (`box-shadow: inset 0 -1px`), and
 * over the content the 2 dp [frame] just inside the edge (`outline-offset: -focus.ring.width`), which follows the
 * shape's top corners as a CSS outline follows `border-radius`.
 */
internal fun Modifier.fieldSurface(
    fill: Color,
    line: Color?,
    frame: Color?,
    shape: Shape,
): Modifier =
    focusFrame(frame != null, shape, FocusFrame.Inside, frame ?: Color.Transparent, Color.Transparent, width = QeetrixFocus.RingWidth)
        .drawWithContent {
            drawOutline(shape.createOutline(size, layoutDirection, this), fill)
            if (line != null) {
                val width = QeetrixBorder.WidthDefault.toPx()
                drawRect(line, Offset(0f, size.height - width), Size(size.width, width))
            }
            drawContent()
        }

/**
 * A tool inside a field — show a password, clear a search, open a field's help: `@qeetrix/react`'s
 * `IconButton size="sm" tone="neutral"` (a 28 dp ghost square at the default density, a 16 dp sharp icon, the
 * button's colour steps and its focus frame inside with a 1 dp gap), drawn without the 48 dp layout a standalone
 * button reserves, so the field keeps its height. Compose's hit testing still sends a touch within 48 dp of it to
 * it. With [toggled] it is a pressed-state toggle (react's `aria-pressed`) whose [stateDescription] says what it
 * changed.
 */
@Composable
internal fun FieldAction(
    icon: QeetrixIconName,
    contentDescription: String,
    enabled: Boolean,
    modifier: Modifier = Modifier,
    toggled: Boolean? = null,
    stateDescription: String? = null,
    onClick: () -> Unit,
) {
    val source = remember { MutableInteractionSource() }
    val hovered by source.collectIsHoveredAsState()
    val pressed by source.collectIsPressedAsState()
    val metrics = buttonMetrics(QeetrixButtonSize.Sm)
    val colors = buttonColors(QeetrixButtonVariant.Ghost, QeetrixButtonTone.Neutral, enabled, hovered && enabled, pressed && enabled)
    val motion = qeetrixAnimationSpec<Color>(QeetrixMotion.EasingStandard, QeetrixMotion.DurationFast)
    val background by animateColorAsState(colors.background, motion, label = "field-action-background")
    val tint by animateColorAsState(colors.content, motion, label = "field-action-content")
    val shape = QeetrixShapes.Control
    val interaction =
        if (toggled != null) {
            Modifier.toggleable(toggled, source, indication = null, enabled = enabled, role = Role.Button) { onClick() }
        } else {
            Modifier.clickable(source, indication = null, enabled = enabled, role = Role.Button, onClick = onClick)
        }
    Box(
        modifier
            .then(interaction)
            .semantics {
                this.contentDescription = contentDescription
                if (stateDescription != null) this.stateDescription = stateDescription
            }.qeetrixFocusFrame(source, shape, FocusFrame.InsideWithGap)
            .background(background, shape)
            .size(metrics.height), // .qx-icon-button: size.control.sm square
        contentAlignment = Alignment.Center,
    ) {
        QeetrixIcon(icon, contentDescription = null, size = metrics.iconSize, shape = QeetrixIconShape.Sharp, tint = tint)
    }
}

/**
 * A field's help (react's Field `help`): an ⓘ [FieldAction] named "About {label}" (`qx_field_help`) that opens a
 * popover titled with the label — a click, not a hover, so it works by touch and keyboard. The button keeps its own
 * size but takes no more height than the label's line (`.qx-field-help`'s negative block margins), so a field with
 * help lines up with its neighbours.
 */
@Composable
private fun FieldHelp(
    label: String,
    help: String,
) {
    var open by remember { mutableStateOf(false) }
    val line = firstLineHeight(QeetrixTheme.typography.labelSm)
    Box(
        Modifier.layout { measurable, constraints ->
            // margin-block: calc((1lh - size.control.sm) / 2)
            val placeable = measurable.measure(constraints.copy(minHeight = 0))
            val height = line.roundToPx()
            layout(placeable.width, height) { placeable.place(0, (height - placeable.height) / 2) }
        },
    ) {
        FieldAction(QeetrixIconName.CircleQuestionMark, stringResource(R.string.qx_field_help, label), enabled = true) { open = !open }
        HelpPopover(open, label, help) { open = false }
    }
}

/**
 * popover.css: `color.surface.overlay`, a 1 dp `color.border.subtle` edge, `radius.overlay`, `shadow.overlay`,
 * `space.inset.lg` padding, at most 360 dp wide; the title in `title.sm`, the help in `body.md`, `space.gap.md`
 * apart. It opens below the button, `sideOffset` 6 dp away (the web's `side="inline-end"` has no room on a
 * phone), fades and slides in like every anchored popup, and closes on a tap outside, Back or Escape.
 */
@Composable
private fun HelpPopover(
    open: Boolean,
    title: String,
    help: String,
    onDismiss: () -> Unit,
) {
    val visibility = remember { MutableTransitionState(false) }
    visibility.targetState = open
    if (!visibility.currentState && !visibility.targetState) return
    val colors = QeetrixTheme.colors
    val typography = QeetrixTheme.typography
    val density = LocalDensity.current
    val gap = with(density) { HELP_OFFSET.roundToPx() }
    val margin = with(density) { QeetrixSpace.InsetSm.roundToPx() }
    Popup(
        popupPositionProvider = remember(gap, margin) { AnchoredPopupPosition(gap, margin, PopupAlign.Start) },
        onDismissRequest = onDismiss,
        properties = PopupProperties(focusable = true),
    ) {
        AnimatedVisibility(visibleState = visibility, enter = popupEnter(fromBottom = false), exit = popupExit(fromBottom = false)) {
            Column(
                Modifier
                    .widthIn(max = HELP_MAX_WIDTH)
                    .qeetrixShadow(QeetrixShadow.Overlay, QeetrixShapes.Overlay, colors.surfaceOverlay)
                    .border(QeetrixBorder.WidthDefault, colors.borderSubtle, QeetrixShapes.Overlay)
                    .padding(QeetrixSpace.InsetLg)
                    .semantics { paneTitle = title },
                verticalArrangement = Arrangement.spacedBy(QeetrixSpace.GapMd),
            ) {
                BasicText(
                    title,
                    Modifier.semantics {
                        heading()
                    },
                    style = typography.titleSm.copy(color = colors.textDefault).labelDirection(alignToLayout = true),
                )
                BasicText(help, style = typography.bodyMd.copy(color = colors.textDefault).labelDirection(alignToLayout = true))
            }
        }
    }
}

/** popover.tsx `sideOffset = 6`. */
private val HELP_OFFSET = 6.dp

/** `.qx-popover { max-inline-size: min(360px, …) }`. */
private val HELP_MAX_WIDTH = 360.dp

/**
 * The placeholder (`::placeholder`): `color.text.subtlest`, an example, never the label. A single-line field's ends in
 * an ellipsis where it doesn't fit, as at 200 % text, so it is never cut silently (QTRX-197); a placeholder is a short
 * example (design rules §8), and the field doesn't grow for it.
 */
@Composable
internal fun FieldPlaceholder(
    text: String,
    textStyle: TextStyle,
    maxLines: Int = 1,
) {
    BasicText(
        text,
        style = textStyle.copy(color = QeetrixTheme.colors.textSubtlest).labelDirection(alignToLayout = true),
        maxLines = maxLines,
        overflow = if (maxLines == Int.MAX_VALUE) TextOverflow.Clip else TextOverflow.Ellipsis,
    )
}

/** A leading icon (`.qx-input-icon`): sharp, 16 dp, decorative, `color.icon.subtle`. */
@Composable
internal fun FieldIcon(icon: QeetrixIconName) {
    QeetrixIcon(
        icon,
        contentDescription = null,
        size = QeetrixSize.IconSm,
        shape = QeetrixIconShape.Sharp,
        tint = QeetrixTheme.colors.iconSubtle,
    )
}

/** An adornment (`.qx-input-adornment`): an affix such as ₹ or a tool, in `color.text.subtle`. */
@Composable
internal fun FieldAdornment(content: @Composable () -> Unit) {
    CompositionLocalProvider(LocalQeetrixContentColor provides QeetrixTheme.colors.textSubtle) {
        Box(contentAlignment = Alignment.Center) { content() }
    }
}

/**
 * What a [QeetrixFieldSet] or a choice group gives the controls inside it: whether it is enabled, and — for a
 * group's options — the group's error, which each option carries for accessibility services, and whether the group
 * is invalid, which colours each option's border.
 */
@Immutable
internal class FieldGroup(
    val enabled: Boolean,
    val invalid: Boolean = false,
    val error: String? = null,
)

internal val LocalFieldGroup = staticCompositionLocalOf { FieldGroup(enabled = true) }

/** The label as accessibility services read it: "Work email, required", or "Nickname (optional)". */
@Composable
internal fun accessibleLabel(
    label: String,
    required: Boolean,
    optional: Boolean,
): String =
    when {
        required -> "$label, ${stringResource(R.string.qx_required)}"
        optional -> "$label ${stringResource(R.string.qx_optional)}"
        else -> label
    }

/**
 * The control's own AutoFill type. Applied outside the text field's own semantics, so it replaces the type
 * Compose derives from the keyboard; `null` leaves that one.
 */
internal fun Modifier.fieldContentType(contentType: ContentType?): Modifier =
    if (contentType != null) semantics { this.contentType = contentType } else this

/** An error marks the control invalid for accessibility services (`aria-invalid`, described by the error). */
internal fun Modifier.fieldError(error: String?): Modifier = if (error != null) semantics { error(error) } else this
