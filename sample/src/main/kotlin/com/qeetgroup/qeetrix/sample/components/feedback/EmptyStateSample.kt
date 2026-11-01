package com.qeetgroup.qeetrix.sample.components.feedback

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.qeetgroup.qeetrix.components.button.QeetrixButton
import com.qeetgroup.qeetrix.components.button.QeetrixButtonVariant
import com.qeetgroup.qeetrix.components.emptystate.QeetrixEmptyState
import com.qeetgroup.qeetrix.components.emptystate.QeetrixEmptyStateSize
import com.qeetgroup.qeetrix.components.emptystate.QeetrixEmptyStateTone
import com.qeetgroup.qeetrix.components.typography.QeetrixText
import com.qeetgroup.qeetrix.components.typography.QeetrixTextSize
import com.qeetgroup.qeetrix.icons.QeetrixIconName
import com.qeetgroup.qeetrix.sample.SampleNote
import com.qeetgroup.qeetrix.sample.SamplePage
import com.qeetgroup.qeetrix.sample.SamplePreview
import com.qeetgroup.qeetrix.sample.SampleSection
import com.qeetgroup.qeetrix.sample.SampleSurface

/** Empty states for each reason there is nothing to show, at the page and section sizes, with words and actions for it. */
@Composable
fun EmptyStateSample() {
    SamplePage {
        SampleSection("Nothing yet, for a whole view") {
            SampleSurface {
                QeetrixEmptyState(
                    "No invoices yet",
                    icon = QeetrixIconName.FileText,
                    description = "Invoices appear here once you bill a customer.",
                    level = 2,
                    footer = { QeetrixText("Or import them from a CSV file", size = QeetrixTextSize.Sm) },
                ) {
                    QeetrixButton("Create invoice", onClick = {}, icon = QeetrixIconName.Plus)
                }
            }
            SampleNote("With a level, the title is a heading for TalkBack: set one when this is the screen's main message.")
        }
        SampleSection("No results, inside a card") {
            SampleSurface {
                QeetrixEmptyState(
                    "No results",
                    icon = QeetrixIconName.SearchX,
                    description = "Nothing matches “quarterly”. Check the spelling or clear the filters.",
                    size = QeetrixEmptyStateSize.Section,
                ) {
                    QeetrixButton("Clear filters", onClick = {}, variant = QeetrixButtonVariant.Ghost)
                }
            }
        }
        SampleSection("No permission") {
            SampleSurface {
                QeetrixEmptyState(
                    "You can't see this project",
                    icon = QeetrixIconName.Lock,
                    description = "Ask the project's owner, Ada Lovelace, for access.",
                    size = QeetrixEmptyStateSize.Section,
                )
            }
        }
        SampleSection("An error") {
            SampleSurface {
                QeetrixEmptyState(
                    "Couldn't load invoices",
                    icon = QeetrixIconName.CircleAlert,
                    description = "Check your connection and try again.",
                    size = QeetrixEmptyStateSize.Section,
                    tone = QeetrixEmptyStateTone.Danger,
                ) {
                    QeetrixButton("Try again", onClick = {}, variant = QeetrixButtonVariant.Soft, icon = QeetrixIconName.RefreshCw)
                }
            }
            SampleNote("Say what happened and what to do — never an error code or a stack trace.")
        }
    }
}

@PreviewLightDark
@Composable
private fun EmptyStateSamplePreview() {
    SamplePreview { EmptyStateSample() }
}
