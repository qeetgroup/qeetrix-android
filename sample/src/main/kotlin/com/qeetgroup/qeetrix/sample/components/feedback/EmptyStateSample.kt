package com.qeetgroup.qeetrix.sample.components.feedback

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.qeetgroup.qeetrix.components.button.QeetrixButton
import com.qeetgroup.qeetrix.components.button.QeetrixButtonVariant
import com.qeetgroup.qeetrix.components.emptystate.QeetrixEmptyState
import com.qeetgroup.qeetrix.components.emptystate.QeetrixEmptyStateSize
import com.qeetgroup.qeetrix.components.emptystate.QeetrixEmptyStateVariant
import com.qeetgroup.qeetrix.icons.QeetrixIconName
import com.qeetgroup.qeetrix.sample.SampleNote
import com.qeetgroup.qeetrix.sample.SamplePage
import com.qeetgroup.qeetrix.sample.SamplePreview
import com.qeetgroup.qeetrix.sample.SampleSection
import com.qeetgroup.qeetrix.sample.SampleSurface

/** Every reason for nothing to show, in the three sizes, each with words and actions for its reason. */
@Composable
fun EmptyStateSample() {
    SamplePage {
        SampleSection("First use") {
            SampleSurface {
                QeetrixEmptyState(
                    "No invoices yet",
                    description = "Invoices appear here once you bill a customer.",
                    variant = QeetrixEmptyStateVariant.FirstUse,
                    icon = QeetrixIconName.FileText,
                ) {
                    QeetrixButton("Create invoice", onClick = {}, leadingIcon = QeetrixIconName.Plus)
                }
            }
        }
        SampleSection("No results") {
            SampleSurface {
                QeetrixEmptyState(
                    "No results",
                    description = "Nothing matches “quarterly”. Check the spelling or clear the filters.",
                    variant = QeetrixEmptyStateVariant.NoResults,
                    size = QeetrixEmptyStateSize.Sm,
                ) {
                    QeetrixButton("Clear search", onClick = {}, variant = QeetrixButtonVariant.Outline)
                    QeetrixButton("Clear filters", onClick = {}, variant = QeetrixButtonVariant.Ghost)
                }
            }
        }
        SampleSection("No permission") {
            SampleSurface {
                QeetrixEmptyState(
                    "You can't see this project",
                    description = "Ask the project's owner, Ada Lovelace, for access.",
                    variant = QeetrixEmptyStateVariant.NoPermission,
                    size = QeetrixEmptyStateSize.Sm,
                )
            }
        }
        SampleSection("Error") {
            SampleSurface {
                QeetrixEmptyState(
                    "Couldn't load invoices",
                    description = "Check your connection and try again.",
                    variant = QeetrixEmptyStateVariant.Error,
                    size = QeetrixEmptyStateSize.Sm,
                ) {
                    QeetrixButton(
                        "Try again",
                        onClick = {},
                        variant = QeetrixButtonVariant.Outline,
                        leadingIcon = QeetrixIconName.RefreshCw,
                    )
                }
            }
            SampleNote("Say what happened and what to do — never an error code or a stack trace.")
        }
        SampleSection("Large, for a whole screen") {
            SampleSurface {
                QeetrixEmptyState(
                    "Your inbox is clear",
                    description = "New messages from your team appear here.",
                    size = QeetrixEmptyStateSize.Lg,
                    icon = QeetrixIconName.Inbox,
                )
            }
        }
    }
}

@PreviewLightDark
@Composable
private fun EmptyStateSamplePreview() {
    SamplePreview { EmptyStateSample() }
}
