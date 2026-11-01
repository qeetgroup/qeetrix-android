package com.qeetgroup.qeetrix

import android.os.Build
import android.view.View
import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.unit.dp
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.qeetgroup.qeetrix.components.navigation.QeetrixNavigationItem
import com.qeetgroup.qeetrix.components.navigation.QeetrixNavigationLayout
import com.qeetgroup.qeetrix.components.navigation.QeetrixNavigationScaffold
import com.qeetgroup.qeetrix.icons.QeetrixIconName
import com.qeetgroup.qeetrix.theme.QeetrixTheme
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * The adaptive scaffold in an edge-to-edge window, which the JVM tests do not have: a screen inside it that keeps clear
 * of the system bars (as QeetrixTopAppBar does by default) still does, beside the rail and above the bar. Regression of
 * the device testing of 2026-10-30: the content beside the rail was told the status and navigation bar insets were
 * consumed, so a top app bar there drew under the status bar on an API 36 tablet.
 */
@RunWith(AndroidJUnit4::class)
class QeetrixNavigationScaffoldInsetsDeviceTest {
    @get:Rule
    val compose = createAndroidComposeRule<ComponentActivity>()

    private val items =
        listOf(
            QeetrixNavigationItem("home", "Home", QeetrixIconName.House),
            QeetrixNavigationItem("inbox", "Inbox", QeetrixIconName.Inbox),
        )

    @Test
    fun theContentBesideTheRailKeepsClearOfTheStatusAndNavigationBars() = assertContentClearOfTheBars(QeetrixNavigationLayout.Rail)

    @Test
    fun theContentAboveTheBarKeepsClearOfTheStatusBar() = assertContentClearOfTheBars(QeetrixNavigationLayout.Bar)

    private fun assertContentClearOfTheBars(layout: QeetrixNavigationLayout) {
        compose.runOnUiThread { drawUnderTheSystemBars() }
        var statusBar = 0
        var navigationBar = 0
        compose.setContent {
            val density = LocalDensity.current
            val top = WindowInsets.statusBars.getTop(density)
            val bottom = WindowInsets.navigationBars.getBottom(density)
            SideEffect {
                statusBar = top
                navigationBar = bottom
            }
            QeetrixTheme {
                QeetrixNavigationScaffold(items, value = "home", onValueChange = {}, layout = layout) {
                    Box(Modifier.fillMaxSize().windowInsetsPadding(WindowInsets.safeDrawing)) {
                        Box(Modifier.testTag("top").size(1.dp).align(Alignment.TopStart))
                        Box(Modifier.testTag("bottom").size(1.dp).align(Alignment.BottomStart))
                    }
                }
            }
        }
        compose.waitForIdle()
        // The window draws under the status bar, or the check would mean nothing.
        assertTrue("no status bar inset in an edge-to-edge window", statusBar > 0)
        val top = boundsOf("top").top
        assertTrue("$layout: the content starts at $top px, under the $statusBar px status bar", top >= statusBar)
        if (layout == QeetrixNavigationLayout.Rail) {
            val bottom = boundsOf("bottom").bottom
            val limit = compose.activity.window.decorView.height - navigationBar
            assertTrue("rail: the content ends at $bottom px, under the navigation bar from $limit px", bottom <= limit)
        }
    }

    /** The window draws under the status and navigation bars, as an edge-to-edge app's does. */
    @Suppress("DEPRECATION") // the platform calls androidx.activity's enableEdgeToEdge() makes, without the dependency
    private fun drawUnderTheSystemBars() {
        val window = compose.activity.window
        if (Build.VERSION.SDK_INT >= 30) {
            window.setDecorFitsSystemWindows(false)
        } else {
            window.decorView.systemUiVisibility =
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
        }
    }

    private fun boundsOf(tag: String) = compose.onNodeWithTag(tag).fetchSemanticsNode().boundsInWindow
}
