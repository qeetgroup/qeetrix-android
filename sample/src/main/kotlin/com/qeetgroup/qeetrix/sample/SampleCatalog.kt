package com.qeetgroup.qeetrix.sample

import androidx.compose.runtime.Composable
import com.qeetgroup.qeetrix.sample.components.actions.ButtonSample
import com.qeetgroup.qeetrix.sample.components.actions.IconButtonSample
import com.qeetgroup.qeetrix.sample.components.datadisplay.AvatarSample
import com.qeetgroup.qeetrix.sample.components.datadisplay.BadgeSample
import com.qeetgroup.qeetrix.sample.components.datadisplay.TextSample
import com.qeetgroup.qeetrix.sample.components.feedback.ProgressSample
import com.qeetgroup.qeetrix.sample.components.feedback.SpinnerSample
import com.qeetgroup.qeetrix.sample.components.forms.CheckboxSample
import com.qeetgroup.qeetrix.sample.components.forms.FieldSample
import com.qeetgroup.qeetrix.sample.components.forms.FormSample
import com.qeetgroup.qeetrix.sample.components.forms.NumberFieldSample
import com.qeetgroup.qeetrix.sample.components.forms.OTPInputSample
import com.qeetgroup.qeetrix.sample.components.forms.PasswordInputSample
import com.qeetgroup.qeetrix.sample.components.forms.RadioGroupSample
import com.qeetgroup.qeetrix.sample.components.forms.SelectSample
import com.qeetgroup.qeetrix.sample.components.forms.SwitchSample
import com.qeetgroup.qeetrix.sample.components.forms.TextFieldSample
import com.qeetgroup.qeetrix.sample.components.forms.TextareaSample
import com.qeetgroup.qeetrix.sample.components.layout.CardSample
import com.qeetgroup.qeetrix.sample.components.layout.SeparatorSample

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
        SampleEntry("avatar", "Avatar", SampleCategory.DataDisplay) { AvatarSample() },
        SampleEntry("badge", "Badge", SampleCategory.DataDisplay) { BadgeSample() },
        SampleEntry("text", "Text", SampleCategory.DataDisplay) { TextSample() },
        SampleEntry("progress", "Progress", SampleCategory.Feedback) { ProgressSample() },
        SampleEntry("spinner", "Spinner", SampleCategory.Feedback) { SpinnerSample() },
        SampleEntry("card", "Card", SampleCategory.Layout) { CardSample() },
        SampleEntry("separator", "Separator", SampleCategory.Layout) { SeparatorSample() },
        // new-component: insert above this line
    )
