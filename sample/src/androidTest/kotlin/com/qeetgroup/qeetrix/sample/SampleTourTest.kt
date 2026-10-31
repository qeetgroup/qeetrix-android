package com.qeetgroup.qeetrix.sample

import android.content.Intent
import android.content.res.Configuration
import android.os.Build
import android.os.ParcelFileDescriptor
import android.view.WindowInsets
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.runner.lifecycle.ActivityLifecycleMonitorRegistry
import androidx.test.runner.lifecycle.Stage
import androidx.test.uiautomator.By
import androidx.test.uiautomator.BySelector
import androidx.test.uiautomator.Direction
import androidx.test.uiautomator.StaleObjectException
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.UiObject2
import androidx.test.uiautomator.Until
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import java.io.ByteArrayOutputStream
import java.io.FileInputStream

/**
 * A walk through every page of the sample on a device, as a person reaches it — the home screen's card, the page, its
 * overlay opened, Back — with the real clock, so spinners and overlays animate as they do for people. Each screen is
 * captured whole (`screencap`), one capture per scrolled viewport, with its window hierarchy beside it, for review in
 * the display mode the device is set to (light, dark, the largest text, right to left): the system settings are the
 * caller's, this test only names the files. Run it with
 * `adb shell am instrument -w -e mode dark -e class com.qeetgroup.qeetrix.sample.SampleTourTest
 * com.qeetgroup.qeetrix.sample.test/androidx.test.runner.AndroidJUnitRunner`, then pull the captures from
 * `/data/local/tmp/qeetrix-tour/<mode>` and the hierarchies from the app's external files, `tour/<mode>`.
 */
@RunWith(AndroidJUnit4::class)
class SampleTourTest {
    private val instrumentation = InstrumentationRegistry.getInstrumentation()
    private val device = UiDevice.getInstance(instrumentation)
    private val arguments = InstrumentationRegistry.getArguments()
    private val mode = arguments.getString("mode") ?: "light"
    private val only = arguments.getString("screens")?.split(',')?.toSet()
    private val maxPages = arguments.getString("pages")?.toInt() ?: 14
    private val out = "$TOUR/$mode"
    private val files = checkNotNull(instrumentation.targetContext.getExternalFilesDir(null)).resolve("tour/$mode")

    private val screens: List<Pair<String, String>> =
        listOf(
            "foundations" to "Foundations",
            "composition" to "Composition",
            "account-preferences" to "Account preferences",
            "workspace" to "Workspace",
            "collections" to "Collections",
        ) + SampleCatalog.map { it.id to it.title }

    /** What opens each page's overlay: the first trigger on it. */
    private val overlays: Map<String, BySelector> =
        mapOf(
            "alert-dialog" to By.text("Delete workspace…"),
            "confirmation-dialog" to By.text("Remove a member"),
            "dialog" to By.text("Publish report"),
            "bottom-sheet" to By.text("Filter invoices"),
            "menu" to By.desc("More options"),
            "snackbar" to By.text("Danger with retry"),
            "select" to By.text("e.g. Monthly"),
        )

    @Test
    fun tour() {
        shell("rm -rf $out")
        shell("mkdir -p $out")
        files.deleteRecursively()
        files.mkdirs()
        launch()
        writeWindow()
        capture("home", pages = 40)
        val missing = mutableListOf<String>()
        for ((id, title) in screens) {
            if (only != null && id !in only) continue
            try {
                if (!onHome()) launch()
                open(title)
                capture(id)
                overlays[id]?.let { trigger ->
                    toTop()
                    find(trigger).click()
                    settle(1_200)
                    shot("$id-open")
                    device.pressBack()
                    settle()
                }
                back()
            } catch (failure: Throwable) {
                missing += "$id: ${failure.message}"
                launch()
            }
        }
        assertTrue(missing.joinToString("\n", prefix = "screens not captured:\n"), missing.isEmpty())
    }

    private fun launch() {
        val context = instrumentation.targetContext
        val intent = checkNotNull(context.packageManager.getLaunchIntentForPackage(context.packageName))
        context.startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK))
        check(device.wait(Until.hasObject(By.text(HOME_TITLE)), 15_000)) { "the home screen did not show" }
        settle()
    }

    private fun onHome(): Boolean = device.hasObject(By.text(HOME_TITLE))

    /** Back to the home screen: Back once, or a few times when a page holds a level of its own. */
    private fun back() {
        repeat(3) {
            device.pressBack()
            if (device.wait(Until.hasObject(By.text(HOME_TITLE)), 3_000)) return
        }
        launch()
    }

    /** Opens the home screen's card [title], scrolling the page to it. */
    private fun open(title: String) {
        val card = find(By.text(title))
        // A card at the very bottom edge would be tapped under the navigation bar: bring it up first.
        if (card.visibleBounds.bottom > device.displayHeight * 0.8) {
            page()?.scroll(Direction.DOWN, 0.3f)
            settle(300)
        }
        repeat(3) { attempt ->
            try {
                find(By.text(title)).click()
                settle(900)
                return
            } catch (stale: StaleObjectException) {
                if (attempt == 2) throw stale
            }
        }
    }

    /**
     * Finds [selector] on screen, scrolling the page down and then up to it. (UI Automator's `scrollUntil` stops early on
     * a long Compose page, so this scrolls step by step until the page stops moving.)
     */
    private fun find(selector: BySelector): UiObject2 {
        device.wait(Until.findObject(selector), 2_000)?.let { return it }
        for (direction in listOf(Direction.DOWN, Direction.UP)) {
            var before = hierarchy()
            for (step in 1..MAX_STEPS) {
                val scroller = page() ?: break
                scroller.scroll(direction, 0.7f)
                device.waitForIdle()
                device.findObject(selector)?.let { return it }
                val after = hierarchy()
                if (after == before) break
                before = after
            }
        }
        error("$selector not found")
    }

    /** The page's scrolling container: the largest scrollable on screen. */
    private fun page(): UiObject2? =
        device
            .findObjects(By.scrollable(true))
            .maxByOrNull { it.visibleBounds.width() * it.visibleBounds.height() }

    private fun toTop() {
        var before = hierarchy()
        for (step in 1..MAX_STEPS) {
            val scroller = page() ?: return
            scroller.scroll(Direction.UP, 1f)
            device.waitForIdle()
            val after = hierarchy()
            if (after == before) return
            before = after
        }
    }

    /** The screen as it is, then each further viewport of its page until the page ends. */
    private fun capture(
        name: String,
        pages: Int = maxPages,
    ) {
        settle()
        var hierarchy = hierarchy()
        shot(name, hierarchy)
        for (number in 2..pages) {
            val scroller = page() ?: break
            scroller.scroll(Direction.DOWN, 0.8f)
            settle(500)
            val next = hierarchy()
            if (next == hierarchy) break
            hierarchy = next
            shot("$name-p$number", hierarchy)
        }
    }

    private fun shot(
        name: String,
        hierarchy: String = hierarchy(),
    ) {
        shell("screencap -p $out/$name-$mode.png")
        write("$name-$mode.xml", hierarchy)
    }

    private fun hierarchy(): String = ByteArrayOutputStream().also { device.dumpWindowHierarchy(it) }.toString()

    /** The window's system-bar and cutout insets and the density, for the review's checks of what sits under them. */
    private fun writeWindow() {
        var line = ""
        instrumentation.runOnMainSync {
            val activity = ActivityLifecycleMonitorRegistry.getInstance().getActivitiesInStage(Stage.RESUMED).first()
            val insets = activity.window.decorView.rootWindowInsets
            val metrics = activity.resources.displayMetrics
            val configuration = activity.resources.configuration

            fun sides(
                left: Int,
                top: Int,
                right: Int,
                bottom: Int,
            ) = "[$left,$top,$right,$bottom]"

            val status: String
            val navigation: String
            val cutout: String
            if (Build.VERSION.SDK_INT >= 30) {
                val s = insets.getInsets(WindowInsets.Type.statusBars())
                val n = insets.getInsets(WindowInsets.Type.navigationBars())
                val c = insets.getInsets(WindowInsets.Type.displayCutout())
                status = sides(s.left, s.top, s.right, s.bottom)
                navigation = sides(n.left, n.top, n.right, n.bottom)
                cutout = sides(c.left, c.top, c.right, c.bottom)
            } else {
                // API 29: the stable insets are the status bar's (top) and the navigation bar's (the other sides).
                @Suppress("DEPRECATION")
                status = sides(0, insets.stableInsetTop, 0, 0)
                @Suppress("DEPRECATION")
                navigation = sides(insets.stableInsetLeft, 0, insets.stableInsetRight, insets.stableInsetBottom)
                val c = insets.displayCutout
                cutout = if (c == null) sides(0, 0, 0, 0) else sides(c.safeInsetLeft, c.safeInsetTop, c.safeInsetRight, c.safeInsetBottom)
            }
            val night = configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES
            line =
                """{"sdk":${Build.VERSION.SDK_INT},"density":${metrics.density},"fontScale":${configuration.fontScale},""" +
                """"rtl":${configuration.layoutDirection == 1},"night":$night,""" +
                """"width":${metrics.widthPixels},"height":${metrics.heightPixels},""" +
                """"statusBars":$status,"navigationBars":$navigation,"cutout":$cutout}"""
        }
        write("window.json", line)
    }

    private fun settle(millis: Long = 600) {
        device.waitForIdle()
        Thread.sleep(millis)
    }

    /** A text file beside the captures, in the app's external files (`tour/<mode>/`), which the shell cannot write for it. */
    private fun write(
        name: String,
        text: String,
    ) {
        files.resolve(name).writeText(text)
    }

    private fun shell(command: String) {
        val descriptor = instrumentation.uiAutomation.executeShellCommand(command)
        ParcelFileDescriptor.AutoCloseInputStream(descriptor).use { (it as FileInputStream).readBytes() }
    }

    private companion object {
        const val TOUR = "/data/local/tmp/qeetrix-tour"
        const val HOME_TITLE = "Qeetrix for Android"
        const val MAX_STEPS = 60
    }
}
