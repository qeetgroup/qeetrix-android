package com.qeetgroup.qeetrix.components.stat

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import com.qeetgroup.qeetrix.components.setQeetrix
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/** Stat: numbers formatted for the locale (lakhs in en-IN), whole amounts without a fraction, the note. */
@RunWith(RobolectricTestRunner::class)
@Config(qualifiers = "en-rIN")
class QeetrixStatTest {
    @get:Rule
    val compose = createComposeRule()

    @Test
    fun valuesAreFormattedForTheLocale() {
        compose.setQeetrix {
            QeetrixStatGroup {
                QeetrixStat("Active users", value = 1240512, note = "+3.2% this week", tone = QeetrixStatTone.Positive)
                QeetrixStat("Revenue", value = 1842600, currency = "INR")
                QeetrixStat("Uptime", text = "99.95%")
            }
        }
        compose.onNodeWithText("12,40,512", useUnmergedTree = true).assertExists()
        compose.onNodeWithText("₹18,42,600", useUnmergedTree = true).assertExists()
        compose.onNodeWithText("+3.2% this week", useUnmergedTree = true).assertExists()
        assertEquals(listOf("neutral", "positive", "negative", "warning"), QeetrixStatTone.entries.map { it.value })
    }
}
