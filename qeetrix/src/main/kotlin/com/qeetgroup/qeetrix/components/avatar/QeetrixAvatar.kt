package com.qeetgroup.qeetrix.components.avatar

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.drawOutline
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import com.qeetgroup.qeetrix.foundation.sizing.QeetrixStroke
import com.qeetgroup.qeetrix.foundation.typography.QeetrixTextRole
import com.qeetgroup.qeetrix.icons.QeetrixIcon
import com.qeetgroup.qeetrix.icons.QeetrixIconName
import com.qeetgroup.qeetrix.internal.FontWeightMedium
import com.qeetgroup.qeetrix.internal.QeetrixFont
import com.qeetgroup.qeetrix.internal.QeetrixWebScale
import com.qeetgroup.qeetrix.internal.qeetrixTextStyle
import com.qeetgroup.qeetrix.theme.QeetrixTheme
import com.qeetgroup.qeetrix.tokens.generated.QeetrixAvatarComponentTokens
import java.text.BreakIterator
import java.util.Locale

/**
 * The sizes of [QeetrixAvatar] (`value` is the web name): fixed steps, not density-scaled — a person
 * should look the same size in a compact table as in a comfortable one.
 */
public enum class QeetrixAvatarSize(
    public val value: String,
    internal val side: Dp,
    internal val badge: Dp,
) {
    /** 20 dp (`size-5`), badge 6 dp. */
    Xs("xs", QeetrixWebScale.spacing(5f), QeetrixWebScale.spacing(1.5f)),

    /** 24 dp (`size-6`), badge 8 dp. */
    Sm("sm", QeetrixWebScale.spacing(6f), QeetrixWebScale.spacing(2f)),

    /** 32 dp (`size-8`), badge 10 dp. The default. */
    Default("default", QeetrixWebScale.spacing(8f), QeetrixWebScale.spacing(2.5f)),

    /** 40 dp (`size-10`), badge 12 dp. */
    Lg("lg", QeetrixWebScale.spacing(10f), QeetrixWebScale.spacing(3f)),

    /** 48 dp (`size-12`), badge 14 dp. */
    Xl("xl", QeetrixWebScale.spacing(12f), QeetrixWebScale.spacing(3.5f)),
}

/** The shapes of [QeetrixAvatar] (`value` is the web name). */
public enum class QeetrixAvatarShape(
    public val value: String,
) {
    /** People. */
    Circle("circle"),

    /** Organisations, workspaces, tenants and apps, so they never read as people. */
    Square("square"),
}

/**
 * A person's (or an organisation's) picture, initials or a neutral icon.
 *
 * - [image] is any `Painter` the app has already loaded (`painterResource`, a loaded bitmap, an image
 *   library's painter). Fetching, caching and resolving avatar URLs stay in the app.
 * - Without an image, the avatar shows up to two initials from [name] — the first letter of the first
 *   and last words, upper-cased for the locale — or, without a name, a neutral person icon. Like the
 *   avatar, the initials keep their size at every font scale.
 * - [contentDescription] defaults to [name]: accessibility services read the avatar as one image called
 *   by it, not as letters spelled out. Pass `null` when the name is already visible beside the avatar
 *   (a comment author, a user row), so it is not read twice.
 * - [badgeLabel] adds a status mark on the bottom-end edge and is appended to the description ("Ada
 *   Lovelace, Online"), so the status never rests on colour alone.
 */
@Composable
public fun QeetrixAvatar(
    name: String?,
    modifier: Modifier = Modifier,
    image: Painter? = null,
    size: QeetrixAvatarSize = QeetrixAvatarSize.Default,
    shape: QeetrixAvatarShape = QeetrixAvatarShape.Circle,
    contentDescription: String? = name,
    badgeLabel: String? = null,
) {
    val outline: Shape =
        when (shape) {
            QeetrixAvatarShape.Circle -> CircleShape

            // rounded-full
            QeetrixAvatarShape.Square -> RoundedCornerShape(percent = (QeetrixAvatarComponentTokens.CornerSquare * 100).toInt())
        }
    val locale = LocalConfiguration.current.locales[0]
    val initials = remember(name, locale) { name?.let { initialsOf(it, locale) }.orEmpty() }
    val description = listOfNotNull(contentDescription, badgeLabel).joinToString(", ").ifEmpty { null }
    val hairline = QeetrixTheme.colors.borderDefault // after:border after:border-border
    val blend = if (QeetrixTheme.isDark) BlendMode.Lighten else BlendMode.Darken // mix-blend-darken / dark:mix-blend-lighten

    Box(
        modifier
            .size(size.side)
            .clearAndSetSemantics {
                if (description != null) {
                    this.contentDescription = description
                    role = Role.Image
                }
            },
    ) {
        Box(
            Modifier
                .fillMaxSize()
                .clip(outline)
                .drawWithContent {
                    drawContent()
                    // A hairline that keeps a pale photo from bleeding into a pale surface; it blends, so it
                    // never reads as a drawn border.
                    val inset = QeetrixStroke.Hairline.toPx()
                    drawOutline(
                        outline.createOutline(this.size, layoutDirection, this),
                        hairline,
                        style = Stroke(inset * 2),
                        blendMode = blend,
                    )
                },
            contentAlignment = Alignment.Center,
        ) {
            if (image != null) {
                Image(image, contentDescription = null, modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop) // object-cover
            } else {
                Fallback(initials, size)
            }
        }
        if (badgeLabel != null) {
            Badge(size, shape)
        }
    }
}

@Composable
private fun Fallback(
    initials: String,
    size: QeetrixAvatarSize,
) {
    val foreground = QeetrixAvatarComponentTokens.FallbackForeground
    Box(
        Modifier.fillMaxSize().background(QeetrixAvatarComponentTokens.FallbackBackground),
        contentAlignment = Alignment.Center,
    ) {
        if (initials.isNotEmpty()) {
            // The avatar is a fixed-size image, so its initials keep their size too rather than grow with the
            // font scale and overflow it (the web scales both together in rem). The name is still read out.
            val density = LocalDensity.current
            CompositionLocalProvider(LocalDensity provides Density(density.density, fontScale = 1f)) {
                BasicText(initials, style = fallbackStyle(size).copy(color = foreground), maxLines = 1, softWrap = false)
            }
        } else {
            // UserRoundIcon, [&>svg]:size-[55%]
            QeetrixIcon(QeetrixIconName.UserRound, contentDescription = null, size = size.side * 0.55f, tint = foreground)
        }
    }
}

/** AvatarBadge: a ring-cut mark in the brand colour on the bottom-end edge. */
@Composable
private fun BoxScope.Badge(
    size: QeetrixAvatarSize,
    shape: QeetrixAvatarShape,
) {
    val ring = if (size == QeetrixAvatarSize.Xs) QeetrixStroke.Hairline else QeetrixStroke.Thin // ring-2, xs: ring-1
    val outset = if (shape == QeetrixAvatarShape.Square) QeetrixWebScale.spacing(0.5f) else Dp(0f) // square: -inset-e-0.5 -bottom-0.5
    Box(
        Modifier
            .align(Alignment.BottomEnd)
            .offset(x = outset, y = outset) // mirrors in right-to-left layouts, as inset-e does
            .size(size.badge + ring * 2)
            .background(QeetrixAvatarComponentTokens.Ring, CircleShape) // ring-(--qx-component-avatar-ring)
            .padding(ring)
            .background(QeetrixAvatarComponentTokens.BadgeBackground, CircleShape),
    )
}

/** font-ui font-medium; text-sm, lg: text-base, xl: text-lg, sm and xs: text-micro. */
private fun fallbackStyle(size: QeetrixAvatarSize): TextStyle =
    when (size) {
        QeetrixAvatarSize.Xs, QeetrixAvatarSize.Sm -> MicroStyle
        QeetrixAvatarSize.Default -> SmStyle
        QeetrixAvatarSize.Lg -> BaseStyle
        QeetrixAvatarSize.Xl -> LgStyle
    }

private val MicroStyle =
    QeetrixTextRole.Micro.spec.let { qeetrixTextStyle(QeetrixFont.Ui, it.size, it.lineHeight, FontWeightMedium) }
private val SmStyle = qeetrixTextStyle(QeetrixFont.Ui, QeetrixWebScale.TextSm, FontWeightMedium)
private val BaseStyle = qeetrixTextStyle(QeetrixFont.Ui, QeetrixWebScale.TextBase, FontWeightMedium)
private val LgStyle = qeetrixTextStyle(QeetrixFont.Ui, QeetrixWebScale.TextLg, FontWeightMedium)

/**
 * Up to two initials from a display name: the first character (grapheme) of the first and last words,
 * upper-cased for [locale]. A single word yields a single initial ("Priya" is "P"); scripts without case
 * keep their first character. qeetrix-ui's `initialsOf` rule, taken by grapheme cluster rather than code
 * point so combining marks stay with their letter (as qeetrix-ios does).
 */
internal fun initialsOf(
    name: String,
    locale: Locale,
): String {
    val words = name.trim().split(Regex("\\s+")).filter { it.isNotEmpty() }
    if (words.isEmpty()) return ""

    fun firstGrapheme(word: String): String {
        val iterator = BreakIterator.getCharacterInstance(locale)
        iterator.setText(word)
        val end = iterator.next()
        return if (end == BreakIterator.DONE) word else word.substring(0, end)
    }
    val first = firstGrapheme(words.first())
    val last = if (words.size > 1) firstGrapheme(words.last()) else ""
    return (first + last).uppercase(locale)
}
