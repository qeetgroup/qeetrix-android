# Navigation rail

`QeetrixNavigationRail` · `components.navigation` · web: [Sidebar](https://ui.qeet.in/docs/components/sidebar)
(the icon rail) · sample: Navigation → Navigation rail

An app's top-level destinations in a column along the inline start of a medium or expanded window.

```kotlin
QeetrixNavigationRail(items, selected = destination, onSelect = { destination = it }) {
    QeetrixIconButton(QeetrixIconName.Plus, "New message", onClick = ::composeMessage, variant = QeetrixButtonVariant.Default)
}
```

## Usage

- The same items and the same app-owned selection as the [bar](NavigationBar.md).
- The trailing lambda is the rail's `header`, above the destinations: a primary action or the product's mark.
- 80 dp wide, wider when a label needs it at a large text size; the destinations scroll when they do not
  fit the height. Labels never truncate.
- Do not choose the rail by device type: use the [adaptive scaffold](NavigationScaffold.md), which follows
  the window width.

## Accessibility

One selectable group of tabs in reading order, each at least 48 dp tall; keyboard and D-pad focus move
through them. The selected item carries the indicator as well as the tint.

## Theme and tokens

As the bar, with a `border.default` rule along the inline end and the indicator on each item's inline
start — the web sidebar's own placement.

## Platform differences

The web sidebar can expand to show labels beside icons; the Android rail keeps labels under icons and leaves
wider navigation to the app. iOS adapts its tab bar to a sidebar on iPad through the system.

Related: [Navigation bar](NavigationBar.md), [Adaptive navigation](NavigationScaffold.md).
