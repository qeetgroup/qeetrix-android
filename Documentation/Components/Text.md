# Text

`QeetrixText` · `components.typography` · web: [Typography](https://ui.qeet.in/docs/components/typography) ·
sample: Data display → Text

A piece of copy in a Qeetrix text style: the font, size, line height, colour and heading semantics of a
variant. It wraps Compose's `BasicText`, so wrapping, `maxLines`, `overflow`, alignment and
`AnnotatedString` spans all work as usual.

```kotlin
QeetrixText("Billing", variant = QeetrixTextVariant.H2)
QeetrixText("Invoices are sent on the first of each month.", variant = QeetrixTextVariant.Muted)
QeetrixText(buildAnnotatedString { append("Ends "); withStyle(SpanStyle(fontWeight = FontWeight.SemiBold)) { append("in 3 days") } })
```

## Variants

`H1`–`H4`, `P` (default), `Lead`, `Large`, `Small`, `Muted`, `Blockquote` (with its leading rule) and
`InlineCode` (Fira Code on a chip) — the web's names. Below a 640 dp window (every phone in portrait)
h1–h3 are 24, 20 and 18 sp; from 640 dp, 32, 24 and 20. h4 is 16 sp throughout.

The foundation roles (`QeetrixTheme.typography`) remain available for text that is not one of these
variants.

## Accessibility

- `H1`–`H4` are headings for TalkBack's heading navigation.
- Sizes are in sp and follow the font scale; text wraps rather than truncates unless you ask.
- Mixed scripts shape correctly: Qeet Sans for Latin, the system's fonts for the rest.

## Theme and tokens

Typography tokens and `component.typography.*` (inline code chip). Colour: the inherited content colour
(`textPrimary` on a Qeetrix surface), or the variant's own for `Lead`, `Muted` and `Blockquote`. `color`
accepts a theme role (`QeetrixTheme.colors.textDanger`), never a literal.

## Platform differences

- **Breakpoint.** The web switches heading sizes at its `sm` breakpoint (640 px); Android uses a 640 dp
  window width. iOS uses the horizontal size class instead.
- **Colour.** As the web, Android inherits the surrounding content colour; iOS sets the primary colour.
- **No `as` or `list`.** The heading level comes from the variant; a list is a layout of rows.

Related: [Badge](Badge.md), [Card](Card.md).
