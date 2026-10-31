# Avatar group

`QeetrixAvatarGroup`, `QeetrixAvatarPerson` · `components.avatar` · `@qeetrix/react`: AvatarGroup (experimental) · QTRX-129

A few people at a glance.

```kotlin
QeetrixAvatarGroup(members.take(3).map { QeetrixAvatarPerson(it.name) }, total = group.memberCount)
```

## Look, behaviour and accessibility

- **Look** (avatar-group.css): the first `max` (3) avatars side by side, 2 dp apart — the web's hairline, not an overlap — then +N in a `bg.neutral.subtle-pressed` tile.
- One image named in full: "412 people: Priya Sharma, Rahul Verma, Ananya Iyer and 409 more", counts in the locale's digits.

Tried on the phone emulator (`qeetrix-api36`, Android 16) on 31 October 2026, in light, dark, 200 % text and right to left, from the sample app's page. Found there and fixed: at 200 % text the +N count grew into a bubble larger than the avatars; it is drawn at a font scale of 1, as the initials are (`theCountIsDrawnLikeTheInitialsAtAFontScaleOfOne`).
