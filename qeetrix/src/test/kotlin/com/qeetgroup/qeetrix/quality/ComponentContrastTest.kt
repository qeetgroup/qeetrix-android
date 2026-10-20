package com.qeetgroup.qeetrix.quality

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.test.junit4.createComposeRule
import com.qeetgroup.qeetrix.foundation.accessibility.QeetrixAccessibility
import com.qeetgroup.qeetrix.theme.QeetrixTheme
import com.qeetgroup.qeetrix.theme.QeetrixThemeMode
import com.qeetgroup.qeetrix.tokens.generated.QeetrixBadgeComponentTokens
import com.qeetgroup.qeetrix.tokens.generated.QeetrixButtonComponentTokens
import com.qeetgroup.qeetrix.tokens.generated.QeetrixInputComponentTokens
import com.qeetgroup.qeetrix.tokens.generated.QeetrixProgressComponentTokens
import com.qeetgroup.qeetrix.tokens.generated.QeetrixSwitchComponentTokens
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

/**
 * Contrast of the colour pairs the components actually draw, beyond the text pairs qeetrix-ui designs together
 * (`ContrastTest`): control boundaries and state indicators against the surfaces they sit on (WCAG 2.2 1.4.11,
 * 3:1), and text on pressed and component surfaces (1.4.3, 4.5:1), in both themes, from the resolved colours —
 * translucent backgrounds composited over the canvas, as they are drawn.
 * Disabled controls are exempt (1.4.3). A failure that comes from canonical Qeetrix tokens is listed in
 * [CanonicalFindings] with its context — recorded and proposed upstream, not patched with an Android colour.
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
    )

    private val graphic = 3.0
    private val text = 4.5

    private val checks: List<Check> =
        buildList {
            val surfaces: List<Pair<String, @Composable () -> Color>> =
                listOf(
                    "surface.canvas" to { QeetrixTheme.colors.surfaceCanvas },
                    "surface.default" to { QeetrixTheme.colors.surfaceDefault },
                )
            for ((name, surface) in surfaces) {
                // Boundaries that identify a control: fields, checkboxes, radios; the invalid boundary.
                add(Check("input.border (field, checkbox, radio) on $name", graphic, { QeetrixInputComponentTokens.Border }, surface))
                add(Check("input.border-invalid on $name", graphic, { QeetrixInputComponentTokens.BorderInvalid }, surface))
                add(Check("switch.track (off) on $name", graphic, { QeetrixSwitchComponentTokens.Track }, surface))
                add(Check("switch.track-checked on $name", graphic, { QeetrixSwitchComponentTokens.TrackChecked }, surface))
                add(Check("action.primary (checked box) on $name", graphic, { QeetrixTheme.colors.actionPrimary }, surface))
                // State indicators: tab line, navigation indicator, selected chip edge, selected row edge.
                add(Check("border.brand (selection indicators) on $name", graphic, { QeetrixTheme.colors.borderBrand }, surface))
                add(Check("focus ring on $name", graphic, { QeetrixTheme.colors.focusRing }, surface))
                // Timeline dots, which carry an event's tone (always with words).
                add(Check("timeline dot text.tertiary on $name", graphic, { QeetrixTheme.colors.textTertiary }, surface))
                add(Check("timeline dot action.primary on $name", graphic, { QeetrixTheme.colors.actionPrimary }, surface))
                add(Check("timeline dot feedback.info on $name", graphic, { QeetrixTheme.colors.feedbackInfo }, surface))
                add(Check("timeline dot feedback.success on $name", graphic, { QeetrixTheme.colors.feedbackSuccess }, surface))
                add(Check("timeline dot feedback.warning on $name", graphic, { QeetrixTheme.colors.feedbackWarning }, surface))
                add(Check("timeline dot feedback.error on $name", graphic, { QeetrixTheme.colors.feedbackError }, surface))
            }
            add(
                Check(
                    "input.border on input.background",
                    graphic,
                    { QeetrixInputComponentTokens.Border },
                    { QeetrixInputComponentTokens.Background },
                ),
            )
            add(
                Check(
                    "switch.thumb on switch.track",
                    graphic,
                    { QeetrixSwitchComponentTokens.Thumb },
                    { QeetrixSwitchComponentTokens.Track },
                ),
            )
            add(
                Check("switch.thumb-checked on switch.track-checked", graphic, {
                    QeetrixSwitchComponentTokens.ThumbChecked
                }, { QeetrixSwitchComponentTokens.TrackChecked }),
            )
            add(
                Check("progress.indicator on progress.track", graphic, {
                    QeetrixProgressComponentTokens.Indicator
                }, { QeetrixProgressComponentTokens.Track }),
            )
            add(
                Check("border.brand (row edge) on surface.brand-subtle", graphic, {
                    QeetrixTheme.colors.borderBrand
                }, { QeetrixTheme.colors.surfaceBrandSubtle }),
            )
            // Empty-state and list tiles: the icon is the tile's meaning for sighted people.
            add(
                Check("tile icon text.tertiary on surface.sunken", graphic, {
                    QeetrixTheme.colors.textTertiary
                }, { QeetrixTheme.colors.surfaceSunken }),
            )
            add(
                Check("tile icon text.brand on surface.brand-subtle", graphic, {
                    QeetrixTheme.colors.textBrand
                }, { QeetrixTheme.colors.surfaceBrandSubtle }),
            )
            add(
                Check("tile icon text.danger on feedback.error-subtle", graphic, {
                    QeetrixTheme.colors.textDanger
                }, { QeetrixTheme.colors.feedbackErrorSubtle }),
            )
            // Text on pressed and component surfaces.
            add(
                Check("text.primary on surface.brand-subtle-active (pressed chip, row)", text, {
                    QeetrixTheme.colors.textPrimary
                }, { QeetrixTheme.colors.surfaceBrandSubtleActive }),
            )
            add(
                Check("text.tertiary on surface.brand-subtle-active (pressed selected row)", text, {
                    QeetrixTheme.colors.textTertiary
                }, { QeetrixTheme.colors.surfaceBrandSubtleActive }),
            )
            add(
                Check("text.primary on surface.interactive-active (pressed outline button)", text, {
                    QeetrixTheme.colors.textPrimary
                }, { QeetrixTheme.colors.surfaceInteractiveActive }),
            )
            add(
                Check("text.on-subtle on surface.interactive (secondary badge, button)", text, {
                    QeetrixTheme.colors.textOnSubtle
                }, { QeetrixTheme.colors.surfaceInteractive }),
            )
            add(
                Check("badge.default-foreground on badge.default-background", text, {
                    QeetrixBadgeComponentTokens.DefaultForeground
                }, { QeetrixBadgeComponentTokens.DefaultBackground }),
            )
            add(
                Check("button.primary-foreground on button.primary-background", text, {
                    QeetrixButtonComponentTokens.PrimaryForeground
                }, { QeetrixButtonComponentTokens.PrimaryBackground }),
            )
            add(
                Check("button.primary-foreground on button.primary-background-active", text, {
                    QeetrixButtonComponentTokens.PrimaryForeground
                }, { QeetrixButtonComponentTokens.PrimaryBackgroundActive }),
            )
            add(
                Check("input.foreground on input.background", text, {
                    QeetrixInputComponentTokens.Foreground
                }, { QeetrixInputComponentTokens.Background }),
            )
            add(
                Check("input.placeholder on input.background", text, {
                    QeetrixInputComponentTokens.Placeholder
                }, { QeetrixInputComponentTokens.Background }),
            )
            add(
                Check("text.danger on surface.overlay (error snackbar title)", text, {
                    QeetrixTheme.colors.textDanger
                }, { QeetrixTheme.colors.surfaceOverlay }),
            )
            add(
                Check("text.tertiary on surface.overlay (snackbar description)", text, {
                    QeetrixTheme.colors.textTertiary
                }, { QeetrixTheme.colors.surfaceOverlay }),
            )
        }

    @Test
    fun componentColourPairsMeetWcagContrast() {
        val measured = mutableListOf<Triple<String, String, Double>>()
        compose.setContent {
            for (mode in listOf(QeetrixThemeMode.Light, QeetrixThemeMode.Dark)) {
                QeetrixTheme(mode) {
                    for (check in checks) {
                        // Backgrounds as drawn: a translucent fill (the dark theme's subtle tints, the progress
                        // track) over the canvas it sits on.
                        val drawn = check.background().compositeOver(QeetrixTheme.colors.surfaceCanvas)
                        val ratio = QeetrixAccessibility.contrastRatio(check.foreground(), drawn)
                        measured += Triple(mode.value, check.context, ratio)
                    }
                }
            }
        }
        compose.waitForIdle()
        val failing = mutableListOf<String>()
        for ((mode, context, ratio) in measured.distinctBy { it.first + it.second }) {
            val minimum = checks.first { it.context == context }.minimum
            val line = "%-5s %-72s %5.2f (needs %.1f)".format(mode, context, ratio, minimum)
            println("CONTRAST $line")
            if (ratio < minimum) failing += "$mode: $context"
        }
        assertEquals("Contrast failures not recorded as canonical findings", CanonicalFindings.sorted(), failing.sorted())
    }

    private companion object {
        /** Canonical-token failures, recorded in Documentation/Quality/ACCESSIBILITY-REPORT.md and proposed upstream. */
        val CanonicalFindings: List<String> = emptyList()
    }
}
