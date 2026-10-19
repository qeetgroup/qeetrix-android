# Number field

`QeetrixNumberField` · `components.numberfield` · web: [Number Field](https://ui.qeet.in/docs/components/number-field) ·
sample: Forms & inputs → Number field

A locale-aware number with stepper buttons — quantities, seats, percentages.

```kotlin
var seats by rememberSaveable { mutableStateOf<Int?>(5) }
QeetrixNumberField(seats, { seats = it }, label = "Seats", range = 1..250)

var discount by rememberSaveable { mutableStateOf<BigDecimal?>(BigDecimal("12.5")) }
QeetrixNumberField(discount, { discount = it }, label = "Discount (%)",
    range = BigDecimal.ZERO..BigDecimal(100), step = BigDecimal("0.5"), fractionDigits = 1)
```

## Usage

- **Exact values.** `BigDecimal?` (or `Int?`); `null` is empty. Values carry `fractionDigits` decimal
  places (12 in a two-decimal field is 12.00): compare with `compareTo`.
- **Typing** keeps its own text: a lone minus, a trailing separator or an unfinished decimal are never
  rejected or rewritten. The value follows the text whenever it reads as a number in `range`.
- **Committing** — the IME action or leaving the field — settles it: out of range goes to the nearest end,
  decimals round (half to even), text that is not a number goes back to the last value, and the value is
  shown formatted. Range errors are corrected, not reported; report your own rules as a message.
- **Steppers** step by `step` and stop at the ends. Up and Down arrow keys step too.
- Currency, units and business limits are the app's.

## Locale

The device's locale decides the decimal separator, grouping and digits. Reading is strict: "12abc" is
not 12, and a grouping separator counts only where the locale puts one — in German "1.234" is 1234 but
"1234.5" is not a number. Digits in any script are read. The keyboard is a number pad, or a decimal pad
with `fractionDigits` above zero; some number pads have no minus key, so the Decrease button reaches
negative values.

## Accessibility

One editable field named by its label, with Decrease and Increase as custom actions on it; the steppers
are also buttons named "Decrease" and "Increase" (`qx_decrease`, `qx_increase`) but not keyboard stops. In
a right-to-left form the number stays in number order.

## Theme and tokens

The field recipe as a group (`component.input.*`), steppers as wide as the control is tall with
`border.default` rules and `surface.interactive-*` fills, the `minus` and `plus` icons, tabular figures.

## Platform differences

The web also scrubs the value with a pointer drag; Android and iOS do not. Android steps with arrow keys
as the web does; iOS relies on the steppers and the keypad.

Related: [Text field](TextField.md), [Field](Field.md).
