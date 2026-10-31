package com.qeetgroup.qeetrix.sample.components.actions

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.qeetgroup.qeetrix.components.actionbar.QeetrixBottomActionBar
import com.qeetgroup.qeetrix.components.button.QeetrixButton
import com.qeetgroup.qeetrix.components.button.QeetrixButtonVariant
import com.qeetgroup.qeetrix.components.descriptionlist.QeetrixDescriptionItem
import com.qeetgroup.qeetrix.components.descriptionlist.QeetrixDescriptionList
import com.qeetgroup.qeetrix.foundation.spacing.QeetrixSpace
import com.qeetgroup.qeetrix.sample.SampleNote
import com.qeetgroup.qeetrix.sample.SamplePage
import com.qeetgroup.qeetrix.sample.SamplePreview
import com.qeetgroup.qeetrix.sample.SampleSection
import com.qeetgroup.qeetrix.sample.SampleSurface

/** The bottom action bar on a sign-in request: the request scrolls, Deny and Approve stay; and a single action. */
@Composable
fun BottomActionBarSample() {
    SamplePage {
        SampleSection("A sign-in request") {
            SampleSurface {
                val scroll = rememberScrollState()
                Column(Modifier.fillMaxWidth().height(420.dp)) {
                    Column(Modifier.weight(1f).verticalScroll(scroll).padding(QeetrixSpace.InsetLg)) {
                        QeetrixDescriptionList {
                            QeetrixDescriptionItem("App", "Qeet Pay console")
                            QeetrixDescriptionItem("Browser", "Chrome on macOS")
                            QeetrixDescriptionItem("Where", "Bengaluru, India")
                            QeetrixDescriptionItem("When", "Just now")
                            QeetrixDescriptionItem("IP address", "103.21.58.44")
                            QeetrixDescriptionItem("Account", "priya@acme.in")
                            QeetrixDescriptionItem("Request", "QX-7F3K")
                        }
                    }
                    QeetrixBottomActionBar(scroll = scroll) {
                        QeetrixButton("Deny", onClick = {}, variant = QeetrixButtonVariant.Soft, fullWidth = true)
                        QeetrixButton("Approve", onClick = {}, fullWidth = true)
                    }
                }
            }
            SampleNote(
                "Scroll the request: a rule appears along the bar's top while content is under it. At large text the two " +
                    "buttons stack, in the same order.",
            )
        }
        SampleSection("One action, ruled") {
            SampleSurface {
                QeetrixBottomActionBar(ruled = true) { QeetrixButton("Continue", onClick = {}, fullWidth = true) }
            }
        }
    }
}

@PreviewLightDark
@Composable
private fun BottomActionBarSamplePreview() {
    SamplePreview { BottomActionBarSample() }
}
