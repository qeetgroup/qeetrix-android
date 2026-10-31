# Icon button

`QeetrixIconButton` · `components.button` · `@qeetrix/react`: [IconButton](https://ui.qeet.in/components/icon-button)
· sample: Actions → Icon button

A square button that shows only an icon — in toolbars, headers and dense rows. It takes the
[Button](Button.md)'s variants, tones, sizes and states.

```kotlin
QeetrixIconButton(QeetrixIconName.X, contentDescription = "Close", onClick = ::dismiss, tone = QeetrixButtonTone.Neutral)
QeetrixIconButton(QeetrixIconName.Plus, contentDescription = "New invoice", onClick = ::create, variant = QeetrixButtonVariant.Solid)
QeetrixIconButton(QeetrixIconName.RefreshCw, contentDescription = "Refresh", onClick = ::refresh, loading = refreshing)
```

## API

| `@qeetrix/react` | Android |
|:--|:--|
| `icon` (required, sharp) | `icon: QeetrixIconName` |
| `aria-label` (required by the type) | `contentDescription: String` (required) |
| `onClick` | `onClick` |
| `variant`: `ghost` (default) · `soft` · `solid` | `variant: QeetrixButtonVariant`, `Ghost` by default |
| `tone`, `size`, `loading` | `tone: QeetrixButtonTone`, `size: QeetrixButtonSize`, `loading` |
| `disabled` | `enabled` (inverted) |
| `tooltip`, `tooltipSide` | — (see Platform differences) |
| — | `interactionSource` |

Removed since 2.x: `QeetrixIconButtonSize` (`Default`, `Xs`, `Sm`, `Lg`) — sizes are `QeetrixButtonSize` — and the
old Button variants.

## Look (button.css, `.qx-icon-button`)

- A square as wide as the control is tall — `size.control.sm`, `md` or `lg` (24 / 28 / 32, 28 / 32 / 36 and
  32 / 40 / 44 dp at compact, default and comfortable) — with the icon centred: 16 dp, 20 dp at `Lg`, sharp
  outline. There is no zone and no rule.
- Colours, corners, states and the focus frame are the [Button](Button.md)'s.
- `loading` swaps the icon for a spinner of the same size in the same square, so a toolbar never reflows while one
  of its actions is busy.
- Utility actions in toolbars and headers take `tone = Neutral`: `color.text.subtle`, and `color.text.default`
  on a neutral tint under the pointer, so their icons stay neutral (design rules §16).

## Accessibility

- `contentDescription` is what TalkBack reads: name the action ("Close dialog", "Mute notifications"), never the
  icon ("X"). When the action changes with state, so does the name ("Mute" / "Unmute").
- A button. Loading adds the state "Loading" (`qx_loading`) and ignores activation; disabled stays focusable.
- A 48 × 48 dp touch area at every size, including `Sm` at compact (24 dp). The square is fixed: icons are sized
  in dp and do not grow with the font scale.

## Theme and tokens

The [Button](Button.md)'s.

## Platform differences

- **No tooltip.** The web shows the `aria-label` as a tooltip on hover and keyboard focus (`tooltip`,
  `tooltipSide`); Android draws none — touch has no hover, and TalkBack reads the content description. Where people
  need to see the name, use a [Button](Button.md) with a label.
- **Touch target, feedback, focus**: as the [Button](Button.md) — a 48 dp touch area reserved in layout, no ripple,
  the Ember frame inside the edge for keyboard focus.

Related: [Button](Button.md), [Top app bar](TopAppBar.md), [Menu](Menu.md).

## Tooltip (QTRX-156)

With `tooltip` (default `true`, as the web's IconButton), the name shows in a tooltip after a 500 ms hover, while it has keyboard focus, and for 1.5 s after a long press (tooltip.css: `surface.inverse`, `text.inverse`, `body.sm`, 4 × 8 dp, `radius.tooltip`, at most 280 dp, 8 dp above the button). The tooltip is hidden from TalkBack, which reads the name already. `iconMirrorsInRightToLeft` mirrors a navigation arrow (QTRX-200). The tooltip keeps `space.inset.sm` (8 dp) inside the window, as menus do: on the phone emulator a tooltip for a button at the screen's edge was pressed against it (`theTooltipKeepsAMarginInsideTheWindow`).
