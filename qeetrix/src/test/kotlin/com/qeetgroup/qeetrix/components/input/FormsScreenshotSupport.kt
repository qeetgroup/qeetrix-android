package com.qeetgroup.qeetrix.components.input

import androidx.compose.foundation.interaction.FocusInteraction
import androidx.compose.foundation.interaction.HoverInteraction
import androidx.compose.foundation.interaction.Interaction
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow

/**
 * An interaction source that reports keyboard focus, so a screenshot can show a control's focus frame without a
 * keyboard (real focus and presses need input the screenshot tests do not send).
 */
@Composable
internal fun rememberFocusedSource(): MutableInteractionSource = remember { HeldInteractionSource(FocusInteraction.Focus()) }

/** An interaction source that reports a hovering pointer, so a screenshot can show a control's hover step. */
@Composable
internal fun rememberHoveredSource(): MutableInteractionSource = remember { HeldInteractionSource(HoverInteraction.Enter()) }

/** Replays [held] to every collector, so a component that starts collecting after composition still sees it. */
private class HeldInteractionSource(
    held: Interaction,
) : MutableInteractionSource {
    private val flow = MutableSharedFlow<Interaction>(replay = 1).apply { tryEmit(held) }

    override val interactions: Flow<Interaction> = flow

    override suspend fun emit(interaction: Interaction) = flow.emit(interaction)

    override fun tryEmit(interaction: Interaction): Boolean = flow.tryEmit(interaction)
}
