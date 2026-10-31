package com.qeetgroup.qeetrix.quality

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import com.qeetgroup.qeetrix.components.accordion.QeetrixAccordion
import com.qeetgroup.qeetrix.components.accordion.QeetrixAccordionItem
import com.qeetgroup.qeetrix.components.accordion.QeetrixCollapsiblePanel
import com.qeetgroup.qeetrix.components.accordion.QeetrixCollapsibleTrigger
import com.qeetgroup.qeetrix.components.actionbar.QeetrixBottomActionBar
import com.qeetgroup.qeetrix.components.alert.QeetrixAlert
import com.qeetgroup.qeetrix.components.alert.QeetrixAlertTone
import com.qeetgroup.qeetrix.components.alert.QeetrixAlertVariant
import com.qeetgroup.qeetrix.components.avatar.QeetrixAvatar
import com.qeetgroup.qeetrix.components.avatar.QeetrixAvatarGroup
import com.qeetgroup.qeetrix.components.avatar.QeetrixAvatarItem
import com.qeetgroup.qeetrix.components.avatar.QeetrixAvatarPerson
import com.qeetgroup.qeetrix.components.avatar.QeetrixAvatarShape
import com.qeetgroup.qeetrix.components.avatar.QeetrixAvatarSize
import com.qeetgroup.qeetrix.components.badge.QeetrixBadge
import com.qeetgroup.qeetrix.components.badge.QeetrixBadgeSize
import com.qeetgroup.qeetrix.components.badge.QeetrixBadgeTone
import com.qeetgroup.qeetrix.components.badge.QeetrixBadgeVariant
import com.qeetgroup.qeetrix.components.badge.QeetrixChip
import com.qeetgroup.qeetrix.components.badge.QeetrixChipGroup
import com.qeetgroup.qeetrix.components.badge.QeetrixChipSize
import com.qeetgroup.qeetrix.components.button.QeetrixButton
import com.qeetgroup.qeetrix.components.button.QeetrixButtonSize
import com.qeetgroup.qeetrix.components.button.QeetrixButtonTone
import com.qeetgroup.qeetrix.components.button.QeetrixButtonVariant
import com.qeetgroup.qeetrix.components.button.QeetrixIconButton
import com.qeetgroup.qeetrix.components.card.QeetrixCard
import com.qeetgroup.qeetrix.components.card.QeetrixCardTone
import com.qeetgroup.qeetrix.components.card.QeetrixCardVariant
import com.qeetgroup.qeetrix.components.checkbox.QeetrixCheckbox
import com.qeetgroup.qeetrix.components.checkbox.QeetrixCheckboxGroup
import com.qeetgroup.qeetrix.components.checklist.QeetrixCheckItem
import com.qeetgroup.qeetrix.components.checklist.QeetrixCheckList
import com.qeetgroup.qeetrix.components.checklist.QeetrixCheckStatus
import com.qeetgroup.qeetrix.components.copy.QeetrixCopyButton
import com.qeetgroup.qeetrix.components.copy.QeetrixCopyField
import com.qeetgroup.qeetrix.components.copy.QeetrixCopyFieldLayout
import com.qeetgroup.qeetrix.components.datepicker.QeetrixDateField
import com.qeetgroup.qeetrix.components.datepicker.QeetrixDateRangeField
import com.qeetgroup.qeetrix.components.datepicker.QeetrixTimeField
import com.qeetgroup.qeetrix.components.descriptionlist.QeetrixDescriptionItem
import com.qeetgroup.qeetrix.components.descriptionlist.QeetrixDescriptionList
import com.qeetgroup.qeetrix.components.descriptionlist.QeetrixDescriptionListLayout
import com.qeetgroup.qeetrix.components.dialog.DialogActions
import com.qeetgroup.qeetrix.components.dialog.DialogSurface
import com.qeetgroup.qeetrix.components.dialog.QeetrixAlertDialog
import com.qeetgroup.qeetrix.components.dialog.QeetrixConfirmationDialog
import com.qeetgroup.qeetrix.components.dialog.QeetrixDialog
import com.qeetgroup.qeetrix.components.dialog.QeetrixDialogAction
import com.qeetgroup.qeetrix.components.dialog.QeetrixDialogActionRole
import com.qeetgroup.qeetrix.components.dialog.QeetrixDialogIntent
import com.qeetgroup.qeetrix.components.dialog.QeetrixDialogSize
import com.qeetgroup.qeetrix.components.dialog.QeetrixFullScreenForm
import com.qeetgroup.qeetrix.components.dialog.arrangeActions
import com.qeetgroup.qeetrix.components.dialog.confirmationActions
import com.qeetgroup.qeetrix.components.drawer.DragHandle
import com.qeetgroup.qeetrix.components.drawer.QeetrixBottomSheet
import com.qeetgroup.qeetrix.components.drawer.SheetLayout
import com.qeetgroup.qeetrix.components.dropdownmenu.MenuSurface
import com.qeetgroup.qeetrix.components.dropdownmenu.QeetrixMenu
import com.qeetgroup.qeetrix.components.dropdownmenu.QeetrixMenuCheckboxItem
import com.qeetgroup.qeetrix.components.dropdownmenu.QeetrixMenuGroupLabel
import com.qeetgroup.qeetrix.components.dropdownmenu.QeetrixMenuItem
import com.qeetgroup.qeetrix.components.dropdownmenu.QeetrixMenuItemTone
import com.qeetgroup.qeetrix.components.dropdownmenu.QeetrixMenuRadioGroup
import com.qeetgroup.qeetrix.components.dropdownmenu.QeetrixMenuRadioItem
import com.qeetgroup.qeetrix.components.dropdownmenu.QeetrixMenuScope
import com.qeetgroup.qeetrix.components.dropdownmenu.QeetrixMenuSeparator
import com.qeetgroup.qeetrix.components.emptystate.QeetrixDataState
import com.qeetgroup.qeetrix.components.emptystate.QeetrixEmptyState
import com.qeetgroup.qeetrix.components.emptystate.QeetrixEmptyStateSize
import com.qeetgroup.qeetrix.components.emptystate.QeetrixEmptyStateTone
import com.qeetgroup.qeetrix.components.feed.QeetrixTimeline
import com.qeetgroup.qeetrix.components.feed.QeetrixTimelineGroup
import com.qeetgroup.qeetrix.components.feed.QeetrixTimelineItem
import com.qeetgroup.qeetrix.components.feed.QeetrixTimelineTone
import com.qeetgroup.qeetrix.components.feed.QeetrixTimelineVariant
import com.qeetgroup.qeetrix.components.fileupload.QeetrixAttachmentPicker
import com.qeetgroup.qeetrix.components.fileupload.QeetrixFileRow
import com.qeetgroup.qeetrix.components.form.QeetrixForm
import com.qeetgroup.qeetrix.components.form.QeetrixFormActions
import com.qeetgroup.qeetrix.components.form.QeetrixFormError
import com.qeetgroup.qeetrix.components.form.QeetrixFormErrorSummary
import com.qeetgroup.qeetrix.components.input.QeetrixChoiceOrientation
import com.qeetgroup.qeetrix.components.input.QeetrixChoiceVariant
import com.qeetgroup.qeetrix.components.input.QeetrixField
import com.qeetgroup.qeetrix.components.input.QeetrixFieldSet
import com.qeetgroup.qeetrix.components.input.QeetrixOption
import com.qeetgroup.qeetrix.components.input.QeetrixOptionGroup
import com.qeetgroup.qeetrix.components.input.QeetrixSearchField
import com.qeetgroup.qeetrix.components.input.QeetrixTextField
import com.qeetgroup.qeetrix.components.input.QeetrixTextarea
import com.qeetgroup.qeetrix.components.list.QeetrixListItem
import com.qeetgroup.qeetrix.components.list.QeetrixListItemIcon
import com.qeetgroup.qeetrix.components.list.QeetrixPullToRefresh
import com.qeetgroup.qeetrix.components.list.QeetrixSectionHeader
import com.qeetgroup.qeetrix.components.list.QeetrixSwipeAction
import com.qeetgroup.qeetrix.components.list.QeetrixSwipeActionTone
import com.qeetgroup.qeetrix.components.list.QeetrixSwipeActions
import com.qeetgroup.qeetrix.components.navigation.QeetrixBackButton
import com.qeetgroup.qeetrix.components.navigation.QeetrixNavigationBar
import com.qeetgroup.qeetrix.components.navigation.QeetrixNavigationItem
import com.qeetgroup.qeetrix.components.navigation.QeetrixNavigationLayout
import com.qeetgroup.qeetrix.components.navigation.QeetrixNavigationRail
import com.qeetgroup.qeetrix.components.navigation.QeetrixNavigationScaffold
import com.qeetgroup.qeetrix.components.navigation.QeetrixTopAppBar
import com.qeetgroup.qeetrix.components.navigation.QeetrixTopAppBarSize
import com.qeetgroup.qeetrix.components.numberfield.QeetrixNumberField
import com.qeetgroup.qeetrix.components.otpinput.QeetrixOTPCode
import com.qeetgroup.qeetrix.components.otpinput.QeetrixOTPCodeSize
import com.qeetgroup.qeetrix.components.otpinput.QeetrixOTPInput
import com.qeetgroup.qeetrix.components.otpinput.QeetrixOTPInputSize
import com.qeetgroup.qeetrix.components.otpinput.QeetrixOTPValidationType
import com.qeetgroup.qeetrix.components.pageindicator.QeetrixPageIndicator
import com.qeetgroup.qeetrix.components.passwordinput.QeetrixPasswordAutoComplete
import com.qeetgroup.qeetrix.components.passwordinput.QeetrixPasswordInput
import com.qeetgroup.qeetrix.components.permission.QeetrixPermission
import com.qeetgroup.qeetrix.components.permission.QeetrixPermissionPrimer
import com.qeetgroup.qeetrix.components.progress.QeetrixMeter
import com.qeetgroup.qeetrix.components.progress.QeetrixMeterTone
import com.qeetgroup.qeetrix.components.progress.QeetrixProgress
import com.qeetgroup.qeetrix.components.progress.QeetrixProgressRing
import com.qeetgroup.qeetrix.components.progress.QeetrixProgressRingSize
import com.qeetgroup.qeetrix.components.progress.QeetrixProgressTone
import com.qeetgroup.qeetrix.components.qrcode.QeetrixQRCode
import com.qeetgroup.qeetrix.components.radiogroup.QeetrixRadioGroup
import com.qeetgroup.qeetrix.components.relativetime.QeetrixRelativeTime
import com.qeetgroup.qeetrix.components.scanner.QeetrixQRScanner
import com.qeetgroup.qeetrix.components.scanner.QeetrixScannerState
import com.qeetgroup.qeetrix.components.segmented.QeetrixSegmentedControl
import com.qeetgroup.qeetrix.components.select.OptionListSurface
import com.qeetgroup.qeetrix.components.select.QeetrixSelect
import com.qeetgroup.qeetrix.components.select.QeetrixSelectSize
import com.qeetgroup.qeetrix.components.select.SelectSection
import com.qeetgroup.qeetrix.components.separator.QeetrixSeparator
import com.qeetgroup.qeetrix.components.separator.QeetrixSeparatorOrientation
import com.qeetgroup.qeetrix.components.separator.QeetrixSeparatorTone
import com.qeetgroup.qeetrix.components.slider.QeetrixRangeSlider
import com.qeetgroup.qeetrix.components.slider.QeetrixSlider
import com.qeetgroup.qeetrix.components.spinner.QeetrixSkeleton
import com.qeetgroup.qeetrix.components.spinner.QeetrixSkeletonSize
import com.qeetgroup.qeetrix.components.spinner.QeetrixSkeletonText
import com.qeetgroup.qeetrix.components.spinner.QeetrixSkeletonVariant
import com.qeetgroup.qeetrix.components.spinner.QeetrixSpinner
import com.qeetgroup.qeetrix.components.spinner.QeetrixSpinnerSize
import com.qeetgroup.qeetrix.components.spinner.QeetrixSpinnerTone
import com.qeetgroup.qeetrix.components.stat.QeetrixStat
import com.qeetgroup.qeetrix.components.stat.QeetrixStatGroup
import com.qeetgroup.qeetrix.components.stat.QeetrixStatTone
import com.qeetgroup.qeetrix.components.steps.QeetrixStep
import com.qeetgroup.qeetrix.components.steps.QeetrixStepStatus
import com.qeetgroup.qeetrix.components.steps.QeetrixSteps
import com.qeetgroup.qeetrix.components.steps.QeetrixStepsOrientation
import com.qeetgroup.qeetrix.components.switchcontrol.QeetrixSwitch
import com.qeetgroup.qeetrix.components.tabs.QeetrixTab
import com.qeetgroup.qeetrix.components.tabs.QeetrixTabPanel
import com.qeetgroup.qeetrix.components.tabs.QeetrixTabs
import com.qeetgroup.qeetrix.components.tabs.QeetrixTabsOrientation
import com.qeetgroup.qeetrix.components.tabs.QeetrixTabsVariant
import com.qeetgroup.qeetrix.components.toast.QeetrixSnackbar
import com.qeetgroup.qeetrix.components.toast.QeetrixSnackbarHost
import com.qeetgroup.qeetrix.components.toast.QeetrixSnackbarTone
import com.qeetgroup.qeetrix.components.toast.rememberQeetrixSnackbarHostState
import com.qeetgroup.qeetrix.components.typography.QeetrixHeading
import com.qeetgroup.qeetrix.components.typography.QeetrixHeadingSize
import com.qeetgroup.qeetrix.components.typography.QeetrixHeadingTone
import com.qeetgroup.qeetrix.components.typography.QeetrixText
import com.qeetgroup.qeetrix.components.typography.QeetrixTextSize
import com.qeetgroup.qeetrix.components.typography.QeetrixTextTone
import com.qeetgroup.qeetrix.components.typography.QeetrixTextWeight
import com.qeetgroup.qeetrix.foundation.typography.QeetrixTextRole
import com.qeetgroup.qeetrix.icons.QeetrixIcon
import com.qeetgroup.qeetrix.icons.QeetrixIconName
import com.qeetgroup.qeetrix.icons.QeetrixIconShape
import com.qeetgroup.qeetrix.icons.QeetrixIconVariant
import com.qeetgroup.qeetrix.theme.QeetrixTheme
import java.math.BigDecimal
import kotlin.time.Duration

/**
 * A named piece of UI the quality scans render: one component, or a few, in the states that carry accessibility and
 * layout risk. Values are synthetic.
 */
internal class QualitySpecimen(
    val name: String,
    val content: @Composable () -> Unit,
)

/**
 * Every public `Qeetrix*` composable of the library in the states that carry accessibility and layout risk —
 * disabled, read-only, selected, checked, invalid, loading, empty, long text, every variant, tone and size — drawn in
 * place. The quality scans run over all of it: touch areas and text clipping (`LayoutQualityTest`, and on a device
 * `QeetrixLayoutDeviceTest`), the Accessibility Test Framework (`QeetrixAccessibilityDeviceTest`).
 *
 * The overlays — dialogs, the bottom sheet, the menu and the select's list — open in windows of their own, which a
 * scan of one root cannot see and a screenshot cannot capture, so they are here as the surfaces those windows hold
 * (`DialogSurface`, `SheetLayout`, `MenuSurface`, `OptionListSurface`: the same parts the public composables draw);
 * [OverlayCatalog] opens the real windows. `QualityCatalogCoverageTest` fails when a public composable has no
 * specimen.
 */
internal val QualityCatalog: List<QualitySpecimen> =
    listOf(
        QualitySpecimen("text") {
            Column {
                QeetrixHeading("Account settings", level = 1)
                QeetrixHeading("Welcome to Qeet", level = 1, size = QeetrixHeadingSize.DisplayLg)
                QeetrixHeading("Security", level = 2, size = QeetrixHeadingSize.TitleSm, tone = QeetrixHeadingTone.Subtle)
                QeetrixText("Changes apply to every device signed in to this account.", tone = QeetrixTextTone.Subtle)
                QeetrixText("Last changed by Asha Rao", size = QeetrixTextSize.Sm, tone = QeetrixTextTone.Subtlest)
                QeetrixText("Plan: Team", size = QeetrixTextSize.Lg, weight = QeetrixTextWeight.Semibold)
                QeetrixText("inv_2K9QX7", mono = true)
                QeetrixText(AnnotatedString("Payment overdue by 3 days"), tone = QeetrixTextTone.Danger, weight = QeetrixTextWeight.Medium)
                for (tone in listOf(QeetrixTextTone.Brand, QeetrixTextTone.Success, QeetrixTextTone.Warning, QeetrixTextTone.Info)) {
                    QeetrixText("${tone.value} text", tone = tone)
                }
            }
        },
        QualitySpecimen("separator") {
            Column {
                for (tone in QeetrixSeparatorTone.entries) QeetrixSeparator(tone = tone)
                Row(Modifier.height(IntrinsicSize.Min)) {
                    QeetrixText("Start")
                    QeetrixSeparator(orientation = QeetrixSeparatorOrientation.Vertical)
                    QeetrixText("End")
                }
            }
        },
        QualitySpecimen("button") {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                for (tone in QeetrixButtonTone.entries) {
                    for (variant in QeetrixButtonVariant.entries) {
                        QeetrixButton("Save changes", onClick = {}, variant = variant, tone = tone)
                    }
                }
                for (size in QeetrixButtonSize.entries) QeetrixButton("Share", onClick = {}, size = size, icon = QeetrixIconName.Share2)
                QeetrixButton("Disabled", onClick = {}, enabled = false)
                QeetrixButton("Disabled", onClick = {}, variant = QeetrixButtonVariant.Ghost, enabled = false)
                QeetrixButton("Saving", onClick = {}, loading = true)
                QeetrixButton("Save draft", onClick = {}, variant = QeetrixButtonVariant.Soft, icon = QeetrixIconName.Check, loading = true)
                QeetrixButton("Continue", onClick = {
                }, size = QeetrixButtonSize.Lg, fullWidth = true, icon = QeetrixIconName.ChevronRight, iconMirrorsInRightToLeft = true)
            }
        },
        QualitySpecimen("icon-button") {
            Column {
                for (tone in QeetrixButtonTone.entries) {
                    for (variant in QeetrixButtonVariant.entries) {
                        QeetrixIconButton(
                            QeetrixIconName.Settings,
                            "Settings, ${variant.value} ${tone.value}",
                            onClick = {},
                            variant = variant,
                            tone = tone,
                        )
                    }
                }
                for (size in QeetrixButtonSize.entries) {
                    QeetrixIconButton(
                        QeetrixIconName.Bell,
                        "Alerts, ${size.value}",
                        onClick = {},
                        size = size,
                    )
                }
                QeetrixIconButton(QeetrixIconName.Trash, "Delete", onClick = {}, enabled = false)
                QeetrixIconButton(QeetrixIconName.Send, "Send", onClick = {}, variant = QeetrixButtonVariant.Solid, loading = true)
            }
        },
        QualitySpecimen("badge") {
            Column {
                for (variant in QeetrixBadgeVariant.entries) {
                    for (tone in QeetrixBadgeTone.entries) QeetrixBadge("${tone.value} ${variant.value}", tone = tone, variant = variant)
                }
                QeetrixBadge("Verified", tone = QeetrixBadgeTone.Success, icon = QeetrixIconName.CircleCheck)
                QeetrixBadge("3", size = QeetrixBadgeSize.Sm)
            }
        },
        QualitySpecimen("chip") {
            Column {
                for (size in QeetrixChipSize.entries) {
                    QeetrixChip("Unread", selected = false, onSelectedChange = {}, size = size)
                    QeetrixChip("Owner: Ada", onRemove = {}, size = size)
                }
                QeetrixChip("Starred", selected = true, onSelectedChange = {}, icon = QeetrixIconName.Bell)
                QeetrixChip("Archived", selected = false, onSelectedChange = {}, enabled = false)
                QeetrixChip("Removed", onRemove = {}, enabled = false)
                QeetrixChipGroup(
                    label = "Status",
                    value = "open",
                    onValueChange = {},
                    options =
                        listOf(
                            QeetrixOption("open", "Open"),
                            QeetrixOption("closed", "Closed"),
                            QeetrixOption("draft", "Draft", enabled = false),
                        ),
                )
                QeetrixChipGroup(
                    label = "Labels",
                    values = setOf("design"),
                    onValuesChange = {},
                    options = listOf(QeetrixOption("design", "Design"), QeetrixOption("legal", "Legal")),
                    size = QeetrixChipSize.Sm,
                )
            }
        },
        QualitySpecimen("card") {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                QeetrixCard(
                    title = "Workspace",
                    description = "3 members",
                    actions = { QeetrixButton("View all", onClick = {}, variant = QeetrixButtonVariant.Ghost) },
                    footer = {
                        QeetrixButton("Cancel", onClick = {}, variant = QeetrixButtonVariant.Ghost)
                        QeetrixButton("Manage", onClick = {})
                    },
                ) { QeetrixText("12 seats in use") }
                QeetrixCard(title = "Plan", actions = { QeetrixBadge("Pro", tone = QeetrixBadgeTone.Brand) })
                QeetrixCard(variant = QeetrixCardVariant.Subdued, title = "Subdued", description = "A secondary panel")
                QeetrixCard(
                    title = "Delete workspace",
                    description = "Removes every project and member. This can't be undone.",
                    tone = QeetrixCardTone.Danger,
                    footer = { QeetrixButton("Delete workspace", onClick = {}, tone = QeetrixButtonTone.Danger) },
                )
                QeetrixCard(onClick = {}, selected = true, title = "Team", description = "Up to 50 members, audit logs")
                QeetrixCard(onClick = {}, title = "Security", description = "Passkeys and sessions")
                QeetrixCard(onClick = {}, enabled = false, title = "Archived", description = "Read-only")
                QeetrixCard { QeetrixText("A card with only a body.") }
            }
        },
        QualitySpecimen("checkbox") {
            Column {
                QeetrixCheckbox(true, {}, label = "Remember this device", description = "For 30 days")
                QeetrixCheckbox(false, {}, label = "Keep me signed in", icon = QeetrixIconName.Lock)
                QeetrixCheckbox(false, {}, label = "All members", indeterminate = true)
                QeetrixCheckbox(false, {}, label = "Accept the terms", invalid = true)
                QeetrixCheckbox(true, {}, label = "Managed by your admin", readOnly = true)
                QeetrixCheckbox(true, {}, label = "Disabled", enabled = false)
                // A bare box in a row that names it, as the component asks.
                Box(
                    Modifier.toggleable(value = true, role = Role.Checkbox, onValueChange = {}).semantics {
                        contentDescription =
                            "Select row"
                    },
                ) {
                    QeetrixCheckbox(checked = true, onCheckedChange = null)
                }
                QeetrixCheckboxGroup(
                    setOf("email"),
                    {},
                    listOf(QeetrixOption("email", "Email"), QeetrixOption("sms", "SMS"), QeetrixOption("push", "Push", enabled = false)),
                    label = "Notify me by",
                    error = "Choose at least one way to reach you.",
                )
                QeetrixCheckboxGroup(
                    setOf("mon"),
                    {},
                    listOf("Mon", "Tue", "Wed").map { QeetrixOption(it.lowercase(), it) },
                    label = "Working days",
                    orientation = QeetrixChoiceOrientation.Horizontal,
                )
                QeetrixCheckboxGroup(
                    setOf("passkey"),
                    {},
                    listOf(
                        QeetrixOption("passkey", "Passkeys", "Face, fingerprint or screen lock", icon = QeetrixIconName.Lock),
                        QeetrixOption(
                            "sso",
                            "Single sign-on",
                            "Through the company's provider",
                            icon = QeetrixIconName.Users,
                            enabled = false,
                        ),
                    ),
                    label = "Sign-in methods",
                    variant = QeetrixChoiceVariant.Tile,
                )
            }
        },
        QualitySpecimen("radio-group") {
            Column {
                QeetrixRadioGroup(
                    "team",
                    {},
                    listOf(
                        QeetrixOption("starter", "Starter", "One person"),
                        QeetrixOption("team", "Team"),
                        QeetrixOption("enterprise", "Enterprise", enabled = false),
                    ),
                    label = "Plan",
                    description = "You can change it later.",
                )
                QeetrixRadioGroup<String?>(
                    null,
                    {},
                    listOf(QeetrixOption("en", "English"), QeetrixOption("hi", "Hindi")),
                    label = "Invoice language",
                    error = "Choose a language for invoices.",
                    required = true,
                )
                QeetrixRadioGroup(
                    "s",
                    {},
                    listOf(QeetrixOption("s", "Small"), QeetrixOption("m", "Medium"), QeetrixOption("l", "Large")),
                    label = "Size",
                    orientation = QeetrixChoiceOrientation.Horizontal,
                )
                QeetrixRadioGroup(
                    "web",
                    {},
                    listOf(
                        QeetrixOption("web", "Web app", "Users sign in through a redirect.", icon = QeetrixIconName.House),
                        QeetrixOption("service", "Service", "Machine to machine, no user.", icon = QeetrixIconName.Server),
                    ),
                    label = "Application type",
                    variant = QeetrixChoiceVariant.Tile,
                )
                QeetrixRadioGroup("monthly", {}, listOf(QeetrixOption("monthly", "Monthly")), label = "Read-only", readOnly = true)
                QeetrixRadioGroup("monthly", {}, listOf(QeetrixOption("monthly", "Monthly")), label = "Disabled", enabled = false)
            }
        },
        QualitySpecimen("switch") {
            Column {
                QeetrixSwitch(true, {}, label = "Email alerts", description = "A message when someone signs in")
                QeetrixSwitch(false, {}, label = "Managed by your organisation", enabled = false)
                QeetrixSwitch(true, {}, label = "Audit log", readOnly = true)
                QeetrixSwitch(false, {}, label = "Accept the terms", invalid = true)
            }
        },
        QualitySpecimen("field") {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                QeetrixField(label = "Notifications", description = "On every device", help = "Sent by email and push.") {
                    QeetrixSwitch(true, {}, Modifier.qeetrixFieldControl())
                }
                QeetrixField(label = "Accent colour", error = "Choose an accent colour.", required = true) {
                    QeetrixSwitch(false, {}, Modifier.qeetrixFieldControl(), invalid = true)
                }
                QeetrixField(label = "Logo", optional = true, enabled = false, description = "Disabled until the plan allows it.") {
                    QeetrixCheckbox(false, {}, Modifier.qeetrixFieldControl(), enabled = false)
                }
                QeetrixField(label = "Hidden label", hideLabel = true) { QeetrixSwitch(false, {}, Modifier.qeetrixFieldControl()) }
                QeetrixFieldSet("Billing address", description = "Where we send invoices.") {
                    QeetrixTextField(TextFieldState("12 MG Road"), label = "Address line 1")
                    QeetrixTextField(TextFieldState(), label = "Address line 2", optional = true)
                }
                QeetrixFieldSet("Disabled section", enabled = false) { QeetrixTextField(TextFieldState("Bengaluru"), label = "City") }
            }
        },
        QualitySpecimen("text-field") {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                QeetrixTextField(TextFieldState(), label = "Work email", placeholder = "e.g. asha@acme.in", required = true)
                QeetrixTextField(TextFieldState("asha@"), label = "Recovery email", error = "Enter an email address like name@company.com.")
                QeetrixTextField(TextFieldState("Asha Rao"), label = "Name", description = "As it appears on your PAN.", invalid = true)
                QeetrixTextField(TextFieldState("QX-2041"), label = "Order", readOnly = true)
                QeetrixTextField(TextFieldState("Qeet Example"), label = "Organisation", enabled = false, optional = true)
                QeetrixTextField(
                    TextFieldState("2,500"),
                    label = "Amount",
                    icon = QeetrixIconName.Search,
                    startAdornment = { QeetrixText("₹") },
                    endAdornment = { QeetrixText("per month") },
                    help = "Billed on the first of each month.",
                )
                QeetrixTextField(TextFieldState(), label = "Filter", hideLabel = true, placeholder = "e.g. overdue")
            }
        },
        QualitySpecimen("textarea") {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                QeetrixTextarea(TextFieldState("Line one\nLine two"), label = "Notes", description = "Shown to your team", rows = 3)
                QeetrixTextarea(
                    TextFieldState(),
                    label = "Delivery notes",
                    placeholder = "e.g. Gate code 4471",
                    optional = true,
                    maxRows = 4,
                )
                QeetrixTextarea(TextFieldState("Flat 4B"), label = "Address", error = "Add a PIN code to the address.", rows = 1)
                QeetrixTextarea(TextFieldState("Approved by finance."), label = "Audit note", readOnly = true, rows = 1)
                QeetrixTextarea(TextFieldState("Locked"), label = "Disabled", enabled = false, rows = 1)
            }
        },
        QualitySpecimen("password-input") {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                QeetrixPasswordInput(TextFieldState("Synthetic-Passw0rd"), label = "Password")
                QeetrixPasswordInput(
                    TextFieldState("Synthetic-Passw0rd"),
                    label = "New password",
                    autoComplete = QeetrixPasswordAutoComplete.NewPassword,
                    visible = true,
                    onVisibleChange = {},
                    description = "Use 12 characters or more.",
                )
                QeetrixPasswordInput(TextFieldState("short"), label = "Password", error = "Use at least 12 characters.")
                QeetrixPasswordInput(
                    TextFieldState("sk_live_synthetic"),
                    label = "Client secret",
                    readOnly = true,
                    autoComplete = QeetrixPasswordAutoComplete.Off,
                )
                QeetrixPasswordInput(TextFieldState("Synthetic-0042"), label = "Disabled", enabled = false)
            }
        },
        QualitySpecimen("otp-input") {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                QeetrixOTPInput(TextFieldState("12"), label = "Verification code", description = "Sent to •••• 4821")
                QeetrixOTPInput(TextFieldState("482105"), label = "Sign-in code", size = QeetrixOTPInputSize.Lg, groups = listOf(3, 3))
                QeetrixOTPInput(
                    TextFieldState("4821"),
                    label = "Failed attempt",
                    error = "That code has expired. Enter the new one we sent.",
                )
                QeetrixOTPInput(TextFieldState("12"), label = "Disabled", length = 4, enabled = false)
            }
        },
        QualitySpecimen("otp-input-long") {
            // Finding Q-15: ten slots in a compact phone are narrower than a wide character at 2× (the column is under
            // the 328 dp a [5, 5] code needs, QTRX-164).
            QeetrixOTPInput(
                TextFieldState("WM8KWX"),
                label = "Recovery code",
                length = 10,
                groups = listOf(5, 5),
                validationType = QeetrixOTPValidationType.Alphanumeric,
            )
        },
        QualitySpecimen("number-field") {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                QeetrixNumberField(4, {}, label = "Seats", min = 1, max = 50)
                QeetrixNumberField(1, {}, label = "At the minimum", min = 1, max = 50, description = "Decrease is off at the minimum.")
                QeetrixNumberField(BigDecimal("12.50"), {}, label = "Price", fractionDigits = 2, startAdornment = { QeetrixText("₹") })
                QeetrixNumberField(BigDecimal("1250000"), {}, label = "Amount", fractionDigits = 2, steppers = false)
                QeetrixNumberField(
                    value = null as Int?,
                    onValueChange = {},
                    label = "Session length",
                    placeholder = "e.g. 30",
                    endAdornment = { QeetrixText("minutes") },
                    error = "Enter a length between 5 and 120 minutes.",
                )
                QeetrixNumberField(30, {}, label = "Read-only", readOnly = true)
                QeetrixNumberField(30, {}, label = "Disabled", enabled = false)
            }
        },
        QualitySpecimen("select") {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                QeetrixSelect("in", {}, Countries, label = "Region")
                QeetrixSelect<String?>(
                    null,
                    {},
                    Countries,
                    label = "Billing country",
                    placeholder = "e.g. India",
                    error = "Choose a billing country.",
                )
                QeetrixSelect("gb", {}, Countries, label = "Long value", description = "Where your data is stored.")
                QeetrixSelect("ae", {}, Countries, label = "Read-only", readOnly = true)
                QeetrixSelect("in", {}, Countries, label = "Disabled", enabled = false)
                QeetrixSelect("in", {}, Countries, label = "Country, medium", size = QeetrixSelectSize.Md, hideLabel = true)
                QeetrixSelect("in", {}, Countries, label = "Country, small", size = QeetrixSelectSize.Sm, hideLabel = true)
                QeetrixSelect(
                    "in",
                    {},
                    listOf(QeetrixOptionGroup("Asia", Countries.take(2)), QeetrixOptionGroup("Europe", listOf(Countries[2]))),
                    label = "Grouped",
                )
            }
        },
        QualitySpecimen("select-list") {
            // QeetrixSelect's open list, drawn in place: its popup is a window of its own.
            OptionListSurface(
                listOf(SelectSection("Asia", Countries.take(2) + Countries.last()), SelectSection("Europe", listOf(Countries[2]))),
                "ae",
                focusSelected = false,
                title = "Country",
                onDismiss = {},
                onChoose = {},
                modifier = Modifier.widthIn(max = 280.dp),
            )
        },
        QualitySpecimen("search-field") {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                QeetrixSearchField(TextFieldState(), placeholder = "e.g. Asha")
                QeetrixSearchField(TextFieldState("Ada"), label = "Find a member")
                QeetrixSearchField(
                    TextFieldState("pay"),
                    endAdornment = { QeetrixText("12 found", size = QeetrixTextSize.Sm) },
                )
                QeetrixSearchField(TextFieldState("ada"), readOnly = true)
                QeetrixSearchField(TextFieldState("ada"), enabled = false)
            }
        },
        QualitySpecimen("form") {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                QeetrixForm(
                    errors =
                        listOf(
                            QeetrixFormError("Enter a GSTIN, like 27AAPFU0939F1ZV.") {},
                            QeetrixFormError("That PAN is already registered."),
                        ),
                    summaryTitle = "Fix these to continue",
                ) {
                    QeetrixTextField(TextFieldState(), label = "GSTIN", error = "Enter a GSTIN, like 27AAPFU0939F1ZV.")
                    QeetrixTextField(TextFieldState("AAPFU0939F"), label = "PAN", error = "That PAN is already registered.")
                    QeetrixFormActions {
                        QeetrixButton("Cancel", onClick = {}, variant = QeetrixButtonVariant.Ghost, size = QeetrixButtonSize.Lg)
                        QeetrixButton("Save", onClick = {}, size = QeetrixButtonSize.Lg)
                    }
                }
                QeetrixFormErrorSummary(
                    listOf(QeetrixFormError("Enter your work email", onClick = {}), QeetrixFormError("Choose a plan", onClick = {})),
                    autoFocus = false,
                )
            }
        },
        QualitySpecimen("accordion") {
            Column {
                QeetrixAccordion(value = listOf("billing"), onValueChange = {}) {
                    QeetrixAccordionItem("billing", "How am I billed?") { QeetrixText("Monthly, on the date you subscribed.") }
                    QeetrixAccordionItem("notifications", "Notifications") {}
                    QeetrixAccordionItem("long", "A question long enough to wrap onto a second line of the trigger") {}
                    QeetrixAccordionItem("export", "Data export", enabled = false) {}
                }
                QeetrixAccordion(value = listOf("a", "b"), onValueChange = {}, multiple = true, enabled = false) {
                    QeetrixAccordionItem("a", "Disabled accordion") { QeetrixText("Open, but every section is disabled.") }
                }
                QeetrixCollapsibleTrigger("Advanced options", open = true, onOpenChange = {})
                QeetrixCollapsiblePanel(open = true) { QeetrixText("Webhook signing secret: rotate every 90 days.") }
                QeetrixCollapsibleTrigger("Show more", open = false, onOpenChange = {}, enabled = false)
            }
        },
        QualitySpecimen("avatar") {
            Column {
                Row { for (size in QeetrixAvatarSize.entries) QeetrixAvatar("Ada Lovelace", size = size) }
                Row {
                    for (size in QeetrixAvatarSize.entries) {
                        QeetrixAvatar(
                            "Acme Pvt Ltd",
                            size = size,
                            shape = QeetrixAvatarShape.Square,
                        )
                    }
                }
                QeetrixAvatar("Photo", size = QeetrixAvatarSize.Xl, image = ColorPainter(QeetrixTheme.colors.datavizCategorical1))
                QeetrixAvatar("नमन शर्मा", size = QeetrixAvatarSize.Xl)
                QeetrixAvatar("Grace Hopper", decorative = true)
            }
        },
        QualitySpecimen("avatar-item") {
            Column {
                QeetrixAvatarItem("Priya Sharma", description = "Owner")
                QeetrixAvatarItem("Acme Pvt Ltd", description = "Organisation", shape = QeetrixAvatarShape.Square)
                // A work email longer than the line: it wraps, breaking where it must (avatar-item.css 0.4.0, QTRX-178).
                QeetrixAvatarItem("Priya Sharma", description = LongEmail)
            }
        },
        QualitySpecimen("description-list") {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                QeetrixDescriptionList {
                    QeetrixDescriptionItem("Plan", "Team")
                    QeetrixDescriptionItem("Status") { QeetrixBadge("Active", tone = QeetrixBadgeTone.Success) }
                    QeetrixDescriptionItem("Billing contact", "Finance team, 4th floor, Indiranagar, Bengaluru 560038")
                    QeetrixDescriptionItem("Phone", null)
                    QeetrixDescriptionItem(
                        "Display name",
                        "Acme",
                        description = "Shown in the top bar and in invitations.",
                        action = {
                            QeetrixButton(
                                "Change",
                                onClick = {},
                                variant = QeetrixButtonVariant.Ghost,
                                size = QeetrixButtonSize.Sm,
                            )
                        },
                    )
                }
                QeetrixDescriptionList(layout = QeetrixDescriptionListLayout.Columns, columns = 2) {
                    QeetrixDescriptionItem("Seats", "12")
                    QeetrixDescriptionItem("Region", "Mumbai (ap-south-1)")
                }
            }
        },
        QualitySpecimen("empty-state") {
            Column {
                QeetrixEmptyState(
                    "No invoices yet",
                    icon = QeetrixIconName.FileText,
                    description = "Invoices appear here once you bill a customer.",
                    footer = { QeetrixText("Or import them from a CSV file", size = QeetrixTextSize.Sm) },
                ) {
                    QeetrixButton("Create invoice", onClick = {}, icon = QeetrixIconName.Plus)
                    QeetrixButton("Import", onClick = {}, variant = QeetrixButtonVariant.Ghost)
                }
                QeetrixEmptyState(
                    "No results",
                    icon = QeetrixIconName.SearchX,
                    description = "Nothing matches “quarterly”.",
                    size = QeetrixEmptyStateSize.Section,
                    level = 3,
                )
                QeetrixEmptyState(
                    "You can't see this project",
                    icon = QeetrixIconName.Lock,
                    description = "Ask its owner, Ada Lovelace, for access.",
                    size = QeetrixEmptyStateSize.Section,
                    tone = QeetrixEmptyStateTone.Danger,
                )
            }
        },
        QualitySpecimen("data-state") {
            Column {
                QeetrixDataState(loading = true) {}
                QeetrixDataState(loading = false, error = true, onRetry = {}) {}
                QeetrixDataState(
                    loading = false,
                    empty = true,
                    emptyState = { QeetrixEmptyState("No members", size = QeetrixEmptyStateSize.Section) },
                ) {}
                QeetrixDataState(loading = false) { QeetrixText("12 members") }
            }
        },
        QualitySpecimen("list-item") {
            Column {
                QeetrixSectionHeader("Members", description = "People with access") {
                    QeetrixButton("Invite", onClick = {}, variant = QeetrixButtonVariant.Ghost, size = QeetrixButtonSize.Sm)
                }
                QeetrixListItem(
                    "Ada Lovelace",
                    description = "ada@example.com",
                    metadata = "Owner",
                    onClick = {},
                    leading = { QeetrixAvatar("Ada Lovelace", decorative = true) },
                )
                QeetrixListItem(
                    "Quarterly report.pdf",
                    description = "2.4 MB",
                    selected = true,
                    onClick = {},
                    onLongClick = {},
                    onLongClickLabel = "Select",
                    leading = { QeetrixListItemIcon(QeetrixIconName.FileText) },
                )
                QeetrixListItem("Weekly digest", description = "Every Monday", trailing = {
                    QeetrixIconButton(QeetrixIconName.Ellipsis, "More for Weekly digest", onClick = {}, tone = QeetrixButtonTone.Neutral)
                })
                QeetrixListItem("Priya Sharma", trailing = { QeetrixBadge("Owner", tone = QeetrixBadgeTone.Brand) })
                QeetrixListItem("Archived project", description = "Read-only", enabled = false, onClick = {})
                QeetrixSectionHeader("Files", ruled = false)
            }
        },
        QualitySpecimen("navigation-bar") { QeetrixNavigationBar(NavigationItems, value = "inbox", onValueChange = {}) },
        QualitySpecimen("navigation-rail") {
            Box(Modifier.height(400.dp)) {
                QeetrixNavigationRail(NavigationItems, value = "home", onValueChange = {}) {
                    QeetrixIconButton(QeetrixIconName.Plus, "New message", onClick = {}, variant = QeetrixButtonVariant.Solid)
                }
            }
        },
        QualitySpecimen("navigation-scaffold") {
            Column {
                Box(Modifier.height(240.dp)) {
                    QeetrixNavigationScaffold(NavigationItems, value = "home", onValueChange = {}, layout = QeetrixNavigationLayout.Bar) {
                        QeetrixText("Home")
                    }
                }
                Box(Modifier.height(360.dp)) {
                    QeetrixNavigationScaffold(NavigationItems, value = "inbox", onValueChange = {}, layout = QeetrixNavigationLayout.Rail) {
                        QeetrixText("Inbox")
                    }
                }
            }
        },
        QualitySpecimen("top-app-bar") {
            Column {
                QeetrixTopAppBar(
                    "Invoices",
                    description = "12 unpaid",
                    navigationIcon = { QeetrixBackButton(onClick = {}) },
                    actions = {
                        QeetrixIconButton(QeetrixIconName.Share2, "Share", onClick = {}, tone = QeetrixButtonTone.Neutral)
                        QeetrixIconButton(QeetrixIconName.EllipsisVertical, "More options", onClick = {}, tone = QeetrixButtonTone.Neutral)
                    },
                )
                QeetrixTopAppBar("Settings")
            }
        },
        QualitySpecimen("tabs") {
            Column {
                val tabs =
                    listOf(
                        QeetrixTab("overview", "Overview", QeetrixIconName.UserRound),
                        QeetrixTab("activity", "Activity", QeetrixIconName.Bell, badge = 12),
                        QeetrixTab("failures", "Failures", badge = 3, badgeTone = QeetrixBadgeTone.Danger),
                        QeetrixTab("billing", "Billing", enabled = false),
                    )
                QeetrixTabs(tabs, value = "overview", onValueChange = {})
                QeetrixTabs(tabs.take(3), value = "activity", onValueChange = {}, variant = QeetrixTabsVariant.Contained)
                QeetrixTabs((1..7).map { QeetrixTab(it, "Section $it") }, value = 2, onValueChange = {})
                QeetrixTabs(tabs, value = "overview", onValueChange = {}, orientation = QeetrixTabsOrientation.Vertical)
            }
        },
        QualitySpecimen("progress") {
            Column {
                QeetrixProgress(40f, label = "Uploading report", showValue = true)
                QeetrixProgress(100f, label = "Uploaded", showValue = true, tone = QeetrixProgressTone.Success)
                QeetrixProgress(64f, label = "Import failed", tone = QeetrixProgressTone.Danger)
                QeetrixProgress(60f, contentDescription = "Storage used")
                QeetrixProgress(null, label = "Preparing export")
                for (tone in QeetrixMeterTone.entries) {
                    QeetrixMeter(
                        30f + 10f * tone.ordinal,
                        label = "Meter ${tone.value}",
                        showValue = true,
                        tone = tone,
                    )
                }
                QeetrixMeter(40f, label = "People with two-step", showValue = true, target = 80f)
            }
        },
        QualitySpecimen("spinner") {
            Row {
                for (size in QeetrixSpinnerSize.entries) QeetrixSpinner(size = size)
                QeetrixSpinner(tone = QeetrixSpinnerTone.Brand, label = "Loading invoices")
                QeetrixSpinner(decorative = true)
            }
        },
        QualitySpecimen("skeleton") {
            Column {
                Row { for (size in QeetrixSkeletonSize.entries) QeetrixSkeleton(variant = QeetrixSkeletonVariant.Circle, size = size) }
                QeetrixSkeleton(Modifier.width(160.dp))
                QeetrixSkeleton(variant = QeetrixSkeletonVariant.Block)
                QeetrixSkeletonText(lines = 2)
                QeetrixSkeletonText(role = QeetrixTextRole.TitleMd)
            }
        },
        QualitySpecimen("snackbar") {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                QeetrixSnackbar("Conversation archived", actionLabel = "Undo", onAction = {}, onDismiss = {})
                QeetrixSnackbar(
                    "Invoice sent",
                    tone = QeetrixSnackbarTone.Success,
                    description = "INV-2041 to accounts@example.com",
                    onDismiss = {},
                )
                QeetrixSnackbar("Sync is paused", tone = QeetrixSnackbarTone.Warning, description = "Reconnect to keep working offline.")
                QeetrixSnackbar(
                    "Couldn't send the invoice",
                    tone = QeetrixSnackbarTone.Danger,
                    description = "The bank declined the card.",
                    actionLabel = "Retry",
                    onAction = {},
                    onDismiss = {},
                )
                QeetrixSnackbar("Copied")
            }
        },
        QualitySpecimen("snackbar-host") {
            val snackbars = rememberQeetrixSnackbarHostState()
            LaunchedEffect(snackbars) { snackbars.show("Payment saved", actionLabel = "View", timeout = Duration.INFINITE) }
            QeetrixSnackbarHost(snackbars)
        },
        QualitySpecimen("timeline") {
            Column {
                QeetrixTimeline {
                    QeetrixTimelineGroup("Today") {
                        QeetrixTimelineItem(
                            "Payment received",
                            time = "Today",
                            description = "By bank transfer.",
                            tone = QeetrixTimelineTone.Success,
                            icon = QeetrixIconName.CircleCheck,
                        )
                        QeetrixTimelineItem("Viewed", time = "Yesterday")
                    }
                    QeetrixTimelineGroup("Earlier") {
                        for (tone in QeetrixTimelineTone.entries) {
                            QeetrixTimelineItem(
                                "A ${tone.value} event",
                                time = "18:00",
                                icon = QeetrixIconName.Info,
                                tone = tone,
                            )
                        }
                    }
                }
                QeetrixTimeline(variant = QeetrixTimelineVariant.Compact) {
                    QeetrixTimelineItem(
                        "Card payment declined",
                        description = "Insufficient funds",
                        time = "5 days ago",
                        tone = QeetrixTimelineTone.Danger,
                    )
                }
            }
        },
        QualitySpecimen("icon") {
            Row {
                QeetrixIcon(QeetrixIconName.Lock, contentDescription = "Locked")
                QeetrixIcon(QeetrixIconName.Info, contentDescription = null)
                QeetrixIcon(
                    QeetrixIconName.House,
                    contentDescription = "Home",
                    shape = QeetrixIconShape.Round,
                    variant = QeetrixIconVariant.Filled,
                )
            }
        },
        QualitySpecimen("dialog") {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                // QeetrixDialog's surface, drawn in place: the dialog is a window of its own.
                DialogSurface(
                    title = "Rename workspace",
                    description = "Everyone in the workspace sees the new name.",
                    intent = QeetrixDialogIntent.Standard,
                    size = QeetrixDialogSize.Md,
                    onClose = {},
                    footerStart = { QeetrixText("Import a file", size = QeetrixTextSize.Sm) },
                    footer = {
                        QeetrixButton("Cancel", onClick = {}, variant = QeetrixButtonVariant.Ghost)
                        QeetrixButton("Save", onClick = {})
                    },
                ) { QeetrixTextField(TextFieldState("Northwind"), label = "Name") }
                DialogSurface(
                    title = "Send the invoice now?",
                    description = "It goes to accounts@example.com straight away.",
                    intent = QeetrixDialogIntent.Warning,
                    size = QeetrixDialogSize.Sm,
                    onClose = {},
                )
            }
        },
        QualitySpecimen("alert-dialog") {
            // QeetrixAlertDialog's surface: its actions arranged and drawn as it draws them.
            DialogSurface(
                title = "Revoke this API key?",
                description = "The reconciler stops working at once. This can't be undone.",
                intent = QeetrixDialogIntent.Danger,
                size = QeetrixDialogSize.Sm,
                onClose = null,
                footer = { DialogActions(arrangeActions(AlertActions, "Cancel")) {} },
            )
        },
        QualitySpecimen("confirmation-dialog") {
            // QeetrixConfirmationDialog's surface: Cancel and the confirming action, as it builds them.
            DialogSurface(
                title = "Archive this project?",
                description = "Members keep read-only access.",
                intent = QeetrixDialogIntent.Standard,
                size = QeetrixDialogSize.Sm,
                onClose = {},
                footer = { DialogActions(confirmationActions("Archive", {}, QeetrixDialogIntent.Standard, "Cancel")) {} },
            )
        },
        QualitySpecimen("bottom-sheet") {
            // QeetrixBottomSheet's handle and parts, drawn in place: the sheet is a window of its own.
            Column(Modifier.fillMaxWidth().background(QeetrixTheme.colors.surfaceOverlay)) {
                DragHandle()
                SheetLayout(
                    title = "Filter invoices",
                    description = "Show only the invoices you need.",
                    onClose = {},
                    footerStart = { QeetrixText("12 match", size = QeetrixTextSize.Sm) },
                    footer = {
                        QeetrixButton("Clear", onClick = {}, variant = QeetrixButtonVariant.Ghost)
                        QeetrixButton("Show results", onClick = {})
                    },
                ) { QeetrixCheckbox(true, {}, label = "Overdue") }
            }
        },
        QualitySpecimen("menu") {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                // The trigger as QeetrixMenu lays it out, closed…
                QeetrixMenu(open = false, onOpenChange = {}, trigger = {
                    QeetrixIconButton(QeetrixIconName.EllipsisVertical, "More options", onClick = {})
                }) { MenuItems() }
                // …and its open surface, drawn in place: the popup is a window of its own.
                MenuSurface(Modifier.widthIn(min = 200.dp, max = 320.dp), focusFirst = false, onDismiss = {}) { MenuItems() }
            }
        },
        QualitySpecimen("relative-time") {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                val now = java.time.Instant.parse("2026-10-30T12:00:00Z")
                QeetrixRelativeTime(now.minusSeconds(120), now = now)
                QeetrixRelativeTime(
                    now.minus(java.time.Duration.ofDays(1)),
                    now = now,
                    size = QeetrixTextSize.Sm,
                    tone = QeetrixTextTone.Subtle,
                )
                // Beyond the 30-day threshold: the date.
                QeetrixRelativeTime(now.minus(java.time.Duration.ofDays(45)), now = now)
            }
        },
        QualitySpecimen("progress-ring") {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                QeetrixProgressRing(0.6f, size = QeetrixProgressRingSize.Sm)
                QeetrixProgressRing(0.4f, contentDescription = "Time left", stateDescription = "12 seconds left")
                QeetrixProgressRing(
                    0.1f,
                    size = QeetrixProgressRingSize.Lg,
                    tone = QeetrixMeterTone.Warning,
                    contentDescription = "Time left",
                )
                QeetrixProgressRing(1f, tone = QeetrixMeterTone.Success, contentDescription = "Uploaded")
            }
        },
        QualitySpecimen("copy") {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    QeetrixCopyButton({ "cl_8f2a" }, label = "Copy client ID")
                    QeetrixCopyButton({ "cl_8f2a" }, text = "Copy ID", variant = QeetrixButtonVariant.Soft)
                    QeetrixCopyButton({ "cl_8f2a" }, enabled = false)
                }
                QeetrixCopyField("Client ID", "cl_8f2a91c4e7", description = "Paste it into your app's settings.")
                QeetrixCopyField("ACS URL", "https://id.qeet.in/saml/acs/tenants/acme-industries/apps/payroll-portal-production")
                QeetrixCopyField("Signing secret", "whsec_2b91d0c4e7a8", layout = QeetrixCopyFieldLayout.Row, sensitive = true)
                QeetrixCopyField("Webhook", "https://hooks.acme.in/qeet/events/payroll", layout = QeetrixCopyFieldLayout.Row, wrap = false)
            }
        },
        QualitySpecimen("otp-code") {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                val now = java.time.Instant.parse("2026-10-30T12:00:18Z")
                val expires = java.time.Instant.parse("2026-10-30T12:00:30Z")
                QeetrixOTPCode("482105", expires, label = "Acme · priya@acme.in", now = now)
                QeetrixOTPCode("48210593", expires, label = "Payroll", size = QeetrixOTPCodeSize.Md, now = expires.minusSeconds(3))
                QeetrixOTPCode("482105", expires, label = "Hidden", hidden = true, now = now)
            }
        },
        QualitySpecimen("qr-code") {
            QeetrixQRCode("otpauth://totp/Acme:priya@acme.in?secret=JBSWY3DPEHPK3PXP&issuer=Acme", label = "QR code to add Acme")
        },
        QualitySpecimen("qr-scanner") {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Box(Modifier.height(480.dp)) {
                    QeetrixQRScanner(QeetrixScannerState.Scanning, torch = false, onTorchChange = {}, onEnterKey = {}) {
                        Box(Modifier.fillMaxSize().background(androidx.compose.ui.graphics.Color.DarkGray))
                    }
                }
                // Each state fills the box it is given, as the camera view does.
                Box(Modifier.height(560.dp)) { QeetrixQRScanner(QeetrixScannerState.PermissionDenied, onEnterKey = {}) {} }
                Box(Modifier.height(560.dp)) { QeetrixQRScanner(QeetrixScannerState.NoCamera, onEnterKey = {}) {} }
            }
        },
        QualitySpecimen("permission-primer") {
            QeetrixPermissionPrimer(
                QeetrixPermission.Notifications,
                title = "Get sign-in requests on this phone",
                description = "Qeet Auth asks you to approve each sign-in. Allow notifications so the requests reach you.",
                onContinue = {},
                onNotNow = {},
            )
        },
        QualitySpecimen("alert") {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                QeetrixAlert(
                    "Push isn't set up",
                    tone = QeetrixAlertTone.Warning,
                    description = "Sign-in requests won't reach this phone.",
                    onDismiss = {},
                    actions = { QeetrixButton("Allow notifications", onClick = {}, size = QeetrixButtonSize.Sm) },
                )
                QeetrixAlert(
                    "You're offline",
                    tone = QeetrixAlertTone.Danger,
                    description = "Codes still work; approvals wait until you're back.",
                )
                QeetrixAlert("Phone linked", tone = QeetrixAlertTone.Success)
                QeetrixAlert("Maintenance on Sunday, 2–4 am", variant = QeetrixAlertVariant.Banner, onDismiss = {})
            }
        },
        QualitySpecimen("steps") {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                val steps =
                    listOf(
                        QeetrixStep("Account"),
                        QeetrixStep("Verify", status = QeetrixStepStatus.Failed),
                        QeetrixStep("Workspace"),
                        QeetrixStep("Done"),
                    )
                QeetrixSteps(steps, current = 2, onStepSelect = {})
                QeetrixSteps(
                    steps.map {
                        QeetrixStep(it.label, description = "What this step sets up, in a line.")
                    },
                    current = 1,
                    orientation = QeetrixStepsOrientation.Vertical,
                )
                Box(Modifier.width(200.dp)) { QeetrixSteps(steps, current = 1) }
            }
        },
        QualitySpecimen("stat") {
            QeetrixStatGroup {
                QeetrixStat("Active users", value = 1240512, note = "+3.2% this week", tone = QeetrixStatTone.Positive)
                QeetrixStat(
                    "Revenue",
                    value = 1842600,
                    currency = "INR",
                    note = "Down ₹12,400 from yesterday",
                    tone = QeetrixStatTone.Negative,
                )
                QeetrixStat("Uptime", text = "99.95%")
                QeetrixStat("Sign-ins", loading = true)
            }
        },
        QualitySpecimen("check-list") {
            QeetrixCheckList(footer = { QeetrixText("Checked 2 minutes ago", size = QeetrixTextSize.Sm) }) {
                QeetrixCheckItem("At least 12 characters", QeetrixCheckStatus.Passed)
                QeetrixCheckItem("Not a password found in a breach", QeetrixCheckStatus.Running, detail = "Checking the breach list")
                QeetrixCheckItem("The certificate is in date", QeetrixCheckStatus.Failed, detail = "Expired 23 Aug 2026")
                QeetrixCheckItem("The metadata URL answers", QeetrixCheckStatus.Pending)
            }
        },
        QualitySpecimen("avatar-group") {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                QeetrixAvatarGroup(listOf("Priya Sharma", "Rahul Verma", "Ananya Iyer").map { QeetrixAvatarPerson(it) }, total = 412)
                QeetrixAvatarGroup(
                    listOf("Acme", "Payroll").map {
                        QeetrixAvatarPerson(it)
                    },
                    size = QeetrixAvatarSize.Lg,
                    shape = QeetrixAvatarShape.Square,
                )
            }
        },
        QualitySpecimen("bottom-action-bar") {
            QeetrixBottomActionBar(ruled = true) {
                QeetrixButton("Deny", onClick = {}, variant = QeetrixButtonVariant.Soft, fullWidth = true)
                QeetrixButton("Approve", onClick = {}, fullWidth = true)
            }
        },
        QualitySpecimen("segmented-control") {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                QeetrixSegmentedControl(
                    "all",
                    {
                    },
                    listOf(
                        QeetrixOption("all", "All"),
                        QeetrixOption("pending", "Pending"),
                        QeetrixOption("done", "Done", enabled = false),
                    ),
                )
                QeetrixSegmentedControl(
                    "light",
                    {
                    },
                    listOf(
                        QeetrixOption("light", "Light"),
                        QeetrixOption("dark", "Dark"),
                        QeetrixOption("system", "System"),
                    ),
                    size = QeetrixButtonSize.Sm,
                )
            }
        },
        QualitySpecimen("page-indicator") {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                QeetrixPageIndicator(1, 4, onPageChange = {})
                QeetrixPageIndicator(0, 2)
            }
        },
        QualitySpecimen("slider") {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                QeetrixSlider(40f, {}, label = "Volume", step = 5f, minLabel = "Quiet", maxLabel = "Loud")
                QeetrixRangeSlider(2000f..18000f, {}, label = "Budget", valueRange = 0f..50000f, step = 500f)
                QeetrixSlider(20f, {}, label = "Disabled", enabled = false)
            }
        },
        QualitySpecimen("date-field") {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                QeetrixDateField(java.time.LocalDate.of(2026, 10, 25), {}, label = "Due date")
                QeetrixDateField(null, {}, label = "Start date", placeholder = "Choose a date", description = "From today onwards.")
                QeetrixDateRangeField(java.time.LocalDate.of(2026, 10, 1)..java.time.LocalDate.of(2026, 10, 25), {}, label = "Period")
                QeetrixTimeField(java.time.LocalTime.of(14, 30), {}, label = "Reminder", minuteStep = 15, is24Hour = false)
                QeetrixDateField(null, {}, label = "Disabled", enabled = false)
            }
        },
        QualitySpecimen("file-upload") {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                QeetrixAttachmentPicker(onPhotos = {}, onCamera = {}, onFiles = {}, hint = "PDF or PNG, up to 10 MB")
                QeetrixFileRow("invoice-oct-2026.pdf", sizeBytes = 248_000, mimeType = "application/pdf", progress = 0.42f, onRemove = {})
                QeetrixFileRow("receipt.png", sizeBytes = 1_200_000, uploaded = true, onRemove = {})
                QeetrixFileRow("payroll-export.zip", sizeBytes = 48_000_000, error = "Larger than 10 MB", onRemove = {})
            }
        },
        QualitySpecimen("swipe-actions") {
            QeetrixSwipeActions(
                startAction = QeetrixSwipeAction("Archive", QeetrixIconName.Folder, {}),
                endAction =
                    QeetrixSwipeAction(
                        "Remove",
                        QeetrixIconName.Trash,
                        {},
                        QeetrixSwipeActionTone.Danger,
                        confirmTitle = "Remove Acme?",
                    ),
            ) { QeetrixListItem("Acme", description = "priya@acme.in") }
        },
        QualitySpecimen("pull-to-refresh") {
            Box(Modifier.height(160.dp)) {
                QeetrixPullToRefresh(refreshing = false, onRefresh = {}) {
                    Column(Modifier.verticalScroll(androidx.compose.foundation.rememberScrollState())) {
                        QeetrixListItem("Sign-in from Chrome on macOS", description = "Bengaluru · 2 min ago")
                    }
                }
            }
        },
        QualitySpecimen("top-app-bar-large") {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                QeetrixTopAppBar(
                    "Members",
                    count = 412,
                    status = { QeetrixBadge("Synced", tone = QeetrixBadgeTone.Success) },
                    tabs = {
                        QeetrixTabs(listOf(QeetrixTab("all", "All"), QeetrixTab("invited", "Invited")), "all", {})
                    },
                )
                QeetrixTopAppBar("Accounts", description = "3 on this phone", size = QeetrixTopAppBarSize.Large)
                val tab = QeetrixTab("all", "All")
                QeetrixTabPanel(tab, "all") { QeetrixText("All members") }
            }
        },
    )

/** Every kind of menu content, in each of its states. */
@Composable
private fun QeetrixMenuScope.MenuItems() {
    QeetrixMenuGroupLabel("Workspace")
    QeetrixMenuItem("Rename", onClick = {}, icon = QeetrixIconName.Pencil, shortcut = "F2")
    QeetrixMenuItem("Duplicate", onClick = {}, icon = QeetrixIconName.Copy, description = "Members and settings, not the data")
    QeetrixMenuItem("Share", onClick = {}, icon = QeetrixIconName.Share2, enabled = false)
    QeetrixMenuSeparator()
    QeetrixMenuCheckboxItem("Show archived", checked = true, onCheckedChange = {})
    QeetrixMenuCheckboxItem("Compact rows", checked = false, onCheckedChange = {}, enabled = false)
    QeetrixMenuRadioGroup("newest", {}) {
        QeetrixMenuGroupLabel("Sort by")
        QeetrixMenuRadioItem("Newest first", "newest")
        QeetrixMenuRadioItem("Amount", "amount", description = "Needs the amounts to load", enabled = false)
    }
    QeetrixMenuSeparator()
    QeetrixMenuItem("Delete workspace", onClick = {}, icon = QeetrixIconName.Trash, tone = QeetrixMenuItemTone.Danger)
}

/**
 * The overlays as apps open them — each in its own window — for the scans that look at every window rather than one
 * root (`LayoutQualityTest`). A window takes the system's font scale and the composition's layout direction, but not a
 * density given to the composition, so these are scanned at the default size, at the system's 200 % and right to left.
 */
internal val OverlayCatalog: List<QualitySpecimen> =
    listOf(
        QualitySpecimen("full-screen-form window") {
            QeetrixFullScreenForm(true, {}, title = "New account", confirmLabel = "Save", onConfirm = {}, dirty = true) {
                QeetrixTextField(TextFieldState("Acme"), label = "Account name")
                QeetrixTextField(TextFieldState(), label = "Issuer", optional = true)
            }
        },
        QualitySpecimen("dialog window") {
            QeetrixDialog(
                open = true,
                onOpenChange = {},
                title = "Rename workspace",
                description = "Everyone in the workspace sees the new name.",
                footerStart = { QeetrixText("Import a file", size = QeetrixTextSize.Sm) },
                footer = {
                    QeetrixButton("Cancel", onClick = {}, variant = QeetrixButtonVariant.Ghost)
                    QeetrixButton("Save", onClick = {})
                },
            ) { QeetrixTextField(TextFieldState("Northwind"), label = "Name") }
        },
        QualitySpecimen("alert-dialog window") {
            QeetrixAlertDialog(
                open = true,
                onOpenChange = {},
                title = "Revoke this API key?",
                actions = AlertActions,
                description = "The reconciler stops working at once. This can't be undone.",
            )
        },
        QualitySpecimen("confirmation-dialog window") {
            QeetrixConfirmationDialog(
                open = true,
                onOpenChange = {},
                title = "Send the invoice now?",
                confirmLabel = "Send",
                onConfirm = {},
                description = "It goes to accounts@example.com straight away.",
                intent = QeetrixDialogIntent.Warning,
            )
        },
        QualitySpecimen("bottom-sheet window") {
            QeetrixBottomSheet(
                open = true,
                onOpenChange = {},
                title = "Filter invoices",
                description = "Show only the invoices you need.",
                footer = { QeetrixButton("Show results", onClick = {}) },
            ) { QeetrixCheckbox(true, {}, label = "Overdue") }
        },
        QualitySpecimen("menu window") {
            QeetrixMenu(open = true, onOpenChange = {}, trigger = {
                QeetrixIconButton(QeetrixIconName.EllipsisVertical, "More options", onClick = {})
            }) { MenuItems() }
        },
    )

/** A work email longer than an avatar item's line, which wraps rather than being cut (QTRX-178, once finding Q-14). */
internal const val LongEmail = "priya.sharma.accounts-payable@acme-industries.co.in"

private val Countries =
    listOf(
        QeetrixOption("in", "India", meta = "412"),
        QeetrixOption("ae", "United Arab Emirates", meta = "38"),
        QeetrixOption("gb", "United Kingdom of Great Britain and Northern Ireland"),
        QeetrixOption("sg", "Singapore", enabled = false),
    )

private val AlertActions =
    listOf(
        QeetrixDialogAction("Keep key", QeetrixDialogActionRole.Cancel) {},
        QeetrixDialogAction("Revoke key", QeetrixDialogActionRole.Destructive) {},
    )

private val NavigationItems =
    listOf(
        QeetrixNavigationItem("home", "Home", QeetrixIconName.House),
        QeetrixNavigationItem("inbox", "Inbox", QeetrixIconName.Inbox, badge = "3"),
        QeetrixNavigationItem("activity", "Activity", QeetrixIconName.Bell),
        QeetrixNavigationItem("settings", "Settings", QeetrixIconName.Settings, enabled = false),
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
                QeetrixHeading("खाता सेटिंग्स", level = 2)
                QeetrixButton("बदलाव सहेजें", onClick = {})
                QeetrixListItem("तिमाही रिपोर्ट", description = "अदा लवलेस ने साझा किया", metadata = "२.४ MB", onClick = {})
                QeetrixTextField(TextFieldState("आशा राव"), label = "पूरा नाम", description = "जैसा आपके पैन पर है")
                QeetrixCheckbox(true, {}, label = "इस डिवाइस को याद रखें")
                QeetrixBadge("सक्रिय", tone = QeetrixBadgeTone.Success)
                QeetrixNavigationBar(
                    listOf(
                        QeetrixNavigationItem("home", "होम", QeetrixIconName.House),
                        QeetrixNavigationItem("inbox", "इनबॉक्स", QeetrixIconName.Inbox),
                        QeetrixNavigationItem("settings", "सेटिंग्स", QeetrixIconName.Settings),
                    ),
                    value = "home",
                    onValueChange = {},
                )
            }
        },
        QualitySpecimen("telugu") {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                QeetrixEmptyState(
                    "ఇంకా ఇన్‌వాయిస్‌లు లేవు",
                    icon = QeetrixIconName.FileText,
                    description = "మీరు కస్టమర్‌కు బిల్ చేసిన తర్వాత ఇన్‌వాయిస్‌లు ఇక్కడ కనిపిస్తాయి.",
                    size = QeetrixEmptyStateSize.Section,
                )
                QeetrixDescriptionList { QeetrixDescriptionItem("ప్రణాళిక", "బృందం") }
                QeetrixChip("చదవనివి", selected = true, onSelectedChange = {})
                QeetrixSwitch(true, {}, label = "ఇమెయిల్ హెచ్చరికలు")
                QeetrixSelect("a", {}, listOf(QeetrixOption("a", "భారతదేశం")), label = "దేశం")
            }
        },
        QualitySpecimen("arabic") {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                QeetrixTextField(TextFieldState("أدا لوفليس"), label = "الاسم الكامل", placeholder = "اكتب اسمك")
                QeetrixSnackbar("تمت أرشفة المحادثة", actionLabel = "تراجع", onAction = {}, onDismiss = {})
                QeetrixTimeline { QeetrixTimelineItem("تم استلام الدفعة", time = "اليوم", description = "بتحويل مصرفي.") }
                QeetrixTabs(listOf(QeetrixTab("a", "نظرة عامة"), QeetrixTab("b", "النشاط")), value = "a", onValueChange = {})
                QeetrixRadioGroup("m", {}, listOf(QeetrixOption("m", "شهريًا"), QeetrixOption("y", "سنويًا")), label = "دورة الفوترة")
                QeetrixButton("متابعة", onClick = {}, icon = QeetrixIconName.ChevronRight, iconMirrorsInRightToLeft = true)
            }
        },
        QualitySpecimen("pseudo-expanded") {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                QeetrixButton(pseudo("Save changes"), onClick = {}, icon = QeetrixIconName.Check)
                QeetrixTabs(
                    listOf(QeetrixTab("a", pseudo("Overview")), QeetrixTab("b", pseudo("Activity"))),
                    value = "a",
                    onValueChange = {},
                )
                QeetrixChipGroup(
                    label = pseudo("Status"),
                    value = "open",
                    onValueChange = {},
                    options = listOf(QeetrixOption("open", pseudo("Open")), QeetrixOption("closed", pseudo("Closed"))),
                )
                QeetrixSnackbar(pseudo("Conversation archived"), actionLabel = pseudo("Undo"), onAction = {}, onDismiss = {})
                QeetrixListItem(
                    pseudo("Quarterly report"),
                    description = pseudo("Shared by Ada Lovelace"),
                    metadata = pseudo("2 MB"),
                    onClick = {},
                    trailing = { QeetrixBadge(pseudo("Shared")) },
                )
                QeetrixAccordion(value = emptyList<String>(), onValueChange = {}) {
                    QeetrixAccordionItem("billing", pseudo("How am I billed?")) {}
                }
                QeetrixSwitch(true, {}, label = pseudo("Email alerts"), description = pseudo("A message when someone signs in"))
                QeetrixTextField(TextFieldState(), label = pseudo("Work email"), placeholder = pseudo("e.g. asha@acme.in"), optional = true)
                QeetrixFormActions {
                    QeetrixButton(pseudo("Cancel"), onClick = {}, variant = QeetrixButtonVariant.Ghost)
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
