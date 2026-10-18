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
import com.qeetgroup.qeetrix.foundation.sizing.QeetrixIconSize
import com.qeetgroup.qeetrix.theme.QeetrixTheme
import com.qeetgroup.qeetrix.theme.QeetrixThemeMode
import org.json.JSONArray
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

    private val manifest = JSONArray(File(TestSupport.root, "Upstream/qeetrix-icons/icons.json").readText())

    @Test
    fun theCatalogueIsThePinnedSetWithUpstreamNames() {
        val upstream = (0 until manifest.length()).map { manifest.getJSONObject(it).getString("name") }
        assertEquals(upstream, QeetrixIconName.entries.map { it.value })
    }

    @Test
    fun drawingsExistExactlyWhereUpstreamDrawsThem() {
        for (index in 0 until manifest.length()) {
            val entry = manifest.getJSONObject(index)
            val name = QeetrixIconName.entries.single { it.value == entry.getString("name") }
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
            assertEquals(entry.getString("directionality") == "mirror", name.mirrorsInRightToLeft)
        }
        assertTrue(QeetrixIconName.Send.mirrorsInRightToLeft)
        assertFalse(QeetrixIconName.Search.mirrorsInRightToLeft)
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
                QeetrixIcon(QeetrixIconName.Lock, contentDescription = "Locked", size = QeetrixIconSize.Lg)
            }
        }
        compose
            .onNodeWithContentDescription("Locked")
            .assert(SemanticsMatcher.expectValue(SemanticsProperties.Role, Role.Image))
            .assertHeightIsEqualTo(QeetrixIconSize.Lg)
    }
}
