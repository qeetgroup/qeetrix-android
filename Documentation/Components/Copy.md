# Copy button and copy field

`QeetrixCopyButton`, `QeetrixCopyField`, `QeetrixCopyFieldLayout`, `QeetrixCopyState` · `components.copy` ·
`@qeetrix/react`: CopyButton, CopyField (experimental) · QTRX-122

Copies text to the clipboard: an ID, a URL to paste elsewhere, a secret shown once.

```kotlin
QeetrixCopyButton({ client.id }, label = "Copy client ID")
QeetrixCopyField("ACS URL", acsUrl, description = "Paste it into your identity provider.")
QeetrixCopyField("Signing secret", secret, layout = QeetrixCopyFieldLayout.Row, sensitive = true)
```

## API

| `@qeetrix/react` | Android |
|:--|:--|
| CopyButton `value` (a string or a function) | `value: () -> String`, read at the moment of the copy |
| `label` ("Copy" by default), `children` | `label` (names an icon-only button), `text` (a visible label: a Button) |
| `variant`, `tone` (default `neutral`), `size` | the same, from Button; default ghost, neutral, `Md` |
| `onCopied`, `onCopyError`, `resetAfter` (2 s) | `onCopied`; a failure shows on the button; 2 s |
| — | `sensitive`: marks the clipboard entry sensitive |
| CopyField `label`, `value`, `description` | the same |
| `layout`: `field` · `row` | `layout: QeetrixCopyFieldLayout` — `Field`, `Row` |
| `wrap`: `anywhere` · `none` | `wrap: Boolean` (`true`) |

## Look (copy-button.css, copy-field.css)

- **Button**: an icon button with the sharp `copy` icon. After a copy the icon is a `check` in `color.icon.success`
  for 2 s; where the clipboard refuses, a `circle-x` in `color.icon.danger` (ghost only, as the CSS).
- **Field**: the label (`label.sm`, `color.text.subtle`) `space.gap.xs` above a box at least `size.control.lg` tall,
  with no fill and a 1 dp `color.border.strong` bottom line, `space.inset.md` before the value and `space.inset.xs`
  after the button; the value in `code.sm`, wrapping anywhere; the description in `body.sm` under it.
- **Row**: the label in the first 30 % (at least 128 dp), the value and Copy after it, `space.gap.sm` above and below,
  no line.

## Accessibility

The icon-only button is named by `label`; a field's button "Copy {label}" (`copyField.copy`). After a copy
"Copied" (`copyButton.copied`), or "Couldn't copy", is announced politely as the button's state. A long press
selects the value.

## Platform differences

- **Confirmation.** Android 13 and later confirm a copy themselves; below them a short toast says "Copied". A light
  haptic (`QeetrixHapticRole.LightImpact`) confirms it on every version.
- **Sensitive entries** (`sensitive`): `ClipDescription.EXTRA_IS_SENSITIVE`, so Android 13 and later hide the value in
  the clipboard preview. The web has no equivalent.
- **No tooltip.** The web shows `label` and "Copied" as a tooltip; on Android the name is read by TalkBack
  (Tooltip on long press is QTRX-156).

Related: [Button](Button.md), [Icon button](IconButton.md), [Haptics](../Foundations/HAPTICS.md).
