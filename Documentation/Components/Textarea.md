# Textarea

`QeetrixTextarea` · `components.input` · web: [Textarea](https://ui.qeet.in/docs/components/textarea) ·
sample: Forms & inputs → Textarea

Multi-line text — notes, feedback, addresses — on the same field parts as the text field.

```kotlin
val feedback = rememberTextFieldState()
QeetrixTextarea(feedback, label = "What could be better?", description = "Up to 500 characters.",
    inputTransformation = InputTransformation.maxLength(500))
```

## Usage

- The text is a `TextFieldState` you own; Enter starts a new line.
- It grows with its text from two control heights (64 dp at default density) to 384 dp, then scrolls
  inside itself. `minLines` and `maxLines` set the height in lines within those bounds.
- Limits and filters are input transformations. Plain text only: no formatting, Markdown or attachments.

## States

Label, required and optional markers, description, the three message tones, read-only (dashed, can be
selected and copied) and disabled — as the [text field](TextField.md).

## Accessibility

One editable text field named by its label; TalkBack reads its lines. The error state and announcements
are the text field's. Text follows the font scale with a 1.5 line height (`leading-normal`).

## Theme and tokens

`component.input.*`; `px-2.5 py-2`; the field text step (16 sp below a 768 dp window, 14 sp above).

## Platform differences

The web textarea can also be resized by dragging its corner; Android, like iOS, grows with the text
instead.

Related: [Text field](TextField.md), [Field](Field.md).
