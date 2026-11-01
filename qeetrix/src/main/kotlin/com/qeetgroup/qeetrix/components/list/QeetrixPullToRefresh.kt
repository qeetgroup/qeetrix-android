package com.qeetgroup.qeetrix.components.list

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.BasicText
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.qeetgroup.qeetrix.R
import com.qeetgroup.qeetrix.components.progress.QeetrixProgressRing
import com.qeetgroup.qeetrix.components.progress.QeetrixProgressRingSize
import com.qeetgroup.qeetrix.components.spinner.QeetrixSpinner
import com.qeetgroup.qeetrix.components.spinner.QeetrixSpinnerSize
import com.qeetgroup.qeetrix.components.spinner.QeetrixSpinnerTone
import com.qeetgroup.qeetrix.foundation.elevation.QeetrixShadow
import com.qeetgroup.qeetrix.foundation.elevation.qeetrixShadow
import com.qeetgroup.qeetrix.foundation.haptics.QeetrixHapticRole
import com.qeetgroup.qeetrix.foundation.haptics.rememberQeetrixHaptics
import com.qeetgroup.qeetrix.foundation.shape.QeetrixShapes
import com.qeetgroup.qeetrix.theme.QeetrixTheme

/**
 * Pull a list down to refresh it — Qeet Auth's pending approvals. `@qeetrix/react` has no counterpart (touch only). The
 * pull is the platform's (Material's nested-scroll handling, so any scrolling [content] works); the indicator is
 * Qeetrix's: a 40 dp `color.surface.raised` disc with the overlay shadow, a [QeetrixProgressRing] filling as the pull
 * nears the point where it refreshes, then the brand [QeetrixSpinner] while [refreshing]. Under reduced motion the
 * spinner stays still and the disc appears without sliding.
 *
 * - **[refreshing]** and [onRefresh] are the app's: set it while the work runs.
 * - **Feel and words**: [QeetrixHapticRole.LightImpact] when a pull reaches the threshold; when the refresh finishes,
 *   "Updated" is announced politely (or [finishedMessage]).
 *
 * ```
 * QeetrixPullToRefresh(refreshing, onRefresh = viewModel::refresh) { LazyColumn { … } }
 * ```
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
public fun QeetrixPullToRefresh(
    refreshing: Boolean,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier,
    finishedMessage: String = stringResource(R.string.qx_refresh_finished),
    content: @Composable BoxScope.() -> Unit,
) {
    val state = rememberPullToRefreshState()
    val haptics = rememberQeetrixHaptics()
    val reached = state.distanceFraction >= 1f
    LaunchedEffect(reached) { if (reached && !refreshing) haptics.perform(QeetrixHapticRole.LightImpact) }
    var announced by remember { mutableStateOf<String?>(null) }
    var wasRefreshing by remember { mutableStateOf(refreshing) }
    LaunchedEffect(refreshing) {
        if (wasRefreshing && !refreshing) announced = finishedMessage
        wasRefreshing = refreshing
    }
    val reduce = QeetrixTheme.reduceMotion
    PullToRefreshBox(
        isRefreshing = refreshing,
        onRefresh = onRefresh,
        modifier = modifier,
        state = state,
        indicator = {
            val fraction = state.distanceFraction
            val disc = QeetrixTheme.colors.surfaceRaised
            if (refreshing || fraction > 0f) {
                Box(
                    Modifier
                        .align(Alignment.TopCenter)
                        .graphicsLayer {
                            translationY =
                                if (reduce) INDICATOR_TOP.toPx() else (fraction.coerceAtMost(1f) * PULL_DISTANCE.toPx()) - DISC.toPx() / 2
                            alpha = if (refreshing) 1f else fraction.coerceIn(0f, 1f)
                        }.size(DISC)
                        .qeetrixShadow(QeetrixShadow.Overlay, QeetrixShapes.Full, disc),
                    contentAlignment = Alignment.Center,
                ) {
                    if (refreshing) {
                        QeetrixSpinner(size = QeetrixSpinnerSize.Lg, tone = QeetrixSpinnerTone.Brand, decorative = true)
                    } else {
                        QeetrixProgressRing(fraction.coerceIn(0f, 1f), size = QeetrixProgressRingSize.Lg)
                    }
                }
            }
        },
    ) {
        content()
        // The finished refresh, said once politely; invisible.
        val words = announced
        if (words != null) {
            BasicText(words, Modifier.size(1.dp).graphicsLayer { alpha = 0f }.semantics { liveRegion = LiveRegionMode.Polite })
        }
    }
}

/** The indicator's disc. */
private val DISC = 40.dp

/** How far the disc comes down at the threshold. */
private val PULL_DISTANCE = 80.dp

/** Where the disc rests under reduced motion. */
private val INDICATOR_TOP = 16.dp
