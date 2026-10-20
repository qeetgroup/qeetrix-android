// The examples keep the documentation's compact layout, so they can be compared with it line by line.
@file:Suppress(
    "ktlint:standard:argument-list-wrapping",
    "ktlint:standard:function-naming",
    "ktlint:standard:multiline-expression-wrapping",
    "ktlint:standard:no-multi-spaces",
    "ktlint:standard:trailing-comma-on-call-site",
    "ktlint:standard:wrapping",
)

package com.qeetgroup.qeetrix.sample

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.hasContentDescription
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.isHeading
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.unit.dp
import com.qeetgroup.qeetrix.components.button.QeetrixButton
import com.qeetgroup.qeetrix.components.button.QeetrixButtonVariant
import com.qeetgroup.qeetrix.components.button.QeetrixIconButton
import com.qeetgroup.qeetrix.components.dialog.QeetrixAlertDialog
import com.qeetgroup.qeetrix.components.dialog.QeetrixConfirmationDialog
import com.qeetgroup.qeetrix.components.dialog.QeetrixDialog
import com.qeetgroup.qeetrix.components.dialog.QeetrixDialogAction
import com.qeetgroup.qeetrix.components.dialog.QeetrixDialogActionRole
import com.qeetgroup.qeetrix.components.drawer.QeetrixBottomSheet
import com.qeetgroup.qeetrix.components.dropdownmenu.QeetrixMenu
import com.qeetgroup.qeetrix.components.dropdownmenu.QeetrixMenuItem
import com.qeetgroup.qeetrix.components.dropdownmenu.QeetrixMenuItemVariant
import com.qeetgroup.qeetrix.components.dropdownmenu.QeetrixMenuSeparator
import com.qeetgroup.qeetrix.components.input.QeetrixFieldMessage
import com.qeetgroup.qeetrix.components.input.QeetrixOption
import com.qeetgroup.qeetrix.components.input.QeetrixTextField
import com.qeetgroup.qeetrix.components.navigation.QeetrixBackButton
import com.qeetgroup.qeetrix.components.navigation.QeetrixNavigationBar
import com.qeetgroup.qeetrix.components.navigation.QeetrixNavigationItem
import com.qeetgroup.qeetrix.components.navigation.QeetrixNavigationRail
import com.qeetgroup.qeetrix.components.navigation.QeetrixNavigationScaffold
import com.qeetgroup.qeetrix.components.navigation.QeetrixTopAppBar
import com.qeetgroup.qeetrix.components.radiogroup.QeetrixRadioGroup
import com.qeetgroup.qeetrix.components.tabs.QeetrixTab
import com.qeetgroup.qeetrix.components.tabs.QeetrixTabs
import com.qeetgroup.qeetrix.components.toast.QeetrixSnackbarHost
import com.qeetgroup.qeetrix.components.toast.QeetrixSnackbarResult
import com.qeetgroup.qeetrix.components.toast.rememberQeetrixSnackbarHostState
import com.qeetgroup.qeetrix.components.typography.QeetrixText
import com.qeetgroup.qeetrix.icons.QeetrixIconName
import com.qeetgroup.qeetrix.theme.QeetrixTheme
import kotlinx.coroutines.launch
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

/**
 * The Phase 4 examples in `README.md` and the component pages in `Documentation/Components`, as written
 * there — each overlay shown — so they keep compiling against the public API and composing. Only the
 * surrounding state and the app's own functions are added. Values are synthetic.
 */
@RunWith(RobolectricTestRunner::class)
class NavigationDocumentationExamplesTest {
    @get:Rule
    val compose = createComposeRule()

    @Test
    fun everyNavigationAndOverlayExampleComposes() {
        compose.setContent {
            QeetrixTheme {
                Column {
                    NavigationBarExample()
                    Box(Modifier.height(240.dp)) { NavigationRailExample() }
                    Box(Modifier.fillMaxWidth().height(240.dp)) { NavigationScaffoldExample() }
                    Box(Modifier.fillMaxWidth().height(240.dp)) { ReadmeNavigation() }
                    TopAppBarExample()
                    TabsExample()
                    MenuExample()
                    SnackbarExample()
                    AlertDialogExample()
                    ConfirmationDialogExample()
                    DialogExample()
                    BottomSheetExample()
                }
            }
        }
        for (text in listOf(
            "Settings", "New message", "Overview", "Invoice INV-2041", "More options", "Inbox",
            "Delete this workspace?", "Remove Ada from the team?", "Rename workspace", "Filter invoices",
        )) {
            val named = hasText(text, substring = true) or hasContentDescription(text, substring = true)
            val nodes = compose.onAllNodes(named, useUnmergedTree = true).fetchSemanticsNodes()
            assertTrue(text, nodes.isNotEmpty())
        }
        assertTrue(compose.onAllNodes(isHeading() and hasText("Filter invoices")).fetchSemanticsNodes().isNotEmpty())
    }
}

private enum class Destination { Home, Inbox, Settings }

private enum class Section { Overview, Activity }

private val items =
    listOf(
        QeetrixNavigationItem(Destination.Home, "Home", QeetrixIconName.House),
        QeetrixNavigationItem(Destination.Inbox, "Inbox", QeetrixIconName.Inbox),
        QeetrixNavigationItem(Destination.Settings, "Settings", QeetrixIconName.Settings),
    )

// NavigationBar.md
@Composable
private fun NavigationBarExample() {
    var destination by rememberSaveable { mutableStateOf(Destination.Home) }
    QeetrixNavigationBar(
        items = listOf(
            QeetrixNavigationItem(Destination.Home, "Home", QeetrixIconName.House),
            QeetrixNavigationItem(Destination.Inbox, "Inbox", QeetrixIconName.Inbox, badge = "3"),
            QeetrixNavigationItem(Destination.Settings, "Settings", QeetrixIconName.Settings),
        ),
        selected = destination,
        onSelect = { destination = it },
    )
}

// NavigationRail.md
@Composable
private fun NavigationRailExample() {
    var destination by rememberSaveable { mutableStateOf(Destination.Home) }
    QeetrixNavigationRail(items, selected = destination, onSelect = { destination = it }) {
        QeetrixIconButton(QeetrixIconName.Plus, "New message", onClick = ::composeMessage, variant = QeetrixButtonVariant.Default)
    }
}

// NavigationScaffold.md
@Composable
private fun NavigationScaffoldExample() {
    var destination by rememberSaveable { mutableStateOf(Destination.Home) }
    QeetrixNavigationScaffold(items, selected = destination, onSelect = { destination = it }) {
        when (destination) {
            Destination.Home -> HomeScreen()
            Destination.Inbox -> InboxScreen()
            Destination.Settings -> SettingsScreen()
        }
    }
}

// README.md, "Components"
@Composable
private fun ReadmeNavigation() {
    var destination by rememberSaveable { mutableStateOf(Destination.Inbox) }
    val destinations = items
    val snackbars = rememberQeetrixSnackbarHostState()
    val scope = rememberCoroutineScope()
    QeetrixNavigationScaffold(destinations, selected = destination, onSelect = { destination = it }) {
        Box(Modifier.fillMaxSize()) {
            InboxScreen(onArchive = { scope.launch { snackbars.show("Conversation archived", actionLabel = "Undo") } })
            QeetrixSnackbarHost(snackbars, Modifier.align(Alignment.BottomCenter))
        }
    }
}

// TopAppBar.md
@Composable
private fun TopAppBarExample() {
    val listState = rememberLazyListState()
    val navigateUp = {}
    QeetrixTopAppBar(
        title = "Invoice INV-2041",
        subtitle = "Draft",
        navigationIcon = { QeetrixBackButton(onClick = navigateUp) },
        actions = { QeetrixIconButton(QeetrixIconName.Share2, "Share", onClick = ::share) },
        scrolled = listState.canScrollBackward,
    )
}

// Tabs.md
@Composable
private fun TabsExample() {
    var section by rememberSaveable { mutableStateOf(Section.Overview) }
    QeetrixTabs(
        tabs = listOf(QeetrixTab(Section.Overview, "Overview"), QeetrixTab(Section.Activity, "Activity", QeetrixIconName.Bell)),
        selected = section,
        onSelect = { section = it },
    )
    when (section) {
        Section.Overview -> OverviewPanel()
        Section.Activity -> ActivityPanel()
    }
}

// Menu.md
@Composable
private fun MenuExample() {
    var open by remember { mutableStateOf(false) }
    QeetrixMenu(
        expanded = open,
        onExpandedChange = { open = it },
        trigger = { QeetrixIconButton(QeetrixIconName.EllipsisVertical, "More options", onClick = { open = true }) },
    ) {
        QeetrixMenuItem("Rename", onClick = ::rename, icon = QeetrixIconName.Pencil)
        QeetrixMenuItem("Duplicate", onClick = ::duplicate, icon = QeetrixIconName.Copy)
        QeetrixMenuSeparator()
        QeetrixMenuItem("Delete", onClick = ::confirmDelete, icon = QeetrixIconName.Trash, variant = QeetrixMenuItemVariant.Destructive)
    }
}

// Snackbar.md
@Composable
private fun SnackbarExample() {
    val snackbars = rememberQeetrixSnackbarHostState()
    val scope = rememberCoroutineScope()
    Box(Modifier.fillMaxWidth().height(120.dp)) {
        Box(Modifier.fillMaxSize()) {
            InboxList(onArchive = { conversation ->
                archive(conversation)
                scope.launch {
                    val result = snackbars.show("Conversation archived", actionLabel = "Undo")
                    if (result == QeetrixSnackbarResult.ActionPerformed) unarchive(conversation)
                }
            })
            QeetrixSnackbarHost(snackbars, Modifier.align(Alignment.BottomCenter))
        }
    }
}

// AlertDialog.md
@Composable
private fun AlertDialogExample() {
    var confirmDelete by remember { mutableStateOf(true) }
    if (confirmDelete) {
        QeetrixAlertDialog(
            title = "Delete this workspace?",
            message = "Its projects and files are removed for everyone.",
            actions = listOf(QeetrixDialogAction("Delete workspace", QeetrixDialogActionRole.Destructive) { deleteWorkspace() }),
            onDismissRequest = { confirmDelete = false },
        )
    }
}

// ConfirmationDialog.md
@Composable
private fun ConfirmationDialogExample() {
    var confirmRemoval by remember { mutableStateOf(true) }
    val ada = "Ada"
    if (confirmRemoval) {
        QeetrixConfirmationDialog(
            title = "Remove Ada from the team?",
            message = "They lose access to the team's projects straight away.",
            confirmLabel = "Remove",
            destructive = true,
            onConfirm = { removeMember(ada) },
            onDismissRequest = { confirmRemoval = false },
        )
    }
}

// Dialog.md
@Composable
private fun DialogExample() {
    var renaming by remember { mutableStateOf(true) }
    val name = rememberTextFieldState()
    val nameMessage: QeetrixFieldMessage? = null
    if (renaming) {
        QeetrixDialog(
            title = "Rename workspace",
            description = "Everyone in the workspace sees the new name.",
            onDismissRequest = { renaming = false },
            footer = {
                QeetrixButton("Cancel", onClick = { renaming = false }, variant = QeetrixButtonVariant.Outline)
                QeetrixButton("Save", onClick = ::save)
            },
        ) {
            QeetrixTextField(name, label = "Name", required = true, message = nameMessage)
        }
    }
}

// BottomSheet.md
@Composable
private fun BottomSheetExample() {
    var filtering by rememberSaveable { mutableStateOf(true) }
    var status by remember { mutableStateOf("all") }
    val statusOptions = listOf(QeetrixOption("all", "All invoices"), QeetrixOption("paid", "Paid"))
    QeetrixBottomSheet(
        visible = filtering,
        onDismissRequest = { filtering = false },
        title = "Filter invoices",
        footer = { QeetrixButton("Show results", onClick = { filtering = false }, modifier = Modifier.fillMaxWidth()) },
    ) {
        QeetrixRadioGroup(status, { status = it }, statusOptions, label = "Status")
    }
}

@Composable
private fun HomeScreen() = QeetrixText("Home")

@Composable
private fun InboxScreen(onArchive: () -> Unit = {}) = QeetrixButton("Inbox", onClick = onArchive)

@Composable
private fun SettingsScreen() = QeetrixText("Settings")

@Composable
private fun OverviewPanel() = QeetrixText("Overview panel")

@Composable
private fun ActivityPanel() = QeetrixText("Activity panel")

@Composable
private fun InboxList(onArchive: (String) -> Unit) = QeetrixButton("Archive", onClick = { onArchive("conversation-1") })

private fun composeMessage() = Unit

private fun share() = Unit

private fun rename() = Unit

private fun duplicate() = Unit

private fun confirmDelete() = Unit

private fun deleteWorkspace() = Unit

private fun removeMember(member: String) = check(member.isNotEmpty())

private fun save() = Unit

private fun archive(conversation: String) = check(conversation.isNotEmpty())

private fun unarchive(conversation: String) = check(conversation.isNotEmpty())
