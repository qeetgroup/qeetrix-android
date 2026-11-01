package com.qeetgroup.qeetrix.components.card

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.Measurable
import androidx.compose.ui.layout.ParentDataModifier
import androidx.compose.ui.layout.Placeable
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import com.qeetgroup.qeetrix.foundation.shape.QeetrixShapes
import com.qeetgroup.qeetrix.foundation.sizing.QeetrixBorder
import com.qeetgroup.qeetrix.foundation.spacing.QeetrixSpace
import com.qeetgroup.qeetrix.internal.QeetrixActionRow
import com.qeetgroup.qeetrix.internal.firstLineHeight
import com.qeetgroup.qeetrix.internal.labelDirection
import com.qeetgroup.qeetrix.theme.LocalQeetrixContentColor
import com.qeetgroup.qeetrix.theme.QeetrixTheme
import kotlin.math.max

/** The surface of a [QeetrixCard] (`value` is `@qeetrix/react`'s `variant`). Both are ruled and flat (design rules §12). */
public enum class QeetrixCardVariant(
    public val value: String,
) {
    /** `color.surface.raised`: the content of a page. The default. */
    Default("default"),

    /** `color.surface.sunken`: a secondary panel beside or below the main content. Still ruled, still no shadow. */
    Subdued("subdued"),
}

/** What a [QeetrixCard] means (`value` is `@qeetrix/react`'s `tone`). */
public enum class QeetrixCardTone(
    public val value: String,
) {
    /** The default. */
    Neutral("neutral"),

    /**
     * A "danger zone" of settings that delete or can't be undone: the border `color.border.danger` and the title
     * `color.text.danger`. The rules inside stay neutral, and the title still says what it is: colour never stands
     * alone.
     */
    Danger("danger"),
}

/**
 * Related content on a page — `@qeetrix/react`'s Card: a ruled, flat surface (`color.surface.raised`, a 1 dp
 * `color.border.default` edge, 4 dp corners, no shadow; design rules §12).
 *
 * - **Header.** A [title] (`title.sm`) and [description] (`body.sm`, `color.text.subtle`), with [actions] at its end
 *   (a ghost "View all" button), on 16 dp padding, over a 1 dp rule when a body follows. Without any of the three
 *   there is no header.
 * - **Body** ([content]): 16 dp padding, `body.md` text in `color.text.default`. A
 *   [com.qeetgroup.qeetrix.components.descriptionlist.QeetrixDescriptionList] in the rows layout placed directly in it
 *   runs edge to edge, its rows keeping the card's padding. Without content there is no body.
 * - **Footer** ([footer]): actions, end-aligned and wrapping, on 16 dp padding under a 1 dp rule.
 *
 * The title is a heading for accessibility services, so TalkBack's heading navigation reaches the card. The web
 * takes a heading `level` with the title; Android has no heading levels.
 *
 * **A card is not a click target** (06-component-specs §9, QTRX-182). A card that is chosen — a plan, a sign-in method —
 * is a choice tile: [com.qeetgroup.qeetrix.components.radiogroup.QeetrixRadioGroup] or
 * [com.qeetgroup.qeetrix.components.checkbox.QeetrixCheckboxGroup] with `variant` Tile. A card that opens a page keeps
 * that action in its content — a button in [actions] or [footer] — and a list of places to go is a list of
 * [com.qeetgroup.qeetrix.components.list.QeetrixListItem]s.
 *
 * ```
 * QeetrixCard(
 *     title = "Workspace",
 *     description = "3 members",
 *     actions = { QeetrixButton("View all", onClick = ::openMembers, variant = QeetrixButtonVariant.Ghost) },
 *     footer = { QeetrixButton("Manage", onClick = ::manage) },
 * ) {
 *     QeetrixText("Created 12 March.")
 * }
 * QeetrixCard(title = "Delete workspace", tone = QeetrixCardTone.Danger) { … }
 * ```
 */
@Composable
public fun QeetrixCard(
    modifier: Modifier = Modifier,
    title: String? = null,
    description: String? = null,
    variant: QeetrixCardVariant = QeetrixCardVariant.Default,
    tone: QeetrixCardTone = QeetrixCardTone.Neutral,
    actions: (@Composable () -> Unit)? = null,
    footer: (@Composable () -> Unit)? = null,
    content: (@Composable () -> Unit)? = null,
) {
    CardSurface(modifier, title, description, variant, tone, actions, footer, content)
}

@Composable
private fun CardSurface(
    modifier: Modifier,
    title: String?,
    description: String?,
    variant: QeetrixCardVariant,
    tone: QeetrixCardTone,
    actions: (@Composable () -> Unit)?,
    footer: (@Composable () -> Unit)?,
    content: (@Composable () -> Unit)?,
) {
    val colors = QeetrixTheme.colors
    val shape = QeetrixShapes.Surface // .qx-card border-radius: radius.surface
    val background = if (variant == QeetrixCardVariant.Subdued) colors.surfaceSunken else colors.surfaceRaised // [data-variant="subdued"]
    // .qx-card border: border.width.default solid color.border.default; [data-tone="danger"] color.border.danger
    val edge = if (tone == QeetrixCardTone.Danger) colors.borderDanger else colors.borderDefault
    val surface =
        modifier
            .clip(shape)
            .background(background)
            .border(QeetrixBorder.WidthDefault, edge, shape)
            .padding(QeetrixBorder.WidthDefault) // the content box sits inside the border

    CompositionLocalProvider(LocalQeetrixContentColor provides colors.textDefault) {
        Column(surface) {
            val hasHeader = title != null || description != null || actions != null
            if (hasHeader) {
                CardHeader(
                    title = title,
                    description = description,
                    // [data-tone="danger"] … .qx-heading
                    titleColor = if (tone == QeetrixCardTone.Danger) colors.textDanger else colors.textDefault,
                    descriptionColor = colors.textSubtle,
                    actions = actions,
                )
            }
            // The rule between the header and the body is the body's, so a header that ends the card or meets the
            // footer has none: .qx-card-header:is(:last-child, :has(+ .qx-card-footer, + .qx-card-body:empty)).
            if (content != null) CardBody(ruled = hasHeader, content)
            if (footer != null) CardFooter(footer)
        }
    }
}

/**
 * `.qx-card-header`: the title and description at the start, the actions at the end, 16 dp padding
 * (`space.inset.lg`) and `space.gap.md` between. Its 1 dp `color.border.default` rule is drawn by the body
 * ([CardBody]), so it is there only when a body with content follows.
 *
 * The actions are centred on the title's first line and may reach 12 dp into the padding above and below
 * without growing the header: the web's `margin-block: -4px`, plus the 8 dp of the 48 dp touch area Android
 * reserves round a 32 dp control. So a `md` button sits as it does on the web, beside a one-line title.
 */
@Composable
private fun CardHeader(
    title: String?,
    description: String?,
    titleColor: Color,
    descriptionColor: Color,
    actions: (@Composable () -> Unit)?,
) {
    val typography = QeetrixTheme.typography
    val line = firstLineHeight(if (title != null) typography.titleSm else typography.bodySm)
    Layout(
        content = {
            // .qx-card-heading: a column, space.gap.xs apart.
            Column(verticalArrangement = Arrangement.spacedBy(QeetrixSpace.GapXs)) {
                if (title != null) {
                    // <Heading size="title-sm">
                    BasicText(
                        title,
                        Modifier.fillMaxWidth().semantics {
                            heading()
                        },
                        style = typography.titleSm.copy(color = titleColor).labelDirection(alignToLayout = true),
                    )
                }
                if (description != null) {
                    // .qx-card-description: body.sm, color.text.subtle
                    BasicText(
                        description,
                        Modifier.fillMaxWidth(),
                        style = typography.bodySm.copy(color = descriptionColor).labelDirection(alignToLayout = true),
                    )
                }
            }
            if (actions != null) {
                // .qx-card-actions: gap space.gap.sm
                Row(horizontalArrangement = Arrangement.spacedBy(QeetrixSpace.GapSm), verticalAlignment = Alignment.CenterVertically) {
                    actions()
                }
            }
        },
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(QeetrixSpace.InsetLg),
    ) { measurables, constraints ->
        val width = constraints.maxWidth
        val gap = QeetrixSpace.GapMd.roundToPx()
        val bleed = ACTIONS_BLEED.roundToPx()
        val actionsPlaceable = measurables.getOrNull(1)?.measure(Constraints(maxWidth = width))
        val headingWidth = (width - (actionsPlaceable?.let { it.width + gap } ?: 0)).coerceAtLeast(0)
        val heading = measurables[0].measure(Constraints.fixedWidth(headingWidth))
        val centre = line.roundToPx() / 2
        val actionsTop = actionsPlaceable?.let { max(centre - it.height / 2, -bleed) } ?: 0
        val height = max(heading.height, actionsPlaceable?.let { actionsTop + it.height - bleed } ?: 0).coerceAtLeast(0)
        layout(width, height) {
            heading.placeRelative(0, 0)
            actionsPlaceable?.placeRelative(width - actionsPlaceable.width, actionsTop)
        }
    }
}

/** What a 48 dp touch area adds on each side of a 32 dp (`md`) control. */
private val TOUCH_SLACK = 8.dp

/** `.qx-card-actions margin-block: -4px`, plus the 8 dp a 48 dp touch area adds round a 32 dp control. */
private val ACTIONS_BLEED = 4.dp + TOUCH_SLACK

/**
 * `.qx-card-body`: `space.inset.lg` round its children, stacked as blocks. A child that marks itself with
 * [qeetrixCardBleed] (a description list in rows) runs edge to edge instead — `inline-size: calc(100% + 2 × inset)`,
 * `margin-inline: -inset` — and, first or last, meets the header's rule or the card's edge (`margin-block`).
 *
 * After a header ([ruled]) the body draws the header's 1 dp `color.border.default` rule along its top
 * (`.qx-card-header` `border-block-end`). Content that emits nothing takes no room and draws no rule
 * (`.qx-card-body:empty { display: none }`, QTRX-174).
 */
@Composable
private fun CardBody(
    ruled: Boolean,
    content: @Composable () -> Unit,
) {
    val rule = QeetrixTheme.colors.borderDefault
    CompositionLocalProvider(LocalInsideQeetrixCardBody provides true) {
        Layout(
            content,
            Modifier.fillMaxWidth().drawBehind {
                // Nothing is drawn by a body that took no room.
                if (!ruled || size.height == 0f) return@drawBehind
                // border-block-end: border.width.default solid color.border.default (the header's)
                drawRect(rule, Offset.Zero, Size(size.width, QeetrixBorder.WidthDefault.toPx()))
            },
        ) { measurables, constraints ->
            if (measurables.isEmpty()) return@Layout layout(0, 0) {}
            val inset = QeetrixSpace.InsetLg.roundToPx()
            val top = if (ruled) QeetrixBorder.WidthDefault.roundToPx() else 0
            val loose = constraints.copy(minWidth = 0, minHeight = 0)

            fun bleeds(measurable: Measurable) = measurable.parentData is QeetrixCardBleed
            val inner =
                if (constraints.hasBoundedWidth) (constraints.maxWidth - 2 * inset).coerceAtLeast(0) else Constraints.Infinity
            val placeables = arrayOfNulls<Placeable>(measurables.size)
            measurables.forEachIndexed { index, measurable ->
                if (!bleeds(measurable)) placeables[index] = measurable.measure(loose.copy(maxWidth = inner))
            }
            val width =
                if (constraints.hasBoundedWidth) {
                    constraints.maxWidth
                } else {
                    (placeables.filterNotNull().maxOfOrNull { it.width } ?: 0) + 2 * inset
                }
            measurables.forEachIndexed { index, measurable ->
                if (bleeds(measurable)) {
                    (measurable.parentData as QeetrixCardBleed).inset = inset
                    placeables[index] = measurable.measure(Constraints.fixedWidth(width))
                }
            }
            val tops = IntArray(measurables.size)
            var y = top + inset
            measurables.forEachIndexed { index, measurable ->
                if (index == 0 && bleeds(measurable)) y = top // :first-child margin-block-start: -inset
                tops[index] = y
                y += placeables[index]!!.height
            }
            val last = measurables.lastOrNull()
            val height = y + if (last != null && bleeds(last)) 0 else inset // :last-child margin-block-end: -inset
            layout(width, height.coerceIn(constraints.minHeight, constraints.maxHeight)) {
                measurables.forEachIndexed { index, measurable ->
                    placeables[index]!!.placeRelative(if (bleeds(measurable)) 0 else inset, tops[index])
                }
            }
        }
    }
}

/**
 * `.qx-card-footer`: end-aligned, wrapping actions `space.gap.sm` apart, on `space.inset.lg` under a 1 dp rule. Its
 * buttons reserve 48 dp touch areas, 8 dp more than a `md` button's drawn 32 dp on each side, so the footer pads
 * those 8 dp less above and below: the drawn buttons sit 16 dp from the rule and the edge, as on the web.
 */
@Composable
private fun CardFooter(footer: @Composable () -> Unit) {
    val rule = QeetrixTheme.colors.borderDefault
    QeetrixActionRow(
        Modifier
            .fillMaxWidth()
            .drawBehind {
                // border-block-start: border.width.default solid color.border.default
                drawRect(rule, Offset.Zero, Size(size.width, QeetrixBorder.WidthDefault.toPx()))
            }.padding(
                start = QeetrixSpace.InsetLg,
                end = QeetrixSpace.InsetLg,
                top = QeetrixSpace.InsetLg - TOUCH_SLACK + QeetrixBorder.WidthDefault,
                bottom = QeetrixSpace.InsetLg - TOUCH_SLACK,
            ),
        content = footer,
    )
}

/** Whether this subtree is a card's body, where a description list in rows may run edge to edge. */
internal val LocalInsideQeetrixCardBody = staticCompositionLocalOf { false }

/**
 * A card body's edge-to-edge child: the parent data a [QeetrixCard]'s body looks for. The body writes [inset], its
 * padding in px, before it measures the child, so the child can keep its own content on the card's padding; outside
 * a card body it stays 0.
 */
internal class QeetrixCardBleed {
    var inset: Int = 0
}

/**
 * Marks this element as a card body's edge-to-edge child, [slot] receiving the body's padding. Only where nothing else
 * has set parent data, so a `Column`'s `weight` or a `Row`'s alignment always wins.
 */
internal fun Modifier.qeetrixCardBleed(slot: QeetrixCardBleed): Modifier = this.then(CardBleedModifier(slot))

private class CardBleedModifier(
    private val slot: QeetrixCardBleed,
) : ParentDataModifier {
    override fun Density.modifyParentData(parentData: Any?): Any = parentData ?: slot

    override fun equals(other: Any?): Boolean = other is CardBleedModifier && other.slot === slot

    override fun hashCode(): Int = System.identityHashCode(slot)
}
