package com.qeetgroup.qeetrix.theme

/**
 * How an app chooses between the Qeetrix light and dark themes — the same three modes as the web's
 * `ThemeProvider` and qeetrix-ios, with the same [value]s, so a stored preference reads the same on
 * every platform.
 *
 * @property value `light`, `dark` or `system`.
 */
public enum class QeetrixThemeMode(
    public val value: String,
) {
    Light("light"),
    Dark("dark"),

    /** Follow the device's dark-theme setting, live. */
    System("system"),
    ;

    public companion object {
        /** The mode stored as [value], or null for an unknown string. */
        public fun fromValue(value: String): QeetrixThemeMode? = entries.firstOrNull { it.value == value }
    }
}
