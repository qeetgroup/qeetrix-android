package com.qeetgroup.qeetrix.components.spinner

import androidx.compose.foundation.progressSemantics
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.takeOrElse
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.qeetgroup.qeetrix.R
import com.qeetgroup.qeetrix.internal.QeetrixSpinnerRing
import com.qeetgroup.qeetrix.theme.LocalQeetrixContentColor
import com.qeetgroup.qeetrix.theme.QeetrixTheme

/** The sizes of [QeetrixSpinner] (`value` is `@qeetrix/react`'s `size`). */
public enum class QeetrixSpinnerSize(
    public val value: String,
    internal val side: Dp,
) {
    /** 14 dp. */
    Sm("sm", 14.dp),

    /** 16 dp — the default. */
    Md("md", 16.dp),

    /** 20 dp. */
    Lg("lg", 20.dp),

    /** 24 dp. */
    Xl("xl", 24.dp),
}

/** The colours of [QeetrixSpinner] (`value` is `@qeetrix/react`'s `tone`). */
public enum class QeetrixSpinnerTone(
    public val value: String,
) {
    /** The inherited content colour, as the web's `currentColor`. The default. */
    Neutral("neutral"),

    /** Ember (`color.icon.brand`). */
    Brand("brand"),
}

/**
 * An indeterminate wait: a 2 dp ring with a quarter clear, turning every 800 ms — `@qeetrix/react`'s
 * Spinner. When the user has turned animations off, the ring is whole and fades instead, as on the web.
 *
 * Accessibility services read it as an in-progress indicator named [label] — "Loading" by default, from
 * the `qx_loading` string, which an app translates by overriding the resource. With [decorative], it is
 * hidden from them, for a wait something else already announces.
 *
 * Inside a button, use the button's own `loading` state instead.
 */
@Composable
public fun QeetrixSpinner(
    modifier: Modifier = Modifier,
    size: QeetrixSpinnerSize = QeetrixSpinnerSize.Md,
    tone: QeetrixSpinnerTone = QeetrixSpinnerTone.Neutral,
    label: String? = null,
    decorative: Boolean = false,
) {
    val color =
        when (tone) {
            QeetrixSpinnerTone.Neutral -> LocalQeetrixContentColor.current.takeOrElse { QeetrixTheme.colors.textDefault }
            QeetrixSpinnerTone.Brand -> QeetrixTheme.colors.iconBrand // [data-tone="brand"]
        }
    val name = label ?: stringResource(R.string.qx_loading)
    val semantics = if (decorative) Modifier else Modifier.progressSemantics().semantics { contentDescription = name }
    QeetrixSpinnerRing(size.side, color, modifier.then(semantics))
}
