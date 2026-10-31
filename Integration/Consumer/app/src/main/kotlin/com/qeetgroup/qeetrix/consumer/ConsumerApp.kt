package com.qeetgroup.qeetrix.consumer

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.qeetgroup.qeetrix.components.badge.QeetrixBadge
import com.qeetgroup.qeetrix.components.badge.QeetrixBadgeTone
import com.qeetgroup.qeetrix.components.badge.QeetrixChipGroup
import com.qeetgroup.qeetrix.components.button.QeetrixButton
import com.qeetgroup.qeetrix.components.button.QeetrixButtonSize
import com.qeetgroup.qeetrix.components.button.QeetrixButtonTone
import com.qeetgroup.qeetrix.components.button.QeetrixButtonVariant
import com.qeetgroup.qeetrix.components.checkbox.QeetrixCheckbox
import com.qeetgroup.qeetrix.components.dialog.QeetrixAlertDialog
import com.qeetgroup.qeetrix.components.dialog.QeetrixConfirmationDialog
import com.qeetgroup.qeetrix.components.dialog.QeetrixDialogAction
import com.qeetgroup.qeetrix.components.dialog.QeetrixDialogActionRole
import com.qeetgroup.qeetrix.components.dialog.QeetrixDialogIntent
import com.qeetgroup.qeetrix.components.drawer.QeetrixBottomSheet
import com.qeetgroup.qeetrix.components.dropdownmenu.QeetrixMenu
import com.qeetgroup.qeetrix.components.dropdownmenu.QeetrixMenuItem
import com.qeetgroup.qeetrix.components.emptystate.QeetrixEmptyState
import com.qeetgroup.qeetrix.components.form.QeetrixFormActions
import com.qeetgroup.qeetrix.components.form.QeetrixFormError
import com.qeetgroup.qeetrix.components.form.QeetrixFormErrorSummary
import com.qeetgroup.qeetrix.components.input.QeetrixOption
import com.qeetgroup.qeetrix.components.input.QeetrixSearchField
import com.qeetgroup.qeetrix.components.input.QeetrixTextField
import com.qeetgroup.qeetrix.components.list.QeetrixListItem
import com.qeetgroup.qeetrix.components.list.QeetrixSectionHeader
import com.qeetgroup.qeetrix.components.navigation.QeetrixNavigationItem
import com.qeetgroup.qeetrix.components.navigation.QeetrixNavigationScaffold
import com.qeetgroup.qeetrix.components.navigation.QeetrixTopAppBar
import com.qeetgroup.qeetrix.components.numberfield.QeetrixNumberField
import com.qeetgroup.qeetrix.components.otpinput.QeetrixOTPInput
import com.qeetgroup.qeetrix.components.passwordinput.QeetrixPasswordAutoComplete
import com.qeetgroup.qeetrix.components.passwordinput.QeetrixPasswordInput
import com.qeetgroup.qeetrix.components.radiogroup.QeetrixRadioGroup
import com.qeetgroup.qeetrix.components.select.QeetrixSelect
import com.qeetgroup.qeetrix.components.switchcontrol.QeetrixSwitch
import com.qeetgroup.qeetrix.components.tabs.QeetrixTab
import com.qeetgroup.qeetrix.components.tabs.QeetrixTabs
import com.qeetgroup.qeetrix.components.toast.QeetrixSnackbarHost
import com.qeetgroup.qeetrix.components.toast.QeetrixSnackbarHostState
import com.qeetgroup.qeetrix.components.toast.QeetrixSnackbarResult
import com.qeetgroup.qeetrix.components.toast.QeetrixSnackbarTone
import com.qeetgroup.qeetrix.components.toast.rememberQeetrixSnackbarHostState
import com.qeetgroup.qeetrix.components.typography.QeetrixHeading
import com.qeetgroup.qeetrix.components.typography.QeetrixText
import com.qeetgroup.qeetrix.components.typography.QeetrixTextTone
import com.qeetgroup.qeetrix.foundation.spacing.QeetrixSpace
import com.qeetgroup.qeetrix.icons.QeetrixIcon
import com.qeetgroup.qeetrix.icons.QeetrixIconName
import com.qeetgroup.qeetrix.theme.QeetrixTheme
import com.qeetgroup.qeetrix.theme.QeetrixThemeMode
import kotlinx.coroutines.launch

/** The app's pages, one per area of the library a product uses. */
enum class Page(
    val title: String,
    val icon: QeetrixIconName,
) {
    Foundations("Foundations", QeetrixIconName.House),
    Form("Form", QeetrixIconName.Pencil),
    Overlays("Overlays", QeetrixIconName.Inbox),
    Collection("People", QeetrixIconName.Users),
}

private val Pages = Page.entries.map { QeetrixNavigationItem(it, it.title, it.icon) }

/**
 * The whole app: Qeetrix's theme around an adaptive navigation scaffold, with the theme, text size and layout
 * direction switchable at run time so tests can check each.
 */
@Composable
fun ConsumerApp() {
    var dark by rememberSaveable { mutableStateOf(false) }
    var largeText by rememberSaveable { mutableStateOf(false) }
    var rightToLeft by rememberSaveable { mutableStateOf(false) }
    var page by rememberSaveable { mutableStateOf(Page.Foundations) }
    val density = LocalDensity.current
    CompositionLocalProvider(
        LocalDensity provides Density(density.density, if (largeText) density.fontScale * 2f else density.fontScale),
        LocalLayoutDirection provides if (rightToLeft) LayoutDirection.Rtl else LayoutDirection.Ltr,
    ) {
        QeetrixTheme(if (dark) QeetrixThemeMode.Dark else QeetrixThemeMode.Light) {
            val snackbars = rememberQeetrixSnackbarHostState()
            Box(Modifier.fillMaxSize().background(QeetrixTheme.colors.surfaceCanvas)) {
                QeetrixNavigationScaffold(items = Pages, value = page, onValueChange = { page = it }) {
                    Box(Modifier.fillMaxSize()) {
                        Column(Modifier.fillMaxSize()) {
                            QeetrixTopAppBar(title = "Qeetrix consumer", description = page.title)
                            when (page) {
                                Page.Foundations ->
                                    FoundationsPage(
                                        dark = dark,
                                        onDarkChange = { dark = it },
                                        largeText = largeText,
                                        onLargeTextChange = { largeText = it },
                                        rightToLeft = rightToLeft,
                                        onRightToLeftChange = { rightToLeft = it },
                                    )
                                Page.Form -> FormPage(snackbars)
                                Page.Overlays -> OverlaysPage(snackbars)
                                Page.Collection -> CollectionPage()
                            }
                        }
                        QeetrixSnackbarHost(snackbars, Modifier.align(Alignment.BottomCenter), windowInsets = WindowInsets(0, 0, 0, 0))
                    }
                }
            }
        }
    }
}

@Composable
private fun PageColumn(content: @Composable () -> Unit) {
    Column(
        Modifier.fillMaxWidth().verticalScroll(rememberScrollState()).padding(QeetrixSpace.InsetLg),
        verticalArrangement = Arrangement.spacedBy(QeetrixSpace.GapMd),
    ) { content() }
}

@Composable
private fun FoundationsPage(
    dark: Boolean,
    onDarkChange: (Boolean) -> Unit,
    largeText: Boolean,
    onLargeTextChange: (Boolean) -> Unit,
    rightToLeft: Boolean,
    onRightToLeftChange: (Boolean) -> Unit,
) = PageColumn {
    val colors = QeetrixTheme.colors
    QeetrixHeading("Foundations", level = 2)
    // The theme's type roles drawn directly, so the fonts are the artifact's: Qeet Sans for body, Fira Code for code.
    BasicText("Body text in the Qeet typeface", style = QeetrixTheme.typography.bodyMd.copy(color = colors.textDefault))
    BasicText("val code = \"Fira Code\"", style = QeetrixTheme.typography.codeMd.copy(color = colors.textSubtle))
    Row(horizontalArrangement = Arrangement.spacedBy(QeetrixSpace.GapSm)) {
        Swatch("Brand", colors.bgBrandBold)
        Swatch("Canvas", colors.surfaceCanvas)
        Swatch("Raised", colors.surfaceRaised)
        Swatch("Text", colors.textDefault)
    }
    Row(horizontalArrangement = Arrangement.spacedBy(QeetrixSpace.GapSm), verticalAlignment = Alignment.CenterVertically) {
        QeetrixIcon(QeetrixIconName.House, contentDescription = "Home icon")
        QeetrixIcon(QeetrixIconName.Search, contentDescription = "Search icon")
        QeetrixIcon(QeetrixIconName.Send, contentDescription = "Send icon")
        QeetrixBadge("New", tone = QeetrixBadgeTone.Brand)
    }
    var clicks by remember { mutableIntStateOf(0) }
    QeetrixButton("Primary action", onClick = { clicks++ }, icon = QeetrixIconName.Check)
    QeetrixButton("Secondary action", onClick = {}, variant = QeetrixButtonVariant.Soft)
    QeetrixText("Pressed $clicks times", tone = QeetrixTextTone.Subtle)
    QeetrixTextField(rememberTextFieldState(), label = "Display name", placeholder = "e.g. Ada Lovelace")
    QeetrixSwitch(dark, onDarkChange, label = "Dark theme")
    QeetrixSwitch(largeText, onLargeTextChange, label = "Large text")
    QeetrixSwitch(rightToLeft, onRightToLeftChange, label = "Right to left")
}

@Composable
private fun Swatch(
    name: String,
    color: Color,
) = Box(Modifier.size(40.dp).background(color).semantics { contentDescription = "$name swatch" })

private val Countries = listOf(QeetrixOption("in", "India"), QeetrixOption("de", "Germany"), QeetrixOption("eg", "Egypt"))
private val Plans = listOf(QeetrixOption("team", "Team"), QeetrixOption("business", "Business", "Up to 200 seats"))

@Composable
private fun FormPage(snackbars: QeetrixSnackbarHostState) =
    PageColumn {
        val scope = rememberCoroutineScope()
        val email = rememberTextFieldState()
        val password = rememberTextFieldState()
        val code = rememberTextFieldState()
        var country by rememberSaveable { mutableStateOf("in") }
        var plan by rememberSaveable { mutableStateOf("team") }
        var seats by rememberSaveable { mutableStateOf<Int?>(5) }
        var terms by rememberSaveable { mutableStateOf(false) }
        var submitted by remember { mutableStateOf(false) }
        val emailError = submitted && email.text.isBlank()
        val termsError = submitted && !terms
        val errors =
            buildList {
                if (emailError) add(QeetrixFormError("Enter your work email", onClick = {}))
                if (termsError) add(QeetrixFormError("Accept the terms to continue", onClick = {}))
            }
        QeetrixHeading("Create an account", level = 3)
        if (errors.isNotEmpty()) QeetrixFormErrorSummary(errors)
        QeetrixTextField(
            email,
            label = "Work email",
            error = if (emailError) "Enter your work email" else null,
            required = true,
        )
        QeetrixPasswordInput(password, label = "Password", autoComplete = QeetrixPasswordAutoComplete.NewPassword)
        QeetrixOTPInput(code, label = "Verification code")
        QeetrixSelect(country, { country = it }, Countries, label = "Country")
        QeetrixRadioGroup(plan, { plan = it }, Plans, label = "Plan")
        QeetrixNumberField(seats, { seats = it }, label = "Seats", min = 1, max = 50)
        QeetrixCheckbox(terms, { terms = it }, label = "I accept the terms", invalid = termsError)
        QeetrixFormActions {
            QeetrixButton(
                "Create account",
                onClick = {
                    submitted = true
                    if (email.text.isNotBlank() && terms) {
                        scope.launch { snackbars.show("Account created", tone = QeetrixSnackbarTone.Success) }
                    }
                },
                // A button that submits a form lines up with its fields.
                size = QeetrixButtonSize.Lg,
            )
        }
    }

@Composable
private fun OverlaysPage(snackbars: QeetrixSnackbarHostState) =
    PageColumn {
        val scope = rememberCoroutineScope()
        var status by remember { mutableStateOf("None") }
        var dialog by remember { mutableStateOf(false) }
        var confirm by remember { mutableStateOf(false) }
        var sheet by remember { mutableStateOf(false) }
        var menu by remember { mutableStateOf(false) }
        var tab by remember { mutableStateOf("overview") }
        QeetrixText("Last action: $status", tone = QeetrixTextTone.Subtle)
        val tabs = listOf(QeetrixTab("overview", "Overview"), QeetrixTab("activity", "Activity"))
        QeetrixTabs(tabs, value = tab, onValueChange = { tab = it })
        QeetrixText(if (tab == "overview") "Overview content" else "Activity content")
        // One solid and one soft button in the region; every other action is ghost (design rules §7).
        QeetrixButton("Open dialog", onClick = { dialog = true })
        QeetrixButton("Open sheet", onClick = { sheet = true }, variant = QeetrixButtonVariant.Ghost)
        QeetrixButton(
            "Delete workspace",
            onClick = { confirm = true },
            variant = QeetrixButtonVariant.Ghost,
            tone = QeetrixButtonTone.Danger,
        )
        QeetrixMenu(
            open = menu,
            onOpenChange = { menu = it },
            trigger = { QeetrixButton("More actions", onClick = { menu = true }, variant = QeetrixButtonVariant.Ghost) },
        ) {
            QeetrixMenuItem("Rename", onClick = { status = "Renamed" }, icon = QeetrixIconName.Pencil)
            QeetrixMenuItem("Duplicate", onClick = { status = "Duplicated" }, icon = QeetrixIconName.Copy)
        }
        QeetrixButton(
            "Show snackbar",
            onClick = {
                scope.launch {
                    val result = snackbars.show("Message archived", actionLabel = "Undo")
                    if (result == QeetrixSnackbarResult.ActionPerformed) status = "Undone"
                }
            },
            variant = QeetrixButtonVariant.Soft,
        )
        // Overlays are always composed; `open` shows them, and choosing an action closes them before it runs.
        QeetrixAlertDialog(
            open = dialog,
            onOpenChange = { dialog = it },
            title = "Archive project?",
            description = "It moves to the archive and can be restored.",
            actions =
                listOf(
                    QeetrixDialogAction("Cancel", QeetrixDialogActionRole.Cancel) {},
                    QeetrixDialogAction("Archive") { status = "Archived" },
                ),
        )
        QeetrixConfirmationDialog(
            open = confirm,
            onOpenChange = { confirm = it },
            title = "Delete workspace?",
            confirmLabel = "Delete",
            onConfirm = { status = "Deleted" },
            intent = QeetrixDialogIntent.Danger,
        )
        QeetrixBottomSheet(open = sheet, onOpenChange = { sheet = it }, title = "Share invoice") {
            QeetrixText("Anyone with the link can view this invoice.")
            QeetrixButton("Copy link", onClick = {
                status = "Link copied"
                sheet = false
            })
        }
    }

private data class Person(
    val id: String,
    val name: String,
    val role: String,
    val team: String,
)

private val People =
    listOf("Engineering", "Design", "Finance").flatMapIndexed { t, team ->
        listOf("Ada Lovelace", "Grace Hopper", "Alan Turing", "Katherine Johnson", "Edsger Dijkstra", "Barbara Liskov")
            .mapIndexed { i, name -> Person("$t-$i", "$name ${t + 1}", if (i % 3 == 0) "Admin" else "Member", team) }
    }

@Composable
private fun CollectionPage() {
    val query = rememberTextFieldState()
    var role by rememberSaveable { mutableStateOf("all") }
    var selected by rememberSaveable { mutableStateOf(setOf<String>()) }
    val text = query.text.toString().trim()
    val shown = People.filter { (text.isEmpty() || it.name.contains(text, ignoreCase = true)) && (role == "all" || it.role == role) }
    Column(
        Modifier.fillMaxSize().padding(horizontal = QeetrixSpace.InsetLg),
        verticalArrangement = Arrangement.spacedBy(QeetrixSpace.GapSm),
    ) {
        QeetrixSearchField(query, label = "Search people")
        QeetrixChipGroup(
            label = "Role",
            value = role,
            onValueChange = { role = it },
            options = listOf(QeetrixOption("all", "Everyone"), QeetrixOption("Admin", "Admins"), QeetrixOption("Member", "Members")),
        )
        if (shown.isEmpty()) {
            QeetrixEmptyState("No people found", icon = QeetrixIconName.SearchX, description = "Try another name.")
        } else {
            LazyColumn(Modifier.fillMaxSize()) {
                shown.groupBy { it.team }.forEach { (team, members) ->
                    item(key = "team-$team") { QeetrixSectionHeader(team) }
                    items(members, key = { it.id }) { person ->
                        QeetrixListItem(
                            title = person.name,
                            description = person.role,
                            selected = person.id in selected,
                            onClick = { selected = if (person.id in selected) selected - person.id else selected + person.id },
                        )
                    }
                }
            }
        }
    }
}
