package com.qeetgroup.qeetrix.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.test.junit4.ComposeContentTestRule
import com.qeetgroup.qeetrix.theme.QeetrixTheme
import com.qeetgroup.qeetrix.theme.QeetrixThemeMode

/** Sets [content] inside a light [QeetrixTheme]. */
internal fun ComposeContentTestRule.setQeetrix(
    mode: QeetrixThemeMode = QeetrixThemeMode.Light,
    content: @Composable () -> Unit,
) {
    setContent { QeetrixTheme(mode) { content() } }
}
