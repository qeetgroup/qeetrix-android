package com.qeetgroup.qeetrix.components.input

import androidx.compose.runtime.Immutable

/**
 * One choice in a [com.qeetgroup.qeetrix.components.radiogroup.QeetrixRadioGroup] or a
 * [com.qeetgroup.qeetrix.components.select.QeetrixSelect]: the [value] it selects, the [label] people read,
 * an optional [description], and whether it can be chosen.
 *
 * The value is the option's identity: options in one list must have different values, and the selection
 * is a value, so it can only ever be one of them. "Nothing selected" is a value no option has — usually
 * `null` with a nullable type:
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
) {
    override fun equals(other: Any?): Boolean =
        other is QeetrixOption<*> &&
            value == other.value &&
            label == other.label &&
            description == other.description &&
            enabled == other.enabled

    override fun hashCode(): Int {
        var result = value?.hashCode() ?: 0
        result = 31 * result + label.hashCode()
        result = 31 * result + (description?.hashCode() ?: 0)
        return 31 * result + enabled.hashCode()
    }

    override fun toString(): String = "QeetrixOption($value, $label)"
}

/** Fails fast on options that share a value: the selection could not tell them apart. */
internal fun <T> requireDistinctValues(options: List<QeetrixOption<T>>) {
    require(options.map { it.value }.toSet().size == options.size) { "Each option needs a value of its own; two options share one." }
}
