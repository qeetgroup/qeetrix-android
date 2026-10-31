package com.qeetgroup.qeetrix.sample.components.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.qeetgroup.qeetrix.components.pageindicator.QeetrixPageIndicator
import com.qeetgroup.qeetrix.components.typography.QeetrixHeading
import com.qeetgroup.qeetrix.components.typography.QeetrixHeadingSize
import com.qeetgroup.qeetrix.foundation.shape.QeetrixShapes
import com.qeetgroup.qeetrix.sample.SampleNote
import com.qeetgroup.qeetrix.sample.SamplePage
import com.qeetgroup.qeetrix.sample.SamplePreview
import com.qeetgroup.qeetrix.sample.SampleSection
import com.qeetgroup.qeetrix.theme.QeetrixTheme
import kotlinx.coroutines.launch

private val Onboarding = listOf("Approve sign-ins", "Codes for every account", "Back up to Qeet ID", "You're set")

/** The page indicator under an onboarding pager: swipe the pages, or tap either side of the dots. */
@Composable
fun PageIndicatorSample() {
    SamplePage {
        SampleSection("Onboarding") {
            val pager = rememberPagerState { Onboarding.size }
            val scope = rememberCoroutineScope()
            HorizontalPager(pager, Modifier.fillMaxWidth().height(180.dp)) { page ->
                Box(
                    Modifier.fillMaxWidth().height(180.dp).background(QeetrixTheme.colors.bgNeutralSubtle, QeetrixShapes.Surface),
                    contentAlignment = Alignment.Center,
                ) { QeetrixHeading(Onboarding[page], level = 3, size = QeetrixHeadingSize.TitleLg) }
            }
            Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                QeetrixPageIndicator(pager.currentPage, pager.pageCount, onPageChange = { scope.launch { pager.animateScrollToPage(it) } })
            }
            SampleNote("Tap left of the current bar to go back a page, right to go on. TalkBack reads \"Page 1 of 4\" and adjusts it.")
        }
    }
}

@PreviewLightDark
@Composable
private fun PageIndicatorSamplePreview() {
    SamplePreview { PageIndicatorSample() }
}
