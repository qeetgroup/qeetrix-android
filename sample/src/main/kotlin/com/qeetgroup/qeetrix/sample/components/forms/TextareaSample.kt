package com.qeetgroup.qeetrix.sample.components.forms

import androidx.compose.foundation.text.input.InputTransformation
import androidx.compose.foundation.text.input.maxLength
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.runtime.Composable
import androidx.compose.ui.autofill.ContentType
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.qeetgroup.qeetrix.components.input.QeetrixTextarea
import com.qeetgroup.qeetrix.sample.SampleNote
import com.qeetgroup.qeetrix.sample.SamplePage
import com.qeetgroup.qeetrix.sample.SamplePreview
import com.qeetgroup.qeetrix.sample.SampleSection

/** Multi-line text: rows, growing to maxRows, a length limit, an address for AutoFill, and the states. */
@Composable
fun TextareaSample() {
    SamplePage {
        SampleSection("Growing with the text") {
            val feedback = rememberTextFieldState()
            QeetrixTextarea(
                feedback,
                label = "What could be better?",
                placeholder = "e.g. The invoice total did not match the order",
                description = "${feedback.text.length} of 500 characters.",
                maxRows = 8,
                inputTransformation = InputTransformation.maxLength(500),
            )
            SampleNote("It starts three lines tall and grows with the text up to eight, then scrolls inside itself.")
        }
        SampleSection("An address") {
            QeetrixTextarea(
                rememberTextFieldState(),
                label = "Billing address",
                rows = 2,
                contentType = ContentType.PostalAddress,
                error = "Add a PIN code to the address.",
            )
        }
        SampleSection("Read-only and disabled") {
            QeetrixTextarea(rememberTextFieldState("Approved by finance on 12 Oct."), label = "Audit note", rows = 1, readOnly = true)
            QeetrixTextarea(rememberTextFieldState("Locked"), label = "Notes", rows = 1, enabled = false)
        }
    }
}

@PreviewLightDark
@Composable
private fun TextareaSamplePreview() {
    SamplePreview { TextareaSample() }
}
