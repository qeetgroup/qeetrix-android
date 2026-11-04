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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.hasContentDescription
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.isHeading
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.unit.dp
import com.qeetgroup.qeetrix.components.accordion.QeetrixAccordion
import com.qeetgroup.qeetrix.components.accordion.QeetrixAccordionItem
import com.qeetgroup.qeetrix.components.accordion.QeetrixCollapsiblePanel
import com.qeetgroup.qeetrix.components.accordion.QeetrixCollapsibleTrigger
import com.qeetgroup.qeetrix.components.badge.QeetrixBadge
import com.qeetgroup.qeetrix.components.badge.QeetrixBadgeTone
import com.qeetgroup.qeetrix.components.badge.QeetrixChip
import com.qeetgroup.qeetrix.components.badge.QeetrixChipGroup
import com.qeetgroup.qeetrix.components.button.QeetrixButton
import com.qeetgroup.qeetrix.components.button.QeetrixButtonSize
import com.qeetgroup.qeetrix.components.button.QeetrixButtonVariant
import com.qeetgroup.qeetrix.components.descriptionlist.QeetrixDescriptionItem
import com.qeetgroup.qeetrix.components.descriptionlist.QeetrixDescriptionList
import com.qeetgroup.qeetrix.components.emptystate.QeetrixDataState
import com.qeetgroup.qeetrix.components.emptystate.QeetrixEmptyState
import com.qeetgroup.qeetrix.components.feed.QeetrixTimeline
import com.qeetgroup.qeetrix.components.feed.QeetrixTimelineGroup
import com.qeetgroup.qeetrix.components.feed.QeetrixTimelineItem
import com.qeetgroup.qeetrix.components.feed.QeetrixTimelineTone
import com.qeetgroup.qeetrix.components.input.QeetrixOption
import com.qeetgroup.qeetrix.components.input.QeetrixSearchField
import com.qeetgroup.qeetrix.components.list.QeetrixListItem
import com.qeetgroup.qeetrix.components.list.QeetrixListItemIcon
import com.qeetgroup.qeetrix.components.list.QeetrixSectionHeader
import com.qeetgroup.qeetrix.components.spinner.QeetrixSkeleton
import com.qeetgroup.qeetrix.components.spinner.QeetrixSkeletonSize
import com.qeetgroup.qeetrix.components.spinner.QeetrixSkeletonText
import com.qeetgroup.qeetrix.components.spinner.QeetrixSkeletonVariant
import com.qeetgroup.qeetrix.components.typography.QeetrixText
import com.qeetgroup.qeetrix.components.typography.QeetrixTextSize
import com.qeetgroup.qeetrix.foundation.spacing.QeetrixSpace
import com.qeetgroup.qeetrix.icons.QeetrixIconName
import com.qeetgroup.qeetrix.theme.QeetrixTheme
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

/**
 * The Phase 5 examples in the component pages in `Documentation/Components` (and one that was in README.md), as written there,
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
            "Quarterly report.pdf", "Engineering", "priya@acme.in", "Not set", "Tax settings", "Advanced options", "whsec_",
            "No invoices yet", "Or import them from a CSV file", "INV-2041", "Unread", "Remove Status: Active", "Departments",
            "Signed in with a passkey", "Today", "e.g. Asha", "Ada Lovelace",
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

private class Group(
    val id: String,
    val label: String,
    val members: List<Person>,
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

private enum class Status { Open, Closed }

private val files = listOf(SampleFile("f1", "Quarterly report.pdf", "Shared by Ada Lovelace", "2.4 MB"))

private val groups =
    listOf(
        Group("design", "Design", listOf(Person("m1", "Asha Rao", "asha@example.com"))),
        Group("engineering", "Engineering", listOf(Person("m2", "Ada Lovelace", "ada@example.com"))),
    )

private val state = InvoicesState(loading = false, failed = false, invoices = listOf(Invoice("i1", "INV-2041", "₹48,000")))

/** Stands in for the app's member search. */
private object Members {
    fun search(text: String) = check(text.length >= 0)
}

private fun open(id: String) = check(id.isNotEmpty())

private fun createInvoice() = Unit

private fun reload() = Unit

private fun edit() = Unit

private fun remove(filter: String) = check(filter.isNotEmpty())

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
                QeetrixBadge("Shared")
            }
        }
    }
}

// SectionHeader.md
@Composable
private fun SectionHeaderExample() {
    LazyColumn {
        for ((index, group) in groups.withIndex()) {
            stickyHeader(key = "header-${group.id}") {
                QeetrixSectionHeader(group.label, Modifier.background(QeetrixTheme.colors.surfaceCanvas), ruled = index > 0)
            }
            items(group.members, key = { it.id }) { member -> QeetrixListItem(member.name) }
        }
    }
}

// DescriptionList.md
@Composable
private fun DescriptionListExample() {
    QeetrixDescriptionList {
        QeetrixDescriptionItem("Email", "priya@acme.in")
        QeetrixDescriptionItem("Status") { QeetrixBadge("Active", tone = QeetrixBadgeTone.Success) }
        QeetrixDescriptionItem("Phone", null) // "—", read as "Not set"
        QeetrixDescriptionItem(
            "Display name",
            "Acme",
            description = "Shown in the top bar and in invitations.",
            action = { QeetrixButton("Change", onClick = ::edit, variant = QeetrixButtonVariant.Ghost, size = QeetrixButtonSize.Sm) },
        )
    }
}

// Accordion.md
@Composable
private fun AccordionExample() {
    var open by rememberSaveable { mutableStateOf(listOf("tax")) }
    QeetrixAccordion(value = open, onValueChange = { open = it }) {
        QeetrixAccordionItem("tax", "Tax settings") { QeetrixText("GST is added at 18%.") }
        QeetrixAccordionItem("numbering", "Invoice numbering") { QeetrixText("INV-YYYY-NNNN") }
    }

    var advanced by rememberSaveable { mutableStateOf(true) }
    QeetrixCollapsibleTrigger("Advanced options", open = advanced, onOpenChange = { advanced = it })
    QeetrixCollapsiblePanel(advanced) { WebhookSecret() }
}

// EmptyState.md
@Composable
private fun EmptyStateExample() {
    QeetrixEmptyState(
        title = "No invoices yet",
        icon = QeetrixIconName.FileText,
        description = "Invoices appear here once you bill a customer.",
        footer = { QeetrixText("Or import them from a CSV file", size = QeetrixTextSize.Sm) },
    ) {
        QeetrixButton("Create invoice", onClick = ::createInvoice, icon = QeetrixIconName.Plus)
    }
}

// DataState.md
@Composable
private fun DataStateExample() {
    QeetrixDataState(
        loading = state.loading,
        error = state.failed,
        empty = state.invoices.isEmpty(),
        onRetry = ::reload,
        emptyState = { QeetrixEmptyState("No invoices yet", description = "Invoices appear here once you bill a customer.") },
    ) {
        InvoiceList(state.invoices)
    }
}

@Composable
private fun InvoiceList(invoices: List<Invoice>) {
    val listState = rememberLazyListState()
    LazyColumn(state = listState) {
        items(invoices, key = { it.id }) { invoice -> QeetrixListItem(invoice.number, metadata = invoice.amount) }
    }
}

// Skeleton.md
@Composable
private fun SkeletonExample() {
    Row(horizontalArrangement = Arrangement.spacedBy(QeetrixSpace.GapMd)) {
        QeetrixSkeleton(variant = QeetrixSkeletonVariant.Circle, size = QeetrixSkeletonSize.Lg)
        QeetrixSkeletonText(lines = 2, modifier = Modifier.weight(1f))
    }
}

// Chip.md
@Composable
private fun ChipExample() {
    Column {
        var unread by rememberSaveable { mutableStateOf(false) }
        val filter = "status"
        var status by rememberSaveable { mutableStateOf<Status?>(null) }
        val statusOptions = listOf(QeetrixOption(null, "All"), QeetrixOption(Status.Open, "Open"), QeetrixOption(Status.Closed, "Closed"))
        var departments by rememberSaveable { mutableStateOf(setOf("Design")) }
        val options = listOf("Design", "Finance").map { QeetrixOption(it, it) }

        QeetrixChip("Unread", selected = unread, onSelectedChange = { unread = it })
        QeetrixChip("Status: Active", onRemove = { remove(filter) })
        QeetrixChipGroup(label = "Status", value = status, onValueChange = { status = it }, options = statusOptions)
        QeetrixChipGroup(label = "Departments", values = departments, onValuesChange = { departments = it }, options = options)
    }
}

// Timeline.md
@Composable
private fun TimelineExample() {
    QeetrixTimeline {
        QeetrixTimelineGroup("Today") {
            QeetrixTimelineItem("Signed in with a passkey", description = "Bengaluru · Chrome on macOS", time = "2 min ago", icon = QeetrixIconName.Lock)
            QeetrixTimelineItem("Sign-in refused", time = "1 h ago", icon = QeetrixIconName.CircleX, tone = QeetrixTimelineTone.Danger)
        }
    }
}

// SearchField.md
@Composable
private fun SearchFieldExample() {
    val members = Members
    val query = rememberTextFieldState()
    QeetrixSearchField(query, placeholder = "e.g. Asha", onSearch = { members.search(it) })
}

// A collection (was in README.md)
@Composable
private fun ReadmeCollection() {
    val people = groups.flatMap { it.members }
    val loading = false
    QeetrixDataState(loading = loading, empty = people.isEmpty(), emptyState = { QeetrixEmptyState("No members yet") }) {
        LazyColumn {
            items(people, key = {
                it.id
            }) { person -> QeetrixListItem(person.name, description = person.email, onClick = { open(person.id) }) }
        }
    }
}

/** Stands in for the app's own content in a collapsible panel. */
@Composable
private fun WebhookSecret() = QeetrixText("whsec_••••", mono = true)
