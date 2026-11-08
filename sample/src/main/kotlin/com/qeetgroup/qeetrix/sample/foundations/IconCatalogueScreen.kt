package com.qeetgroup.qeetrix.sample.foundations

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.qeetgroup.qeetrix.components.input.QeetrixSearchField
import com.qeetgroup.qeetrix.components.navigation.QeetrixBackButton
import com.qeetgroup.qeetrix.components.navigation.QeetrixTopAppBar
import com.qeetgroup.qeetrix.components.typography.QeetrixText
import com.qeetgroup.qeetrix.components.typography.QeetrixTextSize
import com.qeetgroup.qeetrix.components.typography.QeetrixTextTone
import com.qeetgroup.qeetrix.foundation.sizing.QeetrixSize
import com.qeetgroup.qeetrix.foundation.spacing.QeetrixSpace
import com.qeetgroup.qeetrix.icons.QeetrixIcon
import com.qeetgroup.qeetrix.icons.QeetrixIconName
import com.qeetgroup.qeetrix.icons.extended.extendedCatalogue
import com.qeetgroup.qeetrix.theme.QeetrixTheme

/**
 * Every `@qeetrix/icons` icon — the core set and `qeetrix-icons-extended` — searchable by name. A lazy grid, so only
 * the icons on screen are decoded and drawn.
 */
@Composable
fun IconCatalogueScreen(onExit: () -> Unit) {
    val query = rememberTextFieldState()
    val all = remember { (QeetrixIconName.catalogue + QeetrixIconName.extendedCatalogue).sortedBy { it.value } }
    val text =
        query.text
            .toString()
            .trim()
            .lowercase()
    val shown = remember(text, all) { if (text.isEmpty()) all else all.filter { text in it.value } }
    Column(Modifier.fillMaxSize().background(QeetrixTheme.colors.surfaceCanvas)) {
        QeetrixTopAppBar("Icons", navigationIcon = { QeetrixBackButton(onClick = onExit) })
        Column(
            Modifier
                .fillMaxSize()
                .consumeWindowInsets(WindowInsets.safeDrawing.only(WindowInsetsSides.Top))
                .windowInsetsPadding(WindowInsets.safeDrawing.only(WindowInsetsSides.Horizontal + WindowInsetsSides.Bottom))
                .padding(horizontal = QeetrixSpace.InsetLg),
            verticalArrangement = Arrangement.spacedBy(QeetrixSpace.GapSm),
        ) {
            QeetrixSearchField(query, Modifier.fillMaxWidth(), label = "Search ${all.size} icons", placeholder = "Name, such as qr-code")
            QeetrixText(
                if (text.isEmpty()) "${all.size} icons: sharp outline, as on controls" else "${shown.size} of ${all.size}",
                size = QeetrixTextSize.Sm,
                tone = QeetrixTextTone.Subtle,
            )
            LazyVerticalGrid(
                columns = GridCells.Adaptive(minSize = 96.dp),
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(QeetrixSpace.GapMd),
            ) {
                items(shown, key = { it.value }) { icon ->
                    Column(
                        Modifier.padding(QeetrixSpace.GapXs).semantics(mergeDescendants = true) { contentDescription = icon.value },
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(QeetrixSpace.GapXs),
                    ) {
                        QeetrixIcon(icon, contentDescription = null, size = QeetrixSize.IconLg)
                        QeetrixText(
                            icon.value,
                            size = QeetrixTextSize.Sm,
                            tone = QeetrixTextTone.Subtle,
                            textAlign = TextAlign.Center,
                            maxLines = 2,
                        )
                    }
                }
            }
        }
    }
}
