# Navigation rail

`QeetrixNavigationRail` · `components.navigation` · `@qeetrix/react`: the
[AppShell](https://ui.qeet.in/components/app-shell)'s navigation column with its
[Sidebar](https://ui.qeet.in/components/sidebar), as a rail · sample: Navigation → Navigation rail

An app's top-level destinations in a column along the start of a medium or expanded window.

```kotlin
QeetrixNavigationRail(items, value = destination, onValueChange = { destination = it }) {
    QeetrixIconButton(QeetrixIconName.Plus, "New message", onClick = ::compose, variant = QeetrixButtonVariant.Solid)
}
```

## Look

- `color.surface.canvas` with a 1 dp `color.border.default` seam on its end edge; `space.gap.sm` above the
  destinations and `space.inset.md` below; an optional `header` (a primary action, the product's mark) above them.
- Rows run edge to edge with no gap, each a [navigation item](NavigationBar.md): a 20 dp round icon over its label.
  The current row is `color.bg.neutral.subtle-hover` with its label at weight 600, its icon Ember and filled, and
  the 3 dp Ember bar on its **start** edge (design rules §11); hover `color.overlay.hover`; disabled colours.
- 80 dp wide, wider when a label needs it at a large text size; the destinations scroll when they don't fit.

## Accessibility

One selectable group of tabs, each at least 48 dp tall, in reading order; keyboard and D-pad focus move through
them. The app owns `value`; `windowInsets` keeps the items clear of the status bar, a cutout and a start-side
navigation bar.

## Theme and tokens

As for the [navigation bar](NavigationBar.md), plus `space.gap.sm` and `space.inset.md`.

## Platform differences

- **Labels stay visible.** The web's collapsed rail is 48 px of icons with the labels as tooltips; touch has no
  hover to show a tooltip, so Android's rail keeps each label under its icon, at Android's 80 dp rail width.
- No expand-on-hover, no collapse control, no groups or categories: the web Sidebar's console features.

Related: [Navigation bar](NavigationBar.md), [Adaptive navigation](NavigationScaffold.md).
