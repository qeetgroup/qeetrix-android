# Skeleton

`QeetrixSkeleton`, `QeetrixSkeletonText`, `QeetrixSkeletonVariant`, `QeetrixSkeletonSize` · `components.spinner` ·
`@qeetrix/react`: [Skeleton](https://ui.qeet.in/components/skeleton) · sample: Feedback → Skeleton

A placeholder shaped like the content that is loading.

```kotlin
Row(horizontalArrangement = Arrangement.spacedBy(QeetrixSpace.GapMd)) {
    QeetrixSkeleton(variant = QeetrixSkeletonVariant.Circle, size = QeetrixSkeletonSize.Lg)
    QeetrixSkeletonText(lines = 2, modifier = Modifier.weight(1f))
}
```

## Usage

- `variant`: `Text` (default; a line of `body.md`, full width), `Block` (64 dp tall, full width) or `Circle` (an
  avatar, `size` `Sm` 24 · `Md` 32 · `Lg` 40 · `Xl` 48 dp). Size it with `modifier` to match what will arrive.
- `QeetrixSkeletonText(lines, role)`: a paragraph of bars, each the role's font size tall and centred in its line,
  the last one 60 % wide; it takes the lines' height, so the text that replaces it moves nothing.

## Accessibility

Hidden from accessibility services. Announce the wait where it is — a [Data state](DataState.md)'s labelled
spinner, or text.

## Theme and tokens

skeleton.css: `color.bg.neutral.subtle-pressed`; `radius.sm` for text, `radius.surface` for a block,
`radius.full` for a circle; the opacity pulses to 45 % and back every `4 × motion.duration.slower` (1.6 s) on
`motion.easing.standard`, and stands still under reduced motion.

## Platform differences

- `lines` is a separate composable, `QeetrixSkeletonText`, with the role whose line it imitates (the web uses the
  inherited font).
- The earlier Android shimmer sweep and pill shape are gone, as on the web.

Related: [Data state](DataState.md), [Spinner](Spinner.md).
