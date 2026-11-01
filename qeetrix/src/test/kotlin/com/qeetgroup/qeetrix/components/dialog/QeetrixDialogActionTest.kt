package com.qeetgroup.qeetrix.components.dialog

import com.qeetgroup.qeetrix.components.button.QeetrixButtonTone
import com.qeetgroup.qeetrix.components.button.QeetrixButtonVariant
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test

/** The action rules every dialog shares, apart from any drawing: the design rules' hierarchy and footer order. */
class QeetrixDialogActionTest {
    @Test
    fun aDestructiveChoiceAlwaysComesWithCancelFirst() {
        val arranged = arrangeActions(listOf(QeetrixDialogAction("Delete", QeetrixDialogActionRole.Destructive) {}), "Cancel")
        val expected = listOf("Cancel" to QeetrixDialogActionRole.Cancel, "Delete" to QeetrixDialogActionRole.Destructive)
        assertEquals(expected, arranged.map { it.label to it.role })
    }

    @Test
    fun tertiaryFirstThenSecondaryThenThePrimaryEachKeepingItsOrder() {
        val arranged =
            arrangeActions(
                listOf(
                    QeetrixDialogAction("Publish") {},
                    QeetrixDialogAction("Save draft", QeetrixDialogActionRole.Secondary) {},
                    QeetrixDialogAction("Keep editing", QeetrixDialogActionRole.Cancel) {},
                    QeetrixDialogAction("Discard", QeetrixDialogActionRole.Cancel) {},
                ),
                "Cancel",
            )
        assertEquals(listOf("Keep editing", "Discard", "Save draft", "Publish"), arranged.map { it.label })
    }

    @Test
    fun anAcknowledgementNeedsNoCancel() {
        assertEquals(listOf("Got it"), arrangeActions(listOf(QeetrixDialogAction("Got it") {}), "Cancel").map { it.label })
    }

    @Test
    fun aDialogNeedsAnActionAndAtMostOnePrimaryOrDestructiveAndOneSecondary() {
        assertThrows(IllegalArgumentException::class.java) { arrangeActions(emptyList(), "Cancel") }
        assertThrows(IllegalArgumentException::class.java) {
            arrangeActions(
                listOf(QeetrixDialogAction("Save") {}, QeetrixDialogAction("Delete", QeetrixDialogActionRole.Destructive) {}),
                "Cancel",
            )
        }
        assertThrows(IllegalArgumentException::class.java) {
            arrangeActions(
                listOf(
                    QeetrixDialogAction("Export", QeetrixDialogActionRole.Secondary) {},
                    QeetrixDialogAction("Print", QeetrixDialogActionRole.Secondary) {},
                ),
                "Cancel",
            )
        }
    }

    @Test
    fun eachRoleDrawsItsButton() {
        val looks = QeetrixDialogActionRole.entries.associateWith { it.buttonVariant to it.buttonTone }
        assertEquals(QeetrixButtonVariant.Solid to QeetrixButtonTone.Brand, looks[QeetrixDialogActionRole.Primary])
        assertEquals(QeetrixButtonVariant.Soft to QeetrixButtonTone.Brand, looks[QeetrixDialogActionRole.Secondary])
        assertEquals(QeetrixButtonVariant.Ghost to QeetrixButtonTone.Brand, looks[QeetrixDialogActionRole.Cancel])
        assertEquals(QeetrixButtonVariant.Solid to QeetrixButtonTone.Danger, looks[QeetrixDialogActionRole.Destructive])
    }

    @Test
    fun aConfirmationIsCancelAndTheConfirmingRoleOfItsIntent() {
        assertEquals(
            listOf(QeetrixDialogActionRole.Cancel, QeetrixDialogActionRole.Destructive),
            confirmationActions("Remove", {}, QeetrixDialogIntent.Danger, cancelLabel = "Cancel").map { it.role },
        )
        assertEquals(
            listOf(QeetrixDialogActionRole.Cancel, QeetrixDialogActionRole.Primary),
            confirmationActions("Send", {}, QeetrixDialogIntent.Warning, cancelLabel = "Not now").map { it.role },
        )
    }

    @Test
    fun anAlertDialogIsDangerWhenItOffersADestructiveAction() {
        assertEquals(
            QeetrixDialogIntent.Danger,
            alertIntentFor(listOf(QeetrixDialogAction("Delete", QeetrixDialogActionRole.Destructive) {})),
        )
        assertEquals(QeetrixDialogIntent.Standard, alertIntentFor(listOf(QeetrixDialogAction("Send") {})))
    }

    @Test
    fun namesAreTheWebs() {
        assertEquals(listOf("standard", "danger", "warning"), QeetrixDialogIntent.entries.map { it.value })
        assertEquals(listOf("sm", "md", "lg", "xl", "fullscreen"), QeetrixDialogSize.entries.map { it.value })
        assertEquals(listOf(400f, 560f, 800f, 1120f), QeetrixDialogSize.entries.mapNotNull { it.width?.value })
        assertEquals(listOf("primary", "secondary", "cancel", "destructive"), QeetrixDialogActionRole.entries.map { it.value })
    }
}
