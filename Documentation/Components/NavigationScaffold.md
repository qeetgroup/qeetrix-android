# Adaptive navigation

`QeetrixNavigationScaffold`, `QeetrixNavigationLayout` · `components.navigation` · web: none · sample:
Navigation → Adaptive navigation; Overview → Workspace

A screen with its top-level destinations: the [bar](NavigationBar.md) on a compact window, the
[rail](NavigationRail.md) from the medium width up, switching as the window is resized, folded or rotated.

```kotlin
QeetrixNavigationScaffold(items, selected = destination, onSelect = { destination = it }) {
    when (destination) {
        Destination.Home -> HomeScreen()
        Destination.Inbox -> InboxScreen()
        Destination.Settings -> SettingsScreen()
    }
}
```

## Usage

- **The rule.** Below 600 dp of window width, a bar; from 600 dp, a rail — Android's `WindowSizeClass`
  compact boundary (`QeetrixNavigationLayout.forWindowWidth`). It is the window that counts, not the
  device: a phone in landscape, a tablet in split screen and a resized window follow the same rule.
- **`layout`** fixes the presentation when the app decides itself (with its own `WindowSizeClass`, say).
- **The content keeps its state** across a switch: it stays in one place in the composition.
- **Only presentation.** The content is the app's current destination; back stacks, deep links and saved
  state stay with the app's navigation library.
- It is a whole screen: give it a bounded size (`Modifier.fillMaxSize()`), not a scrolling container.

## Accessibility

As the bar and the rail. The bar or rail pads itself for the system bars and tells the content those
insets are consumed, so content padded for `WindowInsets.safeDrawing` does not pad twice.

## Platform differences

Material offers `NavigationSuiteScaffold`, which draws Material's components; Qeetrix's scaffold is a small
adapter over its own bar and rail ([ADR 0015](../Decisions/0015-navigation-overlays-and-feedback.md)). No
navigation drawer yet: the bar and rail cover the destinations Qeet apps have.

Related: [Navigation bar](NavigationBar.md), [Navigation rail](NavigationRail.md).
