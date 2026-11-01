package com.qeetgroup.qeetrix.foundation.typography

import com.qeetgroup.qeetrix.internal.QeetrixTextRoleSpec
import com.qeetgroup.qeetrix.tokens.generated.QeetrixTypographyTokens

/**
 * The Qeetrix typography roles (`typography.<role>.<size>`), one entry per token, named by its path
 * (`typography.body.md` → [BodyMd]). Read a role's style from `QeetrixTheme.typography[role]` or its
 * property (`QeetrixTheme.typography.bodyMd`).
 *
 * Qeet Sans for everything except code (Fira Code). Each role takes Qeet Sans's letter style from the
 * `GEOM` axis as the web does (design rules §5): display roles Geo ([QeetrixFontGeometry.Display]);
 * heading and title roles Base ([QeetrixFontGeometry.Heading]); body, label and code roles A11y
 * ([QeetrixFontGeometry.Body]).
 *
 * @property value The upstream token path after `typography.` (`body.md`), the same string on every platform.
 */
public enum class QeetrixTextRole(
    public val value: String,
) {
    /** `typography.display.lg` — Hero headlines, on welcoming screens only (design rules §17). */
    DisplayLg("display.lg"),

    /** `typography.display.md` */
    DisplayMd("display.md"),

    /** `typography.display.sm` */
    DisplaySm("display.sm"),

    /** `typography.heading.lg` — Page and section headings. */
    HeadingLg("heading.lg"),

    /** `typography.heading.md` */
    HeadingMd("heading.md"),

    /** `typography.heading.sm` — Page titles (design rules §5). */
    HeadingSm("heading.sm"),

    /** `typography.title.lg` — Cards, dialogs and panels. */
    TitleLg("title.lg"),

    /** `typography.title.md` — Dialog titles. */
    TitleMd("title.md"),

    /** `typography.title.sm` — Card titles. */
    TitleSm("title.sm"),

    /** `typography.body.lg` — Running text. */
    BodyLg("body.lg"),

    /** `typography.body.md` — Running text, table cells, descriptions — the interface base. */
    BodyMd("body.md"),

    /** `typography.body.sm` — Help text, errors and other small text. */
    BodySm("body.sm"),

    /** `typography.label.lg` — Interface text: buttons, inputs, tabs, menus. */
    LabelLg("label.lg"),

    /** `typography.label.md` — Buttons, tabs, menu items. */
    LabelMd("label.md"),

    /** `typography.label.sm` — Field labels and status tags. */
    LabelSm("label.sm"),

    /** `typography.label.caps` — The small capitals that label a group (navigation sections, panel sections); the component uppercases it. The only tracked role. */
    LabelCaps("label.caps"),

    /** `typography.code.md` — Code, IDs, keys and shortcuts, in Fira Code. */
    CodeMd("code.md"),

    /** `typography.code.sm` */
    CodeSm("code.sm"),
    ;

    internal val spec: QeetrixTextRoleSpec
        get() =
            when (this) {
                DisplayLg -> QeetrixTypographyTokens.DisplayLg
                DisplayMd -> QeetrixTypographyTokens.DisplayMd
                DisplaySm -> QeetrixTypographyTokens.DisplaySm
                HeadingLg -> QeetrixTypographyTokens.HeadingLg
                HeadingMd -> QeetrixTypographyTokens.HeadingMd
                HeadingSm -> QeetrixTypographyTokens.HeadingSm
                TitleLg -> QeetrixTypographyTokens.TitleLg
                TitleMd -> QeetrixTypographyTokens.TitleMd
                TitleSm -> QeetrixTypographyTokens.TitleSm
                BodyLg -> QeetrixTypographyTokens.BodyLg
                BodyMd -> QeetrixTypographyTokens.BodyMd
                BodySm -> QeetrixTypographyTokens.BodySm
                LabelLg -> QeetrixTypographyTokens.LabelLg
                LabelMd -> QeetrixTypographyTokens.LabelMd
                LabelSm -> QeetrixTypographyTokens.LabelSm
                LabelCaps -> QeetrixTypographyTokens.LabelCaps
                CodeMd -> QeetrixTypographyTokens.CodeMd
                CodeSm -> QeetrixTypographyTokens.CodeSm
            }
}
