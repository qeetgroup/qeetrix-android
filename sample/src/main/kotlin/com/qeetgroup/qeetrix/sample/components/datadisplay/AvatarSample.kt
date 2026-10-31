package com.qeetgroup.qeetrix.sample.components.datadisplay

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.painter.BrushPainter
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.qeetgroup.qeetrix.components.avatar.QeetrixAvatar
import com.qeetgroup.qeetrix.components.avatar.QeetrixAvatarItem
import com.qeetgroup.qeetrix.components.avatar.QeetrixAvatarShape
import com.qeetgroup.qeetrix.components.avatar.QeetrixAvatarSize
import com.qeetgroup.qeetrix.sample.SampleFlow
import com.qeetgroup.qeetrix.sample.SampleNote
import com.qeetgroup.qeetrix.sample.SamplePage
import com.qeetgroup.qeetrix.sample.SamplePreview
import com.qeetgroup.qeetrix.sample.SampleSection
import com.qeetgroup.qeetrix.theme.QeetrixTheme

/** Avatars: sizes and shapes, a supplied image, initials in any script, and AvatarItem's tight pair. */
@Composable
fun AvatarSample() {
    SamplePage {
        SampleSection("Sizes") {
            SampleFlow { for (size in QeetrixAvatarSize.entries) QeetrixAvatar("Priya Sharma", size = size) }
            SampleNote("sm 24, md 32 (the default), lg 40 and xl 48 dp, at every density.")
        }
        SampleSection("Shapes") {
            SampleFlow {
                QeetrixAvatar("Priya Sharma", size = QeetrixAvatarSize.Lg)
                QeetrixAvatar("Acme Pvt Ltd", size = QeetrixAvatarSize.Lg, shape = QeetrixAvatarShape.Square)
                QeetrixAvatar("Kaveri Foods Private Limited", size = QeetrixAvatarSize.Lg, shape = QeetrixAvatarShape.Square)
            }
            SampleNote(
                "A circle for a person; a square with 4 dp corners for an organisation, an application or a tenant. A " +
                    "company's legal form doesn't count: Acme Pvt Ltd is AC, Kaveri Foods Private Limited is KF.",
            )
        }
        SampleSection("Image and initials") {
            val colors = QeetrixTheme.colors
            val photo =
                remember(colors) { BrushPainter(Brush.linearGradient(listOf(colors.datavizCategorical1, colors.datavizCategorical3))) }
            SampleFlow {
                QeetrixAvatar("Grace Hopper", image = photo, size = QeetrixAvatarSize.Xl)
                QeetrixAvatar("Grace Hopper", size = QeetrixAvatarSize.Xl)
                QeetrixAvatar("नमन शर्मा", size = QeetrixAvatarSize.Xl)
                QeetrixAvatar("أحمد علي", size = QeetrixAvatarSize.Xl)
            }
            SampleNote(
                "Load the picture yourself (from resources, Coil, Glide…) and pass a Painter; until it is ready, pass none " +
                    "and the initials show, centred on their capitals. Initials keep combining marks with their letter, in " +
                    "any script. TalkBack reads one image named by the name.",
            )
        }
        SampleSection("Avatar item") {
            QeetrixAvatarItem("Priya Sharma", description = "priya.sharma@acme.in")
            QeetrixAvatarItem("Acme Pvt Ltd", description = "Organisation", shape = QeetrixAvatarShape.Square)
            SampleNote(
                "The name sits on the second line's line height, so the pair is as tall as a md avatar. The avatar is " +
                    "decorative and the pair is read as one element.",
            )
        }
    }
}

@PreviewLightDark
@Composable
private fun AvatarSamplePreview() {
    SamplePreview { AvatarSample() }
}
