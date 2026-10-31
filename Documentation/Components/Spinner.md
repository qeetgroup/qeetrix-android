# Spinner

`QeetrixSpinner`, `QeetrixSpinnerSize`, `QeetrixSpinnerTone` · `components.spinner` · `@qeetrix/react`:
[Spinner](https://ui.qeet.in/components/spinner) · sample: Feedback → Spinner

An indeterminate wait: a 2 dp ring with a quarter clear, turning once every 800 ms.

```kotlin
QeetrixSpinner(label = "Loading invoices")
QeetrixSpinner(size = QeetrixSpinnerSize.Lg, tone = QeetrixSpinnerTone.Brand)
```

## API

| `@qeetrix/react` | Android |
|:--|:--|
| `size`: `sm` 14 · `md` 16 (default) · `lg` 20 · `xl` 24 px | `size: QeetrixSpinnerSize` — `Sm`, `Md`, `Lg`, `Xl`: 14 / 16 / 20 / 24 dp |
| `tone`: `neutral` (default) · `brand` (`default` is a deprecated alias of `neutral`) | `tone: QeetrixSpinnerTone` — `Neutral`, `Brand` |
| `label` (default: QeetrixProvider's `spinner.label`) | `label: String?` (default: the `qx_loading` string) |
| `decorative` | `decorative` |

Removed since 2.x: the turning `loader-circle` icon, the `Default` size and the 16 / 20 / 24 / 32 dp steps.

## Usage

- For a wait of unknown length, such as content loading. When the amount is known, use [Progress](Progress.md);
  inside a button, the button's own `loading`; for the loading, error and empty states of loaded content,
  [Data state](DataState.md), which shows an `Lg` spinner.
- `Neutral` takes the inherited content colour (the web's `currentColor`); `Brand` is `color.icon.brand`.

## Look (spinner.css)

- A 2 dp ring in the tone's colour with its inline-end quarter clear (`border-inline-end-color: transparent`),
  turning once every `2 × motion.duration.slower` (800 ms) with `motion.easing.linear`. Right to left, the clear
  quarter starts on the other side.
- When the user has turned animations off (Settings › Accessibility › Remove animations), the ring is whole and
  fades to 35 % and back every 1.6 s instead, as the web does under `prefers-reduced-motion`, so the wait still
  shows. The 1.6 s is the CSS's own, not a motion token, so it runs although every Qeetrix duration is then 0.

## Accessibility

- Read as an in-progress indicator named by `label` — "Loading" by default, from the `qx_loading` string, which an
  app translates by overriding the resource. Say what is loading when you can.
- `decorative = true` hides it from accessibility services, for a wait something else already announces; a busy
  [Button](Button.md) does this for its own spinner.
- It is not announced as it appears. Announce a wait that matters from the screen that starts it.

## Theme and tokens

`motion.duration.slower`, `motion.easing.linear`, `color.icon.brand` and the content colour. No component tokens.
The 2 dp stroke, the four sizes and the reduced-motion fade (1.6 s, 35 %) are spinner.css's literals, restated
beside each value in the source.

## Platform differences

- **Role and name.** The web's spinner is a `role="status"` element named by QeetrixProvider's strings; Android
  has no status role, so it is an indeterminate progress indicator named by a string resource, and an app
  translates it with Android's resources rather than a provider.

Related: [Progress](Progress.md), [Skeleton](Skeleton.md), [Button](Button.md).
