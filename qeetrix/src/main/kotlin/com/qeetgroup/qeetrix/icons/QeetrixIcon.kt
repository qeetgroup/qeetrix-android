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
import com.qeetgroup.qeetrix.foundation.sizing.QeetrixIconSize
import com.qeetgroup.qeetrix.foundation.sizing.QeetrixIconStroke
import com.qeetgroup.qeetrix.icons.generated.drawing
import com.qeetgroup.qeetrix.icons.generated.mirrorsInRtl
import com.qeetgroup.qeetrix.icons.generated.upstreamName
import com.qeetgroup.qeetrix.internal.QeetrixIconDrawing
import com.qeetgroup.qeetrix.theme.LocalQeetrixContentColor
import com.qeetgroup.qeetrix.theme.QeetrixTheme

/**
 * The Qeetrix icons available natively. Names are qeetrix-icons' (Lucide's): the representative set
 * Phase 1 pinned plus those the components draw — not the full catalogue, and the same set as qeetrix-ios.
 * Adding one is a pin in `Upstream/manifest.json`.
 */
public enum class QeetrixIconName {
    ArrowLeft,
    Bell,
    Calendar,
    Check,
    ChevronDown,
    CircleAlert,
    CircleCheck,
    CircleX,
    Clock,
    Copy,
    Database,
    Ellipsis,
    EllipsisVertical,
    Eye,
    EyeOff,
    FileText,
    Folder,
    House,
    Inbox,
    Info,
    LoaderCircle,
    Lock,
    Mail,
    Minus,
    Navigation,
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

    /** Whether the drawing mirrors in right-to-left layouts (upstream `directionality: "mirror"`). */
    public val mirrorsInRightToLeft: Boolean get() = mirrorsInRtl

    /** Whether qeetrix-icons draws this icon in [shape] and [variant]. Without it, [QeetrixIcon] draws the outline. */
    public fun hasDrawing(
        shape: QeetrixIconShape,
        variant: QeetrixIconVariant,
    ): Boolean = drawing(shape, variant) != null
}

/** The two drawing shapes qeetrix-icons provides. */
public enum class QeetrixIconShape(
    public val value: String,
) {
    /** Round caps and joins — the default, Lucide's own drawing. */
    Round("round"),

    /** Square caps and mitred joins. */
    Sharp("sharp"),
}

/** Outline (stroked) or filled. */
public enum class QeetrixIconVariant(
    public val value: String,
) {
    Outline("outline"),
    Filled("filled"),
}

/**
 * A Qeetrix icon, drawn from vector paths in [tint] — by default the inherited content colour, as
 * icons follow `currentColor` on the web. Defaults match the web's `<Icon>`: size `md`, stroke
 * `regular`, round outline.
 *
 * Pass a [contentDescription] when the icon carries meaning on its own; TalkBack reads it as an image.
 * Pass null when it is decorative — the text beside it says what it means — and it is hidden from
 * accessibility services. Icons whose meaning has a direction (send, reply, undo, …) mirror in
 * right-to-left layouts.
 *
 * Icons are sized in dp and do not grow with the font scale, as is usual on Android (qeetrix-ios
 * scales them with Dynamic Type, as SF Symbols do).
 *
 * ```
 * QeetrixIcon(QeetrixIconName.Search, contentDescription = null)            // decorative
 * QeetrixIcon(QeetrixIconName.Lock, contentDescription = "Locked", size = QeetrixIconSize.Lg)
 * ```
 *
 * @param stroke The outline stroke width in grid units ([QeetrixIconStroke]); it scales with the icon.
 */
@Composable
public fun QeetrixIcon(
    name: QeetrixIconName,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    size: Dp = QeetrixIconSize.Md,
    stroke: Float = QeetrixIconStroke.Regular,
    shape: QeetrixIconShape = QeetrixIconShape.Round,
    variant: QeetrixIconVariant = QeetrixIconVariant.Outline,
    tint: Color = LocalQeetrixContentColor.current.takeOrElse { QeetrixTheme.colors.textPrimary },
) {
    val drawing = name.resolvedDrawing(shape, variant)
    val mirrors = name.mirrorsInRtl
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
