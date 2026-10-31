# QR scanner

`QeetrixQRScanner`, `QeetrixScannerState` · `components.scanner` · `@qeetrix/react`: none · QTRX-103

A QR scanner's screen over the app's own camera preview: no camera dependency in Qeetrix.

```kotlin
QeetrixQRScanner(state, torch = torch, onTorchChange = { torch = it }, onEnterKey = ::typeKey, onAllowCamera = ask) {
    AndroidView(factory = { PreviewView(it).also(bindCamera) })
}
```

## Look, behaviour and accessibility

- `Scanning`: the preview under `color.overlay.scrim` with a clear target (70 % of the shorter side, at most 280 dp) and four 3 dp `border.brand` corners; the guidance in `text.on-bold`; the torch toggle when `onTorchChange` is given.
- `PermissionNeeded` / `PermissionDenied`: the camera's [permission primer](PermissionPrimer.md), centred, without Not now —
  the way out is "Type the key instead" or the app's Back. `NoCamera`: an empty state. Check the permission again when
  the screen resumes: the person may have turned the camera on in Settings.
- "Type the key instead" (`keyboard`) in every state with `onEnterKey`. The bottom actions keep clear of the navigation bar; the preview is hidden from accessibility services.

Tried on the phone emulator (`qeetrix-api36`, Android 16) on 31 October 2026, in light, dark, 200 % text and right to left, from the sample app's page, asking for the camera for real. Found there and fixed: the permission states hid "Type the key instead" and wired the primer's Not now to it; the permission and no-camera states sat at the top of the scanner's area; the guidance overlapped the target in a short box; and the controls sat on the translucent scrim, where over a white page white text is 2.8:1 — they carry their own fills now (`QeetrixQRScannerTest`).
