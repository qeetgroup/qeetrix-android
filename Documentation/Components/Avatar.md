# Avatar

`QeetrixAvatar` · `components.avatar` · web: [Avatar](https://ui.qeet.in/docs/components/avatar) ·
sample: Data display → Avatar

A person's or an organisation's picture, initials, or a neutral icon.

```kotlin
QeetrixAvatar("Ada Lovelace", image = photo, size = QeetrixAvatarSize.Lg, badgeLabel = "Online")
QeetrixAvatar("Qeet Group", shape = QeetrixAvatarShape.Square)
```

## Usage

- `image` is any `Painter` your app has loaded (`painterResource`, a decoded bitmap, an image library's
  painter). Fetching, caching and resolving avatar URLs stay in the app; the library has no networking.
  Until the image is ready, pass `null` and the initials show.
- Initials: the first character of the first and last words, upper-cased for the locale. Characters
  are grapheme clusters, so a Devanagari or Thai letter keeps its combining marks.
- No name: a neutral person icon.
- Shapes: `Circle` for people, `Square` for organisations, workspaces and apps.
- Sizes: `Xs` 20, `Sm` 24, `Default` 32, `Lg` 40, `Xl` 48 dp.

## Accessibility

- One image named by `contentDescription` (default: the name), never letters spelled out. Pass
  `null` when the name is already visible beside the avatar, so it is not read twice.
- `badgeLabel` draws a status mark and joins the description ("Ada Lovelace, Online"), so the status
  is never colour alone.
- The avatar and its initials keep their size at every font scale; the name is still read out.

## Theme and tokens

`component.avatar.*` (`QeetrixAvatarComponentTokens`): fallback background and foreground, badge
colour and ring, the square's corner. A hairline blends darker (lighter in dark theme) so a pale photo
does not bleed into a pale surface.

## Platform differences

- **Images.** The web loads a URL and fades the photo in; Android and iOS take an image the app has
  loaded, so there is nothing to fade.
- **Initials.** The web takes the first code point of each word; Android and iOS take the first
  grapheme cluster, so combining marks stay attached.
- **Font scale.** The web scales the avatar and its initials together (rem); Android and iOS keep both
  fixed.
- The status badge mirrors to the other side in right-to-left layouts, as the web's `inset-e`.

Related: [Badge](Badge.md).
