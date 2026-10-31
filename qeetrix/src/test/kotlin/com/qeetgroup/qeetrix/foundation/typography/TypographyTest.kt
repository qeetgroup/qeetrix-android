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
                .map { it.removePrefix("typography.") }
                .toList()
        assertEquals(upstreamRoles, QeetrixTextRole.entries.map { it.value })
    }

    @Test
    fun stylesCarryTheTokenValues() {
        for (role in QeetrixTextRole.entries) {
            val style = typography[role]

            val token = TestSupport.value("typography.${role.value}") as JSONObject
            assertEquals(
                role.value,
                token
                    .getJSONObject("fontSize")
                    .getDouble("px")
                    .toFloat()
                    .sp,
                style.fontSize,
            )
            assertEquals(role.value, token.getInt("fontWeight"), style.fontWeight!!.weight)
            // The token's line height is a length on the 4 px grid (@qeetrix/tokens 0.0.13, QTRX-184); the style's ratio
            // times the size is that length exactly, in the Float arithmetic Compose lays lines out with.
            val size =
                token
                    .getJSONObject("fontSize")
                    .getDouble("px")
                    .toFloat()
            val length =
                token
                    .getJSONObject("lineHeightLength")
                    .getDouble("px")
                    .toFloat()
            assertEquals(role.value, length, style.lineHeight.value * size, 0f)
            assertEquals(
                role.value,
                token
                    .getJSONObject("letterSpacing")
                    .getDouble("em")
                    .toFloat()
                    .em,
                style.letterSpacing,
            )
            assertEquals(LineHeightStyle(LineHeightStyle.Alignment.Center, LineHeightStyle.Trim.None), style.lineHeightStyle)
        }
        // Spot values, from @qeetrix/tokens' README: body.md 14 / 20, label.caps 11 / 16 tracked 0.66px.
        assertEquals(14.sp, typography.bodyMd.fontSize)
        assertEquals(20f, typography.bodyMd.lineHeight.value * 14f, 0f)
        assertEquals(0.06f, typography.labelCaps.letterSpacing.value, 1e-4f)
        assertEquals(18, QeetrixTextRole.entries.size)
    }

    @Test
    fun fontsSetTheWebsVariationAxes() {
        val label = QeetrixFonts.family(QeetrixTextRole.LabelMd.spec)
        val fonts = (label as androidx.compose.ui.text.font.FontListFontFamily).fonts.map { it as ResourceFont }
        assertEquals(listOf(400, 500, 600, 700), fonts.map { it.weight.weight })
        for (font in fonts) {
            assertEquals(R.font.qx_qeet_sans_vf, font.resId)
            val settings = font.variationSettings.settings.associateBy { it.axisName }
            assertEquals(font.weight.weight.toFloat(), settings.getValue("wght").toVariationValue(null))
            // label roles take A11y letterforms: font.geometry.body, 0 (design rules §5).
            assertEquals(QeetrixFontGeometry.Body, settings.getValue("GEOM").toVariationValue(null))
            assertEquals(0f, QeetrixFontGeometry.Body)
            assertEquals(0f, settings.getValue("ital").toVariationValue(null))
            // opsz is the rendered size, a plain number: 14 at a font scale of 1.
            assertEquals(14f, settings.getValue("opsz").toVariationValue(null))
        }
        // Headings and titles use Base, display Geo; code uses Fira Code, which has no GEOM or opsz.
        val heading =
            QeetrixFonts.settings(QeetrixTextRole.HeadingSm.spec.face, QeetrixTextRole.HeadingSm.spec, 600).associateBy {
                it.axisName
            }
        assertEquals(QeetrixFontGeometry.Heading, heading.getValue("GEOM").toVariationValue(null))
        assertEquals(50f, QeetrixFontGeometry.Heading)
        val display =
            QeetrixFonts
                .settings(
                    QeetrixTextRole.DisplayLg.spec.face,
                    QeetrixTextRole.DisplayLg.spec,
                    600,
                ).associateBy { it.axisName }
        assertEquals(100f, display.getValue("GEOM").toVariationValue(null))
        val code = QeetrixFonts.settings(QeetrixTextRole.CodeMd.spec.face, QeetrixTextRole.CodeMd.spec, 400).map { it.axisName }
        assertEquals(listOf("wght"), code)
        assertTrue(QeetrixTextRole.CodeMd.spec.face.resource == R.font.qx_fira_code_vf)
    }

    /**
     * Compose keeps a loaded typeface for the whole process, keyed by its Font. An opsz in sp took the font scale of
     * whichever context loaded the font first, so text at one scale could keep another's optical size (it made
     * screenshots depend on the order tests ran in). Each scale is now its own Font.
     */
    @Test
    fun eachFontScaleHasItsOwnOpticalSize() {
        fun opsz(style: androidx.compose.ui.text.TextStyle): Float =
            (style.fontFamily as androidx.compose.ui.text.font.FontListFontFamily)
                .fonts
                .map { (it as ResourceFont).variationSettings.settings.single { setting -> setting.axisName == "opsz" } }
                .map { it.toVariationValue(null) }
                .distinct()
                .single()
        assertTrue(QeetrixTypography.at(Density(2f, fontScale = 1f)) === QeetrixTypography.Default)
        val scaled = Density(2f, fontScale = 1.5f)
        val larger = QeetrixTypography.at(scaled)
        assertEquals(14f, opsz(QeetrixTypography.Default.labelMd))
        // The size the text is drawn at, through Android 14's non-linear curve: 14sp at 1.5 is 22dp, not 21.
        assertEquals(22f, with(scaled) { 14.sp.toDp().value }, 0.01f)
        assertEquals(with(scaled) { 14.sp.toDp().value }, opsz(larger.labelMd))
        assertTrue(larger.labelMd.fontFamily != QeetrixTypography.Default.labelMd.fontFamily)
        // Clamped to the file's axis (8–45): display.lg at a large scale stops at the top.
        assertEquals(45f, opsz(QeetrixTypography.at(Density(1f, fontScale = 2f)).displayLg))

        // The theme builds its typography for the composition's font scale.
        var themed: androidx.compose.ui.text.TextStyle? = null
        compose.setContent {
            CompositionLocalProvider(LocalDensity provides Density(1f, fontScale = 2f)) {
                QeetrixTheme(QeetrixThemeMode.Light) { themed = QeetrixTheme.typography.bodyMd }
            }
        }
        compose.waitForIdle()
        assertEquals(with(Density(1f, fontScale = 2f)) { 14.sp.toDp().value }, opsz(themed!!))
    }

    @Test
    fun theWeightSettingIsClampedToTheFontsAxis() {
        val tooLight =
            QeetrixFonts.settings(QeetrixTextRole.BodyMd.spec.face, QeetrixTextRole.BodyMd.spec, 300).single {
                it.axisName == "wght"
            }
        assertEquals(400f, tooLight.toVariationValue(null))
    }

    @Test
    fun theBundledFontsArePackagedAsResources() {
        val resources = RuntimeEnvironment.getApplication().resources
        for (font in listOf(R.font.qx_qeet_sans_vf, R.font.qx_fira_code_vf)) {
            val header = resources.openRawResource(font).use { stream -> ByteArray(4).also { stream.read(it) } }
            // TrueType files start with 0x00010000; WOFF2 ("wOF2") would not load on Android.
            assertEquals(listOf<Byte>(0, 1, 0, 0), header.toList())
        }
        assertNotNull(resources.getFont(R.font.qx_qeet_sans_vf))
    }

    /** Real text layout (Robolectric's native graphics), with the bundled Qeet Sans. */
    @Test
    @GraphicsMode(GraphicsMode.Mode.NATIVE)
    fun textGrowsWithTheFontScale() {
        var fontScale by mutableFloatStateOf(1f)
        compose.setContent {
            CompositionLocalProvider(LocalDensity provides Density(1f, fontScale)) {
                QeetrixTheme(QeetrixThemeMode.Light) {
                    BasicText("Qeetrix", Modifier.testTag("text"), style = QeetrixTheme.typography.bodyLg)
                }
            }
        }

        fun height(): Float = compose.onNodeWithTag("text").getBoundsInRoot().let { (it.bottom - it.top).value }
        val normal = height()
        // body.lg's line height is 1.5em of 16sp: 24 at a 1.0 scale.
        assertEquals(24f, normal, 0.5f)
        fontScale = 2f
        compose.waitForIdle()
        // Android 14+ scales large text non-linearly (14sp at 2.0 is 26px, not 28): the size follows
        // the platform's converter and the line height stays 1.5em of whatever size that gives.
        val scaledSize = with(Density(1f, 2f)) { 16.sp.toPx() }
        assertTrue(scaledSize > 16f)
        assertEquals(scaledSize * 1.5f, height(), 0.5f)
    }
}
