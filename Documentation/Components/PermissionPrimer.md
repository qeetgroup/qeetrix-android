# Permission primer

`QeetrixPermissionPrimer`, `QeetrixPermission`, `QeetrixPermissionStatus` · `components.permission` · `@qeetrix/react`: none · QTRX-135

Says why the app needs a permission before the system asks.

```kotlin
QeetrixPermissionPrimer(QeetrixPermission.Camera, title = "Scan the QR code", description = "…", onContinue = { launcher.launch(CAMERA) }, onNotNow = ::later)
```

## Look, behaviour and accessibility

- An [empty state](EmptyState.md) (section size) with the permission's icon (camera, map pin, bell, image, contact), the title, the reason, then **Continue** and **Not now**.
- `Denied`: **Open Settings** (the app's Settings page, or `onOpenSettings`) and the `deniedDescription`. Check the
  permission again when the screen resumes (`LifecycleResumeEffect`): the person may have turned it on in Settings.
- Qeetrix asks for nothing itself; the app keeps the launcher and decides the status.

Tried on the phone emulator (`qeetrix-api36`, Android 16) on 31 October 2026, in light, dark, 200 % text and right to left, from the sample app's page, asking for notifications for real: the system's question, the refused state with Open Settings, and the granted state on coming back.
