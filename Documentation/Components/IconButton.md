# Icon button

`QeetrixIconButton` · `components.button` · web: [Icon Button](https://ui.qeet.in/docs/components/icon-button) ·
sample: Actions → Icon button

An action shown only as an icon — in toolbars, beside fields, in dense rows. It shares the Button's
variants and states.

```kotlin
QeetrixIconButton(QeetrixIconName.X, contentDescription = "Close", onClick = ::dismiss)
QeetrixIconButton(QeetrixIconName.Bell, contentDescription = "Mute notifications", onClick = ::mute,
    variant = QeetrixButtonVariant.Outline, size = QeetrixIconButtonSize.Sm)
```

## Variants and sizes

The [Button](Button.md) variants; `Ghost` is the default. Sizes are squares: `Default` (the density's
control height, 32 dp) and `Lg` 36 dp with a 16 dp icon, `Sm` 28 dp with 16 dp, `Xs` 24 dp with 12 dp.

## States

Disabled and loading as the Button. Loading swaps the icon for the spinner in the same square, so a
toolbar never reflows.

## Accessibility

- `contentDescription` is required: name the action ("Close dialog"), never the icon ("X"). When the
  action changes with state, so does the name ("Mute" / "Unmute").
- A button role; 48 × 48 dp touch target at every size, including the 24 dp `Xs`.

## Theme and tokens

`component.button.*`, as the Button.

## Platform differences

As the [Button](Button.md): a 48 dp layout touch target, no ripple, the Qeet focus ring.

Related: [Button](Button.md).
