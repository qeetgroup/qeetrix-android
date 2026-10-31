package com.qeetgroup.qeetrix.sample.composition

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
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
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.IntOffset
import com.qeetgroup.qeetrix.components.accordion.QeetrixAccordion
import com.qeetgroup.qeetrix.components.accordion.QeetrixAccordionItem
import com.qeetgroup.qeetrix.components.avatar.QeetrixAvatar
import com.qeetgroup.qeetrix.components.avatar.QeetrixAvatarSize
import com.qeetgroup.qeetrix.components.badge.QeetrixBadge
import com.qeetgroup.qeetrix.components.badge.QeetrixBadgeTone
import com.qeetgroup.qeetrix.components.badge.QeetrixChipGroup
import com.qeetgroup.qeetrix.components.button.QeetrixButton
import com.qeetgroup.qeetrix.components.button.QeetrixButtonTone
import com.qeetgroup.qeetrix.components.button.QeetrixButtonVariant
import com.qeetgroup.qeetrix.components.button.QeetrixIconButton
import com.qeetgroup.qeetrix.components.checkbox.QeetrixCheckbox
import com.qeetgroup.qeetrix.components.descriptionlist.QeetrixDescriptionItem
import com.qeetgroup.qeetrix.components.descriptionlist.QeetrixDescriptionList
import com.qeetgroup.qeetrix.components.dialog.QeetrixConfirmationDialog
import com.qeetgroup.qeetrix.components.dialog.QeetrixDialogIntent
import com.qeetgroup.qeetrix.components.dropdownmenu.QeetrixMenu
import com.qeetgroup.qeetrix.components.dropdownmenu.QeetrixMenuItem
import com.qeetgroup.qeetrix.components.emptystate.QeetrixDataState
import com.qeetgroup.qeetrix.components.emptystate.QeetrixEmptyState
import com.qeetgroup.qeetrix.components.emptystate.QeetrixEmptyStateSize
import com.qeetgroup.qeetrix.components.feed.QeetrixTimeline
import com.qeetgroup.qeetrix.components.feed.QeetrixTimelineItem
import com.qeetgroup.qeetrix.components.feed.QeetrixTimelineTone
import com.qeetgroup.qeetrix.components.input.QeetrixOption
import com.qeetgroup.qeetrix.components.input.QeetrixSearchField
import com.qeetgroup.qeetrix.components.list.QeetrixListItem
import com.qeetgroup.qeetrix.components.list.QeetrixSectionHeader
import com.qeetgroup.qeetrix.components.navigation.QeetrixBackButton
import com.qeetgroup.qeetrix.components.navigation.QeetrixTopAppBar
import com.qeetgroup.qeetrix.components.toast.QeetrixSnackbarHost
import com.qeetgroup.qeetrix.components.toast.QeetrixSnackbarResult
import com.qeetgroup.qeetrix.components.toast.rememberQeetrixSnackbarHostState
import com.qeetgroup.qeetrix.components.typography.QeetrixHeading
import com.qeetgroup.qeetrix.components.typography.QeetrixHeadingSize
import com.qeetgroup.qeetrix.components.typography.QeetrixText
import com.qeetgroup.qeetrix.components.typography.QeetrixTextSize
import com.qeetgroup.qeetrix.components.typography.QeetrixTextTone
import com.qeetgroup.qeetrix.components.typography.QeetrixTextWeight
import com.qeetgroup.qeetrix.foundation.motion.QeetrixMotion
import com.qeetgroup.qeetrix.foundation.motion.qeetrixAnimationSpec
import com.qeetgroup.qeetrix.foundation.spacing.QeetrixSpace
import com.qeetgroup.qeetrix.icons.QeetrixIconName
import com.qeetgroup.qeetrix.theme.QeetrixTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/** A workspace member's role — the sample's own type. */
enum class MemberRole(
    val title: String,
) {
    Owner("Owner"),
    Editor("Editor"),
    Viewer("Viewer"),
}

/** One member of the sample workspace. Synthetic: every address is at example.com. */
class SampleMember(
    val id: String,
    val name: String,
    val email: String,
    val team: String,
    val role: MemberRole,
    val joined: String,
    val lastActive: String,
)

/** The sample's members, in the order a server would return them. */
val SampleMembers =
    listOf(
        SampleMember("m01", "Asha Rao", "asha@example.com", "Design", MemberRole.Owner, "4 January 2026", "Today"),
        SampleMember("m02", "Ada Lovelace", "ada@example.com", "Engineering", MemberRole.Editor, "11 January 2026", "Today"),
        SampleMember("m03", "Grace Hopper", "grace@example.com", "Engineering", MemberRole.Editor, "2 February 2026", "Yesterday"),
        SampleMember("m04", "Priya Nair", "priya@example.com", "Finance", MemberRole.Viewer, "9 February 2026", "3 days ago"),
        SampleMember("m05", "Alan Turing", "alan@example.com", "Engineering", MemberRole.Viewer, "1 March 2026", "Last week"),
        SampleMember("m06", "Kavya Iyer", "kavya@example.com", "Design", MemberRole.Editor, "14 March 2026", "Today"),
        SampleMember("m07", "Rohan Das", "rohan@example.com", "Finance", MemberRole.Editor, "2 April 2026", "Yesterday"),
        SampleMember("m08", "Meera Pillai", "meera@example.com", "Design", MemberRole.Viewer, "19 April 2026", "2 weeks ago"),
        SampleMember("m09", "Arjun Mehta", "arjun@example.com", "Engineering", MemberRole.Owner, "6 May 2026", "Today"),
        SampleMember("m10", "Neha Gupta", "neha@example.com", "Finance", MemberRole.Owner, "23 May 2026", "Today"),
    )

private val RoleOptions = listOf(QeetrixOption<MemberRole?>(null, "Everyone")) + MemberRole.entries.map { QeetrixOption(it, it.title) }

/**
 * Every Phase 5 component in one generic flow, with all of the collection's state owned here as an app's
 * state holder would own it: a simulated load that can fail; a search field and role chips that filter on the
 * device; members grouped by team under sticky headers, each row keyed by the member's id; a long press that
 * starts a selection, removed after a confirmation with an undoable snackbar; and a member's details in a
 * description list, an accordion and a timeline. Back closes the details, then the selection, then the
 * screen ([onExit]). Synthetic data only; nothing is fetched or logged.
 */
@Composable
fun CollectionsScreen(onExit: () -> Unit) {
    var removed by rememberSaveable { mutableStateOf(setOf<String>()) }
    var selected by rememberSaveable { mutableStateOf(setOf<String>()) }
    var openMember by rememberSaveable { mutableStateOf<String?>(null) }
    var confirming by rememberSaveable { mutableStateOf(false) }
    // The people a confirmation asks about, kept while the dialog leaves.
    var removing by rememberSaveable { mutableStateOf(setOf<String>()) }
    var attempt by rememberSaveable { mutableIntStateOf(0) }
    var failNext by rememberSaveable { mutableStateOf(false) }
    var loading by rememberSaveable { mutableStateOf(true) }
    var failed by rememberSaveable { mutableStateOf(false) }
    val snackbars = rememberQeetrixSnackbarHostState()
    val scope = rememberCoroutineScope()

    LaunchedEffect(attempt) {
        loading = true
        delay(SIMULATED_LOAD_MILLIS)
        failed = failNext
        failNext = false
        loading = false
    }

    BackHandler(enabled = openMember != null) { openMember = null }
    BackHandler(enabled = openMember == null && selected.isNotEmpty()) { selected = emptySet() }

    val members = SampleMembers.filter { it.id !in removed }
    Box(Modifier.fillMaxSize().background(QeetrixTheme.colors.surfaceCanvas)) {
        val member = members.firstOrNull { it.id == openMember }
        if (member != null) {
            MemberDetail(member, onBack = { openMember = null })
        } else {
            MemberDirectory(
                members = members,
                loading = loading,
                failed = failed,
                onRetry = { attempt++ },
                selected = selected,
                onSelectedChange = { selected = it },
                onOpen = { openMember = it },
                onReload = { attempt++ },
                onFailNext = {
                    failNext = true
                    attempt++
                },
                onRemove = {
                    removing = selected
                    confirming = true
                },
                onExit = onExit,
            )
        }
        QeetrixSnackbarHost(snackbars, Modifier.align(Alignment.BottomCenter))
    }

    QeetrixConfirmationDialog(
        open = confirming,
        onOpenChange = { confirming = it },
        title = if (removing.size == 1) "Remove 1 person?" else "Remove ${removing.size} people?",
        description = "They lose access to this workspace. You can invite them again later.",
        confirmLabel = "Remove",
        onConfirm = {
            val gone = removing
            removed = removed + gone
            selected = emptySet()
            scope.launch {
                val message = if (gone.size == 1) "1 person removed" else "${gone.size} people removed"
                if (snackbars.show(message, actionLabel = "Undo") == QeetrixSnackbarResult.ActionPerformed) removed = removed - gone
            }
        },
        intent = QeetrixDialogIntent.Danger,
    )
}

@Composable
private fun MemberDirectory(
    members: List<SampleMember>,
    loading: Boolean,
    failed: Boolean,
    onRetry: () -> Unit,
    selected: Set<String>,
    onSelectedChange: (Set<String>) -> Unit,
    onOpen: (String) -> Unit,
    onReload: () -> Unit,
    onFailNext: () -> Unit,
    onRemove: () -> Unit,
    onExit: () -> Unit,
) {
    val query = rememberTextFieldState()
    var role by rememberSaveable { mutableStateOf<MemberRole?>(null) }
    var menu by rememberSaveable { mutableStateOf(false) }
    val listState = rememberLazyListState()
    val selecting = selected.isNotEmpty()
    val text = query.text.toString().trim()
    val shown =
        members.filter { member ->
            (role == null || member.role == role) &&
                (text.isEmpty() || member.name.contains(text, ignoreCase = true) || member.email.contains(text, ignoreCase = true))
        }
    val teams = shown.groupBy { it.team }.toSortedMap()
    val toggle = { id: String -> onSelectedChange(if (id in selected) selected - id else selected + id) }

    Column(Modifier.fillMaxSize()) {
        QeetrixTopAppBar(
            title = if (selecting) "${selected.size} selected" else "Members",
            description = if (selecting || loading) null else "${members.size} people",
            navigationIcon = {
                if (selecting) {
                    QeetrixIconButton(
                        QeetrixIconName.X,
                        "Clear selection",
                        onClick = { onSelectedChange(emptySet()) },
                        tone = QeetrixButtonTone.Neutral,
                    )
                } else {
                    QeetrixBackButton(onClick = onExit)
                }
            },
            actions = {
                if (selecting) {
                    QeetrixButton(
                        "Remove",
                        onClick = onRemove,
                        variant = QeetrixButtonVariant.Ghost,
                        tone = QeetrixButtonTone.Danger,
                        icon = QeetrixIconName.Trash,
                    )
                } else {
                    QeetrixMenu(
                        open = menu,
                        onOpenChange = { menu = it },
                        trigger = {
                            QeetrixIconButton(
                                QeetrixIconName.EllipsisVertical,
                                "More options",
                                onClick = { menu = true },
                                tone = QeetrixButtonTone.Neutral,
                            )
                        },
                    ) {
                        QeetrixMenuItem("Reload", onClick = onReload, icon = QeetrixIconName.RefreshCw)
                        QeetrixMenuItem("Simulate a failed load", onClick = onFailNext, icon = QeetrixIconName.TriangleAlert)
                    }
                }
            },
        )
        Column(
            Modifier.padding(horizontal = QeetrixSpace.InsetLg, vertical = QeetrixSpace.InsetSm),
            verticalArrangement = Arrangement.spacedBy(QeetrixSpace.GapXs),
        ) {
            QeetrixSearchField(query, label = "Search members", placeholder = "e.g. Asha or asha@example.com")
            QeetrixChipGroup(
                label = "Role",
                value = role,
                onValueChange = { role = it },
                options = RoleOptions,
                modifier = Modifier.horizontalScroll(rememberScrollState()),
            )
        }
        QeetrixDataState(
            loading = loading,
            error = failed,
            empty = shown.isEmpty(),
            onRetry = onRetry,
            errorTitle = "Members couldn't be loaded",
            errorDescription = "Check your connection, then try again.",
            emptyState = {
                if (members.isEmpty()) {
                    QeetrixEmptyState(
                        "No members yet",
                        icon = QeetrixIconName.Users,
                        description = "Invite people to work in this workspace with you.",
                        level = 2, // the list's main message, so TalkBack's heading navigation finds it
                    ) { QeetrixButton("Invite people", onClick = {}, icon = QeetrixIconName.Plus) }
                } else {
                    QeetrixEmptyState(
                        "No members found",
                        icon = QeetrixIconName.SearchX,
                        description = if (text.isEmpty()) "No one has this role." else "No one matches “$text”.",
                        size = QeetrixEmptyStateSize.Section,
                        level = 2,
                    ) {
                        QeetrixButton(
                            "Clear search and filters",
                            onClick = {
                                query.clearText()
                                role = null
                            },
                            variant = QeetrixButtonVariant.Soft,
                        )
                    }
                }
            },
            modifier = Modifier.fillMaxSize(),
        ) {
            val placement = qeetrixAnimationSpec<IntOffset>(QeetrixMotion.EasingStandard, QeetrixMotion.DurationNormal)
            val fade = qeetrixAnimationSpec<Float>(QeetrixMotion.EasingStandard, QeetrixMotion.DurationFast)
            LazyColumn(
                Modifier.fillMaxSize(),
                state = listState,
                contentPadding = WindowInsets.safeDrawing.only(WindowInsetsSides.Bottom).asPaddingValues(),
            ) {
                for ((team, people) in teams) {
                    stickyHeader(key = "team-$team", contentType = "header") {
                        QeetrixSectionHeader(
                            team,
                            Modifier.background(QeetrixTheme.colors.surfaceCanvas),
                            description = if (people.size == 1) "1 person" else "${people.size} people",
                        )
                    }
                    items(people, key = { it.id }, contentType = { "member" }) { person ->
                        val isSelected = person.id in selected
                        QeetrixListItem(
                            title = person.name,
                            modifier = Modifier.animateItem(fade, placement, fade),
                            description = person.email,
                            selected = isSelected,
                            onClick = { if (selecting) toggle(person.id) else onOpen(person.id) },
                            onLongClick = { toggle(person.id) },
                            onLongClickLabel = if (isSelected) "Deselect" else "Select",
                            leading = {
                                if (selecting) {
                                    QeetrixCheckbox(checked = isSelected, onCheckedChange = null)
                                } else {
                                    QeetrixAvatar(person.name, decorative = true)
                                }
                            },
                        ) {
                            if (person.role == MemberRole.Owner) QeetrixBadge("Owner", tone = QeetrixBadgeTone.Brand)
                        }
                    }
                }
            }
        }
    }
}

private enum class MemberSection { Permissions, Devices }

@Composable
private fun MemberDetail(
    member: SampleMember,
    onBack: () -> Unit,
) {
    var open by rememberSaveable(member.id) { mutableStateOf(listOf(MemberSection.Permissions)) }
    Column(Modifier.fillMaxSize()) {
        QeetrixTopAppBar(member.name, navigationIcon = { QeetrixBackButton(onClick = onBack) })
        Column(
            Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .windowInsetsPadding(WindowInsets.safeDrawing.only(WindowInsetsSides.Bottom + WindowInsetsSides.Horizontal))
                .padding(QeetrixSpace.InsetLg),
            verticalArrangement = Arrangement.spacedBy(QeetrixSpace.LayoutSm),
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(QeetrixSpace.GapMd), verticalAlignment = Alignment.CenterVertically) {
                QeetrixAvatar(member.name, size = QeetrixAvatarSize.Lg, decorative = true)
                Column {
                    QeetrixText(member.name, size = QeetrixTextSize.Lg, weight = QeetrixTextWeight.Semibold)
                    QeetrixText("${member.role.title} · ${member.team}", tone = QeetrixTextTone.Subtle)
                }
            }
            QeetrixDescriptionList {
                QeetrixDescriptionItem("Email", member.email)
                QeetrixDescriptionItem("Team", member.team)
                QeetrixDescriptionItem("Role") {
                    QeetrixBadge(
                        member.role.title,
                        tone =
                            if (member.role ==
                                MemberRole.Owner
                            ) {
                                QeetrixBadgeTone.Brand
                            } else {
                                QeetrixBadgeTone.Neutral
                            },
                    )
                }
                QeetrixDescriptionItem("Joined", member.joined)
                QeetrixDescriptionItem("Last active", member.lastActive)
            }
            QeetrixAccordion(value = open, onValueChange = { open = it }, multiple = true) {
                QeetrixAccordionItem(MemberSection.Permissions, "Permissions") {
                    val can =
                        when (member.role) {
                            MemberRole.Owner -> "Manage members, billing and every project."
                            MemberRole.Editor -> "Create and edit projects; invite viewers."
                            MemberRole.Viewer -> "Read projects and leave comments."
                        }
                    QeetrixText(can, size = QeetrixTextSize.Sm)
                }
                QeetrixAccordionItem(MemberSection.Devices, "Devices") {
                    QeetrixDescriptionList {
                        QeetrixDescriptionItem(
                            "Pixel 9",
                            "Last used ${member.lastActive.lowercase()}",
                            description = "Signed in with a passkey",
                        )
                        QeetrixDescriptionItem("MacBook Air", "Last used last week")
                    }
                }
            }
            Column(verticalArrangement = Arrangement.spacedBy(QeetrixSpace.GapMd)) {
                QeetrixHeading("Activity", level = 2, size = QeetrixHeadingSize.TitleSm)
                QeetrixTimeline {
                    QeetrixTimelineItem(
                        "Signed in",
                        time = member.lastActive,
                        icon = QeetrixIconName.Lock,
                        tone = QeetrixTimelineTone.Success,
                    )
                    QeetrixTimelineItem("Profile photo changed", time = "Last week")
                    QeetrixTimelineItem(
                        "Role set to ${member.role.title}",
                        time = member.joined,
                        icon = QeetrixIconName.UserRound,
                        tone = QeetrixTimelineTone.Brand,
                    )
                    QeetrixTimelineItem("Joined the workspace", time = member.joined, description = "Invited by Asha Rao.")
                }
            }
        }
    }
}

private const val SIMULATED_LOAD_MILLIS = 800L
