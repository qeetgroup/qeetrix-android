package com.qeetgroup.qeetrix.components.typography

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withLink
import com.qeetgroup.qeetrix.theme.QeetrixTheme

/** A link's colour (`value` is `@qeetrix/react`'s Link `tone`). */
public enum class QeetrixLinkTone(
    public val value: String,
) {
    /** `color.text.link`, and `color.text.link-hover` while pressed, hovered or focused. The default. */
    Default("default"),

    /** `color.text.subtle`, and `color.text.default` while pressed, hovered or focused: a quiet link in a footnote. */
    Subtle("subtle"),

    /** The surrounding text's colour, underlined. */
    Inherit("inherit"),
}

/**
 * A link inside text — `@qeetrix/react`'s Link — to put in a [QeetrixText]'s `AnnotatedString` with `withLink`, so it
 * flows and wraps with the sentence:
 *
 * ```
 * val link = qeetrixLink(onClick = ::linkThisPhone)
 * QeetrixText(buildAnnotatedString {
 *     append("Using Qeet ID? ")
 *     withLink(link) { append("Link this phone") }
 * }, size = QeetrixTextSize.Sm, tone = QeetrixTextTone.Subtle)
 * ```
 *
 * - **Look** (link.css): [tone]'s colour, underlined, in the sentence's own font and weight; pressed, hovered or focused
 *   it takes the tone's active colour, and focused the `color.bg.neutral.subtle-hover` tint behind it too.
 * - **Behaviour** is Android's own for links in text: each link is focusable with a keyboard, a tap or Enter runs
 *   [onClick], and accessibility services offer it as a link with its words. Write words that say where it goes.
 * - It follows the text's direction and font scale.
 */
@Composable
public fun qeetrixLink(
    onClick: () -> Unit,
    tone: QeetrixLinkTone = QeetrixLinkTone.Default,
): LinkAnnotation {
    val colors = QeetrixTheme.colors
    val latest by rememberUpdatedState(onClick)
    return remember(colors, tone) {
        // .qx-link: color.text.link, underlined; :hover color.text.link-hover; [data-tone] subtle and inherit.
        val (rest, active) =
            when (tone) {
                QeetrixLinkTone.Default -> colors.textLink to colors.textLinkHover
                QeetrixLinkTone.Subtle -> colors.textSubtle to colors.textDefault
                QeetrixLinkTone.Inherit -> Color.Unspecified to Color.Unspecified
            }
        LinkAnnotation.Clickable(
            tag = LINK_TAG,
            styles =
                TextLinkStyles(
                    style = SpanStyle(color = rest, textDecoration = TextDecoration.Underline),
                    focusedStyle = SpanStyle(color = active, background = colors.bgNeutralSubtleHover),
                    hoveredStyle = SpanStyle(color = active),
                    pressedStyle = SpanStyle(color = active),
                ),
        ) { latest() }
    }
}

/**
 * A link on its own — "Forgot your password?" under a field, a footnote's last words — drawn as [QeetrixText] at
 * [size] with the whole [text] one [qeetrixLink]. Inside a sentence, put a [qeetrixLink] in the sentence's text instead.
 */
@Composable
public fun QeetrixLink(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    tone: QeetrixLinkTone = QeetrixLinkTone.Default,
    size: QeetrixTextSize = QeetrixTextSize.Md,
) {
    val link = qeetrixLink(onClick, tone)
    QeetrixText(remember(text, link) { buildAnnotatedString { withLink(link) { append(text) } } }, modifier, size = size)
}

/** The tag of every Qeetrix link annotation. */
internal const val LINK_TAG = "qeetrix-link"
