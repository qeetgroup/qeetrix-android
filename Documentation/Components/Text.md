# Text and Heading

`QeetrixText`, `QeetrixTextSize`, `QeetrixTextWeight`, `QeetrixTextTone`, `QeetrixHeading`, `QeetrixHeadingSize`,
`QeetrixHeadingTone` · `components.typography` · `@qeetrix/react`:
[Text](https://ui.qeet.in/components/text) and [Heading](https://ui.qeet.in/components/heading) · sample: Data
display → Text

`QeetrixText` is running text, labels and inline code on the body type roles. `QeetrixHeading` is a section
heading: its `level` says where it sits in the screen's outline, its `size` how it looks.

```kotlin
QeetrixHeading("Billing", level = 2)
QeetrixHeading("Welcome to Qeet", level = 1, size = QeetrixHeadingSize.DisplaySm)
QeetrixText("Invoices are sent on the first of each month.", tone = QeetrixTextTone.Subtle)
QeetrixText("inv_2K9", mono = true, size = QeetrixTextSize.Sm)
QeetrixText(buildAnnotatedString { append("Ends "); withStyle(SpanStyle(fontWeight = FontWeight.SemiBold)) { append("in 3 days") } })
```

Both wrap Compose's `BasicText`, so wrapping, `maxLines`, alignment and `AnnotatedString` spans work as usual.

## Text

| `@qeetrix/react` | Android |
|:--|:--|
| `children` | `text: String`, or an `AnnotatedString` (emphasis, links, spans in another colour) |
| `size`: `sm` 12/16 · `md` 14/20 (default) · `lg` 16/24 | `size: QeetrixTextSize` — `Sm`, `Md`, `Lg`: `body.sm`, `body.md`, `body.lg` |
| `weight`: `regular` 400 (default) · `medium` 500 · `semibold` 600 | `weight: QeetrixTextWeight` — `Regular`, `Medium`, `Semibold`: the weights of `body.md`, `label.md` and `title.md` |
| `tone` (unset inherits) | `tone: QeetrixTextTone?` — null inherits the content colour |
| `mono` | `mono` — Fira Code at the same size |
| `truncate`: `true` or a number of lines | `maxLines` — the last line then ends in an ellipsis |
| `align`: `start` · `center` · `end` | `textAlign: TextAlign` |
| `render` (`<p />`) | — |
| — | `minLines`, `onTextLayout` |

Tones are `color.text.<tone>`: `Default`, `Subtle`, `Subtlest`, `Disabled`, `Inverse`, `Brand`, `Danger`,
`Success`, `Warning`, `Info`. `Disabled` is only for text inside an inactive control: it is exempt from contrast
there and unreadable anywhere else (2.48:1 in light). Quieter text that must still be read is `Subtle`.

## Heading

| `@qeetrix/react` | Android |
|:--|:--|
| `children` | `text: String` |
| `level`: 1–6 (required) | `level: Int` (required, 1–6; anything else throws) |
| `size` (by default from the level) | `size: QeetrixHeadingSize`, by default from the level |
| `tone`: `default` · `subtle` · `inverse` | `tone: QeetrixHeadingTone` — `Default`, `Subtle`, `Inverse` |
| `truncate`, `align` | `maxLines`, `textAlign` |
| `render` | — |
| — | `onTextLayout` |

| `size` | Role | Default for |
|:--|:--|:--|
| `DisplayLg`, `DisplayMd`, `DisplaySm` | `display.lg` 56/64, `display.md` 48/56, `display.sm` 40/48 | — (welcoming screens only, design rules §17) |
| `HeadingLg` | `heading.lg` 32/40 | level 1 |
| `HeadingMd` | `heading.md` 28/36 | level 2 |
| `HeadingSm` | `heading.sm` 24/32 | level 3; a screen title |
| `TitleLg` | `title.lg` 20/28 | level 4 |
| `TitleMd` | `title.md` 18/24 | level 5; a dialog title |
| `TitleSm` | `title.sm` 16/24 | level 6; a card title |

Every heading size is weight 600. Display sizes use Qeet Sans's Geo letter style (`font.geometry.display`, 100),
the others Base (`font.geometry.heading`, 50); text uses A11y (`font.geometry.body`, 0), and Fira Code has no
letter style (design rules §5).

## Look (text.css, heading.css)

- No margins: spacing is the layout's.
- Without `maxLines` text wraps; with it, the last line ends in an ellipsis.
- Text without a `tone` takes the inherited content colour (`LocalQeetrixContentColor`): `color.text.default` on a
  Qeetrix surface, `color.text.on-brand` inside a solid button. A heading is `color.text.default` unless its tone
  says otherwise.
- Qeetrix has no uppercase text and no letter-spacing changes, except a group label
  (`QeetrixTheme.typography.labelCaps`, used by the section header and the timeline's groups).

## Accessibility

- `QeetrixHeading` is a heading for TalkBack's heading navigation; `QeetrixText` is not.
- Sizes are in sp and follow the user's font size, including Android 14's non-linear scaling. Text wraps rather
  than truncating unless `maxLines` says so, and a truncated text is still read in full.
- Glyphs the Qeet fonts lack — Devanagari, Tamil and other Indic scripts — fall back to the system's Noto fonts,
  character by character.

## Theme and tokens

`typography.body.sm` / `md` / `lg`, the weights of `typography.label.md` and `typography.title.md`,
`typography.display.*`, `typography.heading.*`, `typography.title.*`, `font.family.mono`, `font.geometry.*`,
`color.text.*`.

## Platform differences

- **Heading levels.** Android has one heading role and no levels, so `level` only chooses the default look; the
  web renders `<h1>`…`<h6>`. Keep levels in order anyway, so the look follows the outline.
- **No elements.** There is no `render`: a paragraph is a `QeetrixText` in a layout. The web's Link is not ported;
  a link in running text is an `AnnotatedString` with a `LinkAnnotation`.
- **Truncation** is Compose's `maxLines`; `minLines` and `onTextLayout` are Compose's too.
- **Removed since 2.x:** `QeetrixTextVariant` (`H1`–`H4`, `P`, `Lead`, `Large`, `Small`, `Muted`, `Blockquote`,
  `InlineCode`) — headings are `QeetrixHeading`, the rest is `size`, `weight`, `tone` and `mono`; the `color`,
  `overflow` and `softWrap` parameters; the heading sizes that changed at a 640 dp window width.

Related: [Badge](Badge.md), [Card](Card.md), [Section header](SectionHeader.md).

## Right to left (QTRX-199)

A paragraph takes its direction from its own text and its alignment from the layout: in a right-to-left screen an English sentence keeps its full stop at its end and still lines up with its neighbours (finding Q-13, gone).
