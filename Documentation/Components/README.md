# Components

One page per component: what it is for, how to use it, its states, accessibility, the tokens it draws
from and where Android differs from the web and iOS. The parameter-by-parameter reference is the KDoc,
published by Dokka (`./gradlew :qeetrix:dokkaGenerate`); these pages do not repeat it. ui.qeet.in stays
the organisation-wide documentation.

Grouped as ui.qeet.in groups them (qeetrix-docs `src/lib/component-groups.json`). The sample app has a
page for each, under the same group.

| Group | Component | Kotlin | Package (`com.qeetgroup.qeetrix.components.…`) |
|:--|:--|:--|:--|
| Actions | [Button](Button.md) | `QeetrixButton` | `button` |
| Actions | [Icon button](IconButton.md) | `QeetrixIconButton` | `button` |
| Forms & inputs | [Checkbox](Checkbox.md) | `QeetrixCheckbox`, `QeetrixTriStateCheckbox` | `checkbox` |
| Forms & inputs | [Field](Field.md) | `QeetrixField`, `QeetrixFieldSet` | `input` |
| Forms & inputs | [Form](Form.md) | `QeetrixFormActions`, `QeetrixFormErrorSummary` | `form` |
| Forms & inputs | [Text field](TextField.md) | `QeetrixTextField` | `input` |
| Forms & inputs | [Number field](NumberField.md) | `QeetrixNumberField` | `numberfield` |
| Forms & inputs | [OTP input](OTPInput.md) | `QeetrixOTPInput` | `otpinput` |
| Forms & inputs | [Password input](PasswordInput.md) | `QeetrixPasswordInput` | `passwordinput` |
| Forms & inputs | [Radio group](RadioGroup.md) | `QeetrixRadioGroup` | `radiogroup` |
| Forms & inputs | [Select](Select.md) | `QeetrixSelect` | `select` |
| Forms & inputs | [Switch](Switch.md) | `QeetrixSwitch` | `switchcontrol` |
| Forms & inputs | [Textarea](Textarea.md) | `QeetrixTextarea` | `input` |
| Data display | [Avatar](Avatar.md) | `QeetrixAvatar` | `avatar` |
| Data display | [Badge](Badge.md) | `QeetrixBadge` | `badge` |
| Data display | [Text](Text.md) | `QeetrixText` | `typography` |
| Feedback | [Progress](Progress.md) | `QeetrixProgress` | `progress` |
| Feedback | [Spinner](Spinner.md) | `QeetrixSpinner` | `spinner` |
| Layout | [Card](Card.md) | `QeetrixCard` | `card` |
| Layout | [Separator](Separator.md) | `QeetrixSeparator` | `separator` |

Packages follow the web's component families, not these groups
([ADR 0010](../Decisions/0010-component-api-conventions.md)). New components start from
`Scripts/new-component` ([Templates/Component](../../Templates/Component/README.md)).

## What every component shares

- **One brand, no colour parameters.** Customisation is variant, size and density; `QeetrixText`'s
  `color` takes a theme role.
- **Touch.** Every control reserves a 48 × 48 dp touch area around the web-sized drawing.
- **Feedback.** No ripple: pressed, hovered and focused states use the web's colour and elevation steps,
  and keyboard focus draws the Qeet focus ring.
- **Text** is in sp and follows the font scale; containers grow rather than clip.
- **Right to left.** Layouts mirror; directional icons (send) mirror; progress fills from the start.
- **Reduced motion.** With animations turned off in the system settings, spinners and indeterminate
  progress hold still.
