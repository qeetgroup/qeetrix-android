# Spinner

`QeetrixSpinner` · `components.spinner` · web: [Spinner](https://ui.qeet.in/docs/components/spinner) ·
sample: Feedback → Spinner

An indeterminate busy indicator: the `loader-circle` icon turning once a second.

```kotlin
QeetrixSpinner(label = "Loading invoices")
QeetrixSpinner(size = QeetrixSpinnerSize.Sm)
```

## Usage

- For a wait of unknown length. When the amount is known, use [Progress](Progress.md); inside a button,
  use the button's own `loading` state.
- Sizes: `Sm` 16, `Default` 20, `Lg` 24, `Xl` 32 dp. Colour: the muted foreground.

## Accessibility

- Read as an in-progress indicator named by `label` — "Loading" by default, from the `qx_loading`
  string, which an app translates by overriding the resource. Say what is loading when you can.
- A spinner appearing is not announced (nor is it on the web or iOS); announce a wait that matters
  from the screen that starts it.
- With animations turned off it stands still.

## Theme and tokens

Semantic tokens only (the muted text colour); there is no `component.spinner` group.

## Platform differences

None beyond the shared ones: the default label is an Android string resource, as iOS uses its
`Localizable.strings` and the web its message catalogue.

Related: [Progress](Progress.md), [Button](Button.md).
