package com.qeetgroup.qeetrix.sample.components.utilities

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.qeetgroup.qeetrix.components.button.QeetrixButtonVariant
import com.qeetgroup.qeetrix.components.copy.QeetrixCopyButton
import com.qeetgroup.qeetrix.components.copy.QeetrixCopyField
import com.qeetgroup.qeetrix.components.copy.QeetrixCopyFieldLayout
import com.qeetgroup.qeetrix.sample.SampleFlow
import com.qeetgroup.qeetrix.sample.SampleNote
import com.qeetgroup.qeetrix.sample.SamplePage
import com.qeetgroup.qeetrix.sample.SamplePreview
import com.qeetgroup.qeetrix.sample.SampleSection

/** Copy: an icon button, a labelled button, a field, a row, and a secret. */
@Composable
fun CopySample() {
    SamplePage {
        SampleSection("Buttons") {
            SampleFlow {
                QeetrixCopyButton({ "inv_2K9QX4" }, label = "Copy invoice ID")
                QeetrixCopyButton({ "https://pay.qeet.in/i/inv_2K9QX4" }, text = "Copy link", variant = QeetrixButtonVariant.Soft)
            }
            SampleNote("The icon turns into a check and \"Copied\" is announced; Android 13 and later also confirm it themselves.")
        }
        SampleSection("A field") {
            QeetrixCopyField("Client ID", "qx_live_8f2b6c1e4d", description = "Use it in your app's configuration.")
        }
        SampleSection("A row") {
            QeetrixCopyField("Webhook URL", "https://hooks.northwind.in/qeet/payments/v2/events", layout = QeetrixCopyFieldLayout.Row)
            QeetrixCopyField(
                "Long and cut",
                "https://hooks.northwind.in/qeet/payments/v2/events",
                layout = QeetrixCopyFieldLayout.Row,
                wrap = false,
            )
        }
        SampleSection("A secret") {
            QeetrixCopyField("Recovery key", "QX7M-2PLK-9WQA-T3ZE", sensitive = true)
            SampleNote("Marked sensitive: Android 13 and later hide it in the clipboard preview.")
        }
    }
}

@PreviewLightDark
@Composable
private fun CopySamplePreview() {
    SamplePreview { CopySample() }
}
