package com.qeetgroup.qeetrix.foundation.typography

import com.qeetgroup.qeetrix.internal.QeetrixTextRoleSpec
import com.qeetgroup.qeetrix.tokens.generated.QeetrixTypographyTokens

/**
 * The Qeetrix typography roles (`typography.*`). Read a role's style from
 * `QeetrixTheme.typography[role]` or its property (`QeetrixTheme.typography.body`).
 *
 * @property value The upstream role name (`label-compact`), the same string on every platform.
 */
public enum class QeetrixTextRole(
    public val value: String,
) {
    /** Hero and marketing headlines. */
    Display("display"),

    /** Section headings. */
    Heading("heading"),

    /** Page titles. */
    Title("title"),

    /** Paragraphs and long-form text. */
    Body("body"),

    /** Buttons, labels, menus and other interface text. */
    Label("label"),

    /** Interface text in compact controls. */
    LabelCompact("label-compact"),

    /** Secondary and supporting text. */
    Caption("caption"),

    /** The smallest interface text: counters, badges, meta. */
    Micro("micro"),

    /** Code, keys, identifiers and other monospaced content. */
    Code("code"),
    ;

    internal val spec: QeetrixTextRoleSpec
        get() =
            when (this) {
                Display -> QeetrixTypographyTokens.Display
                Heading -> QeetrixTypographyTokens.Heading
                Title -> QeetrixTypographyTokens.Title
                Body -> QeetrixTypographyTokens.Body
                Label -> QeetrixTypographyTokens.Label
                LabelCompact -> QeetrixTypographyTokens.LabelCompact
                Caption -> QeetrixTypographyTokens.Caption
                Micro -> QeetrixTypographyTokens.Micro
                Code -> QeetrixTypographyTokens.Code
            }
}
