# Button

`QeetrixButton` · `components.button` · web: [Button](https://ui.qeet.in/docs/components/button) ·
sample: Actions → Button

The Qeet action control. Use one default (primary) button per view; the other variants rank below it.

```kotlin
QeetrixButton("Save changes", onClick = ::save, loading = saving, loadingLabel = "Saving…")
QeetrixButton("Delete", onClick = ::delete, variant = QeetrixButtonVariant.Destructive, leadingIcon = QeetrixIconName.X)
QeetrixButton(onClick = ::openInbox, variant = QeetrixButtonVariant.Outline) {
    QeetrixText("Inbox", variant = QeetrixTextVariant.Small)
    QeetrixBadge("12", variant = QeetrixBadgeVariant.Secondary)
}
```

## Variants and sizes

| Variant | Use |
|:--|:--|
| `Default` | The primary action, in Qeet orange. |
| `Outline` | A bordered secondary action. |
| `Secondary` | A filled, quiet secondary action. |
| `Ghost` | No surface until pressed: toolbars, low-emphasis actions. |
| `Destructive` | Destroys data: a quiet red tint that turns solid on press. |
| `Link` | An action that reads as part of a sentence; underlines on press and hover. |

Sizes: `Default` (the density's control height, 32 dp at default density), `Xs` 24 dp, `Sm` 28 dp,
`Lg` 36 dp. Heights are minimums: larger text grows the button.

## States

- **Disabled** (`enabled = false`): faded (the primary turns graphite); cannot be activated.
- **Loading**: keeps its colour, label and focus; a spinner takes the leading slot; taps are ignored
  so an action is never sent twice. With `loadingLabel`, the label is replaced, and a loading label no
  longer than the label keeps the width exactly.
- **Pressed / hovered / focused**: the web's colour steps; keyboard focus draws the focus ring.

## Accessibility

- A button role, named by its label (or by `loadingLabel` while loading). Loading adds the state
  "Loading" (`qx_loading`); the button stays enabled and focusable, as the web's `aria-busy`.
- 48 × 48 dp touch target at every size. Icons are decorative.

## Theme and tokens

`component.button.*` (`QeetrixButtonComponentTokens`): backgrounds, foregrounds and borders per
variant and state, corners per size, and the height through the density. Light and dark follow the
theme.

## Platform differences

- **Touch target.** The web's controls are pointer-sized; iOS extends the hit area to 44 pt without
  moving layout. Android reserves 48 dp in layout (Compose's `clickable` cannot extend its hit area
  beyond its bounds), so a small button takes more room than on the web.
- **Feedback.** No ripple; the web's pressed colour step is the feedback, as on iOS.
- **Focus.** iOS leaves keyboard focus to the system; Android draws the Qeet focus ring, as the web.
- **Not implemented:** the web's `invalid` styling (`aria-invalid`), which no Phase 2 form needs.

Related: [Icon button](IconButton.md), [Spinner](Spinner.md).
