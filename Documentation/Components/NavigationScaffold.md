# Adaptive navigation

`QeetrixNavigationScaffold`, `QeetrixNavigationLayout` · `components.navigation` · `@qeetrix/react`: the
[AppShell](https://ui.qeet.in/components/app-shell) frame (navigation column and main region), as Android
adapts it · sample: Navigation → Adaptive navigation

A screen with its top-level destinations: a [bar](NavigationBar.md) on a compact window and a
[rail](NavigationRail.md) from `breakpoint.medium` up, switching as the window is resized, folded or rotated.

```kotlin
QeetrixNavigationScaffold(items, value = destination, onValueChange = { destination = it }) {
    when (destination) {
        Destination.Home -> HomeScreen()
        Destination.Inbox -> InboxScreen()
        Destination.Settings -> SettingsScreen()
    }
}
```

## Usage

- `layout` follows the window's width by default: `Bar` below `QeetrixBreakpoint.Medium` (600 dp), `Rail` from it
  (`QeetrixNavigationLayout.forWindowWidth`) — Android's compact and medium width classes and the web's
  `--qx-viewport-medium`. It is the window that counts, not the device.
- The content keeps its place in the composition, so its state survives a change of layout; it is told the bar's
  or rail's insets are consumed.
- `railHeader` is the rail's header. A screen's own bar is a [top app bar](TopAppBar.md) inside the content.

## Platform differences

The web AppShell is a console frame — a TopBar, a 256 px Sidebar that collapses to a rail, side panels, and a
drawer below 840 px. Android keeps its platform's adaptive pattern: a bottom bar on phones, a rail from 600 dp. It
is a small adapter over the bar and the rail rather than Material's `NavigationSuiteScaffold`
(Documentation/Decisions/0015-navigation-overlays-and-feedback.md). `selected` / `onSelect` are now `value` /
`onValueChange`.

Related: [Navigation bar](NavigationBar.md), [Navigation rail](NavigationRail.md).
