package com.qeetgroup.qeetrix.icons.extended

import androidx.compose.foundation.layout.Column
import androidx.compose.ui.test.junit4.createComposeRule
import com.qeetgroup.qeetrix.icons.QeetrixIcon
import com.qeetgroup.qeetrix.icons.QeetrixIconName
import com.qeetgroup.qeetrix.icons.QeetrixIconShape
import com.qeetgroup.qeetrix.icons.QeetrixIconVariant
import com.qeetgroup.qeetrix.theme.QeetrixTheme
import org.json.JSONArray
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import java.io.File

@RunWith(RobolectricTestRunner::class)
class ExtendedCatalogueTest {
    @get:Rule
    val compose = createComposeRule()

    private val root = File(checkNotNull(System.getProperty("qeetrix.root")) { "qeetrix.root is not set" })

    /** Every icon `@qeetrix/icons` publishes, as the snapshot holds them. */
    private val upstream =
        JSONArray(File(root, "Upstream/qeetrix-icons/icons.json").readText()).let { a ->
            (0 until a.length()).map(a::getJSONObject).associateBy { it.getString("name") }
        }

    @Test
    fun theCoreAndTheExtendedCatalogueAreTheWholePackageOnce() {
        val core = QeetrixIconName.catalogue.map { it.value }
        val extended = QeetrixIconName.extendedCatalogue.map { it.value }
        assertEquals(extended.sorted(), extended)
        assertTrue("core and extended overlap", core.intersect(extended.toSet()).isEmpty())
        assertEquals(upstream.keys.sorted(), (core + extended).sorted())
        // @qeetrix/icons 2.1.2: 1,863 icons in outline, 775 of them filled too.
        assertEquals(1863, core.size + extended.size)
        val filled =
            (QeetrixIconName.catalogue + QeetrixIconName.extendedCatalogue).count {
                it.hasDrawing(QeetrixIconShape.Round, QeetrixIconVariant.Filled)
            }
        assertEquals(775, filled)
    }

    @Test
    fun drawingsAndMirroringFollowTheManifest() {
        for (icon in QeetrixIconName.extendedCatalogue) {
            val entry = upstream.getValue(icon.value)
            val shapes = entry.getJSONArray("shapes").let { a -> (0 until a.length()).map(a::getString) }
            val variants = entry.getJSONArray("variants").let { a -> (0 until a.length()).map(a::getString) }
            for (shape in QeetrixIconShape.entries) {
                for (variant in QeetrixIconVariant.entries) {
                    assertEquals(
                        "$icon $shape $variant",
                        shape.value in shapes && variant.value in variants,
                        icon.hasDrawing(shape, variant),
                    )
                }
            }
            assertEquals(icon.value, entry.getString("directionality") == "mirror", icon.mirrorsInRightToLeft)
        }
    }

    @Test
    fun namesReadAsTheirUpstreamName() {
        assertEquals("alarm-clock", QeetrixIconName.AlarmClock.value)
        assertEquals(QeetrixIconName.AlarmClock, QeetrixIconName.extendedCatalogue.single { it.value == "alarm-clock" })
        assertFalse(QeetrixIconName.AlarmClock.mirrorsInRightToLeft)
        // A directional icon outside the core set mirrors, as on the web.
        assertTrue(QeetrixIconName.extendedCatalogue.single { it.value == "log-out" }.mirrorsInRightToLeft)
    }

    /** Draws every drawing once, so each decodes: a malformed one would throw here, not in an app. */
    @Test
    fun everyDrawingDraws() {
        compose.setContent {
            QeetrixTheme {
                Column {
                    for (icon in QeetrixIconName.extendedCatalogue) {
                        for (shape in QeetrixIconShape.entries) {
                            for (variant in QeetrixIconVariant.entries) {
                                if (icon.hasDrawing(
                                        shape,
                                        variant,
                                    )
                                ) {
                                    QeetrixIcon(icon, contentDescription = null, shape = shape, variant = variant)
                                }
                            }
                        }
                    }
                }
            }
        }
        compose.waitForIdle()
    }
}
