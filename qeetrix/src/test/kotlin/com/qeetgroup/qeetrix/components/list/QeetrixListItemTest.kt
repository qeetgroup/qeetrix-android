package com.qeetgroup.qeetrix.components.list

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.semantics.getOrNull
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertHeightIsAtLeast
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.getUnclippedBoundsInRoot
import androidx.compose.ui.test.hasClickAction
import androidx.compose.ui.test.isHeading
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.longClick
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.qeetgroup.qeetrix.components.badge.QeetrixBadge
import com.qeetgroup.qeetrix.components.button.QeetrixButton
import com.qeetgroup.qeetrix.components.button.QeetrixButtonVariant
import com.qeetgroup.qeetrix.components.button.QeetrixIconButton
import com.qeetgroup.qeetrix.components.otpinput.QeetrixOTPCode
import com.qeetgroup.qeetrix.components.setQeetrix
import com.qeetgroup.qeetrix.foundation.sizing.QeetrixDensity
import com.qeetgroup.qeetrix.icons.QeetrixIconName
import com.qeetgroup.qeetrix.theme.LocalQeetrixDensity
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.GraphicsMode
import java.time.Instant

/** List rows: one element in reading order, the app's clicks and selection, sizes, large text; section headers. */
@RunWith(RobolectricTestRunner::class)
class QeetrixListItemTest {
    @get:Rule
    val compose = createComposeRule()

    @Test
    fun aRowIsOneElementReadTitleDescriptionThenMetadata() {
        compose.setQeetrix {
            QeetrixListItem(
                "Quarterly report.pdf",
                Modifier.testTag("row"),
                description = "Shared by Ada",
                metadata = "2.4 MB",
                leading = { QeetrixListItemIcon(QeetrixIconName.FileText) },
            ) { QeetrixBadge("Shared") }
        }
        val text =
            compose
                .onNodeWithTag("row")
                .fetchSemanticsNode()
                .config
                .getOrNull(SemanticsProperties.Text)
                ?.map { it.text }
        // The icon tile is decorative; the badge's word is part of the row.
        assertEquals(listOf("Quarterly report.pdf", "Shared by Ada", "2.4 MB", "Shared"), text)
        // A static row is not clickable and not selected.
        compose.onNodeWithTag("row").assert(SemanticsMatcher.keyNotDefined(SemanticsActions.OnClick))
        compose.onNodeWithTag("row").assert(SemanticsMatcher.keyNotDefined(SemanticsProperties.Selected))
    }

    @Test
    fun clicksAndLongClicksAreTheApps() {
        val events = mutableListOf<String>()
        var enabled by mutableStateOf(true)
        compose.setQeetrix {
            QeetrixListItem(
                "Priya Sharma",
                Modifier.testTag("row"),
                enabled = enabled,
                onClick = { events += "click" },
                onLongClick = { events += "long" },
                onLongClickLabel = "Select",
            )
        }
        compose.onNodeWithTag("row").performClick()
        compose.onNodeWithTag("row").performTouchInput { longClick() }
        assertEquals(listOf("click", "long"), events)
        val longLabel =
            compose
                .onNodeWithTag("row")
                .fetchSemanticsNode()
                .config
                .getOrNull(SemanticsActions.OnLongClick)
                ?.label
        assertEquals("Select", longLabel)

        compose.runOnIdle { enabled = false }
        compose.onNodeWithTag("row").assertIsNotEnabled().performClick()
        assertEquals(listOf("click", "long"), events)
    }

    @Test
    fun aLongClickNeedsAClick() {
        assertThrows(IllegalArgumentException::class.java) {
            compose.setQeetrix { QeetrixListItem("Priya", onLongClick = {}) }
        }
    }

    @Test
    fun selectionIsDrawnAndReadOnlyWhenSelected() {
        var selected by mutableStateOf(true)
        compose.setQeetrix { QeetrixListItem("Priya", Modifier.testTag("row"), selected = selected, onClick = {}) }
        compose.onNodeWithTag("row").assertIsSelected()
        compose.runOnIdle { selected = false }
        // Rows that can't be selected aren't read as "not selected".
        compose.onNodeWithTag("row").assert(SemanticsMatcher.keyNotDefined(SemanticsProperties.Selected))
    }

    @Test
    fun aRowIsAtLeastTheRowHeightAndA48DpTarget() {
        var density by mutableStateOf(QeetrixDensity.Compact)
        compose.setQeetrix {
            CompositionLocalProvider(LocalQeetrixDensity provides density) {
                QeetrixListItem("Priya", Modifier.testTag("row"), onClick = {})
            }
        }
        for (each in QeetrixDensity.entries) {
            compose.runOnIdle { density = each }
            compose.onNodeWithTag("row").assertHeightIsAtLeast(maxOf(each.sizeRow, 48.dp))
        }
    }

    @Test
    fun aTrailingControlStaysItsOwnElement() {
        val events = mutableListOf<String>()
        compose.setQeetrix {
            QeetrixListItem("Weekly digest", Modifier.testTag("row"), onClick = { events += "row" }) {
                QeetrixIconButton(QeetrixIconName.Ellipsis, "More for Weekly digest", onClick = { events += "more" })
            }
        }
        compose.onNodeWithContentDescription("More for Weekly digest").assert(hasClickAction()).performClick()
        assertEquals(listOf("more"), events)
        val rowText =
            compose
                .onNodeWithTag("row")
                .fetchSemanticsNode()
                .config
                .getOrNull(SemanticsProperties.ContentDescription)
        assertTrue("the button isn't merged into the row: $rowText", rowText.isNullOrEmpty())
    }

    /** The letter tile is the icon tile's 32 dp, two characters at most, and not read: the title names the item. */
    @Test
    @GraphicsMode(GraphicsMode.Mode.NATIVE) // real text sizes
    fun aLetterTileIsTheIconTileWithTwoCharactersAtMost() {
        assertEquals("Go", firstCharacters("Google", 2))
        assertEquals("A\u0304B", firstCharacters("A\u0304BC", 2)) // A with a combining macron is one character
        assertEquals("7", firstCharacters("7", 2))
        assertEquals("", firstCharacters("", 2))
        compose.setQeetrix {
            QeetrixListItem("GitHub", Modifier.testTag("row"), description = "priya", leading = {
                QeetrixListItemIcon("GH", Modifier.testTag("tile"))
            })
        }
        val tile = compose.onNodeWithTag("tile", useUnmergedTree = true).getUnclippedBoundsInRoot()
        assertEquals(32.dp, tile.right - tile.left)
        assertEquals(32.dp, tile.bottom - tile.top)
        val text =
            compose
                .onNodeWithTag("row")
                .fetchSemanticsNode()
                .config
                .getOrNull(SemanticsProperties.Text)
                ?.map { it.text }
        assertEquals(listOf("GitHub", "priya"), text)
        compose.onAllNodesWithText("GH").assertCountEquals(0)
    }

    /** At 200 % the tile grows past 32 dp, square, holding its text with gap.xs (4 dp) on every side. */
    @Test
    @GraphicsMode(GraphicsMode.Mode.NATIVE) // real text sizes
    fun aLetterTileGrowsWithTheFontScale() {
        var text = IntSize.Zero
        var density = 1f
        compose.setContent {
            CompositionLocalProvider(LocalDensity provides Density(LocalDensity.current.density, fontScale = 2f)) {
                com.qeetgroup.qeetrix.theme.QeetrixTheme {
                    density = LocalDensity.current.density
                    val style =
                        com.qeetgroup.qeetrix.theme.QeetrixTheme.typography.labelMd
                            .copy(fontWeight = com.qeetgroup.qeetrix.internal.QeetrixTextWeight.Semibold)
                    text = rememberTextMeasurer().measure("12", style).size
                    QeetrixListItemIcon("12", Modifier.testTag("tile"))
                }
            }
        }
        val tile = compose.onNodeWithTag("tile", useUnmergedTree = true).getUnclippedBoundsInRoot()
        val side = tile.bottom - tile.top
        assertEquals(side, tile.right - tile.left)
        assertTrue("grows past the icon tile: $side", side > 32.dp)
        assertTrue("holds the text's height: $side, $text", side.value >= text.height / density + 8f - 0.5f)
        assertTrue("holds the text's width: $side, $text", side.value >= text.width / density + 8f - 0.5f)
    }

    /**
     * An authenticator account row — the tile, issuer and account, then the code under them, its countdown at
     * the end. The row (with its long press) and the code (copy) are two elements, and the code sits gap.xs under the
     * description.
     */
    @Test
    fun contentSitsUnderTheDescriptionAndAControlThereIsItsOwnElement() {
        val now = Instant.parse("2026-11-07T10:00:12Z")
        compose.setQeetrix {
            QeetrixListItem(
                "GitHub",
                Modifier.testTag("row"),
                description = "priya",
                onClick = {},
                onLongClick = {},
                onLongClickLabel = "Account actions",
                content = {
                    QeetrixOTPCode(
                        "482105",
                        Instant.parse("2026-11-07T10:00:30Z"),
                        Modifier.fillMaxWidth(),
                        label = "GitHub",
                        now = now,
                        countdownAtEnd = true,
                    )
                },
                leading = { QeetrixListItemIcon("G") },
            )
        }
        compose.onAllNodes(hasClickAction()).assertCountEquals(2)
        val row = compose.onNodeWithTag("row").fetchSemanticsNode().config
        assertEquals(listOf("GitHub", "priya"), row.getOrNull(SemanticsProperties.Text)?.map { it.text })
        assertEquals("Account actions", row.getOrNull(SemanticsActions.OnLongClick)?.label)
        val code = compose.onNodeWithContentDescription("GitHub, 4 8 2 1 0 5, 18 seconds left")
        code.assert(hasClickAction())
        val description = compose.onNodeWithText("priya", useUnmergedTree = true).getUnclippedBoundsInRoot()
        // The code's 48 dp touch target starts gap.xs under the description.
        assertEquals((description.bottom + 4.dp).value, code.getUnclippedBoundsInRoot().top.value, 0.5f)
    }

    @Test
    fun fromLargeTextTheTrailingContentMovesBelowTheText() {
        var scale by mutableFloatStateOf(1f)
        compose.setQeetrix {
            val base = LocalDensity.current
            CompositionLocalProvider(LocalDensity provides Density(base.density, scale)) {
                QeetrixListItem("Storage", Modifier.width(360.dp), description = "Used this month") {
                    QeetrixBadge("80 %", Modifier.testTag("trailing"))
                }
            }
        }
        val title = compose.onNodeWithText("Storage", useUnmergedTree = true)
        val beside = compose.onNodeWithTag("trailing", useUnmergedTree = true).getUnclippedBoundsInRoot()
        assertTrue("beside the text at 100 %", beside.top < title.getUnclippedBoundsInRoot().bottom)
        compose.runOnIdle { scale = 1.5f }
        val below = compose.onNodeWithTag("trailing", useUnmergedTree = true).getUnclippedBoundsInRoot()
        val description = compose.onNodeWithText("Used this month", useUnmergedTree = true).getUnclippedBoundsInRoot()
        assertTrue("below the text at 150 %", below.top >= description.bottom)
    }

    @Test
    fun metadataSitsOnTheTitleLineAtTheEnd() {
        compose.setQeetrix { QeetrixListItem("Report", Modifier.width(360.dp), description = "Shared by Ada", metadata = "2 MB") }
        val title = compose.onNodeWithText("Report", useUnmergedTree = true).getUnclippedBoundsInRoot()
        val metadata = compose.onNodeWithText("2 MB", useUnmergedTree = true).getUnclippedBoundsInRoot()
        val description = compose.onNodeWithText("Shared by Ada", useUnmergedTree = true).getUnclippedBoundsInRoot()
        assertTrue("same line", metadata.top < title.bottom && metadata.bottom > title.top)
        assertTrue("after the title", metadata.left > title.right)
        assertTrue("above the description", metadata.bottom <= description.top)
    }

    @Test
    fun aSectionHeaderIsAHeadingReadAsWrittenWithASeparateAction() {
        var invited = 0
        compose.setQeetrix {
            QeetrixSectionHeader("Members", description = "People with access") {
                QeetrixButton("Invite", onClick = { invited++ }, variant = QeetrixButtonVariant.Ghost)
            }
        }
        val heading = compose.onNode(isHeading()).fetchSemanticsNode()
        // Drawn in capitals (label.caps), read as written.
        assertEquals(listOf("Members", "People with access"), heading.config.getOrNull(SemanticsProperties.Text)?.map { it.text })
        assertFalse(heading.config.contains(SemanticsActions.OnClick))
        compose.onNodeWithText("Invite").performClick()
        assertEquals(1, invited)
    }

    @Test
    fun indicTextIsFoundByItsScript() {
        assertTrue(hasIndicScript("दस्तावेज़"))
        assertTrue(hasIndicScript("தமிழ்"))
        assertFalse(hasIndicScript("Members"))
        assertFalse(hasIndicScript("الأعضاء"))
    }
}
