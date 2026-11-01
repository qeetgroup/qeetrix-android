package com.qeetgroup.qeetrix.components.fileupload

import android.text.format.Formatter
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.qeetgroup.qeetrix.R
import com.qeetgroup.qeetrix.components.button.QeetrixButton
import com.qeetgroup.qeetrix.components.button.QeetrixButtonSize
import com.qeetgroup.qeetrix.components.button.QeetrixButtonTone
import com.qeetgroup.qeetrix.components.button.QeetrixButtonVariant
import com.qeetgroup.qeetrix.components.button.QeetrixIconButton
import com.qeetgroup.qeetrix.components.progress.QeetrixProgress
import com.qeetgroup.qeetrix.components.progress.QeetrixProgressTone
import com.qeetgroup.qeetrix.foundation.sizing.QeetrixSize
import com.qeetgroup.qeetrix.foundation.spacing.QeetrixSpace
import com.qeetgroup.qeetrix.icons.QeetrixIcon
import com.qeetgroup.qeetrix.icons.QeetrixIconName
import com.qeetgroup.qeetrix.icons.QeetrixIconShape
import com.qeetgroup.qeetrix.internal.labelDirection
import com.qeetgroup.qeetrix.theme.QeetrixTheme

/**
 * Where a person chooses files on a phone — `@qeetrix/react`'s FileUpload, through the system's pickers: **Photos**,
 * **Camera** and **Files**, each shown when the app gives its action, then [hint] ("PDF or PNG, up to 10 MB", from
 * [QeetrixFileLimits.hint]). Qeetrix asks for no permission and launches nothing itself: the app keeps its
 * `ActivityResultLauncher`s (`PickVisualMedia`, `TakePicture`, `OpenDocument`) and passes what they return to a list
 * of [QeetrixFileRow]s. The actions are soft, neutral, 48 dp buttons that wrap onto more lines at large text.
 *
 * ```
 * QeetrixAttachmentPicker(
 *     onPhotos = { photos.launch(PickVisualMediaRequest(ImageOnly)) },
 *     onFiles = { files.launch(arrayOf("application/pdf")) },
 *     hint = limits.hint(context),
 * )
 * ```
 */
@Composable
public fun QeetrixAttachmentPicker(
    modifier: Modifier = Modifier,
    onPhotos: (() -> Unit)? = null,
    onCamera: (() -> Unit)? = null,
    onFiles: (() -> Unit)? = null,
    hint: String? = null,
    enabled: Boolean = true,
) {
    Column(modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(QeetrixSpace.GapXs)) {
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(QeetrixSpace.GapSm),
            verticalArrangement = Arrangement.spacedBy(QeetrixSpace.GapSm),
        ) {
            if (onPhotos != null) Pick(stringResource(R.string.qx_attach_photos), QeetrixIconName.Image, onPhotos, enabled)
            if (onCamera != null) Pick(stringResource(R.string.qx_attach_camera), QeetrixIconName.Camera, onCamera, enabled)
            if (onFiles != null) Pick(stringResource(R.string.qx_attach_files), QeetrixIconName.Paperclip, onFiles, enabled)
        }
        if (hint != null) {
            // fileUpload.hint: body.sm, color.text.subtle
            BasicText(
                hint,
                style =
                    QeetrixTheme.typography.bodySm
                        .copy(color = QeetrixTheme.colors.textSubtle)
                        .labelDirection(alignToLayout = true),
            )
        }
    }
}

@Composable
private fun Pick(
    text: String,
    icon: QeetrixIconName,
    onClick: () -> Unit,
    enabled: Boolean,
) {
    QeetrixButton(
        text,
        onClick = onClick,
        variant = QeetrixButtonVariant.Soft,
        tone = QeetrixButtonTone.Neutral,
        icon = icon,
        enabled = enabled,
    )
}

/**
 * One chosen file — `@qeetrix/react`'s FileUpload item (file-upload.css): the type's icon (`file-image`,
 * `file-spreadsheet`, `file-code`, `file-archive`, `file-text`, else `file`; 16 dp, `color.icon.subtle`), the [name]
 * (`label.md`, wrapping), its size in the locale's units and type (`body.sm`, `color.text.subtle`), then — while it
 * uploads — a [progress] bar, or the [error] in `color.text.danger`, and a Remove button named "Remove {name}". At least
 * `size.row` tall; rows are ruled by the list they sit in.
 *
 * Read as one element: the name, then "Uploading report.pdf, 42%", "Uploaded" or the error.
 */
@Composable
public fun QeetrixFileRow(
    name: String,
    modifier: Modifier = Modifier,
    sizeBytes: Long? = null,
    mimeType: String? = null,
    progress: Float? = null,
    uploaded: Boolean = false,
    error: String? = null,
    onRemove: (() -> Unit)? = null,
) {
    val colors = QeetrixTheme.colors
    val typography = QeetrixTheme.typography
    val context = LocalContext.current
    val size = sizeBytes?.let { Formatter.formatShortFileSize(context, it) }
    Row(
        modifier
            .fillMaxWidth()
            .heightIn(min = QeetrixTheme.density.sizeRow)
            .padding(start = QeetrixSpace.InsetSm, end = QeetrixSpace.GapXs, top = QeetrixSpace.GapXs, bottom = QeetrixSpace.GapXs),
        horizontalArrangement = Arrangement.spacedBy(QeetrixSpace.GapSm),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        QeetrixIcon(
            iconFor(mimeType, name),
            contentDescription = null,
            size = QeetrixSize.IconSm,
            shape = QeetrixIconShape.Sharp,
            tint = colors.iconSubtle,
        )
        Column(Modifier.weight(1f).semantics(mergeDescendants = true) {}, verticalArrangement = Arrangement.spacedBy(2.dp)) {
            BasicText(name, style = typography.labelMd.copy(color = colors.textDefault).labelDirection(alignToLayout = true))
            val meta =
                listOfNotNull(size, if (uploaded && error == null) stringResource(R.string.qx_file_uploaded) else null).joinToString(" · ")
            if (meta.isNotEmpty()) {
                BasicText(
                    meta,
                    style = typography.bodySm.copy(color = colors.textSubtle).labelDirection(alignToLayout = true),
                )
            }
            if (error != null) {
                BasicText(error, style = typography.bodySm.copy(color = colors.textDanger).labelDirection(alignToLayout = true))
            } else if (progress != null && !uploaded) {
                QeetrixProgress(
                    progress * 100f,
                    Modifier.padding(top = QeetrixSpace.GapXs),
                    tone = QeetrixProgressTone.Brand,
                    contentDescription = stringResource(R.string.qx_file_uploading, name),
                )
            }
        }
        if (onRemove != null) {
            QeetrixIconButton(
                QeetrixIconName.X,
                stringResource(R.string.qx_file_remove, name),
                onClick = onRemove,
                tone = QeetrixButtonTone.Neutral,
                size = QeetrixButtonSize.Sm,
            )
        }
    }
}

/** file-upload.tsx's icon for a file: by MIME type, else by extension. */
internal fun iconFor(
    mimeType: String?,
    name: String,
): QeetrixIconName {
    val type = mimeType.orEmpty().lowercase()
    val extension = name.substringAfterLast('.', "").lowercase()
    return when {
        type.startsWith("image/") || extension in setOf("png", "jpg", "jpeg", "gif", "webp", "heic", "svg") -> {
            QeetrixIconName.FileImage
        }

        type in setOf("text/csv", "application/vnd.ms-excel") || type.contains("spreadsheet") ||
            extension in setOf("csv", "xls", "xlsx", "ods") -> {
            QeetrixIconName.FileSpreadsheet
        }

        type == "application/json" || type.contains("xml") || extension in setOf("json", "xml", "yaml", "yml", "js", "ts", "kt") -> {
            QeetrixIconName.FileCode
        }

        type == "application/zip" || type.contains("compressed") || extension in setOf("zip", "gz", "tar", "7z", "rar") -> {
            QeetrixIconName.FileArchive
        }

        type == "application/pdf" || type.startsWith("text/") || extension in setOf("pdf", "txt", "doc", "docx", "md") -> {
            QeetrixIconName.FileText
        }

        else -> {
            QeetrixIconName.File
        }
    }
}

/**
 * The limits on what can be chosen, and the web's messages for them (`fileUpload.*`): [types] as people name them
 * ("PDF", "PNG"), matched by extension, and [maxBytes].
 */
public class QeetrixFileLimits(
    public val types: List<String> = emptyList(),
    public val maxBytes: Long? = null,
) {
    /** "PDF or PNG, up to 10 MB", "Up to 10 MB", or null without limits (`fileUpload.hint`, `hintSize`). */
    public fun hint(context: android.content.Context): String? {
        val size = maxBytes?.let { Formatter.formatShortFileSize(context, it) }
        val named = joinedTypes(context)
        return when {
            named != null && size != null -> context.getString(R.string.qx_file_hint, named, size)
            size != null -> context.getString(R.string.qx_file_hint_size, size)
            else -> named
        }
    }

    /** Why [name] of [sizeBytes] can't be used — "Only PDF or PNG files can be used here", "Larger than 10 MB" — or null. */
    public fun problem(
        context: android.content.Context,
        name: String,
        sizeBytes: Long?,
    ): String? {
        val extension = name.substringAfterLast('.', "").lowercase()
        if (types.isNotEmpty() && types.none { it.lowercase() == extension }) {
            return context.getString(R.string.qx_file_wrong_type, joinedTypes(context))
        }
        if (maxBytes != null && sizeBytes != null && sizeBytes > maxBytes) {
            return context.getString(R.string.qx_file_too_big, Formatter.formatShortFileSize(context, maxBytes))
        }
        return null
    }

    // "PDF, PNG or JPG": the last two joined by the translatable "or" (Intl.ListFormat's disjunction).
    private fun joinedTypes(context: android.content.Context): String? =
        when (types.size) {
            0 -> null
            1 -> types[0]
            else -> context.getString(R.string.qx_file_types_or, types.dropLast(1).joinToString(", "), types.last())
        }
}
