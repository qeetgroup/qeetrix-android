package com.qeetgroup.qeetrix.components.typography

import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.test.click
import androidx.compose.ui.test.hasClickAction
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withLink
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.qeetgroup.qeetrix.components.setQeetrix
import com.qeetgroup.qeetrix.theme.QeetrixTheme
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.GraphicsMode

/** A link inside text — its own clickable element, link.css's colour and underline, a 48 dp tap. */
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
class QeetrixLinkTest {
    @get:Rule
    val compose = createComposeRule()

    private val sentence = "Using Qeet ID? "
    private val words = "Link this phone"

    @Test
    fun aLinkInASentenceIsItsOwnClickableElementInTheLinkColour() {
        var clicks = 0
        var linkColour = Color.Unspecified
        var layout: TextLayoutResult? = null
        compose.setQeetrix {
            linkColour = QeetrixTheme.colors.textLink
            val link = qeetrixLink(onClick = { clicks++ })
            QeetrixText(
                buildAnnotatedString {
                    append(sentence)
                    withLink(link) { append(words) }
                },
                onTextLayout = { layout = it },
            )
        }
        compose.onNode(hasClickAction()).performClick()
        compose.runOnIdle { assertEquals(1, clicks) }
        // link.css: color.text.link and an underline over the link's words, nothing over the sentence before them.
        val spans = layout!!.layoutInput.text.spanStyles
        val start = sentence.length
        val styled = spans.filter { it.start <= start && it.end >= start + words.length }.map { it.item }
        assertTrue("the link colour: $styled", styled.any { it.color == linkColour })
        assertTrue("underlined: $styled", styled.any { it.textDecoration == TextDecoration.Underline })
        assertTrue("the sentence is not a link", spans.none { it.start < start && it.item.textDecoration == TextDecoration.Underline })
    }

    /** A tap within 48 dp of the words — here 12 dp below them — still opens the link; the text doesn't move. */
    @Test
    fun aLinkTakesATapWithinA48DpTarget() {
        var clicks = 0
        compose.setQeetrix { QeetrixLink(words, onClick = { clicks++ }) }
        val link = compose.onNode(hasClickAction()).fetchSemanticsNode().boundsInRoot
        val below = with(compose.density) { 12.dp.toPx() }
        compose.onRoot().performTouchInput { click(Offset(link.center.x, link.bottom + below)) }
        compose.runOnIdle { assertEquals(1, clicks) }
    }

    @Test
    fun itWorksRightToLeftAndAt200Percent() {
        var clicks = 0
        compose.setContent {
            CompositionLocalProvider(
                LocalDensity provides Density(LocalDensity.current.density, fontScale = 2f),
                LocalLayoutDirection provides LayoutDirection.Rtl,
            ) {
                QeetrixTheme {
                    val link = qeetrixLink(onClick = { clicks++ }, tone = QeetrixLinkTone.Subtle)
                    QeetrixText(
                        buildAnnotatedString {
                            append("क्या आप Qeet ID इस्तेमाल करते हैं? ")
                            withLink(link) { append("यह फ़ोन जोड़ें") }
                        },
                    )
                }
            }
        }
        val node = compose.onNode(hasClickAction()).fetchSemanticsNode()
        assertTrue(node.config.contains(SemanticsActions.OnClick))
        compose.onNode(hasClickAction()).performClick()
        compose.runOnIdle { assertEquals(1, clicks) }
        assertEquals(listOf("default", "subtle", "inherit"), QeetrixLinkTone.entries.map { it.value })
    }
}
