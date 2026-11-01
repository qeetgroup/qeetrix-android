package com.qeetgroup.qeetrix.components.avatar

import android.graphics.Paint
import android.graphics.Rect
import android.graphics.Typeface
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.AlignmentLine
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.FirstBaseline
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFontFamilyResolver
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontSynthesis
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.qeetgroup.qeetrix.foundation.shape.QeetrixShapes
import com.qeetgroup.qeetrix.foundation.spacing.QeetrixSpace
import com.qeetgroup.qeetrix.internal.labelDirection
import com.qeetgroup.qeetrix.theme.QeetrixTheme
import java.text.BreakIterator
import java.util.Locale
import kotlin.math.roundToInt

/**
 * The sizes of [QeetrixAvatar] (`value` is `@qeetrix/react`'s `size`): fixed steps, not density-scaled, so
 * a person looks the same size in a compact table as in a comfortable form.
 */
public enum class QeetrixAvatarSize(
    public val value: String,
    internal val side: Dp,
) {
    /** 24 dp, initials in `label.sm`. */
    Sm("sm", 24.dp), // .qx-avatar[data-size="sm"] block-size 24px

    /** 32 dp, initials in `label.sm` — the default: the height of two tight lines ([QeetrixAvatarItem]). */
    Md("md", 32.dp), // .qx-avatar block-size 32px

    /** 40 dp, initials in `label.md`. */
    Lg("lg", 40.dp), // .qx-avatar[data-size="lg"] block-size 40px

    /** 48 dp, initials in `label.md`. */
    Xl("xl", 48.dp), // .qx-avatar[data-size="xl"] block-size 48px
}

/** The shapes of [QeetrixAvatar] (`value` is `@qeetrix/react`'s `shape`). */
public enum class QeetrixAvatarShape(
    public val value: String,
) {
    /** A person: a circle (`radius.full`). People are one of Qeetrix's two soft shapes. The default. */
    Circle("circle"),

    /**
     * An organisation, an application or a tenant: a square with the controls' 4 dp corners
     * (`radius.control`), so it never reads as a person.
     */
    Square("square"),
}

/**
 * A person, or an organisation, application or tenant — `@qeetrix/react`'s Avatar: their photo or logo, or
 * their initials on `color.bg.neutral.subtle-pressed`. A person is a [QeetrixAvatarShape.Circle]; anything
 * else is a [QeetrixAvatarShape.Square].
 *
 * - [image] is any `Painter` the app has already loaded (`painterResource`, a decoded bitmap, an image
 *   library's painter), cropped to the shape; the web's `src`. Fetching, caching and failures stay in the
 *   app: pass null while it loads or when it fails, and the initials show, as on the web.
 * - **Initials** are the first grapheme of the first and last words of [name] ("Priya Sharma" is "PS"),
 *   upper-cased for the locale. A company's legal form doesn't count ("Kaveri Foods Private Limited" is
 *   "KF"), and a name left with one word takes its first two ("Acme Pvt Ltd" is "AC"). They are centred on
 *   their capitals' height, not on the line, so they don't sit high in the shape (optical centring).
 * - **Accessibility.** The avatar is one image named by [name]; the initials are not spelled out. When the
 *   name is shown beside it — a member row, a comment's author — pass [decorative] `true`: it is hidden
 *   from accessibility services, so the name is not read twice.
 *
 * The avatar is a fixed-size image, so its initials keep their size at every font scale rather than
 * overflow it.
 *
 * ```
 * QeetrixAvatar("Priya Sharma")
 * QeetrixAvatar("Acme Pvt Ltd", shape = QeetrixAvatarShape.Square, size = QeetrixAvatarSize.Lg, image = logo)
 * ```
 *
 * @param name The person's, organisation's or application's name: the accessible name and the initials.
 */
@Composable
public fun QeetrixAvatar(
    name: String,
    modifier: Modifier = Modifier,
    image: Painter? = null,
    size: QeetrixAvatarSize = QeetrixAvatarSize.Md,
    shape: QeetrixAvatarShape = QeetrixAvatarShape.Circle,
    decorative: Boolean = false,
) {
    val outline: Shape = if (shape == QeetrixAvatarShape.Square) QeetrixShapes.Control else QeetrixShapes.Full
    val locale = LocalConfiguration.current.locales[0]
    val initials = remember(name, locale) { initialsOf(name, locale) }
    Layout(
        content = {
            if (image != null) {
                // .qx-avatar-image: object-fit cover, 100% × 100%
                Image(image, contentDescription = null, modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
            } else if (initials.isNotEmpty()) {
                Initials(initials, size)
            }
        },
        modifier =
            modifier
                .then(
                    if (decorative) {
                        Modifier.clearAndSetSemantics {}
                    } else {
                        Modifier.clearAndSetSemantics {
                            contentDescription = name
                            role = Role.Image
                        }
                    },
                ).size(size.side)
                .clip(outline)
                .background(QeetrixTheme.colors.bgNeutralSubtlePressed), // .qx-avatar background
    ) { measurables, constraints ->
        val side = size.side.roundToPx()
        val placeables = measurables.map { it.measure(constraints.copy(minWidth = 0, minHeight = 0)) }
        layout(side, side) {
            for (placeable in placeables) placeable.placeRelative((side - placeable.width) / 2, (side - placeable.height) / 2)
        }
    }
}

/**
 * The initials, `color.text.default` in `label.sm` (`label.md` at `lg` and `xl`), at a font scale of 1 and
 * centred on their capitals — the web's `text-box: trim-both cap alphabetic`: the box is trimmed to the
 * capitals' height, so centring it centres the letters rather than the line, which keeps room below the
 * baseline for descenders capitals don't have.
 */
@Composable
private fun Initials(
    initials: String,
    size: QeetrixAvatarSize,
) {
    val typography = QeetrixTheme.typography
    val style =
        when (size) {
            QeetrixAvatarSize.Sm, QeetrixAvatarSize.Md -> typography.labelSm

            // .qx-avatar font: label.sm
            QeetrixAvatarSize.Lg, QeetrixAvatarSize.Xl -> typography.labelMd // [data-size="lg"|"xl"] font: label.md
        }.copy(color = QeetrixTheme.colors.textDefault).labelDirection()
    val density = LocalDensity.current
    CompositionLocalProvider(LocalDensity provides Density(density.density, fontScale = 1f)) {
        val capHeight = capHeight(style)
        Layout(
            content = { BasicText(initials, style = style, maxLines = 1, softWrap = false, overflow = TextOverflow.Clip) },
        ) { measurables, constraints ->
            val text = measurables.single().measure(constraints.copy(minWidth = 0, minHeight = 0))
            val baseline = text[FirstBaseline]
            val cap = capHeight?.roundToInt()
            // The box of the capitals: from the cap height above the baseline down to the baseline.
            val top = if (cap != null && baseline != AlignmentLine.Unspecified) baseline - cap else 0
            val height = if (cap != null && baseline != AlignmentLine.Unspecified) cap else text.height
            layout(text.width, height) { text.placeRelative(0, -top) }
        }
    }
}

/** The height of the capitals of [style]'s font at its size, in px, measured from the font's own "H"; null without a font. */
@Composable
private fun capHeight(style: TextStyle): Float? {
    val resolver = LocalFontFamilyResolver.current
    val density = LocalDensity.current
    val typeface =
        resolver
            .resolve(
                style.fontFamily,
                style.fontWeight ?: FontWeight.Normal,
                style.fontStyle ?: FontStyle.Normal,
                style.fontSynthesis ?: FontSynthesis.All,
            ).value as? Typeface ?: return null
    val textSize = with(density) { style.fontSize.toPx() }
    return remember(typeface, textSize) {
        val bounds = Rect()
        Paint()
            .apply {
                this.typeface = typeface
                this.textSize = textSize
            }.getTextBounds("H", 0, 1, bounds)
        (-bounds.top).toFloat()
    }
}

/**
 * A person as lists and menus show them — `@qeetrix/react`'s AvatarItem: their [QeetrixAvatar], their name
 * and an optional second line ([description]: an email, a role, a team). An organisation or application
 * takes [QeetrixAvatarShape.Square].
 *
 * The two lines sit close: the name (`label.md`) on the second line's line height (`body.sm`'s, 16 sp), so
 * together they are as tall as a `md` avatar and a compact row. Both wrap rather than end in an ellipsis, a long
 * email breaking where it must, so neither is ever cut (`overflow-wrap: anywhere`, QTRX-178). The avatar is
 * decorative, since the name is shown; the name and description are read as one element.
 *
 * ```
 * QeetrixAvatarItem("Priya Sharma", description = "priya@acme.in")
 * ```
 */
@Composable
public fun QeetrixAvatarItem(
    name: String,
    modifier: Modifier = Modifier,
    description: String? = null,
    image: Painter? = null,
    size: QeetrixAvatarSize = QeetrixAvatarSize.Md,
    shape: QeetrixAvatarShape = QeetrixAvatarShape.Circle,
) {
    val colors = QeetrixTheme.colors
    val typography = QeetrixTheme.typography
    Row(
        modifier.semantics(mergeDescendants = true) {},
        horizontalArrangement = Arrangement.spacedBy(QeetrixSpace.GapSm), // .qx-avatar-item gap: space.gap.sm
        verticalAlignment = Alignment.CenterVertically, // align-items: center
    ) {
        QeetrixAvatar(name, image = image, size = size, shape = shape, decorative = true)
        Column {
            BasicText(
                name,
                // .qx-avatar-item-name: label.md on body.sm's line height (font-size × line-height = 16).
                style =
                    typography.labelMd
                        .copy(
                            color = colors.textDefault,
                            lineHeight = typography.bodySm.fontSize * typography.bodySm.lineHeight.value,
                        ).labelDirection(alignToLayout = true),
            )
            if (description != null) {
                BasicText(
                    description,
                    // .qx-avatar-item-description
                    style = typography.bodySm.copy(color = colors.textSubtle).labelDirection(alignToLayout = true),
                )
            }
        }
    }
}

/**
 * Legal forms that end a company's name, with or without dots ("Acme Pvt. Ltd." is Acme) — avatar.tsx's
 * `LEGAL_FORMS`: the Indian forms first (Private Limited, LLP, OPC), then the common international ones.
 */
private val LegalForms =
    setOf("pvt", "private", "ltd", "limited", "llp", "opc", "inc", "incorporated", "llc", "plc", "corp", "co", "gmbh", "pte", "pty")

private fun isLegalForm(word: String): Boolean = word.replace(Regex("[.,]"), "").lowercase(Locale.ROOT) in LegalForms

/**
 * avatar.tsx's `initials`: the first grapheme of the first and last words, upper-cased for [locale] ("Priya
 * Sharma" is "PS"). Trailing legal forms don't count ("Kaveri Foods Private Limited" is "KF"); a name left
 * with one word because of them takes its first two graphemes ("Acme Pvt Ltd" is "AC"). Graphemes come
 * from `BreakIterator`, as the web's from `Intl.Segmenter`, so a letter keeps its marks and Indic names stay
 * whole.
 */
internal fun initialsOf(
    name: String,
    locale: Locale,
): String {
    val words = name.trim().split(Regex("\\s+")).filter { it.isNotEmpty() }
    if (words.isEmpty()) return ""
    var kept = words.size
    while (kept > 1 && isLegalForm(words[kept - 1])) kept--

    fun firstGraphemes(
        word: String,
        count: Int = 1,
    ): String {
        val iterator = BreakIterator.getCharacterInstance(locale)
        iterator.setText(word)
        var end = 0
        repeat(count) {
            val next = iterator.next()
            if (next == BreakIterator.DONE) return word.substring(0, end.coerceAtLeast(0)).ifEmpty { word }
            end = next
        }
        return word.substring(0, end)
    }
    val first = words.first()
    if (kept == 1 && kept < words.size) return firstGraphemes(first, 2).uppercase(locale)
    val last = if (kept > 1) firstGraphemes(words[kept - 1]) else ""
    return (firstGraphemes(first) + last).uppercase(locale)
}
