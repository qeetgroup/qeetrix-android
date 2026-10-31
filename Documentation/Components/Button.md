# Button

`QeetrixButton`, `QeetrixButtonVariant`, `QeetrixButtonTone`, `QeetrixButtonSize` · `components.button` ·
`@qeetrix/react`: [Button](https://ui.qeet.in/components/button) · sample: Actions → Button

An action. The label comes first; an optional icon trails in its own zone. Write the label as a verb ("Create
invoice").

```kotlin
QeetrixButton("Create invoice", onClick = ::create, icon = QeetrixIconName.Plus)
QeetrixButton("Export", onClick = ::export, variant = QeetrixButtonVariant.Soft)
QeetrixButton("Cancel", onClick = ::cancel, variant = QeetrixButtonVariant.Ghost)
QeetrixButton("Revoke key", onClick = ::revoke, tone = QeetrixButtonTone.Danger, loading = revoking)
```

## API

| `@qeetrix/react` | Android |
|:--|:--|
| `children` (the label) | `text: String` |
| `onClick` | `onClick` |
| `variant`: `solid` (default) · `soft` · `ghost` | `variant: QeetrixButtonVariant` — `Solid`, `Soft`, `Ghost` |
| `tone`: `brand` (default) · `danger` · `neutral` | `tone: QeetrixButtonTone` — `Brand`, `Danger`, `Neutral` |
| `size`: `sm` · `md` (default) · `lg` | `size: QeetrixButtonSize` — `Sm`, `Md`, `Lg` |
| `icon` (trailing, sharp) | `icon: QeetrixIconName?` |
| `loading` | `loading` |
| `fullWidth` | `fullWidth` |
| `disabled` | `enabled` (inverted) |
| `render` | — |
| — | `interactionSource` |

Removed since 2.x: the `Default`, `Outline`, `Secondary`, `Destructive` and `Link` variants (now `Solid`, `Soft`,
`Ghost` and `tone = Danger`), the `Default` and `Xs` sizes, `leadingIcon` and `trailingIcon` (one trailing `icon`),
`loadingLabel` (the label stays while loading) and the overload with a content slot.

## Hierarchy (design rules §7)

| Emphasis | Android | Look | Use |
|:--|:--|:--|:--|
| Primary | `Solid` | Ember fill (`color.bg.brand.bold`), `color.text.on-brand` | The one main action in a region |
| Secondary | `Soft` | Warm graphite fill (`color.bg.neutral.bold`), `color.text.inverse` | The main alternative: Export, Save draft |
| Tertiary | `Ghost` | No fill, `color.text.brand`; `color.bg.brand.subtle` under the pointer, `-pressed` while pressed | Everything else: Cancel, Back, Learn more |
| Destructive | `Solid` + `tone = Danger` | `color.bg.danger.bold`, `color.text.on-bold` | Only the confirming action of a destructive flow; it replaces the primary, never sits beside it |

- A region has at most one `Solid` and one `Soft` button; every other action is `Ghost`.
- `Neutral` is for utility actions in toolbars and headers. A ghost neutral button is `color.text.subtle`, and
  `color.text.default` on `color.bg.neutral.subtle` under the pointer (`-pressed` while pressed); a solid or soft
  neutral button looks like `Soft`.
- `Soft` + `Danger` is the red fill too; `Ghost` + `Danger` is `color.text.danger` with `color.bg.danger.subtle`
  under the pointer and while pressed.
- A button that submits a form, or sits in a row with fields, is `Lg`, so it lines up with them (§6).

## Look (button.css)

| `size` | Height (compact / default / comfortable) | Before the label | Label to rule | Each side of the icon | Rule | Label | Icon |
|:--|:--|:--|:--|:--|:--|:--|:--|
| `Sm` | `size.control.sm`: 24 / 28 / 32 dp | 10 dp | 8 dp | 8 dp | 14 dp (`size.icon.xs`) | `label.sm` | 16 dp |
| `Md` | `size.control.md`: 28 / 32 / 36 dp | 14 dp | 12 dp | 10 dp | 16 dp (`size.icon.sm`) | `label.md` | 16 dp |
| `Lg` | `size.control.lg`: 32 / 40 / 44 dp | 16 dp | 14 dp | 12 dp | 20 dp (`size.icon.md`) | `label.md` | 20 dp |

- 4 dp corners (`radius.control`). With an `icon`, the icon sits at the far end with equal space on either side,
  behind a 1 dp (`border.width.default`) rule as tall as the "Rule" column, centred on the button's height, in the
  label's colour at 55 %: a short seam, not a split. Without an icon there is no zone and no rule, and the label
  has the "Before the label" padding on both sides.
- The label stays on one line and ends in an ellipsis when there is no room. `fullWidth` fills the container,
  keeping the label at the start and the icon at the end.
- **Hover and press** step the fill to its `-hover` and `-pressed` tokens in `motion.duration.fast` with
  `motion.easing.standard`; nothing scales or moves.
- **Disabled**: `color.bg.disabled` (a ghost button keeps no fill) and `color.text.disabled`; the rule follows the
  label's colour.
- **Loading**: a spinner — 16 dp, 20 dp at `Lg` — takes the icon's place in the zone; a button without an icon
  gains the zone. The label and the resting colours stay, and a button with an icon keeps its width.

## Accessibility

- A button named by its label. A disabled button is read as disabled and stays focusable, so the reason it is
  disabled can be read nearby; prefer explaining over disabling.
- Loading ignores taps and keys, so an action is never sent twice. The button stays enabled and focusable and is
  read with the state "Loading" (`qx_loading`), as the web's `aria-busy`; the spinner and the icon are decorative.
- Keyboard focus draws the 2 dp Ember frame (`focus.ring.width`, `color.border.focus`) inside the edge, with a
  1 dp `color.surface.default` gap between the frame and the fill (§10). Touch does not move focus on Android, so
  the frame appears for keyboard, D-pad and switch access only.
- Every size has a 48 × 48 dp touch area. The label follows the font scale and the button grows taller rather
  than clipping.

## Theme and tokens

`color.bg.brand.bold` / `-hover` / `-pressed`, `color.bg.neutral.bold` / `-hover` / `-pressed`,
`color.bg.danger.bold` / `-hover` / `-pressed`, `color.bg.brand.subtle` / `-pressed`,
`color.bg.neutral.subtle` / `-pressed`, `color.bg.danger.subtle`, `color.bg.disabled`; `color.text.on-brand`,
`color.text.inverse`, `color.text.on-bold`, `color.text.brand`, `color.text.danger`, `color.text.subtle`,
`color.text.default`, `color.text.disabled`; `size.control.*` (through the density), `size.icon.xs` / `sm` / `md`,
`radius.control`, `border.width.default`, `typography.label.sm` / `label.md`, `focus.ring.width`,
`color.border.focus`, `color.surface.default`, `motion.duration.fast`, `motion.easing.standard`. `@qeetrix/tokens`
has no `component.button` group; the paddings and gaps are button.css's, restated beside each value in
`QeetrixButtonFrame.kt`.

## Platform differences

- **Touch target.** The drawn button keeps the web's size inside a 48 × 48 dp touch area reserved in layout
  (Compose's `clickable` cannot take touches beyond its bounds), so a small button takes more room than on the web.
- **Feedback.** No ripple: the colour steps are the feedback, as on the web. Hover shows only with a mouse or
  trackpad.
- **Neutral and ghost danger states** follow button.css 0.4.0: a solid neutral button hovers and presses in graphite,
  as soft does (QTRX-157), and a ghost danger press keeps the danger tint (QTRX-158).
- **Ghost neutral hover** is `color.bg.neutral.subtle`, the label turning `color.text.default` (button.css; the spec
  agrees since QTRX-159).
- **Not ported:** `render` and LinkButton — a `QeetrixButton` whose `onClick` navigates is still read as a button;
  the forced-colours border (Android has no forced-colours mode).

Related: [Icon button](IconButton.md), [Spinner](Spinner.md), [Form](Form.md).

## Arrows in right to left (QTRX-200)

Arrows and chevrons no longer mirror by themselves (the icons catalogue marks them `preserve`). A button whose icon means "next" or "back" passes `iconMirrorsInRightToLeft = true`; one that means a physical direction leaves it `false`.
