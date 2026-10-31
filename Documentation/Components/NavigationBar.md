# Navigation bar

`QeetrixNavigationBar`, `QeetrixNavigationItem` · `components.navigation` · `@qeetrix/react`: none — app chrome
drawn as the [AppShell](https://ui.qeet.in/components/app-shell)'s navigation column and the
[Sidebar](https://ui.qeet.in/components/sidebar)'s items (below 840 px the web opens its Sidebar as a drawer;
an Android phone keeps its destinations in a bottom bar) · sample: Navigation → Navigation bar

An app's three to five top-level destinations along the bottom of a compact window.

```kotlin
QeetrixNavigationBar(
    items = listOf(
        QeetrixNavigationItem(Destination.Home, "Home", QeetrixIconName.House),
        QeetrixNavigationItem(Destination.Inbox, "Inbox", QeetrixIconName.Inbox, badge = "3"),
        QeetrixNavigationItem(Destination.Settings, "Settings", QeetrixIconName.Settings),
    ),
    value = destination,
    onValueChange = { destination = it },
)
```

## Look

- The bar: `color.surface.canvas` with a 1 dp `color.border.default` seam on its top edge; items share the width.
- An item (`QeetrixNavigationItem`, the web's SidebarItem): a 20 dp **round** icon (design rules §16) over its
  label in `label.sm`, `space.gap.xs` apart, at least 48 dp tall.

| State | Look |
|:--|:--|
| Rest | Label `color.text.subtle`, icon `color.icon.subtle` |
| Hover / pressed | `color.overlay.hover` / `color.overlay.pressed`, label `color.text.default` |
| Current | `color.bg.neutral.subtle-hover`, label `color.text.default` at weight 600, icon `color.icon.brand` and filled (`filledWhenCurrent`), and the 3 dp Ember bar on the item's top edge — the edge facing the screen |
| Disabled | `color.text.disabled`, `color.icon.disabled`, no tint; can't be chosen |
| Focus | The 2 dp Ember frame inside the item |

A `badge` ("3", "99+", "New") is a small neutral [badge](Badge.md) over the icon's end corner.

## Accessibility

- The app owns navigation: `value` is the current destination and `onValueChange` reports each choice — also the
  current one again, so the app can return to its top. Qeetrix keeps no back stack; system Back is the app's.
- One selectable group; each item is a tab, read with its label, its badge and whether it is selected. At large
  text sizes a label that doesn't fit ends in an ellipsis (read in full) and the bar grows taller.
- Edge to edge: the surface runs under the system bar; `windowInsets` keeps the items clear of it.

## Theme and tokens

`color.surface.canvas`, `color.border.default` / `brand`, `border.width.accent`, `color.bg.neutral.subtle-hover`,
`color.overlay.hover` / `pressed`, `color.text.subtle` / `default` / `disabled`, `color.icon.subtle` / `brand` /
`disabled`, `typography.label.sm`, `size.icon.md`, `space.gap.xs`, `space.inset.xs` / `sm`.

## Platform differences

- There is no bottom bar on the web; the item rules are the Sidebar's, with the icon over the label and the
  Ember bar on the top edge (the Sidebar's start edge faces its page; a bottom bar's top edge does).
- Icons are 20 dp (`size.icon.md`, standalone) where the Sidebar's are 16 dp beside the label.
- **Removed since 2.x:** `selected` / `onSelect` (now `value` / `onValueChange`), the Ember tint and rounded half
  indicator on the current item, `surface.rail`.

Related: [Navigation rail](NavigationRail.md), [Adaptive navigation](NavigationScaffold.md), [Tabs](Tabs.md).
