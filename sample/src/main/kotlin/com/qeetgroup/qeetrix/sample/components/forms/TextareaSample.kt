package com.qeetgroup.qeetrix.sample.components.forms

import androidx.compose.foundation.text.input.InputTransformation
import androidx.compose.foundation.text.input.maxLength
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.runtime.Composable
import androidx.compose.ui.autofill.ContentType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.qeetgroup.qeetrix.components.input.QeetrixFieldMessage
import com.qeetgroup.qeetrix.components.input.QeetrixFieldMessageTone
import com.qeetgroup.qeetrix.components.input.QeetrixTextarea
import com.qeetgroup.qeetrix.sample.SampleNote
import com.qeetgroup.qeetrix.sample.SamplePage
import com.qeetgroup.qeetrix.sample.SamplePreview
import com.qeetgroup.qeetrix.sample.SampleSection

/** Multi-line input: growing with its text, a character limit with a live count, and the states. */
@Composable
fun TextareaSample() {
    SamplePage {
        SampleSection("Feedback") {
            val feedback = rememberTextFieldState()
            val left = 280 - feedback.text.length
            QeetrixTextarea(
                feedback,
                label = "What could be better?",
                placeholder = "Tell us what happened and what you expected.",
                inputTransformation = InputTransformation.maxLength(280),
                message = if (left <= 40) QeetrixFieldMessage("$left characters left", QeetrixFieldMessageTone.Warning) else null,
            )
            SampleNote("It grows from two lines to 384 dp, then scrolls. Enter starts a new line; the limit is an input transformation.")
        }
        SampleSection("Fixed height in lines") {
            QeetrixTextarea(
                rememberTextFieldState(),
                label = "Address",
                minLines = 3,
                maxLines = 3,
                description = "Three lines, then it scrolls.",
                contentType = ContentType.PostalAddress,
            )
        }
        SampleSection("Read-only and disabled") {
            QeetrixTextarea(rememberTextFieldState("12 Residency Road\nBengaluru 560025"), label = "Registered office", readOnly = true)
            QeetrixTextarea(rememberTextFieldState("Locked by an administrator."), label = "Notes", enabled = false)
        }
    }
}

@PreviewLightDark
@Composable
private fun TextareaSamplePreview() {
    SamplePreview { TextareaSample() }
}

@Preview(name = "200% text", fontScale = 2f)
@Composable
private fun TextareaLargeTextPreview() {
    SamplePreview { QeetrixTextarea(rememberTextFieldState("Long notes stay readable at large text sizes."), label = "Notes") }
}
