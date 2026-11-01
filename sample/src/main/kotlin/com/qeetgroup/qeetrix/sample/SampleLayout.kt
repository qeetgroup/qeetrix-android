package com.qeetgroup.qeetrix.sample

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.FlowRowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import com.qeetgroup.qeetrix.components.typography.QeetrixHeading
import com.qeetgroup.qeetrix.components.typography.QeetrixHeadingSize
import com.qeetgroup.qeetrix.components.typography.QeetrixText
import com.qeetgroup.qeetrix.components.typography.QeetrixTextSize
import com.qeetgroup.qeetrix.components.typography.QeetrixTextTone
import com.qeetgroup.qeetrix.foundation.shape.QeetrixShapes
import com.qeetgroup.qeetrix.foundation.sizing.QeetrixBorder
import com.qeetgroup.qeetrix.foundation.spacing.QeetrixSpace
import com.qeetgroup.qeetrix.theme.QeetrixTheme

/**
 * A component page: its sections one under another, `space.layout.sm` apart. The app shell adds the title,
 * scrolling and margins.
 */
@Composable
fun SamplePage(content: @Composable ColumnScope.() -> Unit) {
    Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(QeetrixSpace.LayoutSm), content = content)
}

/** A titled group of specimens on a page: a `title.lg` heading (level 2, under the page's title), then the specimens. */
@Composable
fun SampleSection(
    title: String,
    content: @Composable ColumnScope.() -> Unit,
) {
    Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(QeetrixSpace.GapMd)) {
        QeetrixHeading(title, level = 2, size = QeetrixHeadingSize.TitleLg)
        content()
    }
}

/** A short explanation under a specimen — what to try, or what TalkBack says — as help text: `body.sm`, subtle. */
@Composable
fun SampleNote(text: String) {
    QeetrixText(text, size = QeetrixTextSize.Sm, tone = QeetrixTextTone.Subtle)
}

/** Specimens side by side, `space.gap.sm` apart, wrapping onto new lines. */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SampleFlow(content: @Composable FlowRowScope.() -> Unit) {
    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(QeetrixSpace.GapSm),
        verticalArrangement = Arrangement.spacedBy(QeetrixSpace.GapSm),
        itemVerticalAlignment = Alignment.CenterVertically,
        content = content,
    )
}

/**
 * A surface for content that carries its own inset — list rows, sections — so it meets the surface's edges: a
 * ruled, flat surface as a card draws one (`color.surface.raised`, a 1 dp `color.border.default` edge, the surface
 * corner, no shadow; design rules §12).
 */
@Composable
fun SampleSurface(content: @Composable ColumnScope.() -> Unit) {
    val colors = QeetrixTheme.colors
    Column(
        Modifier
            .fillMaxWidth()
            .clip(QeetrixShapes.Surface)
            .background(colors.surfaceRaised)
            .border(QeetrixBorder.WidthDefault, colors.borderDefault, QeetrixShapes.Surface),
        content = content,
    )
}

/**
 * A preview frame: [content] on the theme's canvas, in the system theme so `@PreviewLightDark` shows both.
 * Previews use the same page composables the app shows.
 */
@Composable
fun SamplePreview(content: @Composable () -> Unit) {
    QeetrixTheme {
        Box(Modifier.background(QeetrixTheme.colors.surfaceCanvas).padding(QeetrixSpace.InsetLg)) { content() }
    }
}
