# Card

`QeetrixCard` · `components.card` · web: [Card](https://ui.qeet.in/docs/components/card) ·
sample: Layout → Card

A surface that groups related content: an optional header (title, description and an action), the
content, and an optional footer band.

```kotlin
QeetrixCard(
    title = "Workspace",
    description = "3 members",
    action = { QeetrixBadge("Pro", variant = QeetrixBadgeVariant.Brand) },
    footer = { QeetrixButton("Manage", onClick = ::manage, variant = QeetrixButtonVariant.Outline) },
) {
    QeetrixText("Created 12 March")
}

QeetrixCard(onClick = ::openPlan, title = "Team", description = "Up to 50 members")   // the card is the control
```

## Usage

- **Static** (no `onClick`): a group of content; buttons inside it are separate controls.
- **Clickable** (`onClick`): the whole card is one button, for a card that does one thing (open a
  record, choose a plan). A card with several actions is a static card with buttons inside.
- Variants: `Default` (boundary and faint shadow), `Outline` (no shadow, for dense layouts),
  `Elevated` (one step forward — for the one surface that should stand out). Sizes: `Default` (16 dp
  padding and gaps), `Sm` (12 dp, 14 sp title).
- Every slot is optional; an absent slot takes no space.

## States

- `selected` draws a 2 dp brand boundary. It is visual only: state the choice for accessibility where
  the choice is owned — `Modifier.semantics { selected = … }` on a clickable card, with
  `Modifier.selectableGroup()` on the list.
- Clickable: lifts on hover, settles on press and shows the hover boundary while pressed; keyboard
  focus draws the focus ring; disabled fades and cannot be activated.
- A card inside a card drops its shadow, so elevation never stacks.

## Accessibility

- The title is a heading. A clickable card is one button named by its content.

## Theme and tokens

`component.card.*` (`QeetrixCardComponentTokens`): backgrounds, foreground, boundary per state,
corner, and elevations (`Raised` at rest, `Hover` when lifted or elevated).

## Platform differences

- **Press feedback.** The web lifts an interactive card under the pointer only. Touch has no hover, so
  Android (like iOS) shows the hover boundary while pressed; with a mouse the card lifts as on the web.
- **Edge-to-edge media.** The web clips first or last images to the corners; here the content is one
  padded region.

Related: [Separator](Separator.md), [Button](Button.md).
