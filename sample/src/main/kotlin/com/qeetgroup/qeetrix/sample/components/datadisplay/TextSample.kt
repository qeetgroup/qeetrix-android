package com.qeetgroup.qeetrix.sample.components.datadisplay

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.qeetgroup.qeetrix.components.typography.QeetrixText
import com.qeetgroup.qeetrix.components.typography.QeetrixTextVariant
import com.qeetgroup.qeetrix.sample.SampleNote
import com.qeetgroup.qeetrix.sample.SamplePage
import com.qeetgroup.qeetrix.sample.SamplePreview
import com.qeetgroup.qeetrix.sample.SampleSection
import com.qeetgroup.qeetrix.theme.QeetrixTheme

/** The typography variants, and text that wraps, truncates, aligns and mixes styles. */
@Composable
fun TextSample() {
    SamplePage {
        SampleSection("Variants") {
            QeetrixText("Plans and pricing", variant = QeetrixTextVariant.H1)
            QeetrixText("Choose a plan", variant = QeetrixTextVariant.H2)
            QeetrixText("Billing", variant = QeetrixTextVariant.H3)
            QeetrixText("Payment method", variant = QeetrixTextVariant.H4)
            QeetrixText("Every plan includes passkeys, audit logs and 99.9% uptime.", variant = QeetrixTextVariant.Lead)
            QeetrixText(
                "Body copy in Qeet Sans. Prices include GST; invoices are issued on the first of each month " +
                    "and can be downloaded from the billing page.",
            )
            QeetrixText("“Simple tools, used well, beat clever ones.”", variant = QeetrixTextVariant.Blockquote)
            QeetrixText("₹1,999 per seat", variant = QeetrixTextVariant.Large)
            QeetrixText("Billed annually", variant = QeetrixTextVariant.Small)
            QeetrixText("Last updated 12 March", variant = QeetrixTextVariant.Muted)
            QeetrixText("QEETRIX_REGION=ap-south-1", variant = QeetrixTextVariant.InlineCode)
            SampleNote("h1 to h4 are headings for TalkBack, so its heading navigation jumps between them.")
        }
        SampleSection("Wrapping, truncation and alignment") {
            QeetrixText(
                "A long single line that is truncated with an ellipsis when it does not fit the width available to it.",
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            QeetrixText("Centred", Modifier.fillMaxWidth(), textAlign = TextAlign.Center)
            QeetrixText(
                "Mixed scripts wrap and shape correctly: नमस्ते दुनिया, مرحبا بالعالم, and English.",
            )
        }
        SampleSection("Styled spans and colour") {
            QeetrixText(
                buildAnnotatedString {
                    append("Your trial ends ")
                    withStyle(SpanStyle(fontWeight = FontWeight.SemiBold)) { append("in 3 days") }
                    append(".")
                },
            )
            QeetrixText("Payment failed. Update your card.", color = QeetrixTheme.colors.textDanger)
        }
    }
}

@PreviewLightDark
@Composable
private fun TextSamplePreview() {
    SamplePreview { TextSample() }
}

@Preview(name = "200% text", fontScale = 2f)
@Composable
private fun TextLargePreview() {
    SamplePreview { QeetrixText("Choose a plan", variant = QeetrixTextVariant.H2) }
}
