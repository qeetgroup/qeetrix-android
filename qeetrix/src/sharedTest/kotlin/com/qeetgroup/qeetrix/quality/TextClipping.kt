package com.qeetgroup.qeetrix.quality

import androidx.compose.ui.text.TextLayoutResult

/**
 * Whether some of a text isn't shown: lines cut off by the maximum line count or an ellipsis, a line wider than
 * the element, or lines taller than it, by more than [slack] pixels — a line's width includes trailing spacing the
 * element's rounded width can leave out, so a pixel either way is not clipping. (`hasVisualOverflow` is not this:
 * a single-line text is laid out at the full available width, so it reports overflow for any text narrower than
 * its parent.)
 *
 * A line's width is its right edge less its left, which leaves out the space a line wraps after. In a text laid out
 * without a width limit — a label that never wraps, measured with unbounded constraints — a right-to-left line sits at
 * an offset of about 2³¹ px, where two floats differ by multiples of 128 px; there the paragraph's own measure of the
 * line is used instead.
 */
internal fun TextLayoutResult.isClipped(slack: Float): Boolean {
    val tooWide = (0 until lineCount).any { lineExtent(it) > size.width + slack }
    val tooTall = multiParagraph.height > size.height + slack
    val ellipsized = (0 until lineCount).any { isLineEllipsized(it) }
    return multiParagraph.didExceedMaxLines || tooWide || tooTall || ellipsized
}

/** What [isClipped] measured, for a report. */
internal fun TextLayoutResult.clipping(): String {
    val widest = (0 until lineCount).maxOfOrNull { lineExtent(it) } ?: 0f
    val ellipsized = (0 until lineCount).any { isLineEllipsized(it) }
    return "in $size: widest line $widest, paragraph height ${multiParagraph.height}, lines $lineCount, " +
        "exceeded ${multiParagraph.didExceedMaxLines}, ellipsized $ellipsized"
}

/** How wide line [line] is drawn: see [isClipped]. */
private fun TextLayoutResult.lineExtent(line: Int): Float =
    if (multiParagraph.width < UNBOUNDED_WIDTH) getLineRight(line) - getLineLeft(line) else multiParagraph.getLineWidth(line)

/** Wider than any screen: a paragraph laid out without a width limit. */
private const val UNBOUNDED_WIDTH = 1_000_000f

/** Texts of one specimen: those of the specimen named [specimen] that [text] matches (every one when null). */
internal class SpecimenText(
    val specimen: String,
    val text: Regex? = null,
) {
    constructor(specimen: String, text: String) : this(specimen, Regex.fromLiteral(text))

    fun matches(
        specimen: String,
        text: String,
    ): Boolean = specimen == this.specimen && (this.text == null || this.text.matches(text))
}

/**
 * Texts that end in an ellipsis by design, each read in full by accessibility services: a navigation bar's labels
 * share the bar's width (ADR 0015); a select shows its value on one line (`.qx-select-value { white-space: nowrap;
 * text-overflow: ellipsis }`), as a field does — the open list shows every option in full; a copy field asked not to
 * wrap keeps its value on one line (copy-field.css `[data-wrap="none"]`), and its Copy button copies it whole; a button's
 * label keeps one line (button.css `white-space: nowrap`), read in full as the button's name. Only a
 * text that does end in an ellipsis is excused: the same specimens' labels and descriptions are still checked.
 */
internal val EllipsizedByDesign: List<SpecimenText> =
    listOf(
        SpecimenText("navigation-bar"),
        SpecimenText("navigation-scaffold"),
        SpecimenText("select"),
        // A multiple select's trigger lists the chosen labels on one line, cut with an ellipsis (06 Increment 5; QTRX-190).
        SpecimenText("multi-select", "India, United Kingdom of Great Britain and Northern Ireland"),
        SpecimenText("copy", "https://hooks.acme.in/qeet/events/payroll"),
        // A single-line field's placeholder ends in an ellipsis where it doesn't fit (QTRX-197; once finding Q-5).
        SpecimenText("pseudo-expanded", pseudo("e.g. asha@acme.in")),
        // A button's label keeps one line and ends in an ellipsis where there is no room (Button.md), as the web's does.
        SpecimenText("alert", "Allow notifications"),
        SpecimenText("qr-scanner", "Type the key instead"),
    )

/**
 * Texts the scans find clipped that are recorded quality findings (Q-n, for Documentation/Quality/ACCESSIBILITY-REPORT.md),
 * not by design — each excluded here by name until the component changes: the texts, and the finding. Each specimen
 * makes its finding happen whatever the text stack's rounding, so the scans can tell when it is fixed.
 */
internal val ClippingFindings: Map<SpecimenText, String> =
    mapOf(
        SpecimenText("otp-input-long", Regex(".")) to
            "Q-15: OTP slots share the width and keep shrinking (the web stops them at 24px and runs past the column); at 2× " +
            "and 200 % a 10-character code's slots in a compact phone are narrower than their characters, which overflow " +
            "into the gaps. 07-components (QTRX-164) says a code never goes in a column narrower than it needs (328px for " +
            "[5, 5]), so this stays recorded",
    )

/** Whether this text, in the specimen named [specimen], ends in an ellipsis by design ([EllipsizedByDesign]). */
internal fun TextLayoutResult.isEllipsizedByDesign(specimen: String): Boolean =
    (0 until lineCount).any { isLineEllipsized(it) } && EllipsizedByDesign.any { it.matches(specimen, layoutInput.text.text) }

/** The recorded finding ([ClippingFindings]) that excuses this text's clipping in the specimen named [specimen], if any. */
internal fun TextLayoutResult.clippingFinding(specimen: String): String? =
    ClippingFindings.entries.firstOrNull { it.key.matches(specimen, layoutInput.text.text) }?.value
