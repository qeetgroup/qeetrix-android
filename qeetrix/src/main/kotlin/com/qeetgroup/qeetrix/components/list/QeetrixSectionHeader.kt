package com.qeetgroup.qeetrix.components.list

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import com.qeetgroup.qeetrix.internal.FontWeightSemibold
import com.qeetgroup.qeetrix.internal.QeetrixFont
import com.qeetgroup.qeetrix.internal.QeetrixWebScale
import com.qeetgroup.qeetrix.internal.qeetrixTextStyle
import com.qeetgroup.qeetrix.theme.QeetrixTheme

/**
 * The heading of a group of rows: a [title], an optional [description] and an optional [action] — above a
 * section of a lazy list, or above a group of [QeetrixListItem]s anywhere else.
 *
 * ```
 * LazyColumn {
 *     for ((day, events) in eventsByDay) {
 *         stickyHeader(key = "header-$day") {
 *             QeetrixSectionHeader(day.label, Modifier.background(QeetrixTheme.colors.surfaceCanvas))
 *         }
 *         items(events, key = { it.id }) { event -> QeetrixListItem(event.title) }
 *     }
 * }
 * ```
 *
 * Sections stay Compose's: a `LazyColumn` with `item`/`stickyHeader` owns the grouping, the scrolling, the
 * sticky behaviour and the collection. This header gives the group Qeetrix's type, the rows' 16 dp inset
 * and an action slot. A sticky header draws over the rows scrolling under it, so give it the surface it
 * sits on through [modifier], as above.
 *
 * The title is a heading for accessibility services, so TalkBack's heading navigation moves between
 * groups; the description is read with it, and the action is its own control — typically a link-style
 * [com.qeetgroup.qeetrix.components.button.QeetrixButton].
 */
@Composable
public fun QeetrixSectionHeader(
    title: String,
    modifier: Modifier = Modifier,
    description: String? = null,
    action: (@Composable () -> Unit)? = null,
) {
    Row(
        modifier
            .fillMaxWidth()
            // The rows' inline padding; a group's space above, closer to its own rows below.
            .padding(
                start = HorizontalPadding,
                end = HorizontalPadding,
                top = QeetrixWebScale.spacing(4f),
                bottom = QeetrixWebScale.spacing(2f),
            ),
        horizontalArrangement = Arrangement.spacedBy(QeetrixWebScale.spacing(3f)), // gap-3
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(
            Modifier.weight(1f).semantics(mergeDescendants = true) { heading() },
            verticalArrangement = Arrangement.spacedBy(QeetrixWebScale.spacing(0.5f)), // gap-0.5
        ) {
            BasicText(title, Modifier.fillMaxWidth(), style = TitleStyle.copy(color = QeetrixTheme.colors.textPrimary))
            if (description != null) {
                BasicText(description, Modifier.fillMaxWidth(), style = DescriptionStyle.copy(color = QeetrixTheme.colors.textTertiary))
            }
        }
        action?.invoke()
    }
}

// font-heading text-sm font-semibold: a group's title, quieter than a page heading.
private val TitleStyle = qeetrixTextStyle(QeetrixFont.Heading, QeetrixWebScale.TextSm, FontWeightSemibold)
