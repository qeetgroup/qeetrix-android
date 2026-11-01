package com.qeetgroup.qeetrix.icons

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.takeOrElse
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.qeetgroup.qeetrix.foundation.sizing.QeetrixIconStroke
import com.qeetgroup.qeetrix.foundation.sizing.QeetrixSize
import com.qeetgroup.qeetrix.icons.generated.drawing
import com.qeetgroup.qeetrix.icons.generated.mirrorsInRtl
import com.qeetgroup.qeetrix.icons.generated.upstreamName
import com.qeetgroup.qeetrix.internal.QeetrixIconDrawing
import com.qeetgroup.qeetrix.theme.LocalQeetrixContentColor
import com.qeetgroup.qeetrix.theme.QeetrixTheme

/**
 * The Qeetrix icons available natively. Names are `@qeetrix/icons` 2.1's (Lucide's): the icons the
 * components and the sample draw — not the full catalogue, and the same set as qeetrix-ios. Adding one
 * is a pin in `Upstream/manifest.json`.
 */
public enum class QeetrixIconName {
    ArrowBigUp,
    ArrowLeft,
    ArrowRight,
    Bell,
    Calendar,
    Camera,
    CameraOff,
    Check,
    ChevronDown,
    ChevronLeft,
    ChevronRight,
    CircleAlert,
    CircleCheck,
    CircleDashed,
    CircleQuestionMark,
    CircleX,
    Clock,
    CloudUpload,
    ContactRound,
    Copy,
    Ellipsis,
    EllipsisVertical,
    Eye,
    EyeOff,
    File,
    FileArchive,
    FileCode,
    FileImage,
    FileSpreadsheet,
    FileText,
    Flashlight,
    FlashlightOff,
    Folder,
    House,
    Image,
    Inbox,
    Info,
    Keyboard,
    Lock,
    Mail,
    MapPin,
    Menu,
    Minus,
    Navigation,
    Paperclip,
    Pencil,
    Plus,
    RefreshCw,
    Search,
    SearchX,
    Send,
    Server,
    Settings,
    Share2,
    Trash,
    TriangleAlert,
    UserRound,
    Users,
    X,
    ;

    /** The upstream name (`send`), the same string on every platform. */
    public val value: String get() = upstreamName

    /**
     * Whether `@qeetrix/icons` says this drawing points a reading direction (`directionality: "mirror"`: send, undo,
     * log-in, …), so it mirrors in right-to-left layouts. Physical arrows and chevrons ([ArrowLeft], [ChevronRight], …)
     * never do by themselves: the place they are used decides (QTRX-200), as the web's `mirrorInRtl()` does —
     * [QeetrixIcon]'s `mirrorInRightToLeft`, and Button's `iconMirrorsInRightToLeft` for a navigation arrow.
     */
    public val mirrorsInRightToLeft: Boolean get() = mirrorsInRtl

    /** Whether `@qeetrix/icons` draws this icon in [shape] and [variant]. Without it, [QeetrixIcon] draws the outline. */
    public fun hasDrawing(
        shape: QeetrixIconShape,
        variant: QeetrixIconVariant,
    ): Boolean = drawing(shape, variant) != null
}

/**
 * The two drawing shapes `@qeetrix/icons` provides. Qeetrix draws [Sharp] on controls — buttons,
 * fields, menus — where it matches the square corners, and [Round] in navigation (design rules §16).
 */
public enum class QeetrixIconShape(
    public val value: String,
) {
    /** Round caps and joins — Lucide's own drawing; for navigation. */
    Round("round"),

    /** Square caps, mitred joins and squared details — the Qeetrix default. */
    Sharp("sharp"),
}

/** Outline (stroked), the default, or filled — only for a selected or active state (design rules §16). */
public enum class QeetrixIconVariant(
    public val value: String,
) {
    Outline("outline"),
    Filled("filled"),
}

/**
 * A Qeetrix icon, drawn from vector paths in [tint] — by default the inherited content colour, as
 * icons follow `currentColor` on the web. Defaults follow the design rules (§16): the sharp outline,
 * 20 dp standalone (`size.icon.md`; 16 dp inside controls, 24 dp in empty states), stroke 2 — 1.5 at
 * the compact density.
 *
 * Pass a [contentDescription] when the icon carries meaning on its own; TalkBack reads it as an image.
 * Pass null when it is decorative — the text beside it says what it means — and it is hidden from
 * accessibility services. Icons whose `@qeetrix/icons` manifest says they point a reading direction
 * (send, log-out, …) mirror in right-to-left layouts; [mirrorInRightToLeft] decides for any other — `true` for an arrow
 * or chevron that means "back" or "next", `false` (the default) for one that means a physical direction.
 *
 * Icons are sized in dp and do not grow with the font scale, as is usual on Android (qeetrix-ios
 * scales them with Dynamic Type, as SF Symbols do).
 *
 * ```
 * QeetrixIcon(QeetrixIconName.Search, contentDescription = null)            // decorative
 * QeetrixIcon(QeetrixIconName.Lock, contentDescription = "Locked", size = QeetrixSize.IconLg)
 * ```
 *
 * @param stroke The outline stroke width in grid units ([QeetrixIconStroke]); it scales with the icon.
 */
@Composable
public fun QeetrixIcon(
    name: QeetrixIconName,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    size: Dp = QeetrixSize.IconMd,
    stroke: Float = QeetrixTheme.density.iconStroke,
    shape: QeetrixIconShape = QeetrixIconShape.Sharp,
    variant: QeetrixIconVariant = QeetrixIconVariant.Outline,
    tint: Color = LocalQeetrixContentColor.current.takeOrElse { QeetrixTheme.colors.iconDefault },
    mirrorInRightToLeft: Boolean = name.mirrorsInRightToLeft,
) {
    val drawing = name.resolvedDrawing(shape, variant)
    val mirrors = mirrorInRightToLeft
    val vector = remember(drawing, stroke, mirrors) { drawing.toImageVector(stroke, mirrors) }
    Image(
        painter = rememberVectorPainter(vector),
        contentDescription = contentDescription,
        modifier = modifier.size(size),
        colorFilter = ColorFilter.tint(tint),
    )
}

/** The requested drawing, else the same shape's outline, else the round outline (which every icon has). */
internal fun QeetrixIconName.resolvedDrawing(
    shape: QeetrixIconShape,
    variant: QeetrixIconVariant,
): QeetrixIconDrawing =
    drawing(shape, variant)
        ?: drawing(shape, QeetrixIconVariant.Outline)
        ?: checkNotNull(drawing(QeetrixIconShape.Round, QeetrixIconVariant.Outline)) { "$value has no round outline drawing." }

/** The drawing as a 24 × 24 vector, painted black and recoloured by the icon's tint. */
internal fun QeetrixIconDrawing.toImageVector(
    stroke: Float,
    mirrors: Boolean,
): ImageVector {
    val builder =
        ImageVector.Builder(
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f,
            autoMirror = mirrors,
        )
    for (element in elements) {
        builder.addPath(
            pathData = element.nodes,
            pathFillType = if (element.evenOdd) PathFillType.EvenOdd else PathFillType.NonZero,
            fill = if (element.fill) SolidColor(Color.Black) else null,
            stroke = if (element.stroke) SolidColor(Color.Black) else null,
            strokeLineWidth = stroke,
            strokeLineCap = element.cap,
            strokeLineJoin = element.join,
            strokeLineMiter = miterLimit,
        )
    }
    return builder.build()
}
