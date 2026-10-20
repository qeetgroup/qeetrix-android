package com.qeetgroup.qeetrix.quality

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.state.ToggleableState
import androidx.compose.ui.unit.dp
import com.qeetgroup.qeetrix.components.accordion.QeetrixAccordion
import com.qeetgroup.qeetrix.components.accordion.QeetrixAccordionItem
import com.qeetgroup.qeetrix.components.avatar.QeetrixAvatar
import com.qeetgroup.qeetrix.components.badge.QeetrixBadge
import com.qeetgroup.qeetrix.components.badge.QeetrixBadgeVariant
import com.qeetgroup.qeetrix.components.badge.QeetrixChip
import com.qeetgroup.qeetrix.components.badge.QeetrixChipGroup
import com.qeetgroup.qeetrix.components.button.QeetrixButton
import com.qeetgroup.qeetrix.components.button.QeetrixButtonVariant
import com.qeetgroup.qeetrix.components.button.QeetrixIconButton
import com.qeetgroup.qeetrix.components.card.QeetrixCard
import com.qeetgroup.qeetrix.components.checkbox.QeetrixCheckbox
import com.qeetgroup.qeetrix.components.checkbox.QeetrixTriStateCheckbox
import com.qeetgroup.qeetrix.components.descriptionlist.QeetrixDescriptionItem
import com.qeetgroup.qeetrix.components.descriptionlist.QeetrixDescriptionList
import com.qeetgroup.qeetrix.components.emptystate.QeetrixDataFailure
import com.qeetgroup.qeetrix.components.emptystate.QeetrixDataState
import com.qeetgroup.qeetrix.components.emptystate.QeetrixEmptyState
import com.qeetgroup.qeetrix.components.emptystate.QeetrixEmptyStateVariant
import com.qeetgroup.qeetrix.components.feed.QeetrixTimeline
import com.qeetgroup.qeetrix.components.feed.QeetrixTimelineEmphasis
import com.qeetgroup.qeetrix.components.feed.QeetrixTimelineItem
import com.qeetgroup.qeetrix.components.feed.QeetrixTimelineTone
import com.qeetgroup.qeetrix.components.form.QeetrixFormActions
import com.qeetgroup.qeetrix.components.form.QeetrixFormError
import com.qeetgroup.qeetrix.components.form.QeetrixFormErrorSummary
import com.qeetgroup.qeetrix.components.input.QeetrixField
import com.qeetgroup.qeetrix.components.input.QeetrixFieldMessage
import com.qeetgroup.qeetrix.components.input.QeetrixFieldSet
import com.qeetgroup.qeetrix.components.input.QeetrixOption
import com.qeetgroup.qeetrix.components.input.QeetrixSearchField
import com.qeetgroup.qeetrix.components.input.QeetrixTextField
import com.qeetgroup.qeetrix.components.input.QeetrixTextarea
import com.qeetgroup.qeetrix.components.list.QeetrixListItem
import com.qeetgroup.qeetrix.components.list.QeetrixListItemIcon
import com.qeetgroup.qeetrix.components.list.QeetrixSectionHeader
import com.qeetgroup.qeetrix.components.navigation.QeetrixBackButton
import com.qeetgroup.qeetrix.components.navigation.QeetrixNavigationBar
import com.qeetgroup.qeetrix.components.navigation.QeetrixNavigationItem
import com.qeetgroup.qeetrix.components.navigation.QeetrixNavigationRail
import com.qeetgroup.qeetrix.components.navigation.QeetrixTopAppBar
import com.qeetgroup.qeetrix.components.numberfield.QeetrixNumberField
import com.qeetgroup.qeetrix.components.otpinput.QeetrixOTPInput
import com.qeetgroup.qeetrix.components.passwordinput.QeetrixPasswordInput
import com.qeetgroup.qeetrix.components.progress.QeetrixProgress
import com.qeetgroup.qeetrix.components.radiogroup.QeetrixRadioGroup
import com.qeetgroup.qeetrix.components.select.QeetrixSelect
import com.qeetgroup.qeetrix.components.separator.QeetrixSeparator
import com.qeetgroup.qeetrix.components.spinner.QeetrixSkeleton
import com.qeetgroup.qeetrix.components.spinner.QeetrixSkeletonText
import com.qeetgroup.qeetrix.components.spinner.QeetrixSpinner
import com.qeetgroup.qeetrix.components.switchcontrol.QeetrixSwitch
import com.qeetgroup.qeetrix.components.tabs.QeetrixTab
import com.qeetgroup.qeetrix.components.tabs.QeetrixTabs
import com.qeetgroup.qeetrix.components.tabs.QeetrixTabsVariant
import com.qeetgroup.qeetrix.components.toast.QeetrixSnackbar
import com.qeetgroup.qeetrix.components.toast.QeetrixSnackbarType
import com.qeetgroup.qeetrix.components.typography.QeetrixText
import com.qeetgroup.qeetrix.components.typography.QeetrixTextVariant
import com.qeetgroup.qeetrix.icons.QeetrixIcon
import com.qeetgroup.qeetrix.icons.QeetrixIconName
import java.math.BigDecimal

/**
 * Every in-place component of Phases 2–5 in the states that carry accessibility and layout risk: disabled,
 * selected, invalid, loading, long text. The quality scans (accessibility checks, touch targets, text clipping,
 * reduced motion) run over all of it. Overlays open in their own windows and are checked by their own tests.
 * Values are synthetic.
 */
internal class QualitySpecimen(
    val name: String,
    val content: @Composable () -> Unit,
)

internal val QualityCatalog: List<QualitySpecimen> =
    listOf(
        QualitySpecimen("text") {
            Column {
                QeetrixText("Account settings", variant = QeetrixTextVariant.H2)
                QeetrixText("Changes apply to every device signed in to this account.", variant = QeetrixTextVariant.Muted)
                QeetrixText("Last changed by Asha Rao", variant = QeetrixTextVariant.Small)
            }
        },
        QualitySpecimen("separator") { QeetrixSeparator() },
        QualitySpecimen("card") {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                QeetrixCard(title = "Plan", description = "Team, billed monthly") { QeetrixText("12 seats in use") }
                QeetrixCard(onClick = {}, title = "Security", description = "Passkeys and sessions")
                QeetrixCard(onClick = {}, title = "Archived", description = "Read-only", enabled = false)
            }
        },
        QualitySpecimen("badge") {
            Column {
                for (variant in QeetrixBadgeVariant.entries) {
                    QeetrixBadge(
                        variant.value,
                        variant = variant,
                        icon = QeetrixIconName.Check,
                    )
                }
            }
        },
        QualitySpecimen("button") {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                for (variant in QeetrixButtonVariant.entries) QeetrixButton("Save changes", onClick = {}, variant = variant)
                QeetrixButton("Disabled", onClick = {}, enabled = false)
                QeetrixButton("Save", onClick = {}, loading = true, loadingLabel = "Saving…")
                QeetrixButton("Share", onClick = {}, leadingIcon = QeetrixIconName.Share2)
            }
        },
        QualitySpecimen("icon-button") {
            Column {
                for (variant in QeetrixButtonVariant.entries) {
                    QeetrixIconButton(
                        QeetrixIconName.Settings,
                        "Settings",
                        onClick = {},
                        variant = variant,
                    )
                }
                QeetrixIconButton(QeetrixIconName.Trash, "Delete", onClick = {}, enabled = false)
                QeetrixIconButton(QeetrixIconName.Send, "Send", onClick = {}, loading = true)
            }
        },
        QualitySpecimen("spinner") { QeetrixSpinner() },
        QualitySpecimen("progress") {
            Column {
                QeetrixProgress(0.4f, label = "Uploading report")
                QeetrixProgress(null, label = "Preparing export")
            }
        },
        QualitySpecimen("avatar") {
            Column {
                QeetrixAvatar("Ada Lovelace")
                QeetrixAvatar("Grace Hopper", badgeLabel = "Online")
                QeetrixAvatar(null)
            }
        },
        QualitySpecimen("switch") {
            Column {
                QeetrixSwitch(true, {}, label = "Email alerts", description = "A message when someone signs in")
                QeetrixSwitch(false, {}, label = "Managed by your organisation", enabled = false)
                QeetrixSwitch(false, {}, label = "Accept the terms", invalid = true)
            }
        },
        QualitySpecimen("text-field") {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                QeetrixTextField(TextFieldState(), label = "Work email", placeholder = "you@company.com", required = true)
                QeetrixTextField(
                    TextFieldState("ada@"),
                    label = "Recovery email",
                    message = QeetrixFieldMessage("Enter an email address like name@company.com."),
                )
                QeetrixTextField(TextFieldState("Asha Rao"), label = "Name", readOnly = true)
                QeetrixTextField(TextFieldState("Qeet Example"), label = "Organisation", enabled = false, optional = true)
            }
        },
        QualitySpecimen("field") {
            QeetrixField(label = "Notifications", description = "On every device") {
                QeetrixSwitch(true, {}, Modifier.qeetrixFieldControl())
            }
        },
        QualitySpecimen("field-set") {
            QeetrixFieldSet(
                legend = "Topics",
                message = QeetrixFieldMessage("Choose at least one topic."),
            ) {
                QeetrixCheckbox(false, {}, label = "Product updates", description = "About once a month")
                QeetrixCheckbox(false, {}, label = "Security notices")
            }
        },
        QualitySpecimen("textarea") {
            QeetrixTextarea(TextFieldState("Line one\nLine two"), label = "Notes", description = "Shown to your team", minLines = 3)
        },
        QualitySpecimen("password-input") {
            Column {
                QeetrixPasswordInput(TextFieldState("Synthetic-Passw0rd"), label = "Password")
                QeetrixPasswordInput(TextFieldState(), label = "New password", message = QeetrixFieldMessage("Use 12 characters or more."))
            }
        },
        QualitySpecimen("otp-input") {
            Column {
                QeetrixOTPInput(TextFieldState("12"), label = "Verification code", groupSize = 3)
                QeetrixOTPInput(TextFieldState(), message = QeetrixFieldMessage("That code has expired."))
            }
        },
        QualitySpecimen("checkbox") {
            Column {
                QeetrixCheckbox(true, {}, label = "Remember this device")
                QeetrixCheckbox(false, {}, label = "Disabled", enabled = false)
                QeetrixTriStateCheckbox(ToggleableState.Indeterminate, onClick = {}, label = "All members")
                // A bare box in a row that names it, as the component asks.
                Box(
                    Modifier.toggleable(value = true, role = Role.Checkbox, onValueChange = {}).semantics {
                        contentDescription =
                            "Select row"
                    },
                ) {
                    QeetrixCheckbox(checked = true, onCheckedChange = null)
                }
            }
        },
        QualitySpecimen("radio-group") {
            QeetrixRadioGroup(
                "team",
                {},
                listOf(
                    QeetrixOption("starter", "Starter", "One person"),
                    QeetrixOption("team", "Team"),
                    QeetrixOption("enterprise", "Enterprise", enabled = false),
                ),
                label = "Plan",
            )
        },
        QualitySpecimen("select") {
            Column {
                QeetrixSelect("in", {}, listOf(QeetrixOption("in", "India"), QeetrixOption("sg", "Singapore")), label = "Region")
                QeetrixSelect(
                    null,
                    {
                    },
                    listOf(
                        QeetrixOption<String?>("in", "India"),
                    ),
                    label = "Billing country",
                    placeholder = "Choose a country",
                    message = QeetrixFieldMessage("Choose a country."),
                )
            }
        },
        QualitySpecimen("number-field") {
            Column {
                QeetrixNumberField(4, {}, label = "Seats", range = 1..50)
                QeetrixNumberField(BigDecimal("12.50"), {}, label = "Price", fractionDigits = 2)
            }
        },
        QualitySpecimen("form") {
            Column {
                QeetrixFormErrorSummary(
                    listOf(QeetrixFormError("Enter your work email", onClick = {}), QeetrixFormError("Choose a plan", onClick = {})),
                )
                QeetrixFormActions {
                    QeetrixButton("Cancel", onClick = {}, variant = QeetrixButtonVariant.Outline)
                    QeetrixButton("Save", onClick = {})
                }
            }
        },
        QualitySpecimen("navigation-bar") { QeetrixNavigationBar(NavigationItems, selected = "inbox", onSelect = {}) },
        QualitySpecimen("navigation-rail") {
            Box(Modifier.height(320.dp)) { QeetrixNavigationRail(NavigationItems, selected = "home", onSelect = {}) }
        },
        QualitySpecimen("top-app-bar") {
            QeetrixTopAppBar(
                "Invoices",
                subtitle = "12 unpaid",
                navigationIcon = { QeetrixBackButton(onClick = {}) },
                actions = { QeetrixIconButton(QeetrixIconName.EllipsisVertical, "More options", onClick = {}) },
            )
        },
        QualitySpecimen("tabs") {
            Column {
                val tabs =
                    listOf(
                        QeetrixTab("overview", "Overview"),
                        QeetrixTab("activity", "Activity", QeetrixIconName.Bell),
                        QeetrixTab("billing", "Billing", enabled = false),
                    )
                QeetrixTabs(tabs, selected = "overview", onSelect = {})
                QeetrixTabs(tabs, selected = "activity", onSelect = {}, variant = QeetrixTabsVariant.Line)
            }
        },
        QualitySpecimen("snackbar") {
            Column {
                QeetrixSnackbar("Conversation archived", actionLabel = "Undo", onAction = {}, onDismiss = {})
                QeetrixSnackbar(
                    "Payment failed",
                    type = QeetrixSnackbarType.Error,
                    description = "The bank declined the card.",
                    onDismiss = {},
                )
            }
        },
        QualitySpecimen("list-item") {
            Column {
                QeetrixSectionHeader("Members", description = "People with access") {
                    QeetrixButton("Invite", onClick = {}, variant = QeetrixButtonVariant.Link)
                }
                QeetrixListItem("Ada Lovelace", description = "ada@example.com", metadata = "Owner", onClick = {}, leading = {
                    QeetrixAvatar("Ada Lovelace", contentDescription = null)
                })
                QeetrixListItem("Quarterly report.pdf", description = "2.4 MB", selected = true, onClick = {
                }, onLongClick = {}, onLongClickLabel = "Select", leading = {
                    QeetrixListItemIcon(QeetrixIconName.FileText)
                })
                QeetrixListItem("Weekly digest", description = "Every Monday") {
                    QeetrixIconButton(QeetrixIconName.Ellipsis, "More for Weekly digest", onClick = {})
                }
                QeetrixListItem("Archived project", enabled = false, onClick = {})
            }
        },
        QualitySpecimen("description-list") {
            QeetrixDescriptionList(divided = true) {
                QeetrixDescriptionItem("Plan", "Team")
                QeetrixDescriptionItem("Status") { QeetrixBadge("Active", variant = QeetrixBadgeVariant.Success) }
                QeetrixDescriptionItem("Billing contact", "Finance team, 4th floor, Indiranagar, Bengaluru")
            }
        },
        QualitySpecimen("accordion") {
            QeetrixAccordion {
                QeetrixAccordionItem("How am I billed?", expanded = true, onExpandedChange = {
                }) { QeetrixText("Monthly.", variant = QeetrixTextVariant.Small) }
                QeetrixAccordionItem("Notifications", expanded = false, onExpandedChange = {
                }, description = "Email and push", icon = QeetrixIconName.Bell) {}
                QeetrixAccordionItem("Data export", expanded = false, onExpandedChange = {}, enabled = false) {}
            }
        },
        QualitySpecimen("empty-state") {
            Column {
                QeetrixEmptyState(
                    "No invoices yet",
                    description = "Invoices appear here once you bill a customer.",
                    variant = QeetrixEmptyStateVariant.FirstUse,
                ) {
                    QeetrixButton("Create invoice", onClick = {})
                }
                QeetrixEmptyState("No results", description = "Nothing matches “quarterly”.", variant = QeetrixEmptyStateVariant.NoResults)
            }
        },
        QualitySpecimen("data-state") {
            Column {
                QeetrixDataState(loading = true, empty = false, emptyContent = {}) {}
                QeetrixDataState(loading = false, empty = false, failure = QeetrixDataFailure(onRetry = {}), emptyContent = {}) {}
            }
        },
        QualitySpecimen("skeleton") {
            Column {
                QeetrixSkeleton(Modifier.size(40.dp))
                QeetrixSkeletonText(lines = 2)
            }
        },
        QualitySpecimen("chip") {
            Column {
                QeetrixChip("Unread", selected = true, onSelectedChange = {})
                QeetrixChip("Archived", selected = false, onSelectedChange = {}, enabled = false)
                QeetrixChip("Owner: Ada", onRemove = {})
                QeetrixChipGroup(label = "Status", selected = "open", onSelect = {
                }, options = listOf(QeetrixOption("open", "Open"), QeetrixOption("closed", "Closed")))
                QeetrixChipGroup(label = "Labels", selected = setOf("design"), onSelectedChange = {
                }, options = listOf(QeetrixOption("design", "Design"), QeetrixOption("legal", "Legal")))
            }
        },
        QualitySpecimen("timeline") {
            QeetrixTimeline {
                QeetrixTimelineItem(
                    "Payment received",
                    time = "Today",
                    description = "By bank transfer.",
                    tone = QeetrixTimelineTone.Success,
                    icon = QeetrixIconName.CircleCheck,
                )
                QeetrixTimelineItem("Viewed", time = "Yesterday", emphasis = QeetrixTimelineEmphasis.Minor)
                QeetrixTimelineItem("Card payment", time = "5 days ago", tone = QeetrixTimelineTone.Destructive, markerLabel = "Failed")
            }
        },
        QualitySpecimen("search-field") {
            Column {
                QeetrixSearchField(TextFieldState())
                QeetrixSearchField(TextFieldState("Ada"), label = "Find a member")
            }
        },
        QualitySpecimen("icon") {
            Column {
                QeetrixIcon(QeetrixIconName.Lock, contentDescription = "Locked")
                QeetrixIcon(QeetrixIconName.Info, contentDescription = null)
            }
        },
    )

private val NavigationItems =
    listOf(
        QeetrixNavigationItem("home", "Home", QeetrixIconName.House),
        QeetrixNavigationItem("inbox", "Inbox", QeetrixIconName.Inbox, badge = "3"),
        QeetrixNavigationItem("settings", "Settings", QeetrixIconName.Settings),
    )

/**
 * App-supplied text in other scripts and lengths: Hindi (Devanagari), Telugu, Arabic (right to left when the
 * layout is) and pseudo-expanded English — accented and about 40 % longer, as Android's en-XA pseudolocale writes
 * it. Translations are illustrative, not reviewed copy.
 */
internal val LocalizedCatalog: List<QualitySpecimen> =
    listOf(
        QualitySpecimen("hindi") {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                QeetrixButton("बदलाव सहेजें", onClick = {})
                QeetrixListItem("तिमाही रिपोर्ट", description = "अदा लवलेस ने साझा किया", metadata = "२.४ MB", onClick = {})
                QeetrixTextField(TextFieldState("आशा राव"), label = "पूरा नाम")
                QeetrixNavigationBar(
                    listOf(
                        QeetrixNavigationItem("home", "होम", QeetrixIconName.House),
                        QeetrixNavigationItem("inbox", "इनबॉक्स", QeetrixIconName.Inbox),
                        QeetrixNavigationItem("settings", "सेटिंग्स", QeetrixIconName.Settings),
                    ),
                    selected = "home",
                    onSelect = {},
                )
            }
        },
        QualitySpecimen("telugu") {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                QeetrixEmptyState(
                    "ఇంకా ఇన్‌వాయిస్‌లు లేవు",
                    description = "మీరు కస్టమర్‌కు బిల్ చేసిన తర్వాత ఇన్‌వాయిస్‌లు ఇక్కడ కనిపిస్తాయి.",
                )
                QeetrixDescriptionList { QeetrixDescriptionItem("ప్రణాళిక", "బృందం") }
                QeetrixChip("చదవనివి", selected = true, onSelectedChange = {})
            }
        },
        QualitySpecimen("arabic") {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                QeetrixTextField(TextFieldState("أدا لوفليس"), label = "الاسم الكامل", placeholder = "اكتب اسمك")
                QeetrixSnackbar("تمت أرشفة المحادثة", actionLabel = "تراجع", onAction = {}, onDismiss = {})
                QeetrixTimeline { QeetrixTimelineItem("تم استلام الدفعة", time = "اليوم", description = "بتحويل مصرفي.") }
                QeetrixTabs(listOf(QeetrixTab("a", "نظرة عامة"), QeetrixTab("b", "النشاط")), selected = "a", onSelect = {})
            }
        },
        QualitySpecimen("pseudo-expanded") {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                QeetrixButton(pseudo("Save changes"), onClick = {}, leadingIcon = QeetrixIconName.Check)
                QeetrixTabs(listOf(QeetrixTab("a", pseudo("Overview")), QeetrixTab("b", pseudo("Activity"))), selected = "a", onSelect = {})
                QeetrixChipGroup(label = pseudo("Status"), selected = "open", onSelect = {
                }, options = listOf(QeetrixOption("open", pseudo("Open")), QeetrixOption("closed", pseudo("Closed"))))
                QeetrixSnackbar(pseudo("Conversation archived"), actionLabel = pseudo("Undo"), onAction = {}, onDismiss = {})
                QeetrixListItem(
                    pseudo("Quarterly report"),
                    description = pseudo("Shared by Ada Lovelace"),
                    metadata = pseudo("2 MB"),
                    onClick = {},
                ) {
                    QeetrixBadge(pseudo("Shared"))
                }
                QeetrixAccordionItem(pseudo("How am I billed?"), expanded = false, onExpandedChange = {
                }, description = pseudo("Invoices and payment method")) {}
                QeetrixSwitch(true, {}, label = pseudo("Email alerts"), description = pseudo("A message when someone signs in"))
                QeetrixFormActions {
                    QeetrixButton(pseudo("Cancel"), onClick = {}, variant = QeetrixButtonVariant.Outline)
                    QeetrixButton(pseudo("Save"), onClick = {})
                }
            }
        },
    )

/** English as Android's en-XA pseudolocale writes it: accented, and padded to about 140 % of its length. */
internal fun pseudo(text: String): String {
    val accented = text.map { Accents[it] ?: it }.joinToString("")
    val padding = " one two three four five six seven".take((text.length * 0.4f).toInt().coerceAtLeast(2))
    return "[$accented$padding]"
}

private val Accents: Map<Char, Char> =
    "aàeéiîoôuüAÅEÉIÎOÖUÛcçCÇnñNÑsšSŠyýYÝ".chunked(2).associate { it[0] to it[1] }
