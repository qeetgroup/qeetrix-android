# Field and field set

`QeetrixField`, `QeetrixFieldSet` · `components.input` · web: [Field](https://ui.qeet.in/docs/components/field) ·
sample: Forms & inputs → Field

The parts every Qeetrix input is made of — a label (with the required asterisk or "(optional)"), the
control, a description and a message, 6 dp apart — and the group around related fields.

```kotlin
QeetrixField(label = "Tone", description = "How notifications sound.", required = true) {
    ToneChooser(tone, onTone, Modifier.qeetrixFieldControl())   // a control Qeetrix does not provide
}

QeetrixFieldSet("Notifications", description = "Choose how we reach you.", message = groupMessage) {
    QeetrixCheckbox(email, { email = it }, label = "Email")
    QeetrixCheckbox(push, { push = it }, label = "Push")
}
```

## Usage

- **Qeetrix's inputs already carry a field.** Give `QeetrixTextField`, `QeetrixTextarea`,
  `QeetrixPasswordInput`, `QeetrixOTPInput`, `QeetrixSelect` and `QeetrixNumberField` their `label`,
  `description`, `message`, `required` and `optional` directly. Use `QeetrixField` only for a control
  Qeetrix does not have.
- **`qeetrixFieldControl()`** goes on the element accessibility services focus. It names the control by
  the label ("Tone, required"), carries the error, and hides the visible label from TalkBack so it is read
  once. A control that shows a value should expose it as its state description. Not for text fields.
- **`QeetrixFieldSet`** groups fields or choices under a legend (`Legend` style, a heading) or a label
  (`Label` style, as a radio group). Fields keep the density's field gap; checkbox, radio and switch rows
  sit flush, each a 48 dp target. Its `message` is about the group ("Choose at least one") and every
  checkbox and radio in it carries it. `enabled = false` disables every Qeetrix control inside.
- Validation is your app's: compute a `QeetrixFieldMessage` and pass it. There is no form model.

## Accessibility

- A Qeetrix input's label is part of the control for TalkBack, as Compose's own text fields do it: the
  label is drawn inside the control. Description and message follow the control in reading order.
- `required` is read as "required" with the label (`qx_required`); the asterisk itself is not read.
- An error message sets the control's error state and is announced at once (a live region); warnings and
  successes are announced politely. Each tone also has its icon and words.
- A legend is a heading, and the set is one traversal group.

## Theme and tokens

`component.input.*` for every field boundary, background and state; `density.field-gap` between fields;
the caption role for descriptions and messages.

## Platform differences

- **Association.** The web links label, description and message to the control by element ids. Compose
  has no references between nodes, so a Qeetrix input draws its label inside its own control (and
  `qeetrixFieldControl()` names another control); iOS passes them through the environment instead.
- **"Required".** Android and iOS add the word to what is read; the web relies on the control's
  `required`.

Related: [Text field](TextField.md), [Form](Form.md), [Checkbox](Checkbox.md), [Radio group](RadioGroup.md).
