package com.qeetgroup.qeetrix.sample.components.datadisplay

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.qeetgroup.qeetrix.components.typography.QeetrixHeading
import com.qeetgroup.qeetrix.components.typography.QeetrixHeadingSize
import com.qeetgroup.qeetrix.components.typography.QeetrixHeadingTone
import com.qeetgroup.qeetrix.components.typography.QeetrixText
import com.qeetgroup.qeetrix.components.typography.QeetrixTextSize
import com.qeetgroup.qeetrix.components.typography.QeetrixTextTone
import com.qeetgroup.qeetrix.components.typography.QeetrixTextWeight
import com.qeetgroup.qeetrix.sample.SampleNote
import com.qeetgroup.qeetrix.sample.SamplePage
import com.qeetgroup.qeetrix.sample.SamplePreview
import com.qeetgroup.qeetrix.sample.SampleSection

/**
 * Type — `@qeetrix/react`'s Heading and Text: headings by level and size, then running text in its sizes, weights,
 * tones and Fira Code, and text that wraps, truncates, aligns and mixes styles.
 */
@Composable
fun TextSample() {
    SamplePage {
        SampleSection("Headings") {
            QeetrixHeading("Plans and pricing", level = 1)
            QeetrixHeading("Choose a plan", level = 2)
            QeetrixHeading("Billing", level = 3)
            QeetrixHeading("Payment method", level = 4)
            QeetrixHeading("Invoices", level = 5)
            QeetrixHeading("Card ending 4242", level = 6)
            SampleNote(
                "Levels 1 to 6 default to heading.lg, md, sm and title.lg, md, sm. Each is a heading for TalkBack, so its " +
                    "heading navigation jumps between them; the level only chooses the default look on Android.",
            )
        }
        SampleSection("Sizes other than the level's") {
            QeetrixHeading("Welcome to Qeet", level = 1, size = QeetrixHeadingSize.DisplaySm)
            QeetrixHeading("Workspace settings", level = 1, size = QeetrixHeadingSize.HeadingSm)
            QeetrixHeading("Archived", level = 2, size = QeetrixHeadingSize.TitleSm, tone = QeetrixHeadingTone.Subtle)
            SampleNote(
                "Display sizes, in Qeet Sans's Geo letter style, are for welcoming screens only. A screen's title is " +
                    "heading.sm; a card's title.sm.",
            )
        }
        SampleSection("Body sizes and weights") {
            QeetrixText("Every plan includes passkeys, audit logs and 99.9% uptime.", size = QeetrixTextSize.Lg)
            QeetrixText(
                "Body text in Qeet Sans. Prices include GST; invoices are issued on the first of each month and can be " +
                    "downloaded from the billing page.",
            )
            QeetrixText("Billed annually", size = QeetrixTextSize.Sm)
            QeetrixText("₹1,999 per seat", weight = QeetrixTextWeight.Semibold)
            QeetrixText("Ada Lovelace", weight = QeetrixTextWeight.Medium)
            SampleNote("body.lg, md (the default) and sm; regular, medium and semibold are each a role's own weight.")
        }
        SampleSection("Tones and code") {
            QeetrixText("Last updated 12 March", tone = QeetrixTextTone.Subtle)
            QeetrixText("Payment failed. Update your card.", tone = QeetrixTextTone.Danger)
            QeetrixText("Your card was charged.", tone = QeetrixTextTone.Success)
            QeetrixText("QEETRIX_REGION=ap-south-1", mono = true, size = QeetrixTextSize.Sm)
            SampleNote(
                "Without a tone, text inherits the content colour. Status tones go with words that say the status. Mono " +
                    "is Fira Code at the same size, for code, IDs and keys.",
            )
        }
        SampleSection("Wrapping, truncation and alignment") {
            QeetrixText(
                "A long single line that ends in an ellipsis when it does not fit the width available to it.",
                maxLines = 1,
            )
            QeetrixText("Centred", Modifier.fillMaxWidth(), textAlign = TextAlign.Center)
            QeetrixText("Mixed scripts wrap and shape correctly: नमस्ते दुनिया, مرحبا بالعالم, and English.")
        }
        SampleSection("Styled spans") {
            QeetrixText(
                buildAnnotatedString {
                    append("Your trial ends ")
                    withStyle(SpanStyle(fontWeight = FontWeight.SemiBold)) { append("in 3 days") }
                    append(".")
                },
            )
            SampleNote("An AnnotatedString takes spans: a word that matters at a role's own weight, or a link in a theme colour.")
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
    SamplePreview { QeetrixHeading("Choose a plan", level = 2) }
}
