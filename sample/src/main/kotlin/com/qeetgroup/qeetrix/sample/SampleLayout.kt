package com.qeetgroup.qeetrix.sample

import androidx.compose.foundation.background
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
import com.qeetgroup.qeetrix.components.typography.QeetrixText
import com.qeetgroup.qeetrix.components.typography.QeetrixTextVariant
import com.qeetgroup.qeetrix.foundation.spacing.QeetrixSpacing
import com.qeetgroup.qeetrix.theme.QeetrixTheme

/** A component page: its sections one under another. The app shell adds the title, scrolling and margins. */
@Composable
fun SamplePage(content: @Composable ColumnScope.() -> Unit) {
    Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(QeetrixSpacing.Xl2), content = content)
}

/** A titled group of specimens on a page. */
@Composable
fun SampleSection(
    title: String,
    content: @Composable ColumnScope.() -> Unit,
) {
    Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(QeetrixSpacing.M)) {
        QeetrixText(title, variant = QeetrixTextVariant.H4)
        content()
    }
}

/** A short explanation under a specimen — what to try, or what TalkBack says. */
@Composable
fun SampleNote(text: String) {
    QeetrixText(text, variant = QeetrixTextVariant.Muted)
}

/** Specimens side by side, wrapping onto new lines. */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SampleFlow(content: @Composable FlowRowScope.() -> Unit) {
    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(QeetrixSpacing.S),
        verticalArrangement = Arrangement.spacedBy(QeetrixSpacing.S),
        itemVerticalAlignment = Alignment.CenterVertically,
        content = content,
    )
}

/**
 * A preview frame: [content] on the theme's canvas, in the system theme so `@PreviewLightDark` shows
 * both. Previews use the same page composables the app shows.
 */
@Composable
fun SamplePreview(content: @Composable () -> Unit) {
    QeetrixTheme {
        Box(Modifier.background(QeetrixTheme.colors.surfaceCanvas).padding(QeetrixSpacing.L)) { content() }
    }
}
