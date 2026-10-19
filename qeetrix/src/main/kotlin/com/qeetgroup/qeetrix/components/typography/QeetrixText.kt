package com.qeetgroup.qeetrix.components.typography

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.takeOrElse
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.LineBreak
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.em
import com.qeetgroup.qeetrix.foundation.typography.QeetrixTextRole
import com.qeetgroup.qeetrix.internal.FontWeightSemibold
import com.qeetgroup.qeetrix.internal.QeetrixFont
import com.qeetgroup.qeetrix.internal.QeetrixWebScale
import com.qeetgroup.qeetrix.internal.qeetrixTextStyle
import com.qeetgroup.qeetrix.internal.windowAtLeast
import com.qeetgroup.qeetrix.theme.LocalQeetrixContentColor
import com.qeetgroup.qeetrix.theme.QeetrixTheme
import com.qeetgroup.qeetrix.tokens.generated.QeetrixTypographyComponentTokens

/**
 * The styles of [QeetrixText]: the web Typography component's variants, by the same names (`value`).
 *
 * Headings follow the web's application scale: below the web's `sm` breakpoint (a window narrower than
 * 640 dp — every phone in portrait) h1, h2 and h3 are 24, 20 and 18 sp; from 640 dp they are 32, 24 and
 * 20. h4 is 16 sp at every width. There is no display or hero variant, as on the web.
 */
public enum class QeetrixTextVariant(
    public val value: String,
) {
    /** A page title. A heading for accessibility services. */
    H1("h1"),

    /** A section heading. */
    H2("h2"),

    /** A group heading. */
    H3("h3"),

    /** A block heading. */
    H4("h4"),

    /** Body copy, in the body role. The default. */
    P("p"),

    /** A quotation: body copy in the tertiary colour behind a 2 dp rule on the leading edge. */
    Blockquote("blockquote"),

    /** An introductory paragraph: 16 sp with relaxed leading, in the secondary colour. */
    Lead("lead"),

    /** Emphasised copy: 18 sp semibold. */
    Large("large"),

    /** An interface label, in the label role. */
    Small("small"),

    /** De-emphasised body copy, in the tertiary colour. */
    Muted("muted"),

    /** Code in Fira Code on a sunken chip, 0.875 × the body size. */
    InlineCode("inlineCode"),
    ;

    /** True for [H1]–[H4], which accessibility services announce as headings. */
    public val isHeading: Boolean get() = this == H1 || this == H2 || this == H3 || this == H4
}

/**
 * A piece of copy in a Qeetrix text style: the font, size, line height, colour and heading semantics of
 * a [QeetrixTextVariant]. Sizes are in sp, so text follows the user's font size; it wraps rather than
 * truncates unless [maxLines] and [overflow] say otherwise.
 *
 * ```
 * QeetrixText("Billing", variant = QeetrixTextVariant.H2)
 * QeetrixText("Invoices are sent on the first of each month.", variant = QeetrixTextVariant.Muted)
 * ```
 *
 * @param color The text colour. By default the inherited content colour ([LocalQeetrixContentColor]) —
 *   `textPrimary` on a Qeetrix surface — or the variant's own colour for [QeetrixTextVariant.Lead],
 *   [QeetrixTextVariant.Muted] and [QeetrixTextVariant.Blockquote]. Pass a `QeetrixTheme.colors` role, never
 *   a literal.
 */
@Composable
public fun QeetrixText(
    text: String,
    modifier: Modifier = Modifier,
    variant: QeetrixTextVariant = QeetrixTextVariant.P,
    color: Color = Color.Unspecified,
    textAlign: TextAlign = TextAlign.Unspecified,
    overflow: TextOverflow = TextOverflow.Clip,
    softWrap: Boolean = true,
    maxLines: Int = Int.MAX_VALUE,
    minLines: Int = 1,
    onTextLayout: ((TextLayoutResult) -> Unit)? = null,
) {
    QeetrixText(AnnotatedString(text), modifier, variant, color, textAlign, overflow, softWrap, maxLines, minLines, onTextLayout)
}

/** [QeetrixText] for styled text — links, emphasis, spans in another colour. */
@Composable
public fun QeetrixText(
    text: AnnotatedString,
    modifier: Modifier = Modifier,
    variant: QeetrixTextVariant = QeetrixTextVariant.P,
    color: Color = Color.Unspecified,
    textAlign: TextAlign = TextAlign.Unspecified,
    overflow: TextOverflow = TextOverflow.Clip,
    softWrap: Boolean = true,
    maxLines: Int = Int.MAX_VALUE,
    minLines: Int = 1,
    onTextLayout: ((TextLayoutResult) -> Unit)? = null,
) {
    val style = QeetrixTextStyles.style(variant, wide = windowAtLeast(QeetrixWebScale.BreakpointSm))
    val resolvedColor = color.takeOrElse { variantColor(variant) }
    BasicText(
        text = text,
        modifier =
            modifier
                .textDecoration(variant, QeetrixTheme.colors.borderStrong, QeetrixTypographyComponentTokens.CodeBackground)
                .then(if (variant.isHeading) Modifier.semantics { heading() } else Modifier),
        style = style.copy(color = resolvedColor, textAlign = textAlign),
        onTextLayout = onTextLayout,
        overflow = overflow,
        softWrap = softWrap,
        maxLines = maxLines,
        minLines = minLines,
    )
}

/** The variant's own colour, else the inherited content colour. */
@Composable
@ReadOnlyComposable
private fun variantColor(variant: QeetrixTextVariant): Color =
    when (variant) {
        QeetrixTextVariant.Lead -> QeetrixTheme.colors.textSecondary

        // text-(--qx-color-text-secondary)
        QeetrixTextVariant.Muted, QeetrixTextVariant.Blockquote -> QeetrixTheme.colors.textTertiary

        // text-muted-foreground
        else -> LocalQeetrixContentColor.current.takeOrElse { QeetrixTheme.colors.textPrimary }
    }

/**
 * The blockquote's leading rule (in [rule]) and the inline-code chip (on [codeBackground]); other variants
 * draw nothing extra.
 */
private fun Modifier.textDecoration(
    variant: QeetrixTextVariant,
    rule: Color,
    codeBackground: Color,
): Modifier =
    when (variant) {
        // border-s-2 border-border-strong ps-4
        QeetrixTextVariant.Blockquote -> {
            drawBehind {
                val width = QeetrixWebScale.spacing(0.5f).toPx()
                val x = if (layoutDirection == LayoutDirection.Ltr) width / 2 else size.width - width / 2
                drawLine(rule, Offset(x, 0f), Offset(x, size.height), strokeWidth = width)
            }.padding(start = QeetrixWebScale.spacing(4f))
        }

        QeetrixTextVariant.InlineCode -> {
            background(codeBackground, RoundedCornerShape(QeetrixTypographyComponentTokens.CodeCorner)).padding(
                horizontal = QeetrixTypographyComponentTokens.CodePaddingInline,
                vertical = QeetrixTypographyComponentTokens.CodePaddingBlock,
            )
        }

        else -> {
            this
        }
    }

/**
 * The web Typography classes for each variant, at mobile width and from the `sm` breakpoint up. Built
 * once: text styles hold font families, which Compose caches by identity.
 */
internal object QeetrixTextStyles {
    private val title = QeetrixTextRole.Title.spec
    private val headingRole = QeetrixTextRole.Heading.spec
    private val body = QeetrixTextRole.Body.spec
    private val label = QeetrixTextRole.Label.spec

    private fun heading(
        size: Float,
        lineHeight: Float,
        letterSpacing: Float,
    ): TextStyle =
        qeetrixTextStyle(QeetrixFont.Heading, size, lineHeight, FontWeightSemibold) // font-heading font-semibold
            .copy(letterSpacing = letterSpacing.em, lineBreak = LineBreak.Heading) // text-balance

    // h1: text-2xl/(title line height) → sm:text-title, title tracking.
    private val h1 = heading(QeetrixWebScale.Text2xl.size, title.lineHeight, title.letterSpacing)
    private val h1Wide = heading(title.size, title.lineHeight, title.letterSpacing)

    // h2: text-xl/(heading line height) → sm:text-2xl, heading tracking.
    private val h2 = heading(QeetrixWebScale.TextXl.size, headingRole.lineHeight, headingRole.letterSpacing)
    private val h2Wide = heading(QeetrixWebScale.Text2xl.size, headingRole.lineHeight, headingRole.letterSpacing)

    // h3: text-lg/(heading line height) → sm:text-heading.
    private val h3 = heading(QeetrixWebScale.TextLg.size, headingRole.lineHeight, headingRole.letterSpacing)
    private val h3Wide = heading(headingRole.size, headingRole.lineHeight, headingRole.letterSpacing)

    // h4: text-base/snug.
    private val h4 = heading(QeetrixWebScale.TextBase.size, QeetrixWebScale.LEADING_SNUG, headingRole.letterSpacing)

    // p, muted, blockquote: font-sans text-body, text-pretty.
    private val paragraph = qeetrixTextStyle(QeetrixFont.Sans, body.size, body.lineHeight).copy(lineBreak = LineBreak.Paragraph)

    // lead: font-sans text-base/relaxed.
    private val lead =
        qeetrixTextStyle(
            QeetrixFont.Sans,
            QeetrixWebScale.TextBase.size,
            QeetrixWebScale.LEADING_RELAXED,
        ).copy(lineBreak = LineBreak.Paragraph)

    // large: font-sans text-lg/snug font-semibold.
    private val large = qeetrixTextStyle(QeetrixFont.Sans, QeetrixWebScale.TextLg.size, QeetrixWebScale.LEADING_SNUG, FontWeightSemibold)

    // small: font-ui text-label font-medium.
    private val small = qeetrixTextStyle(QeetrixFont.Ui, label.size, label.lineHeight, label.weight)

    // inlineCode: font-mono, 0.875em of the body text it sits in, font-normal.
    private val inlineCode =
        qeetrixTextStyle(
            QeetrixFont.Mono,
            body.size * QeetrixTypographyComponentTokens.CodeFontSize.value,
            body.lineHeight / QeetrixTypographyComponentTokens.CodeFontSize.value,
        )

    fun style(
        variant: QeetrixTextVariant,
        wide: Boolean,
    ): TextStyle =
        when (variant) {
            QeetrixTextVariant.H1 -> if (wide) h1Wide else h1
            QeetrixTextVariant.H2 -> if (wide) h2Wide else h2
            QeetrixTextVariant.H3 -> if (wide) h3Wide else h3
            QeetrixTextVariant.H4 -> h4
            QeetrixTextVariant.P, QeetrixTextVariant.Muted, QeetrixTextVariant.Blockquote -> paragraph
            QeetrixTextVariant.Lead -> lead
            QeetrixTextVariant.Large -> large
            QeetrixTextVariant.Small -> small
            QeetrixTextVariant.InlineCode -> inlineCode
        }
}
