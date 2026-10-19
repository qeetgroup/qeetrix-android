package com.qeetgroup.qeetrix.components.separator

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.qeetgroup.qeetrix.foundation.sizing.QeetrixStroke
import com.qeetgroup.qeetrix.theme.QeetrixTheme

/** The two weights of [QeetrixSeparator] (`value` is the web name). */
public enum class QeetrixSeparatorVariant(
    public val value: String,
) {
    /** The border role: divides sections, groups and toolbars. */
    Default("default"),

    /** The subtle border role, one step quieter: divides items inside a section. */
    Muted("muted"),
}

/** Which way a [QeetrixSeparator] runs. */
public enum class QeetrixSeparatorOrientation(
    public val value: String,
) {
    /** A full-width rule. */
    Horizontal("horizontal"),

    /** A rule as tall as its parent row; give the row a height (`Modifier.height(IntrinsicSize.Min)`). */
    Vertical("vertical"),
}

/**
 * A 1 dp rule between groups of content.
 *
 * It is decorative — well under 3:1 by design — and invisible to accessibility services: a separator
 * supports grouping that spacing and headings already express, and is never the only signal. (The web
 * gives it `role="separator"`; Android has no separator role, and TalkBack would only read an empty
 * element.)
 */
@Composable
public fun QeetrixSeparator(
    modifier: Modifier = Modifier,
    orientation: QeetrixSeparatorOrientation = QeetrixSeparatorOrientation.Horizontal,
    variant: QeetrixSeparatorVariant = QeetrixSeparatorVariant.Default,
) {
    val color =
        when (variant) {
            QeetrixSeparatorVariant.Default -> QeetrixTheme.colors.borderDefault

            // bg-border
            QeetrixSeparatorVariant.Muted -> QeetrixTheme.colors.borderSubtle // bg-border-subtle
        }
    val size =
        when (orientation) {
            QeetrixSeparatorOrientation.Horizontal -> Modifier.fillMaxWidth().height(QeetrixStroke.Hairline)

            // h-px w-full
            QeetrixSeparatorOrientation.Vertical -> Modifier.fillMaxHeight().width(QeetrixStroke.Hairline) // w-px self-stretch
        }
    Box(modifier.then(size).background(color))
}
