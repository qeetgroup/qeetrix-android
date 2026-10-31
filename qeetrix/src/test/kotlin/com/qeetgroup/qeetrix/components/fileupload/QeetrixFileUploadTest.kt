package com.qeetgroup.qeetrix.components.fileupload

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.qeetgroup.qeetrix.components.setQeetrix
import com.qeetgroup.qeetrix.icons.QeetrixIconName
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment

/** File upload: the limits and the web's words for them, the type's icon, the row's Remove, and only the pickers given. */
@RunWith(RobolectricTestRunner::class)
class QeetrixFileUploadTest {
    @get:Rule
    val compose = createComposeRule()

    private val context = RuntimeEnvironment.getApplication()
    private val limits = QeetrixFileLimits(types = listOf("PDF", "PNG", "JPG"), maxBytes = 10L * 1024 * 1024)

    @Test
    fun theLimitsAreSaidInTheWebsWords() {
        val hint = limits.hint(context)!!
        assertTrue(hint, hint.startsWith("PDF, PNG or JPG, up to "))
        assertTrue(QeetrixFileLimits(maxBytes = 1024).hint(context)!!.startsWith("Up to "))
        assertEquals("PDF", QeetrixFileLimits(types = listOf("PDF")).hint(context))
        assertNull(QeetrixFileLimits().hint(context))
    }

    @Test
    fun aFileOfAnotherTypeOrTooLargeIsRefusedWithTheReason() {
        assertEquals("Only PDF, PNG or JPG files can be used here", limits.problem(context, "notes.txt", 2_048))
        assertTrue(limits.problem(context, "scan.PDF", 20L * 1024 * 1024)!!.startsWith("Larger than "))
        // The extension is matched whatever its case, and a file of unknown size isn't refused for it.
        assertNull(limits.problem(context, "receipt.Pdf", 34))
        assertNull(limits.problem(context, "receipt.jpg", null))
        assertNull(QeetrixFileLimits().problem(context, "anything.bin", 1L shl 40))
    }

    @Test
    fun theIconFollowsTheTypeThenTheExtension() {
        assertEquals(QeetrixIconName.FileImage, iconFor("image/heic", "IMG_2041"))
        assertEquals(QeetrixIconName.FileSpreadsheet, iconFor(null, "payroll.xlsx"))
        assertEquals(QeetrixIconName.FileCode, iconFor("application/json", "export"))
        assertEquals(QeetrixIconName.FileArchive, iconFor(null, "statements.zip"))
        assertEquals(QeetrixIconName.FileText, iconFor("application/pdf", "receipt"))
        assertEquals(QeetrixIconName.File, iconFor(null, "firmware.bin"))
    }

    @Test
    fun aRowIsRemovedByItsNamedButtonAndOnlyTheGivenPickersShow() {
        var removed = false
        var files = 0
        compose.setQeetrix {
            QeetrixAttachmentPicker(onFiles = { files++ })
            QeetrixFileRow("receipt.pdf", sizeBytes = 34, uploaded = true, onRemove = { removed = true })
        }
        compose.onNodeWithText("Photos").assertDoesNotExist()
        compose.onNodeWithText("Camera").assertDoesNotExist()
        compose.onNodeWithText("Files").performClick()
        compose.onNodeWithContentDescription("Remove receipt.pdf").performClick()
        assertEquals(1, files)
        assertTrue(removed)
    }
}
