package com.qeetgroup.qeetrix.components.radiogroup

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertHeightIsAtLeast
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.assertIsNotSelected
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.getUnclippedBoundsInRoot
import androidx.compose.ui.test.hasAnyAncestor
import androidx.compose.ui.test.hasContentDescription
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.unit.dp
import com.qeetgroup.qeetrix.components.badge.QeetrixBadge
import com.qeetgroup.qeetrix.components.badge.QeetrixBadgeTone
import com.qeetgroup.qeetrix.components.input.QeetrixChoiceOrientation
import com.qeetgroup.qeetrix.components.input.QeetrixChoiceVariant
import com.qeetgroup.qeetrix.components.input.QeetrixOption
import com.qeetgroup.qeetrix.components.input.requireDistinctValues
import com.qeetgroup.qeetrix.components.setQeetrix
import com.qeetgroup.qeetrix.components.typography.QeetrixText
import com.qeetgroup.qeetrix.components.typography.QeetrixTextSize
import com.qeetgroup.qeetrix.icons.QeetrixIconName
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@RunWith(RobolectricTestRunner::class)
class QeetrixRadioGroupTest {
    @get:Rule
    val compose = createComposeRule()

    private enum class Plan { Starter, Team, Enterprise }

    private val options =
        listOf(
            QeetrixOption(Plan.Starter, "Starter", "Up to 5 members"),
            QeetrixOption(Plan.Team, "Team"),
            QeetrixOption(Plan.Enterprise, "Enterprise", enabled = false),
        )

    @Test
    fun choosingAnOptionSelectsItAndOnlyIt() {
        var plan by mutableStateOf<Plan?>(null)
        compose.setQeetrix { QeetrixRadioGroup(plan, { plan = it }, options, label = "Plan") }
        compose
            .onNodeWithText("Team")
            .assertIsNotSelected()
            .performClick()
            .assertIsSelected()
        assertEquals(Plan.Team, plan)
        compose.onNodeWithText("Starter").assertIsNotSelected().performClick()
        compose.onNodeWithText("Starter").assertIsSelected()
        compose.onNodeWithText("Team").assertIsNotSelected()
    }

    /**
     * With hideLabel the question isn't drawn — react's label in a VisuallyHidden — in every orientation and variant: the
     * options start at the group's top, and the group carries the question as its name and heading, read first.
     */
    @Test
    @GraphicsMode(GraphicsMode.Mode.NATIVE)
    fun aHiddenLabelLeavesTheOptionsAloneAndStillNamesTheGroup() {
        var layout by mutableStateOf(QeetrixChoiceVariant.Default to QeetrixChoiceOrientation.Vertical)
        compose.setQeetrix {
            QeetrixRadioGroup(
                Plan.Team,
                {},
                options,
                label = "This phone has a different SIM",
                modifier = Modifier.testTag("group"),
                variant = layout.first,
                orientation = layout.second,
                required = true,
                hideLabel = true,
            )
        }
        val layouts =
            listOf(
                QeetrixChoiceVariant.Default to QeetrixChoiceOrientation.Vertical,
                QeetrixChoiceVariant.Default to QeetrixChoiceOrientation.Horizontal,
                QeetrixChoiceVariant.Tile to QeetrixChoiceOrientation.Vertical,
            )
        for (each in layouts) {
            compose.runOnIdle { layout = each }
            compose.onNodeWithText("This phone has a different SIM", substring = true, useUnmergedTree = true).assertDoesNotExist()
            val group = compose.onNodeWithTag("group").getUnclippedBoundsInRoot()
            val first = compose.onNodeWithText("Starter").getUnclippedBoundsInRoot()
            assertEquals("$each", group.top, first.top)
            compose
                .onNodeWithTag("group")
                .assert(SemanticsMatcher.keyIsDefined(SemanticsProperties.Heading))
                .assert(hasContentDescription("This phone has a different SIM, required"))
            compose
                .onNodeWithText("Team")
                .assert(SemanticsMatcher.expectValue(SemanticsProperties.Role, Role.RadioButton))
                .assert(hasAnyAncestor(SemanticsMatcher.keyIsDefined(SemanticsProperties.SelectableGroup)))
                .assertIsSelected()
        }
    }

    /**
     * field.css `--qx-choice-tile-min`: short choices on their own (no icon, no description) take tiles of at least
     * 128 dp, so `columns = 2` gives two equal tiles a row in a 360 dp phone's 328 dp column, 8 dp apart, each row as
     * tall as its tallest tile; tiles with a description keep 192 dp, so the same column holds one a row.
     */
    @Test
    @GraphicsMode(GraphicsMode.Mode.NATIVE)
    @Config(qualifiers = "w360dp-h1200dp")
    fun twoColumnsOfShortTilesShareAPhonesWidth() {
        val languages =
            listOf("English", "हिन्दी", "বাংলা", "తెలుగు", "मराठी", "தமிழ்", "ગુજરાતી", "ಕನ್ನಡ", "മലയാളം", "Odia, the language of Odisha")
        var described by mutableStateOf(false)
        compose.setQeetrix {
            Box(Modifier.width(328.dp)) {
                QeetrixRadioGroup(
                    "English",
                    {},
                    languages.map { QeetrixOption(it, it, description = if (described) "A language" else null) },
                    label = "Language",
                    variant = QeetrixChoiceVariant.Tile,
                    columns = 2,
                )
            }
        }
        val bounds = languages.map { compose.onNodeWithText(it).getUnclippedBoundsInRoot() }
        for (row in bounds.chunked(2)) {
            val (start, end) = row
            assertEquals(160.dp, start.right - start.left)
            assertEquals(160.dp, end.right - end.left)
            assertEquals(8.dp, end.left - start.right)
            assertEquals(start.top, end.top)
            assertEquals(start.bottom - start.top, end.bottom - end.top)
        }
        // The long label wraps inside its tile, and its neighbour is as tall.
        assertTrue(bounds[9].bottom - bounds[9].top > bounds[0].bottom - bounds[0].top)
        compose.runOnIdle { described = true }
        val first = compose.onNodeWithText("English").getUnclippedBoundsInRoot()
        assertEquals(328.dp, first.right - first.left)
    }

    /**
     * An option's own label — a language in its own script, a badge after a title — is drawn in the title's place, in
     * a row and in a tile, with the radio on its first line; TalkBack reads the option's plain label, its description
     * and its state, as for a text label, and nothing of the drawing.
     */
    @Test
    @GraphicsMode(GraphicsMode.Mode.NATIVE)
    fun anOptionsOwnLabelIsDrawnAndItsPlainLabelIsRead() {
        var variant by mutableStateOf(QeetrixChoiceVariant.Default)
        val drawn = mutableMapOf<String, androidx.compose.ui.unit.IntSize>()
        compose.setQeetrix {
            QeetrixRadioGroup(
                "hi",
                {},
                listOf(QeetrixOption("en", "English"), QeetrixOption("hi", "हिन्दी, Hindi", description = "Hindi")),
                label = "Language",
                variant = variant,
                optionLabel = { option ->
                    Row(Modifier.onSizeChanged { drawn[option.value] = it }) {
                        QeetrixText(if (option.value == "hi") "हिन्दी" else "English", size = QeetrixTextSize.Lg)
                        if (option.value == "hi") QeetrixBadge("Recommended", tone = QeetrixBadgeTone.Brand)
                    }
                },
            )
        }
        for (each in listOf(QeetrixChoiceVariant.Default, QeetrixChoiceVariant.Tile)) {
            if (each != variant) {
                compose.runOnIdle {
                    drawn.clear()
                    variant = each
                }
            }
            compose.waitForIdle()
            assertTrue("$each drew ${drawn.keys}", drawn.keys == setOf("en", "hi") && drawn.values.all { it.width > 0 && it.height > 0 })
            compose
                .onNode(hasText("हिन्दी, Hindi") and SemanticsMatcher.expectValue(SemanticsProperties.Role, Role.RadioButton))
                .assert(hasText("Hindi"))
                .assertIsSelected()
            compose.onNodeWithText("Recommended").assertDoesNotExist()
            compose.onNodeWithText("English").assertIsNotSelected()
        }
    }

    @Test
    fun optionsAreRadioButtonsInASelectableGroupUnderItsQuestion() {
        compose.setQeetrix { QeetrixRadioGroup(Plan.Team, {}, options, label = "Plan", required = true) }
        compose
            .onNodeWithText("Starter")
            .assert(SemanticsMatcher.expectValue(SemanticsProperties.Role, Role.RadioButton))
            .assert(hasText("Up to 5 members"))
            .assert(hasAnyAncestor(SemanticsMatcher.keyIsDefined(SemanticsProperties.SelectableGroup)))
        compose
            .onNodeWithText("Plan")
            .assert(SemanticsMatcher.keyIsDefined(SemanticsProperties.Heading))
            .assert(hasContentDescription("Plan, required"))
    }

    @Test
    fun disabledOptionsAndReadOnlyGroupsCannotBeChanged() {
        var plan by mutableStateOf(Plan.Team)
        compose.setQeetrix {
            androidx.compose.foundation.layout.Column {
                QeetrixRadioGroup(plan, { plan = it }, options, label = "Plan")
                QeetrixRadioGroup(Plan.Team, {
                    plan = it
                }, listOf(QeetrixOption(Plan.Starter, "Small"), QeetrixOption(Plan.Team, "Large")), label = "Size", readOnly = true)
            }
        }
        compose.onNodeWithText("Enterprise").assertIsNotEnabled().performClick()
        compose.onNodeWithText("Small").performClick()
        assertEquals(Plan.Team, plan)
    }

    @Test
    fun theGroupsErrorIsHeardOnEveryOptionAndShownAfterThem() {
        compose.setQeetrix {
            QeetrixRadioGroup<Plan?>(null, {}, options, label = "Plan", description = "You can change it later.", error = "Choose a plan.")
        }
        for (name in listOf("Starter", "Team")) {
            compose.onNodeWithText(name).assert(SemanticsMatcher.expectValue(SemanticsProperties.Error, "Choose a plan."))
        }
        compose.onNodeWithText("You can change it later.").assertDoesNotExist()
        val team = compose.onNodeWithText("Enterprise").getUnclippedBoundsInRoot()
        val error = compose.onNodeWithText("Choose a plan.").getUnclippedBoundsInRoot()
        assertTrue(error.top >= team.bottom)
    }

    @Test
    @GraphicsMode(GraphicsMode.Mode.NATIVE)
    fun rowsAreFullTouchTargetsAndSitFlush() {
        compose.setQeetrix { QeetrixRadioGroup(Plan.Team, {}, options, label = "Plan") }
        val starter = compose.onNodeWithText("Starter").assertHeightIsAtLeast(48.dp).getUnclippedBoundsInRoot()
        val team = compose.onNodeWithText("Team").assertHeightIsAtLeast(48.dp).getUnclippedBoundsInRoot()
        assertEquals(starter.bottom, team.top)
    }

    /** field.css: horizontal options wrap in a row, space.gap.lg (16 dp) apart. */
    @Test
    @GraphicsMode(GraphicsMode.Mode.NATIVE)
    fun horizontalOptionsSitInARowSixteenDpApart() {
        compose.setQeetrix {
            QeetrixRadioGroup(
                Plan.Team,
                {},
                options.take(2),
                label = "Plan",
                orientation = QeetrixChoiceOrientation.Horizontal,
            )
        }
        val starter = compose.onNodeWithText("Starter").getUnclippedBoundsInRoot()
        val team = compose.onNodeWithText("Team").getUnclippedBoundsInRoot()
        assertEquals(starter.top, team.top)
        assertEquals(16f, (team.left - starter.right).value, 0.5f)
    }

    @Test
    fun tilesAreRadioButtonsTooAndTheWholeTileChooses() {
        var plan by mutableStateOf(Plan.Starter)
        val tiles =
            listOf(
                QeetrixOption(Plan.Starter, "Web app", "Sign-in with a redirect", icon = QeetrixIconName.House),
                QeetrixOption(Plan.Team, "Service", "Machine to machine", icon = QeetrixIconName.Server),
            )
        compose.setQeetrix {
            QeetrixRadioGroup(plan, { plan = it }, tiles, label = "Application type", variant = QeetrixChoiceVariant.Tile, columns = 2)
        }
        compose
            .onNodeWithText("Service")
            .assert(SemanticsMatcher.expectValue(SemanticsProperties.Role, Role.RadioButton))
            .assert(hasText("Machine to machine"))
        compose.onNodeWithText("Machine to machine", useUnmergedTree = true).performClick()
        assertEquals(Plan.Team, plan)
    }

    @Test
    fun optionsMustHaveValuesOfTheirOwn() {
        assertThrows(IllegalArgumentException::class.java) {
            requireDistinctValues(listOf(QeetrixOption(1, "One"), QeetrixOption(1, "Also one")))
        }
    }
}
