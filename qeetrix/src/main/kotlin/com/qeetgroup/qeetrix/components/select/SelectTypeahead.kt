package com.qeetgroup.qeetrix.components.select

import android.os.SystemClock
import androidx.compose.ui.input.key.KeyEvent
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.isAltPressed
import androidx.compose.ui.input.key.isCtrlPressed
import androidx.compose.ui.input.key.isMetaPressed
import androidx.compose.ui.input.key.type
import androidx.compose.ui.input.key.utf16CodePoint
import com.qeetgroup.qeetrix.components.input.QeetrixOption

/**
 * Type-to-jump (`@qeetrix/react` Select, 06 §4): characters typed on a hardware keyboard within [TIMEOUT] of each other
 * build a prefix, and the next enabled option whose label starts with it is the match. The same letter typed again
 * moves on to the next option starting with it, as a native list does.
 */
internal class SelectTypeahead(
    private val now: () -> Long = { SystemClock.uptimeMillis() },
) {
    private var typed = ""
    private var last = Long.MIN_VALUE

    /**
     * The option [character] leads to, from the option at [current] (−1 for none), or `null` when none matches; the
     * typed prefix starts again after [TIMEOUT].
     */
    fun <T> next(
        character: Char,
        options: List<QeetrixOption<T>>,
        current: Int,
    ): QeetrixOption<T>? {
        val time = now()
        typed = if (last == Long.MIN_VALUE || time - last > TIMEOUT) "$character" else typed + character
        last = time
        // "aaa" is "a" three times: each moves on. A longer prefix may still match the current option, so it stays.
        val repeated = typed.length > 1 && typed.all { it.equals(typed[0], ignoreCase = true) }
        val prefix = if (repeated) typed.take(1) else typed
        val from = if (prefix.length == 1) current + 1 else current.coerceAtLeast(0)
        if (options.isEmpty()) return null
        for (step in options.indices) {
            val option = options[(from + step).mod(options.size)]
            if (option.enabled && option.label.trimStart().startsWith(prefix, ignoreCase = true)) return option
        }
        return null
    }

    companion object {
        /** How long, in milliseconds, a typed prefix waits for its next character: one second, as native lists do. */
        const val TIMEOUT = 1_000L
    }
}

/** The character a key press types for type-to-jump: a letter, digit or symbol without Ctrl, Alt or Meta, or `null`. */
internal fun KeyEvent.typeaheadCharacter(): Char? {
    if (type != KeyEventType.KeyDown || isCtrlPressed || isAltPressed || isMetaPressed) return null
    val code = utf16CodePoint
    if (code <= 0 || code > Char.MAX_VALUE.code) return null
    val character = code.toChar()
    return character.takeIf { !it.isWhitespace() && !it.isISOControl() }
}
