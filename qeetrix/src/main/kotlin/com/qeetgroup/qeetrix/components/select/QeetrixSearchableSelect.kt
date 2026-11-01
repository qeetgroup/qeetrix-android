package com.qeetgroup.qeetrix.components.select

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.text.input.clearText
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.qeetgroup.qeetrix.R
import com.qeetgroup.qeetrix.components.button.QeetrixButton
import com.qeetgroup.qeetrix.components.drawer.QeetrixSideSheet
import com.qeetgroup.qeetrix.components.emptystate.QeetrixEmptyState
import com.qeetgroup.qeetrix.components.emptystate.QeetrixEmptyStateSize
import com.qeetgroup.qeetrix.components.input.QeetrixOption
import com.qeetgroup.qeetrix.components.input.QeetrixOptionGroup
import com.qeetgroup.qeetrix.components.input.QeetrixSearchField
import com.qeetgroup.qeetrix.foundation.spacing.QeetrixSpace
import com.qeetgroup.qeetrix.icons.QeetrixIconName

/**
 * Pick one option from a long list — a country, a currency, a time zone — where a [QeetrixSelect]'s popup would be too
 * long to scan: `@qeetrix/react`'s Combobox in the form a phone uses. The trigger is the select's; a tap opens a sheet
 * named by the [label] with a search field above the options.
 *
 * - **Search.** Typing filters the options to those whose label (or `meta`) contains what was typed, ignoring case;
 *   group labels stay over the options left in their group. When nothing matches, "No matches" and what was typed take
 *   the list's place, announced as they appear.
 * - **The sheet** is a [QeetrixBottomSheet][com.qeetgroup.qeetrix.components.drawer.QeetrixBottomSheet] on a phone and a
 *   side sheet from `breakpoint.medium` ([QeetrixSideSheet]). The chosen option carries the brand check; a choice
 *   closes the sheet, as do the close button, a swipe, a tap on the scrim, Back and Escape. The search starts empty
 *   each time it opens.
 * - **Everything else is [QeetrixSelect]'s**: typed values, the field, its states, and type-to-jump on the closed
 *   trigger.
 *
 * Use a [QeetrixSelect] for up to about 15 options, and [QeetrixSearchableMultiSelect] to pick several.
 *
 * ```
 * QeetrixSearchableSelect(country, { country = it }, Countries, label = "Country", placeholder = "e.g. India")
 * ```
 *
 * @param searchPlaceholder An example of what to type in the search field; its label is "Search", read with the
 *   sheet's title.
 */
@Composable
public fun <T> QeetrixSearchableSelect(
    value: T,
    onValueChange: (T) -> Unit,
    items: List<QeetrixOption<T>>,
    label: String,
    modifier: Modifier = Modifier,
    placeholder: String? = null,
    searchPlaceholder: String? = null,
    description: String? = null,
    error: String? = null,
    invalid: Boolean = false,
    optional: Boolean = false,
    required: Boolean = false,
    hideLabel: Boolean = false,
    help: String? = null,
    size: QeetrixSelectSize = QeetrixSelectSize.Lg,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    interactionSource: MutableInteractionSource? = null,
) {
    SelectField(
        chosen = { it == value },
        onChoose = { if (it != value) onValueChange(it) },
        multiple = false,
        sections = listOf(SelectSection(null, items)),
        label = label,
        modifier = modifier,
        placeholder = placeholder,
        description = description,
        error = error,
        invalid = invalid,
        optional = optional,
        required = required,
        hideLabel = hideLabel,
        help = help,
        size = size,
        enabled = enabled,
        readOnly = readOnly,
        open = null,
        onOpenChange = null,
        interactionSource = interactionSource,
        searchable = true,
        searchPlaceholder = searchPlaceholder,
    )
}

/** A [QeetrixSearchableSelect] whose options sit under group labels, as [QeetrixSelect]'s grouped overload. */
@Composable
@JvmName("QeetrixSearchableSelectGrouped") // the flat overload has the same erased signature
public fun <T> QeetrixSearchableSelect(
    value: T,
    onValueChange: (T) -> Unit,
    groups: List<QeetrixOptionGroup<T>>,
    label: String,
    modifier: Modifier = Modifier,
    placeholder: String? = null,
    searchPlaceholder: String? = null,
    description: String? = null,
    error: String? = null,
    invalid: Boolean = false,
    optional: Boolean = false,
    required: Boolean = false,
    hideLabel: Boolean = false,
    help: String? = null,
    size: QeetrixSelectSize = QeetrixSelectSize.Lg,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    interactionSource: MutableInteractionSource? = null,
) {
    SelectField(
        chosen = { it == value },
        onChoose = { if (it != value) onValueChange(it) },
        multiple = false,
        sections = groups.map { SelectSection(it.label, it.options) },
        label = label,
        modifier = modifier,
        placeholder = placeholder,
        description = description,
        error = error,
        invalid = invalid,
        optional = optional,
        required = required,
        hideLabel = hideLabel,
        help = help,
        size = size,
        enabled = enabled,
        readOnly = readOnly,
        open = null,
        onOpenChange = null,
        interactionSource = interactionSource,
        searchable = true,
        searchPlaceholder = searchPlaceholder,
    )
}

/**
 * Pick several options from a long list: [QeetrixSearchableSelect]'s sheet and search with [QeetrixMultiSelect]'s
 * choices — a set of values, check-box options, the trigger listing the chosen labels. The sheet stays open while
 * options are toggled, through searches, and closes with its Done button, the close button, Back or a tap outside.
 */
@Composable
public fun <T> QeetrixSearchableMultiSelect(
    values: Set<T>,
    onValuesChange: (Set<T>) -> Unit,
    items: List<QeetrixOption<T>>,
    label: String,
    modifier: Modifier = Modifier,
    placeholder: String? = null,
    searchPlaceholder: String? = null,
    description: String? = null,
    error: String? = null,
    invalid: Boolean = false,
    optional: Boolean = false,
    required: Boolean = false,
    hideLabel: Boolean = false,
    help: String? = null,
    size: QeetrixSelectSize = QeetrixSelectSize.Lg,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    interactionSource: MutableInteractionSource? = null,
) {
    SelectField(
        chosen = { it in values },
        onChoose = { onValuesChange(if (it in values) values - it else values + it) },
        multiple = true,
        sections = listOf(SelectSection(null, items)),
        label = label,
        modifier = modifier,
        placeholder = placeholder,
        description = description,
        error = error,
        invalid = invalid,
        optional = optional,
        required = required,
        hideLabel = hideLabel,
        help = help,
        size = size,
        enabled = enabled,
        readOnly = readOnly,
        open = null,
        onOpenChange = null,
        interactionSource = interactionSource,
        searchable = true,
        searchPlaceholder = searchPlaceholder,
    )
}

/** A [QeetrixSearchableMultiSelect] whose options sit under group labels. */
@Composable
@JvmName("QeetrixSearchableMultiSelectGrouped") // the flat overload has the same erased signature
public fun <T> QeetrixSearchableMultiSelect(
    values: Set<T>,
    onValuesChange: (Set<T>) -> Unit,
    groups: List<QeetrixOptionGroup<T>>,
    label: String,
    modifier: Modifier = Modifier,
    placeholder: String? = null,
    searchPlaceholder: String? = null,
    description: String? = null,
    error: String? = null,
    invalid: Boolean = false,
    optional: Boolean = false,
    required: Boolean = false,
    hideLabel: Boolean = false,
    help: String? = null,
    size: QeetrixSelectSize = QeetrixSelectSize.Lg,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    interactionSource: MutableInteractionSource? = null,
) {
    SelectField(
        chosen = { it in values },
        onChoose = { onValuesChange(if (it in values) values - it else values + it) },
        multiple = true,
        sections = groups.map { SelectSection(it.label, it.options) },
        label = label,
        modifier = modifier,
        placeholder = placeholder,
        description = description,
        error = error,
        invalid = invalid,
        optional = optional,
        required = required,
        hideLabel = hideLabel,
        help = help,
        size = size,
        enabled = enabled,
        readOnly = readOnly,
        open = null,
        onOpenChange = null,
        interactionSource = interactionSource,
        searchable = true,
        searchPlaceholder = searchPlaceholder,
    )
}

/** Whether [option] matches the search [query]: its label or meta contains it, ignoring case. An empty query matches all. */
internal fun QeetrixOption<*>.matches(query: String): Boolean =
    query.isEmpty() || label.contains(query, ignoreCase = true) || meta?.contains(query, ignoreCase = true) == true

/**
 * The searchable select's sheet: [title] (the field's label), a search field, then the [sections]' options that match
 * what was typed — or the empty state — and, for [multiple] choices, a Done button. The query starts empty each time
 * the sheet opens; the options area keeps a minimum height, so the sheet does not jump as it filters.
 */
@Composable
internal fun <T> SearchSheet(
    expanded: Boolean,
    sections: List<SelectSection<T>>,
    chosen: (T) -> Boolean,
    multiple: Boolean,
    title: String,
    searchPlaceholder: String?,
    onDismiss: () -> Unit,
    onChoose: (T) -> Unit,
) {
    val query = rememberTextFieldState()
    LaunchedEffect(expanded) { if (expanded) query.clearText() }
    QeetrixSideSheet(
        open = expanded,
        onOpenChange = { if (!it) onDismiss() },
        title = title,
        footer = if (multiple) ({ QeetrixButton(stringResource(R.string.qx_done), onClick = onDismiss) }) else null,
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(QeetrixSpace.GapMd)) {
            QeetrixSearchField(query, Modifier.fillMaxWidth(), placeholder = searchPlaceholder)
            SearchResults(query.text.toString().trim(), sections, chosen, multiple, onChoose)
        }
    }
}

/** The options matching [typed], under their group labels, or the empty state when none does. */
@Composable
internal fun <T> SearchResults(
    typed: String,
    sections: List<SelectSection<T>>,
    chosen: (T) -> Boolean,
    multiple: Boolean,
    onChoose: (T) -> Unit,
) {
    val shown =
        sections
            .map { section -> SelectSection(section.label, section.options.filter { it.matches(typed) }) }
            .filter { it.options.isNotEmpty() }
    Column(
        Modifier
            .fillMaxWidth()
            .heightIn(min = OPTIONS_MIN_HEIGHT)
            .then(if (multiple) Modifier else Modifier.selectableGroup()),
    ) {
        if (shown.isEmpty()) {
            QeetrixEmptyState(
                title = stringResource(R.string.qx_no_matches),
                modifier = Modifier.semantics { liveRegion = LiveRegionMode.Polite },
                icon = QeetrixIconName.SearchX,
                description = stringResource(R.string.qx_no_matches_description, typed),
                size = QeetrixEmptyStateSize.Section,
            )
        } else {
            for (section in shown) {
                if (section.label != null) GroupLabel(section.label)
                for (option in section.options) {
                    key(option.value) {
                        OptionRow(
                            option = option,
                            selected = chosen(option.value),
                            takeFocus = false,
                            multiple = multiple,
                            requester = remember { FocusRequester() },
                            onFocused = {},
                        ) { onChoose(option.value) }
                    }
                }
            }
        }
    }
}

/** The options area's least height under the search field, so a short result does not shrink the sheet. */
private val OPTIONS_MIN_HEIGHT = 320.dp
