package com.qeetgroup.qeetrix.components.dialog

import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test

/** The action rules every dialog shares, apart from any drawing. */
class QeetrixDialogActionTest {
    @Test
    fun aDestructiveChoiceAlwaysComesWithCancelFirst() {
        val arranged = arrangeActions(listOf(QeetrixDialogAction("Delete", QeetrixDialogActionRole.Destructive) {}), "Cancel")
        val expected = listOf("Cancel" to QeetrixDialogActionRole.Cancel, "Delete" to QeetrixDialogActionRole.Destructive)
        assertEquals(expected, arranged.map { it.label to it.role })
    }

    @Test
    fun cancellingActionsComeFirstAndTheRestKeepTheirOrder() {
        val arranged =
            arrangeActions(
                listOf(
                    QeetrixDialogAction("Save draft") {},
                    QeetrixDialogAction("Publish") {},
                    QeetrixDialogAction("Keep editing", QeetrixDialogActionRole.Cancel) {},
                ),
                "Cancel",
            )
        assertEquals(listOf("Keep editing", "Save draft", "Publish"), arranged.map { it.label })
    }

    @Test
    fun anAcknowledgementNeedsNoCancel() {
        assertEquals(listOf("Got it"), arrangeActions(listOf(QeetrixDialogAction("Got it") {}), "Cancel").map { it.label })
    }

    @Test
    fun aDialogNeedsAnAction() {
        assertThrows(IllegalArgumentException::class.java) { arrangeActions(emptyList(), "Cancel") }
    }

    @Test
    fun aConfirmationIsCancelAndTheConfirmingRole() {
        assertEquals(
            listOf(QeetrixDialogActionRole.Cancel, QeetrixDialogActionRole.Destructive),
            confirmationActions("Remove", {}, destructive = true, cancelLabel = "Cancel").map { it.role },
        )
        assertEquals(
            listOf(QeetrixDialogActionRole.Cancel, QeetrixDialogActionRole.Default),
            confirmationActions("Send", {}, destructive = false, cancelLabel = "Not now").map { it.role },
        )
    }
}
