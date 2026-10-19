package com.qeetgroup.qeetrix.components.input

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.node.ModifierNodeElement
import androidx.compose.ui.node.SemanticsModifierNode
import androidx.compose.ui.platform.InspectorInfo
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.SemanticsPropertyReceiver
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.error
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.isTraversalGroup
import androidx.compose.ui.semantics.semantics
import com.qeetgroup.qeetrix.R
import com.qeetgroup.qeetrix.internal.FontWeightMedium
import com.qeetgroup.qeetrix.internal.QeetrixFont
import com.qeetgroup.qeetrix.internal.QeetrixWebScale
import com.qeetgroup.qeetrix.internal.qeetrixTextStyle
import com.qeetgroup.qeetrix.theme.QeetrixTheme

/** The tone of a [QeetrixFieldMessage] (`value` is the web name). */
public enum class QeetrixFieldMessageTone(
    public val value: String,
) {
    /** What is wrong (`FieldError`). Marks the field invalid and is announced at once. */
    Error("error"),

    /** A caution that does not block (`FieldWarning`). Announced politely. */
    Warning("warning"),

    /** Confirmation that a value is good (`FieldSuccess`). Announced politely. */
    Success("success"),
}

/** A message under a field: what is wrong, a caution, or a confirmation. Your app decides it; Qeetrix shows it. */
@Immutable
public class QeetrixFieldMessage(
    public val text: String,
    public val tone: QeetrixFieldMessageTone = QeetrixFieldMessageTone.Error,
) {
    override fun equals(other: Any?): Boolean = other is QeetrixFieldMessage && text == other.text && tone == other.tone

    override fun hashCode(): Int = 31 * text.hashCode() + tone.hashCode()

    override fun toString(): String = "QeetrixFieldMessage(${tone.value}: $text)"
}

/**
 * A field around a control Qeetrix does not provide: its [label] above, then the control, a
 * [description] and a [message], 6 dp apart — the web's vertical `Field`, with the same parts every
 * Qeetrix input is built from.
 *
 * Qeetrix's own inputs already carry a field: give [QeetrixTextField], [QeetrixTextarea] and the others
 * their label, description and message directly, and they are read as one control.
 *
 * For any other control, put [QeetrixFieldScope.qeetrixFieldControl] on the element accessibility services
 * focus. The control is then named by the label (with "required" when [required]) and carries the
 * error, and the visible label is hidden from accessibility services so it is not read twice. A control
 * that shows a value should expose it as its state description. Without the modifier, the label is read
 * as text just before the control. Business validation is your app's: pass the result as [message].
 *
 * ```
 * QeetrixField(label = "Brand colour", description = "Used on receipts.") {
 *     ColourSwatches(selected, onSelect, Modifier.qeetrixFieldControl())
 * }
 * ```
 */
@Composable
public fun QeetrixField(
    label: String?,
    modifier: Modifier = Modifier,
    description: String? = null,
    message: QeetrixFieldMessage? = null,
    required: Boolean = false,
    optional: Boolean = false,
    enabled: Boolean = true,
    content: @Composable QeetrixFieldScope.() -> Unit,
) {
    val active = enabled && LocalFieldGroup.current.enabled
    val scope = remember { FieldScopeImpl() }
    scope.name = label?.let { accessibleLabel(it, required, optional) }
    scope.error = message?.takeIf { it.tone == QeetrixFieldMessageTone.Error }?.text
    FieldFrame(description, message, modifier) {
        if (label != null) {
            // Read once: as the control's name when the control takes it, else as text before the control.
            FieldLabel(label, required, optional, active, if (scope.controls > 0) Modifier.clearAndSetSemantics {} else Modifier)
        }
        scope.content()
    }
}

/** What a [QeetrixField]'s content can use. */
@Stable
public sealed interface QeetrixFieldScope {
    /**
     * Gives this control the field's accessibility: it is named by the field's label and marked with the
     * field's error, and the visible label is no longer read separately. Apply it to the element
     * accessibility services focus — not to a text field, which Qeetrix's inputs already cover.
     */
    public fun Modifier.qeetrixFieldControl(): Modifier
}

private class FieldScopeImpl : QeetrixFieldScope {
    var name by mutableStateOf<String?>(null)
    var error by mutableStateOf<String?>(null)
    var controls by mutableIntStateOf(0)

    override fun Modifier.qeetrixFieldControl(): Modifier = this then FieldControlElement(this@FieldScopeImpl)
}

private class FieldControlElement(
    private val scope: FieldScopeImpl,
) : ModifierNodeElement<FieldControlNode>() {
    override fun create() = FieldControlNode(scope)

    override fun update(node: FieldControlNode) {
        node.scope = scope
    }

    override fun hashCode(): Int = scope.hashCode()

    override fun equals(other: Any?): Boolean = other is FieldControlElement && other.scope === scope

    override fun InspectorInfo.inspectableProperties() {
        name = "qeetrixFieldControl"
    }
}

private class FieldControlNode(
    var scope: FieldScopeImpl,
) : Modifier.Node(),
    SemanticsModifierNode {
    override fun onAttach() {
        scope.controls++
    }

    override fun onDetach() {
        scope.controls--
    }

    override fun SemanticsPropertyReceiver.applySemantics() {
        scope.name?.let { contentDescription = it }
        scope.error?.let { error(it) }
    }
}

/** The label as accessibility services read it: "Work email, required" or "Nickname (optional)". */
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

/** How a [QeetrixFieldSet]'s legend is drawn (`value` is the web's `FieldLegend` variant). */
public enum class QeetrixFieldSetLegend(
    public val value: String,
) {
    /** A heading over a group of fields: `font-heading text-base font-medium`. */
    Legend("legend"),

    /** A field label over a group of choices, as a radio group uses: `text-sm font-medium`. */
    Label("label"),
}

/**
 * A group of related fields or choices under a [legend] — the web's `FieldSet` with a `FieldLegend` and a
 * `FieldGroup`.
 *
 * The legend is a heading and the set is one group for accessibility services. Fields inside are spaced
 * by the density's field gap; checkbox, radio and switch rows sit flush, each already a 48 dp touch
 * target. A [message] here is about the group as a whole ("Choose at least one"): it is shown after the
 * group and each checkbox in it carries it, so the error is heard on whichever one is focused; fields in
 * the set keep their own messages. [enabled] `false` disables every Qeetrix control inside.
 *
 * ```
 * QeetrixFieldSet("Notifications", description = "Choose how we reach you.") {
 *     QeetrixCheckbox(email, { email = it }, label = "Email")
 *     QeetrixCheckbox(push, { push = it }, label = "Push")
 * }
 * ```
 */
@Composable
public fun QeetrixFieldSet(
    legend: String?,
    modifier: Modifier = Modifier,
    legendStyle: QeetrixFieldSetLegend = QeetrixFieldSetLegend.Legend,
    description: String? = null,
    message: QeetrixFieldMessage? = null,
    required: Boolean = false,
    optional: Boolean = false,
    enabled: Boolean = true,
    content: @Composable () -> Unit,
) {
    val parent = LocalFieldGroup.current
    val active = enabled && parent.enabled
    // A fieldset spaces its parts by the field gap; a radio group keeps the field's 6 dp rhythm.
    val spacing = if (legendStyle == QeetrixFieldSetLegend.Legend) QeetrixTheme.density.fieldGap else FieldPartGap
    Column(
        modifier.semantics { isTraversalGroup = true },
        verticalArrangement = Arrangement.spacedBy(spacing),
    ) {
        if (legend != null) {
            FieldLabel(
                legend,
                required,
                optional,
                active,
                Modifier.semantics(mergeDescendants = true) { heading() },
                style = if (legendStyle == QeetrixFieldSetLegend.Legend) LegendStyle else FieldLabelStyle,
            )
        }
        if (description != null) FieldDescription(description)
        CompositionLocalProvider(LocalFieldGroup provides FieldGroup(message, active)) {
            FieldStack(QeetrixTheme.density.fieldGap) { content() }
        }
        if (message != null) FieldMessage(message)
    }
}

/** FieldLegend variant="legend": font-heading text-base font-medium. */
private val LegendStyle = qeetrixTextStyle(QeetrixFont.Heading, QeetrixWebScale.TextBase, FontWeightMedium)
