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

## A card is not a click target

The specs say so (06-component-specs §9, QTRX-182), and the web's Card takes no `onClick`:

- **A card that is chosen** — a plan, a sign-in method — is a choice tile: [`QeetrixRadioGroup`](RadioGroup.md) or
  [`QeetrixCheckboxGroup`](Checkbox.md) with `variant = QeetrixChoiceVariant.Tile`, which keeps the group's name, state
  and arrow keys.
- **A card that opens a page** keeps that action in its content: a button in `actions` or `footer`.
- **A list of places to go** is a list of [`QeetrixListItem`](ListItem.md)s, on a card if they need a surface.

Android had a clickable `QeetrixCard(onClick, selected, enabled, …)` in the tile look until 1 November 2026; it is
removed (nothing had been published).

## Accessibility

- The title is a heading, so TalkBack's heading navigation reaches the card. The web takes a heading `level`;
  Android has no heading levels.
- A card is not a control: nothing on it takes a tap but the controls its content puts there.

## Theme and tokens

`color.surface.raised` / `sunken`, `color.border.default` / `danger`, `color.text.danger`, `radius.surface`,
`space.inset.lg`, `space.gap.xs` / `sm` / `md`, `typography.title.sm` / `body.sm` / `body.md`.

## Platform differences

- **Touch targets.** Header actions may reach 12 dp into the header's padding (the web's `margin-block: -4px`
  plus the 8 dp of a 48 dp touch area round a 32 dp control), and the footer pads 8 dp less above and below its
  48 dp touch areas, so buttons are drawn where the web draws them.
- **No `level`, no `render`** (`section` / `article`): Android has no heading levels or elements.
- **An empty body takes no room**, as card.css 0.4.0 draws it (`.qx-card-body:empty`, QTRX-174): no body without
  `content`, and none when the content emits nothing.
- **Not implemented:** the Table and TreeView bleed rules (no Android Table yet), forced colours.
- **Removed since 2.x:** `Outline` and `Elevated` variants, `size` (`Sm`), `action` (now `actions`), shadows and
  nested-card shadow rules, `selected` on a static card, and the clickable card (`onClick`, `selected`, `enabled`;
  QTRX-182).

Related: [Description list](DescriptionList.md), [Button](Button.md).
