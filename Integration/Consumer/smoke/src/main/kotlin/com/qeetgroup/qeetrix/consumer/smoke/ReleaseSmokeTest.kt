package com.qeetgroup.qeetrix.consumer.smoke

import android.content.Intent
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.By
import androidx.test.uiautomator.Direction
import androidx.test.uiautomator.StaleObjectException
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.UiObject2
import androidx.test.uiautomator.Until
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File

/**
 * The consumer's release build — R8 in full mode and resource shrinking, no Qeetrix keep rules — driven from outside
 * the app as a person would: each test finds what Qeetrix drew by its text or description, acts on it and checks the
 * result. A class R8 removed or renamed wrongly, or a resource the shrinker dropped, ends the app or the flow.
 */
@RunWith(AndroidJUnit4::class)
class ReleaseSmokeTest {
    private val instrumentation = InstrumentationRegistry.getInstrumentation()
    private val device = UiDevice.getInstance(instrumentation)

    @Before
    fun launch() {
        val context = instrumentation.context
        val intent = checkNotNull(context.packageManager.getLaunchIntentForPackage(APP)) { "$APP is not installed" }
        context.startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK))
        assertTrue("the app did not show its title", device.wait(Until.hasObject(By.text("Qeetrix consumer")), 15_000))
    }

    /** Finds [text], scrolling the page to it if it is off screen — above or below — as on a short landscape screen. */
    private fun find(text: String): UiObject2 = checkNotNull(findOrNull(text)) { "\"$text\" not found" }

    private fun findOrNull(text: String): UiObject2? {
        device.wait(Until.findObject(By.text(text)), 3_000)?.let { return it }
        val page = device.findObject(By.scrollable(true)) ?: return null
        return page.scrollUntil(Direction.UP, Until.findObject(By.text(text)))
            ?: device.findObject(By.scrollable(true))?.scrollUntil(Direction.DOWN, Until.findObject(By.text(text)))
    }

    /** Taps [text], finding it again if the layout moved it between finding and tapping (a stale node). */
    private fun tap(text: String) {
        repeat(3) { attempt ->
            try {
                find(text).click()
                device.waitForIdle()
                return
            } catch (stale: StaleObjectException) {
                if (attempt == 2) throw stale
            }
        }
    }

    private fun assertShown(text: String) {
        device.wait(Until.hasObject(By.text(text)), 5_000)
        assertTrue("\"$text\" is not shown", findOrNull(text) != null)
    }

    private fun assertGone(text: String) = assertTrue("\"$text\" is still shown", device.wait(Until.gone(By.text(text)), 5_000))

    private fun capture(name: String) {
        val directory = File(instrumentation.context.getExternalFilesDir(null), "release")
        directory.mkdirs()
        device.takeScreenshot(File(directory, "$name.png"))
    }

    @Test
    fun foundationsRenderAndRespond() {
        // Waits, as every check here does: Compose publishes its accessibility tree after the first frame.
        assertTrue("no Home icon", device.wait(Until.hasObject(By.desc("Home icon")), 5_000))
        assertTrue("no Primary swatch", device.wait(Until.hasObject(By.desc("Primary swatch")), 5_000))
        tap("Primary action")
        assertShown("Pressed 1 times")
        capture("foundations_light")
        tap("Dark theme")
        capture("foundations_dark")
        assertEquals(APP, device.currentPackageName)
    }

    @Test
    fun aFormValidatesAndSubmits() {
        tap("Form")
        tap("Create account")
        assertShown("Accept the terms to continue")
        device.findObjects(By.clazz("android.widget.EditText")).first().text = "ada@example.com"
        tap("I accept the terms")
        tap("Create account")
        assertShown("Account created")
        capture("form")
    }

    @Test
    fun overlaysOpenAndClose() {
        tap("Overlays")
        tap("Open dialog")
        assertShown("Archive project?")
        capture("dialog")
        tap("Archive")
        assertGone("Archive project?")
        assertShown("Last action: Archived")

        tap("Open sheet")
        assertShown("Share invoice")
        capture("sheet")
        device.pressBack()
        assertGone("Share invoice")

        tap("More actions")
        tap("Rename")
        assertShown("Last action: Renamed")

        tap("Show snackbar")
        assertShown("Message archived")
        tap("Undo")
        assertShown("Last action: Undone")
    }

    @Test
    fun aCollectionSearches() {
        tap("People")
        assertShown("Ada Lovelace 1")
        device.findObjects(By.clazz("android.widget.EditText")).first().text = "grace"
        assertShown("Grace Hopper 1")
        assertGone("Ada Lovelace 1")
        capture("people")
    }

    private companion object {
        const val APP = "com.qeetgroup.qeetrix.consumer"
    }
}
