package com.qeetgroup.qeetrix.components.badge

import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.hasClickAction
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import com.qeetgroup.qeetrix.components.setQeetrix
import com.qeetgroup.qeetrix.icons.QeetrixIconName
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class QeetrixBadgeTest {
    @get:Rule
    val compose = createComposeRule()

    @Test
    fun theTextCarriesTheMeaningAndTheIconIsDecorative() {
        compose.setQeetrix { QeetrixBadge("Failed", variant = QeetrixBadgeVariant.Destructive, icon = QeetrixIconName.CircleAlert) }
        compose.onNodeWithText("Failed").assert(!hasClickAction())
        compose
            .onAllNodes(
                SemanticsMatcher.keyIsDefined(SemanticsProperties.ContentDescription),
                useUnmergedTree = true,
            ).assertCountEquals(0)
    }

    @Test
    fun variantsAreTheWebs() {
        assertEquals(
            listOf("default", "secondary", "outline", "brand", "info", "success", "warning", "destructive", "muted"),
            QeetrixBadgeVariant.entries.map { it.value },
        )
    }
}
