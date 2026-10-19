package com.qeetgroup.qeetrix.sample.components.datadisplay

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.painter.BrushPainter
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.qeetgroup.qeetrix.components.avatar.QeetrixAvatar
import com.qeetgroup.qeetrix.components.avatar.QeetrixAvatarShape
import com.qeetgroup.qeetrix.components.avatar.QeetrixAvatarSize
import com.qeetgroup.qeetrix.sample.SampleFlow
import com.qeetgroup.qeetrix.sample.SampleNote
import com.qeetgroup.qeetrix.sample.SamplePage
import com.qeetgroup.qeetrix.sample.SamplePreview
import com.qeetgroup.qeetrix.sample.SampleSection
import com.qeetgroup.qeetrix.theme.QeetrixTheme

/** Avatars: sizes and shapes, a supplied image, the initials and icon fallbacks, and a status badge. */
@Composable
fun AvatarSample() {
    SamplePage {
        SampleSection("Sizes") {
            SampleFlow { for (size in QeetrixAvatarSize.entries) QeetrixAvatar("Ada Lovelace", size = size) }
        }
        SampleSection("Shapes") {
            SampleFlow {
                QeetrixAvatar("Ada Lovelace", size = QeetrixAvatarSize.Lg)
                QeetrixAvatar("Qeet Group", size = QeetrixAvatarSize.Lg, shape = QeetrixAvatarShape.Square)
            }
            SampleNote("Circle for people; square for organisations, workspaces and apps.")
        }
        SampleSection("Image and fallbacks") {
            val colors = QeetrixTheme.colors
            val photo = remember(colors) { BrushPainter(Brush.linearGradient(listOf(colors.dataCategorical1, colors.dataCategorical3))) }
            SampleFlow {
                QeetrixAvatar("Grace Hopper", image = photo, size = QeetrixAvatarSize.Xl)
                QeetrixAvatar("Grace Hopper", size = QeetrixAvatarSize.Xl)
                QeetrixAvatar("नमन शर्मा", size = QeetrixAvatarSize.Xl)
                QeetrixAvatar("أحمد علي", size = QeetrixAvatarSize.Xl)
                QeetrixAvatar(null, size = QeetrixAvatarSize.Xl)
            }
            SampleNote(
                "Load the picture yourself (from resources, Coil, Glide…) and pass a Painter; until it is " +
                    "ready, pass none and the initials show. Initials keep combining marks with their letter, in " +
                    "any script. With no name, a neutral person icon.",
            )
        }
        SampleSection("Status") {
            SampleFlow {
                QeetrixAvatar("Priya Raman", size = QeetrixAvatarSize.Lg, badgeLabel = "Online")
                QeetrixAvatar("Qeet Group", size = QeetrixAvatarSize.Lg, shape = QeetrixAvatarShape.Square, badgeLabel = "Verified")
            }
            SampleNote("TalkBack reads one image: \"Priya Raman, Online\". The badge moves to the other side right to left.")
        }
    }
}

@PreviewLightDark
@Composable
private fun AvatarSamplePreview() {
    SamplePreview { AvatarSample() }
}
