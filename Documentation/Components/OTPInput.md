# OTP input

`QeetrixOTPInput` · `components.otpinput` · web: [OTP Input](https://ui.qeet.in/docs/components/otp-input) ·
sample: Forms & inputs → OTP input

A one-time-code field drawn as cells — presentation only.

```kotlin
val code = rememberTextFieldState()
QeetrixOTPInput(code, label = "Verification code", groupSize = 3, description = "Sent to •••• 4821") { completed ->
    verify(completed)   // your app's
}
```

## Usage

- **One field, many cells.** One native text field holds the code; the cells only draw it. Keyboard
  input, paste, the text menu, AutoFill and TalkBack all work on the code as one value.
- The code is the text of your `TextFieldState`, so leading zeros are kept. `length` (default 6) caps it;
  other characters are dropped ("123-456" pastes as 123456); a pasted or autofilled code of full length
  replaces a partial one. Clear it with `state.clearText()`.
- `characters`: `Digits` (default, a number pad; digits in any script become 0–9) or `Alphanumeric`.
- `onComplete` runs each time the code becomes a complete, different code. Nothing is submitted for you.
- `groupSize` draws a divider every so many cells.

## AutoFill and security

- `smsCodeAutofill` (default on) marks the field for SMS one-time-code AutoFill
  (`ContentType.SmsOtpCode`). A device's AutoFill provider may offer the code; that is not guaranteed. Turn
  it off for codes that never come by SMS, such as an authenticator app's.
- Qeetrix never reads messages, asks for permissions or uses SMS Retriever or Play services. An app that
  retrieves a code itself writes it into the state.
- The keyboard is a plain number pad with auto-correction off. (`NumberPassword` is avoided: Compose would
  mark the field a password for AutoFill, and password managers would offer passwords in it.)
- Generating, sending, checking and storing codes are the app's; the component never logs or describes
  the code.

## Accessibility

One field named by its label — "One-time code" (`qx_one_time_code`) without one; the cells are hidden
from TalkBack, which reads the code itself. The error state and announcements are the field's. Each cell
is 32–48 dp wide and the control height + 16 dp tall.

## Theme and tokens

`component.input.*` per cell; Fira Code at `text-lg` with tabular figures; `border.strong` dividers.

## Platform differences

- A code reads left to right in every script, so the cells keep that order in right-to-left layouts, on
  all three platforms.
- `Alphanumeric` is a native addition (also on iOS); the web code is digits only.
- The web moves focus from cell to cell; on Android, as on iOS, one field has one caret and the typing
  cell draws the focus boundary.

Related: [Password input](PasswordInput.md), [Field](Field.md).
