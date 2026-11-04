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
import com.qeetgroup.qeetrix.components.button.QeetrixButtonTone
import com.qeetgroup.qeetrix.components.button.QeetrixButtonVariant
import com.qeetgroup.qeetrix.components.button.QeetrixIconButton
import com.qeetgroup.qeetrix.components.dialog.QeetrixAlertDialog
import com.qeetgroup.qeetrix.components.dialog.QeetrixConfirmationDialog
import com.qeetgroup.qeetrix.components.dialog.QeetrixDialog
import com.qeetgroup.qeetrix.components.dialog.QeetrixDialogAction
import com.qeetgroup.qeetrix.components.dialog.QeetrixDialogActionRole
import com.qeetgroup.qeetrix.components.dialog.QeetrixDialogIntent
import com.qeetgroup.qeetrix.components.drawer.QeetrixBottomSheet
import com.qeetgroup.qeetrix.components.dropdownmenu.QeetrixMenu
import com.qeetgroup.qeetrix.components.dropdownmenu.QeetrixMenuItem
import com.qeetgroup.qeetrix.components.dropdownmenu.QeetrixMenuItemTone
import com.qeetgroup.qeetrix.components.dropdownmenu.QeetrixMenuSeparator
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
 * The Phase 4 examples in the component pages in `docs/components` (and one that was in README.md), as written
 * there — each overlay shown — so they keep compiling against the public API and composing. Only the
 * surrounding state and the app's own functions are added, with a field or a filter where a page writes
 * "the form's fields" or "the filters". Values are synthetic.
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
            "Settings", "New message", "Profile", "Sessions", "Invoice INV-2041", "Draft", "More options", "Inbox",
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

private enum class Section { Profile, Sessions }

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
        value = destination,
        onValueChange = { destination = it },
    )
}

// NavigationRail.md
@Composable
private fun NavigationRailExample() {
    var destination by rememberSaveable { mutableStateOf(Destination.Home) }
    QeetrixNavigationRail(items, value = destination, onValueChange = { destination = it }) {
        QeetrixIconButton(QeetrixIconName.Plus, "New message", onClick = ::composeMessage, variant = QeetrixButtonVariant.Solid)
    }
}

// NavigationScaffold.md
@Composable
private fun NavigationScaffoldExample() {
    var destination by rememberSaveable { mutableStateOf(Destination.Home) }
    QeetrixNavigationScaffold(items, value = destination, onValueChange = { destination = it }) {
        when (destination) {
            Destination.Home -> HomeScreen()
            Destination.Inbox -> InboxScreen()
            Destination.Settings -> SettingsScreen()
        }
    }
}

// Navigation with snackbars (was in README.md)
@Composable
private fun ReadmeNavigation() {
    var destination by rememberSaveable { mutableStateOf(Destination.Inbox) }
    val destinations = items
    val snackbars = rememberQeetrixSnackbarHostState()
    val scope = rememberCoroutineScope()
    QeetrixNavigationScaffold(destinations, value = destination, onValueChange = { destination = it }) {
        Box(Modifier.fillMaxSize()) {
            InboxScreen(onArchive = { scope.launch { snackbars.show("Conversation archived", actionLabel = "Undo") } })
            QeetrixSnackbarHost(snackbars, Modifier.align(Alignment.BottomCenter))
        }
    }
}

// TopAppBar.md
@Composable
private fun TopAppBarExample() {
    val navigateUp = {}
    QeetrixTopAppBar(
        title = "Invoice INV-2041",
        description = "Draft",
        navigationIcon = { QeetrixBackButton(onClick = navigateUp) },
        actions = { QeetrixIconButton(QeetrixIconName.Share2, "Share", onClick = ::share, tone = QeetrixButtonTone.Neutral) },
    )
}

// Tabs.md
@Composable
private fun TabsExample() {
    var section by rememberSaveable { mutableStateOf(Section.Profile) }
    QeetrixTabs(
        tabs = listOf(
            QeetrixTab(Section.Profile, "Profile", QeetrixIconName.UserRound),
            QeetrixTab(Section.Sessions, "Sessions", badge = 3),
        ),
        value = section,
        onValueChange = { section = it },
    )
    when (section) {
        Section.Profile -> ProfilePanel()
        Section.Sessions -> SessionsPanel()
    }
}

// Menu.md
@Composable
private fun MenuExample() {
    var open by remember { mutableStateOf(false) }
    QeetrixMenu(
        open = open,
        onOpenChange = { open = it },
        trigger = { QeetrixIconButton(QeetrixIconName.EllipsisVertical, "More options", onClick = { open = true }) },
    ) {
        QeetrixMenuItem("Rename", onClick = ::rename, icon = QeetrixIconName.Pencil)
        QeetrixMenuItem("Duplicate", onClick = ::duplicate, icon = QeetrixIconName.Copy, shortcut = "Ctrl+D")
        QeetrixMenuSeparator()
        QeetrixMenuItem("Delete", onClick = ::confirmDelete, icon = QeetrixIconName.Trash, tone = QeetrixMenuItemTone.Danger)
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
    QeetrixAlertDialog(
        open = confirmDelete,
        onOpenChange = { confirmDelete = it },
        title = "Delete this workspace?",
        description = "Its projects and files are removed for everyone.",
        actions = listOf(QeetrixDialogAction("Delete workspace", QeetrixDialogActionRole.Destructive) { deleteWorkspace() }),
    )
}

// ConfirmationDialog.md
@Composable
private fun ConfirmationDialogExample() {
    var confirmRemoval by remember { mutableStateOf(true) }
    val ada = "Ada"
    QeetrixConfirmationDialog(
        open = confirmRemoval,
        onOpenChange = { confirmRemoval = it },
        title = "Remove Ada from the team?",
        description = "They lose access to the team's projects straight away.",
        confirmLabel = "Remove",
        onConfirm = { removeMember(ada) },
        intent = QeetrixDialogIntent.Danger,
    )
}

// Dialog.md
@Composable
private fun DialogExample() {
    var renaming by remember { mutableStateOf(true) }
    val saving = false
    val name = rememberTextFieldState()
    QeetrixDialog(
        open = renaming,
        onOpenChange = { renaming = it },
        title = "Rename workspace",
        description = "Everyone in the workspace sees the new name.",
        footer = {
            QeetrixButton("Cancel", onClick = { renaming = false }, variant = QeetrixButtonVariant.Ghost)
            QeetrixButton("Save", onClick = ::save, loading = saving)
        },
    ) {
        // the form's fields
        QeetrixTextField(name, label = "Name", required = true)
    }
}

// BottomSheet.md
@Composable
private fun BottomSheetExample() {
    var filtering by rememberSaveable { mutableStateOf(true) }
    var status by remember { mutableStateOf("all") }
    val statusOptions = listOf(QeetrixOption("all", "All invoices"), QeetrixOption("paid", "Paid"))
    QeetrixBottomSheet(
        open = filtering,
        onOpenChange = { filtering = it },
        title = "Filter invoices",
        footer = {
            QeetrixButton("Clear", onClick = ::clear, variant = QeetrixButtonVariant.Ghost)
            QeetrixButton("Show results", onClick = { filtering = false })
        },
    ) {
        // the filters
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
private fun ProfilePanel() = QeetrixText("Profile panel")

@Composable
private fun SessionsPanel() = QeetrixText("Sessions panel")

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

private fun clear() = Unit

private fun archive(conversation: String) = check(conversation.isNotEmpty())

private fun unarchive(conversation: String) = check(conversation.isNotEmpty())
