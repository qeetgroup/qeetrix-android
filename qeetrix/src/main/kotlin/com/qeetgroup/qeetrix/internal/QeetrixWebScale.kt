package com.qeetgroup.qeetrix.internal

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * The Tailwind CSS default-theme values qeetrix-ui components use through utility classes — `h-7`,
 * `px-2.5`, `text-sm`, `leading-snug`, `animate-spin` — rather than through Qeetrix tokens.
 *
 * They are not Qeetrix tokens, so the generator cannot read them; they are restated here, once, from
 * `tailwindcss` 4.3.3 `theme.css`, the version in qeetrix-ui's `bun.lock` at the pinned commit
 * `7d7f651` (qeetrix-ios restates the same values). Component code writes the web class beside each
 * use — `QeetrixWebScale.spacing(2.5f) // px-2.5` — so it can be compared with the web source line by
 * line. Documentation/Decisions/0011-component-tokens-and-web-metrics.md records why.
 */
internal object QeetrixWebScale {
    /** `--spacing: 0.25rem` — one step of the spacing scale, 4 dp (1 rem = 16 dp). */
    private const val SPACING_STEP = 4f

    /** A spacing-scale value: `spacing(2.5f)` is `px-2.5`, `spacing(7f)` is `h-7`. */
    fun spacing(steps: Float): Dp = (steps * SPACING_STEP).dp

    /** A step of the type ramp: a size in sp and its paired line height, as a multiple of the size. */
    class TextStep(
        val size: Float,
        val lineHeight: Float,
    )

    /** `--text-xs: 0.75rem`, `--text-xs--line-height: calc(1 / 0.75)`. */
    val TextXs = TextStep(12f, 1f / 0.75f)

    /** `--text-sm: 0.875rem`, `--text-sm--line-height: calc(1.25 / 0.875)`. */
    val TextSm = TextStep(14f, 1.25f / 0.875f)

    /** `--text-base: 1rem`, `--text-base--line-height: calc(1.5 / 1)`. */
    val TextBase = TextStep(16f, 1.5f)

    /** `--text-lg: 1.125rem`, `--text-lg--line-height: calc(1.75 / 1.125)`. */
    val TextLg = TextStep(18f, 1.75f / 1.125f)

    /** `--text-xl: 1.25rem`, `--text-xl--line-height: calc(1.75 / 1.25)`. */
    val TextXl = TextStep(20f, 1.75f / 1.25f)

    /** `--text-2xl: 1.5rem`, `--text-2xl--line-height: calc(2 / 1.5)`. */
    val Text2xl = TextStep(24f, 2f / 1.5f)

    /** `--leading-normal: 1.5`. */
    const val LEADING_NORMAL = 1.5f

    /** `--leading-snug: 1.375`. */
    const val LEADING_SNUG = 1.375f

    /** `--leading-relaxed: 1.625`. */
    const val LEADING_RELAXED = 1.625f

    /** `--animate-spin: spin 1s linear infinite`. */
    const val SPIN_MILLIS = 1000

    /** The `sm:` breakpoint, `40rem` (640 CSS px, 640 dp). */
    val BreakpointSm: Dp = 640.dp

    /** The `md:` breakpoint, `48rem` (768 CSS px, 768 dp). */
    val BreakpointMd: Dp = 768.dp
}
