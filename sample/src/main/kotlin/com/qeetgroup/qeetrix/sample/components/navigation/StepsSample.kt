package com.qeetgroup.qeetrix.sample.components.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.qeetgroup.qeetrix.components.button.QeetrixButton
import com.qeetgroup.qeetrix.components.button.QeetrixButtonSize
import com.qeetgroup.qeetrix.components.button.QeetrixButtonVariant
import com.qeetgroup.qeetrix.components.steps.QeetrixStep
import com.qeetgroup.qeetrix.components.steps.QeetrixStepStatus
import com.qeetgroup.qeetrix.components.steps.QeetrixSteps
import com.qeetgroup.qeetrix.components.steps.QeetrixStepsOrientation
import com.qeetgroup.qeetrix.sample.SampleFlow
import com.qeetgroup.qeetrix.sample.SampleNote
import com.qeetgroup.qeetrix.sample.SamplePage
import com.qeetgroup.qeetrix.sample.SamplePreview
import com.qeetgroup.qeetrix.sample.SampleSection

private val Setup =
    listOf(
        QeetrixStep("Account"),
        QeetrixStep("Verify"),
        QeetrixStep("Link phone"),
        QeetrixStep("Done"),
    )

/** Steps: a set-up flow moved with Back and Next, steps to go back to, and a vertical list with a failed step. */
@Composable
fun StepsSample() {
    SamplePage {
        SampleSection("A set-up flow") {
            var current by rememberSaveable { mutableIntStateOf(1) }
            QeetrixSteps(Setup, current)
            SampleFlow {
                QeetrixButton("Back", onClick = {
                    current = (current - 1).coerceAtLeast(0)
                }, variant = QeetrixButtonVariant.Ghost, size = QeetrixButtonSize.Sm)
                QeetrixButton("Next", onClick = { current = (current + 1).coerceAtMost(Setup.lastIndex) }, size = QeetrixButtonSize.Sm)
            }
            SampleNote("TalkBack reads \"Step 2 of 4, Verify, current\".")
        }
        SampleSection("Going back to a step") {
            var current by rememberSaveable { mutableIntStateOf(2) }
            QeetrixSteps(Setup, current, onStepSelect = { if (it < current) current = it })
            SampleNote("With onStepSelect, the steps done are buttons: tap Account to go back to it.")
        }
        SampleSection("Vertical, with a failed step") {
            QeetrixSteps(
                listOf(
                    QeetrixStep("Payment received", "₹12,400 from Northwind"),
                    QeetrixStep("Invoice sent", "To accounts@northwind.in"),
                    QeetrixStep("GST filed", "The portal didn't answer. Try again later.", QeetrixStepStatus.Failed),
                    QeetrixStep("Settled"),
                ),
                current = 2,
                orientation = QeetrixStepsOrientation.Vertical,
            )
        }
    }
}

@PreviewLightDark
@Composable
private fun StepsSamplePreview() {
    SamplePreview { StepsSample() }
}
