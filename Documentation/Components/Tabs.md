# Tabs

`QeetrixTabs`, `QeetrixTab`, `QeetrixTabsVariant`, `QeetrixTabsOrientation` · `components.tabs` ·
`@qeetrix/react`: [Tabs](https://ui.qeet.in/components/tabs) (Tabs, TabsList, Tab) · sample: Navigation → Tabs

Views of the same thing, one at a time: a user's profile, security and sessions.

```kotlin
var section by rememberSaveable { mutableStateOf(Section.Profile) }
QeetrixTabs(
    tabs = listOf(
        QeetrixTab(Section.Profile, "Profile", QeetrixIconName.UserRound),
        QeetrixTab(Section.Sessions, "Sessions", badge = 3),
    ),
    value = section,
    onValueChange = { section = it },
)
when (section) {
    Section.Profile -> ProfilePanel()
    Section.Sessions -> SessionsPanel()
}
```

## Looks

| `variant` | Look |
|:--|:--|
| `Line` (default) | Tabs over a 2 dp `color.border.subtle` rule that runs under the whole list; the selected tab has the 3 dp Ember bar (`border.width.accent`, `color.border.brand`) on its bottom edge. For the views of a screen. |
| `Contained` | A segmented strip as wide as its tabs: a 1 dp `color.border.default` edge round it and between segments, 4 dp outer corners, `color.bg.neutral.subtle`; hover `-hover`; the selected segment on `color.surface.default` with the Ember bar. For a few views of one card or panel. |

`orientation = Vertical` stacks the tabs; the rule (line) and the bar move to the start edge, as for the current
navigation item, and contained segments are ruled across.

**Tabs:** `size.control.lg` tall, the density's inset (`clamp(12px, size.control.lg − 20px, 16px)`: 16 dp, 12 dp
compact), `space.gap.sm` between icon, label and count; `label.md` in `color.text.subtle`, `color.text.default`
under the pointer; selected `color.text.default` at weight 600 with its icon filled (`filledWhenSelected`); disabled
`color.text.disabled`. The bar slides to the selected tab in `motion.duration.normal` with `motion.easing.standard`
— at once under reduced motion. `badge` is a count on the label's baseline, formatted for the locale and shown as
a [badge](Badge.md) (neutral, or `badgeTone`); it takes no more height than the label.

## Accessibility

- One selectable group; each tab is a tab, read with its label, its count and whether it is selected; a disabled
  tab is read as unavailable. The app draws the panel for `value`.
- Keyboard focus draws the 2 dp Ember frame **inside** the tab: the list scrolls, and a scrolling box clips what
  is drawn outside it (as the web's CSS does).
- The drawn list keeps the web's height inside a 48 dp touch area, and grows with the text size.
- Horizontal tabs that don't fit scroll sideways, and a newly selected tab is kept in view. Labels never truncate.

## Theme and tokens

`size.control.lg`, `space.gap.sm`, `typography.label.md` / `label.sm`, `color.text.subtle` / `default` /
`disabled`, `color.border.subtle` / `default` / `brand`, `border.width.strong` / `accent`, `color.bg.neutral.subtle(-hover)`,
`color.surface.default`, `radius.control`, `motion.duration.fast` / `normal`, `motion.easing.standard`.

## Platform differences

- **Focus.** The web list is one tab stop with the arrow keys between tabs and `activateOnFocus`; on Android, as on
  iOS, each tab is its own focus stop and Enter or the D-pad centre selects it, so `activateOnFocus` is not offered.
- **No TabsPanel or TabNav.** The app shows the panel (a `when`, `AnimatedContent`); sub-pages with their own routes
  are the app's navigation.
- `badge` is a number with a `badgeTone`, where the web also takes any Badge.
- **Removed since 2.x:** `Default` (the sunken well, now `Contained`), `selected` / `onSelect` (now `value` /
  `onValueChange`), the 2 dp rounded indicator and its hover preview.

Related: [Navigation bar](NavigationBar.md), [Top app bar](TopAppBar.md).

## Focus and panels (QTRX-194)

`activateOnFocus` (default `true`, as the web's TabsList): keyboard or D-pad focus selects the tab it reaches; touch and TalkBack's focus never do. `QeetrixTabPanel(tab, value)` shows a tab's content while it is selected, named by the tab for accessibility services.
