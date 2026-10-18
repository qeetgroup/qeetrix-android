package com.qeetgroup.qeetrix.theme

import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.test.junit4.createComposeRule
import com.qeetgroup.qeetrix.foundation.color.QeetrixColors
import com.qeetgroup.qeetrix.tokens.generated.QeetrixDarkColors
import com.qeetgroup.qeetrix.tokens.generated.QeetrixLightColors
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNull
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
class ThemeTest {
    @get:Rule
    val compose = createComposeRule()

    private class Seen {
        var colors: QeetrixColors? = null
        var dark: Boolean? = null
        var content: Color? = null
    }

    private fun capture(mode: QeetrixThemeMode): Seen {
        val seen = Seen()
        compose.setContent {
            QeetrixTheme(mode) {
                seen.colors = QeetrixTheme.colors
                seen.dark = QeetrixTheme.isDark
                seen.content = LocalQeetrixContentColor.current
            }
        }
        compose.waitForIdle()
        return seen
    }

    @Test
    fun lightModeSelectsTheLightTokens() {
        val seen = capture(QeetrixThemeMode.Light)
        assertEquals(QeetrixLightColors, seen.colors)
        assertEquals(false, seen.dark)
        assertEquals(QeetrixLightColors.textPrimary, seen.content)
    }

    @Test
    @Config(qualifiers = "notnight")
    fun darkModeIgnoresALightSystemSetting() {
        val seen = capture(QeetrixThemeMode.Dark)
        assertEquals(QeetrixDarkColors, seen.colors)
        assertEquals(true, seen.dark)
    }

    @Test
    @Config(qualifiers = "night")
    fun systemModeFollowsANightConfiguration() {
        assertEquals(QeetrixDarkColors, capture(QeetrixThemeMode.System).colors)
    }

    @Test
    @Config(qualifiers = "notnight")
    fun systemModeFollowsADayConfiguration() {
        assertEquals(QeetrixLightColors, capture(QeetrixThemeMode.System).colors)
    }

    @Test
    fun aNestedThemeOverridesOnlyItsSubtree() {
        var outer: QeetrixColors? = null
        var inner: QeetrixColors? = null
        var after: QeetrixColors? = null
        compose.setContent {
            QeetrixTheme(QeetrixThemeMode.Light) {
                outer = QeetrixTheme.colors
                QeetrixTheme(QeetrixThemeMode.Dark) { inner = QeetrixTheme.colors }
                after = QeetrixTheme.colors
            }
        }
        compose.waitForIdle()
        assertEquals(QeetrixLightColors, outer)
        assertEquals(QeetrixDarkColors, inner)
        assertEquals(QeetrixLightColors, after)
    }

    @Test
    fun aModeChangeRecomposesWithTheNewTheme() {
        var mode by mutableStateOf(QeetrixThemeMode.Light)
        var seen: QeetrixColors? = null
        compose.setContent { QeetrixTheme(mode) { seen = QeetrixTheme.colors } }
        compose.waitForIdle()
        assertEquals(QeetrixLightColors, seen)
        mode = QeetrixThemeMode.Dark
        compose.waitForIdle()
        assertEquals(QeetrixDarkColors, seen)
    }

    @Test
    fun textSelectionUsesTheSelectionTokens() {
        var selection: Color? = null
        compose.setContent { QeetrixTheme(QeetrixThemeMode.Light) { selection = LocalTextSelectionColors.current.backgroundColor } }
        compose.waitForIdle()
        assertEquals(QeetrixLightColors.selectionBackground, selection)
    }

    @Test
    fun modesUseTheWebsValues() {
        assertEquals(listOf("light", "dark", "system"), QeetrixThemeMode.entries.map { it.value })
        assertEquals(QeetrixThemeMode.System, QeetrixThemeMode.fromValue("system"))
        assertNull(QeetrixThemeMode.fromValue("sepia"))
        assertNotEquals(QeetrixLightColors, QeetrixDarkColors)
    }
}
