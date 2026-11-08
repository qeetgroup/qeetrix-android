package com.qeetgroup.qeetrix.icons

import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertHeightIsEqualTo
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import com.qeetgroup.qeetrix.TestSupport
import com.qeetgroup.qeetrix.foundation.sizing.QeetrixSize
import com.qeetgroup.qeetrix.theme.QeetrixTheme
import com.qeetgroup.qeetrix.theme.QeetrixThemeMode
import org.json.JSONArray
import org.json.JSONObject
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import java.io.File

@RunWith(RobolectricTestRunner::class)
class IconTest {
    @get:Rule
    val compose = createComposeRule()

    private val pins =
        JSONObject(File(TestSupport.root, "Upstream/manifest.json").readText())
            .getJSONObject("qeetrixIcons")
            .getJSONArray("icons")
            .let { a -> (0 until a.length()).map(a::getString) }

    // The snapshot holds every icon in the package; the core catalogue is the pinned ones.
    private val manifest =
        JSONArray(File(TestSupport.root, "Upstream/qeetrix-icons/icons.json").readText()).let { all ->
            JSONArray((0 until all.length()).map(all::getJSONObject).filter { it.getString("name") in pins })
        }

    @Test
    fun theCatalogueIsThePinnedSetWithUpstreamNames() {
        assertEquals(pins.sorted(), QeetrixIconName.catalogue.map { it.value })
        assertEquals(100, QeetrixIconName.catalogue.size)
        assertEquals(pins.size, manifest.length())
    }

    @Test
    fun namesAreEqualByValue() {
        assertEquals(QeetrixIconName.Send, QeetrixIconName.catalogue.single { it.value == "send" })
        assertEquals(QeetrixIconName.Send.hashCode(), "send".hashCode())
        assertEquals("send", QeetrixIconName.Send.toString())
        assertFalse(QeetrixIconName.Send == QeetrixIconName.Search)
    }

    @Test
    fun drawingsExistExactlyWhereUpstreamDrawsThem() {
        for (index in 0 until manifest.length()) {
            val entry = manifest.getJSONObject(index)
            val name = QeetrixIconName.catalogue.single { it.value == entry.getString("name") }
            val shapes = entry.getJSONArray("shapes").let { a -> (0 until a.length()).map(a::getString) }
            val variants = entry.getJSONArray("variants").let { a -> (0 until a.length()).map(a::getString) }
            for (shape in QeetrixIconShape.entries) {
                for (variant in QeetrixIconVariant.entries) {
                    assertEquals(
                        "$name $shape $variant",
                        shape.value in shapes && variant.value in variants,
                        name.hasDrawing(shape, variant),
                    )
                }
            }
            // The catalogue's mirroring only: arrows and chevrons are "preserve", and the place they're used decides.
            assertEquals(name.value, entry.getString("directionality") == "mirror", name.mirrorsInRightToLeft)
        }
        assertTrue(QeetrixIconName.Send.mirrorsInRightToLeft)
        assertFalse(QeetrixIconName.ChevronRight.mirrorsInRightToLeft)
        assertFalse(QeetrixIconName.ChevronDown.mirrorsInRightToLeft)
        assertFalse(QeetrixIconName.Search.mirrorsInRightToLeft)
    }

    /** The native Qeet ID apps draw these, sharp on controls, so the pin must keep them. */
    @Test
    fun theAuthenticatorIconsHaveTheirSharpOutline() {
        val authenticator =
            listOf(
                "key",
                "shield-check",
                "shield-x",
                "scan-line",
                "link",
                "lock-open",
                "timer",
                "hourglass",
                "bell-off",
                "wifi-off",
                "fingerprint-pattern",
                "arrow-right",
                "external-link",
                "scan-face",
                "scan-qr-code",
                "rectangle-ellipsis",
            )
        for (value in authenticator) {
            val name = QeetrixIconName.catalogue.singleOrNull { it.value == value }
            assertTrue(
                "$value is not in the catalogue",
                name != null && name.hasDrawing(QeetrixIconShape.Sharp, QeetrixIconVariant.Outline),
            )
        }
    }

    /** The Qeet ID screens draw these, sharp on controls; none mirrors, as on iOS. */
    @Test
    fun theQeetIdScreensIconsHaveTheirSharpOutlineAndDoNotMirror() {
        val screens =
            listOf(
                "qr-code",
                "clipboard-paste",
                "arrow-left-right",
                "bell-ring",
                "bot-off",
                "download",
                "globe",
                "laptop",
                "monitor",
                "rotate-ccw",
                "shield-alert",
                "sparkles",
            )
        for (value in screens) {
            val name = QeetrixIconName.catalogue.singleOrNull { it.value == value }
            assertTrue(
                "$value is not in the catalogue",
                name != null && name.hasDrawing(QeetrixIconShape.Sharp, QeetrixIconVariant.Outline),
            )
            // A two-way arrow reads the same either way: upstream marks arrow-left-right "preserve".
            assertFalse("$value mirrors", name!!.mirrorsInRightToLeft)
        }
    }

    @Test
    fun aMissingVariantFallsBackToTheOutline() {
        assertFalse(QeetrixIconName.Check.hasDrawing(QeetrixIconShape.Round, QeetrixIconVariant.Filled))
        assertSame(
            QeetrixIconName.Check.resolvedDrawing(QeetrixIconShape.Round, QeetrixIconVariant.Outline),
            QeetrixIconName.Check.resolvedDrawing(QeetrixIconShape.Round, QeetrixIconVariant.Filled),
        )
    }

    @Test
    fun vectorsAre24UnitGridsThatMirrorWhenTheIconDoes() {
        val send =
            QeetrixIconName.Send
                .resolvedDrawing(
                    QeetrixIconShape.Round,
                    QeetrixIconVariant.Outline,
                ).toImageVector(2f, mirrors = true)
        assertTrue(send.autoMirror)
        assertEquals(24f, send.viewportWidth)
        val search =
            QeetrixIconName.Search
                .resolvedDrawing(
                    QeetrixIconShape.Sharp,
                    QeetrixIconVariant.Filled,
                ).toImageVector(1.5f, mirrors = false)
        assertFalse(search.autoMirror)
    }

    @Test
    fun aDecorativeIconIsHiddenFromAccessibilityServices() {
        compose.setContent {
            QeetrixTheme(QeetrixThemeMode.Light) {
                QeetrixIcon(QeetrixIconName.Search, contentDescription = null, modifier = Modifier.testTag("icon"))
            }
        }
        compose
            .onNodeWithTag("icon")
            .assert(SemanticsMatcher.keyNotDefined(SemanticsProperties.ContentDescription))
            .assert(SemanticsMatcher.keyNotDefined(SemanticsProperties.Role))
    }

    @Test
    fun aMeaningfulIconIsAnImageWithItsLabel() {
        compose.setContent {
            QeetrixTheme(QeetrixThemeMode.Light) {
                QeetrixIcon(QeetrixIconName.Lock, contentDescription = "Locked", size = QeetrixSize.IconLg)
            }
        }
        compose
            .onNodeWithContentDescription("Locked")
            .assert(SemanticsMatcher.expectValue(SemanticsProperties.Role, Role.Image))
            .assertHeightIsEqualTo(QeetrixSize.IconLg)
    }
}
