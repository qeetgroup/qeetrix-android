package com.qeetgroup.qeetrix.quality

import androidx.compose.ui.text.TextLayoutResult

/**
 * Whether some of a text isn't shown: lines cut off by the maximum line count or an ellipsis, a line wider than
 * the element, or lines taller than it, by more than [slack] pixels — a line's width includes trailing spacing the
 * element's rounded width can leave out, so a pixel either way is not clipping. (`hasVisualOverflow` is not this:
 * a single-line text is laid out at the full available width, so it reports overflow for any text narrower than
 * its parent.)
 */
internal fun TextLayoutResult.isClipped(slack: Float): Boolean {
    val tooWide = (0 until lineCount).any { getLineRight(it) - getLineLeft(it) > size.width + slack }
    val tooTall = multiParagraph.height > size.height + slack
    val ellipsized = (0 until lineCount).any { isLineEllipsized(it) }
    return multiParagraph.didExceedMaxLines || tooWide || tooTall || ellipsized
}

/** What [isClipped] measured, for a report. */
internal fun TextLayoutResult.clipping(): String {
    val widest = (0 until lineCount).maxOfOrNull { getLineRight(it) - getLineLeft(it) } ?: 0f
    val ellipsized = (0 until lineCount).any { isLineEllipsized(it) }
    return "in $size: widest line $widest, paragraph height ${multiParagraph.height}, lines $lineCount, " +
        "exceeded ${multiParagraph.didExceedMaxLines}, ellipsized $ellipsized"
}
