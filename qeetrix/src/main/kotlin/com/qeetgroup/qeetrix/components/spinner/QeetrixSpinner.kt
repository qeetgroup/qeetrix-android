package com.qeetgroup.qeetrix.components.spinner

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.progressSemantics
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import com.qeetgroup.qeetrix.R
import com.qeetgroup.qeetrix.icons.QeetrixIcon
import com.qeetgroup.qeetrix.icons.QeetrixIconName
import com.qeetgroup.qeetrix.internal.QeetrixWebScale
import com.qeetgroup.qeetrix.internal.rememberSpin
import com.qeetgroup.qeetrix.theme.QeetrixTheme

/** The sizes of [QeetrixSpinner] (`value` is the web name). */
public enum class QeetrixSpinnerSize(
    public val value: String,
    internal val side: Dp,
) {
    /** 16 dp (`size-4`). */
    Sm("sm", QeetrixWebScale.spacing(4f)),

    /** 20 dp (`size-5`). */
    Default("default", QeetrixWebScale.spacing(5f)),

    /** 24 dp (`size-6`). */
    Lg("lg", QeetrixWebScale.spacing(6f)),

    /** 32 dp (`size-8`). */
    Xl("xl", QeetrixWebScale.spacing(8f)),
}

/**
 * An indeterminate busy indicator: the `loader-circle` icon turning once a second in the muted
 * foreground colour.
 *
 * Accessibility services read it as an in-progress indicator named [label] — "Loading" by default,
 * from the `qx_loading` string, which an app translates by overriding the resource. When the user has
 * turned animations off it stands still, as the web's reduced-motion rule stops it.
 *
 * Inside a button, use the button's own `loading` state instead.
 */
@Composable
public fun QeetrixSpinner(
    modifier: Modifier = Modifier,
    size: QeetrixSpinnerSize = QeetrixSpinnerSize.Default,
    label: String? = null,
) {
    val name = label ?: stringResource(R.string.qx_loading)
    val angle by rememberSpin()
    Box(
        modifier
            .progressSemantics()
            .semantics { contentDescription = name }
            .graphicsLayer { rotationZ = angle },
    ) {
        // LoaderCircleIcon, animate-spin, text-muted-foreground
        QeetrixIcon(QeetrixIconName.LoaderCircle, contentDescription = null, size = size.side, tint = QeetrixTheme.colors.textTertiary)
    }
}
