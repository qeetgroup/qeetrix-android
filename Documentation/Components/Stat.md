# Stat

`QeetrixStat`, `QeetrixStatGroup`, `QeetrixStatTone` · `components.stat` · `@qeetrix/react`: Stat, StatGroup (experimental) · QTRX-124

Key numbers in a ruled strip.

```kotlin
QeetrixStatGroup {
    QeetrixStat("Active users", value = 1240512, note = "+3.2% this week", tone = QeetrixStatTone.Positive)
    QeetrixStat("Revenue", value = 1842600, currency = "INR")
}
```

## Look, behaviour and accessibility

- **Look** (stat.css): the label `label.sm` `text.subtle`, the value `heading.sm` tabular, the note `body.sm` in its tone; cells at least 200 dp, ruled in `border.default`, wrapping into ruled rows.
- Numbers through [formatting](../Foundations/FORMAT.md): `en-IN` writes 12,40,512 and ₹18,42,600 (whole amounts drop the fraction).
- Say a change in words ("+3.2% this week"); `loading` keeps the height and reads "Loading". No trend line or chart yet.

Tried on the phone emulator (`qeetrix-api36`, Android 16) on 31 October 2026, in light, dark, 200 % text and right to left, from the sample app's page: the amounts in the locale, the notes in their tones, loading.
