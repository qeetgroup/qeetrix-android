package com.qeetgroup.qeetrix.sample.components.feedback

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.qeetgroup.qeetrix.components.button.QeetrixButton
import com.qeetgroup.qeetrix.components.button.QeetrixButtonSize
import com.qeetgroup.qeetrix.components.checklist.QeetrixCheckItem
import com.qeetgroup.qeetrix.components.checklist.QeetrixCheckList
import com.qeetgroup.qeetrix.components.checklist.QeetrixCheckStatus
import com.qeetgroup.qeetrix.sample.SampleNote
import com.qeetgroup.qeetrix.sample.SamplePage
import com.qeetgroup.qeetrix.sample.SamplePreview
import com.qeetgroup.qeetrix.sample.SampleSection

/** A check list: a phone's security checks in each status, with a footer action. */
@Composable
fun CheckListSample() {
    SamplePage {
        SampleSection("This phone's security") {
            QeetrixCheckList(footer = { QeetrixButton("Fix the failed check", onClick = {}, size = QeetrixButtonSize.Sm) }) {
                QeetrixCheckItem("Screen lock is on", QeetrixCheckStatus.Passed)
                QeetrixCheckItem(
                    "Notifications are allowed",
                    QeetrixCheckStatus.Failed,
                    detail = "Sign-in requests won't reach this phone.",
                )
                QeetrixCheckItem("Backup to Qeet ID", QeetrixCheckStatus.Running, detail = "Checking…")
                QeetrixCheckItem("Play Integrity", QeetrixCheckStatus.Pending)
            }
            SampleNote("Each check's status is in words for TalkBack (\"Passed\", \"Failed\"), not only in its icon.")
        }
    }
}

@PreviewLightDark
@Composable
private fun CheckListSamplePreview() {
    SamplePreview { CheckListSample() }
}
