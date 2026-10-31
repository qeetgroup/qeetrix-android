package com.qeetgroup.qeetrix.components.pageindicator

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.ProgressBarRangeInfo
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.progressBarRangeInfo
import androidx.compose.ui.semantics.setProgress
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.qeetgroup.qeetrix.R
import com.qeetgroup.qeetrix.foundation.accessibility.QeetrixAccessibility
import com.qeetgroup.qeetrix.foundation.motion.QeetrixMotion
import com.qeetgroup.qeetrix.foundation.motion.qeetrixAnimationSpec
import com.qeetgroup.qeetrix.foundation.shape.QeetrixShapes
import com.qeetgroup.qeetrix.theme.QeetrixTheme
import kotlin.math.roundToInt

/**
 * Which page of paged content is showing — onboarding, a carousel: a dot per page, the current one a 16 dp bar in
 * `color.bg.brand.bold`, the others 6 dp `color.border.strong` dots, 6 dp apart. `@qeetrix/react` has no counterpart.
 *
 * - **Tapping** before the current page goes back one page, after it on one, as a platform page control does, when
 *   [onPageChange] is given; the row is one 48 dp control, as dots are too small to touch one by one, with 24 dp of
 *   tappable room either side of the dots.
 * - **Accessibility**: one adjustable element read "Page 2 of 4"; TalkBack's adjust gestures move a page.
 * - **Motion**: the current bar grows over `motion.duration.normal`, and at once under reduced motion.
 *
 * ```
 * QeetrixPageIndicator(pagerState.currentPage, pagerState.pageCount, onPageChange = { scope.launch { pagerState.animateScrollToPage(it) } })
 * ```
 */
@Composable
public fun QeetrixPageIndicator(
    page: Int,
    pageCount: Int,
    modifier: Modifier = Modifier,
    onPageChange: ((Int) -> Unit)? = null,
) {
    require(pageCount >= 1 && page in 0 until pageCount) { "page is from 0 to pageCount − 1." }
    val colors = QeetrixTheme.colors
    val words = stringResource(R.string.qx_page_indicator, page + 1, pageCount)
    val rtl = LocalLayoutDirection.current == LayoutDirection.Rtl
    Row(
        modifier
            .heightIn(min = QeetrixAccessibility.MinimumTouchTarget)
            .widthIn(min = QeetrixAccessibility.MinimumTouchTarget)
            .then(
                if (onPageChange == null) {
                    Modifier
                } else {
                    // One control, as a platform page control: a tap before the current page goes back one, after it on one.
                    Modifier
                        .pointerInput(page, pageCount, rtl) {
                            detectTapGestures { tap ->
                                val step = (DOT + GAP).toPx()
                                // The dots are centred in the control, which is wider than they are by TAP_ROOM a side.
                                val dots = (pageCount - 1) * step + (CURRENT + GAP).toPx()
                                val centre = (size.width - dots) / 2 + page * step + (CURRENT + GAP).toPx() / 2
                                val x = if (rtl) size.width - tap.x else tap.x
                                val next = if (x < centre) page - 1 else page + 1
                                if (next in 0 until pageCount) onPageChange(next)
                            }
                        }
                },
            ).clearAndSetSemantics {
                contentDescription = words
                progressBarRangeInfo =
                    ProgressBarRangeInfo(page.toFloat(), 0f..(pageCount - 1).toFloat(), steps = (pageCount - 2).coerceAtLeast(0))
                if (onPageChange != null) {
                    setProgress { target ->
                        val next = target.roundToInt().coerceIn(0, pageCount - 1)
                        if (next != page) onPageChange(next)
                        true
                    }
                }
            }.then(if (onPageChange != null) Modifier.padding(horizontal = TAP_ROOM) else Modifier),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        for (index in 0 until pageCount) {
            val current = index == page
            val width by animateDpAsState(
                if (current) CURRENT else DOT,
                qeetrixAnimationSpec(QeetrixMotion.EasingStandard, QeetrixMotion.DurationNormal),
                label = "page-dot",
            )
            Box(
                Modifier
                    .padding(horizontal = GAP / 2)
                    .width(width)
                    .height(DOT)
                    .background(if (current) colors.bgBrandBold else colors.borderStrong, QeetrixShapes.Full),
            )
        }
    }
}

/** A page's dot. */
private val DOT = 6.dp

/** The current page's bar. */
private val CURRENT = 16.dp

/** Between dots. */
private val GAP = 6.dp

/**
 * Tappable room either side of the dots: on its own a row of dots is about as wide as one fingertip, so going back
 * from the first page's bar, or on from the last, would leave a target a few dp wide.
 */
private val TAP_ROOM = 24.dp
