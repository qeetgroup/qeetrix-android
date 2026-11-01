package com.qeetgroup.qeetrix.components.qrcode

import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import com.qeetgroup.qeetrix.components.setQeetrix
import org.json.JSONObject
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

/** The encoder against the web's: `qr-cases.json` holds @qeetrix/react's own modules for each value and level. */
@RunWith(RobolectricTestRunner::class)
class QeetrixQREncoderTest {
    @get:Rule
    val compose = createComposeRule()

    @Test
    fun everyCaseHasTheWebsModules() {
        val text = javaClass.classLoader!!.getResource("qeetrix/qr-cases.json")!!.readText()
        val cases = JSONObject(text).getJSONArray("cases")
        for (index in 0 until cases.length()) {
            val case = cases.getJSONObject(index)
            val level = QeetrixQRCodeLevel.entries.single { it.value == case.getString("level") }
            val modules = QeetrixQREncoder.encode(case.getString("value"), level)
            val rows = case.getJSONArray("rows")
            val want = (0 until rows.length()).map { rows.getString(it) }
            val got = modules.map { row -> row.joinToString("") { if (it) "1" else "0" } }
            assertEquals("${case.getString("value").take(40)} at $level", want, got)
        }
    }

    @Test
    fun tooLongAValueIsRejected() {
        assertThrows(IllegalArgumentException::class.java) { QeetrixQREncoder.encode("x".repeat(3000), QeetrixQRCodeLevel.H) }
        assertEquals(listOf("L", "M", "Q", "H"), QeetrixQRCodeLevel.entries.map { it.value })
    }

    @Test
    fun theCodeIsAnImageNamedByItsLabel() {
        compose.setQeetrix { QeetrixQRCode("https://qeet.in", label = "QR code to open qeet.in") }
        compose
            .onNodeWithContentDescription("QR code to open qeet.in")
            .assert(SemanticsMatcher.expectValue(SemanticsProperties.Role, Role.Image))
    }
}
