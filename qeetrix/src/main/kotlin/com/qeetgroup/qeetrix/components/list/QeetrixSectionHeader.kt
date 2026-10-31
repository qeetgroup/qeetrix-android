package com.qeetgroup.qeetrix.components.list

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.text
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import com.qeetgroup.qeetrix.foundation.sizing.QeetrixBorder
import com.qeetgroup.qeetrix.foundation.spacing.QeetrixSpace
import com.qeetgroup.qeetrix.internal.labelDirection
import com.qeetgroup.qeetrix.theme.QeetrixTheme

/**
 * The label of a group of rows — a group label in `typography.label.caps`, Qeetrix's one uppercase role (design rules
 * §5): small, tracked capitals in `color.text.subtlest` under a 1 dp `color.border.subtle` rule, as the web
 * Sidebar labels its groups. Above a section of a lazy list, or above a group of [QeetrixListItem]s anywhere else; an
 * optional [description] and [action] follow.
 *
 * `@qeetrix/react` has no section header for rows (a web screen groups a Sidebar with SidebarGroup and a Table with
 * column groups), so this one is Android's own, drawn as a SidebarGroup's label: `space.gap.sm` and the rule above
 * it, `space.inset.md` from the rule to the label, 6 dp from the label to the rows, the rows' 16 dp inset. Pass
 * [ruled] `false` for the first group of a list, as the Sidebar's first group has no rule.
 *
 * ```
 * LazyColumn {
 *     for ((index, group) in groups.withIndex()) {
 *         stickyHeader(key = "header-${group.id}") {
 *             QeetrixSectionHeader(group.label, Modifier.background(QeetrixTheme.colors.surfaceCanvas), ruled = index > 0)
 *         }
 *         items(group.members, key = { it.id }) { member -> QeetrixListItem(member.name) }
 *     }
 * }
 * ```
 *
 * - **Write the label in sentence case** ("Members", "Pending invitations"); the capitals are styling, so
 *   accessibility services read the words as written, and Indic text — which has no capitals — renders as written,
 *   without the tracking (the web drops it for Hindi, Marathi, Bengali, Tamil and the other Indic languages).
 * - The label is a heading for accessibility services, so TalkBack's heading navigation moves between groups; the
 *   description is read with it, and the action is its own control — typically a ghost
 *   [com.qeetgroup.qeetrix.components.button.QeetrixButton].
 * - A sticky header draws over the rows scrolling under it, so give it the surface it sits on through [modifier].
 */
@Composable
public fun QeetrixSectionHeader(
    title: String,
    modifier: Modifier = Modifier,
    description: String? = null,
    ruled: Boolean = true,
    action: (@Composable () -> Unit)? = null,
) {
    val colors = QeetrixTheme.colors
    val rule = colors.borderSubtle
    Row(
        modifier
            .fillMaxWidth()
            .drawBehind {
                if (!ruled) return@drawBehind
                // .qx-sidebar-group + .qx-sidebar-group: margin-block-start gap.sm, then border-block-start border.subtle
                val y = QeetrixSpace.GapSm.toPx()
                drawRect(rule, Offset(0f, y), Size(size.width, QeetrixBorder.WidthDefault.toPx()))
            }.padding(
                start = HorizontalPadding,
                end = HorizontalPadding,
                // Ruled: gap.sm + the rule + padding-block-start inset.md; the first group: the nav's padding-block gap.sm.
                top = if (ruled) QeetrixSpace.GapSm + QeetrixBorder.WidthDefault + QeetrixSpace.InsetMd else QeetrixSpace.GapSm,
                bottom = LABEL_BOTTOM,
            ),
        horizontalArrangement = Arrangement.spacedBy(QeetrixSpace.GapMd),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(
            Modifier.weight(1f).semantics(mergeDescendants = true) { heading() },
            verticalArrangement = Arrangement.spacedBy(QeetrixSpace.GapXs),
        ) {
            QeetrixGroupLabel(title, colors.textSubtlest, Modifier.fillMaxWidth())
            if (description != null) {
                BasicText(
                    description,
                    Modifier.fillMaxWidth(),
                    style =
                        QeetrixTheme.typography.bodySm
                            .copy(color = colors.textSubtle)
                            .labelDirection(alignToLayout = true),
                )
            }
        }
        action?.invoke()
    }
}

/** `.qx-sidebar-group-label padding-block-end: 6px`. */
private val LABEL_BOTTOM = 6.dp

/**
 * A group label: [text] in `typography.label.caps` (11 / 16 sp, weight 600, tracked +6%) in [color], upper-cased for
 * the locale (`text-transform: uppercase`). Indic text keeps no tracking, so its conjuncts stay intact. Accessibility
 * services read [text] as written, not the capitals, which a screen reader may spell out.
 */
@Composable
internal fun QeetrixGroupLabel(
    text: String,
    color: Color,
    modifier: Modifier = Modifier,
) {
    val locale = LocalConfiguration.current.locales[0]
    val shown = remember(text, locale) { text.uppercase(locale) }
    val base = QeetrixTheme.typography.labelCaps
    val style: TextStyle = if (remember(text) { hasIndicScript(text) }) base.copy(letterSpacing = 0.em) else base
    BasicText(
        shown,
        modifier.clearAndSetSemantics { this.text = AnnotatedString(text) },
        style = style.copy(color = color).labelDirection(alignToLayout = true),
    )
}

/** The scripts the web's `:lang(hi, mr, bn, gu, pa, or, ta, te, kn, ml)` covers. */
private val IndicScripts =
    setOf(
        Character.UnicodeScript.DEVANAGARI,
        Character.UnicodeScript.BENGALI,
        Character.UnicodeScript.GURMUKHI,
        Character.UnicodeScript.GUJARATI,
        Character.UnicodeScript.ORIYA,
        Character.UnicodeScript.TAMIL,
        Character.UnicodeScript.TELUGU,
        Character.UnicodeScript.KANNADA,
        Character.UnicodeScript.MALAYALAM,
    )

/** Whether [text] is written in an Indic script — by its letters, so a Hindi label in an English app drops the tracking too. */
internal fun hasIndicScript(text: String): Boolean = text.codePoints().anyMatch { Character.UnicodeScript.of(it) in IndicScripts }
