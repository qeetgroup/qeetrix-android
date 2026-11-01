# Textarea

`QeetrixTextarea` · `components.input` · `@qeetrix/react`: Textarea in a Field
([Textarea](https://ui.qeet.in/components/textarea)) · sample: Forms → Textarea

Multi-line text with the look and states of the [text field](TextField.md), for notes, feedback and addresses.

```kotlin
val feedback = rememberTextFieldState()
QeetrixTextarea(feedback, label = "What could be better?", maxRows = 8,
    description = "Up to 500 characters.", inputTransformation = InputTransformation.maxLength(500))
```

## API

| `@qeetrix/react` | Android |
|:--|:--|
| `rows` (default 3) | `rows: Int = 3` — the height before it grows |
| `maxRows` | `maxRows: Int?` — then it scrolls; without it, it keeps growing |
| Field and Input props | as [Text field](TextField.md), without the adornments |

Old `minLines` / `maxLines` became `rows` / `maxRows`; the old 384 dp cap is gone (the web has none).

## Look

The text field's box, states and corners (input.css `.qx-textarea`), with block padding
`(size.control.lg − 1 line) / 2` — 10 dp at the default density — so a one-line textarea is a text field's height,
and each row adds a `body.md` line. The line is measured at the 1× font scale for the padding; the text itself
follows the font scale.

## Accessibility

As [Text field](TextField.md). Enter starts a new line; the keyboard action is the caller's.

## Platform differences

- **Growing.** The web grows through CSS `field-sizing: content`; Compose's multi-line limits do the same.
- **Resizing.** There is no drag handle to make it taller on Android.

Related: [Text field](TextField.md), [Field](Field.md).
