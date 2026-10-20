// The examples keep the documentation's compact layout, so they can be compared with it line by line.
@file:Suppress(
    "ktlint:standard:argument-list-wrapping",
    "ktlint:standard:function-naming",
    "ktlint:standard:max-line-length",
    "ktlint:standard:multiline-expression-wrapping",
    "ktlint:standard:trailing-comma-on-call-site",
    "ktlint:standard:wrapping",
)

package com.qeetgroup.qeetrix.sample.datadisplay

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.input.clearText
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.hasContentDescription
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.isHeading
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.unit.dp
import com.qeetgroup.qeetrix.components.accordion.QeetrixAccordion
import com.qeetgroup.qeetrix.components.accordion.QeetrixAccordionItem
import com.qeetgroup.qeetrix.components.badge.QeetrixBadge
import com.qeetgroup.qeetrix.components.badge.QeetrixBadgeVariant
import com.qeetgroup.qeetrix.components.badge.QeetrixChip
import com.qeetgroup.qeetrix.components.badge.QeetrixChipGroup
import com.qeetgroup.qeetrix.components.button.QeetrixButton
import com.qeetgroup.qeetrix.components.button.QeetrixButtonVariant
import com.qeetgroup.qeetrix.components.descriptionlist.QeetrixDescriptionItem
import com.qeetgroup.qeetrix.components.descriptionlist.QeetrixDescriptionList
import com.qeetgroup.qeetrix.components.emptystate.QeetrixDataFailure
import com.qeetgroup.qeetrix.components.emptystate.QeetrixDataState
import com.qeetgroup.qeetrix.components.emptystate.QeetrixEmptyState
import com.qeetgroup.qeetrix.components.emptystate.QeetrixEmptyStateSize
import com.qeetgroup.qeetrix.components.emptystate.QeetrixEmptyStateVariant
import com.qeetgroup.qeetrix.components.feed.QeetrixTimeline
import com.qeetgroup.qeetrix.components.feed.QeetrixTimelineItem
import com.qeetgroup.qeetrix.components.feed.QeetrixTimelineTone
import com.qeetgroup.qeetrix.components.input.QeetrixOption
import com.qeetgroup.qeetrix.components.input.QeetrixSearchField
import com.qeetgroup.qeetrix.components.list.QeetrixListItem
import com.qeetgroup.qeetrix.components.list.QeetrixListItemIcon
import com.qeetgroup.qeetrix.components.list.QeetrixSectionHeader
import com.qeetgroup.qeetrix.components.spinner.QeetrixSkeleton
import com.qeetgroup.qeetrix.components.spinner.QeetrixSkeletonShape
import com.qeetgroup.qeetrix.components.spinner.QeetrixSkeletonText
import com.qeetgroup.qeetrix.components.typography.QeetrixText
import com.qeetgroup.qeetrix.components.typography.QeetrixTextVariant
import com.qeetgroup.qeetrix.icons.QeetrixIconName
import com.qeetgroup.qeetrix.theme.QeetrixTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

/**
 * The Phase 5 examples in `README.md` and the component pages in `Documentation/Components`, as written there,
 * so they keep compiling against the public API and composing. Only the surrounding data and the app's own
 * functions are added. Values are synthetic.
 */
@RunWith(RobolectricTestRunner::class)
class DataDisplayDocumentationExamplesTest {
    @get:Rule
    val compose = createComposeRule()

    @Test
    fun everyDataDisplayExampleComposes() {
        compose.setContent {
            QeetrixTheme {
                Column(Modifier.verticalScroll(rememberScrollState())) {
                    Box(Modifier.height(200.dp)) { ListItemExample() }
                    Box(Modifier.height(200.dp)) { SectionHeaderExample() }
                    DescriptionListExample()
                    AccordionExample()
                    EmptyStateExample()
                    Box(Modifier.height(200.dp)) { DataStateExample() }
                    SkeletonExample()
                    ChipExample()
                    TimelineExample()
                    SearchFieldExample()
                    Box(Modifier.height(200.dp)) { ReadmeCollection() }
                }
            }
        }
        for (text in listOf(
            "Quarterly report.pdf", "Engineering", "Mumbai (ap-south-1)", "How am I billed?", "No invoices yet", "No results",
            "INV-2041", "Unread", "Remove Owner: Ada", "Payment received", "Search members", "Ada Lovelace",
        )) {
            val named = hasText(text, substring = true) or hasContentDescription(text, substring = true)
            assertTrue(text, compose.onAllNodes(named, useUnmergedTree = true).fetchSemanticsNodes().isNotEmpty())
        }
        assertTrue(compose.onAllNodes(isHeading() and hasText("Engineering")).fetchSemanticsNodes().isNotEmpty())
    }
}

private class SampleFile(
    val id: String,
    val name: String,
    val owner: String,
    val size: String,
)

private class Person(
    val id: String,
    val name: String,
    val email: String,
)

private class Invoice(
    val id: String,
    val number: String,
    val amount: String,
)

private class InvoicesState(
    val loading: Boolean,
    val failed: Boolean,
    val invoices: List<Invoice>,
)

private class Event(
    val id: String,
    val title: String,
    val time: String,
    val detail: String?,
    val failed: Boolean,
)

private enum class Section(
    val title: String,
    val summary: String,
) {
    Billing("How am I billed?", "Monthly, on the date you subscribed."),
    Seats("Can I add people later?", "Yes, at any time."),
}

private enum class Status { Open, Closed }

private val files = listOf(SampleFile("f1", "Quarterly report.pdf", "Shared by Ada Lovelace", "2.4 MB"))

private val membersByTeam =
    mapOf(
        "Design" to listOf(Person("m1", "Asha Rao", "asha@example.com")),
        "Engineering" to listOf(Person("m2", "Ada Lovelace", "ada@example.com")),
    )

private val state = InvoicesState(loading = false, failed = false, invoices = listOf(Invoice("i1", "INV-2041", "₹48,000")))

private val events =
    listOf(
        Event("e1", "Payment received", "Today", "By bank transfer.", failed = false),
        Event("e2", "Card payment", "5 days ago", "The bank declined it.", failed = true),
    )

private fun open(id: String) = check(id.isNotEmpty())

private fun createInvoice() = Unit

private fun reload() = Unit

private fun removeFilter(id: String) = check(id.isNotEmpty())

private fun search(text: String) = check(text.length >= 0)

// ListItem.md
@Composable
private fun ListItemExample() {
    LazyColumn {
        items(files, key = { it.id }) { file ->
            QeetrixListItem(
                title = file.name,
                description = file.owner,
                metadata = file.size,
                onClick = { open(file.id) },
                leading = { QeetrixListItemIcon(QeetrixIconName.FileText) },
            ) {
                QeetrixBadge("Shared", variant = QeetrixBadgeVariant.Secondary)
            }
        }
    }
}

// SectionHeader.md
@Composable
private fun SectionHeaderExample() {
    LazyColumn {
        for ((team, people) in membersByTeam) {
            stickyHeader(key = "team-$team") {
                QeetrixSectionHeader(team, Modifier.background(QeetrixTheme.colors.surfaceCanvas), description = "${people.size} people")
            }
            items(people, key = { it.id }) { person -> QeetrixListItem(person.name, description = person.email) }
        }
    }
}

// DescriptionList.md
@Composable
private fun DescriptionListExample() {
    QeetrixDescriptionList(divided = true) {
        QeetrixDescriptionItem("Plan", "Team")
        QeetrixDescriptionItem("Region", "Mumbai (ap-south-1)")
        QeetrixDescriptionItem("Status") { QeetrixBadge("Active", variant = QeetrixBadgeVariant.Success) }
    }
}

// Accordion.md
@Composable
private fun AccordionExample() {
    var open by rememberSaveable { mutableStateOf<Section?>(Section.Billing) }
    QeetrixAccordion {
        for (section in Section.entries) {
            QeetrixAccordionItem(
                title = section.title,
                expanded = open == section,
                onExpandedChange = { open = if (it) section else null },
            ) { QeetrixText(section.summary, variant = QeetrixTextVariant.Small) }
        }
    }
}

// EmptyState.md
@Composable
private fun EmptyStateExample() {
    val query = rememberTextFieldState("quarterly")
    Column {
        QeetrixEmptyState(
            title = "No invoices yet",
            description = "Invoices appear here once you bill a customer.",
            variant = QeetrixEmptyStateVariant.FirstUse,
            icon = QeetrixIconName.FileText,
        ) {
            QeetrixButton("Create invoice", onClick = ::createInvoice)
        }

        QeetrixEmptyState(
            title = "No results",
            description = "Nothing matches “${query.text}”.",
            variant = QeetrixEmptyStateVariant.NoResults,
            size = QeetrixEmptyStateSize.Sm,
        ) {
            QeetrixButton("Clear search", onClick = { query.clearText() }, variant = QeetrixButtonVariant.Outline)
        }
    }
}

// DataState.md
@Composable
private fun DataStateExample() {
    val listState = rememberLazyListState()
    QeetrixDataState(
        loading = state.loading,
        empty = state.invoices.isEmpty(),
        failure = if (state.failed) QeetrixDataFailure("Invoices couldn't be loaded.", onRetry = ::reload) else null,
        emptyContent = { QeetrixEmptyState("No invoices yet", description = "Invoices appear here once you bill a customer.") },
    ) {
        LazyColumn(state = listState) {
            items(state.invoices, key = { it.id }) { invoice -> QeetrixListItem(invoice.number, metadata = invoice.amount) }
        }
    }
}

// Skeleton.md
@Composable
private fun SkeletonExample() {
    Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
        QeetrixSkeleton(Modifier.size(40.dp), QeetrixSkeletonShape.Circle)
        QeetrixSkeletonText(Modifier.weight(1f), lines = 2)
    }
}

// Chip.md
@Composable
private fun ChipExample() {
    Column {
        var unread by rememberSaveable { mutableStateOf(false) }
        QeetrixChip("Unread", selected = unread, onSelectedChange = { unread = it })

        var status by rememberSaveable { mutableStateOf<Status?>(null) }
        QeetrixChipGroup(
            label = "Status",
            selected = status,
            onSelect = { status = it },
            options = listOf(QeetrixOption(null, "All"), QeetrixOption(Status.Open, "Open"), QeetrixOption(Status.Closed, "Closed")),
        )

        QeetrixChip("Owner: Ada", onRemove = { removeFilter("owner") })
    }
}

// Timeline.md
@Composable
private fun TimelineExample() {
    QeetrixTimeline {
        for (event in events) {
            key(event.id) {
                QeetrixTimelineItem(
                    title = event.title,
                    time = event.time,
                    description = event.detail,
                    tone = if (event.failed) QeetrixTimelineTone.Destructive else QeetrixTimelineTone.Neutral,
                    markerLabel = if (event.failed) "Failed" else null,
                )
            }
        }
    }
}

// SearchField.md
@Composable
private fun SearchFieldExample() {
    val query = rememberTextFieldState()
    QeetrixSearchField(query, placeholder = "Search members", onSearch = { search(it) })

// Filter as the person types, after a pause.
    LaunchedEffect(query) {
        snapshotFlow { query.text.toString() }.collectLatest { text ->
            delay(300)
            search(text)
        }
    }
}

// README.md, "Components"
@Composable
private fun ReadmeCollection() {
    val people = membersByTeam.values.flatten()
    val loading = false
    QeetrixDataState(loading = loading, empty = people.isEmpty(), emptyContent = { QeetrixEmptyState("No members yet") }) {
        LazyColumn {
            items(people, key = {
                it.id
            }) { person -> QeetrixListItem(person.name, description = person.email, onClick = { open(person.id) }) }
        }
    }
}
