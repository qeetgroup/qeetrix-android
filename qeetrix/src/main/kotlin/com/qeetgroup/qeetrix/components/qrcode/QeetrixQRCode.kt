package com.qeetgroup.qeetrix.components.qrcode

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.qeetgroup.qeetrix.foundation.shape.QeetrixRadius
import com.qeetgroup.qeetrix.foundation.sizing.QeetrixBorder
import com.qeetgroup.qeetrix.tokens.generated.QeetrixLightColors
import kotlin.math.floor

/** How much of a [QeetrixQRCode] can be lost and still read (`value` is `@qeetrix/react`'s QRCodeLevel). */
public enum class QeetrixQRCodeLevel(
    public val value: String,
) {
    /** 7 %. */
    L("L"),

    /** 15 %: the default. */
    M("M"),

    /** 25 %. */
    Q("Q"),

    /** 30 %: denser, for a code that may be printed or scratched. */
    H("H"),
}

/**
 * A QR code a phone camera can read — `@qeetrix/react`'s QRCode (07-components): an `otpauth://` link to add an
 * account, a UPI payment link. Qeetrix's own encoder, the web's step for step, so a value gives the same modules on both.
 *
 * - **Always dark on white**, whatever the colour scheme: the light `color.surface.default` tile with a 1 dp
 *   `color.border.subtle` edge inside it and `radius.control` corners, the modules in the light `color.text.default`
 *   (qr-code.css: `color-scheme: light`). The standard's quiet zone of four modules is inside the tile, and the
 *   modules are whole pixels where the size allows, so the edges stay crisp.
 * - **[label]** is what it is for, read in place of the image: "QR code to add Acme to your authenticator app".
 * - Pair it with the same thing as text for anyone who can't scan — the setup key, the UPI ID — in a
 *   [QeetrixCopyField][com.qeetgroup.qeetrix.components.copy.QeetrixCopyField].
 * - [level] trades density for damage it survives (default `M`). A [value] too long for the level throws
 *   [IllegalArgumentException], as the web throws a RangeError.
 *
 * ```
 * QeetrixQRCode(otpauthUri, label = "QR code to add Acme to your authenticator app")
 * QeetrixCopyField("Setup key", setupKey, sensitive = true)
 * ```
 */
@Composable
public fun QeetrixQRCode(
    value: String,
    label: String,
    modifier: Modifier = Modifier,
    level: QeetrixQRCodeLevel = QeetrixQRCodeLevel.M,
    size: Dp = 160.dp,
) {
    val modules = remember(value, level) { QeetrixQREncoder.encode(value, level) }
    val colors = QeetrixLightColors
    Canvas(
        modifier.size(size).semantics {
            role = Role.Image
            contentDescription = label
        },
    ) {
        val count = modules.size
        val radius = QeetrixRadius.Control.toPx() // border-radius: radius.control
        drawRoundRect(
            colors.surfaceDefault,
            cornerRadius =
                androidx.compose.ui.geometry
                    .CornerRadius(radius),
        )
        // viewBox -4 -4 (count + 8): four modules of quiet zone on each side; whole pixels per module where they fit.
        val exact = this.size.width / (count + QUIET_ZONE * 2)
        val module = if (exact >= 1f) floor(exact) else exact
        val origin = (this.size.width - module * count) / 2
        for (y in 0 until count) {
            val row = modules[y]
            var x = 0
            while (x < count) {
                if (!row[x]) {
                    x++
                    continue
                }
                val start = x
                while (x + 1 < count && row[x + 1]) x++
                drawRect(
                    colors.textDefault,
                    topLeft = Offset(origin + start * module, origin + y * module),
                    size = Size((x - start + 1) * module, module),
                )
                x++
            }
        }
        // box-shadow: inset 0 0 0 1px border.subtle, over the tile's edge.
        val edge = QeetrixBorder.WidthDefault.toPx()
        drawRoundRect(
            colors.borderSubtle,
            topLeft = Offset(edge / 2, edge / 2),
            size = Size(this.size.width - edge, this.size.height - edge),
            cornerRadius =
                androidx.compose.ui.geometry
                    .CornerRadius(radius),
            style = Stroke(edge),
        )
    }
}

/** The standard's quiet zone, in modules on each side. */
private const val QUIET_ZONE = 4
