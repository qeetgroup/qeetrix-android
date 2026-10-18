package com.qeetgroup.qeetrix.sample

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import com.qeetgroup.qeetrix.sample.screens.FoundationsScreen
import com.qeetgroup.qeetrix.theme.QeetrixTheme
import com.qeetgroup.qeetrix.theme.QeetrixThemeMode

/**
 * The sample's root. The chosen mode is stored as its upstream string (`light`, `dark`, `system`),
 * the same value a web or iOS app stores, and survives configuration changes and process death.
 */
@Composable
fun QeetrixSampleApp() {
    var modeValue by rememberSaveable { mutableStateOf(QeetrixThemeMode.System.value) }
    val mode = QeetrixThemeMode.fromValue(modeValue) ?: QeetrixThemeMode.System
    QeetrixTheme(mode = mode) {
        FoundationsScreen(mode = mode, onModeChange = { modeValue = it.value })
    }
}
