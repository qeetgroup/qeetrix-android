# Card

`QeetrixCard`, `QeetrixCardVariant`, `QeetrixCardTone` · `components.card` ·
`@qeetrix/react`: [Card](https://ui.qeet.in/components/card) · sample: Layout → Card

Related content on a page: a ruled, flat surface with an optional header, a body and an optional footer.

```kotlin
QeetrixCard(
    title = "Workspace",
    description = "3 members",
    actions = { QeetrixButton("View all", onClick = ::openMembers, variant = QeetrixButtonVariant.Ghost) },
    footer = { QeetrixButton("Manage", onClick = ::manage) },
) {
    QeetrixText("Created 12 March.")
}
QeetrixCard(title = "Delete workspace", tone = QeetrixCardTone.Danger, footer = { … })
QeetrixCard(onClick = { plan = "team" }, selected = plan == "team", title = "Team", description = "Up to 50 members")
```

## Anatomy

| Part | Look |
|:--|:--|
| Card | `color.surface.raised` (`Subdued`: `color.surface.sunken`), 1 dp `color.border.default` (`Danger`: `color.border.danger`), 4 dp corners (`radius.surface`), **no shadow** (design rules §12). |
| Header | `title` in `title.sm` (`Danger`: `color.text.danger`), `description` in `body.sm`, `color.text.subtle`, `space.gap.xs` apart; `actions` at the end, `space.gap.md` from them, centred on the title's first line; 16 dp padding, over a 1 dp rule only when a body follows (a header that ends the card or meets the footer has none). No header without any of the three. |
| Body | The `content`: 16 dp padding, `body.md`, `color.text.default`. A [description list](DescriptionList.md) in rows placed directly in it runs edge to edge, its rows keeping the card's padding, and meets the header's rule and the card's edge. |
| Footer | `footer` actions, end-aligned and wrapping, `space.gap.sm` apart, under a 1 dp rule; a `md` button is drawn 16 dp from the rule and the edge, as on the web. |

| Parameter | Values |
|:--|:--|
| `variant` | `Default` (raised) · `Subdued` (sunken, for a secondary panel) |
| `tone` | `Neutral` · `Danger` (a "danger zone" of settings that delete or can't be undone; the rules inside stay neutral) |

## The choice card

`QeetrixCard(onClick = …)` makes the whole card one button: a plan to choose, a project to open. `@qeetrix/react`
has no clickable Card (its spec names a separate SelectionCard, not built yet), so this one takes the choice
tile's look (RadioGroup and CheckboxGroup `variant="tile"`): `color.bg.neutral.subtle` under the pointer,
`color.bg.neutral.subtle-hover` while pressed, and with `selected` `color.bg.selected` with a 2 dp
`color.border.brand` edge drawn inside it, so nothing moves. Keyboard focus draws the 2 dp Ember frame round it.
`enabled = false` takes `color.text.disabled` for the title and `color.text.subtlest` for the description.

## Accessibility

- The title is a heading, so TalkBack's heading navigation reaches the card. The web takes a heading `level`;
  Android has no heading levels.
- A choice card is one button named by its text. `selected` only draws: state the choice with
  `Modifier.semantics { selected = … }` and put the cards in a `selectableGroup()`.

## Theme and tokens

`color.surface.raised` / `sunken`, `color.border.default` / `danger`, `color.text.danger`, `radius.surface`,
`space.inset.lg`, `space.gap.xs` / `sm` / `md`, `typography.title.sm` / `body.sm` / `body.md`; choice card
`color.bg.neutral.subtle(-hover)`, `color.bg.selected(-hover)`, `color.border.brand`, `border.width.strong`.

## Platform differences

- **Touch targets.** Header actions may reach 12 dp into the header's padding (the web's `margin-block: -4px`
  plus the 8 dp of a 48 dp touch area round a 32 dp control), and the footer pads 8 dp less above and below its
  48 dp touch areas, so buttons are drawn where the web draws them.
- **No `level`, no `render`** (`section` / `article`): Android has no heading levels or elements.
- **An empty body takes no room**, as card.css 0.4.0 draws it (`.qx-card-body:empty`, QTRX-174): no body without
  `content`, and none when the content emits nothing.
- **Not implemented:** the Table and TreeView bleed rules (no Android Table yet), forced colours.
- **Removed since 2.x:** `Outline` and `Elevated` variants, `size` (`Sm`), `action` (now `actions`), shadows and
  nested-card shadow rules, `selected` on a static card.

Related: [Description list](DescriptionList.md), [Button](Button.md).
