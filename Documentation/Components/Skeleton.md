# Skeleton

`QeetrixSkeleton`, `QeetrixSkeletonShape`, `QeetrixSkeletonText` · `components.spinner` ·
web: [Skeleton](https://ui.qeet.in/docs/components/skeleton) · sample: Feedback → Skeleton

A placeholder for content that is loading. Size it with its modifier and compose several in the shape of
what will arrive.

```kotlin
Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
    QeetrixSkeleton(Modifier.size(40.dp), QeetrixSkeletonShape.Circle)
    QeetrixSkeletonText(Modifier.weight(1f), lines = 2)
}
```

## Usage

- **Shapes:** `Rectangle` (the chip corner, the default) for blocks and lines, `Circle` for avatars,
  `Capsule` for badges and chips.
- **`QeetrixSkeletonText`** draws lines the height of a typography role's text, spaced by its line height,
  the last one shorter.
- Prefer skeletons in the content's shape to a [spinner](Spinner.md) when the layout is known; the
  [data state](DataState.md) shows rows by default.

## Accessibility

Skeletons are invisible to accessibility services. Say that the region is loading where it is — the data
state reads its loading branch as one "Loading" element.

## Theme and tokens

`component.skeleton.background` (`text.primary` at 10 %), so it keeps its weight on every surface in both
themes; a highlight of `text.primary` at 8 % sweeps across every 1.6 s (ease-standard), and stands still
with animations off. The sweep only redraws: nothing recomposes.

## Platform differences

The web sets the shape with a class; Android and iOS name it.

Related: [Data state](DataState.md), [Spinner](Spinner.md).
