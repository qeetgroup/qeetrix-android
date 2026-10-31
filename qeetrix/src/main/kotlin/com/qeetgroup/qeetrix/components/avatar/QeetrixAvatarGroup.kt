package com.qeetgroup.qeetrix.components.avatar

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.unit.Density
import com.qeetgroup.qeetrix.R
import com.qeetgroup.qeetrix.foundation.format.QeetrixFormat
import com.qeetgroup.qeetrix.foundation.shape.QeetrixShapes
import com.qeetgroup.qeetrix.foundation.sizing.QeetrixBorder
import com.qeetgroup.qeetrix.foundation.spacing.QeetrixSpace
import com.qeetgroup.qeetrix.theme.QeetrixTheme

/** One person, organisation or application in a [QeetrixAvatarGroup]. */
public class QeetrixAvatarPerson(
    public val name: String,
    public val image: Painter? = null,
)

/**
 * A few people at a glance — `@qeetrix/react`'s AvatarGroup: a group's members, an application's users. The first [max]
 * avatars side by side, a 2 dp hairline apart (`border.width.strong`) so initials stay whole on any background, then
 * "+N" for the rest in a `color.bg.neutral.subtle-pressed` tile as tall as the avatars (avatar-group.css).
 *
 * - [total] is how many there are in all when [people] is only the first few: a group's 412 members.
 * - [shape] [QeetrixAvatarShape.Square] for organisations and applications, with a square +N.
 * - **Accessibility**: one image, named in full — "412 people: Priya Sharma, Rahul Verma, Ananya Iyer and 409 more"
 *   (`avatarGroup.label`, `avatarGroup.more`), counts in the device's locale.
 *
 * ```
 * QeetrixAvatarGroup(members.take(3).map { QeetrixAvatarPerson(it.name) }, total = group.memberCount)
 * ```
 */
@Composable
public fun QeetrixAvatarGroup(
    people: List<QeetrixAvatarPerson>,
    modifier: Modifier = Modifier,
    max: Int = 3,
    total: Int = people.size,
    size: QeetrixAvatarSize = QeetrixAvatarSize.Sm,
    shape: QeetrixAvatarShape = QeetrixAvatarShape.Circle,
) {
    require(max >= 1) { "An avatar group shows at least one avatar." }
    require(total >= people.size) { "total counts everyone, so it is at least people.size." }
    val locale = LocalConfiguration.current.locales[0]
    val shown = people.take(max)
    val rest = total - shown.size
    val more = if (rest > 0) stringResource(R.string.qx_avatar_group_more, QeetrixFormat.number(rest, locale)) else null
    val names = (shown.map { it.name } + listOfNotNull(more)).let { joinNames(it, locale) }
    val label = stringResource(R.string.qx_avatar_group_label, QeetrixFormat.number(total, locale), names)
    val colors = QeetrixTheme.colors
    Row(
        modifier.clearAndSetSemantics {
            role = Role.Image
            contentDescription = label
        },
        horizontalArrangement = Arrangement.spacedBy(QeetrixBorder.WidthStrong),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        for (person in shown) QeetrixAvatar(person.name, image = person.image, size = size, shape = shape, decorative = true)
        if (rest > 0) {
            // .qx-avatar-group-more: block-size and min-inline-size the avatar's, padding-inline space.gap.xs
            Box(
                Modifier
                    .heightIn(min = size.side)
                    .widthIn(min = size.side)
                    .background(
                        colors.bgNeutralSubtlePressed,
                        if (shape ==
                            QeetrixAvatarShape.Square
                        ) {
                            QeetrixShapes.Control
                        } else {
                            QeetrixShapes.Full
                        },
                    ).padding(horizontal = QeetrixSpace.GapXs),
                contentAlignment = Alignment.Center,
            ) {
                val style =
                    if (size == QeetrixAvatarSize.Lg ||
                        size == QeetrixAvatarSize.Xl
                    ) {
                        QeetrixTheme.typography.labelMd
                    } else {
                        QeetrixTheme.typography.labelSm
                    }
                // At a font scale of 1, as the avatars' initials are: the count is the group's last avatar, and the
                // whole group is read in words ("… and 3 others").
                val density = LocalDensity.current
                CompositionLocalProvider(LocalDensity provides Density(density.density, fontScale = 1f)) {
                    BasicText(
                        "+${QeetrixFormat.number(rest, locale)}",
                        style = style.copy(color = colors.textDefault, fontFeatureSettings = "tnum"),
                        maxLines = 1,
                        softWrap = false,
                    )
                }
            }
        }
    }
}

/** The names as the locale lists them: "Priya, Rahul and 409 more" (Intl.ListFormat's conjunction). */
private fun joinNames(
    names: List<String>,
    locale: java.util.Locale,
): String =
    android.icu.text.ListFormatter
        .getInstance(locale)
        .format(names)
