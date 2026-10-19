# Separator

`QeetrixSeparator` · `components.separator` · web: [Separator](https://ui.qeet.in/docs/components/separator) ·
sample: Layout → Separator

A 1 dp rule between groups of content.

```kotlin
QeetrixSeparator()
Row(Modifier.height(IntrinsicSize.Min)) {
    QeetrixText("Docs")
    QeetrixSeparator(orientation = QeetrixSeparatorOrientation.Vertical)
    QeetrixText("Status")
}
```

## Usage

- `Horizontal` fills the width; `Vertical` takes its parent's height, so give the row one
  (`Modifier.height(IntrinsicSize.Min)`).
- Variants: `Default` (the border role, between sections) and `Muted` (the subtle border, between items
  inside a section).

## Accessibility

Decorative, and deliberately below 3:1 contrast: it supports grouping that spacing and headings already
express. TalkBack skips it.

## Theme and tokens

Semantic tokens only: `border.default` and `border.subtle`.

## Platform differences

The web gives the rule `role="separator"`. Android has no separator role (TalkBack would read an empty
element), so it is hidden, as iOS hides it from VoiceOver.

Related: [Card](Card.md).
