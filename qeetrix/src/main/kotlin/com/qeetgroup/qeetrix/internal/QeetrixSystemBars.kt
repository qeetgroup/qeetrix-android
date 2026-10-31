package com.qeetgroup.qeetrix.internal

import android.os.Build
import android.view.View
import android.view.Window
import android.view.WindowInsetsController
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.window.DialogWindowProvider

/**
 * The status and navigation bars' icons for the dialog window this is composed in: dark over a light surface
 * ([darkIcons]), light over a dark one or the scrim in dark mode. A dialog's own window drawn edge to edge doesn't take
 * the activity's setting, so a full-screen form on a white surface had white icons — an invisible clock and battery.
 */
@Composable
internal fun DialogSystemBars(darkIcons: Boolean) {
    val view = LocalView.current
    SideEffect { (view.parent as? DialogWindowProvider)?.window?.setDarkSystemBarIcons(darkIcons) }
}

internal fun Window.setDarkSystemBarIcons(dark: Boolean) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        val both = WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS or WindowInsetsController.APPEARANCE_LIGHT_NAVIGATION_BARS
        insetsController?.setSystemBarsAppearance(if (dark) both else 0, both)
    } else {
        @Suppress("DEPRECATION")
        val both = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR or View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
        @Suppress("DEPRECATION")
        decorView.systemUiVisibility = if (dark) decorView.systemUiVisibility or both else decorView.systemUiVisibility and both.inv()
    }
}
