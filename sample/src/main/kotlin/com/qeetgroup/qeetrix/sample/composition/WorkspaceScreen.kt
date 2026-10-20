package com.qeetgroup.qeetrix.sample.composition

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.qeetgroup.qeetrix.components.button.QeetrixButton
import com.qeetgroup.qeetrix.components.button.QeetrixButtonVariant
import com.qeetgroup.qeetrix.components.button.QeetrixIconButton
import com.qeetgroup.qeetrix.components.card.QeetrixCard
import com.qeetgroup.qeetrix.components.card.QeetrixCardSize
import com.qeetgroup.qeetrix.components.dialog.QeetrixConfirmationDialog
import com.qeetgroup.qeetrix.components.dialog.QeetrixDialog
import com.qeetgroup.qeetrix.components.drawer.QeetrixBottomSheet
import com.qeetgroup.qeetrix.components.dropdownmenu.QeetrixMenu
import com.qeetgroup.qeetrix.components.dropdownmenu.QeetrixMenuItem
import com.qeetgroup.qeetrix.components.dropdownmenu.QeetrixMenuItemVariant
import com.qeetgroup.qeetrix.components.dropdownmenu.QeetrixMenuSeparator
import com.qeetgroup.qeetrix.components.emptystate.QeetrixEmptyState
import com.qeetgroup.qeetrix.components.input.QeetrixFieldMessage
import com.qeetgroup.qeetrix.components.input.QeetrixOption
import com.qeetgroup.qeetrix.components.input.QeetrixTextField
import com.qeetgroup.qeetrix.components.navigation.QeetrixBackButton
import com.qeetgroup.qeetrix.components.navigation.QeetrixNavigationItem
import com.qeetgroup.qeetrix.components.navigation.QeetrixNavigationScaffold
import com.qeetgroup.qeetrix.components.navigation.QeetrixTopAppBar
import com.qeetgroup.qeetrix.components.radiogroup.QeetrixRadioGroup
import com.qeetgroup.qeetrix.components.switchcontrol.QeetrixSwitch
import com.qeetgroup.qeetrix.components.tabs.QeetrixTab
import com.qeetgroup.qeetrix.components.tabs.QeetrixTabs
import com.qeetgroup.qeetrix.components.toast.QeetrixSnackbarHost
import com.qeetgroup.qeetrix.components.toast.QeetrixSnackbarResult
import com.qeetgroup.qeetrix.components.toast.QeetrixSnackbarType
import com.qeetgroup.qeetrix.components.toast.rememberQeetrixSnackbarHostState
import com.qeetgroup.qeetrix.components.typography.QeetrixText
import com.qeetgroup.qeetrix.components.typography.QeetrixTextVariant
import com.qeetgroup.qeetrix.foundation.spacing.QeetrixSpacing
import com.qeetgroup.qeetrix.icons.QeetrixIconName
import com.qeetgroup.qeetrix.theme.QeetrixTheme
import kotlinx.coroutines.launch

/** The workspace's top-level destinations — the sample's own route type. */
enum class WorkspaceDestination {
    Projects,
    Inbox,
    Settings,
}

private val Destinations =
    listOf(
        QeetrixNavigationItem(WorkspaceDestination.Projects, "Projects", QeetrixIconName.House),
        QeetrixNavigationItem(WorkspaceDestination.Inbox, "Inbox", QeetrixIconName.Inbox, badge = "2"),
        QeetrixNavigationItem(WorkspaceDestination.Settings, "Settings", QeetrixIconName.Settings),
    )

private val StatusFilter =
    listOf(QeetrixOption("all", "All projects"), QeetrixOption("active", "Active"), QeetrixOption("archived", "Archived"))

/**
 * Every Phase 4 component in one generic flow, with all navigation state owned here, as an app owns it:
 * choose a destination; open a project; its menu renames it (a dialog with a form) or deletes it (a
 * confirmation, then a snackbar with Undo); filter projects in a bottom sheet; Back closes the project, then
 * leaves the screen ([onExit]). Synthetic data only.
 */
@Composable
fun WorkspaceScreen(onExit: () -> Unit) {
    var destination by rememberSaveable { mutableStateOf(WorkspaceDestination.Projects) }
    var openProject by rememberSaveable { mutableStateOf<String?>(null) }
    val projects = remember { mutableStateListOf("Website refresh", "Quarterly report", "Onboarding guide") }
    val snackbars = rememberQeetrixSnackbarHostState()
    val scope = rememberCoroutineScope()

    // Back closes the open project first; on the list, the sample's own Back leaves the screen.
    BackHandler(enabled = openProject != null) { openProject = null }

    QeetrixNavigationScaffold(
        Destinations,
        selected = destination,
        onSelect = {
            destination = it
            openProject = null
        },
        modifier = Modifier.fillMaxSize().background(QeetrixTheme.colors.surfaceCanvas),
    ) {
        Box(Modifier.fillMaxSize()) {
            when (destination) {
                WorkspaceDestination.Projects -> {
                    val project = openProject
                    if (project == null) {
                        ProjectList(projects, onOpen = { openProject = it }, onExit = onExit)
                    } else {
                        ProjectDetail(
                            name = project,
                            onBack = { openProject = null },
                            onRename = { renamed ->
                                projects[projects.indexOf(project)] = renamed
                                openProject = renamed
                                scope.launch { snackbars.show("Project renamed", QeetrixSnackbarType.Success) }
                            },
                            onDelete = {
                                val index = projects.indexOf(project)
                                projects.removeAt(index)
                                openProject = null
                                scope.launch {
                                    val result = snackbars.show("Project deleted", description = project, actionLabel = "Undo")
                                    val undone = result == QeetrixSnackbarResult.ActionPerformed
                                    if (undone) projects.add(index.coerceAtMost(projects.size), project)
                                }
                            },
                        )
                    }
                }

                WorkspaceDestination.Inbox -> {
                    Inbox()
                }

                WorkspaceDestination.Settings -> {
                    WorkspaceSettings()
                }
            }
            QeetrixSnackbarHost(snackbars, Modifier.align(Alignment.BottomCenter))
        }
    }
}

@Composable
private fun ProjectList(
    projects: List<String>,
    onOpen: (String) -> Unit,
    onExit: () -> Unit,
) {
    var filtering by rememberSaveable { mutableStateOf(false) }
    var status by rememberSaveable { mutableStateOf("all") }
    var draft by rememberSaveable { mutableStateOf("all") }
    val scroll = rememberScrollState()
    Column(Modifier.fillMaxSize()) {
        QeetrixTopAppBar(
            title = "Projects",
            subtitle = StatusFilter.first { it.value == status }.label,
            navigationIcon = { QeetrixBackButton(onClick = onExit) },
            actions = {
                QeetrixButton("Filter", onClick = {
                    draft = status
                    filtering = true
                }, variant = QeetrixButtonVariant.Ghost)
            },
            scrolled = scroll.canScrollBackward,
        )
        Column(
            Modifier.verticalScroll(scroll).padding(QeetrixSpacing.L),
            verticalArrangement = Arrangement.spacedBy(QeetrixSpacing.M),
        ) {
            for (project in projects) {
                QeetrixCard(onClick = { onOpen(project) }, size = QeetrixCardSize.Sm, title = project, description = "Updated this week")
            }
            if (projects.isEmpty()) {
                QeetrixEmptyState("No projects", description = "Deleted projects can be restored from the snackbar's Undo.")
            }
        }
    }
    QeetrixBottomSheet(
        visible = filtering,
        onDismissRequest = { filtering = false },
        title = "Filter projects",
        footer = {
            QeetrixButton("Show projects", onClick = {
                status = draft
                filtering = false
            }, modifier = Modifier.fillMaxWidth())
        },
    ) {
        QeetrixRadioGroup(draft, { draft = it }, StatusFilter, label = "Status")
    }
}

@Composable
private fun ProjectDetail(
    name: String,
    onBack: () -> Unit,
    onRename: (String) -> Unit,
    onDelete: () -> Unit,
) {
    var menu by remember { mutableStateOf(false) }
    var renaming by rememberSaveable { mutableStateOf(false) }
    var deleting by rememberSaveable { mutableStateOf(false) }
    Column(Modifier.fillMaxSize()) {
        QeetrixTopAppBar(
            title = name,
            navigationIcon = { QeetrixBackButton(onClick = onBack) },
            actions = {
                QeetrixMenu(
                    expanded = menu,
                    onExpandedChange = { menu = it },
                    trigger = { QeetrixIconButton(QeetrixIconName.EllipsisVertical, "More options", onClick = { menu = true }) },
                ) {
                    QeetrixMenuItem("Rename", onClick = { renaming = true }, icon = QeetrixIconName.Pencil)
                    QeetrixMenuSeparator()
                    QeetrixMenuItem(
                        "Delete project",
                        onClick = { deleting = true },
                        icon = QeetrixIconName.Trash,
                        variant = QeetrixMenuItemVariant.Destructive,
                    )
                }
            },
        )
        Column(Modifier.padding(QeetrixSpacing.L), verticalArrangement = Arrangement.spacedBy(QeetrixSpacing.M)) {
            QeetrixText("Three open tasks and one review due this week.")
            QeetrixText("Use the menu to rename or delete the project.", variant = QeetrixTextVariant.Muted)
        }
    }
    if (renaming) {
        val field = rememberTextFieldState(name)
        var error by rememberSaveable { mutableStateOf<String?>(null) }
        QeetrixDialog(
            title = "Rename project",
            onDismissRequest = { renaming = false },
            footer = {
                QeetrixButton("Cancel", onClick = { renaming = false }, variant = QeetrixButtonVariant.Outline)
                QeetrixButton("Save", onClick = {
                    val value = field.text.trim().toString()
                    if (value.isEmpty()) {
                        error = "Enter a name."
                    } else {
                        renaming = false
                        onRename(value)
                    }
                })
            },
        ) {
            QeetrixTextField(field, label = "Name", required = true, message = error?.let { QeetrixFieldMessage(it) })
        }
    }
    if (deleting) {
        QeetrixConfirmationDialog(
            title = "Delete $name?",
            message = "Its tasks and files are removed for everyone in the workspace.",
            confirmLabel = "Delete project",
            destructive = true,
            onConfirm = onDelete,
            onDismissRequest = { deleting = false },
        )
    }
}

@Composable
private fun Inbox() {
    var tab by rememberSaveable { mutableStateOf("unread") }
    Column(Modifier.fillMaxSize()) {
        QeetrixTopAppBar(title = "Inbox")
        Column(Modifier.padding(horizontal = QeetrixSpacing.L), verticalArrangement = Arrangement.spacedBy(QeetrixSpacing.M)) {
            QeetrixTabs(listOf(QeetrixTab("unread", "Unread"), QeetrixTab("all", "All")), selected = tab, onSelect = { tab = it })
            if (tab == "unread") {
                QeetrixText("Ada mentioned you in Website refresh.")
                QeetrixText("The quarterly report is ready for review.")
            } else {
                QeetrixText("No other messages.", variant = QeetrixTextVariant.Muted)
            }
        }
    }
}

@Composable
private fun WorkspaceSettings() {
    var digest by rememberSaveable { mutableStateOf(true) }
    Column(Modifier.fillMaxSize()) {
        QeetrixTopAppBar(title = "Settings")
        Column(Modifier.padding(QeetrixSpacing.L)) {
            QeetrixSwitch(digest, { digest = it }, label = "Weekly digest", description = "A summary of the workspace every Monday.")
        }
    }
}
