package com.qeetgroup.qeetrix.foundation.color

import com.qeetgroup.qeetrix.tokens.generated.QeetrixOpacityTokens

/** Opacities (`opacity.*`), 0–1. */
public object QeetrixOpacity {
    /**
     * `opacity.disabled` — for disabled content that has no disabled colour, such as an image. Disabled
     * controls use the disabled colours (`bgDisabled`, `textDisabled`, `borderDisabled`) instead.
     */
    public val Disabled: Float = QeetrixOpacityTokens.Disabled
}
