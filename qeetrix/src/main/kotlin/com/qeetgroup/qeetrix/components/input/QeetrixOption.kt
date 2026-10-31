package com.qeetgroup.qeetrix.components.input

import androidx.compose.runtime.Immutable
import com.qeetgroup.qeetrix.icons.QeetrixIconName

/**
 * One choice in a [com.qeetgroup.qeetrix.components.radiogroup.QeetrixRadioGroup], a
 * [com.qeetgroup.qeetrix.components.checkbox.QeetrixCheckboxGroup], a
 * [com.qeetgroup.qeetrix.components.select.QeetrixSelect] or a chip group: `@qeetrix/react`'s Radio and Checkbox
 * props inside a group, and its Select `items`.
 *
 * - [value] selects it, [label] is what people read; [enabled] `false` is react's `disabled`.
 * - [description] — a line under the label of a radio or checkbox (a tile's description). A select's options and
 *   chips show labels only.
 * - [icon] — a radio's or checkbox's icon: 16 dp before the label in a row, 20 dp in a 36 dp box on a tile.
 * - [meta] — a select option's short fact at its end ("19"), read with the option but not shown in the closed
 *   trigger.
 *
 * The value is the option's identity: options in one list must have different values, and the selection is a
 * value, so it can only ever be one of them. "Nothing selected" is a value no option has — usually `null` with a
 * nullable type:
 *
 * ```
 * var plan by rememberSaveable { mutableStateOf<Plan?>(null) }
 * QeetrixSelect(plan, { plan = it }, Plan.entries.map { QeetrixOption(it, it.title) }, label = "Plan")
 * ```
 */
@Immutable
public class QeetrixOption<out T>(
    public val value: T,
    public val label: String,
    public val description: String? = null,
    public val enabled: Boolean = true,
    public val icon: QeetrixIconName? = null,
    public val meta: String? = null,
) {
    override fun equals(other: Any?): Boolean =
        other is QeetrixOption<*> &&
            value == other.value &&
            label == other.label &&
            description == other.description &&
            enabled == other.enabled &&
            icon == other.icon &&
            meta == other.meta

    override fun hashCode(): Int {
        var result = value?.hashCode() ?: 0
        result = 31 * result + label.hashCode()
        result = 31 * result + (description?.hashCode() ?: 0)
        result = 31 * result + enabled.hashCode()
        result = 31 * result + (icon?.hashCode() ?: 0)
        return 31 * result + (meta?.hashCode() ?: 0)
    }

    override fun toString(): String = "QeetrixOption($value, $label)"
}

/** Options under a group label in a [com.qeetgroup.qeetrix.components.select.QeetrixSelect] — react's `SelectItemGroup`. */
@Immutable
public class QeetrixOptionGroup<out T>(
    public val label: String,
    public val options: List<QeetrixOption<T>>,
) {
    override fun equals(other: Any?): Boolean = other is QeetrixOptionGroup<*> && label == other.label && options == other.options

    override fun hashCode(): Int = 31 * label.hashCode() + options.hashCode()

    override fun toString(): String = "QeetrixOptionGroup($label, ${options.size} options)"
}

/** Fails fast on options that share a value: the selection could not tell them apart. */
internal fun <T> requireDistinctValues(options: List<QeetrixOption<T>>) {
    require(options.map { it.value }.toSet().size == options.size) { "Each option needs a value of its own; two options share one." }
}
