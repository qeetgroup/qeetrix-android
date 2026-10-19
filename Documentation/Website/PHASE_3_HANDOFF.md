# ui.qeet.in handoff: Android inputs and forms

For whoever maintains the component pages on ui.qeet.in (`qeetrix-docs`). It lists what Qeetrix for
Android now provides for each web component in the Forms & inputs group, so a page can gain an
**Android** section. Nothing here has been published: qeetrix-android has no release, and nothing should
be documented as installable until it does.

The source of truth for wording is each component's page in `Documentation/Components/` and its KDoc
(`./gradlew :qeetrix:dokkaGenerate`). The decisions behind it are in
[ADR 0014](../Decisions/0014-form-components.md); the cross-cutting rules (validation, focus, secure input,
AutoFill) are in [Standards/FORMS.md](../Standards/FORMS.md).

## What to add, page by page

| ui.qeet.in page | Android API | One-line summary for the page |
|:--|:--|:--|
| Field | `QeetrixField`, `QeetrixFieldSet`, `Modifier.qeetrixFieldControl()` | The same label, description and message parts. Qeetrix's inputs carry them already; `QeetrixField` is for any other control. |
| Input | `QeetrixTextField` (`contentType` for AutoFill) | Phase 2's field, now on the shared field parts; "required" is read with the label. |
| Textarea | `QeetrixTextarea` | Grows from two control heights to 384 dp, then scrolls. No drag handle. |
| Password Input | `QeetrixPasswordInput`, `QeetrixPasswordPurpose` | Compose's secure field with a reveal button; `Current` or `New` sets the AutoFill type. |
| OTP Input | `QeetrixOTPInput`, `QeetrixOTPCharacters` | One field drawn as cells, with SMS one-time-code AutoFill. Digits, or alphanumeric as a native addition. |
| Checkbox | `QeetrixCheckbox`, `QeetrixTriStateCheckbox` | Checked, unchecked and mixed; the labelled row is one 48 dp control. |
| Radio Group | `QeetrixRadioGroup`, `QeetrixOption<T>` | One typed choice; each option is its own focus stop. |
| Select | `QeetrixSelect`, `QeetrixSelectSize`, `QeetrixOption<T>` | A field-styled trigger and Qeetrix's own list in a popup, below the trigger. |
| Number Field | `QeetrixNumberField` (`Int?` or `BigDecimal?`) | Exact values, locale-aware typing, range, step and steppers; arrow keys step. |
| Form | `QeetrixFormActions`, `QeetrixFormErrorSummary`, `QeetrixFormError` | Button placement and the error summary. No form container: the app owns values and validation. |

Not on Android yet, so say so on those pages if they gain an Android section: Native Select, Input Group,
Password Strength Meter, Checkbox Card, Radio Card, and the other Forms & inputs components.

## Examples to show

Use the examples from the component pages verbatim. Each one is compiled and composed by
`sample/src/test/kotlin/com/qeetgroup/qeetrix/sample/DocumentationExamplesTest.kt`, so they stay correct.
Shortened forms, for a page that needs only one line:

```kotlin
// Password Input
QeetrixPasswordInput(password, label = "Password", purpose = QeetrixPasswordPurpose.Current)

// OTP Input
QeetrixOTPInput(code, label = "Verification code", groupSize = 3) { completed -> verify(completed) }

// Select
QeetrixSelect(region, { region = it }, regions.map { QeetrixOption(it.id, it.name) },
    label = "Data region", placeholder = "Choose a region")

// Number Field
QeetrixNumberField(seats, { seats = it }, label = "Seats", range = 1..250)
```

## Differences each page should state

Write each of these as a platform note, not as a limitation:

- **Association.** Compose has no element ids, so a Qeetrix input draws its label inside its own control
  and TalkBack reads it as the control's name.
- **State.** The app owns every value: a `TextFieldState` for text, a value and a callback for choices and
  numbers. Validation is the app's; Qeetrix shows the message.
- **Select.** Qeetrix's own list in a popup, opening below the trigger (above when there is no room);
  the web aligns the selected item over the trigger for a mouse.
- **Radio group.** Each option is its own keyboard focus stop, as on iOS, not one stop with arrow keys.
- **Password.** It stays revealed after submit: Android has no browser form history to protect.
- **OTP.** Cells keep left-to-right order in right-to-left layouts, and one caret moves through them.
- **Number field.** No pointer scrubbing.
- **Touch targets.** Checkbox, radio, switch and select-option rows are 48 dp; error-summary entries are
  full 48 dp rows.

## Images

Don't take screenshots from the repository: screenshot baselines are never committed
([ADR 0012](../Decisions/0012-visual-regression-testing.md)). To make images for the site, run the sample
app (`./gradlew :sample:installDebug`) on an emulator or device and open Forms & inputs → Field, Textarea,
Password input, OTP input, Checkbox, Radio group, Select, Number field and Form, and the home screen's
Account preferences. Use the synthetic content already on those pages; don't add real names, codes or
passwords. Leave password fields masked.

## Verification status to quote

- **Verified on the JVM (Robolectric, Android 16 runtime):** behaviour and semantics tests for every
  component, screenshot comparisons in light, dark, 200 % text and right to left, and the sample's form
  integration tests.
- **Not yet verified:**
  - TalkBack speech on a device;
  - AutoFill offers (passwords, SMS codes) from a real provider;
  - hardware keyboards and D-pads on a device;
  - the instrumented tests (no emulator or device was available).

Don't describe these as tested until they are.
