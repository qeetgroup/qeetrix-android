package com.qeetgroup.qeetrix.sample.components.datadisplay

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.qeetgroup.qeetrix.components.button.QeetrixButton
import com.qeetgroup.qeetrix.components.button.QeetrixButtonSize
import com.qeetgroup.qeetrix.components.button.QeetrixButtonVariant
import com.qeetgroup.qeetrix.components.list.QeetrixListItem
import com.qeetgroup.qeetrix.components.list.QeetrixSwipeAction
import com.qeetgroup.qeetrix.components.list.QeetrixSwipeActionTone
import com.qeetgroup.qeetrix.components.list.QeetrixSwipeActions
import com.qeetgroup.qeetrix.components.separator.QeetrixSeparator
import com.qeetgroup.qeetrix.icons.QeetrixIconName
import com.qeetgroup.qeetrix.sample.SampleNote
import com.qeetgroup.qeetrix.sample.SamplePage
import com.qeetgroup.qeetrix.sample.SamplePreview
import com.qeetgroup.qeetrix.sample.SampleSection
import com.qeetgroup.qeetrix.sample.SampleSurface

private val Accounts = listOf("Acme" to "priya@acme.in", "Qeet Pay" to "finance@qeet.in", "Northwind" to "admin@northwind.in")

/** Swipe actions on account rows: Pin toward the end, Remove (confirmed) toward the start. */
@Composable
fun SwipeActionsSample() {
    val accounts = remember { mutableStateListOf(*Accounts.toTypedArray()) }
    val pinned = remember { mutableStateListOf<String>() }
    SamplePage {
        SampleSection("Account rows") {
            SampleSurface {
                accounts.forEachIndexed { index, (name, email) ->
                    if (index > 0) QeetrixSeparator()
                    QeetrixSwipeActions(
                        startAction =
                            QeetrixSwipeAction(
                                if (name in pinned) "Unpin" else "Pin",
                                QeetrixIconName.MapPin,
                                { if (name in pinned) pinned.remove(name) else pinned.add(name) },
                                QeetrixSwipeActionTone.Brand,
                            ),
                        endAction =
                            QeetrixSwipeAction(
                                "Remove",
                                QeetrixIconName.Trash,
                                { accounts.removeAll { it.first == name } },
                                QeetrixSwipeActionTone.Danger,
                                confirmTitle = "Remove $name from this phone?",
                                confirmDescription = "Its codes stop here. You can add it again with its QR code.",
                            ),
                    ) {
                        QeetrixListItem(name, description = email, metadata = if (name in pinned) "Pinned" else null)
                    }
                }
            }
            if (accounts.size < Accounts.size) {
                QeetrixButton(
                    "Put them back",
                    onClick = {
                        accounts.clear()
                        accounts.addAll(Accounts)
                    },
                    variant = QeetrixButtonVariant.Ghost,
                    size = QeetrixButtonSize.Sm,
                )
            }
            SampleNote(
                "Swipe toward the start for Remove (it asks first), toward the end for Pin; swipe all the way to run it. " +
                    "TalkBack lists both as actions of the row, so nothing needs a swipe.",
            )
        }
    }
}

@PreviewLightDark
@Composable
private fun SwipeActionsSamplePreview() {
    SamplePreview { SwipeActionsSample() }
}
