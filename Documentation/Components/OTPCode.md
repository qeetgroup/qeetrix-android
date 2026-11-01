# One-time code

`QeetrixOTPCode`, `QeetrixOTPCodeSize` · `components.otpinput` · `@qeetrix/react`: none (beside OTPInput) · QTRX-101

A one-time code to read and type elsewhere, with the time it has left: the authenticator's code.

```kotlin
QeetrixOTPCode(code, expiresAt, label = "Acme · priya@acme.in")
```

## Look, behaviour and accessibility

- **Look**: the code in Fira Code, weight 500, tabular figures, grouped 123 456 (1234 5678 for eight digits), at `title.lg`'s size (`Md`) or `heading.md`'s (`Lg`); a [progress ring](ProgressRing.md) emptying to `expiresAt` over the `period` (30 or 60 s) and the seconds beside it, both in the warning tone for the last 5 s. Every frame, once a second under reduced motion.
- **Tap to copy**: a sensitive clipboard entry (Android 13+ hide it in the preview), a light haptic, a toast below Android 13; "Copied" is announced. `hidden = true` shows dots until the first tap.
- **Accessibility**: one button, read as the label, the digits one by one and the time left ("Acme, 4 8 2 1 0 5, 12 seconds left"); its action is "Copy code" ("Show code" while hidden).
- For a 30 s TOTP: `expiresAt = Instant.ofEpochSecond((now.epochSecond / 30 + 1) * 30)`; the app gives the next code when it passes.

Tried on the phone emulator (`qeetrix-api36`, Android 16) on 31 October 2026, in light, dark, 200 % text and right to left, from the sample app's page: the countdown and its warning tone, tap to copy (the clipboard preview shows ••••••, the code marked sensitive), hidden until tapped. The account's name is the app's row's: `label` is what TalkBack reads.
