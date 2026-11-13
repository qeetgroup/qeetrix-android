# ✨ Icons

Every `@qeetrix/icons` icon is available on Android by its upstream name, in both shapes (sharp on controls, round in
navigation), outline and — where upstream draws one — filled, mirrored in right to left where the meaning has a
direction. qeetrix-ios carries the same names at the same `@qeetrix/icons` version.

## 📦 Two modules

| | Icons | Name |
|:--|:--|:--|
| `com.qeetgroup:qeetrix` | The 100 core icons: what the components and the sample draw | `QeetrixIconName.Send` |
| `com.qeetgroup:qeetrix-icons-extended` (optional) | The other 1,763 | `QeetrixIconName.AlarmClock`, imported from `com.qeetgroup.qeetrix.icons.extended` |

```kotlin
implementation("com.qeetgroup:qeetrix-icons-extended:0.3.0") // always the same version as com.qeetgroup:qeetrix

QeetrixButton("Scan", onClick = ::scan, icon = QeetrixIconName.QrCode)       // core
QeetrixEmptyState(icon = QeetrixIconName.AlarmClock, title = "No reminders") // extended
```

Every component that takes an icon takes either kind: both are `QeetrixIconName`. Two names are equal when their
`value`s (`"alarm-clock"`) are; match them with `==`, or a `when` with an `else` branch, since the catalogue grows with
`@qeetrix/icons`. `QeetrixIconName.catalogue` lists the core icons and `QeetrixIconName.extendedCatalogue` the
extended ones, for an icon picker or a gallery such as the sample's Icons page.

## 📏 What an app pays

Each extended icon is its own class holding its drawings as compact path strings, decoded the first time the icon is
drawn. R8 drops every icon class an app doesn't name, so a release build grows only by the icons it uses.

**Budget:** 4 KiB of DEX per extended icon an app uses. `Scripts/icon-budget` builds `:icon-budget` twice with R8 — one
core icon, then twenty extended ones as well — and fails the local gate unless R8 kept no extended icon in the first build,
exactly the twenty in the second, and the second grew by no more than 20 × 4 KiB. Naming
`QeetrixIconName.extendedCatalogue` keeps every icon, so an app that draws fixed icons names them instead.

Debug builds aren't minified, so they carry the whole module. An app that never uses an extended icon doesn't add the
dependency at all.

## 🔄 Where the icons come from

`Upstream/manifest.json` pins `@qeetrix/icons`; `Scripts/sync-tokens` copies every icon's catalogue entry and SVG
drawings into `Upstream/qeetrix-icons/`, and `Scripts/generate-tokens` writes the Kotlin: the core icons into
`qeetrix/…/icons/generated/QeetrixIconCatalogue.kt`, every other one into `qeetrix-icons-extended/src/main/`, one file
per icon. Nothing there is edited by hand; the build fails if a generated file drifts from the snapshot.

- **A new `@qeetrix/icons` version:** bump the pin, sync, generate. New icons appear in the extended module; an icon
  upstream removes or renames disappears and the build names it.
- **An icon a component or the sample now draws:** add its name to `qeetrixIcons.icons`, so it moves into the core set,
  then sync and generate. Apps keep writing `QeetrixIconName.X`; they drop the `icons.extended` import.
