# Page indicator

`QeetrixPageIndicator` · `components.pageindicator` · `@qeetrix/react`: none · QTRX-136

Which page of paged content is showing.

```kotlin
QeetrixPageIndicator(pagerState.currentPage, pagerState.pageCount, onPageChange = ::scrollTo)
```

## Look, behaviour and accessibility

- 6 dp `border.strong` dots, the current page a 16 dp `bg.brand.bold` bar, growing over `motion.duration.normal`.
- One 48 dp control: a tap before the current page goes back, after it forward, as platform page controls do (dots are too small to touch one by one). Read "Page 2 of 4"; adjustable with TalkBack.

Tried on the phone emulator (`qeetrix-api36`, Android 16) on 31 October 2026, in light, dark, 200 % text and right to left, from the sample app's page. Found there and fixed: the control was only as wide as its dots, so going back from the first page's bar left a target a few dp wide; it has 24 dp of tappable room either side, and a tap is judged from where the centred dots are (`aTapEitherSideOfTheDotsTurnsThePage`).
