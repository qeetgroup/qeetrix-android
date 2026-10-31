package com.qeetgroup.qeetrix.sample.components.datadisplay

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.qeetgroup.qeetrix.components.avatar.QeetrixAvatarGroup
import com.qeetgroup.qeetrix.components.avatar.QeetrixAvatarPerson
import com.qeetgroup.qeetrix.components.avatar.QeetrixAvatarShape
import com.qeetgroup.qeetrix.components.avatar.QeetrixAvatarSize
import com.qeetgroup.qeetrix.sample.SampleNote
import com.qeetgroup.qeetrix.sample.SamplePage
import com.qeetgroup.qeetrix.sample.SamplePreview
import com.qeetgroup.qeetrix.sample.SampleSection

private val Team =
    listOf("Priya Sharma", "Arjun Mehta", "Lakshmi Iyer", "Rahul Verma", "Ananya Rao", "Vikram Singh").map {
        QeetrixAvatarPerson(it)
    }

/** Avatar groups: a few people, more than fit with the rest counted, a large total, sizes and the square shape. */
@Composable
fun AvatarGroupSample() {
    SamplePage {
        SampleSection("Who is in a workspace") {
            QeetrixAvatarGroup(Team.take(3))
            QeetrixAvatarGroup(Team)
            QeetrixAvatarGroup(Team, total = 1248)
            SampleNote("TalkBack reads the group once: \"6 people: Priya Sharma, Arjun Mehta, Lakshmi Iyer, and 3 more\".")
        }
        SampleSection("Sizes and shape") {
            for (size in QeetrixAvatarSize.entries) QeetrixAvatarGroup(Team, size = size)
            QeetrixAvatarGroup(Team, shape = QeetrixAvatarShape.Square, max = 4)
        }
    }
}

@PreviewLightDark
@Composable
private fun AvatarGroupSamplePreview() {
    SamplePreview { AvatarGroupSample() }
}
