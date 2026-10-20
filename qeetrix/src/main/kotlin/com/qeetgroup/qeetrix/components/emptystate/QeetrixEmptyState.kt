package com.qeetgroup.qeetrix.components.emptystate

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.qeetgroup.qeetrix.foundation.shape.QeetrixCorner
import com.qeetgroup.qeetrix.foundation.sizing.QeetrixStroke
import com.qeetgroup.qeetrix.icons.QeetrixIcon
import com.qeetgroup.qeetrix.icons.QeetrixIconName
import com.qeetgroup.qeetrix.internal.FontWeightSemibold
import com.qeetgroup.qeetrix.internal.QeetrixFont
import com.qeetgroup.qeetrix.internal.QeetrixWebScale
import com.qeetgroup.qeetrix.internal.qeetrixTextStyle
import com.qeetgroup.qeetrix.theme.QeetrixTheme

/**
 * Why there is nothing to show (`value` is the web name). The variant sets the icon tile's tone and default
 * icon; the words and the actions are always the app's.
 */
public enum class QeetrixEmptyStateVariant(
    public val value: String,
) {
    /** A neutral empty collection: "No invoices yet". */
    Default("default"),

    /**
     * An invitation to create the first thing — the only variant with the brand tint. Pair it with a primary
     * [com.qeetgroup.qeetrix.components.button.QeetrixButton].
     */
    FirstUse("first-use"),

    /** The data exists and a search or filter excluded it. Shows search-x; offer a way to clear. */
    NoResults("no-results"),

    /** The data exists and the person can't see it. Shows lock; say who can grant access. */
    NoPermission("no-permission"),

    /** Loading failed. Only the tile takes the destructive tint, so the failure reads as recoverable; shows triangle-alert. */
    Error("error"),
}

/** How much room a [QeetrixEmptyState] takes (`value` is the web name). */
public enum class QeetrixEmptyStateSize(
    public val value: String,
) {
    /** For a section of a screen, a side panel or a list's body. */
    Sm("sm"),

    /** For a collection's whole area. The default. */
    Default("default"),

    /** For a whole screen. */
    Lg("lg"),
}

/**
 * What a surface shows when it has nothing to show: an icon on a tinted tile, a [title], a [description]
 * and [actions] — the web's `EmptyState`.
 *
 * ```
 * QeetrixEmptyState(
 *     title = "No invoices yet",
 *     description = "Invoices appear here once you bill a customer.",
 *     variant = QeetrixEmptyStateVariant.FirstUse,
 *     icon = QeetrixIconName.FileText,
 * ) {
 *     QeetrixButton("Create invoice", onClick = ::createInvoice)
 * }
 * ```
 *
 * - **It renders what you give it.** Whether a surface is empty because there is no data, because a filter
 *   excluded it, because the person lacks permission or because loading failed is the app's to decide — and
 *   so is what an action does. Never put a raw error, a status code or a stack trace in the description: say
 *   what happened and what the person can do.
 * - **Accessibility.** The title is a heading; the description is read after it; the tile is decorative;
 *   actions are their own controls.
 * - **Layout.** Centred, with the web's padding for each [size]; the description is at most 384 dp wide;
 *   actions wrap onto more lines when they don't fit. Text follows the font scale.
 *
 * For the loading, failure and empty branches of one surface together, use [QeetrixDataState].
 *
 * @param icon The tile's icon. `null` uses the variant's: search-x, lock or triangle-alert for no results,
 *   no permission and an error; none for the others.
 * @param actions Buttons under the description, in a centred row that wraps.
 */
@Composable
public fun QeetrixEmptyState(
    title: String,
    modifier: Modifier = Modifier,
    description: String? = null,
    variant: QeetrixEmptyStateVariant = QeetrixEmptyStateVariant.Default,
    size: QeetrixEmptyStateSize = QeetrixEmptyStateSize.Default,
    icon: QeetrixIconName? = null,
    actions: (@Composable () -> Unit)? = null,
) {
    EmptyStateContent(title, description, variant, size, icon, announce = false, modifier, actions)
}

/**
 * The empty state's layout, also without a title (a data state's failure). With [announce], the description
 * is an assertive live region, read out as it appears — the web failure's `role="alert"`.
 */
@Composable
internal fun EmptyStateContent(
    title: String?,
    description: String?,
    variant: QeetrixEmptyStateVariant,
    size: QeetrixEmptyStateSize,
    icon: QeetrixIconName?,
    announce: Boolean,
    modifier: Modifier,
    actions: (@Composable () -> Unit)?,
) {
    val metrics = EmptyStateMetrics.of(size)
    val colors = QeetrixTheme.colors
    Column(
        modifier.fillMaxWidth().padding(horizontal = metrics.paddingX, vertical = metrics.paddingY),
        verticalArrangement = Arrangement.spacedBy(metrics.gap),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        val glyph = icon ?: defaultIcon(variant)
        if (glyph != null) EmptyStateTile(glyph, variant, metrics, Modifier.padding(bottom = metrics.tileMargin))
        if (title != null) {
            // Text fills its line and is centred in it, rather than measured to its content: content-width text
            // in a right-to-left paragraph can wrap its last word.
            BasicText(
                title,
                Modifier.fillMaxWidth().semantics { heading() },
                style = metrics.titleStyle.copy(color = colors.textPrimary, textAlign = TextAlign.Center),
            )
        }
        if (description != null) {
            BasicText(
                description,
                Modifier
                    .widthIn(max = DescriptionMaxWidth)
                    .fillMaxWidth()
                    .then(if (announce) Modifier.semantics { liveRegion = LiveRegionMode.Assertive } else Modifier),
                style = DescriptionStyle.copy(color = colors.textTertiary, textAlign = TextAlign.Center), // text-muted-foreground
            )
        }
        if (actions != null) {
            // flex flex-wrap justify-center gap-2
            FlowRow(
                Modifier.padding(top = metrics.actionsMargin - metrics.gap),
                horizontalArrangement = Arrangement.spacedBy(QeetrixWebScale.spacing(2f), Alignment.CenterHorizontally),
                verticalArrangement = Arrangement.spacedBy(QeetrixWebScale.spacing(2f)),
            ) { actions() }
        }
    }
}

/**
 * The icon on its tinted tile, with an inset subtle ring: graphite for most reasons, the brand for a first
 * use, the destructive tint for an error. Decorative: the title says it.
 */
@Composable
private fun EmptyStateTile(
    icon: QeetrixIconName,
    variant: QeetrixEmptyStateVariant,
    metrics: EmptyStateMetrics,
    modifier: Modifier,
) {
    val colors = QeetrixTheme.colors
    val (background, foreground) =
        when (variant) {
            // bg-surface-sunken text-muted-foreground
            QeetrixEmptyStateVariant.Default,
            QeetrixEmptyStateVariant.NoResults,
            QeetrixEmptyStateVariant.NoPermission,
            -> colors.surfaceSunken to colors.textTertiary

            QeetrixEmptyStateVariant.FirstUse -> colors.surfaceBrandSubtle to colors.textBrand

            // bg-brand-subtle text-brand

            // bg-destructive-subtle text-destructive-text
            QeetrixEmptyStateVariant.Error -> colors.feedbackErrorSubtle to colors.textDanger
        }
    val shape = RoundedCornerShape(QeetrixCorner.Surface) // rounded-xl
    Box(
        modifier
            .size(metrics.tileSize)
            .background(background, shape)
            .border(QeetrixStroke.Hairline, colors.borderSubtle, shape), // ring-1 ring-inset ring-border-subtle
        contentAlignment = Alignment.Center,
    ) {
        QeetrixIcon(icon, contentDescription = null, size = metrics.iconSize, tint = foreground)
    }
}

private fun defaultIcon(variant: QeetrixEmptyStateVariant): QeetrixIconName? =
    when (variant) {
        QeetrixEmptyStateVariant.Default, QeetrixEmptyStateVariant.FirstUse -> null
        QeetrixEmptyStateVariant.NoResults -> QeetrixIconName.SearchX
        QeetrixEmptyStateVariant.NoPermission -> QeetrixIconName.Lock
        QeetrixEmptyStateVariant.Error -> QeetrixIconName.TriangleAlert
    }

/** One size's geometry: the web's size classes, resolved. */
private class EmptyStateMetrics(
    val gap: Dp,
    val paddingX: Dp,
    val paddingY: Dp,
    val tileSize: Dp,
    val iconSize: Dp,
    val tileMargin: Dp,
    val actionsMargin: Dp,
    val titleStyle: TextStyle,
) {
    companion object {
        // gap-1.5 px-4 py-6; tile mb-0.5 size-9, icon size-4; title text-sm; actions mt-2
        private val Sm =
            EmptyStateMetrics(
                gap = QeetrixWebScale.spacing(1.5f),
                paddingX = QeetrixWebScale.spacing(4f),
                paddingY = QeetrixWebScale.spacing(6f),
                tileSize = QeetrixWebScale.spacing(9f),
                iconSize = QeetrixWebScale.spacing(4f),
                tileMargin = QeetrixWebScale.spacing(0.5f),
                actionsMargin = QeetrixWebScale.spacing(2f),
                titleStyle = qeetrixTextStyle(QeetrixFont.Heading, QeetrixWebScale.TextSm, FontWeightSemibold),
            )

        // gap-2 px-6 py-12; tile mb-1 size-11, icon size-5; title text-base; actions mt-3
        private val Default =
            EmptyStateMetrics(
                gap = QeetrixWebScale.spacing(2f),
                paddingX = QeetrixWebScale.spacing(6f),
                paddingY = QeetrixWebScale.spacing(12f),
                tileSize = QeetrixWebScale.spacing(11f),
                iconSize = QeetrixWebScale.spacing(5f),
                tileMargin = QeetrixWebScale.spacing(1f),
                actionsMargin = QeetrixWebScale.spacing(3f),
                titleStyle = qeetrixTextStyle(QeetrixFont.Heading, QeetrixWebScale.TextBase, FontWeightSemibold),
            )

        // gap-2.5 px-6 py-16; tile mb-1.5 size-12, icon size-6; title text-lg; actions mt-3
        private val Lg =
            EmptyStateMetrics(
                gap = QeetrixWebScale.spacing(2.5f),
                paddingX = QeetrixWebScale.spacing(6f),
                paddingY = QeetrixWebScale.spacing(16f),
                tileSize = QeetrixWebScale.spacing(12f),
                iconSize = QeetrixWebScale.spacing(6f),
                tileMargin = QeetrixWebScale.spacing(1.5f),
                actionsMargin = QeetrixWebScale.spacing(3f),
                titleStyle = qeetrixTextStyle(QeetrixFont.Heading, QeetrixWebScale.TextLg, FontWeightSemibold),
            )

        fun of(size: QeetrixEmptyStateSize): EmptyStateMetrics =
            when (size) {
                QeetrixEmptyStateSize.Sm -> Sm
                QeetrixEmptyStateSize.Default -> Default
                QeetrixEmptyStateSize.Lg -> Lg
            }
    }
}

/** `max-w-sm`. */
private val DescriptionMaxWidth = 384.dp

// text-sm text-pretty text-muted-foreground
private val DescriptionStyle = qeetrixTextStyle(QeetrixFont.Sans, QeetrixWebScale.TextSm)
