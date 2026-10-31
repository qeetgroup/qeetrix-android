package com.qeetgroup.qeetrix.components.checklist

import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.semantics.getOrNull
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createComposeRule
import com.qeetgroup.qeetrix.components.setQeetrix
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

/** CheckList: each check read as one element with its result in words. */
@RunWith(RobolectricTestRunner::class)
class QeetrixCheckListTest {
    @get:Rule
    val compose = createComposeRule()

    @Test
    fun eachCheckIsReadWithItsResultInWords() {
        compose.setQeetrix {
            QeetrixCheckList {
                QeetrixCheckItem("At least 12 characters", QeetrixCheckStatus.Passed)
                QeetrixCheckItem("The metadata URL answers", QeetrixCheckStatus.Pending, detail = "Not checked yet")
            }
        }
        val passed = compose.onNode(hasText("At least 12 characters")).fetchSemanticsNode()
        assertEquals(listOf("At least 12 characters", "Passed"), passed.config.getOrNull(SemanticsProperties.Text)?.map { it.text })
        val pending = compose.onNode(hasText("The metadata URL answers")).fetchSemanticsNode()
        assertEquals(
            listOf(
                "The metadata URL answers",
                "Not checked yet",
                "Not run",
            ),
            pending.config.getOrNull(SemanticsProperties.Text)?.map {
                it.text
            },
        )
        assertEquals(listOf("passed", "failed", "pending", "running"), QeetrixCheckStatus.entries.map { it.value })
    }
}
