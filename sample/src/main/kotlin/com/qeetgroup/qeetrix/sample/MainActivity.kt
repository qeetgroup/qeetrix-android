package com.qeetgroup.qeetrix.sample

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        // adb shell am start -n com.qeetgroup.qeetrix.sample/.MainActivity --es route otp-code [--ez rtl true]: open one
        // page, optionally right to left.
        setContent {
            QeetrixSampleApp(startRoute = intent.getStringExtra("route"), startRightToLeft = intent.getBooleanExtra("rtl", false))
        }
    }
}
