package com.qeetgroup.qeetrix.foundation.typography

import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.getBoundsInRoot
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.text.font.FontVariation
import androidx.compose.ui.text.font.ResourceFont
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import com.qeetgroup.qeetrix.R
import com.qeetgroup.qeetrix.TestSupport
import com.qeetgroup.qeetrix.theme.QeetrixTheme
import com.qeetgroup.qeetrix.theme.QeetrixThemeMode
import org.json.JSONObject
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.GraphicsMode

@RunWith(RobolectricTestRunner::class)
class TypographyTest {
    @get:Rule
    val compose = createComposeRule()

    private val typography = QeetrixTypography.Default

    @Test
    fun everyUpstreamRoleHasAStyle() {
        val upstreamRoles =
            TestSupport.tokenValues
                .keys()
                .asSequence()
                .filter { it.startsWith("typography.") }
                .map { it.split('.')[1] }
                .distinct()
                .toList()
        assertEquals(upstreamRoles, QeetrixTextRole.entries.map { it.value })
    }

    @Test
    fun stylesCarryTheTokenValues() {
        for (role in QeetrixTextRole.entries) {
            val style = typography[role]

            fun token(field: String) = TestSupport.value("typography.${role.value}.$field")
            assertEquals(role.value, ((token("font-size") as JSONObject).getDouble("dp")).toFloat().sp, style.fontSize)
            assertEquals(role.value, (token("font-weight") as Number).toInt(), style.fontWeight!!.weight)
            assertEquals(role.value, (token("line-height") as Number).toFloat().em, style.lineHeight)
            assertEquals(role.value, ((token("letter-spacing") as JSONObject).getDouble("em")).toFloat().em, style.letterSpacing)
            assertEquals(LineHeightStyle(LineHeightStyle.Alignment.Center, LineHeightStyle.Trim.None), style.lineHeightStyle)
        }
        // Spot values, as the web sets them: label-compact is 0.8rem, body 14px / 1.5.
        assertEquals(12.8.sp, typography.labelCompact.fontSize)
        assertEquals(1.5.em, typography.body.lineHeight)
    }

    @Test
    fun fontsSetTheWebsVariationAxes() {
        val label = QeetrixFonts.family(QeetrixTextRole.Label.spec)
        val fonts = (label as androidx.compose.ui.text.font.FontListFontFamily).fonts.map { it as ResourceFont }
        assertEquals(listOf(400, 500, 600, 700), fonts.map { it.weight.weight })
        for (font in fonts) {
            assertEquals(R.font.qx_cal_sans_vf, font.resId)
            val settings = font.variationSettings.settings.associateBy { it.axisName }
            assertEquals(font.weight.weight.toFloat(), settings.getValue("wght").toVariationValue(null))
            assertEquals(QeetrixFontGeometry.Ui, settings.getValue("GEOM").toVariationValue(null))
            assertEquals(0f, settings.getValue("ital").toVariationValue(null))
            // opsz follows the rendered size: the role's sp times the font scale.
            assertEquals(13f * 1.5f, settings.getValue("opsz").toVariationValue(Density(2f, fontScale = 1.5f)))
        }
        // Headings use the base letter style; code uses Fira Code, which has no GEOM or opsz.
        val heading =
            QeetrixFonts.settings(QeetrixTextRole.Heading.spec.face, QeetrixTextRole.Heading.spec, 600).associateBy {
                it.axisName
            }
        assertEquals(QeetrixFontGeometry.Base, heading.getValue("GEOM").toVariationValue(null))
        val code = QeetrixFonts.settings(QeetrixTextRole.Code.spec.face, QeetrixTextRole.Code.spec, 400).map { it.axisName }
        assertEquals(listOf("wght"), code)
        assertTrue(QeetrixTextRole.Code.spec.face.resource == R.font.qx_fira_code_vf)
    }

    @Test
    fun theWeightSettingIsClampedToTheFontsAxis() {
        val tooLight =
            QeetrixFonts.settings(QeetrixTextRole.Body.spec.face, QeetrixTextRole.Body.spec, 300).single {
                it.axisName == "wght"
            }
        assertEquals(400f, tooLight.toVariationValue(null))
    }

    @Test
    fun theBundledFontsArePackagedAsResources() {
        val resources = RuntimeEnvironment.getApplication().resources
        for (font in listOf(R.font.qx_cal_sans_vf, R.font.qx_fira_code_vf)) {
            val header = resources.openRawResource(font).use { stream -> ByteArray(4).also { stream.read(it) } }
            // TrueType files start with 0x00010000; WOFF2 ("wOF2") would not load on Android.
            assertEquals(listOf<Byte>(0, 1, 0, 0), header.toList())
        }
        assertNotNull(resources.getFont(R.font.qx_cal_sans_vf))
    }

    /** Real text layout (Robolectric's native graphics), with the bundled Qeet Sans. */
    @Test
    @GraphicsMode(GraphicsMode.Mode.NATIVE)
    fun textGrowsWithTheFontScale() {
        var fontScale by mutableFloatStateOf(1f)
        compose.setContent {
            CompositionLocalProvider(LocalDensity provides Density(1f, fontScale)) {
                QeetrixTheme(QeetrixThemeMode.Light) {
                    BasicText("Qeetrix", Modifier.testTag("text"), style = QeetrixTheme.typography.body)
                }
            }
        }

        fun height(): Float = compose.onNodeWithTag("text").getBoundsInRoot().let { (it.bottom - it.top).value }
        val normal = height()
        // Line height is 1.5em of 14sp: 21 at a 1.0 scale.
        assertEquals(21f, normal, 0.5f)
        fontScale = 2f
        compose.waitForIdle()
        // Android 14+ scales large text non-linearly (14sp at 2.0 is 26px, not 28): the size follows
        // the platform's converter and the line height stays 1.5em of whatever size that gives.
        val scaledSize = with(Density(1f, 2f)) { 14.sp.toPx() }
        assertTrue(scaledSize > 14f)
        assertEquals(scaledSize * 1.5f, height(), 0.5f)
    }
}
