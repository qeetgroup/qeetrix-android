# Bottom action bar

`QeetrixBottomActionBar` · `components.actionbar` · `@qeetrix/react`: none · QTRX-132

A screen's main actions pinned to the bottom.

```kotlin
QeetrixBottomActionBar(scroll = scroll) {
    QeetrixButton("Deny", onClick = ::deny, variant = QeetrixButtonVariant.Soft, fullWidth = true)
    QeetrixButton("Approve", onClick = ::approve, fullWidth = true)
}
```

## Look, behaviour and accessibility

- Above the keyboard when it's open, above the gesture area otherwise; a 1 dp `border.subtle` rule only while content scrolls under it.
- On a phone the actions share the width, and stack full width when a label wouldn't fit (large text); from `breakpoint.medium` they sit at the end.
- At most one solid and one soft action (design rules §7), the primary last.

Tried on the phone emulator (`qeetrix-api36`, Android 16) on 31 October 2026, in light, dark, 200 % text and right to left, from the sample app's page: the rule while content scrolls under it, the shared width, the primary last (on the left in right to left).
