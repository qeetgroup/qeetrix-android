# Pull to refresh

`QeetrixPullToRefresh` · `components.list` · `@qeetrix/react`: none · QTRX-107

Pull a list down to refresh it.

```kotlin
QeetrixPullToRefresh(refreshing, onRefresh = viewModel::refresh) { LazyColumn { … } }
```

## Look, behaviour and accessibility

- The platform's pull (Material's nested scroll) with Qeetrix's indicator: a 40 dp `surface.raised` disc with the overlay shadow, a progress ring filling as the pull nears the threshold, then the brand spinner.
- A LightImpact haptic at the threshold; "Updated" is announced when the refresh finishes. Reduced motion: no slide.
- Replaces the earlier decision not to build one (ADR 0016).

Tried on the phone emulator (`qeetrix-api36`, Android 16) on 31 October 2026, in light, dark, 200 % text and right to left, from the sample app's page, pulling a list for real: the ring as it nears the threshold, the spinner while refreshing, the new row after.
