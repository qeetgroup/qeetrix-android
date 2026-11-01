package com.qeetgroup.qeetrix.foundation.layout

import androidx.compose.ui.unit.Dp
import com.qeetgroup.qeetrix.tokens.generated.QeetrixBreakpointTokens

/**
 * Window-width breakpoints (`breakpoint.*`), in dp: the lower bounds of Android's window size classes,
 * the same widths the web's `--qx-viewport-*` media queries use. Below [Medium] is compact.
 */
public object QeetrixBreakpoint {
    /** `breakpoint.medium` */
    public val Medium: Dp = QeetrixBreakpointTokens.Medium

    /** `breakpoint.expanded` */
    public val Expanded: Dp = QeetrixBreakpointTokens.Expanded

    /** `breakpoint.large` */
    public val Large: Dp = QeetrixBreakpointTokens.Large

    /** `breakpoint.xlarge` */
    public val Xlarge: Dp = QeetrixBreakpointTokens.Xlarge
}
