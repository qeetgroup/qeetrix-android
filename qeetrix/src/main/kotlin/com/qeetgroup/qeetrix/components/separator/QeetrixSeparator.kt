package com.qeetgroup.qeetrix.components.separator

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.qeetgroup.qeetrix.foundation.sizing.QeetrixBorder
import com.qeetgroup.qeetrix.theme.QeetrixTheme

/** The line colours of [QeetrixSeparator] (`value` is `@qeetrix/react`'s `tone`). */
public enum class QeetrixSeparatorTone(
    public val value: String,
) {
    /** `color.border.subtle` — the default. */
    Subtle("subtle"),

    /** `color.border.default`. */
    Default("default"),

    /** `color.border.strong`. */
    Strong("strong"),
}

/** Which way a [QeetrixSeparator] runs (`value` is `@qeetrix/react`'s `orientation`). */
public enum class QeetrixSeparatorOrientation(
    public val value: String,
) {
    /** A full-width rule. */
    Horizontal("horizontal"),

    /** A rule as tall as its parent row; give the row a height (`Modifier.height(IntrinsicSize.Min)`). */
    Vertical("vertical"),
}

/**
 * A 1 dp rule (`border.width.default`) between groups of content — `@qeetrix/react`'s Separator. Ruled
 * surfaces are a Qeetrix signature (design rules §2, §12): content is separated by lines and tone, not
 * shadows.
 *
 * It is invisible to accessibility services: a separator supports grouping that spacing and headings
 * already express, and is never the only signal. (The web gives a non-decorative one `role="separator"`;
 * Android has no separator role, and TalkBack would only read an empty element.)
 */
@Composable
public fun QeetrixSeparator(
    modifier: Modifier = Modifier,
    orientation: QeetrixSeparatorOrientation = QeetrixSeparatorOrientation.Horizontal,
    tone: QeetrixSeparatorTone = QeetrixSeparatorTone.Subtle,
) {
    val colors = QeetrixTheme.colors
    val color =
        when (tone) {
            QeetrixSeparatorTone.Subtle -> colors.borderSubtle
            QeetrixSeparatorTone.Default -> colors.borderDefault
            QeetrixSeparatorTone.Strong -> colors.borderStrong
        }
    val size =
        when (orientation) {
            QeetrixSeparatorOrientation.Horizontal -> Modifier.fillMaxWidth().height(QeetrixBorder.WidthDefault)
            QeetrixSeparatorOrientation.Vertical -> Modifier.fillMaxHeight().width(QeetrixBorder.WidthDefault)
        }
    Box(modifier.then(size).background(color))
}
