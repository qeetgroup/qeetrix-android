package com.qeetgroup.qeetrix.components.input

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.test.getUnclippedBoundsInRoot
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.qeetgroup.qeetrix.components.setQeetrix
import com.qeetgroup.qeetrix.components.typography.QeetrixText
import com.qeetgroup.qeetrix.foundation.sizing.QeetrixDensity
import com.qeetgroup.qeetrix.foundation.typography.QeetrixTextRole
import com.qeetgroup.qeetrix.theme.LocalQeetrixDensity
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.GraphicsMode

/**
 * The large text field (input.css `[data-size="lg"]`, as the owner decided for one-value sign-up steps): 16 dp taller
 * than the field at every density, the value at title.lg's size in regular weight with tabular figures, the
 * placeholder in body.md; the adornment and the help text line up as at the default size.
 */
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
class QeetrixLargeTextFieldTest {
    @get:Rule
    val compose = createComposeRule()

    private fun layout(text: String): TextLayoutResult {
        val layouts = mutableListOf<TextLayoutResult>()
        compose
            .onNodeWithText(
                text,
                useUnmergedTree = true,
            ).fetchSemanticsNode()
            .config[SemanticsActions.GetTextLayoutResult]
            .action
            ?.invoke(layouts)
        return layouts.first()
    }

    @Test
    fun itIs16DpTallerAtEveryDensity() {
        var density by mutableStateOf(QeetrixDensity.Default)
        compose.setQeetrix {
            CompositionLocalProvider(LocalQeetrixDensity provides density) {
                Box(Modifier.width(320.dp)) {
                    QeetrixTextField(TextFieldState(), label = "Phone number", hideLabel = true, modifier = Modifier.testTag("md"))
                }
                Box(Modifier.width(320.dp)) {
                    QeetrixTextField(
                        TextFieldState(),
                        label = "Phone number",
                        hideLabel = true,
                        modifier = Modifier.testTag("lg"),
                        size = QeetrixTextFieldSize.Lg,
                    )
                }
            }
        }
        for (each in QeetrixDensity.entries) {
            compose.runOnIdle { density = each }
            // The hidden label leaves the box alone in its 48 dp touch height; the large box is taller than that.
            val lg = compose.onNodeWithTag("lg").getUnclippedBoundsInRoot()
            assertEquals("$each", each.sizeControlLg + 16.dp, lg.bottom - lg.top)
        }
        assertEquals(60.dp, QeetrixDensity.Comfortable.sizeControlLg + 16.dp)
    }

    @Test
    fun theValueIsTitleLgRegularTabularAndThePlaceholderBodyMd() {
        compose.setQeetrix {
            Box(Modifier.width(320.dp)) {
                QeetrixTextField(TextFieldState("98765 43210"), label = "Phone number", size = QeetrixTextFieldSize.Lg)
            }
            Box(Modifier.width(320.dp)) {
                QeetrixTextField(TextFieldState(), label = "Name", placeholder = "e.g. Asha Rao", size = QeetrixTextFieldSize.Lg)
            }
        }
        val value = layout("98765 43210").layoutInput.style
        assertEquals(QeetrixTextRole.TitleLg.spec.size, value.fontSize.value, 0.01f)
        assertEquals(FontWeight(QeetrixTextRole.BodyMd.spec.weight), value.fontWeight)
        assertEquals("tnum", value.fontFeatureSettings)
        val placeholder = layout("e.g. Asha Rao").layoutInput.style
        assertEquals(QeetrixTextRole.BodyMd.spec.size, placeholder.fontSize.value, 0.01f)
    }

    /** A start adornment sits on the box's middle, and the help text starts where it does at the default size. */
    @Test
    fun theAdornmentAndHelpLineUp() {
        compose.setQeetrix {
            Box(Modifier.width(320.dp)) {
                QeetrixTextField(
                    TextFieldState("98765 43210"),
                    label = "Phone number",
                    description = "We'll send a code to it.",
                    startAdornment = { QeetrixText("+91", modifier = Modifier.testTag("affix")) },
                    size = QeetrixTextFieldSize.Lg,
                    modifier = Modifier.testTag("field"),
                )
            }
        }
        val affix = compose.onNodeWithTag("affix", useUnmergedTree = true).getUnclippedBoundsInRoot()
        // The text field's node is its decorator, label and box: the box is its bottom 56 dp (40 + 16 at the default).
        val field = compose.onNodeWithText("98765 43210", useUnmergedTree = true).getUnclippedBoundsInRoot()
        val boxCentre = field.bottom - (QeetrixDensity.Default.sizeControlLg + 16.dp) / 2
        assertEquals(boxCentre.value, ((affix.top + affix.bottom) / 2).value, 1f)
        val help = compose.onNodeWithText("We'll send a code to it.", useUnmergedTree = true).getUnclippedBoundsInRoot()
        assertEquals(compose.onNodeWithTag("field").getUnclippedBoundsInRoot().left, help.left)
    }
}
