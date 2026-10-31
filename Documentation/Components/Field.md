# Field

`QeetrixField`, `QeetrixFieldSet` · `components.input` · `@qeetrix/react`: Field, Fieldset
([Input](https://ui.qeet.in/components/input)) · sample: Forms → Field

The label, help text and error for a control Qeetrix does not provide, and a section of fields under a heading.
Every Qeetrix input is built from the same parts — the label, the filled box, the description and the error —
so every field reads and behaves the same.

```kotlin
QeetrixField(label = "Notification tone", description = "How alerts sound.", error = toneError) {
    ToneSwatches(selected, onSelect, Modifier.qeetrixFieldControl())
}

QeetrixFieldSet("Billing address", description = "Where we send invoices.") {
    QeetrixTextField(line1, label = "Address line 1")
    QeetrixTextField(line2, label = "Address line 2", optional = true)
}
```

## API

| `@qeetrix/react` | Android |
|:--|:--|
| `label` (required) | `label: String` (required) |
| `description`, `error` | `description`, `error: String?` |
| `invalid` | `invalid` — invalid without a message; the description stays |
| `optional` | `optional` — appends "(optional)" (`qx_optional`) |
| `hideLabel` | `hideLabel` — only for a toolbar search with a visible icon |
| `help` | `help: String?` — an ⓘ button after the label opens it in a popover |
| `disabled` | `enabled` (inverted) |
| `children` (render function with aria props) | `content: QeetrixFieldScope.() -> Unit` + `Modifier.qeetrixFieldControl()` |
| Fieldset `legend`, `description`, `disabled` | `QeetrixFieldSet(legend, description, enabled)` |

## Look

- **Label**: `label.sm` in `color.text.subtle`, 4 dp (`space.gap.xs`) above the control; disabled,
  `color.text.disabled`. "(optional)" is `color.text.subtlest` at `body.sm`'s weight. No asterisks.
- **Help text**: `body.sm`, `color.text.subtle`, 4 dp below; disabled, `color.text.subtlest` (it often says
  why the field is disabled, and stays readable).
- **Error**: a sharp 16 dp `circle-alert` in `color.icon.danger`, then the message in `body.sm` /
  `color.text.danger`. It **replaces** the help text while it shows.
- **Field set**: the legend in `title.sm` / `color.text.default`, its description in `body.sm`, and the fields,
  all `space.form.gap` apart (12 / 16 / 20 dp with the density).

## Accessibility

- `qeetrixFieldControl()` names the control by the label ("Notification tone, required" when `required`),
  marks it with the error, and hides the visible label from TalkBack so it is read once. Without it, the label
  is read as text just before the control.
- The error is a live region announced at once; the description is not composed while it shows, so the
  control is described by the error alone.
- A field set's legend is a heading and the set is one traversal group; `enabled = false` disables every
  Qeetrix control inside.
- A choice question is not a field set: use [Radio group](RadioGroup.md) or a checkbox group
  ([Checkbox](Checkbox.md)), whose question looks like every field's label.

## Theme and tokens

`color.text.subtle`, `color.text.subtlest`, `color.text.disabled`, `color.text.danger`, `color.icon.danger`,
`typography.label.sm`, `typography.body.sm`, `typography.title.sm`, `space.gap.xs`, `space.form.gap`
(through the density). Help: `color.surface.overlay`, `color.border.subtle`, `radius.overlay`, `shadow.overlay`,
`space.inset.lg`, `space.gap.md`.

## Platform differences

- **Required.** The web leaves "required" to the native attribute; Compose has none, so the word is added to
  the name TalkBack reads (`qx_required`).
- **Help.** The web opens the popover on the label's inline-end side; on a phone there is no room, so it opens
  below the ⓘ and closes on Back or a tap outside. The ⓘ is `circle-question-mark`, as on the web.
- **Groups and errors.** A checkbox, radio or switch inside a field with an error carries that error for
  TalkBack, as Base UI's `Field.Item` links it on the web.

Related: [Text field](TextField.md), [Form](Form.md), [Checkbox](Checkbox.md), [Radio group](RadioGroup.md).
