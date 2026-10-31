package com.qeetgroup.qeetrix.components.avatar

import androidx.compose.foundation.layout.width
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.semantics.getOrNull
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertHeightIsEqualTo
import androidx.compose.ui.test.getUnclippedBoundsInRoot
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.unit.dp
import com.qeetgroup.qeetrix.components.setQeetrix
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.GraphicsMode
import java.util.Locale

/** Avatars: avatar.tsx's initials, one image named by the name or hidden when decorative, the web's sizes; AvatarItem. */
@RunWith(RobolectricTestRunner::class)
class QeetrixAvatarTest {
    @get:Rule
    val compose = createComposeRule()

    @Test
    fun initialsFollowTheWebRule() {
        val en = Locale.ENGLISH
        assertEquals("PS", initialsOf("Priya Sharma", en))
        assertEquals("AL", initialsOf("  ada   king  lovelace ", en)) // first and last words
        assertEquals("P", initialsOf("Priya", en)) // one word, one initial
        assertEquals("", initialsOf("   ", en))
        // A company's legal form doesn't count; a name left with one word takes its first two graphemes.
        assertEquals("KF", initialsOf("Kaveri Foods Private Limited", en))
        assertEquals("AC", initialsOf("Acme Pvt Ltd", en))
        assertEquals("AC", initialsOf("Acme Pvt. Ltd.", en))
        assertEquals("QG", initialsOf("Qeet Group LLP", en))
        assertEquals("ÉZ", initialsOf("émile zola", en))
        assertEquals("İ", initialsOf("istanbul", Locale.forLanguageTag("tr"))) // locale-aware upper case
        assertEquals("👩‍💻D", initialsOf("👩‍💻 dev", en)) // a grapheme cluster stays whole
        assertEquals("नश", initialsOf("नमन शर्मा", Locale.forLanguageTag("hi"))) // Indic names stay whole
        // No case; the first grapheme cluster, mark included.
        assertEquals("รั", initialsOf("รัก", Locale.forLanguageTag("th")))
    }

    @Test
    fun aNamedAvatarIsOneImageCalledByTheName() {
        compose.setQeetrix { QeetrixAvatar("Priya Sharma") }
        compose
            .onNodeWithContentDescription("Priya Sharma")
            .assert(SemanticsMatcher.expectValue(SemanticsProperties.Role, Role.Image))
            .assertHeightIsEqualTo(32.dp)
        // The initials are drawn but not spelled out: they are not in the tree accessibility services read.
        compose.onNodeWithText("PS").assertDoesNotExist()
    }

    @Test
    fun aDecorativeAvatarIsHidden() {
        compose.setQeetrix { QeetrixAvatar("Priya Sharma", decorative = true, modifier = Modifier.testTag("a")) }
        compose.onNodeWithTag("a").assert(SemanticsMatcher.keyNotDefined(SemanticsProperties.ContentDescription))
        compose.onNodeWithTag("a").assert(SemanticsMatcher.keyNotDefined(SemanticsProperties.Role))
    }

    @Test
    fun sizesAndShapesAreTheWebs() {
        assertEquals(listOf("sm", "md", "lg", "xl"), QeetrixAvatarSize.entries.map { it.value })
        assertEquals(listOf(24f, 32f, 40f, 48f), QeetrixAvatarSize.entries.map { it.side.value })
        assertEquals(listOf("circle", "square"), QeetrixAvatarShape.entries.map { it.value })
    }

    /** avatar-item.css: the name on body.sm's 16 sp line, so the two lines are a `md` avatar's 32 dp. Real text layout. */
    @Test
    @GraphicsMode(GraphicsMode.Mode.NATIVE)
    fun anAvatarItemIsOneElementAsTallAsAMdAvatar() {
        compose.setQeetrix { QeetrixAvatarItem("Priya Sharma", Modifier.testTag("item"), description = "priya@acme.in") }
        val item = compose.onNodeWithTag("item").assertHeightIsEqualTo(32.dp).fetchSemanticsNode()
        assertEquals(listOf("Priya Sharma", "priya@acme.in"), item.config.getOrNull(SemanticsProperties.Text)?.map { it.text })
        // The avatar is decorative: the name is shown.
        compose.onNodeWithContentDescription("Priya Sharma").assertDoesNotExist()
    }

    /**
     * avatar-item.css 0.4.0 (QTRX-178): the name and description wrap (`overflow-wrap: anywhere`) instead of ending in an
     * ellipsis, so a long work email in a narrow column is shown whole, broken where it must. Real text layout.
     */
    @Test
    @GraphicsMode(GraphicsMode.Mode.NATIVE)
    fun aLongDescriptionWrapsAndIsNeverCut() {
        val email = "priya.sharma.accounts-payable@acme-industries.co.in"
        compose.setQeetrix { QeetrixAvatarItem("Priya Sharma", Modifier.width(200.dp).testTag("item"), description = email) }
        val description = compose.onNodeWithText(email, useUnmergedTree = true).getUnclippedBoundsInRoot()
        // body.sm's 16 sp line: more than one of them, and the whole email stays inside the 200 dp row.
        assertTrue("one line: ${description.bottom - description.top}", description.bottom - description.top >= 32.dp)
        val item = compose.onNodeWithTag("item").getUnclippedBoundsInRoot()
        assertTrue(description.right <= item.right)
        assertTrue(item.bottom - item.top > 32.dp)
    }
}
