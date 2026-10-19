# Password input

`QeetrixPasswordInput` · `components.passwordinput` · web: [Password Input](https://ui.qeet.in/docs/components/password-input) ·
sample: Forms & inputs → Password input

A password field with a reveal button, built on Compose's `BasicSecureTextField`.

```kotlin
val password = rememberTextFieldState()
QeetrixPasswordInput(password, label = "Password", purpose = QeetrixPasswordPurpose.Current,
    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done), onKeyboardAction = { signIn() })
```

## Usage

- **Purpose.** `Current` for signing in, `New` for sign-up, reset and change: AutoFill and password
  managers offer saved passwords, or a new one and a save prompt. Whether they do depends on the device and
  the user's provider.
- **Reveal.** The button at the end shows and hides the password in the same field — text, cursor,
  selection, focus and keyboard all stay. `showToggle = false` removes it.
- **Policy is the app's.** Length, strength and matching rules are yours; show them as messages.

## Security

- Masked by default; the last character typed shows briefly only if the device's "Show passwords"
  setting allows. Copy and cut are unavailable; suggestions and auto-correction are off.
- The password lives only in your `TextFieldState`. Qeetrix keeps no copy and never logs, stores, sends or
  describes it — no label, description or accessibility text contains it (tested by scanning every
  semantics node).
- Compose exposes the field's text to accessibility services marked as a password, as for every secure
  field; TalkBack does not speak it unless the user has turned on spoken passwords.
- Qeetrix cannot protect a password from a compromised device or a malicious keyboard, and stores no
  credentials.

## Accessibility

One password field named by its label. The reveal button is named for its action — "Show password" /
"Hide password" (`qx_show_password`, `qx_hide_password`) — and is drawn 24 dp inside the field without
changing its height; Compose's hit testing gives it touches within 48 dp of it.

## Theme and tokens

`component.input.*`; the web's `fieldAction` geometry for the button (max(24 dp, control height − 8 dp),
corner − 2 dp); the `eye` and `eye-off` icons.

## Platform differences

- The web re-masks a revealed password when its form submits, to keep it out of browser form history.
  Android, like iOS, has no form history; the password stays as the person left it.
- The keyboard switches to its visible-password variant while revealed, as Compose's guidance asks.

Related: [OTP input](OTPInput.md), [Text field](TextField.md).
