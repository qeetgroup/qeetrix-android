package com.qeetgroup.qeetrix.foundation.elevation

import android.graphics.BlurMaskFilter
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.ClipOp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.addOutline
import androidx.compose.ui.graphics.drawOutline
import androidx.compose.ui.graphics.drawscope.ContentDrawScope
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.node.CompositionLocalConsumerModifierNode
import androidx.compose.ui.node.DrawModifierNode
import androidx.compose.ui.node.ModifierNodeElement
import androidx.compose.ui.node.currentValueOf
import androidx.compose.ui.node.invalidateDraw
import androidx.compose.ui.platform.InspectorInfo
import androidx.compose.ui.unit.Dp
import com.qeetgroup.qeetrix.theme.LocalQeetrixIsDark
import com.qeetgroup.qeetrix.tokens.generated.darkLayers
import com.qeetgroup.qeetrix.tokens.generated.lightLayers
import kotlin.math.abs

/**
 * The Qeetrix elevations (`elevation.*`): layered `box-shadow`s that differ by theme. Draw one with
 * [qeetrixElevation]; read its layers with [com.qeetgroup.qeetrix.theme.QeetrixTheme.shadowLayers].
 *
 * @property token The upstream token path, the same string on every platform.
 */
public enum class QeetrixElevation(
    public val token: String,
) {
    Flat("elevation.flat"),
    Inset("elevation.inset"),
    Raised("elevation.raised"),
    Hover("elevation.hover"),
    Overlay("elevation.overlay"),
    Modal("elevation.modal"),
    Subtle("elevation.subtle"),
    Low("elevation.low"),
    Ramp2xs("elevation.ramp.2xs"),
    RampXs("elevation.ramp.xs"),
    RampSm("elevation.ramp.sm"),
    RampMd("elevation.ramp.md"),
    RampLg("elevation.ramp.lg"),
    RampXl("elevation.ramp.xl"),
    ;

    /** The shadow layers in a theme, in CSS order: the first layer is drawn on top. */
    public fun layers(dark: Boolean): List<QeetrixShadowLayer> = if (dark) darkLayers() else lightLayers()
}

/**
 * One `box-shadow` layer, in dp. [blur] is the CSS blur radius: the shadow is a Gaussian blur with a
 * standard deviation of half of it.
 */
@Immutable
public class QeetrixShadowLayer(
    public val inset: Boolean,
    public val offsetX: Dp,
    public val offsetY: Dp,
    public val blur: Dp,
    public val spread: Dp,
    public val color: Color,
) {
    override fun equals(other: Any?): Boolean =
        other is QeetrixShadowLayer && inset == other.inset && offsetX == other.offsetX && offsetY == other.offsetY &&
            blur == other.blur && spread == other.spread && color == other.color

    override fun hashCode(): Int = listOf(inset, offsetX, offsetY, blur, spread, color).hashCode()

    override fun toString(): String =
        "QeetrixShadowLayer(inset=$inset, offsetX=$offsetX, offsetY=$offsetY, blur=$blur, spread=$spread, color=$color)"
}

/**
 * Draws a Qeetrix surface behind this element: the elevation's shadows for the current theme, the
 * [fill], and any inset shadow — the way CSS draws `box-shadow`. Outer layers are offset, spread,
 * blurred with a standard deviation of half the blur radius and drawn only outside the [shape]; inset
 * layers are drawn only inside it, over the fill and under the content. So negative spreads and the
 * 1 dp ring layers render as on the web and iOS, which `Modifier.shadow` cannot do.
 *
 * ```
 * Box(Modifier.qeetrixElevation(QeetrixElevation.Raised, QeetrixShapes.Surface, QeetrixTheme.colors.surfaceElevated))
 * ```
 */
public fun Modifier.qeetrixElevation(
    elevation: QeetrixElevation,
    shape: Shape,
    fill: Color,
): Modifier = this then QeetrixElevationElement(elevation, shape, fill)

private data class QeetrixElevationElement(
    val elevation: QeetrixElevation,
    val shape: Shape,
    val fill: Color,
) : ModifierNodeElement<QeetrixElevationNode>() {
    override fun create() = QeetrixElevationNode(elevation, shape, fill)

    override fun update(node: QeetrixElevationNode) {
        node.elevation = elevation
        node.shape = shape
        node.fill = fill
        node.invalidateDraw()
    }

    override fun InspectorInfo.inspectableProperties() {
        name = "qeetrixElevation"
        properties["elevation"] = elevation
        properties["shape"] = shape
        properties["fill"] = fill
    }
}

private class QeetrixElevationNode(
    var elevation: QeetrixElevation,
    var shape: Shape,
    var fill: Color,
) : Modifier.Node(),
    DrawModifierNode,
    CompositionLocalConsumerModifierNode {
    private val paint = Paint()

    override fun ContentDrawScope.draw() {
        val layers = elevation.layers(dark = currentValueOf(LocalQeetrixIsDark))
        val outline = shape.createOutline(size, layoutDirection, this)
        val shapePath = Path().apply { addOutline(outline) }

        // Outer layers, last CSS layer first so the first ends on top, only outside the shape.
        clipPath(shapePath, ClipOp.Difference) {
            for (layer in layers.asReversed()) {
                if (layer.inset) continue
                val spread = layer.spread.toPx()
                val grown = Size(size.width + 2 * spread, size.height + 2 * spread)
                if (grown.width <= 0f || grown.height <= 0f) continue
                val path = Path().apply { addOutline(shape.createOutline(grown, layoutDirection, this@draw)) }
                drawBlurred(path, Offset(layer.offsetX.toPx() - spread, layer.offsetY.toPx() - spread), layer)
            }
        }
        drawOutline(outline, fill)
        // Inset layers: the area outside the shape (shrunk by the spread), offset and blurred, inside it.
        clipPath(shapePath) {
            for (layer in layers.asReversed()) {
                if (!layer.inset) continue
                val spread = layer.spread.toPx()
                val shrunk = Size(size.width - 2 * spread, size.height - 2 * spread)
                val margin = layer.blur.toPx() * 2 + abs(layer.offsetX.toPx()) + abs(layer.offsetY.toPx()) + abs(spread) + 1f
                val path =
                    Path().apply {
                        fillType = PathFillType.EvenOdd
                        addRect(Rect(-margin, -margin, size.width + margin, size.height + margin))
                        if (shrunk.width > 0f && shrunk.height > 0f) {
                            val inner = Path().apply { addOutline(shape.createOutline(shrunk, layoutDirection, this@draw)) }
                            inner.translate(Offset(spread + layer.offsetX.toPx(), spread + layer.offsetY.toPx()))
                            addPath(inner)
                        }
                    }
                drawBlurred(path, Offset.Zero, layer)
            }
        }
        drawContent()
    }

    private fun DrawScope.drawBlurred(
        path: Path,
        offset: Offset,
        layer: QeetrixShadowLayer,
    ) {
        paint.color = layer.color
        paint.asFrameworkPaint().maskFilter = blurFilter(layer.blur.toPx())
        translate(offset.x, offset.y) {
            drawIntoCanvas { it.drawPath(path, paint) }
        }
    }
}

/**
 * A mask filter whose Gaussian has the CSS standard deviation, blur ÷ 2. Android's BlurMaskFilter takes
 * a Skia "radius", which Skia converts with σ = 0.57735 · radius + 0.5, so the radius is inverted from
 * that; below σ = 0.5 px the blur is under half a pixel and is not drawn.
 */
internal fun blurFilter(cssBlurPx: Float): BlurMaskFilter? {
    val sigma = cssBlurPx / 2
    if (sigma <= 0.5f) return null
    return BlurMaskFilter((sigma - 0.5f) / 0.57735f, BlurMaskFilter.Blur.NORMAL)
}
