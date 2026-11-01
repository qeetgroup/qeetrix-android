# Alert

`QeetrixAlert`, `QeetrixAlertTone`, `QeetrixAlertVariant`, `QeetrixAlertAnnounce` · `components.alert` · `@qeetrix/react`: Alert (experimental) · QTRX-104

A message in the page: push not set up, offline, a phone not linked.

```kotlin
QeetrixAlert("Push isn't set up", tone = QeetrixAlertTone.Warning, description = "…", actions = { QeetrixButton("Allow", onClick = ::allow, size = QeetrixButtonSize.Sm) })
```

## Look, behaviour and accessibility

- **Look** (alert.css): `bg.<tone>.subtle`, a 3 dp start edge in `icon.<tone>`, the tone's 16 dp icon on the title's line, the title in `label.md` at weight 600, the description in `body.md` `text.subtle`, the actions under it; Dismiss with `onDismiss`. `Banner` spans the screen with a rule in the tone under it.
- **Never colour alone**: the icon and a word read before the title ("Warning: …", `alert.*`).
- `announce` only for an alert that appears after something happens (polite or urgent). It never takes focus or disappears by itself.

Tried on the phone emulator (`qeetrix-api36`, Android 16) on 31 October 2026, in light, dark, 200 % text and right to left, from the sample app's page: the tone's word read first by TalkBack ("Warning: …"), Dismiss, the banner, and the announced alert.
