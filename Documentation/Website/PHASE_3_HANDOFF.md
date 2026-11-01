# ui.qeet.in handoff: Android inputs and forms

For whoever maintains the component pages on ui.qeet.in (`qeetrix-docs`). It lists what Qeetrix for
Android provides for each `@qeetrix/react` component in the Forms group, so a page can gain an **Android**
section. Nothing here has been published: qeetrix-android has no release, and nothing should be documented as
installable until it does.

The source of truth for wording is each component's page in `Documentation/Components/` and its KDoc
(`./gradlew :qeetrix:dokkaGenerate`). The decisions behind it are in
[ADR 0014](../Decisions/0014-form-components.md); the cross-cutting rules (the field look, validation, focus,
secure input, AutoFill) are in [Standards/FORMS.md](../Standards/FORMS.md). Android follows `@qeetrix/react` 0.3.0:
the same props under Kotlin names, the same look from its CSS.

## What to add, page by page

| ui.qeet.in page | `@qeetrix/react` | Android API | One-line summary for the page |
|:--|:--|:--|:--|
| Text field | TextField | `QeetrixTextField` (`contentType` for AutoFill) | Field and Input in one: the label above, the filled box, the help text or the error. |
| Input | Input, Field, Fieldset | `QeetrixField`, `QeetrixFieldSet`, `Modifier.qeetrixFieldControl()` | The same label, help text and error for any other control; a field set is a section of fields under a legend. Qeetrix's inputs carry the parts already. |
| Textarea | Textarea | `QeetrixTextarea` (`rows`, `maxRows`) | The text field's box, growing from `rows` to `maxRows`, then scrolling. No drag handle. |
| Password input | PasswordInput (experimental) | `QeetrixPasswordInput`, `QeetrixPasswordAutoComplete` | Compose's secure field with Show password and a Caps Lock warning; `autoComplete` sets the AutoFill type. |
| OTP input | OTPInput (experimental) | `QeetrixOTPInput`, `QeetrixOTPInputSize`, `QeetrixOTPValidationType` | One native field drawn as slots, with SMS one-time-code AutoFill. |
| Checkbox | Checkbox, CheckboxGroup | `QeetrixCheckbox`, `QeetrixCheckboxGroup` | Checked, unchecked and indeterminate; the labelled row is one 48 dp control; groups as rows or tiles. |
| Radio | RadioGroup, Radio | `QeetrixRadioGroup`, `QeetrixOption<T>` | One typed choice, as rows or tiles. |
| Switch | Switch | `QeetrixSwitch` | The square switch with its label after it; the whole row toggles. |
| Select | Select | `QeetrixSelect`, `QeetrixSelectSize`, `QeetrixOption<T>`, `QeetrixOptionGroup<T>` | A field-styled trigger and Qeetrix's own list in a popup below it, with groups and meta. |
| Number field | NumberField (experimental) | `QeetrixNumberField` (`Int?` or `BigDecimal?`) | Exact values, locale-aware typing, `min` / `max`, `step`, `largeStep` and steppers; arrow keys step. |
| Form | Form, ErrorSummary (experimental) | `QeetrixForm`, `QeetrixFormErrorSummary`, `QeetrixFormError`, `QeetrixFormActions` | The form's column and its error summary; the app owns the values and the validation. |

Not on Android yet, so say so on those pages if they gain an Android section: Combobox, Slider, File upload, and
the experimental Code field, Copy field, Color field and Multi-email field.

## Examples to show

Use the examples from the component pages verbatim; `sample/src/test/kotlin/com/qeetgroup/qeetrix/sample/DocumentationExamplesTest.kt`
compiles and composes them. Shortened forms, for a page that needs only one line:

```kotlin
// Password input
QeetrixPasswordInput(password, label = "Password", autoComplete = QeetrixPasswordAutoComplete.CurrentPassword)

// OTP input
QeetrixOTPInput(code, label = "Verification code", groups = listOf(3, 3), onValueComplete = { verify(it) })

// Select
QeetrixSelect(region, { region = it }, regions.map { QeetrixOption(it.id, it.name) },
    label = "Data region", placeholder = "e.g. Mumbai")

// Number field
QeetrixNumberField(seats, { seats = it }, label = "Seats", min = 1, max = 250)
```

## Differences each page should state

Write each of these as a platform note, not as a limitation:

- **Association.** Compose has no element ids, so a Qeetrix input draws its label inside its own control
  and TalkBack reads it as the control's name; tapping the label reaches the control.
- **Required.** The web leaves "required" to the native attribute; Compose has none, so Android adds the word to
  the name TalkBack reads.
- **State.** The app owns every value: a `TextFieldState` for text, `value` and `onValueChange` for choices and
  numbers. Validation is the app's: the web's Form reads invalid rows from the page, Android's takes the app's
  `errors`.
- **Help.** A field's `help` opens below its ⓘ button on a phone, where the web opens it beside the label.
- **Select.** Qeetrix's own list in a popup, opening below the trigger (above when there is no room); options are
  at least 48 dp. Typing to jump to an option, `multiple` and a controlled `open` are not ported.
- **Radio group.** Arrow keys move focus between options (Android's focus traversal) and Enter or Space selects;
  the web's arrows move and select at once.
- **Password.** Show password is a toggle whose state TalkBack reads, where the web announces it in a live region.
  It is not hidden on submit: Android has no form submission to hook. Caps Lock is only known from a hardware
  keyboard.
- **OTP.** One field, not one per slot, so SMS AutoFill, paste and TalkBack treat the code as one value. The slots
  keep left-to-right order in right-to-left layouts, and keep shrinking in a narrow column.
- **Number field.** The device's locale sets separators and digits; the steppers are not keyboard stops (TalkBack
  gets Decrease and Increase as actions on the field).
- **Touch targets.** Checkbox, radio and switch rows and select options are 48 dp; error-summary items are full
  48 dp rows.

## Images

Don't take screenshots from the repository: screenshot baselines are never committed
([ADR 0012](../Decisions/0012-visual-regression-testing.md)). To make images for the site, run the sample
app (`./gradlew :sample:installDebug`) on an emulator or device and open Forms & inputs → Text field, Field,
Textarea, Password input, OTP input, Checkbox, Radio group, Switch, Select, Number field and Form, and the home
screen's Account preferences. Use the synthetic content already on those pages; don't add real names, codes or
passwords. Leave password fields masked.

## Verification status to quote

- **Verified on the JVM (Robolectric, Android 16 runtime):** behaviour and semantics tests for every
  component, screenshot comparisons in light, dark, 200 % text and right to left, and the sample's form
  integration tests.
- **Not yet verified:**
  - TalkBack speech on a device;
  - AutoFill offers (passwords, SMS codes) from a real provider;
  - hardware keyboards and D-pads on a device.

Don't describe these as tested until they are.
