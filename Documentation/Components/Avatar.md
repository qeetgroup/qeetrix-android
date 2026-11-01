# Avatar

`QeetrixAvatar`, `QeetrixAvatarItem`, `QeetrixAvatarSize`, `QeetrixAvatarShape` · `components.avatar` ·
`@qeetrix/react`: [Avatar](https://ui.qeet.in/components/avatar) and AvatarItem (experimental) ·
sample: Data display → Avatar

A person, or an organisation, application or tenant: their photo or logo, or their initials.

```kotlin
QeetrixAvatar("Priya Sharma")
QeetrixAvatar("Acme Pvt Ltd", size = QeetrixAvatarSize.Lg, shape = QeetrixAvatarShape.Square, image = logo)
QeetrixAvatarItem("Priya Sharma", description = "priya.sharma@acme.in")
```

## Options

| Parameter | Values | Notes |
|:--|:--|:--|
| `name` | required | The accessible name and the source of the initials. |
| `image` | a `Painter` | The web's `src`. Load it yourself (resources, Coil, Glide…); pass null while it loads or when it fails, and the initials show. |
| `size` | `Sm` 24 · `Md` 32 (default) · `Lg` 40 · `Xl` 48 dp | Fixed steps, the same at every density. |
| `shape` | `Circle` (default) for a person · `Square` for an organisation, an application or a tenant | Square has the controls' 4 dp corners (`radius.control`), so it never reads as a person. |
| `decorative` | `false` (default) | `true` when the name is shown beside it: hidden from TalkBack, so the name isn't read twice. |

**Initials** (avatar.tsx's rule): the first grapheme of the first and last words, upper-cased for the locale
("Priya Sharma" is "PS"). A company's legal form doesn't count ("Kaveri Foods Private Limited" is "KF"), and a
name left with one word takes its first two ("Acme Pvt Ltd" is "AC"). Graphemes come from `BreakIterator`, so a
letter keeps its marks and Indic names stay whole. They are `label.sm` (`label.md` at `Lg` and `Xl`) in
`color.text.default` on `color.bg.neutral.subtle-pressed`, centred on their capitals' height rather than the
line (optical centring, `text-box: trim-both cap alphabetic`), measured from the font's own cap height.

**AvatarItem**: the avatar (decorative), the name in `label.md` on `body.sm`'s 16 sp line, and an optional
`description` in `body.sm`, `color.text.subtle`: two lines as tall as a `Md` avatar, `space.gap.sm` from it. Both
wrap rather than end in an ellipsis, a long email breaking where it must (avatar-item.css 0.4.0, QTRX-178). Read as
one element.

## Accessibility

- An avatar is one image named by `name`; the initials are not spelled out. `decorative` hides it.
- Never rely on the picture alone to identify someone: show the name nearby, and make the avatar decorative.

## Theme and tokens

`color.bg.neutral.subtle-pressed`, `color.text.default`, `typography.label.sm` / `label.md`, `radius.full` /
`radius.control`; AvatarItem `space.gap.sm`, `typography.body.sm`, `color.text.subtle`.

## Platform differences

- **Font scale.** The avatar is a fixed-size image, so its initials keep their size at every font scale rather
  than overflow it (the web scales the text in rem inside a px box). AvatarItem's lines grow with the font.
- **Images.** The app loads and caches the picture and passes a `Painter`; there is no URL loading or failure
  callback, as there is on the web's Base UI Avatar.
- **Not implemented:** forced-colours border (Android has no forced-colours mode; high-contrast text is the
  system's).
- **Removed since 2.x:** the `Xs` (20 dp) size, the no-name person icon, `contentDescription` (use `decorative`)
  and the status `badgeLabel` — `@qeetrix/react` has no presence badge; show a status as a Badge beside the name.

Related: [List item](ListItem.md), [Badge](Badge.md).
