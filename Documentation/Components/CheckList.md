# Check list

`QeetrixCheckList`, `QeetrixCheckItem`, `QeetrixCheckStatus` · `components.checklist` · `@qeetrix/react`: CheckList, CheckItem (experimental) · QTRX-131

Requirements and their results: password rules, setup checks.

```kotlin
QeetrixCheckList {
    QeetrixCheckItem("At least 12 characters", QeetrixCheckStatus.Passed)
}
```

## Look, behaviour and accessibility

- **Look** (check-list.css): a ruled `surface.raised` sheet; each check 48 dp, its icon (`circle-check`, `circle-x`, `circle-dashed`, a spinner), title `label.md`, detail `body.sm`, and the result in words at the end.
- Each check is one element read with its result ("At least 12 characters, Passed"); changes are announced politely.

Tried on the phone emulator (`qeetrix-api36`, Android 16) on 31 October 2026, in light, dark, 200 % text and right to left, from the sample app's page: each status in words. Like the web's `.qx-check-item { align-items: center }`, a check's icon and status are centred on the whole item, not its first line (QTRX-215).
