# Localization and right-to-left report (Phase 6)

What was tested for languages, scripts, number formats and right-to-left layouts, what was found and fixed, and
what remains.

## Ownership

Qeetrix owns its own 21 built-in strings (`qx_*`); apps own everything they pass in — titles, labels, messages,
dates and amounts — and its translation. Qeetrix ships **English only**. No translation exists and none was
written: a translation needs a linguist's review, and inventing one would be worse than an honest gap.

## Library strings

| Check | Result |
|:--|:--|
| Hard-coded user-facing text in `qeetrix/src/main` | None. Every string a component shows or announces is a `qx_` resource; literals in the sources are KDoc examples and `toString()` |
| Resource prefix | All resources `qx_` (`resourcePrefix`), so an app's own names cannot collide |
| Overridable | Every built-in string is a resource an app overrides to translate, and each component that shows one also takes a parameter that replaces it |
| Plurals | None needed: no library string contains a count. Counts in the sample's screens ("2 people removed") are the app's |
| Format arguments | One: `qx_remove` ("Remove %1$s"), so a translation can move the chip's name |
| Pseudolocales | The sample's debug build now enables en-XA and ar-XB (`isPseudoLocalesEnabled`), so the library's strings can be read accented, expanded and right to left on a device |

## Locales, scripts and expansion tested

| Locale or fixture | Where | What | Result |
|:--|:--|:--|:--|
| English (US) | JVM, device | Every test | Baseline |
| Hindi (Devanagari, incl. Devanagari digits) | JVM screenshots, device ATF and capture | Buttons, rows, fields, navigation | Renders through the platform's fallback fonts; no missing glyphs; no clipping |
| Telugu | JVM screenshots, device ATF and capture | Empty state, description list, chip | As Hindi |
| Arabic (right to left) | JVM screenshots, device ATF and capture | Fields, snackbar, timeline, tabs | Right-to-left paragraphs, mirrored layouts |
| Pseudo-expanded English (accented, ~140 %) | JVM layout scans, screenshots, device ATF and capture | Buttons, tabs, chips, snackbar, rows, accordion, switch, form actions | Wraps or stacks; one defect in right-to-left layouts (below), fixed |
| German, French | JVM and device number tests | Decimal comma, narrow-space grouping | Read and written correctly |
| hi-IN, te-IN, en-IN | **Device** number tests (Android's ICU) | Indian grouping (1,23,456.5), Devanagari digits typed into a number field | Read and written correctly |
| ar-EG, ar-SA | **Device** number tests | Arabic-Indic digits and separators | Every value written in the locale reads back to itself |

Number formatting was tested on a device deliberately: the JVM tests use the JDK's `java.text`, which has no
Indian grouping, while Android formats with ICU.

## Fonts

Qeet Sans is Latin-only by design ([0007](../Decisions/0007-fonts-are-the-webs-fonts-in-native-format.md)).
Devanagari, Telugu and Arabic fall back to the platform's fonts glyph by glyph; the screenshots and the device
capture show complete glyphs, and the clipping scan found no text cut by line height in those scripts at 100 % or
200 %. Bold Text and the system's largest sizes on real fonts remain to be checked on a physical device.

## Findings and fixes

| # | Severity | Finding | Fix | Evidence |
|:--|:--|:--|:--|:--|
| L1 | P2 | **Number field changes a value on a change of locale.** Text the field wrote in one locale ("1,234" in English) was read again in the next ("1.234" in German) when the locale changed or an activity was recreated for one, and the new value reported to the app. | The field rewrites text another locale wrote instead of reading it (`NumberText.reads`). | `QeetrixNumberFieldTest`: `aChangeOfLocaleRewritesTheTextAndKeepsTheValue`, `restoredTextInAnotherLocaleIsWrittenAgainNotReadAgain` (both failed before the fix) |
| L2 | P2 | **Left-to-right labels cut in right-to-left layouts.** Compose lays every text out in the layout's direction, so an English label in an Arabic screen is a right-to-left paragraph, and Android's text measurement comes up a few pixels short for some ("[Çàñçél o]", "[Övérvîéw on]"): a full-width button's label ended in an ellipsis, chips lost their first characters, a tab broke onto two lines. The same direction put trailing punctuation and numbers at the wrong end of ordinary English labels: a loading button read "…Saving", a timeline time "days ago 2". Reproduced with plain `BasicText` on the JVM and on an API 36 device. | Short labels (button, chip, tab, badge, navigation, menu, field labels, row metadata, timeline time) take their paragraph direction from their text (`TextDirection.Content`, `labelDirection()`), as a `TextView` does. A label sized to its text needs nothing more. A menu item, and a timeline's time once it moves below its title at large text, are wider than their text: they also pin their alignment to the layout's start, so English and Arabic lines start at the same edge. A first version pinned the alignment of every label and was narrowed: with right alignment and no wrapping, Android reports a line as wide as its box, which the clipping scan rightly could not tell from a cut line. | `RightToLeftLabelTest`; the localization screenshots and the device capture, reviewed before and after |
| L3 | P3 | **Punctuation of left-to-right paragraphs in right-to-left layouts.** A full-width English sentence in an Arabic screen shows its full stop at the start ("workspace." reads ".workspace") — Compose's default, in the layout's direction. Not changed: paragraph text must align with its right-to-left neighbours, and the label fix's content direction would align it left. | Documented. An app that shows an English paragraph in a right-to-left screen sets `TextDirection.Content` on its own text. | Screenshots |
| L4 | P3 | A test typed the company's real mail domain | Synthetic address | — |

## Right-to-left layouts

Every component has a right-to-left screenshot baseline (Phases 2–5), reviewed again in Phase 6 after L2. Layouts
use start and end throughout; the only absolute left/right arithmetic in the library is popup placement, which
handles right to left explicitly. Directional icons (back, send) mirror; the brand, numbers and non-directional
icons don't; the OTP cells keep their left-to-right order, as codes are read that way.

## Not done

- No translation of the 21 built-in strings, and no linguistic review.
- No physical-device check of Indic glyph rendering with Bold Text, or of a whole app running in ar-XB or an
  Arabic system locale (the sample enables the pseudolocales for that check).
- Date and time formatting is the app's: no Qeetrix component formats dates.
