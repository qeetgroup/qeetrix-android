package com.qeetgroup.qeetrix.sample.foundations

import androidx.compose.animation.core.Easing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.qeetgroup.qeetrix.components.button.QeetrixButton
import com.qeetgroup.qeetrix.components.button.QeetrixButtonSize
import com.qeetgroup.qeetrix.components.button.QeetrixButtonVariant
import com.qeetgroup.qeetrix.components.typography.QeetrixHeading
import com.qeetgroup.qeetrix.components.typography.QeetrixHeadingSize
import com.qeetgroup.qeetrix.foundation.accessibility.QeetrixAccessibility
import com.qeetgroup.qeetrix.foundation.color.QeetrixColors
import com.qeetgroup.qeetrix.foundation.elevation.QeetrixShadow
import com.qeetgroup.qeetrix.foundation.elevation.qeetrixShadow
import com.qeetgroup.qeetrix.foundation.layout.QeetrixBreakpoint
import com.qeetgroup.qeetrix.foundation.motion.QeetrixMotion
import com.qeetgroup.qeetrix.foundation.motion.QeetrixSpring
import com.qeetgroup.qeetrix.foundation.motion.qeetrixAnimationSpec
import com.qeetgroup.qeetrix.foundation.shape.QeetrixRadius
import com.qeetgroup.qeetrix.foundation.shape.QeetrixShapes
import com.qeetgroup.qeetrix.foundation.sizing.QeetrixBorder
import com.qeetgroup.qeetrix.foundation.sizing.QeetrixDensity
import com.qeetgroup.qeetrix.foundation.sizing.QeetrixSize
import com.qeetgroup.qeetrix.foundation.spacing.QeetrixSpace
import com.qeetgroup.qeetrix.foundation.typography.QeetrixTextRole
import com.qeetgroup.qeetrix.icons.QeetrixIcon
import com.qeetgroup.qeetrix.icons.QeetrixIconName
import com.qeetgroup.qeetrix.icons.QeetrixIconShape
import com.qeetgroup.qeetrix.icons.QeetrixIconVariant
import com.qeetgroup.qeetrix.theme.LocalQeetrixDensity
import com.qeetgroup.qeetrix.theme.QeetrixTheme
import com.qeetgroup.qeetrix.theme.QeetrixThemeMode
import java.util.Locale
import kotlin.math.roundToInt

/** One colour role: its token path after `color.`, and how to read it from the scheme. */
private class Role(
    val token: String,
    val color: (QeetrixColors) -> Color,
)

/** The colour roles by group, in the token order of `@qeetrix/tokens`. */
private val ColorGroups: List<Pair<String, List<Role>>> =
    listOf(
        "Surface" to
            listOf(
                Role("surface.canvas") { it.surfaceCanvas },
                Role("surface.default") { it.surfaceDefault },
                Role("surface.raised") { it.surfaceRaised },
                Role("surface.overlay") { it.surfaceOverlay },
                Role("surface.sunken") { it.surfaceSunken },
                Role("surface.inverse") { it.surfaceInverse },
            ),
        "Background" to
            listOf(
                Role("bg.neutral.subtle") { it.bgNeutralSubtle },
                Role("bg.neutral.subtle-hover") { it.bgNeutralSubtleHover },
                Role("bg.neutral.subtle-pressed") { it.bgNeutralSubtlePressed },
                Role("bg.neutral.bold") { it.bgNeutralBold },
                Role("bg.neutral.bold-hover") { it.bgNeutralBoldHover },
                Role("bg.neutral.bold-pressed") { it.bgNeutralBoldPressed },
                Role("bg.brand.subtle") { it.bgBrandSubtle },
                Role("bg.brand.subtle-hover") { it.bgBrandSubtleHover },
                Role("bg.brand.subtle-pressed") { it.bgBrandSubtlePressed },
                Role("bg.brand.bold") { it.bgBrandBold },
                Role("bg.brand.bold-hover") { it.bgBrandBoldHover },
                Role("bg.brand.bold-pressed") { it.bgBrandBoldPressed },
                Role("bg.danger.subtle") { it.bgDangerSubtle },
                Role("bg.danger.bold") { it.bgDangerBold },
                Role("bg.danger.bold-hover") { it.bgDangerBoldHover },
                Role("bg.danger.bold-pressed") { it.bgDangerBoldPressed },
                Role("bg.warning.subtle") { it.bgWarningSubtle },
                Role("bg.warning.bold") { it.bgWarningBold },
                Role("bg.success.subtle") { it.bgSuccessSubtle },
                Role("bg.success.bold") { it.bgSuccessBold },
                Role("bg.info.subtle") { it.bgInfoSubtle },
                Role("bg.info.bold") { it.bgInfoBold },
                Role("bg.selected") { it.bgSelected },
                Role("bg.selected-hover") { it.bgSelectedHover },
                Role("bg.input") { it.bgInput },
                Role("bg.disabled") { it.bgDisabled },
            ),
        "Text" to
            listOf(
                Role("text.default") { it.textDefault },
                Role("text.subtle") { it.textSubtle },
                Role("text.subtlest") { it.textSubtlest },
                Role("text.disabled") { it.textDisabled },
                Role("text.inverse") { it.textInverse },
                Role("text.brand") { it.textBrand },
                Role("text.link") { it.textLink },
                Role("text.link-hover") { it.textLinkHover },
                Role("text.on-brand") { it.textOnBrand },
                Role("text.on-bold") { it.textOnBold },
                Role("text.on-warning") { it.textOnWarning },
                Role("text.danger") { it.textDanger },
                Role("text.warning") { it.textWarning },
                Role("text.success") { it.textSuccess },
                Role("text.info") { it.textInfo },
            ),
        "Icon" to
            listOf(
                Role("icon.default") { it.iconDefault },
                Role("icon.subtle") { it.iconSubtle },
                Role("icon.disabled") { it.iconDisabled },
                Role("icon.inverse") { it.iconInverse },
                Role("icon.brand") { it.iconBrand },
                Role("icon.on-brand") { it.iconOnBrand },
                Role("icon.on-bold") { it.iconOnBold },
                Role("icon.danger") { it.iconDanger },
                Role("icon.warning") { it.iconWarning },
                Role("icon.success") { it.iconSuccess },
                Role("icon.info") { it.iconInfo },
                Role("icon.highlight") { it.iconHighlight },
            ),
        "Border" to
            listOf(
                Role("border.default") { it.borderDefault },
                Role("border.subtle") { it.borderSubtle },
                Role("border.strong") { it.borderStrong },
                Role("border.strong-hover") { it.borderStrongHover },
                Role("border.focus") { it.borderFocus },
                Role("border.brand") { it.borderBrand },
                Role("border.selected") { it.borderSelected },
                Role("border.danger") { it.borderDanger },
                Role("border.warning") { it.borderWarning },
                Role("border.success") { it.borderSuccess },
                Role("border.info") { it.borderInfo },
                Role("border.disabled") { it.borderDisabled },
            ),
        "Overlay" to
            listOf(
                Role("overlay.scrim") { it.overlayScrim },
                Role("overlay.hover") { it.overlayHover },
                Role("overlay.pressed") { it.overlayPressed },
                Role("selection") { it.selection },
            ),
        "Data visualisation" to
            listOf(
                Role("dataviz.categorical.1") { it.datavizCategorical1 },
                Role("dataviz.categorical.2") { it.datavizCategorical2 },
                Role("dataviz.categorical.3") { it.datavizCategorical3 },
                Role("dataviz.categorical.4") { it.datavizCategorical4 },
                Role("dataviz.categorical.5") { it.datavizCategorical5 },
                Role("dataviz.categorical.6") { it.datavizCategorical6 },
                Role("dataviz.categorical.7") { it.datavizCategorical7 },
                Role("dataviz.categorical.8") { it.datavizCategorical8 },
                Role("dataviz.sequential.1") { it.datavizSequential1 },
                Role("dataviz.sequential.2") { it.datavizSequential2 },
                Role("dataviz.sequential.3") { it.datavizSequential3 },
                Role("dataviz.sequential.4") { it.datavizSequential4 },
                Role("dataviz.sequential.5") { it.datavizSequential5 },
                Role("dataviz.sequential.6") { it.datavizSequential6 },
                Role("dataviz.sequential.7") { it.datavizSequential7 },
                Role("dataviz.diverging.1") { it.datavizDiverging1 },
                Role("dataviz.diverging.2") { it.datavizDiverging2 },
                Role("dataviz.diverging.3") { it.datavizDiverging3 },
                Role("dataviz.diverging.4") { it.datavizDiverging4 },
                Role("dataviz.diverging.5") { it.datavizDiverging5 },
                Role("dataviz.diverging.6") { it.datavizDiverging6 },
                Role("dataviz.diverging.7") { it.datavizDiverging7 },
                Role("dataviz.positive") { it.datavizPositive },
                Role("dataviz.negative") { it.datavizNegative },
                Role("dataviz.warning") { it.datavizWarning },
                Role("dataviz.grid") { it.datavizGrid },
                Role("dataviz.axis") { it.datavizAxis },
                Role("dataviz.reference") { it.datavizReference },
            ),
        "Code" to
            listOf(
                Role("code.keyword") { it.codeKeyword },
                Role("code.string") { it.codeString },
                Role("code.number") { it.codeNumber },
                Role("code.literal") { it.codeLiteral },
                Role("code.punctuation") { it.codePunctuation },
                Role("code.comment") { it.codeComment },
            ),
    )

/**
 * The foundations on one screen: the colour roles by group, the 18 type roles, space, radius, border widths,
 * shadows, motion, the densities and the icons in both shapes and both variants. Everything is drawn from the
 * Qeetrix tokens through the public API, with plain foundation layouts rather than components (a heading per
 * section and the motion demo's button aside), so the screen shows the tokens themselves.
 */
@Composable
fun FoundationsScreen(modifier: Modifier = Modifier) {
    BoxWithConstraints(modifier.fillMaxWidth()) {
        // Responsive: one column on phones, two from breakpoint.medium (600 dp).
        if (maxWidth >= QeetrixBreakpoint.Medium) {
            Row(horizontalArrangement = Arrangement.spacedBy(QeetrixSpace.LayoutSm)) {
                Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(QeetrixSpace.LayoutSm)) {
                    ColorSection()
                    SpaceSection()
                    ShapeSection()
                    ShadowSection()
                }
                Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(QeetrixSpace.LayoutSm)) {
                    TypographySection()
                    MotionSection()
                    DensitySection()
                    IconSection()
                }
            }
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(QeetrixSpace.LayoutSm)) {
                ColorSection()
                TypographySection()
                SpaceSection()
                ShapeSection()
                ShadowSection()
                MotionSection()
                DensitySection()
                IconSection()
            }
        }
    }
}

@Composable
private fun ColorSection() {
    Section("Colour roles") {
        Caption("Every color.* token of @qeetrix/tokens in the current scheme, as QeetrixTheme.colors reads it.")
        val width = swatchWidth()
        for ((group, roles) in ColorGroups) {
            GroupLabel(group)
            FlowGrid {
                for (role in roles) Swatch(role.token, role.color(QeetrixTheme.colors), width)
            }
        }
        // A nested theme: the opposite scheme for one panel, as the web's nested data-qx-color-scheme does.
        val opposite = if (QeetrixTheme.isDark) QeetrixThemeMode.Light else QeetrixThemeMode.Dark
        QeetrixTheme(mode = opposite) {
            val colors = QeetrixTheme.colors
            Column(
                Modifier
                    .fillMaxWidth()
                    .background(colors.surfaceRaised, QeetrixShapes.Surface)
                    .border(QeetrixBorder.WidthDefault, colors.borderDefault, QeetrixShapes.Surface)
                    .padding(QeetrixSpace.InsetMd),
                verticalArrangement = Arrangement.spacedBy(QeetrixSpace.GapXs),
            ) {
                Text("Nested ${opposite.value} scheme", QeetrixTheme.typography.titleSm, color = colors.textDefault)
                val ratio = QeetrixAccessibility.contrastRatio(colors.textSubtle, colors.surfaceRaised)
                Text(
                    "text.subtle on surface.raised: ${"%.2f".format(Locale.ROOT, ratio)}:1",
                    QeetrixTheme.typography.bodySm,
                    color = colors.textSubtle,
                )
            }
        }
    }
}

/**
 * How wide a swatch is: 104 dp — three to a row on a phone — or, when a token name's longest unbroken part needs more,
 * as wide as that part. Names break only after their dots, so a part never breaks inside a word. Measured, not set in
 * em: a fixed 104 dp broke "bold-hover" at the system's 200 % on an API 36 phone and "subtle-pressed" at 100 % on the
 * API 36 tablet, where Fira Code's advances round up at its 2.0 density.
 */
@Composable
private fun swatchWidth(): Dp {
    val label = QeetrixTheme.typography.codeSm
    val measurer = rememberTextMeasurer()
    val density = LocalDensity.current
    return remember(label, density) {
        val parts = ColorGroups.flatMap { (_, roles) -> roles.flatMap { swatchName(it.token).split(ZWSP) } }
        val widest = parts.distinct().maxOf { measurer.measure(it, label, softWrap = false, maxLines = 1).size.width }
        with(density) { widest.toDp() }.coerceAtLeast(104.dp)
    }
}

/** A token name with a break opportunity after each dot. */
private fun swatchName(token: String) = token.replace(".", ".$ZWSP")

private const val ZWSP = "\u200B"

@Composable
private fun Swatch(
    name: String,
    color: Color,
    width: Dp,
) {
    val colors = QeetrixTheme.colors
    Column(Modifier.width(width), verticalArrangement = Arrangement.spacedBy(QeetrixSpace.GapXs)) {
        Box(
            Modifier
                .fillMaxWidth()
                .height(40.dp)
                .background(colors.surfaceRaised, QeetrixShapes.Control)
                .background(color, QeetrixShapes.Control)
                .border(QeetrixBorder.WidthDefault, colors.borderSubtle, QeetrixShapes.Control),
        )
        Text(swatchName(name), QeetrixTheme.typography.codeSm, color = colors.textSubtle)
    }
}

@Composable
private fun TypographySection() {
    Section("Type roles") {
        Caption("Qeet Sans with each role's letter style (display Geo, heading and title Base, body and label A11y); code in Fira Code.")
        for (role in QeetrixTextRole.entries) {
            val style = QeetrixTheme.typography[role]
            val specimen =
                when (role) {
                    QeetrixTextRole.CodeMd, QeetrixTextRole.CodeSm -> "val colors = QeetrixTheme.colors"

                    QeetrixTextRole.BodyLg, QeetrixTextRole.BodyMd, QeetrixTextRole.BodySm -> "Invoices are sent monthly. नमस्ते"

                    // The only uppercase text: a group label, which its component uppercases.
                    QeetrixTextRole.LabelCaps -> "Workspace".uppercase(LocalConfiguration.current.locales[0])

                    else -> "Choose a plan"
                }
            // The line height as the web writes it: a ratio of the size, in sp here.
            val lineHeight = if (style.lineHeight.isEm) style.lineHeight.value * style.fontSize.value else style.lineHeight.value
            Column {
                Text(specimen, style, color = QeetrixTheme.colors.textDefault)
                Text(
                    "${role.value} · ${style.fontSize.value.roundToInt()} / ${lineHeight.roundToInt()} · ${style.fontWeight?.weight}",
                    QeetrixTheme.typography.codeSm,
                    color = QeetrixTheme.colors.textSubtle,
                )
            }
        }
    }
}

@Composable
private fun SpaceSection() {
    Section("Space") {
        Caption("A 4 dp grid: inset inside containers, gap between siblings, layout between sections.")
        val steps: List<Pair<String, List<Pair<String, Dp>>>> =
            listOf(
                "Inset" to
                    listOf(
                        "inset.xs" to QeetrixSpace.InsetXs,
                        "inset.sm" to QeetrixSpace.InsetSm,
                        "inset.md" to QeetrixSpace.InsetMd,
                        "inset.lg" to QeetrixSpace.InsetLg,
                        "inset.xl" to QeetrixSpace.InsetXl,
                    ),
                "Gap" to
                    listOf(
                        "gap.xs" to QeetrixSpace.GapXs,
                        "gap.sm" to QeetrixSpace.GapSm,
                        "gap.md" to QeetrixSpace.GapMd,
                        "gap.lg" to QeetrixSpace.GapLg,
                        "gap.xl" to QeetrixSpace.GapXl,
                    ),
                "Layout" to
                    listOf(
                        "layout.sm" to QeetrixSpace.LayoutSm,
                        "layout.md" to QeetrixSpace.LayoutMd,
                        "layout.lg" to QeetrixSpace.LayoutLg,
                        "layout.xl" to QeetrixSpace.LayoutXl,
                    ),
            )
        for ((group, values) in steps) {
            GroupLabel(group)
            for ((name, value) in values) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("$name ${value.value.toInt()}", QeetrixTheme.typography.codeSm, Modifier.width(120.dp))
                    Box(Modifier.size(width = value, height = QeetrixSpace.InsetSm).background(QeetrixTheme.colors.bgNeutralBold))
                }
            }
        }
    }
}

@Composable
private fun ShapeSection() {
    Section("Radius and borders") {
        Caption("Square: 4 dp on controls and surfaces, 2 dp on small marks; only people and status are round.")
        val radii: List<Triple<String, Dp, Shape>> =
            listOf(
                Triple("none", QeetrixRadius.None, QeetrixShapes.None),
                Triple("sm", QeetrixRadius.Sm, QeetrixShapes.Sm),
                Triple("tooltip", QeetrixRadius.Tooltip, QeetrixShapes.Tooltip),
                Triple("control", QeetrixRadius.Control, QeetrixShapes.Control),
                Triple("surface", QeetrixRadius.Surface, QeetrixShapes.Surface),
                Triple("overlay", QeetrixRadius.Overlay, QeetrixShapes.Overlay),
                Triple("modal", QeetrixRadius.Modal, QeetrixShapes.Modal),
                Triple("chip", QeetrixRadius.Chip, QeetrixShapes.Chip),
                Triple("full", QeetrixRadius.Full, QeetrixShapes.Full),
            )
        FlowGrid {
            for ((name, radius, shape) in radii) {
                Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(QeetrixSpace.GapXs)) {
                    Box(
                        Modifier
                            .size(width = 72.dp, height = 48.dp)
                            .background(QeetrixTheme.colors.surfaceSunken, shape)
                            .border(QeetrixBorder.WidthDefault, QeetrixTheme.colors.borderStrong, shape),
                    )
                    val value = if (radius.value > 100f) "pill" else radius.value.toInt().toString()
                    Text("$name $value", QeetrixTheme.typography.codeSm, color = QeetrixTheme.colors.textSubtle)
                }
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(QeetrixSpace.GapXs)) {
                // A filled field: control corners on top, square at its strong bottom line.
                Column(Modifier.width(72.dp)) {
                    Box(Modifier.fillMaxWidth().height(47.dp).background(QeetrixTheme.colors.bgNeutralSubtle, QeetrixShapes.Field))
                    Box(Modifier.fillMaxWidth().height(QeetrixBorder.WidthDefault).background(QeetrixTheme.colors.borderStrong))
                }
                Text("field", QeetrixTheme.typography.codeSm, color = QeetrixTheme.colors.textSubtle)
            }
        }
        GroupLabel("Border widths")
        val widths =
            listOf(
                Triple("width.default", QeetrixBorder.WidthDefault, QeetrixTheme.colors.borderDefault),
                Triple("width.strong", QeetrixBorder.WidthStrong, QeetrixTheme.colors.borderStrong),
                // The Ember bar: the current item, and a dialog's intent line.
                Triple("width.accent", QeetrixBorder.WidthAccent, QeetrixTheme.colors.borderBrand),
            )
        for ((name, width, color) in widths) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("$name ${width.value.toInt()}", QeetrixTheme.typography.codeSm, Modifier.width(120.dp))
                Box(Modifier.weight(1f).height(width).background(color))
            }
        }
    }
}

@Composable
private fun ShadowSection() {
    Section("Shadows") {
        Caption("Only floating layers have shadows: overlay for menus and toasts, modal for dialogs and sheets. Cards stay flat.")
        FlowGrid {
            for (shadow in QeetrixShadow.entries) {
                val fill = if (shadow == QeetrixShadow.Inset) QeetrixTheme.colors.surfaceSunken else QeetrixTheme.colors.surfaceOverlay
                Box(
                    Modifier
                        .padding(QeetrixSpace.InsetSm)
                        .size(width = 120.dp, height = 64.dp)
                        .qeetrixShadow(shadow, QeetrixShapes.Surface, fill),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(shadow.token.removePrefix("shadow."), QeetrixTheme.typography.codeSm, color = QeetrixTheme.colors.textDefault)
                }
            }
        }
    }
}

@Composable
private fun MotionSection() {
    Section("Motion") {
        Caption("Productive: quick, never bouncing or scaling. Under Remove animations every duration is 0, so changes snap.")
        GroupLabel("Durations")
        val durations =
            listOf(
                "instant" to QeetrixMotion.DurationInstant,
                "fast" to QeetrixMotion.DurationFast,
                "normal" to QeetrixMotion.DurationNormal,
                "slow" to QeetrixMotion.DurationSlow,
                "slower" to QeetrixMotion.DurationSlower,
            )
        for ((name, millis) in durations) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("duration.$name ${millis}ms", QeetrixTheme.typography.codeSm, Modifier.width(176.dp))
                Box(Modifier.size(width = (millis / 4).dp, height = QeetrixSpace.InsetSm).background(QeetrixTheme.colors.bgNeutralBold))
            }
        }
        GroupLabel("Easings, over duration.slow")
        var moved by rememberSaveable { mutableStateOf(false) }
        val easings: List<Pair<String, Easing>> =
            listOf(
                "standard" to QeetrixMotion.EasingStandard,
                "enter" to QeetrixMotion.EasingEnter,
                "exit" to QeetrixMotion.EasingExit,
                "emphasized" to QeetrixMotion.EasingEmphasized,
                "linear" to QeetrixMotion.EasingLinear,
            )
        for ((name, easing) in easings) {
            val progress by animateFloatAsState(
                if (moved) 1f else 0f,
                qeetrixAnimationSpec(easing, QeetrixMotion.DurationSlow),
                label = "easing-$name",
            )
            Column(verticalArrangement = Arrangement.spacedBy(QeetrixSpace.GapXs)) {
                Text("easing.$name", QeetrixTheme.typography.codeSm)
                Track(progress)
            }
        }
        QeetrixButton(if (moved) "Move back" else "Move", onClick = {
            moved = !moved
        }, variant = QeetrixButtonVariant.Soft, size = QeetrixButtonSize.Sm)
        GroupLabel("Springs")
        for ((name, spring) in listOf<Pair<String, QeetrixSpring>>(
            "snappy" to QeetrixMotion.SpringSnappy,
            "gentle" to QeetrixMotion.SpringGentle,
        )) {
            Text("spring.$name stiffness ${spring.stiffness} · damping ${spring.dampingRatio}", QeetrixTheme.typography.codeSm)
        }
    }
}

/** A rail with a mark that travels along it at [progress], mirrored right to left. */
@Composable
private fun Track(progress: Float) {
    val colors = QeetrixTheme.colors
    BoxWithConstraints(Modifier.fillMaxWidth().height(QeetrixSize.IconSm)) {
        val travel = maxWidth - QeetrixSize.IconSm
        Box(
            Modifier
                .align(Alignment.CenterStart)
                .fillMaxWidth()
                .height(QeetrixBorder.WidthDefault)
                .background(colors.borderDefault),
        )
        Box(
            Modifier
                .padding(start = travel * progress)
                .size(QeetrixSize.IconSm)
                .background(colors.bgNeutralBold, QeetrixShapes.Sm),
        )
    }
}

@Composable
private fun DensitySection() {
    Section("Densities") {
        Caption("One set of components, three sizes, set once per region with LocalQeetrixDensity.")
        for (density in QeetrixDensity.entries) {
            CompositionLocalProvider(LocalQeetrixDensity provides density) {
                val current = QeetrixTheme.density
                GroupLabel(current.value)
                Row(horizontalArrangement = Arrangement.spacedBy(QeetrixSpace.GapSm), verticalAlignment = Alignment.Bottom) {
                    for ((name, height) in listOf(
                        "sm" to current.sizeControlSm,
                        "md" to current.sizeControlMd,
                        "lg" to current.sizeControlLg,
                    )) {
                        Box(
                            Modifier
                                .size(width = 64.dp, height = height)
                                .background(QeetrixTheme.colors.bgNeutralSubtle, QeetrixShapes.Control),
                            contentAlignment = Alignment.Center,
                        ) { Text("$name ${height.value.toInt()}", QeetrixTheme.typography.codeSm) }
                    }
                }
                Text(
                    "row ${current.sizeRow.value.toInt()} · form gap ${current.spaceFormGap.value.toInt()} · icon stroke ${current.iconStroke}",
                    QeetrixTheme.typography.codeSm,
                    color = QeetrixTheme.colors.textSubtle,
                )
            }
        }
    }
}

@Composable
private fun IconSection() {
    Section("Icons") {
        Caption(
            "@qeetrix/icons in both shapes and both variants: sharp on controls, round in navigation, filled only for a " +
                "selected or active state. An icon with no filled drawing is left out of the filled rows.",
        )
        for (shape in QeetrixIconShape.entries) {
            for (variant in QeetrixIconVariant.entries) {
                GroupLabel("${shape.value} ${variant.value}")
                FlowGrid {
                    for (name in QeetrixIconName.entries) {
                        if (name.hasDrawing(shape, variant)) {
                            QeetrixIcon(name, contentDescription = name.value, size = QeetrixSize.IconLg, shape = shape, variant = variant)
                        }
                    }
                }
            }
        }
        GroupLabel("Selected, and right to left")
        Row(horizontalArrangement = Arrangement.spacedBy(QeetrixSpace.GapMd), verticalAlignment = Alignment.CenterVertically) {
            // Ember only where the icon is part of a current or selected item.
            QeetrixIcon(
                QeetrixIconName.House,
                contentDescription = "Home, current",
                size = QeetrixSize.IconLg,
                shape = QeetrixIconShape.Round,
                variant = QeetrixIconVariant.Filled,
                tint = QeetrixTheme.colors.iconBrand,
            )
            CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                QeetrixIcon(QeetrixIconName.Send, contentDescription = "Send, right to left", size = QeetrixSize.IconLg)
                // Arrows and chevrons never mirror by themselves (@qeetrix/icons' catalogue); "next" asks for it (QTRX-200).
                QeetrixIcon(
                    QeetrixIconName.ArrowRight,
                    contentDescription = "Next, right to left",
                    size = QeetrixSize.IconLg,
                    mirrorInRightToLeft = true,
                )
            }
            Text("send mirrors itself; arrow-right is mirrored by the app", QeetrixTheme.typography.bodySm, Modifier.weight(1f))
        }
    }
}

/** A foundations section: a ruled, flat surface (no shadow) with a `title.lg` heading. */
@Composable
private fun Section(
    title: String,
    content: @Composable ColumnScope.() -> Unit,
) {
    val colors = QeetrixTheme.colors
    Column(
        Modifier
            .fillMaxWidth()
            .background(colors.surfaceRaised, QeetrixShapes.Surface)
            .border(QeetrixBorder.WidthDefault, colors.borderDefault, QeetrixShapes.Surface)
            .padding(QeetrixSpace.InsetLg),
        verticalArrangement = Arrangement.spacedBy(QeetrixSpace.GapMd),
    ) {
        QeetrixHeading(title, level = 2, size = QeetrixHeadingSize.TitleLg)
        content()
    }
}

/** A group label: `label.caps` in `color.text.subtle` — the one uppercase style (design rules §5). */
@Composable
private fun GroupLabel(text: String) {
    Text(
        text.uppercase(LocalConfiguration.current.locales[0]),
        QeetrixTheme.typography.labelCaps,
        Modifier.padding(top = QeetrixSpace.GapSm),
        QeetrixTheme.colors.textSubtle,
    )
}

@Composable
private fun Caption(text: String) {
    Text(text, QeetrixTheme.typography.bodySm, color = QeetrixTheme.colors.textSubtle)
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun FlowGrid(content: @Composable () -> Unit) {
    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(QeetrixSpace.GapMd),
        verticalArrangement = Arrangement.spacedBy(QeetrixSpace.GapMd),
    ) { content() }
}

@Composable
private fun Text(
    text: String,
    style: TextStyle,
    modifier: Modifier = Modifier,
    color: Color = QeetrixTheme.colors.textDefault,
) {
    BasicText(text, modifier, style = style.copy(color = color))
}

@Preview(widthDp = 400, heightDp = 1800)
@Composable
private fun FoundationsScreenLightPreview() {
    QeetrixTheme(QeetrixThemeMode.Light) { SampleCanvas { FoundationsScreen() } }
}

@Preview(widthDp = 800, heightDp = 1200)
@Composable
private fun FoundationsScreenDarkWidePreview() {
    QeetrixTheme(QeetrixThemeMode.Dark) { SampleCanvas { FoundationsScreen() } }
}

@Composable
private fun SampleCanvas(content: @Composable () -> Unit) {
    Box(Modifier.background(QeetrixTheme.colors.surfaceCanvas).padding(QeetrixSpace.InsetLg)) { content() }
}
