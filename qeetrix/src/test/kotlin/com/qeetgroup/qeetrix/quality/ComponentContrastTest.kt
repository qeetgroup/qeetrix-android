package com.qeetgroup.qeetrix.quality

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.test.junit4.createComposeRule
import com.qeetgroup.qeetrix.components.badge.QeetrixBadgeTone
import com.qeetgroup.qeetrix.components.badge.QeetrixBadgeVariant
import com.qeetgroup.qeetrix.components.badge.badgeColors
import com.qeetgroup.qeetrix.components.button.QeetrixButtonTone
import com.qeetgroup.qeetrix.components.button.QeetrixButtonVariant
import com.qeetgroup.qeetrix.components.button.buttonColors
import com.qeetgroup.qeetrix.components.dialog.QeetrixDialogIntent
import com.qeetgroup.qeetrix.components.input.fieldColors
import com.qeetgroup.qeetrix.components.progress.QeetrixMeterTone
import com.qeetgroup.qeetrix.components.progress.indicator
import com.qeetgroup.qeetrix.components.progress.progressTrack
import com.qeetgroup.qeetrix.components.switchcontrol.switchThumb
import com.qeetgroup.qeetrix.components.typography.QeetrixTextTone
import com.qeetgroup.qeetrix.components.typography.toneColor
import com.qeetgroup.qeetrix.foundation.accessibility.QeetrixAccessibility
import com.qeetgroup.qeetrix.foundation.color.QeetrixColors
import com.qeetgroup.qeetrix.theme.QeetrixTheme
import com.qeetgroup.qeetrix.theme.QeetrixThemeMode
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

/**
 * Contrast of the colour pairs the components actually draw, in both themes, from the resolved colours: text on the
 * fills it sits on (WCAG 2.2 1.4.3, 4.5:1), and the boundaries, marks and indicators that identify a control or its
 * state against what is next to them (1.4.11, 3:1). Translucent colours are composited as they are drawn — a fill
 * over the surface it sits on, a colour over that fill. Where a component computes its colours in one function
 * (`buttonColors`, `badgeColors`, `fieldColors`, `toneColor`, the dialog's intent line), the check calls it for every
 * variant, tone and state, so the pairs follow the component; the rest name the tokens the component draws, with
 * the file that draws them.
 *
 * Disabled controls are exempt (1.4.3). A failure that comes from canonical Qeetrix tokens is listed in
 * [CanonicalFindings] with its context — recorded and proposed upstream, not patched with an Android colour; the
 * test fails both on a new failure and on a listed one that passes.
 */
@RunWith(RobolectricTestRunner::class)
class ComponentContrastTest {
    @get:Rule
    val compose = createComposeRule()

    private class Check(
        val context: String,
        val minimum: Double,
        val foreground: @Composable () -> Color,
        val background: @Composable () -> Color,
        val under: @Composable () -> Color,
    )

    private val checks = mutableListOf<Check>()

    private fun text(
        context: String,
        foreground: @Composable () -> Color,
        background: @Composable () -> Color,
        under: @Composable () -> Color = { QeetrixTheme.colors.surfaceCanvas },
    ) {
        checks += Check(context, TEXT, foreground, background, under)
    }

    private fun graphic(
        context: String,
        foreground: @Composable () -> Color,
        background: @Composable () -> Color,
        under: @Composable () -> Color = { QeetrixTheme.colors.surfaceCanvas },
    ) {
        checks += Check(context, GRAPHIC, foreground, background, under)
    }

    /** The surfaces content sits on in the page: the canvas, the default surface (bars, sheets' fields) and cards. */
    private val page: List<Pair<String, @Composable () -> Color>> =
        listOf(
            "surface.canvas" to { QeetrixTheme.colors.surfaceCanvas },
            "surface.default" to { QeetrixTheme.colors.surfaceDefault },
            "surface.raised" to { QeetrixTheme.colors.surfaceRaised },
        )

    /** The page surfaces and the overlay surface menus, dialogs, sheets and snackbars are drawn on. */
    private val surfaces: List<Pair<String, @Composable () -> Color>> = page + ("surface.overlay" to { QeetrixTheme.colors.surfaceOverlay })

    private fun colors(pick: QeetrixColors.() -> Color): @Composable () -> Color = { QeetrixTheme.colors.pick() }

    init {
        // Buttons and the tools inside fields (button/QeetrixButtonFrame.kt buttonColors): every variant and tone, at
        // rest, under the pointer and pressed, on every surface a button sits on. A ghost button has no fill at rest.
        for (variant in QeetrixButtonVariant.entries) {
            for (tone in QeetrixButtonTone.entries) {
                for ((state, hovered, pressed) in listOf(
                    Triple("rest", false, false),
                    Triple("hover", true, false),
                    Triple("pressed", false, true),
                )) {
                    for ((name, surface) in surfaces) {
                        text(
                            "button ${variant.value} ${tone.value} $state: label on its fill on $name",
                            { buttonColors(variant, tone, enabled = true, hovered = hovered, pressed = pressed).content },
                            { buttonColors(variant, tone, enabled = true, hovered = hovered, pressed = pressed).background },
                            surface,
                        )
                    }
                }
            }
        }
        // A button's focus frame, inside its edge with a 1 dp surface.default gap (design rules §7, §10).
        graphic("button focus frame border.focus on its surface.default gap", colors { borderFocus }, colors { surfaceDefault })

        // Status tags (badge/QeetrixBadge.kt badgeColors), on every surface.
        for (variant in QeetrixBadgeVariant.entries) {
            for (tone in QeetrixBadgeTone.entries) {
                for ((name, surface) in surfaces) {
                    text(
                        "badge ${variant.value} ${tone.value}: text on its fill on $name",
                        { badgeColors(tone, variant).second },
                        { badgeColors(tone, variant).first },
                        surface,
                    )
                }
            }
        }

        // Text tones (typography/QeetrixText.kt toneColor) on the page and overlay surfaces; inverse on surface.inverse.
        for (tone in QeetrixTextTone.entries - QeetrixTextTone.Disabled - QeetrixTextTone.Inverse) {
            for ((name, surface) in surfaces) text("text ${tone.value} on $name", { toneColor(tone) }, surface)
        }
        text("text inverse on surface.inverse", { toneColor(QeetrixTextTone.Inverse) }, colors { surfaceInverse })
        for ((name, surface) in page) text("text.link on $name", colors { textLink }, surface)

        // Fields (input/QeetrixFieldParts.kt fieldColors): the value and the placeholder (text.subtlest) on the fill,
        // the bottom line and the 2 dp frames against the fill — in every enabled state, on every page surface.
        val states =
            listOf(
                "rest" to listOf(false, false, false, false),
                "hover" to listOf(true, false, false, false),
                "focused" to listOf(false, true, false, false),
                "invalid" to listOf(false, false, true, false),
                "read-only" to listOf(false, false, false, true),
            )
        for ((state, flags) in states) {
            val (hovered, focused, invalid, readOnly) = flags
            val field = @Composable {
                fieldColors(
                    enabled = true,
                    readOnly = readOnly,
                    focused = focused,
                    hovered = hovered,
                    invalid = invalid,
                )
            }
            for ((name, surface) in page) {
                text("field $state: value on the fill on $name", { field().content }, { field().fill }, surface)
                text("field $state: placeholder text.subtlest on the fill on $name", colors { textSubtlest }, { field().fill }, surface)
                // The line and the frame mark the field's edge: against the fill inside and the surface around (a
                // read-only field has no fill).
                if (!readOnly) {
                    graphic(
                        "field $state: edge (line or frame) against the fill on $name",
                        { field().frame ?: field().line!! },
                        { field().fill },
                        surface,
                    )
                }
                graphic("field $state: edge (line or frame) against $name", { field().frame ?: field().line!! }, surface)
            }
        }
        for ((name, surface) in page) {
            // input/QeetrixFieldParts.kt: the label, " (optional)", the help text and the error with its icon.
            text("field label text.subtle on $name", colors { textSubtle }, surface)
            text("field (optional) text.subtlest on $name", colors { textSubtlest }, surface)
            text("field error text.danger on $name", colors { textDanger }, surface)
            graphic("field error icon.danger on $name", colors { iconDanger }, surface)
            // otpinput/QeetrixOTPInput.kt: the group separator between slots.
            graphic("otp group separator border.strong on $name", colors { borderStrong }, surface)
            // numberfield/QeetrixNumberField.kt: the steppers' icons on the field's fill, at rest, hovered and pressed.
            graphic(
                "number-field stepper icon.default on bg.neutral.subtle on $name",
                colors { iconDefault },
                colors { bgNeutralSubtle },
                surface,
            )
            graphic(
                "number-field stepper icon.default on overlay.pressed over bg.neutral.subtle on $name",
                colors { iconDefault },
                { QeetrixTheme.colors.overlayPressed.compositeOver(QeetrixTheme.colors.bgNeutralSubtle) },
                surface,
            )
        }

        for ((name, surface) in surfaces) {
            // checkbox/QeetrixCheckbox.kt, radiogroup/QeetrixRadioGroup.kt, switchcontrol/QeetrixSwitch.kt: the
            // control's edge against its bg.input fill and against the surface; the checked fill against the surface.
            graphic("checkbox, radio, switch edge border.strong on bg.input on $name", colors { borderStrong }, colors { bgInput }, surface)
            graphic("checkbox, radio, switch edge border.strong on $name", colors { borderStrong }, surface)
            graphic("checkbox, radio, switch edge border.strong-hover on $name", colors { borderStrongHover }, surface)
            graphic("checkbox, radio, switch invalid edge border.danger on $name", colors { borderDanger }, surface)
            graphic("checked checkbox, radio ring, switch track bg.brand.bold on $name", colors { bgBrandBold }, surface)
            graphic("checked, hovered bg.brand.bold-hover on $name", colors { bgBrandBoldHover }, surface)
            // Focus: the 2 dp Ember frame, outside a selection control or inside a field, row or tab (design rules §10).
            graphic("focus frame border.focus on $name", colors { borderFocus }, surface)
        }
        graphic("checkbox tick icon.on-brand on bg.brand.bold", colors { iconOnBrand }, colors { bgBrandBold })
        // switchcontrol/QeetrixSwitch.kt: the on thumb, icon.on-brand, white in both schemes (switch.css 0.4.0).
        val thumb: @Composable () -> Color = { switchThumb(QeetrixTheme.colors, enabled = true, checked = true) }
        graphic("switch on thumb on bg.brand.bold", thumb, colors { bgBrandBold })
        graphic("switch on thumb on bg.brand.bold-hover", thumb, colors { bgBrandBoldHover })
        graphic("radio's centre bg.input inside its bg.brand.bold ring", colors { bgInput }, colors { bgBrandBold })
        graphic("switch thumb (off) icon.subtle on bg.input", colors { iconSubtle }, colors { bgInput })
        graphic("switch thumb (on) surface.default on bg.brand.bold", colors { surfaceDefault }, colors { bgBrandBold })

        // Current and selected marks (design rules §11).
        for ((name, surface) in page) {
            // tabs/QeetrixTabs.kt: the Ember bar under the selected tab; the others' labels; contained tabs' segments.
            graphic("tab bar border.brand on $name", colors { borderBrand }, surface)
            text("tab label text.subtle on $name", colors { textSubtle }, surface)
            text(
                "contained tab label text.subtle on bg.neutral.subtle on $name",
                colors { textSubtle },
                colors { bgNeutralSubtle },
                surface,
            )
            text(
                "contained tab hovered text.default on bg.neutral.subtle-hover on $name",
                colors {
                    textDefault
                },
                colors { bgNeutralSubtleHover },
                surface,
            )
            text(
                "contained tab pressed text.default on bg.neutral.subtle-pressed on $name",
                colors {
                    textDefault
                },
                colors { bgNeutralSubtlePressed },
                surface,
            )
            // list/QeetrixListItem.kt, card/QeetrixCard.kt, badge/QeetrixChip.kt: selected rows, cards and chips.
            text("selected row, card, chip text.default on bg.selected on $name", colors { textDefault }, colors { bgSelected }, surface)
            text(
                "selected row, card, chip text.default on bg.selected-hover on $name",
                colors { textDefault },
                colors { bgSelectedHover },
                surface,
            )
            text("selected row description text.subtle on bg.selected on $name", colors { textSubtle }, colors { bgSelected }, surface)
            graphic(
                "selected card ring, chip edge border.brand on bg.selected on $name",
                colors { borderBrand },
                colors { bgSelected },
                surface,
            )
            text(
                "pressed row, card text.default on bg.neutral.subtle-hover on $name",
                colors { textDefault },
                colors { bgNeutralSubtleHover },
                surface,
            )
            text(
                "chip text.default on bg.neutral.subtle-pressed on $name",
                colors { textDefault },
                colors { bgNeutralSubtlePressed },
                surface,
            )
            // list/QeetrixListItem.kt: the leading icon tile; list/QeetrixSectionHeader.kt: the group label.
            graphic("list icon tile icon.subtle on bg.neutral.subtle on $name", colors { iconSubtle }, colors { bgNeutralSubtle }, surface)
            text("section header label text.subtlest on $name", colors { textSubtlest }, surface)
            // descriptionlist/QeetrixDescriptionList.kt: a value that is not set.
            text("description list empty value text.subtlest on $name", colors { textSubtlest }, surface)
        }
        // navigation/QeetrixNavigationItem.kt on the bar's and rail's surface.canvas: the current item's tint, bar,
        // filled Ember icon and label; the others' labels and icons; a hovered or pressed one.
        graphic(
            "current navigation item bar border.brand on bg.neutral.subtle-hover",
            colors { borderBrand },
            colors { bgNeutralSubtleHover },
        )
        graphic("current navigation item icon.brand on bg.neutral.subtle-hover", colors { iconBrand }, colors { bgNeutralSubtleHover })
        text("current navigation item text.default on bg.neutral.subtle-hover", colors { textDefault }, colors { bgNeutralSubtleHover })
        text("navigation item text.subtle on surface.canvas", colors { textSubtle }, colors { surfaceCanvas })
        graphic("navigation item icon.subtle on surface.canvas", colors { iconSubtle }, colors { surfaceCanvas })
        text(
            "pressed navigation item text.default on overlay.pressed",
            colors { textDefault },
            colors { overlayPressed },
        )
        // The badge over an icon (QeetrixBadge sm, neutral subtle) on the current item's tint.
        text(
            "navigation badge on bg.neutral.subtle-hover",
            { badgeColors(QeetrixBadgeTone.Neutral, QeetrixBadgeVariant.Subtle).second },
            { badgeColors(QeetrixBadgeTone.Neutral, QeetrixBadgeVariant.Subtle).first },
            colors { bgNeutralSubtleHover },
        )

        // dropdownmenu/QeetrixMenu.kt and select/QeetrixSelect.kt on surface.overlay: items, their descriptions and
        // shortcuts, icons, the danger item, and the highlighted row's tint and Ember edge; the chosen option's check.
        val overlay = colors { surfaceOverlay }
        val highlight = colors { bgNeutralSubtleHover }
        for ((state, fill) in listOf("rest" to overlay, "highlighted" to highlight)) {
            text("menu item, option text.default ($state)", colors { textDefault }, fill, overlay)
            text("menu item, option description text.subtle ($state)", colors { textSubtle }, fill, overlay)
            text("menu shortcut text.subtlest ($state)", colors { textSubtlest }, fill, overlay)
            text("danger menu item text.danger ($state)", colors { textDanger }, fill, overlay)
            graphic("menu item icon.subtle ($state)", colors { iconSubtle }, fill, overlay)
            graphic("danger menu item icon.danger ($state)", colors { iconDanger }, fill, overlay)
            graphic("chosen option check icon.brand ($state)", colors { iconBrand }, fill, overlay)
        }
        graphic("highlighted row edge border.brand on bg.neutral.subtle-hover", colors { borderBrand }, highlight, overlay)
        text("menu group label text.subtle on surface.overlay", colors { textSubtle }, overlay, overlay)

        // dialog/QeetrixDialog.kt: the 3 dp intent line on the popup's top edge, for every intent.
        for (intent in QeetrixDialogIntent.entries) {
            graphic("dialog ${intent.value} intent line on surface.overlay", { intent.lineColor(QeetrixTheme.colors) }, overlay, overlay)
        }
        // dialog/QeetrixDialogParts.kt: the description; the sheet's and dialog's title is text.default.
        text("dialog, sheet description text.subtle on surface.overlay", colors { textSubtle }, overlay, overlay)

        // toast/QeetrixSnackbar.kt: the 3 dp status edge and the status icon, both in the tone's icon colour; the text.
        for ((tone, color) in listOf<Pair<String, QeetrixColors.() -> Color>>(
            "info" to { iconInfo },
            "success" to { iconSuccess },
            "warning" to { iconWarning },
            "danger" to { iconDanger },
        )) {
            graphic("snackbar $tone edge and icon on surface.overlay", colors(color), overlay, overlay)
        }
        text("snackbar description text.subtle on surface.overlay", colors { textSubtle }, overlay, overlay)

        for ((name, surface) in page) {
            // feed/QeetrixTimeline.kt: an event's icon, in its tone, in the bg.neutral.subtle marker; the compact dot.
            for ((tone, color) in listOf<Pair<String, QeetrixColors.() -> Color>>(
                "neutral" to { iconSubtle },
                "brand" to { iconBrand },
                "info" to { iconInfo },
                "success" to { iconSuccess },
                "warning" to { iconWarning },
                "danger" to { iconDanger },
            )) {
                graphic("timeline $tone icon on its bg.neutral.subtle marker on $name", colors(color), colors { bgNeutralSubtle }, surface)
                graphic("compact timeline $tone dot on $name", if (tone == "neutral") colors { borderStrong } else colors(color), surface)
            }
            graphic("meter target tick text.default on $name", colors { textDefault }, surface)
            // emptystate/QeetrixEmptyState.kt: the icon, neutral or danger.
            graphic("empty state icon.subtle on $name", colors { iconSubtle }, surface)
            graphic("empty state icon.danger on $name", colors { iconDanger }, surface)
        }
        // progress/QeetrixProgress.kt: each tone's indicator against the track it fills (opaque, so on any surface), from
        // the component's own rules: the track is border.default in light and surface.sunken in dark, the warning fill
        // border.warning in light and bg.warning.bold in dark (progress.css 0.4.0, QTRX-176). Meter's six tones include
        // Progress's three.
        val track: @Composable () -> Color = { progressTrack(QeetrixTheme.colors, QeetrixTheme.isDark) }
        for (tone in QeetrixMeterTone.entries) {
            val fill: @Composable () -> Color = { tone.indicator(QeetrixTheme.colors, QeetrixTheme.isDark) }
            graphic("progress, meter ${tone.value} indicator on its track", fill, track)
        }
        // avatar/QeetrixAvatar.kt: initials on the avatar's fill.
        for ((name, surface) in page) {
            text(
                "avatar initials text.default on bg.neutral.subtle-pressed on $name",
                colors {
                    textDefault
                },
                colors { bgNeutralSubtlePressed },
                surface,
            )
        }
        // scanner/QeetrixQRScanner.kt: the guidance over the camera on its own fill (the translucent scrim over a white
        // page would leave text.on-bold at 2.8:1 in light); the torch and the fallback are buttons, checked above.
        text("qr scanner guidance text.inverse on bg.neutral.bold", colors { textInverse }, colors { bgNeutralBold })
        // form/QeetrixForm.kt: the error summary's title, messages and links, and its icon, on bg.danger.subtle.
        for ((name, surface) in page) {
            text("error summary text.default on bg.danger.subtle on $name", colors { textDefault }, colors { bgDangerSubtle }, surface)
            text("error summary link text.link on bg.danger.subtle on $name", colors { textLink }, colors { bgDangerSubtle }, surface)
            text(
                "error summary link text.link-hover on bg.danger.subtle on $name",
                colors { textLinkHover },
                colors { bgDangerSubtle },
                surface,
            )
            graphic("error summary icon.danger on bg.danger.subtle on $name", colors { iconDanger }, colors { bgDangerSubtle }, surface)
        }
    }

    /** Every check's ratio in each theme, as the components draw it. */
    private fun measure(checks: List<Check>): List<Triple<String, Check, Double>> {
        val measured = mutableListOf<Triple<String, Check, Double>>()
        compose.setContent {
            for (mode in listOf(QeetrixThemeMode.Light, QeetrixThemeMode.Dark)) {
                QeetrixTheme(mode) {
                    for (check in checks) {
                        val drawn = check.background().compositeOver(check.under().compositeOver(QeetrixTheme.colors.surfaceCanvas))
                        val ratio = QeetrixAccessibility.contrastRatio(check.foreground().compositeOver(drawn), drawn)
                        measured += Triple(mode.value, check, ratio)
                    }
                }
            }
        }
        compose.waitForIdle()
        return measured.distinctBy { it.first + it.second.context }
    }

    @Test
    fun componentColourPairsMeetWcagContrast() {
        val failing = mutableListOf<String>()
        for ((mode, check, ratio) in measure(checks)) {
            val line = "%-5s %-100s %5.2f (needs %.1f)".format(mode, check.context, ratio, check.minimum)
            println("CONTRAST $line")
            if (ratio < check.minimum) failing += "$mode: ${check.context}"
        }
        assertTrue("checks: ${checks.size}", checks.size > 300)
        assertEquals("Contrast failures not recorded as canonical findings", CanonicalFindings.keys.sorted(), failing.sorted())
    }

    /**
     * The measurement can fail: a mid grey on itself is 1:1, and two pairs Qeetrix draws on purpose below the
     * minimums — disabled text, and the subtle rule that separates but does not identify — come out below them.
     */
    @Test
    fun theCheckCatchesPairsBelowTheMinimum() {
        val controls =
            listOf(
                Check("grey on grey", GRAPHIC, { Color.Gray }, { Color.Gray }, { Color.Gray }),
                Check("text.disabled on surface.canvas", TEXT, colors { textDisabled }, colors { surfaceCanvas }, colors { surfaceCanvas }),
                Check(
                    "border.subtle on surface.canvas",
                    GRAPHIC,
                    colors { borderSubtle },
                    colors { surfaceCanvas },
                    colors { surfaceCanvas },
                ),
            )
        val passing = measure(controls).filter { (_, check, ratio) -> ratio >= check.minimum }.map { "${it.first}: ${it.second.context}" }
        assertEquals(emptyList<String>(), passing)
    }

    private companion object {
        const val GRAPHIC = 3.0
        const val TEXT = 4.5

        /**
         * Canonical-token failures — quality findings (Q-n) for docs/quality.md, to be
         * proposed upstream: the failing check, and why it stands.
         *
         * Q-8 (read-only field lines), Q-9 (progress fills on their track) and Q-10 (a hovered checked control on the
         * dark overlay) were fixed upstream in `@qeetrix/react` 0.4.0 and `@qeetrix/tokens` 0.0.13 (QTRX-175, QTRX-176,
         * QTRX-187), and those checks pass; none is recorded.
         */
        val CanonicalFindings: Map<String, String> = emptyMap()
    }
}
