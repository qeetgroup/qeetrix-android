# Navigation bar

`QeetrixNavigationBar`, `QeetrixNavigationItem` · `components.navigation` · web: none (app chrome; the web's
[Sidebar](https://ui.qeet.in/docs/components/sidebar) is its counterpart on wide screens) · sample: Navigation → Navigation bar

An app's three to five top-level destinations along the bottom of a compact window.

```kotlin
var destination by rememberSaveable { mutableStateOf(Destination.Home) }
QeetrixNavigationBar(
    items = listOf(
        QeetrixNavigationItem(Destination.Home, "Home", QeetrixIconName.House),
        QeetrixNavigationItem(Destination.Inbox, "Inbox", QeetrixIconName.Inbox, badge = "3"),
        QeetrixNavigationItem(Destination.Settings, "Settings", QeetrixIconName.Settings),
    ),
    selected = destination,
    onSelect = { destination = it },
)
```

## Usage

- **The app owns navigation.** Items carry the app's own values (an enum, a route); `selected` is the app's
  current destination and `onSelect` reports each choice — also the current destination chosen again, so
  the app can return to its top. Qeetrix keeps no back stack and needs no navigation library.
- **Not tabs.** Destinations are the app's top level, each with its own history. Views of one screen's
  content are [tabs](Tabs.md).
- On a medium or expanded window use the [rail](NavigationRail.md), or the
  [adaptive scaffold](NavigationScaffold.md) to switch between them.
- `badge` is short text shown over the icon ("3", "99+", "New") and read with the label.

## States

Selected (tint, brand icon, medium label, the brand indicator on the top edge), unselected, hovered or
pressed (the neutral fill), disabled.

## Accessibility

One selectable group; each item is a tab, read with its label, badge and whether it is selected, at least
48 dp in each direction. Selection is shown by the indicator and the weight as well as the tint. At large
text sizes a label that does not fit ends in an ellipsis and is still read in full; the bar grows taller.

## Theme and tokens

`surface.rail` with a `border.default` rule, `surface.brand-subtle` for the selection,
`surface.interactive` under the pointer, `text.brand` for the selected icon, `border.brand` for the
indicator (`component.sidebar.indicator-width`, 3 dp), `corner.chip`, text-xs labels, 24 dp icons.

## Platform differences

- The web has no bottom bar; its Sidebar's selection signature is drawn here along the top edge.
- iOS styles the system tab bar (`qeetrixTabBar()`); Android draws Qeetrix's own, edge to edge over the
  system navigation bar (`windowInsets`).

Related: [Navigation rail](NavigationRail.md), [Adaptive navigation](NavigationScaffold.md), [Top app bar](TopAppBar.md).
