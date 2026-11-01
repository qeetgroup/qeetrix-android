# QR code

`QeetrixQRCode`, `QeetrixQRCodeLevel` · `components.qrcode` · `@qeetrix/react`: QRCode (experimental) · QTRX-125

A QR code a camera can read: an `otpauth://` link, a UPI payment link.

```kotlin
QeetrixQRCode(otpauthUri, label = "QR code to add Acme to your authenticator app")
```

## Look, behaviour and accessibility

- **The web's encoder, ported step for step** (`QeetrixQREncoder`): byte mode (UTF-8), versions 1–40, levels L/M/Q/H, Reed–Solomon, the eight masks and four penalty rules. `QeetrixQREncoderTest` compares 28 cases (versions 1–21, every level) with `@qeetrix/react` 0.4.0's own `encodeQr` output, module for module (`Tools/QrCases`).
- **Always dark on white** (qr-code.css `color-scheme: light`): the light `surface.default` tile, a 1 dp `border.subtle` edge, `radius.control`, the light `text.default` modules, the four-module quiet zone inside the tile, whole-pixel modules where they fit.
- **Accessibility**: an image named `label`. Pair it with the same thing as text for anyone who can't scan, in a [copy field](Copy.md).
- A value too long for its level throws `IllegalArgumentException` (the web's RangeError).

Tried on the phone emulator (`qeetrix-api36`, Android 16) on 31 October 2026, in light, dark, 200 % text and right to left, from the sample app's page: dark modules on white with a quiet zone in dark mode too, at every error-correction level.
