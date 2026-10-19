package com.qeetgroup.qeetrix.sample

import android.provider.Settings
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsFocused
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.hasClickAction
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.isHeading
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performTextClearance
import androidx.compose.ui.test.performTextInput
import com.qeetgroup.qeetrix.foundation.sizing.QeetrixDensity
import com.qeetgroup.qeetrix.sample.components.forms.FormSample
import com.qeetgroup.qeetrix.sample.composition.AccountPreferencesScreen
import com.qeetgroup.qeetrix.sample.composition.CompositionScreen
import com.qeetgroup.qeetrix.sample.foundations.FoundationsScreen
import com.qeetgroup.qeetrix.theme.QeetrixThemeMode
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment

/** The sample as a consumer of the public API: every page composes, and the composition form works. */
@RunWith(RobolectricTestRunner::class)
class SampleTest {
    @get:Rule
    val compose = createComposeRule()

    @Before
    fun animationsOff() {
        // The still, reduced-motion form of spinners and indeterminate progress, so the UI goes idle.
        Settings.Global.putFloat(RuntimeEnvironment.getApplication().contentResolver, Settings.Global.ANIMATOR_DURATION_SCALE, 0f)
    }

    @Test
    fun everyPageComposesInEveryDisplaySetting() {
        val pages: List<Pair<String, @Composable () -> Unit>> =
            SampleCatalog.map { it.id to it.content } +
                listOf(
                    "foundations" to { FoundationsScreen() },
                    "composition" to { CompositionScreen() },
                    "account-preferences" to { AccountPreferencesScreen() },
                )
        val settings =
            listOf(
                DisplaySettings(QeetrixThemeMode.Light),
                DisplaySettings(QeetrixThemeMode.Dark, QeetrixDensity.Compact, rightToLeft = true),
                DisplaySettings(QeetrixThemeMode.Light, QeetrixDensity.Comfortable, largeText = true),
            )
        var page by mutableStateOf(pages.first().second)
        var display by mutableStateOf(settings.first())
        compose.setContent {
            SampleDisplay(display) { Column(Modifier.verticalScroll(rememberScrollState())) { page() } }
        }
        for ((id, content) in pages) {
            for (setting in settings) {
                page = content
                display = setting
                compose.waitForIdle()
                assertTrue("$id in $setting has no heading", compose.onAllNodes(isHeading()).fetchSemanticsNodes().isNotEmpty())
            }
        }
    }

    @Test
    fun theCompositionFormValidatesSavesAndAnnounces() {
        compose.setContent {
            SampleDisplay(
                DisplaySettings(QeetrixThemeMode.Light),
            ) { Column(Modifier.verticalScroll(rememberScrollState())) { CompositionScreen() } }
        }
        val name = compose.onNodeWithText("Workspace name")

        name.performTextClearance()
        name.assert(SemanticsMatcher.expectValue(SemanticsProperties.Error, "Enter a name for the workspace."))
        compose.onNodeWithText("Save changes").performScrollTo().assertIsNotEnabled()

        name.performTextInput("Acme")
        name.assert(SemanticsMatcher.keyNotDefined(SemanticsProperties.Error))
        compose.mainClock.autoAdvance = false
        compose.onNodeWithText("Save changes").assertIsEnabled().performClick()
        compose.mainClock.advanceTimeBy(100) // past the tap delay inside a scrolling container
        compose.onNodeWithContentDescription("Saving…").assertExists()
        compose.onNodeWithText("Reset").assertIsNotEnabled()

        compose.mainClock.advanceTimeBy(1_600)
        compose.onNodeWithText("Saved").assert(SemanticsMatcher.expectValue(SemanticsProperties.LiveRegion, LiveRegionMode.Polite))
        compose.onNodeWithText("Save changes").assertIsEnabled()
    }

    @Test
    fun theFormSummaryTakesYouToEachField() {
        compose.setContent {
            SampleDisplay(
                DisplaySettings(QeetrixThemeMode.Light),
            ) { Column(Modifier.verticalScroll(rememberScrollState())) { FormSample() } }
        }
        compose.onNodeWithText("Save profile").performScrollTo().performClick()
        compose.onNodeWithText("There is a problem").assertExists()
        compose.onNodeWithText("Full name").assert(SemanticsMatcher.expectValue(SemanticsProperties.Error, "Enter your name."))
        // Each link moves focus to its field — a text field and a select alike.
        compose.onAllNodes(hasText("Enter your name.") and hasClickAction())[0].performScrollTo().performClick()
        compose.onNodeWithText("Full name").assertIsFocused()
        compose.onAllNodes(hasText("Choose your role.") and hasClickAction())[0].performScrollTo().performClick()
        compose.onNode(hasText("Role") and SemanticsMatcher.expectValue(SemanticsProperties.Role, Role.DropdownList)).assertIsFocused()
    }

    @Test
    fun twoStepVerificationAsksForACodeBeforeSaving() {
        compose.setContent {
            SampleDisplay(DisplaySettings(QeetrixThemeMode.Light)) {
                Column(Modifier.verticalScroll(rememberScrollState())) { AccountPreferencesScreen() }
            }
        }
        compose.onNodeWithText("Verification code").assertDoesNotExist()
        compose.onNodeWithText("Two-step verification").performScrollTo().performClick()
        compose.onNodeWithText("Save preferences").performScrollTo().assertIsNotEnabled()
        compose.onNodeWithText("Verification code").performScrollTo().performTextInput("135790")
        compose.onNodeWithText("Save preferences").performScrollTo().assertIsEnabled()
    }
}
