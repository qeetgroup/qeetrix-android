# Password input

`QeetrixPasswordInput` · `components.passwordinput` · `@qeetrix/react`: PasswordInput (`experimental`)
([Password input](https://ui.qeet.in/components/password-input)) · sample: Forms → Password input

A password or secret field with a Show password toggle, in the [text field](TextField.md)'s box with its label,
help text and error.

```kotlin
val password = rememberTextFieldState()
QeetrixPasswordInput(password, label = "Password",
    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done), onKeyboardAction = { signIn() })
```

## API

| `@qeetrix/react` | Android |
|:--|:--|
| `autoComplete`: `current-password` · `new-password` · `off` | `autoComplete: QeetrixPasswordAutoComplete` — `CurrentPassword` (default), `NewPassword`, `Off` |
| `visible` / `defaultVisible` / `onVisibleChange` | `visible: Boolean?` (null: the field keeps its own, hidden at first) / `onVisibleChange` |
| `capsLockWarning` (default on) | `capsLockWarning = true` |
| `endAdornment` (tools before the toggle) | `endAdornment` |
| Field and Input props | as [Text field](TextField.md) |

Old `purpose` became `autoComplete`; `showToggle` is gone (the toggle is always there, as on the web).

## Behaviour

- **Show password**: a pressed-state toggle (`qx_show_password`), eye ↔ eye-off, a small neutral icon button
  at the end. It shows the password in the same field — text, cursor, focus and keyboard stay. TalkBack reads its
  state as "Password shown" or "Password hidden" (`qx_password_shown`, `qx_password_hidden`).
- **Caps Lock**: while a hardware keyboard types with Caps Lock on, "Caps Lock is on" (`qx_caps_lock`) shows
  before the toggle in `label.sm` / `color.text.warning`.
- **Secure entry**: Compose's `BasicSecureTextField` — masked (the last character shows briefly when the
  device allows), copy and cut off, suggestions, spelling and capitalisation off, marked as a password for
  TalkBack. Paste and password managers always work: never cap its length or block paste (WCAG 3.3.8).

## Platform differences

- **Announcement.** The web announces "Password shown" in a polite live region; Android makes it the toggle's
  state, which TalkBack reads after the toggle changes.
- **Hiding on submit.** The web hides the password before the browser reads the form; Android has no form
  submission to hook, and the app owns the state.
- **Caps Lock** is only known from hardware keyboards; soft keyboards do not report it.
- **`Off`** marks the field as not for AutoFill (`ContentDataType.None`).
- The Caps Lock warning starts with a 14 dp `arrow-big-up`, as on the web.

Related: [Text field](TextField.md), [OTP input](OTPInput.md).
