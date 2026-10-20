# Top app bar

`QeetrixTopAppBar`, `QeetrixBackButton` · `components.navigation` · web: none (the web's
[PageHeader](https://ui.qeet.in/docs/components/page-header) is the in-page title block) · sample: Navigation → Top app bar

The bar at the top of a screen: its title, an optional subtitle, an optional back button, and actions.

```kotlin
QeetrixTopAppBar(
    title = "Invoice INV-2041",
    subtitle = "Draft",
    navigationIcon = { QeetrixBackButton(onClick = navigateUp) },
    actions = { QeetrixIconButton(QeetrixIconName.Share2, "Share", onClick = ::share) },
    scrolled = listState.canScrollBackward,
)
```

## Usage

- **Presentation only.** It draws what it is given, adds no back button by itself and keeps no state.
  System Back is the app's navigation's whether or not the bar shows a back button.
- **`QeetrixBackButton`**: the arrow pointing to where reading started (it turns round in right-to-left
  layouts), named "Back" (`qx_back`); its action is the app's "up".
- **Actions** are icon buttons and, for overflow, a [menu](Menu.md) on an `ellipsis-vertical` button.
- **`scrolled`**: pass whether content is scrolled under the bar; a rule appears along its bottom edge.

## Accessibility

The title and subtitle are one heading. Icon buttons need their content descriptions; the back button has
one. The bar is at least 56 dp tall and grows with the text size; the title and subtitle keep to one line,
end in an ellipsis and are read in full.

## Theme and tokens

`surface.canvas`, so bar and page read as one; `border.default` for the scrolled rule; the `heading` type
role for the title, text-sm `text.tertiary` for the subtitle.

## Platform differences

iOS styles the system navigation bar (`qeetrixNavigationBar(background:)`) and keeps the system's back
button and large titles; Android has no system app bar in Compose, so Qeetrix draws one. No collapsing
large-title behaviour.

Related: [Navigation bar](NavigationBar.md), [Menu](Menu.md).
