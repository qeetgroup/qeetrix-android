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
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Constraints
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

/** How a scanning [QeetrixQRScanner] lays out its torch and fallback (`value` is the name shared with qeetrix-ios). */
public enum class QeetrixScannerControlsLayout(
    public val value: String,
) {
    /** The torch above the fallback, both centred. The default. */
    Stacked("stacked"),

    /**
     * One row: the fallback filling it, the torch at its own size at the end. Where the fallback's words don't fit beside
     * the torch — a narrow phone, large text — the torch goes above a full-width fallback.
     */
    Row("row"),
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
 *   flash); and the fallback, soft. [controlsLayout] [QeetrixScannerControlsLayout.Row] puts the fallback and the torch
 *   in one row, the fallback filling it.
 * - **Permission states**: a [QeetrixPermissionPrimer] for the camera, centred — Continue ([onAllowCamera]) while not
 *   yet asked, Open Settings once refused ([onOpenSettings], or the app's Settings page). It has no Not now: the way
 *   out is the fallback, or the app's Back. [deniedDescription] says how to turn the camera on ("Turn on the camera for
 *   Qeet ID in Settings…"); the default says "for this app". Check the permission again when the screen resumes (`LifecycleResumeEffect`):
 *   the person may have turned the camera on in Settings.
 * - **[QeetrixScannerState.NoCamera]**: an empty state saying so.
 * - **The fallback**: with [onEnterKey], [enterKeyLabel] ("Type the key instead"; "Type the code instead" for a pairing
 *   code) with the `keyboard` icon is there in every state — ghost on the page, soft on the camera. It is a button's
 *   label, on one line, so keep it as short as the default: longer words end in an ellipsis on a small phone at 200 %.
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
    enterKeyLabel: String = stringResource(R.string.qx_scanner_enter_key),
    deniedDescription: String = stringResource(R.string.qx_scanner_permission_denied),
    controlsLayout: QeetrixScannerControlsLayout = QeetrixScannerControlsLayout.Stacked,
    preview: @Composable () -> Unit,
) {
    val colors = QeetrixTheme.colors
    // Ghost on the page; on the camera, filled (soft), as the scrim is translucent. [fill] makes it fill its row.
    val fallback: @Composable (QeetrixButtonVariant, Modifier?) -> Unit = { variant, fill ->
        if (onEnterKey != null) {
            QeetrixButton(
                enterKeyLabel,
                onClick = onEnterKey,
                modifier = fill ?: Modifier,
                variant = variant,
                tone = QeetrixButtonTone.Neutral,
                icon = QeetrixIconName.Keyboard,
                fullWidth = fill != null,
            )
        }
    }
    val torchToggle: @Composable () -> Unit = {
        if (onTorchChange != null) {
            QeetrixIconButton(
                if (torch) QeetrixIconName.FlashlightOff else QeetrixIconName.Flashlight,
                stringResource(if (torch) R.string.qx_scanner_torch_off else R.string.qx_scanner_torch_on),
                onClick = { onTorchChange(!torch) },
                variant = QeetrixButtonVariant.Solid,
                tone = QeetrixButtonTone.Neutral,
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
                        when (controlsLayout) {
                            QeetrixScannerControlsLayout.Stacked -> {
                                torchToggle()
                                fallback(QeetrixButtonVariant.Soft, null)
                            }

                            QeetrixScannerControlsLayout.Row -> {
                                // A fill modifier makes the fallback full width; ControlsRow gives it the row's width.
                                ControlsRow({ fallback(QeetrixButtonVariant.Soft, Modifier) }, torchToggle)
                            }
                        }
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
                    deniedDescription = deniedDescription,
                    onOpenSettings = onOpenSettings,
                )
                fallback(QeetrixButtonVariant.Ghost, null)
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
                fallback(QeetrixButtonVariant.Ghost, null)
            }
        }
    }
}

/**
 * [QeetrixScannerControlsLayout.Row]: the fallback filling the row, `space.gap.sm` before the torch at the end — or, when
 * the fallback's words need more than the row leaves beside the torch, the torch centred above the full-width fallback,
 * `space.gap.md` apart, so they are never cut off.
 */
@Composable
private fun ControlsRow(
    fallback: @Composable () -> Unit,
    torch: @Composable () -> Unit,
) {
    val rowGap = QeetrixSpace.GapSm
    val stackGap = QeetrixSpace.GapMd
    Layout(listOf(fallback, torch), Modifier.fillMaxWidth()) { (fallbacks, torches), constraints ->
        val width = constraints.maxWidth
        val torchPlaced = torches.firstOrNull()?.measure(constraints.copy(minWidth = 0, minHeight = 0))
        val torchWidth = torchPlaced?.width ?: 0
        val words = fallbacks.firstOrNull()
        val gap = if (torchPlaced != null) rowGap.roundToPx() else 0
        if (words == null || words.maxIntrinsicWidth(constraints.maxHeight) + gap + torchWidth <= width) {
            val fallbackPlaced = words?.measure(Constraints.fixedWidth(width - gap - torchWidth))
            val height = maxOf(fallbackPlaced?.height ?: 0, torchPlaced?.height ?: 0)
            layout(width, height) {
                fallbackPlaced?.placeRelative(0, (height - fallbackPlaced.height) / 2)
                val torchX = if (fallbackPlaced == null) (width - torchWidth) / 2 else width - torchWidth
                torchPlaced?.placeRelative(torchX, (height - torchPlaced.height) / 2)
            }
        } else {
            val fallbackPlaced = words.measure(Constraints.fixedWidth(width))
            val below = torchPlaced?.let { it.height + stackGap.roundToPx() } ?: 0
            layout(width, below + fallbackPlaced.height) {
                torchPlaced?.placeRelative((width - torchWidth) / 2, 0)
                fallbackPlaced.placeRelative(0, below)
            }
        }
    }
}

/** 0.1.0's signature without the label, message and layout, kept so apps compiled against it still link. */
@Deprecated("Kept for binary compatibility.", level = DeprecationLevel.HIDDEN)
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
): Unit =
    QeetrixQRScanner(
        state = state,
        modifier = modifier,
        guidance = guidance,
        torch = torch,
        onTorchChange = onTorchChange,
        onEnterKey = onEnterKey,
        onAllowCamera = onAllowCamera,
        onOpenSettings = onOpenSettings,
        enterKeyLabel = stringResource(R.string.qx_scanner_enter_key),
        deniedDescription = stringResource(R.string.qx_scanner_permission_denied),
        controlsLayout = QeetrixScannerControlsLayout.Stacked,
        preview = preview,
    )

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
