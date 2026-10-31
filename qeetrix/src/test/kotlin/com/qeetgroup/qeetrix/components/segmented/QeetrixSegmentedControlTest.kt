package com.qeetgroup.qeetrix.components.segmented

import androidx.compose.ui.test.assertIsNotSelected
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.qeetgroup.qeetrix.components.input.QeetrixOption
import com.qeetgroup.qeetrix.components.setQeetrix
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

/** Segmented control: radio buttons, the chosen one selected, a choice reported. */
@RunWith(RobolectricTestRunner::class)
class QeetrixSegmentedControlTest {
    @get:Rule
    val compose = createComposeRule()

    @Test
    fun choosingASegmentReportsIt() {
        var value = "light"
        compose.setQeetrix {
            QeetrixSegmentedControl(value, { value = it }, listOf(QeetrixOption("light", "Light"), QeetrixOption("dark", "Dark")))
        }
        compose.onNodeWithText("Light").assertIsSelected()
        compose.onNodeWithText("Dark").assertIsNotSelected().performClick()
        assertEquals("dark", value)
    }
}
