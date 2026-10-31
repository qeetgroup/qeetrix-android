// The examples keep the documentation's compact layout, so they can be compared with it line by line.
@file:Suppress(
    "ktlint:standard:argument-list-wrapping",
    "ktlint:standard:max-line-length",
    "ktlint:standard:no-multi-spaces",
    "ktlint:standard:trailing-comma-on-call-site",
    "ktlint:standard:wrapping",
)

package com.qeetgroup.qeetrix.sample

import android.provider.Settings
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.hasContentDescription
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createComposeRule
import com.qeetgroup.qeetrix.components.alert.QeetrixAlert
import com.qeetgroup.qeetrix.components.alert.QeetrixAlertTone
import com.qeetgroup.qeetrix.components.datepicker.QeetrixDateField
import com.qeetgroup.qeetrix.components.input.QeetrixOption
import com.qeetgroup.qeetrix.components.otpinput.QeetrixOTPCode
import com.qeetgroup.qeetrix.components.segmented.QeetrixSegmentedControl
import com.qeetgroup.qeetrix.foundation.format.QeetrixFormat
import com.qeetgroup.qeetrix.theme.QeetrixTheme
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import java.time.Duration
import java.time.Instant
import java.time.LocalDate
import java.util.Locale

/**
 * The README's "Native components" and "Formatting" examples, as written there, so they keep compiling against the
 * public API, composing and saying what they show. Only the surrounding state is added here. Values are synthetic.
 */
@RunWith(RobolectricTestRunner::class)
class NativeDocumentationExamplesTest {
    @get:Rule
    val compose = createComposeRule()

    @Before
    fun animationsOff() {
        // The OTP code's countdown steps once a second instead of every frame, so the UI goes idle.
        Settings.Global.putFloat(RuntimeEnvironment.getApplication().contentResolver, Settings.Global.ANIMATOR_DURATION_SCALE, 0f)
    }

    @Test
    fun theNativeComponentsExampleComposes() {
        compose.setContent { QeetrixTheme { Column(Modifier.verticalScroll(rememberScrollState())) { ReadmeNative() } } }
        for (text in listOf("Push isn't set up", "All", "Pending", "Acme · priya@acme.in", "Invoice date")) {
            val named = hasText(text, substring = true) or hasContentDescription(text, substring = true)
            assertTrue(text, compose.onAllNodes(named, useUnmergedTree = true).fetchSemanticsNodes().isNotEmpty())
        }
    }

    @Test
    fun theFormattingExampleSaysWhatItShows() {
        val sentAt = Instant.now().minusSeconds(120)
        // README.md, "Formatting"
        val india = Locale.forLanguageTag("en-IN")
        assertEquals("₹1,24,500.00", QeetrixFormat.currency(124500, "INR", india))
        assertEquals("2 minutes ago", QeetrixFormat.relativeTime(sentAt, india))
        assertEquals("1 hour, 5 minutes", QeetrixFormat.duration(Duration.ofMinutes(65), india))
    }
}

// README.md, "Native components"
@Composable
private fun ReadmeNative() {
    var filter by remember { mutableStateOf("all") }
    var date by remember { mutableStateOf<LocalDate?>(null) }
    val code = "482105"
    val expiresAt = Instant.now().plusSeconds(20)
    QeetrixAlert("Push isn't set up", tone = QeetrixAlertTone.Warning,
        description = "Sign-in requests won't reach this phone until you allow notifications.")
    QeetrixSegmentedControl(filter, { filter = it }, listOf(QeetrixOption("all", "All"), QeetrixOption("pending", "Pending")))
    QeetrixOTPCode(code, expiresAt, label = "Acme · priya@acme.in")   // the code, its countdown ring, tap to copy
    QeetrixDateField(date, { date = it }, label = "Invoice date", max = LocalDate.now())
}
