package com.qeetgroup.qeetrix.components.scanner

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.qeetgroup.qeetrix.R
import com.qeetgroup.qeetrix.components.button.QeetrixButton
import com.qeetgroup.qeetrix.components.button.QeetrixButtonTone
import com.qeetgroup.qeetrix.components.button.QeetrixButtonVariant
import com.qeetgroup.qeetrix.components.button.QeetrixIconButton
import com.qeetgroup.qeetrix.components.emptystate.QeetrixEmptyState
import com.qeetgroup.qeetrix.components.emptystate.QeetrixEmptyStateSize
import com.qeetgroup.qeetrix.components.permission.PermissionPrimer
import com.qeetgroup.qeetrix.components.permission.QeetrixPermission
import com.qeetgroup.qeetrix.components.permission.QeetrixPermissionStatus
import com.qeetgroup.qeetrix.foundation.shape.QeetrixShapes
import com.qeetgroup.qeetrix.foundation.sizing.QeetrixBorder
import com.qeetgroup.qeetrix.foundation.spacing.QeetrixSpace
import com.qeetgroup.qeetrix.icons.QeetrixIconName
import com.qeetgroup.qeetrix.internal.labelDirection
import com.qeetgroup.qeetrix.theme.QeetrixTheme

/** What a [QeetrixQRScanner] shows (`value` is the name shared with qeetrix-ios). */
public enum class QeetrixScannerState(
    public val value: String,
) {
    /** The camera is running: the preview under the scrim and the target frame. */
    Scanning("scanning"),

    /** The camera has not been allowed yet: why, and Continue to let the system ask. */
    PermissionNeeded("permission-needed"),

    /** The camera was refused: Open Settings. */
    PermissionDenied("permission-denied"),

    /** The device has no camera: type the key instead. */
    NoCamera("no-camera"),
}

/**
 * A QR scanner's screen over a camera preview the app supplies — for adding an account or pairing a phone. Qeetrix has
 * no camera dependency: the app brings CameraX (or any camera), decodes the frames, and passes its preview as
 * [preview]. `@qeetrix/react` has no counterpart.
 *
 * - **[QeetrixScannerState.Scanning]**: the [preview] fills the box, under `color.overlay.scrim` with a clear square
 *   target and its four corners drawn 3 dp (`border.width.accent`) in `color.border.brand`. The controls stack at the
 *   bottom, and the target is centred in the space above them (70 % of its shorter side, at most 280 dp), so they never
 *   meet. The controls carry their own fills, as the scrim is translucent and the camera may see a white page: the
 *   [guidance] ("Point the camera at the QR code", `body.md`) in `color.text.inverse` on `color.bg.neutral.bold`;
 *   [onTorchChange]'s torch toggle (`flashlight` / `flashlight-off`, solid neutral; leave it null on a device with no
 *   flash); and the fallback, soft.
 * - **Permission states**: a [QeetrixPermissionPrimer] for the camera, centred — Continue ([onAllowCamera]) while not
 *   yet asked, Open Settings once refused ([onOpenSettings], or the app's Settings page). It has no Not now: the way
 *   out is the fallback, or the app's Back. Check the permission again when the screen resumes (`LifecycleResumeEffect`):
 *   the person may have turned the camera on in Settings.
 * - **[QeetrixScannerState.NoCamera]**: an empty state saying so.
 * - **The fallback**: with [onEnterKey], "Type the key instead" (`keyboard`) is there in every state — ghost on the
 *   page, soft on the camera.
 *
 * The bottom actions keep clear of the navigation bar. The preview is decorative to accessibility services; the
 * guidance, the torch and the fallback are read.
 *
 * ```
 * QeetrixQRScanner(state, torch = torch, onTorchChange = { torch = it }, onEnterKey = ::typeKey, onAllowCamera = ask) {
 *     AndroidView(factory = { PreviewView(it).also(bindCamera) })
 * }
 * ```
 */
@Composable
public fun QeetrixQRScanner(
    state: QeetrixScannerState,
    modifier: Modifier = Modifier,
    guidance: String = stringResource(R.string.qx_scanner_guidance),
    torch: Boolean = false,
    onTorchChange: ((Boolean) -> Unit)? = null,
    onEnterKey: (() -> Unit)? = null,
    onAllowCamera: () -> Unit = {},
    onOpenSettings: (() -> Unit)? = null,
    preview: @Composable () -> Unit,
) {
    val colors = QeetrixTheme.colors
    // Ghost on the page; on the camera, filled (soft), as the scrim is translucent.
    val fallback: @Composable (QeetrixButtonVariant) -> Unit = { variant ->
        if (onEnterKey != null) {
            QeetrixButton(
                stringResource(R.string.qx_scanner_enter_key),
                onClick = onEnterKey,
                variant = variant,
                tone = QeetrixButtonTone.Neutral,
                icon = QeetrixIconName.Keyboard,
            )
        }
    }
    when (state) {
        QeetrixScannerState.Scanning -> {
            Box(modifier.fillMaxSize()) {
                Box(Modifier.fillMaxSize().clearAndSetSemantics {}) { preview() }
                val scrim = colors.overlayScrim
                Column(Modifier.fillMaxSize()) {
                    // The target, centred in the space above the controls, so the two never meet — in a short box, at
                    // 200 % text, in landscape.
                    BoxWithConstraints(Modifier.weight(1f).fillMaxWidth()) {
                        val side = minOf(minOf(maxWidth, maxHeight) * TARGET_SHARE, MAX_TARGET)
                        val corner = colors.borderBrand
                        Canvas(Modifier.fillMaxSize()) {
                            val target = side.toPx()
                            val hole = Rect(Offset((size.width - target) / 2, (size.height - target) / 2), Size(target, target))
                            // The scrim with the target cut out.
                            val path =
                                Path().apply {
                                    fillType = PathFillType.EvenOdd
                                    addRect(Rect(Offset.Zero, size))
                                    addRect(hole)
                                }
                            drawPath(path, scrim)
                            // The four corners, inside the target's edge.
                            val width = QeetrixBorder.WidthAccent.toPx()
                            val arm = CORNER_ARM.toPx()
                            val inset = width / 2
                            val corners =
                                listOf(
                                    Triple(Offset(hole.left + inset, hole.top + inset), 1f, 1f),
                                    Triple(Offset(hole.right - inset, hole.top + inset), -1f, 1f),
                                    Triple(Offset(hole.left + inset, hole.bottom - inset), 1f, -1f),
                                    Triple(Offset(hole.right - inset, hole.bottom - inset), -1f, -1f),
                                )
                            for ((point, dx, dy) in corners) {
                                val stroke =
                                    Path().apply {
                                        moveTo(point.x + dx * arm, point.y)
                                        lineTo(point.x, point.y)
                                        lineTo(point.x, point.y + dy * arm)
                                    }
                                drawPath(stroke, corner, style = Stroke(width, cap = StrokeCap.Square))
                            }
                        }
                    }
                    // The controls, on the scrim but each on its own fill: the scrim is translucent, and the camera may
                    // see a white page (white text on the light scrim over white is 2.8:1).
                    Column(
                        Modifier
                            .fillMaxWidth()
                            .background(scrim)
                            .windowInsetsPadding(WindowInsets.safeDrawing.only(WindowInsetsSides.Bottom + WindowInsetsSides.Horizontal))
                            .padding(QeetrixSpace.InsetLg),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(QeetrixSpace.GapMd),
                    ) {
                        BasicText(
                            guidance,
                            Modifier
                                .background(colors.bgNeutralBold, QeetrixShapes.Control)
                                .padding(horizontal = QeetrixSpace.InsetMd, vertical = QeetrixSpace.InsetXs),
                            style =
                                QeetrixTheme.typography.bodyMd
                                    .copy(
                                        color = colors.textInverse,
                                        textAlign = TextAlign.Center,
                                    ).labelDirection(alignToLayout = true),
                        )
                        if (onTorchChange != null) {
                            QeetrixIconButton(
                                if (torch) QeetrixIconName.FlashlightOff else QeetrixIconName.Flashlight,
                                stringResource(if (torch) R.string.qx_scanner_torch_off else R.string.qx_scanner_torch_on),
                                onClick = { onTorchChange(!torch) },
                                variant = QeetrixButtonVariant.Solid,
                                tone = QeetrixButtonTone.Neutral,
                            )
                        }
                        fallback(QeetrixButtonVariant.Soft)
                    }
                }
            }
        }

        QeetrixScannerState.PermissionNeeded, QeetrixScannerState.PermissionDenied -> {
            StateColumn(modifier) {
                // No Not now: the way out is the fallback, or the app's Back.
                PermissionPrimer(
                    QeetrixPermission.Camera,
                    title = stringResource(R.string.qx_scanner_permission_title),
                    description = stringResource(R.string.qx_scanner_permission_description),
                    onContinue = onAllowCamera,
                    onNotNow = null,
                    status =
                        if (state ==
                            QeetrixScannerState.PermissionDenied
                        ) {
                            QeetrixPermissionStatus.Denied
                        } else {
                            QeetrixPermissionStatus.NotAsked
                        },
                    deniedDescription = stringResource(R.string.qx_scanner_permission_denied),
                    onOpenSettings = onOpenSettings,
                )
                fallback(QeetrixButtonVariant.Ghost)
            }
        }

        QeetrixScannerState.NoCamera -> {
            StateColumn(modifier) {
                QeetrixEmptyState(
                    stringResource(R.string.qx_scanner_no_camera),
                    icon = QeetrixIconName.CameraOff,
                    description = stringResource(R.string.qx_scanner_no_camera_description),
                    size = QeetrixEmptyStateSize.Section,
                )
                fallback(QeetrixButtonVariant.Ghost)
            }
        }
    }
}

/** A state without the camera: centred in the scanner's area, clear of the navigation bar. */
@Composable
private fun StateColumn(
    modifier: Modifier,
    content: @Composable ColumnScope.() -> Unit,
) {
    Column(
        modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.safeDrawing.only(WindowInsetsSides.Bottom + WindowInsetsSides.Horizontal))
            .padding(QeetrixSpace.InsetLg),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(QeetrixSpace.GapMd, Alignment.CenterVertically),
        content = content,
    )
}

/** The target's share of the shorter side of the space above the controls, and its largest size. */
private const val TARGET_SHARE = 0.7f
private val MAX_TARGET = 280.dp

/** The length of each corner's arms. */
private val CORNER_ARM = 24.dp
