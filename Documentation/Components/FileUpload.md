# Attachments

`QeetrixAttachmentPicker`, `QeetrixFileRow`, `QeetrixFileLimits` · `components.fileupload` · `@qeetrix/react`: FileUpload (experimental) · QTRX-127

Choosing files on a phone and showing them.

```kotlin
QeetrixAttachmentPicker(onPhotos = { photos.launch(…) }, onFiles = { files.launch(…) }, hint = limits.hint(context))
QeetrixFileRow("invoice.pdf", sizeBytes = 248_000, progress = 0.42f, onRemove = ::remove)
```

## Look, behaviour and accessibility

- Photos, Camera and Files through the system's pickers, which the app launches (no permission or launcher in Qeetrix).
- A row per file: the type's icon (file-image, file-spreadsheet, file-code, file-archive, file-text, file), the name, the size in the locale's units, a progress bar while uploading, the error, Remove ("Remove {name}").
- `QeetrixFileLimits` writes the web's messages: "PDF or PNG, up to 10 MB", "Only PDF or PNG files can be used here", "Larger than 10 MB".

Tried on the phone emulator (`qeetrix-api36`, Android 16) on 31 October 2026, in light, dark, 200 % text and right to left, from the sample app's page, choosing a file through the system's Files picker for real. The web shows a file's name on one line with an ellipsis; Android wraps it, so a long name stays readable at large text.
