package com.qeetgroup.qeetrix.iconbudget

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import com.qeetgroup.qeetrix.icons.QeetrixIcon
import com.qeetgroup.qeetrix.theme.QeetrixTheme

/** Draws [budgetIcons], the flavour's icons, so R8 keeps exactly those. */
class BudgetActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            QeetrixTheme {
                Column {
                    for (icon in budgetIcons) QeetrixIcon(icon, contentDescription = icon.value)
                }
            }
        }
    }
}
