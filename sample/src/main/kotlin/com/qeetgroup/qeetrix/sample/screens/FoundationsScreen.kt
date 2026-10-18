package com.qeetgroup.qeetrix.sample.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.qeetgroup.qeetrix.foundation.accessibility.qeetrixMinimumTouchTarget
import com.qeetgroup.qeetrix.foundation.color.QeetrixColors
import com.qeetgroup.qeetrix.foundation.elevation.QeetrixElevation
import com.qeetgroup.qeetrix.foundation.elevation.qeetrixElevation
import com.qeetgroup.qeetrix.foundation.shape.QeetrixCorner
import com.qeetgroup.qeetrix.foundation.shape.QeetrixShapes
import com.qeetgroup.qeetrix.foundation.sizing.QeetrixIconSize
import com.qeetgroup.qeetrix.foundation.sizing.QeetrixStroke
import com.qeetgroup.qeetrix.foundation.spacing.QeetrixSpacing
import com.qeetgroup.qeetrix.foundation.typography.QeetrixTextRole
import com.qeetgroup.qeetrix.icons.QeetrixIcon
import com.qeetgroup.qeetrix.icons.QeetrixIconName
import com.qeetgroup.qeetrix.icons.QeetrixIconShape
import com.qeetgroup.qeetrix.icons.QeetrixIconVariant
import com.qeetgroup.qeetrix.theme.LocalQeetrixContentColor
import com.qeetgroup.qeetrix.theme.QeetrixTheme
import com.qeetgroup.qeetrix.theme.QeetrixThemeMode
import com.qeetgroup.qeetrix.tokens.QeetrixProvenance

/**
 * The Phase 1 foundations on one screen: theme modes, semantic colours, typography, spacing and
 * corners, elevation and icons. Everything is drawn from Qeetrix tokens through the public API; the
 * controls are plain foundation layouts, not Qeetrix components (those arrive in Phase 2).
 */
@Composable
fun FoundationsScreen(
    mode: QeetrixThemeMode,
    onModeChange: (QeetrixThemeMode) -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = QeetrixTheme.colors
    BoxWithConstraints(
        modifier
            .fillMaxSize()
            .background(colors.surfaceCanvas)
            .windowInsetsPadding(WindowInsets.safeDrawing),
    ) {
        // Responsive: one column on phones, two from 600 dp (a typical tablet or unfolded foldable).
        val twoColumns = maxWidth >= 600.dp
        Column(
            Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(QeetrixSpacing.L),
            verticalArrangement = Arrangement.spacedBy(QeetrixSpacing.Xl),
        ) {
            Header()
            ThemeModePicker(mode, onModeChange)
            if (twoColumns) {
                Row(horizontalArrangement = Arrangement.spacedBy(QeetrixSpacing.Xl)) {
                    Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(QeetrixSpacing.Xl)) {
                        ColorSection()
                        SpacingAndShapeSection()
                    }
                    Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(QeetrixSpacing.Xl)) {
                        TypographySection()
                        ElevationSection()
                        IconSection()
                    }
                }
            } else {
                ColorSection()
                TypographySection()
                SpacingAndShapeSection()
                ElevationSection()
                IconSection()
            }
        }
    }
}

@Composable
private fun Header() {
    val fontScale = LocalDensity.current.fontScale
    Column(verticalArrangement = Arrangement.spacedBy(QeetrixSpacing.Xs)) {
        Text("Qeetrix foundations", QeetrixTheme.typography.title, Modifier.semantics { heading() })
        Text(
            "${QeetrixProvenance.tokensPackage} ${QeetrixProvenance.tokensVersion} · font scale ${"%.2f".format(fontScale)}",
            QeetrixTheme.typography.caption,
            color = QeetrixTheme.colors.textSecondary,
        )
    }
}

@Composable
private fun ThemeModePicker(
    mode: QeetrixThemeMode,
    onModeChange: (QeetrixThemeMode) -> Unit,
) {
    val colors = QeetrixTheme.colors
    Row(Modifier.selectableGroup(), horizontalArrangement = Arrangement.spacedBy(QeetrixSpacing.S)) {
        for (option in QeetrixThemeMode.entries) {
            val selected = option == mode
            Box(
                Modifier
                    .selectable(selected = selected, role = Role.RadioButton, onClick = { onModeChange(option) })
                    .qeetrixMinimumTouchTarget()
                    .background(if (selected) colors.surfaceBrandSubtle else colors.surfaceInteractive, QeetrixShapes.Control)
                    .border(QeetrixStroke.Hairline, if (selected) colors.borderBrand else colors.borderDefault, QeetrixShapes.Control)
                    .padding(horizontal = QeetrixSpacing.M, vertical = QeetrixSpacing.S),
                contentAlignment = Alignment.Center,
            ) {
                Text(option.value, QeetrixTheme.typography.label, color = if (selected) colors.textOnSubtle else colors.textPrimary)
            }
        }
    }
}

@Composable
private fun ColorSection() {
    Section("Semantic colours") {
        val swatches: List<Pair<String, (QeetrixColors) -> Color>> =
            listOf(
                "surface.canvas" to { it.surfaceCanvas },
                "surface.default" to { it.surfaceDefault },
                "surface.sunken" to { it.surfaceSunken },
                "surface.brand-subtle" to { it.surfaceBrandSubtle },
                "action.primary" to { it.actionPrimary },
                "text.primary" to { it.textPrimary },
                "text.secondary" to { it.textSecondary },
                "text.brand" to { it.textBrand },
                "border.default" to { it.borderDefault },
                "border.control" to { it.borderControl },
                "focus.ring" to { it.focusRing },
                "feedback.success" to { it.feedbackSuccess },
                "feedback.warning" to { it.feedbackWarning },
                "feedback.error" to { it.feedbackError },
                "feedback.info" to { it.feedbackInfo },
                "data.categorical.2" to { it.dataCategorical2 },
            )
        FlowGrid {
            for ((name, color) in swatches) Swatch(name, color(QeetrixTheme.colors))
        }
        // A nested theme: the opposite theme for one panel, as the web's nested `.dark` scope does.
        val opposite = if (QeetrixTheme.isDark) QeetrixThemeMode.Light else QeetrixThemeMode.Dark
        QeetrixTheme(mode = opposite) {
            Column(
                Modifier
                    .fillMaxWidth()
                    .background(QeetrixTheme.colors.surfaceDefault, QeetrixShapes.Surface)
                    .border(QeetrixStroke.Hairline, QeetrixTheme.colors.borderDefault, QeetrixShapes.Surface)
                    .padding(QeetrixSpacing.M),
            ) {
                Text("Nested ${opposite.value} theme", QeetrixTheme.typography.label)
                Text("text.secondary on surface.default", QeetrixTheme.typography.caption, color = QeetrixTheme.colors.textSecondary)
            }
        }
    }
}

@Composable
private fun Swatch(
    name: String,
    color: Color,
) {
    Column(Modifier.width(96.dp), verticalArrangement = Arrangement.spacedBy(QeetrixSpacing.Xxs)) {
        Box(
            Modifier
                .fillMaxWidth()
                .height(40.dp)
                .background(color, QeetrixShapes.Chip)
                .border(QeetrixStroke.Hairline, QeetrixTheme.colors.borderSubtle, QeetrixShapes.Chip),
        )
        Text(name, QeetrixTheme.typography.micro, color = QeetrixTheme.colors.textSecondary)
    }
}

@Composable
private fun TypographySection() {
    Section("Typography") {
        for (role in QeetrixTextRole.entries) {
            val sample =
                when (role) {
                    QeetrixTextRole.Code -> "val theme = QeetrixTheme.colors"
                    QeetrixTextRole.Body -> "Qeet Sans body text, with a Hindi fallback: नमस्ते"
                    else -> "${role.value} · Qeet Sans"
                }
            Text(sample, QeetrixTheme.typography[role])
        }
    }
}

@Composable
private fun SpacingAndShapeSection() {
    Section("Spacing and corners") {
        val steps: List<Pair<String, Dp>> =
            listOf(
                "xxs" to QeetrixSpacing.Xxs,
                "xs" to QeetrixSpacing.Xs,
                "s" to QeetrixSpacing.S,
                "m" to QeetrixSpacing.M,
                "l" to QeetrixSpacing.L,
                "xl" to QeetrixSpacing.Xl,
                "2xl" to QeetrixSpacing.Xl2,
                "3xl" to QeetrixSpacing.Xl3,
            )
        for ((name, step) in steps) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("space.$name", QeetrixTheme.typography.caption, Modifier.width(88.dp))
                Box(Modifier.size(width = step, height = QeetrixSpacing.S).background(QeetrixTheme.colors.actionPrimary))
            }
        }
        Spacer(Modifier.height(QeetrixSpacing.S))
        val corners: List<Triple<String, Dp, Shape>> =
            listOf(
                Triple("chip", QeetrixCorner.Chip, QeetrixShapes.Chip),
                Triple("control", QeetrixCorner.Control, QeetrixShapes.Control),
                Triple("surface", QeetrixCorner.Surface, QeetrixShapes.Surface),
                Triple("pill", QeetrixCorner.Pill, QeetrixShapes.Pill),
            )
        FlowGrid {
            for ((name, radius, shape) in corners) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        Modifier
                            .size(width = 72.dp, height = 48.dp)
                            .background(QeetrixTheme.colors.surfaceSunken, shape)
                            .border(QeetrixStroke.Thin, QeetrixTheme.colors.borderStrong, shape),
                    )
                    Text("$name ${radius.value}", QeetrixTheme.typography.micro, color = QeetrixTheme.colors.textSecondary)
                }
            }
        }
    }
}

@Composable
private fun ElevationSection() {
    Section("Elevation") {
        FlowGrid {
            for (elevation in listOf(QeetrixElevation.Raised, QeetrixElevation.Hover, QeetrixElevation.Overlay, QeetrixElevation.Modal)) {
                Box(
                    Modifier
                        .padding(QeetrixSpacing.S)
                        .size(width = 112.dp, height = 64.dp)
                        .qeetrixElevation(elevation, QeetrixShapes.Surface, QeetrixTheme.colors.surfaceElevated),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(elevation.token.removePrefix("elevation."), QeetrixTheme.typography.label)
                }
            }
        }
    }
}

@Composable
private fun IconSection() {
    Section("Icons") {
        for (shape in QeetrixIconShape.entries) {
            for (variant in QeetrixIconVariant.entries) {
                Text("${shape.value} · ${variant.value}", QeetrixTheme.typography.caption, color = QeetrixTheme.colors.textSecondary)
                FlowGrid {
                    for (name in QeetrixIconName.entries) {
                        if (name.hasDrawing(shape, variant)) {
                            QeetrixIcon(name, contentDescription = name.value, size = QeetrixIconSize.Lg, shape = shape, variant = variant)
                        }
                    }
                }
            }
        }
        Text("Brand-coloured and right-to-left", QeetrixTheme.typography.caption, color = QeetrixTheme.colors.textSecondary)
        Row(horizontalArrangement = Arrangement.spacedBy(QeetrixSpacing.M), verticalAlignment = Alignment.CenterVertically) {
            CompositionLocalProvider(LocalQeetrixContentColor provides QeetrixTheme.colors.textBrand) {
                QeetrixIcon(QeetrixIconName.Send, contentDescription = "Send", size = QeetrixIconSize.Lg)
            }
            CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                QeetrixIcon(QeetrixIconName.Send, contentDescription = "Send, right to left", size = QeetrixIconSize.Lg)
            }
            Text("send mirrors in RTL", QeetrixTheme.typography.caption)
        }
    }
}

@Composable
private fun Section(
    title: String,
    content: @Composable () -> Unit,
) {
    Column(
        Modifier
            .fillMaxWidth()
            .qeetrixElevation(QeetrixElevation.Subtle, QeetrixShapes.Surface, QeetrixTheme.colors.surfaceDefault)
            .padding(QeetrixSpacing.L),
        verticalArrangement = Arrangement.spacedBy(QeetrixSpacing.S),
    ) {
        Text(title, QeetrixTheme.typography.heading, Modifier.semantics { heading() })
        content()
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun FlowGrid(content: @Composable () -> Unit) {
    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(QeetrixSpacing.M),
        verticalArrangement = Arrangement.spacedBy(QeetrixSpacing.M),
    ) { content() }
}

@Composable
private fun Text(
    text: String,
    style: TextStyle,
    modifier: Modifier = Modifier,
    color: Color = LocalQeetrixContentColor.current,
) {
    BasicText(text, modifier, style = style.copy(color = color))
}

@Preview(widthDp = 400, heightDp = 1600)
@Composable
private fun FoundationsScreenLightPreview() {
    QeetrixTheme(QeetrixThemeMode.Light) { FoundationsScreen(QeetrixThemeMode.Light, onModeChange = {}) }
}

@Preview(widthDp = 800, heightDp = 1200)
@Composable
private fun FoundationsScreenDarkWidePreview() {
    QeetrixTheme(QeetrixThemeMode.Dark) { FoundationsScreen(QeetrixThemeMode.Dark, onModeChange = {}) }
}
