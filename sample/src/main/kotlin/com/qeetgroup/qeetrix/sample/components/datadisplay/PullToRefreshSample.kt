package com.qeetgroup.qeetrix.sample.components.datadisplay

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.qeetgroup.qeetrix.components.list.QeetrixListItem
import com.qeetgroup.qeetrix.components.list.QeetrixPullToRefresh
import com.qeetgroup.qeetrix.components.separator.QeetrixSeparator
import com.qeetgroup.qeetrix.sample.SampleNote
import com.qeetgroup.qeetrix.sample.SamplePage
import com.qeetgroup.qeetrix.sample.SamplePreview
import com.qeetgroup.qeetrix.sample.SampleSection
import com.qeetgroup.qeetrix.sample.SampleSurface
import kotlinx.coroutines.delay

/** Pull to refresh over a list of pending sign-in requests: each refresh takes two seconds and adds one. */
@Composable
fun PullToRefreshSample() {
    SamplePage {
        SampleSection("Pending approvals") {
            var refreshing by remember { mutableStateOf(false) }
            var count by remember { mutableIntStateOf(3) }
            LaunchedEffect(refreshing) {
                if (refreshing) {
                    delay(2_000)
                    count++
                    refreshing = false
                }
            }
            SampleSurface {
                QeetrixPullToRefresh(refreshing, onRefresh = { refreshing = true }, modifier = Modifier.fillMaxWidth().height(360.dp)) {
                    LazyColumn(Modifier.fillMaxWidth()) {
                        itemsIndexed(List(count) { count - it }) { index, n ->
                            if (index > 0) QeetrixSeparator()
                            QeetrixListItem("Sign-in request $n", description = "Chrome on macOS · Bengaluru", metadata = "${n * 3} min")
                        }
                    }
                }
            }
            SampleNote(
                "Pull the list down: the ring fills as you near the point where it refreshes, then the spinner turns while it " +
                    "works. \"Updated\" is announced when it finishes.",
            )
        }
    }
}

@PreviewLightDark
@Composable
private fun PullToRefreshSamplePreview() {
    SamplePreview { PullToRefreshSample() }
}
