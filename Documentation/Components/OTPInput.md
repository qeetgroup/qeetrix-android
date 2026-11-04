# OTP input

`QeetrixOTPInput` · `components.otpinput` · `@qeetrix/react`: OTPInput (`experimental`)
([OTP input](https://ui.qeet.in/components/otp-input)) · sample: Forms → OTP input

A one-time code in separate slots, each an Input box, sharing the container's width.

```kotlin
val code = rememberTextFieldState()
QeetrixOTPInput(code, label = "Verification code", size = QeetrixOTPInputSize.Lg, groups = listOf(3, 3),
    error = if (failed) "That code didn't work. Enter the new one we sent." else null,
    onValueComplete = { verify(it) })
```

## API

| `@qeetrix/react` | Android |
|:--|:--|
| `length` (6) | `length = 6` (1–12) |
| `groups` (`[3, 3]`) | `groups: List<Int>?` — must add up to `length` |
| `size`: `md` · `lg` | `size: QeetrixOTPInputSize` — `Md` (default), `Lg` |
| `validationType` (Base UI): `numeric` · `alpha` · `alphanumeric` | `validationType: QeetrixOTPValidationType` — `Numeric`, `Alpha`, `Alphanumeric` |
| `onValueComplete` | `onValueComplete: ((String) -> Unit)?` |
| `aria-label` | `label` (required) with `hideLabel` |
| — | `smsCodeAutofill = true` — SMS one-time-code AutoFill (the web's `autocomplete="one-time-code"`) |

Old `characters` became `validationType`, `groupSize` became `groups`, `onComplete` became `onValueComplete`;
the label is now required.

## Look (otp-input.css)

One filled box per character — `color.bg.neutral.subtle`, a 1 dp `color.border.strong` bottom line, 4 dp top
corners — `space.gap.sm` apart, sharing the width; `Md` slots are `size.control.lg` tall, `Lg` 16 dp more. The
character is `title.md` (`title.lg` at `Lg`) with the body's A11y letterforms and tabular figures. A group break
is a `space.gap.sm` × 2 dp `color.border.strong` rule. The typing slot has the Ember frame and an Ember caret; an
invalid code frames the other slots in `color.border.danger`, the typing slot keeping its focus frame (otp-input.css
0.4.0, QTRX-163). A read-only code has no fill and keeps the `color.border.strong` line, as a read-only field
(QTRX-165). After a failed attempt, clear the code.

## Accessibility

One native text field holds the code; the slots only draw it. TalkBack reads one field named by the label, with
the code as its text; paste, the text menu and AutoFill work on the whole code. Digits typed in any script are
entered as 0–9.

## Platform differences

- **One field, not one per slot.** The web has an input per slot ("Character 2 of 6"); Android keeps one field,
  so SMS AutoFill, paste and TalkBack treat the code as one value. `otpInput.character` is not needed.
- **Narrow columns.** The web stops slots at 24 dp and overflows a column too narrow; Android keeps shrinking.
  07-components (QTRX-164) says a code never goes in a column narrower than it needs (312 dp for 10 characters,
  328 dp for `[5, 5]`); a longer code is a single `QeetrixTextField`.

Related: [Password input](PasswordInput.md), [Field](Field.md).
