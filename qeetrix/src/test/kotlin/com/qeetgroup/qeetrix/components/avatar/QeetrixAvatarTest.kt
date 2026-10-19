package com.qeetgroup.qeetrix.components.avatar

import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertHeightIsEqualTo
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.unit.dp
import com.qeetgroup.qeetrix.components.setQeetrix
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import java.util.Locale

@RunWith(RobolectricTestRunner::class)
class QeetrixAvatarTest {
    @get:Rule
    val compose = createComposeRule()

    @Test
    fun initialsFollowTheWebRule() {
        val en = Locale.ENGLISH
        assertEquals("AL", initialsOf("Ada Lovelace", en))
        assertEquals("AL", initialsOf("  ada   king  lovelace ", en)) // first and last words
        assertEquals("P", initialsOf("Priya", en)) // one word, one initial
        assertEquals("", initialsOf("   ", en))
        assertEquals("ÉZ", initialsOf("émile zola", en))
        assertEquals("İ", initialsOf("istanbul", Locale.forLanguageTag("tr"))) // locale-aware upper case
        assertEquals("👩‍💻D", initialsOf("👩‍💻 dev", en)) // a grapheme cluster stays whole
        // No case; the first grapheme cluster, mark included. (The web's Array.from takes the first code point,
        // "ร", which separates the vowel mark — a deliberate native improvement, CROSS_PLATFORM_CONTRACT.md.)
        assertEquals("รั", initialsOf("รัก", Locale.forLanguageTag("th")))
    }

    @Test
    fun aNamedAvatarIsOneImageCalledByTheName() {
        compose.setQeetrix { QeetrixAvatar("Ada Lovelace", badgeLabel = "Online") }
        compose
            .onNodeWithContentDescription("Ada Lovelace, Online")
            .assert(SemanticsMatcher.expectValue(SemanticsProperties.Role, Role.Image))
            .assertHeightIsEqualTo(32.dp)
        // The initials are drawn but not spelled out: they are not in the tree accessibility services read.
        compose.onNodeWithText("AL").assertDoesNotExist()
    }

    @Test
    fun anAvatarBesideAVisibleNameIsDecorative() {
        compose.setQeetrix { QeetrixAvatar("Ada Lovelace", contentDescription = null, modifier = Modifier.testTag("a")) }
        compose.onNodeWithTag("a").assert(SemanticsMatcher.keyNotDefined(SemanticsProperties.ContentDescription))
        compose.onNodeWithTag("a").assert(SemanticsMatcher.keyNotDefined(SemanticsProperties.Role))
    }

    @Test
    fun sizesAreFixedWebSteps() {
        assertEquals(listOf(20f, 24f, 32f, 40f, 48f), QeetrixAvatarSize.entries.map { it.side.value })
        assertEquals(listOf("circle", "square"), QeetrixAvatarShape.entries.map { it.value })
    }
}
