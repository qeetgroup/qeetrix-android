package com.qeetgroup.qeetrix.sample

import androidx.compose.runtime.Composable
import com.qeetgroup.qeetrix.sample.components.actions.BottomActionBarSample
import com.qeetgroup.qeetrix.sample.components.actions.ButtonSample
import com.qeetgroup.qeetrix.sample.components.actions.IconButtonSample
import com.qeetgroup.qeetrix.sample.components.datadisplay.AvatarGroupSample
import com.qeetgroup.qeetrix.sample.components.datadisplay.AvatarSample
import com.qeetgroup.qeetrix.sample.components.datadisplay.BadgeSample
import com.qeetgroup.qeetrix.sample.components.datadisplay.ChipSample
import com.qeetgroup.qeetrix.sample.components.datadisplay.DescriptionListSample
import com.qeetgroup.qeetrix.sample.components.datadisplay.ListItemSample
import com.qeetgroup.qeetrix.sample.components.datadisplay.OTPCodeSample
import com.qeetgroup.qeetrix.sample.components.datadisplay.PullToRefreshSample
import com.qeetgroup.qeetrix.sample.components.datadisplay.QRCodeSample
import com.qeetgroup.qeetrix.sample.components.datadisplay.StatSample
import com.qeetgroup.qeetrix.sample.components.datadisplay.SwipeActionsSample
import com.qeetgroup.qeetrix.sample.components.datadisplay.TextSample
import com.qeetgroup.qeetrix.sample.components.datadisplay.TimelineSample
import com.qeetgroup.qeetrix.sample.components.datetime.DateFieldSample
import com.qeetgroup.qeetrix.sample.components.datetime.RelativeTimeSample
import com.qeetgroup.qeetrix.sample.components.feedback.AlertSample
import com.qeetgroup.qeetrix.sample.components.feedback.CheckListSample
import com.qeetgroup.qeetrix.sample.components.feedback.DataStateSample
import com.qeetgroup.qeetrix.sample.components.feedback.EmptyStateSample
import com.qeetgroup.qeetrix.sample.components.feedback.ProgressRingSample
import com.qeetgroup.qeetrix.sample.components.feedback.ProgressSample
import com.qeetgroup.qeetrix.sample.components.feedback.SkeletonSample
import com.qeetgroup.qeetrix.sample.components.feedback.SnackbarSample
import com.qeetgroup.qeetrix.sample.components.feedback.SpinnerSample
import com.qeetgroup.qeetrix.sample.components.forms.CheckboxSample
import com.qeetgroup.qeetrix.sample.components.forms.FieldSample
import com.qeetgroup.qeetrix.sample.components.forms.FileUploadSample
import com.qeetgroup.qeetrix.sample.components.forms.FormSample
import com.qeetgroup.qeetrix.sample.components.forms.NumberFieldSample
import com.qeetgroup.qeetrix.sample.components.forms.OTPInputSample
import com.qeetgroup.qeetrix.sample.components.forms.PasswordInputSample
import com.qeetgroup.qeetrix.sample.components.forms.RadioGroupSample
import com.qeetgroup.qeetrix.sample.components.forms.SearchFieldSample
import com.qeetgroup.qeetrix.sample.components.forms.SegmentedControlSample
import com.qeetgroup.qeetrix.sample.components.forms.SelectSample
import com.qeetgroup.qeetrix.sample.components.forms.SliderSample
import com.qeetgroup.qeetrix.sample.components.forms.SwitchSample
import com.qeetgroup.qeetrix.sample.components.forms.TextFieldSample
import com.qeetgroup.qeetrix.sample.components.forms.TextareaSample
import com.qeetgroup.qeetrix.sample.components.layout.AccordionSample
import com.qeetgroup.qeetrix.sample.components.layout.CardSample
import com.qeetgroup.qeetrix.sample.components.layout.SeparatorSample
import com.qeetgroup.qeetrix.sample.components.media.QRScannerSample
import com.qeetgroup.qeetrix.sample.components.navigation.AdaptiveNavigationSample
import com.qeetgroup.qeetrix.sample.components.navigation.NavigationBarSample
import com.qeetgroup.qeetrix.sample.components.navigation.NavigationRailSample
import com.qeetgroup.qeetrix.sample.components.navigation.PageIndicatorSample
import com.qeetgroup.qeetrix.sample.components.navigation.StepsSample
import com.qeetgroup.qeetrix.sample.components.navigation.TabsSample
import com.qeetgroup.qeetrix.sample.components.navigation.TopAppBarSample
import com.qeetgroup.qeetrix.sample.components.overlays.AlertDialogSample
import com.qeetgroup.qeetrix.sample.components.overlays.BottomSheetSample
import com.qeetgroup.qeetrix.sample.components.overlays.ConfirmationDialogSample
import com.qeetgroup.qeetrix.sample.components.overlays.DialogSample
import com.qeetgroup.qeetrix.sample.components.overlays.FullScreenFormSample
import com.qeetgroup.qeetrix.sample.components.overlays.MenuSample
import com.qeetgroup.qeetrix.sample.components.utilities.CopySample
import com.qeetgroup.qeetrix.sample.components.utilities.PermissionPrimerSample

/**
 * The component groups of ui.qeet.in, in its sidebar order (qeetrix-docs `src/lib/component-groups.json`).
 * The home screen lists the groups that have components.
 */
enum class SampleCategory(
    val title: String,
) {
    Actions("Actions"),
    Forms("Forms & inputs"),
    DateTime("Date & time"),
    DataDisplay("Data display"),
    Feedback("Feedback"),
    Overlays("Overlays"),
    Navigation("Navigation"),
    Layout("Layout"),
    Utilities("Utilities"),
}

/** A component page: [id] is its route (the component name in kebab case), [title] its name in sentence case. */
class SampleEntry(
    val id: String,
    val title: String,
    val category: SampleCategory,
    val content: @Composable () -> Unit,
)

/** Every component page, in the order the home screen lists them within a group. */
val SampleCatalog: List<SampleEntry> =
    listOf(
        SampleEntry("button", "Button", SampleCategory.Actions) { ButtonSample() },
        SampleEntry("icon-button", "Icon button", SampleCategory.Actions) { IconButtonSample() },
        SampleEntry("bottom-action-bar", "Bottom action bar", SampleCategory.Actions) { BottomActionBarSample() },
        SampleEntry("text-field", "Text field", SampleCategory.Forms) { TextFieldSample() },
        SampleEntry("switch", "Switch", SampleCategory.Forms) { SwitchSample() },
        SampleEntry("field", "Field", SampleCategory.Forms) { FieldSample() },
        SampleEntry("textarea", "Textarea", SampleCategory.Forms) { TextareaSample() },
        SampleEntry("password-input", "Password input", SampleCategory.Forms) { PasswordInputSample() },
        SampleEntry("otp-input", "OTP input", SampleCategory.Forms) { OTPInputSample() },
        SampleEntry("checkbox", "Checkbox", SampleCategory.Forms) { CheckboxSample() },
        SampleEntry("radio-group", "Radio group", SampleCategory.Forms) { RadioGroupSample() },
        SampleEntry("select", "Select", SampleCategory.Forms) { SelectSample() },
        SampleEntry("number-field", "Number field", SampleCategory.Forms) { NumberFieldSample() },
        SampleEntry("form", "Form", SampleCategory.Forms) { FormSample() },
        SampleEntry("search-field", "Search field", SampleCategory.Forms) { SearchFieldSample() },
        SampleEntry("segmented-control", "Segmented control", SampleCategory.Forms) { SegmentedControlSample() },
        SampleEntry("file-upload", "File upload", SampleCategory.Forms) { FileUploadSample() },
        SampleEntry("slider", "Slider", SampleCategory.Forms) { SliderSample() },
        SampleEntry("date-field", "Date field", SampleCategory.DateTime) { DateFieldSample() },
        SampleEntry("relative-time", "Relative time", SampleCategory.DateTime) { RelativeTimeSample() },
        SampleEntry("avatar", "Avatar", SampleCategory.DataDisplay) { AvatarSample() },
        SampleEntry("avatar-group", "Avatar group", SampleCategory.DataDisplay) { AvatarGroupSample() },
        SampleEntry("badge", "Badge", SampleCategory.DataDisplay) { BadgeSample() },
        SampleEntry("text", "Text", SampleCategory.DataDisplay) { TextSample() },
        SampleEntry("list-item", "List item", SampleCategory.DataDisplay) { ListItemSample() },
        SampleEntry("swipe-actions", "Swipe actions", SampleCategory.DataDisplay) { SwipeActionsSample() },
        SampleEntry("pull-to-refresh", "Pull to refresh", SampleCategory.DataDisplay) { PullToRefreshSample() },
        SampleEntry("description-list", "Description list", SampleCategory.DataDisplay) { DescriptionListSample() },
        SampleEntry("chip", "Chip", SampleCategory.DataDisplay) { ChipSample() },
        SampleEntry("timeline", "Timeline", SampleCategory.DataDisplay) { TimelineSample() },
        SampleEntry("stat", "Stat", SampleCategory.DataDisplay) { StatSample() },
        SampleEntry("qr-code", "QR code", SampleCategory.DataDisplay) { QRCodeSample() },
        SampleEntry("otp-code", "OTP code", SampleCategory.DataDisplay) { OTPCodeSample() },
        SampleEntry("progress", "Progress", SampleCategory.Feedback) { ProgressSample() },
        SampleEntry("progress-ring", "Progress ring", SampleCategory.Feedback) { ProgressRingSample() },
        SampleEntry("alert", "Alert", SampleCategory.Feedback) { AlertSample() },
        SampleEntry("check-list", "Check list", SampleCategory.Feedback) { CheckListSample() },
        SampleEntry("spinner", "Spinner", SampleCategory.Feedback) { SpinnerSample() },
        SampleEntry("snackbar", "Snackbar", SampleCategory.Feedback) { SnackbarSample() },
        SampleEntry("empty-state", "Empty state", SampleCategory.Feedback) { EmptyStateSample() },
        SampleEntry("data-state", "Data state", SampleCategory.Feedback) { DataStateSample() },
        SampleEntry("skeleton", "Skeleton", SampleCategory.Feedback) { SkeletonSample() },
        SampleEntry("alert-dialog", "Alert dialog", SampleCategory.Overlays) { AlertDialogSample() },
        SampleEntry("confirmation-dialog", "Confirmation dialog", SampleCategory.Overlays) { ConfirmationDialogSample() },
        SampleEntry("dialog", "Dialog", SampleCategory.Overlays) { DialogSample() },
        SampleEntry("full-screen-form", "Full-screen form", SampleCategory.Overlays) { FullScreenFormSample() },
        SampleEntry("bottom-sheet", "Bottom sheet", SampleCategory.Overlays) { BottomSheetSample() },
        SampleEntry("menu", "Menu", SampleCategory.Overlays) { MenuSample() },
        SampleEntry("navigation-bar", "Navigation bar", SampleCategory.Navigation) { NavigationBarSample() },
        SampleEntry("navigation-rail", "Navigation rail", SampleCategory.Navigation) { NavigationRailSample() },
        SampleEntry("adaptive-navigation", "Adaptive navigation", SampleCategory.Navigation) { AdaptiveNavigationSample() },
        SampleEntry("top-app-bar", "Top app bar", SampleCategory.Navigation) { TopAppBarSample() },
        SampleEntry("tabs", "Tabs", SampleCategory.Navigation) { TabsSample() },
        SampleEntry("steps", "Steps", SampleCategory.Navigation) { StepsSample() },
        SampleEntry("page-indicator", "Page indicator", SampleCategory.Navigation) { PageIndicatorSample() },
        SampleEntry("accordion", "Accordion", SampleCategory.Layout) { AccordionSample() },
        SampleEntry("card", "Card", SampleCategory.Layout) { CardSample() },
        SampleEntry("separator", "Separator", SampleCategory.Layout) { SeparatorSample() },
        SampleEntry("qr-scanner", "QR scanner", SampleCategory.Utilities) { QRScannerSample() },
        SampleEntry("copy", "Copy", SampleCategory.Utilities) { CopySample() },
        SampleEntry("permission-primer", "Permission primer", SampleCategory.Utilities) { PermissionPrimerSample() },
        // new-component: insert above this line
    )
