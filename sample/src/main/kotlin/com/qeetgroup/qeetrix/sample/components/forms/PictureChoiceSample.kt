package com.qeetgroup.qeetrix.sample.components.forms

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.qeetgroup.qeetrix.components.avatar.QeetrixAvatar
import com.qeetgroup.qeetrix.components.avatar.QeetrixAvatarSize
import com.qeetgroup.qeetrix.components.input.QeetrixOption
import com.qeetgroup.qeetrix.components.picturechoice.QeetrixPictureChoiceGroup
import com.qeetgroup.qeetrix.components.picturechoice.QeetrixPictureChoiceShape
import com.qeetgroup.qeetrix.components.picturechoice.QeetrixPictureChoiceSize
import com.qeetgroup.qeetrix.sample.SampleNote
import com.qeetgroup.qeetrix.sample.SamplePage
import com.qeetgroup.qeetrix.sample.SamplePreview
import com.qeetgroup.qeetrix.sample.SampleSection
import com.qeetgroup.qeetrix.theme.QeetrixTheme

/** Choosing an avatar: a background tone from a row of swatches, then a picture from a four-column grid. */
@Composable
fun PictureChoiceSample() {
    val colors = QeetrixTheme.colors
    val tones =
        listOf(
            "ember" to colors.datavizCategorical1,
            "teal" to colors.datavizCategorical2,
            "indigo" to colors.datavizCategorical3,
            "rose" to colors.datavizCategorical4,
            "sand" to colors.bgNeutralSubtle,
            "white" to colors.surfaceDefault,
        )
    val names = mapOf("ember" to "Ember", "teal" to "Teal", "indigo" to "Indigo", "rose" to "Rose", "sand" to "Sand", "white" to "White")
    SamplePage {
        SampleSection("A background tone") {
            var tone by rememberSaveable { mutableStateOf("teal") }
            QeetrixPictureChoiceGroup(
                tone,
                { tone = it },
                tones.map { (id, _) -> QeetrixOption(id, names.getValue(id)) },
                contentDescription = "Background",
                shape = QeetrixPictureChoiceShape.Square,
            ) { option ->
                androidx.compose.foundation.layout
                    .Box(Modifier.fillMaxSize().background(tones.toMap().getValue(option.value)))
            }
            SampleNote("Swatches in a row that wraps. The white one keeps its 1 dp rule, so it shows on a white surface.")
            var small by rememberSaveable { mutableStateOf("ember") }
            QeetrixPictureChoiceGroup(
                small,
                { small = it },
                tones.map { (id, _) -> QeetrixOption(id, names.getValue(id)) },
                contentDescription = "Avatar background",
                size = QeetrixPictureChoiceSize.Sm,
            ) { option ->
                androidx.compose.foundation.layout
                    .Box(Modifier.fillMaxSize().background(tones.toMap().getValue(option.value)))
            }
            SampleNote("sm: 36 dp round swatches, the tick a 16 dp disc; each still takes a tap within 48 dp.")
        }
        SampleSection("An avatar") {
            var avatar by rememberSaveable { mutableStateOf("asha") }
            val people =
                listOf(
                    "asha" to "Asha Rao",
                    "ravi" to "Ravi Kumar",
                    "meera" to "Meera Iyer",
                    "arjun" to "Arjun Nair",
                    "kavya" to "Kavya Shah",
                    "dev" to "Dev Menon",
                    "nila" to "Nila Das",
                    "omar" to "Omar Khan",
                )
            QeetrixPictureChoiceGroup(
                avatar,
                { avatar = it },
                people.map { (id, name) -> QeetrixOption(id, name) },
                contentDescription = "Avatar",
                columns = 4,
            ) { option -> QeetrixAvatar(option.label, size = QeetrixAvatarSize.Xl) }
            SampleNote(
                "The chosen picture has an Ember ring set off by the surface and a tick, so the choice isn't told by colour " +
                    "alone. TalkBack reads each by its name, \"Asha Rao, selected\", as a radio button in the Avatar group.",
            )
        }
    }
}

@PreviewLightDark
@Composable
private fun PictureChoiceSamplePreview() {
    SamplePreview { PictureChoiceSample() }
}
