package com.qeetgroup.qeetrix.sample.components.forms

import android.net.Uri
import android.provider.OpenableColumns
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.qeetgroup.qeetrix.components.fileupload.QeetrixAttachmentPicker
import com.qeetgroup.qeetrix.components.fileupload.QeetrixFileLimits
import com.qeetgroup.qeetrix.components.fileupload.QeetrixFileRow
import com.qeetgroup.qeetrix.components.separator.QeetrixSeparator
import com.qeetgroup.qeetrix.sample.SampleNote
import com.qeetgroup.qeetrix.sample.SamplePage
import com.qeetgroup.qeetrix.sample.SamplePreview
import com.qeetgroup.qeetrix.sample.SampleSection
import com.qeetgroup.qeetrix.sample.SampleSurface
import kotlinx.coroutines.delay

/** A chosen file and its made-up upload. */
private class Chosen(
    val name: String,
    val size: Long?,
    val type: String?,
    val problem: String?,
) {
    var progress by mutableFloatStateOf(0f)
}

/** File upload: the system's pickers for real, each choice checked against the limits and uploaded; then every row state. */
@Composable
fun FileUploadSample() {
    val context = LocalContext.current
    val limits = remember { QeetrixFileLimits(types = listOf("PDF", "PNG", "JPG"), maxBytes = 10L * 1024 * 1024) }
    val chosen = remember { mutableStateListOf<Chosen>() }

    fun add(uri: Uri?) {
        if (uri == null) return
        var name = uri.lastPathSegment ?: "file"
        var size: Long? = null
        context.contentResolver.query(uri, arrayOf(OpenableColumns.DISPLAY_NAME, OpenableColumns.SIZE), null, null, null)?.use {
            if (it.moveToFirst()) {
                name = it.getString(0) ?: name
                size = if (it.isNull(1)) null else it.getLong(1)
            }
        }
        chosen += Chosen(name, size, context.contentResolver.getType(uri), limits.problem(context, name, size))
    }
    val photos = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { add(it) }
    val files = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { add(it) }
    SamplePage {
        SampleSection("Attach a receipt") {
            QeetrixAttachmentPicker(
                onPhotos = { photos.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)) },
                onFiles = { files.launch(arrayOf("*/*")) },
                hint = limits.hint(context),
            )
            if (chosen.isNotEmpty()) {
                SampleSurface {
                    chosen.forEachIndexed { index, file ->
                        if (index > 0) QeetrixSeparator()
                        if (file.problem == null) {
                            LaunchedEffect(file) {
                                while (file.progress < 1f) {
                                    delay(120)
                                    file.progress = (file.progress + 0.05f).coerceAtMost(1f)
                                }
                            }
                        }
                        QeetrixFileRow(
                            file.name,
                            sizeBytes = file.size,
                            mimeType = file.type,
                            progress = if (file.problem == null) file.progress else null,
                            uploaded = file.problem == null && file.progress >= 1f,
                            error = file.problem,
                            onRemove = { chosen.remove(file) },
                        )
                    }
                }
            }
            SampleNote(
                "Photos and Files open the system's own pickers; Qeetrix asks for no permission. A file of another type, or " +
                    "over 10 MB, is refused with the reason.",
            )
        }
        SampleSection("Every row") {
            SampleSurface {
                QeetrixFileRow("GST-invoice-October.pdf", sizeBytes = 248_000, progress = 0.42f, onRemove = {})
                QeetrixSeparator()
                QeetrixFileRow("receipt.png", sizeBytes = 1_200_000, uploaded = true, onRemove = {})
                QeetrixSeparator()
                QeetrixFileRow(
                    "payroll-2026.xlsx",
                    sizeBytes = 32_000,
                    error = "Only PDF, PNG or JPG files can be used here",
                    onRemove = {},
                )
                QeetrixSeparator()
                QeetrixFileRow("statements-archive-for-the-whole-financial-year-2025-26.zip", sizeBytes = 48_000_000, onRemove = {})
            }
        }
    }
}

@PreviewLightDark
@Composable
private fun FileUploadSamplePreview() {
    SamplePreview { FileUploadSample() }
}
