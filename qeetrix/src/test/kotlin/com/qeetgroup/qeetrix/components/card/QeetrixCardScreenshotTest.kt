package com.qeetgroup.qeetrix.components.card

import com.qeetgroup.qeetrix.components.badge.QeetrixBadge
import com.qeetgroup.qeetrix.components.badge.QeetrixBadgeTone
import com.qeetgroup.qeetrix.components.button.QeetrixButton
import com.qeetgroup.qeetrix.components.button.QeetrixButtonTone
import com.qeetgroup.qeetrix.components.button.QeetrixButtonVariant
import com.qeetgroup.qeetrix.components.descriptionlist.QeetrixDescriptionItem
import com.qeetgroup.qeetrix.components.descriptionlist.QeetrixDescriptionList
import com.qeetgroup.qeetrix.components.typography.QeetrixText
import com.qeetgroup.qeetrix.screenshot.SCREENSHOT_QUALIFIERS
import com.qeetgroup.qeetrix.screenshot.ScreenshotStack
import com.qeetgroup.qeetrix.screenshot.captureStates
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

/** Reviewed baselines for [QeetrixCard] in `src/test/screenshots/card/`: header, body and footer, the variants, tones and the choice look. */
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(qualifiers = SCREENSHOT_QUALIFIERS)
class QeetrixCardScreenshotTest {
    @Test
    fun states() =
        captureStates("card") {
            ScreenshotStack {
                QeetrixCard(
                    title = "Workspace",
                    description = "3 members",
                    actions = { QeetrixButton("View all", onClick = {}, variant = QeetrixButtonVariant.Ghost) },
                    footer = {
                        QeetrixButton("Cancel", onClick = {}, variant = QeetrixButtonVariant.Ghost)
                        QeetrixButton("Manage", onClick = {})
                    },
                ) { QeetrixText("Created 12 March. Billing renews on 12 April.") }
                QeetrixCard(title = "Plan", actions = { QeetrixBadge("Pro", tone = QeetrixBadgeTone.Brand) }) {
                    QeetrixDescriptionList {
                        QeetrixDescriptionItem("Seats", "12 of 50")
                        QeetrixDescriptionItem("Renews", "12 April 2026")
                    }
                }
                QeetrixCard(variant = QeetrixCardVariant.Subdued, title = "Subdued", description = "A secondary panel")
                QeetrixCard(
                    title = "Delete workspace",
                    description = "Removes every project and member. This can't be undone.",
                    tone = QeetrixCardTone.Danger,
                    footer = { QeetrixButton("Delete workspace", onClick = {}, tone = QeetrixButtonTone.Danger) },
                )
                QeetrixCard(onClick = {}, selected = true, title = "Team", description = "Up to 50 members, audit logs")
                QeetrixCard(onClick = {}, title = "Starter", description = "Up to 5 members")
                QeetrixCard(onClick = {}, enabled = false, title = "Enterprise", description = "Contact sales")
                QeetrixCard { QeetrixText("A card without a header: the body alone.") }
            }
        }
}
