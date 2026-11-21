package com.qeetgroup.qeetrix.theme

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.qeetgroup.qeetrix.R
import com.qeetgroup.qeetrix.TestSupport
import com.qeetgroup.qeetrix.foundation.color.QeetrixColors
import com.qeetgroup.qeetrix.tokens.generated.QeetrixDarkColors
import com.qeetgroup.qeetrix.tokens.generated.QeetrixLightColors
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config

/**
 * The colour roles published as Android resources for an app's XML themes (window background, splash screen): each
 * equals `QeetrixTheme.colors` in its scheme — light in `values`, dark in `values-night` — and the token report lists them.
 */
@RunWith(RobolectricTestRunner::class)
class ColorResourceTest {
    private val roles: List<Pair<Int, (QeetrixColors) -> Color>> =
        listOf(
            R.color.qx_surface_canvas to QeetrixColors::surfaceCanvas,
            R.color.qx_surface_default to QeetrixColors::surfaceDefault,
            R.color.qx_surface_raised to QeetrixColors::surfaceRaised,
            R.color.qx_text_default to QeetrixColors::textDefault,
            R.color.qx_bg_brand_bold to QeetrixColors::bgBrandBold,
        )

    private fun check(scheme: QeetrixColors) {
        val context = RuntimeEnvironment.getApplication()
        for ((resource, role) in roles) {
            assertEquals(context.resources.getResourceEntryName(resource), role(scheme).toArgb(), context.getColor(resource))
        }
    }

    @Test
    fun theLightResourcesAreTheLightScheme() = check(QeetrixLightColors)

    @Test
    @Config(qualifiers = "night")
    fun theNightResourcesAreTheDarkScheme() = check(QeetrixDarkColors)

    @Test
    fun theTokenReportListsThem() {
        val report = java.io.File(TestSupport.root, "docs/tokens.md").readText()
        for (name in listOf("qx_surface_canvas", "qx_surface_default", "qx_surface_raised", "qx_text_default", "qx_bg_brand_bold")) {
            assertTrue(name, report.contains("`@color/$name`"))
        }
    }
}
