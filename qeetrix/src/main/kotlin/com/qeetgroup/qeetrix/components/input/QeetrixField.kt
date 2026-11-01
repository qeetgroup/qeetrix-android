package com.qeetgroup.qeetrix.components.input

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
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
import androidx.compose.ui.semantics.SemanticsPropertyReceiver
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.error
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.isTraversalGroup
import androidx.compose.ui.semantics.semantics
import com.qeetgroup.qeetrix.foundation.spacing.QeetrixSpace
import com.qeetgroup.qeetrix.internal.labelDirection
import com.qeetgroup.qeetrix.theme.QeetrixTheme

/**
 * The label, help text and error for a control Qeetrix does not provide — `@qeetrix/react`'s Field. The [label]
 * sits 4 dp above the control in `label.sm` / `color.text.subtle`; the [description] or the [error] sits 4 dp
 * below it in `body.sm`. Qeetrix's own inputs already carry a field: give [QeetrixTextField], [QeetrixTextarea]
 * and the others their label, description and error directly, and they are read as one control.
 *
 * - **Errors replace the help text.** While [error] shows — with a sharp alert icon, in `color.text.danger` — the
 *   [description] is not shown, on screen or to accessibility services. Word it as what to do ("Enter a work
 *   email"). [invalid] marks the field invalid without a message (a failed code, say); the description stays.
 * - **Optional, never required.** [optional] appends "(optional)" (`qx_optional`); fields carry no asterisk. A
 *   [required] field says "required" to accessibility services only (`qx_required`).
 * - [hideLabel] hides the label visually and keeps it for accessibility services — only for a toolbar search with
 *   a visible icon. [help] adds an ⓘ button after the label that opens a longer explanation in a popover.
 * - [enabled] `false` dims the label to `color.text.disabled` and the description to `color.text.subtlest` (it
 *   often says why). Disable the control yourself; Qeetrix controls inside a disabled [QeetrixFieldSet] already are.
 *
 * For accessibility, put [QeetrixFieldScope.qeetrixFieldControl] on the element accessibility services focus. The
 * control is then named by the label and carries the error, and the visible label is not read a second time.
 * Without the modifier, the label is read as text just before the control.
 *
 * ```
 * QeetrixField(label = "Brand colour", description = "Used on receipts.") {
 *     ColourSwatches(selected, onSelect, Modifier.qeetrixFieldControl())
 * }
 * ```
 */
@Composable
public fun QeetrixField(
    label: String,
    modifier: Modifier = Modifier,
    description: String? = null,
    error: String? = null,
    invalid: Boolean = false,
    optional: Boolean = false,
    required: Boolean = false,
    hideLabel: Boolean = false,
    help: String? = null,
    enabled: Boolean = true,
    content: @Composable QeetrixFieldScope.() -> Unit,
) {
    val active = enabled && LocalFieldGroup.current.enabled
    val scope = remember { FieldScopeImpl() }
    scope.name = accessibleLabel(label, required, optional)
    scope.error = error
    val group = LocalFieldGroup.current
    FieldFrame(description, error, active, modifier) {
        if (!hideLabel) {
            // Read once: as the control's name when the control takes it, else as text before the control. Only the
            // label's text is hidden: the help button stays a control of its own (quality finding Q-7).
            FieldLabelRow(
                label,
                optional,
                required,
                active,
                help,
                labelModifier = if (scope.controls > 0) Modifier.clearAndSetSemantics {} else Modifier,
            )
        }
        // A checkbox or switch inside the field is one of its items: invalid with it, carrying its error.
        CompositionLocalProvider(
            LocalFieldGroup provides FieldGroup(active, invalid || error != null || group.invalid, error ?: group.error),
        ) {
            scope.content()
        }
    }
}

/** What a [QeetrixField]'s content can use. */
@Stable
public sealed interface QeetrixFieldScope {
    /**
     * Gives this control the field's accessibility: it is named by the field's label and marked with the field's
     * error, and the visible label is no longer read separately. Apply it to the element accessibility services
     * focus — not to a text field, which Qeetrix's inputs already cover.
     */
    public fun Modifier.qeetrixFieldControl(): Modifier
}

private class FieldScopeImpl : QeetrixFieldScope {
    var name by mutableStateOf("")
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
        contentDescription = scope.name
        scope.error?.let { error(it) }
    }
}

/**
 * A section of fields under a heading — `@qeetrix/react`'s Fieldset, such as "Billing address": the [legend] in
 * `title.sm` / `color.text.default`, an optional [description] under it in `body.sm` / `color.text.subtle`, then
 * the fields, all `space.form.gap` apart (12 / 16 / 20 dp with the density).
 *
 * The legend is a heading and the section is one group for accessibility services, so its description is read on
 * entering it. [enabled] `false` disables every Qeetrix control inside and dims the legend to
 * `color.text.disabled`. A choice question is not a field set: use [com.qeetgroup.qeetrix.components.radiogroup.QeetrixRadioGroup]
 * or [com.qeetgroup.qeetrix.components.checkbox.QeetrixCheckboxGroup], whose question looks like every field's label.
 *
 * ```
 * QeetrixFieldSet("Billing address", description = "Where we send invoices.") {
 *     QeetrixTextField(line1, label = "Address line 1")
 *     QeetrixTextField(city, label = "City")
 * }
 * ```
 */
@Composable
public fun QeetrixFieldSet(
    legend: String,
    modifier: Modifier = Modifier,
    description: String? = null,
    enabled: Boolean = true,
    content: @Composable () -> Unit,
) {
    val active = enabled && LocalFieldGroup.current.enabled
    val colors = QeetrixTheme.colors
    val typography = QeetrixTheme.typography
    // .qx-fieldset { gap: space.form-gap }
    Column(
        modifier.semantics { isTraversalGroup = true },
        verticalArrangement = Arrangement.spacedBy(QeetrixTheme.density.spaceFormGap),
    ) {
        // .qx-fieldset-header { gap: space.gap.xs }
        Column(verticalArrangement = Arrangement.spacedBy(QeetrixSpace.GapXs)) {
            BasicText(
                legend,
                Modifier.semantics { heading() },
                style =
                    typography.titleSm
                        .copy(
                            color = if (active) colors.textDefault else colors.textDisabled,
                        ).labelDirection(alignToLayout = true),
            )
            if (description !=
                null
            ) {
                BasicText(description, style = typography.bodySm.copy(color = colors.textSubtle).labelDirection(alignToLayout = true))
            }
        }
        CompositionLocalProvider(LocalFieldGroup provides FieldGroup(active)) { content() }
    }
}
