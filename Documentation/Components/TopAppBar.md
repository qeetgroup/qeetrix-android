# Top app bar

`QeetrixTopAppBar`, `QeetrixBackButton` · `components.navigation` · `@qeetrix/react`:
[PageHeader](https://ui.qeet.in/components/page-header) and PageHeaderBack (experimental), as a phone's app
bar · sample: Navigation → Top app bar

The bar at the top of a screen: its title, an optional description, an optional back button, and actions.

```kotlin
QeetrixTopAppBar(
    title = "Invoice INV-2041",
    description = "Draft",
    navigationIcon = { QeetrixBackButton(onClick = navigateUp) },
    actions = { QeetrixIconButton(QeetrixIconName.Share2, "Share", onClick = ::share, tone = QeetrixButtonTone.Neutral) },
)
```

## Look

- PageHeader's band: `color.surface.default`, ending on a 1 dp `color.border.default` rule; the phone's 16 dp gutter
  (`space.inset.lg`), less beside a 48 dp button.
- At least `size.control.lg` + 16 dp tall (56 dp at the default density, the chrome bar's height), growing with the
  text.
- Title in `title.lg` (PageHeader's), description in `body.md`, `color.text.subtle`; each on one line with an
  ellipsis.
- Actions at the end: neutral [icon buttons](IconButton.md), a menu for overflow; at most one solid and one soft
  button (design rules §7).
- `QeetrixBackButton`: a neutral ghost icon button with the sharp `arrow-left`, pointing the reading way (mirrored in
  right-to-left layouts), named "Back" (`qx_back`); pass `contentDescription = "Back to Users"` to name the parent.

## Accessibility

- The title and description are one heading for TalkBack.
- The bar keeps no navigation state and adds no back button by itself; system Back works either way.
- Edge to edge: `windowInsets` keeps the content clear of the status bar and cutouts.

## Theme and tokens

`color.surface.default`, `color.border.default`, `size.control.lg`, `typography.title.lg` / `body.md`,
`color.text.default` / `subtle`, `space.inset.xs` / `lg`, `space.gap.xs` / `sm`.

## Platform differences

- **A bar, not a band.** PageHeader is a page's title band (back link above the title, breadcrumb, count,
  status, summary, tabs, 20 px padding); on a phone the back control sits at the start of the bar as an icon
  button, as Android's up button does. Count, status, breadcrumb, summary and tabs are not offered.
- **The console TopBar** (product, scope switchers, search, account, on black with the Ember line) has no Android
  counterpart.
- **Removed since 2.x:** `subtitle` (now `description`), `scrolled` (the band is always ruled), the canvas surface
  and the `heading` role's 2.x size.

Related: [Navigation bar](NavigationBar.md), [Tabs](Tabs.md).

## Count, status, tabs and large titles (QTRX-196, QTRX-137)

`count` sits beside the title in its size at `body.md`'s weight, `text.subtlest`, formatted for the locale; `status` after it (a badge); `tabs` under the bar, edge to edge. `size = Medium` or `Large` adds a large title (`heading.sm` / `heading.md`) under the bar that collapses into it as content scrolls, with `rememberQeetrixTopAppBarScrollState()` and `Modifier.nestedScroll(state.nestedScrollConnection)`. Breadcrumbs stay off phones. The back button mirrors its own arrow.
