package com.qeetgroup.qeetrix.components

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.test.junit4.createComposeRule
import com.qeetgroup.qeetrix.components.input.FieldColors
import com.qeetgroup.qeetrix.components.input.fieldColors
import com.qeetgroup.qeetrix.components.input.selectionControlEdge
import com.qeetgroup.qeetrix.components.otpinput.otpSlotColors
import com.qeetgroup.qeetrix.components.progress.QeetrixMeterTone
import com.qeetgroup.qeetrix.components.progress.indicator
import com.qeetgroup.qeetrix.components.progress.progressTrack
import com.qeetgroup.qeetrix.components.switchcontrol.switchThumb
import com.qeetgroup.qeetrix.tokens.generated.QeetrixDarkColors
import com.qeetgroup.qeetrix.tokens.generated.QeetrixLightColors
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

/**
 * The looks `@qeetrix/react` 0.4.0 fixed in the CSS Android mirrors, from the components' own colour rules, in both
 * schemes: each assertion fails on the 0.3.0 look it replaced.
 */
@RunWith(RobolectricTestRunner::class)
class UpstreamFixesLookTest {
    @get:Rule
    val compose = createComposeRule()

    private val schemes = listOf(QeetrixLightColors, QeetrixDarkColors)

    /** checkbox.css, radio.css, switch.css: `[data-invalid]` after the checked and hover rules (QTRX-161). */
    @Test
    fun anInvalidSelectionControlKeepsItsDangerEdgeCheckedAndUnderThePointer() {
        for (colors in schemes) {
            for (on in listOf(false, true)) {
                for (hover in listOf(false, true)) {
                    val edge = selectionControlEdge(colors, enabled = true, on = on, invalid = true, hover = hover)
                    assertEquals("on=$on hover=$hover", colors.borderDanger, edge)
                }
            }
            // A valid control still steps: Ember when on (its hover step under the pointer), strong-hover when off.
            assertEquals(colors.bgBrandBoldHover, selectionControlEdge(colors, true, on = true, invalid = false, hover = true))
            assertEquals(colors.borderStrongHover, selectionControlEdge(colors, true, on = false, invalid = false, hover = true))
            assertEquals(colors.borderDisabled, selectionControlEdge(colors, false, on = true, invalid = true, hover = false))
        }
    }

    /** switch.css `.qx-switch-thumb[data-checked]`: `color.icon.on-brand`, white in both schemes (QTRX-162). */
    @Test
    fun theSwitchsOnThumbIsWhiteInBothSchemes() {
        for (colors in schemes) {
            assertEquals(Color.White, switchThumb(colors, enabled = true, checked = true))
            assertEquals(colors.iconSubtle, switchThumb(colors, enabled = true, checked = false))
        }
    }

    /** otp-input.css: the typing slot of an invalid code shows focus (QTRX-163); a read-only code looks read-only (QTRX-165). */
    @Test
    fun anOtpSlotShowsFocusOverInvalidAndAReadOnlyCodeHasNoFill() {
        for (colors in schemes) {
            val typing = otpSlotColors(colors, enabled = true, readOnly = false, invalid = true, typing = true)
            assertEquals(colors.borderFocus, typing.frame)
            assertEquals(colors.bgNeutralSubtle, typing.fill)
            assertNull(typing.line)
            val other = otpSlotColors(colors, enabled = true, readOnly = false, invalid = true, typing = false)
            assertEquals(colors.borderDanger, other.frame)
            val readOnly = otpSlotColors(colors, enabled = true, readOnly = true, invalid = false, typing = false)
            assertEquals(Color.Transparent, readOnly.fill)
            assertEquals(colors.borderStrong, readOnly.line)
            assertNull(readOnly.frame)
        }
    }

    /** input.css, select.css, number-field.css: a read-only field's line is `border.strong`, with no fill (QTRX-175). */
    @Test
    fun aReadOnlyFieldDrawsTheEditableFieldsLine() {
        lateinit var readOnly: FieldColors
        compose.setQeetrix { readOnly = fieldColors(enabled = true, readOnly = true, focused = false, hovered = false, invalid = false) }
        compose.waitForIdle()
        assertEquals(QeetrixLightColors.borderStrong, readOnly.line)
        assertEquals(Color.Transparent, readOnly.fill)
    }

    /** progress.css `--qx-progress-track` and `--qx-progress-warning` (QTRX-176). */
    @Test
    fun progressTracksAndTheWarningFillFollowTheScheme() {
        assertEquals(QeetrixLightColors.borderDefault, progressTrack(QeetrixLightColors, dark = false))
        assertEquals(QeetrixDarkColors.surfaceSunken, progressTrack(QeetrixDarkColors, dark = true))
        assertEquals(QeetrixLightColors.borderWarning, QeetrixMeterTone.Warning.indicator(QeetrixLightColors, dark = false))
        assertEquals(QeetrixDarkColors.bgWarningBold, QeetrixMeterTone.Warning.indicator(QeetrixDarkColors, dark = true))
    }
}
