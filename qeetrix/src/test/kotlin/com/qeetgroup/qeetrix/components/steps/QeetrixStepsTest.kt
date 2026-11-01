package com.qeetgroup.qeetrix.components.steps

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import com.qeetgroup.qeetrix.components.setQeetrix
import com.qeetgroup.qeetrix.theme.QeetrixTheme
import com.qeetgroup.qeetrix.theme.QeetrixThemeMode
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.GraphicsMode

/** Steps: each read with its state and position; done steps go back; compact where narrow. */
@RunWith(RobolectricTestRunner::class)
class QeetrixStepsTest {
    @get:Rule
    val compose = createComposeRule()

    private val steps = listOf(QeetrixStep("Account"), QeetrixStep("Verify"), QeetrixStep("Workspace"), QeetrixStep("Done"))

    @Test
    fun eachStepIsReadWithItsStateAndPosition() {
        var chosen = -1
        compose.setQeetrix { QeetrixSteps(steps, current = 1, onStepSelect = { chosen = it }) }
        compose
            .onNodeWithContentDescription(
                "Verify",
            ).assert(SemanticsMatcher.expectValue(SemanticsProperties.StateDescription, "Current step, 2 of 4"))
        compose
            .onNodeWithContentDescription(
                "Workspace",
            ).assert(SemanticsMatcher.expectValue(SemanticsProperties.StateDescription, "Not started, 3 of 4"))
        compose
            .onNodeWithContentDescription(
                "Account",
            ).assert(SemanticsMatcher.expectValue(SemanticsProperties.StateDescription, "Completed, 1 of 4"))
            .performClick()
        assertEquals(0, chosen)
    }

    @Test
    fun narrowStepsAreCompact() {
        compose.setQeetrix { Box(Modifier.width(200.dp)) { QeetrixSteps(steps, current = 1) } }
        compose.onNodeWithText("Step 2 of 4", useUnmergedTree = true).assertExists()
        assertEquals(listOf("done", "current", "upcoming", "failed", "attention"), QeetrixStepStatus.entries.map { it.value })
    }

    /** Found on a device: at 200 % text each column was wide enough by the 72 dp rule, but "Account" broke mid-word. */
    @Test
    @GraphicsMode(GraphicsMode.Mode.NATIVE) // real text measurement, with the bundled Qeet Sans
    fun aLabelThatWouldBreakMidWordMakesThemCompact() {
        val setup = listOf(QeetrixStep("Account"), QeetrixStep("Verify"), QeetrixStep("Link phone"), QeetrixStep("Done"))
        compose.setContent {
            CompositionLocalProvider(LocalDensity provides Density(LocalDensity.current.density, fontScale = 2f)) {
                QeetrixTheme(QeetrixThemeMode.Light) { Box(Modifier.width(320.dp)) { QeetrixSteps(setup, current = 1) } }
            }
        }
        compose.onNodeWithText("Step 2 of 4", useUnmergedTree = true).assertExists()
    }

    @Test
    @GraphicsMode(GraphicsMode.Mode.NATIVE)
    fun labelsThatFitStayInColumns() {
        val setup = listOf(QeetrixStep("Account"), QeetrixStep("Verify"), QeetrixStep("Link phone"), QeetrixStep("Done"))
        compose.setQeetrix { Box(Modifier.width(320.dp)) { QeetrixSteps(setup, current = 1) } }
        compose.onNodeWithText("Step 2 of 4", useUnmergedTree = true).assertDoesNotExist()
        compose.onNodeWithText("Account", useUnmergedTree = true).assertExists()
    }
}
