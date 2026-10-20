# Tabs

`QeetrixTabs`, `QeetrixTab`, `QeetrixTabsVariant` · `components.tabs` · web: [Tabs](https://ui.qeet.in/docs/components/tabs) ·
sample: Navigation → Tabs

A row of tabs that switches between views of related content in one place.

```kotlin
var section by rememberSaveable { mutableStateOf(Section.Overview) }
QeetrixTabs(
    tabs = listOf(QeetrixTab(Section.Overview, "Overview"), QeetrixTab(Section.Activity, "Activity", QeetrixIconName.Bell)),
    selected = section,
    onSelect = { section = it },
)
when (section) {
    Section.Overview -> OverviewPanel()
    Section.Activity -> ActivityPanel()
}
```

## Usage

- **Two treatments.** `Default`: a sunken well with the selected tab raised in it — a view switch for a
  card, a toolbar or a panel. `Line`: an underline track with a 2 dp Qeet indicator — section navigation.
- **The app draws the panel** for the selected value; there is no pager or page framework.
- **Overflow.** Tabs that do not fit scroll sideways and the newly selected tab is kept in view. Labels
  never truncate.
- **Not app navigation.** Top-level destinations belong in the [navigation bar](NavigationBar.md).

## States

Selected, unselected (muted), hovered or pressed (brightened; a neutral indicator for `Line`), disabled.

## Accessibility

One selectable group; each tab is a tab, read with its label and whether it is selected; a disabled tab is
read as unavailable. The drawn row keeps the web's height inside a 48 dp touch area, and grows with the
text size.

## Theme and tokens

`surface.sunken` well and `surface.elevated` + `elevation.raised` selected tab (default); `border.default`
track, `border.brand` indicator, `border.strong` hover preview (line); text-sm medium labels, 16 dp icons.

## Platform differences

The web row is one tab stop with arrow keys between tabs; on Android, as on iOS, each tab is its own focus
stop. Vertical tabs are not provided.

Related: [Navigation bar](NavigationBar.md).
