# Number field

`QeetrixNumberField` · `components.numberfield` · `@qeetrix/react`: NumberField (`experimental`)
([Number field](https://ui.qeet.in/components/number-field)) · sample: Forms → Number field

A number, typed or stepped, in the Input box every field shares. Exact decimals (`BigDecimal`), or whole numbers
(`Int`).

```kotlin
var seats by rememberSaveable { mutableStateOf<Int?>(5) }
QeetrixNumberField(seats, { seats = it }, label = "Seats", min = 1, max = 250)

var discount by rememberSaveable { mutableStateOf<BigDecimal?>(BigDecimal("12.5")) }
QeetrixNumberField(discount, { discount = it }, label = "Discount", endAdornment = { QeetrixText("%") },
    min = BigDecimal.ZERO, max = BigDecimal(100), step = BigDecimal("0.5"), fractionDigits = 1)
```

## API

| `@qeetrix/react` | Android |
|:--|:--|
| `value` / `onValueChange` (number or null) | `value: BigDecimal?` or `Int?` / `onValueChange` |
| `min`, `max`, `step`, `largeStep` (10) | `min`, `max`, `step`, `largeStep` (10) |
| `format` (`Intl.NumberFormatOptions`) | `fractionDigits` — the decimals; the device's locale formats |
| `steppers` (default on) | `steppers = true` |
| `startAdornment`, `endAdornment` | the same — units such as ₹, %, "minutes" |
| `placeholder`, Field props | as [Text field](TextField.md) |

Old `range` became `min` / `max`; the steppers moved from both ends to the end.

## Look (number-field.css)

The Input box (filled, bottom line, 4 dp top corners, `size.control.lg`), the value at the start in tabular
figures, and the − and + steppers at the end behind a 1 dp `color.border.default` hairline: `size.control.sm`
squares, 2 dp apart, 4 dp corners, `color.icon.default` (`color.icon.disabled` at an end),
`color.overlay.hover` / `color.overlay.pressed`. The box's end padding is `space.gap.xs` with steppers,
`space.inset.md` without.

## Behaviour

- Typing keeps its own text, so a lone minus or a trailing separator is never rewritten. The value follows the
  text while it is a number within `min` and `max`; leaving the field or the keyboard action settles it (clamps,
  rounds, formats). Range errors are corrected, not reported.
- Keyboard: Up / Down step by `step`; Shift, Page Up and Page Down by `largeStep`; Home and End go to `min` and
  `max`. The steppers are not keyboard stops.

## Accessibility

TalkBack gets Decrease and Increase (`qx_decrease`, `qx_increase`) as actions on the field itself; the steppers
are named the same. Each stepper's touch area runs the field's full height (40 dp at the default density) though
it is drawn 28 dp square.

## Platform differences

- **Role description.** The web reads "Number field"; Compose has no role description, so TalkBack reads an
  edit box.
- **Locale.** The device's locale sets separators and digits; the web takes QeetrixProvider's (en-IN lakhs).
- **Steppers** are 28 × 40 dp targets, under Android's 48 dp: the field's actions are the accessible path.

Related: [Text field](TextField.md), [Field](Field.md).

## Format and locale (QTRX-193)

`currency` (ISO 4217) shows the currency's symbol where the locale writes it (₹ before in `en-IN`, € after in `de-DE`) and its fraction digits; `percent` makes the value a share (0.25 shown and typed as 25 beside %), `min`, `max` and a `step` under 1 in shares; `grouping` and `locale` as the web's options. The marks are adornments rather than part of the text, so typing stays a plain number.

Money is written with its currency's decimals in full, as Intl's currency style does ("1,284,500.00", "1.234,50" in `de-DE`); other numbers drop trailing zeros ("12.5"). Found on the phone emulator on 31 October 2026, where an amount read "1.234,5 €" (`moneyIsWrittenWithItsCurrencysDecimalsAndSign`).
