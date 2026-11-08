package com.qeetgroup.qeetrix.components.typography

import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.takeOrElse
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import com.qeetgroup.qeetrix.foundation.color.QeetrixColors
import com.qeetgroup.qeetrix.foundation.typography.QeetrixTextRole
import com.qeetgroup.qeetrix.foundation.typography.QeetrixTypography
import com.qeetgroup.qeetrix.internal.labelDirection
import com.qeetgroup.qeetrix.theme.LocalQeetrixContentColor
import com.qeetgroup.qeetrix.theme.QeetrixTheme
import com.qeetgroup.qeetrix.tokens.generated.QeetrixFontFamilyTokens
import com.qeetgroup.qeetrix.internal.QeetrixTextWeight as RoleWeight

/** The body sizes of [QeetrixText] (`value` is `@qeetrix/react`'s `size`). */
public enum class QeetrixTextSize(
    public val value: String,
    internal val role: QeetrixTextRole,
) {
    /** `body.sm`, 12 / 16. */
    Sm("sm", QeetrixTextRole.BodySm),

    /** `body.md`, 14 / 20 — the default. */
    Md("md", QeetrixTextRole.BodyMd),

    /** `body.lg`, 16 / 24. */
    Lg("lg", QeetrixTextRole.BodyLg),
}

/** The weights of [QeetrixText] (`value` is `@qeetrix/react`'s `weight`): each a role's own weight. */
public enum class QeetrixTextWeight(
    public val value: String,
) {
    /** 400, `body.md`'s. The default. */
    Regular("regular"),

    /** 500, `label.md`'s. */
    Medium("medium"),

    /** 600, `title.md`'s. */
    Semibold("semibold"),
}

/**
 * Text colours from the tokens (`value` is `@qeetrix/react`'s `tone`). Pass one to [QeetrixText] or
 * [QeetrixHeading]; without one, text inherits the content colour.
 */
public enum class QeetrixTextTone(
    public val value: String,
) {
    /** `color.text.default` */
    Default("default"),

    /** `color.text.subtle` — quieter text that must still be read. */
    Subtle("subtle"),

    /** `color.text.subtlest` */
    Subtlest("subtlest"),

    /**
     * `color.text.disabled` — only for text inside an inactive control. It is exempt from contrast there
     * and unreadable anywhere else (2.48:1 in light); quieter text that must be read is [Subtle].
     */
    Disabled("disabled"),

    /** `color.text.inverse` — on `color.surface.inverse`. */
    Inverse("inverse"),

    /** `color.text.brand` */
    Brand("brand"),

    /** `color.text.danger` */
    Danger("danger"),

    /** `color.text.success` */
    Success("success"),

    /** `color.text.warning` */
    Warning("warning"),

    /** `color.text.info` */
    Info("info"),
    ;

    internal fun color(colors: QeetrixColors): Color =
        when (this) {
            Default -> colors.textDefault
            Subtle -> colors.textSubtle
            Subtlest -> colors.textSubtlest
            Disabled -> colors.textDisabled
            Inverse -> colors.textInverse
            Brand -> colors.textBrand
            Danger -> colors.textDanger
            Success -> colors.textSuccess
            Warning -> colors.textWarning
            Info -> colors.textInfo
        }
}

/**
 * Running text, labels and inline code on the body type roles — `@qeetrix/react`'s Text. Sizes are in sp,
 * so text follows the user's font size; it wraps unless [maxLines] limits it, and then ends in an
 * ellipsis (the web's `truncate`).
 *
 * ```
 * QeetrixText("Invoices are sent on the first of each month.", tone = QeetrixTextTone.Subtle)
 * QeetrixText("inv_2K9", mono = true, size = QeetrixTextSize.Sm)
 * ```
 *
 * For headings use [QeetrixHeading]; Qeetrix has no uppercase text except a group label
 * (`QeetrixTheme.typography.labelCaps`).
 *
 * @param tone The colour; null inherits the content colour ([LocalQeetrixContentColor]), as the web does.
 * @param mono Fira Code at the same size, for code, IDs and keys.
 */
@Composable
public fun QeetrixText(
    text: String,
    modifier: Modifier = Modifier,
    size: QeetrixTextSize = QeetrixTextSize.Md,
    weight: QeetrixTextWeight = QeetrixTextWeight.Regular,
    tone: QeetrixTextTone? = null,
    mono: Boolean = false,
    textAlign: TextAlign = TextAlign.Unspecified,
    maxLines: Int = Int.MAX_VALUE,
    minLines: Int = 1,
    onTextLayout: ((TextLayoutResult) -> Unit)? = null,
) {
    QeetrixText(AnnotatedString(text), modifier, size, weight, tone, mono, textAlign, maxLines, minLines, onTextLayout)
}

/** [QeetrixText] for styled text — links, emphasis, spans in another colour. */
@Composable
public fun QeetrixText(
    text: AnnotatedString,
    modifier: Modifier = Modifier,
    size: QeetrixTextSize = QeetrixTextSize.Md,
    weight: QeetrixTextWeight = QeetrixTextWeight.Regular,
    tone: QeetrixTextTone? = null,
    mono: Boolean = false,
    textAlign: TextAlign = TextAlign.Unspecified,
    maxLines: Int = Int.MAX_VALUE,
    minLines: Int = 1,
    onTextLayout: ((TextLayoutResult) -> Unit)? = null,
) {
    // A paragraph takes its direction from its own text and its alignment from the layout, as labels do: in
    // a right-to-left screen an English sentence keeps its full stop at its end and still lines up with its neighbours.
    val style = textStyle(size, weight, mono).copy(textAlign = textAlign).labelDirection(alignToLayout = true)
    BasicText(
        text = text,
        modifier = modifier,
        style = style.copy(color = toneColor(tone)),
        onTextLayout = onTextLayout,
        overflow = if (maxLines == Int.MAX_VALUE) TextOverflow.Clip else TextOverflow.Ellipsis,
        maxLines = maxLines,
        minLines = minLines,
    )
}

/** The look of a [QeetrixHeading] (`value` is `@qeetrix/react`'s `size`): a display, heading or title role. */
public enum class QeetrixHeadingSize(
    public val value: String,
    internal val role: QeetrixTextRole,
) {
    /** `display.lg` — welcoming screens only (design rules §17). */
    DisplayLg("display-lg", QeetrixTextRole.DisplayLg),

    /** `display.md` */
    DisplayMd("display-md", QeetrixTextRole.DisplayMd),

    /** `display.sm` */
    DisplaySm("display-sm", QeetrixTextRole.DisplaySm),

    /** `heading.lg` — level 1's default. */
    HeadingLg("heading-lg", QeetrixTextRole.HeadingLg),

    /** `heading.md` — level 2's default. */
    HeadingMd("heading-md", QeetrixTextRole.HeadingMd),

    /** `heading.sm` — level 3's default, and a screen title. */
    HeadingSm("heading-sm", QeetrixTextRole.HeadingSm),

    /** `title.lg` — level 4's default. */
    TitleLg("title-lg", QeetrixTextRole.TitleLg),

    /** `title.md` — level 5's default; a dialog title. */
    TitleMd("title-md", QeetrixTextRole.TitleMd),

    /** `title.sm` — level 6's default; a card title. */
    TitleSm("title-sm", QeetrixTextRole.TitleSm),
}

/** The colours of a [QeetrixHeading] (`value` is `@qeetrix/react`'s `tone`). */
public enum class QeetrixHeadingTone(
    public val value: String,
) {
    /** `color.text.default` — the default. */
    Default("default"),

    /** `color.text.subtle` */
    Subtle("subtle"),

    /** `color.text.inverse` */
    Inverse("inverse"),
}

/**
 * A section heading — `@qeetrix/react`'s Heading: the [level] says where it sits in the screen's outline,
 * the [size] how it looks (by default from the level: 1 `heading.lg`, 2 `heading.md`, 3 `heading.sm`,
 * 4 `title.lg`, 5 `title.md`, 6 `title.sm`). Display sizes take Qeet Sans's Geo letter style, the rest Base.
 *
 * Accessibility services announce it as a heading. Android has no heading levels, so [level] only chooses
 * the default look here; the web renders `<h1>`…`<h6>`.
 */
@Composable
public fun QeetrixHeading(
    text: String,
    level: Int,
    modifier: Modifier = Modifier,
    size: QeetrixHeadingSize = headingSizeFor(level),
    tone: QeetrixHeadingTone = QeetrixHeadingTone.Default,
    textAlign: TextAlign = TextAlign.Unspecified,
    maxLines: Int = Int.MAX_VALUE,
    onTextLayout: ((TextLayoutResult) -> Unit)? = null,
) {
    require(level in 1..6) { "A heading level is 1 to 6, not $level." }
    val colors = QeetrixTheme.colors
    val color =
        when (tone) {
            QeetrixHeadingTone.Default -> colors.textDefault
            QeetrixHeadingTone.Subtle -> colors.textSubtle
            QeetrixHeadingTone.Inverse -> colors.textInverse
        }
    BasicText(
        text = text,
        modifier = modifier.semantics { heading() },
        style = QeetrixTheme.typography[size.role].copy(color = color, textAlign = textAlign).labelDirection(alignToLayout = true),
        onTextLayout = onTextLayout,
        overflow = if (maxLines == Int.MAX_VALUE) TextOverflow.Clip else TextOverflow.Ellipsis,
        maxLines = maxLines,
    )
}

/** heading.tsx's default size for a level. */
internal fun headingSizeFor(level: Int): QeetrixHeadingSize =
    when (level) {
        1 -> QeetrixHeadingSize.HeadingLg
        2 -> QeetrixHeadingSize.HeadingMd
        3 -> QeetrixHeadingSize.HeadingSm
        4 -> QeetrixHeadingSize.TitleLg
        5 -> QeetrixHeadingSize.TitleMd
        else -> QeetrixHeadingSize.TitleSm
    }

/** The colour of a [tone], or the inherited content colour. */
@Composable
@ReadOnlyComposable
internal fun toneColor(tone: QeetrixTextTone?): Color =
    tone?.color(QeetrixTheme.colors) ?: LocalQeetrixContentColor.current.takeOrElse { QeetrixTheme.colors.textDefault }

/**
 * text.css: the body role at [size], at a role's [weight], and with [mono] Fira Code at the same size (and
 * no letter style, which Fira Code does not have).
 */
@Composable
@ReadOnlyComposable
internal fun textStyle(
    size: QeetrixTextSize,
    weight: QeetrixTextWeight,
    mono: Boolean,
): TextStyle {
    val base =
        if (mono) {
            QeetrixTypography.style(size.role.spec.copy(face = QeetrixFontFamilyTokens.Mono, geometry = null), LocalDensity.current)
        } else {
            QeetrixTheme.typography[size.role]
        }
    val fontWeight =
        when (weight) {
            QeetrixTextWeight.Regular -> RoleWeight.Regular
            QeetrixTextWeight.Medium -> RoleWeight.Medium
            QeetrixTextWeight.Semibold -> RoleWeight.Semibold
        }
    return base.copy(fontWeight = fontWeight)
}
