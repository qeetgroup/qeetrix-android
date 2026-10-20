# Module qeetrix

Qeetrix for Android: the Qeet Group design system in Kotlin and Jetpack Compose. The foundations —
semantic colours, typography, spacing, corners, sizing, elevation, motion, icons and accessibility
helpers — are generated from the published `@qeetrix/ui` tokens, and the components in
`com.qeetgroup.qeetrix.components.*` are built on them: Button, Icon button, Text field, Switch, Avatar,
Badge, Text, Progress, Spinner, Card and Separator; the form inputs — Field and field set, Textarea,
Password input, OTP input, Checkbox, Radio group, Select, Number field — with the form patterns; and
navigation, overlays and feedback — the navigation bar, rail and adaptive scaffold, the top app bar, tabs,
alert, confirmation and custom dialogs, the bottom sheet, the menu and the snackbar; and data display,
collections and content states — the list item and section header, the description list, the accordion,
the empty and data states, the skeleton, chips, the timeline and the search field.

Wrap your content in [com.qeetgroup.qeetrix.theme.QeetrixTheme] and read the theme's values from the
`QeetrixTheme` object:

```kotlin
QeetrixTheme {
    BasicText(
        "Account",
        style = QeetrixTheme.typography.title.copy(color = QeetrixTheme.colors.textPrimary),
    )
}
```

Every value comes from the upstream tokens; there is no public way to define a colour, size or font of
your own. Token names are the web's (`color.text.on-brand` → `textOnBrand`), so a design reference on
ui.qeet.in maps directly to a Kotlin name.

# Package com.qeetgroup.qeetrix.theme

The theme: [com.qeetgroup.qeetrix.theme.QeetrixTheme] scopes the light or dark colours, typography,
content colour, text-selection colours and the reduced-motion setting over its content.

- **Modes.** `System` follows the device's dark-theme setting live; `Light` and `Dark` force a theme.
  The [com.qeetgroup.qeetrix.theme.QeetrixThemeMode] values (`light`, `dark`, `system`) are the web's
  and iOS's, so a stored preference reads the same everywhere.
- **Nested themes.** A `QeetrixTheme` inside another changes only its subtree's colours — a dark panel
  on a light screen, as the web's nested `.dark` scope does.
- **Content colour.** [com.qeetgroup.qeetrix.theme.LocalQeetrixContentColor] is what text and icons
  inherit, like `currentColor` on the web. The theme sets it to `textPrimary`.

The theme does not style the status and navigation bars; that is the activity's job
(`enableEdgeToEdge`).

# Package com.qeetgroup.qeetrix.foundation.color

[com.qeetgroup.qeetrix.foundation.color.QeetrixColors] holds every semantic colour of one theme. Read the
current theme's with `QeetrixTheme.colors`.

Use the role, not the look: `surfaceDefault` for a card, `textSecondary` for supporting text,
`borderControl` for a field's outline, `feedbackError` for an error fill with `textOnFeedback` on it.
The pairs qeetrix-ui designs together meet WCAG 2.2 AA in both themes; this library's tests measure
them on the Android values.

Palette primitives (`color.qeet.500` and so on) are deliberately not available, as on the web.
Colours are converted from OKLCH per CSS Color 4; the few outside sRGB are Display P3 colours, drawn
in P3 in an activity with `android:colorMode="wideColorGamut"` on a wide-gamut screen and converted to
sRGB elsewhere.

# Package com.qeetgroup.qeetrix.foundation.typography

Nine roles — `display`, `heading`, `title`, `body`, `label`, `label-compact`, `caption`, `micro` and
`code` — as Compose `TextStyle`s in [com.qeetgroup.qeetrix.foundation.typography.QeetrixTypography].

- **Fonts.** Qeet Sans (Cal Sans v2.011, the web's font) and Fira Code 6.2, bundled as variable TTFs.
  Each role sets the axes the web sets: weight (`wght`), letter style (`GEOM`, see
  [com.qeetgroup.qeetrix.foundation.typography.QeetrixFontGeometry]) and optical size (`opsz`, from
  the rendered size).
- **Scaling.** Sizes are in sp and follow the user's font size, including Android 14's non-linear
  scaling of large text. Line heights are em multiples with the extra space split above and below
  each line, as CSS does, so text keeps its proportions at every scale.
- **Bold Text.** Android's Bold Text setting raises the weight; every weight of the scale is bundled,
  so the heavier instance is real rather than synthesised.
- **Other scripts.** The Qeet fonts are Latin-only. Devanagari, Tamil, Bengali and other scripts fall
  back to the system's fonts character by character; give such text enough line height.

# Package com.qeetgroup.qeetrix.foundation.spacing

[com.qeetgroup.qeetrix.foundation.spacing.QeetrixSpacing] is the spacing scale in dp — one CSS px is
one dp — from `Xxs` (2) to `Xl5` (96). Steps whose upstream name starts with a digit move the digit to
the end: `space.2xl` is `Xl2`.

# Package com.qeetgroup.qeetrix.foundation.shape

[com.qeetgroup.qeetrix.foundation.shape.QeetrixCorner] holds the corner radii in dp, and
[com.qeetgroup.qeetrix.foundation.shape.QeetrixShapes] the same values as Compose shapes. Use the role
that matches the element — `Control` for buttons, `Field` for inputs, `Chip`, `Surface` for cards,
`Overlay` for menus and dialogs — rather than a raw step.

# Package com.qeetgroup.qeetrix.foundation.sizing

Icon sizes and stroke widths, border widths, and the control metrics of the three densities
(`default`, `comfortable`, `compact`). Density heights are drawn sizes: a control still needs a 48 dp
touch target ([com.qeetgroup.qeetrix.foundation.accessibility.qeetrixMinimumTouchTarget]).

# Package com.qeetgroup.qeetrix.foundation.elevation

[com.qeetgroup.qeetrix.foundation.elevation.QeetrixElevation] lists the web's elevations, and
[com.qeetgroup.qeetrix.foundation.elevation.qeetrixElevation] draws one behind an element the way CSS
draws `box-shadow` — offset, spread and blur per layer, outer layers outside the shape and inset layers
inside — in the current theme's shadow colours.

# Package com.qeetgroup.qeetrix.foundation.motion

Durations in milliseconds ([com.qeetgroup.qeetrix.foundation.motion.QeetrixDuration]) and easing curves
([com.qeetgroup.qeetrix.foundation.motion.QeetrixEasing]). Build animation specs with
[com.qeetgroup.qeetrix.foundation.motion.qeetrixAnimationSpec]: when the user has turned animations off
(Settings › Accessibility › Remove animations), it uses the `motion.reduced.*` tokens instead, as the web
does for `prefers-reduced-motion`.

# Package com.qeetgroup.qeetrix.foundation.accessibility

Platform accessibility rules that are not tokens: the 48 dp minimum touch target, the WCAG contrast
ratio Qeetrix's tests use, and the focus-indicator geometry ([com.qeetgroup.qeetrix.foundation.accessibility.QeetrixFocus]).
See `Documentation/Standards/ACCESSIBILITY.md` in the repository for the full standard, including what
needs testing on a device.

# Package com.qeetgroup.qeetrix.icons

[com.qeetgroup.qeetrix.icons.QeetrixIcon] draws a Qeetrix icon from vector paths in the inherited content
colour. The library carries the icons its components use ([com.qeetgroup.qeetrix.icons.QeetrixIconName]) in
round and sharp shapes, outline and filled where qeetrix-icons draws them.

Pass a `contentDescription` when the icon means something on its own; pass `null` when it is
decorative and the text beside it carries the meaning. Icons whose meaning has a direction, such as
`send`, mirror in right-to-left layouts.

# Package com.qeetgroup.qeetrix.tokens

[com.qeetgroup.qeetrix.tokens.QeetrixProvenance] names the exact upstream releases this build was
generated from — useful on an about screen or in a bug report, and for checking that web, iOS and
Android ship the same design-system release.

# Package com.qeetgroup.qeetrix.components.button

Actions. [com.qeetgroup.qeetrix.components.button.QeetrixButton] is the Qeet action control — one
default (primary) button per view, with outline, secondary, ghost, destructive and link variants, four
sizes, and a loading state that keeps focus and ignores taps.
[com.qeetgroup.qeetrix.components.button.QeetrixIconButton] is its icon-only form and requires a
`contentDescription` that names the action. Every size reserves a 48 dp touch target.

# Package com.qeetgroup.qeetrix.components.input

The field every Qeetrix input is built on, and the text inputs.
[com.qeetgroup.qeetrix.components.input.QeetrixTextField] is a single-line field and
[com.qeetgroup.qeetrix.components.input.QeetrixTextarea] a multi-line one, each with its label, help text
and a [com.qeetgroup.qeetrix.components.input.QeetrixFieldMessage] (error, warning or success), built on
Compose's `BasicTextField` and a caller-owned `TextFieldState`.
[com.qeetgroup.qeetrix.components.input.QeetrixField] gives any other control the same label,
description, message and accessibility; [com.qeetgroup.qeetrix.components.input.QeetrixFieldSet] groups
fields or choices under a legend. [com.qeetgroup.qeetrix.components.input.QeetrixOption] is a typed
choice for the radio group, the select and the chip group.
[com.qeetgroup.qeetrix.components.input.QeetrixSearchField] is a field for a search query, with a clear
button and the keyboard's search key; the filtering is the app's.

# Package com.qeetgroup.qeetrix.components.passwordinput

[com.qeetgroup.qeetrix.components.passwordinput.QeetrixPasswordInput]: a password field on Compose's
`BasicSecureTextField`, with a reveal button and AutoFill for a current or a new password. The password
stays in the caller's `TextFieldState`; Qeetrix keeps, logs and describes none of it.

# Package com.qeetgroup.qeetrix.components.otpinput

[com.qeetgroup.qeetrix.components.otpinput.QeetrixOTPInput]: a one-time code in one native field, drawn
as cells, with SMS one-time-code AutoFill. It never reads messages or asks for permissions.

# Package com.qeetgroup.qeetrix.components.checkbox

[com.qeetgroup.qeetrix.components.checkbox.QeetrixCheckbox] and
[com.qeetgroup.qeetrix.components.checkbox.QeetrixTriStateCheckbox]: checked, unchecked and mixed, as a
labelled row that is one 48 dp control or as a bare box.

# Package com.qeetgroup.qeetrix.components.radiogroup

[com.qeetgroup.qeetrix.components.radiogroup.QeetrixRadioGroup]: one typed choice from a short, visible
list of options.

# Package com.qeetgroup.qeetrix.components.select

[com.qeetgroup.qeetrix.components.select.QeetrixSelect]: one typed choice from a list in a popup, with a
trigger drawn as a field.

# Package com.qeetgroup.qeetrix.components.numberfield

[com.qeetgroup.qeetrix.components.numberfield.QeetrixNumberField]: an exact `Int` or `BigDecimal` with
locale-aware typing, a range, a step and stepper buttons.

# Package com.qeetgroup.qeetrix.components.form

Form patterns: [com.qeetgroup.qeetrix.components.form.QeetrixFormActions] places a form's buttons and
[com.qeetgroup.qeetrix.components.form.QeetrixFormErrorSummary] lists its problems after a failed submit.
Values, validation and submission stay with the app.

# Package com.qeetgroup.qeetrix.components.navigation

App chrome. [com.qeetgroup.qeetrix.components.navigation.QeetrixNavigationBar] and
[com.qeetgroup.qeetrix.components.navigation.QeetrixNavigationRail] present an app's top-level
destinations ([com.qeetgroup.qeetrix.components.navigation.QeetrixNavigationItem]);
[com.qeetgroup.qeetrix.components.navigation.QeetrixNavigationScaffold] switches between them with the
window width; [com.qeetgroup.qeetrix.components.navigation.QeetrixTopAppBar] heads a screen, with
[com.qeetgroup.qeetrix.components.navigation.QeetrixBackButton]. Selection is the app's: there is no router
or back stack.

# Package com.qeetgroup.qeetrix.components.tabs

[com.qeetgroup.qeetrix.components.tabs.QeetrixTabs]: switches between views of related content within a
screen, in the web's contained and line treatments.

# Package com.qeetgroup.qeetrix.components.dialog

Modal decisions and tasks in Compose's dialog window:
[com.qeetgroup.qeetrix.components.dialog.QeetrixAlertDialog] with typed
[com.qeetgroup.qeetrix.components.dialog.QeetrixDialogAction]s,
[com.qeetgroup.qeetrix.components.dialog.QeetrixConfirmationDialog] for "are you sure?", and
[com.qeetgroup.qeetrix.components.dialog.QeetrixDialog] for content. Actions run once; a destructive choice
always comes with Cancel.

# Package com.qeetgroup.qeetrix.components.drawer

[com.qeetgroup.qeetrix.components.drawer.QeetrixBottomSheet]: the web's Drawer, a modal sheet from the
bottom edge, presented by a flag the app owns.

# Package com.qeetgroup.qeetrix.components.dropdownmenu

[com.qeetgroup.qeetrix.components.dropdownmenu.QeetrixMenu]: a short list of actions opened from a trigger,
with [com.qeetgroup.qeetrix.components.dropdownmenu.QeetrixMenuItem]s, labels and separators.

# Package com.qeetgroup.qeetrix.components.toast

Transient feedback, the web Toast's Android form: [com.qeetgroup.qeetrix.components.toast.QeetrixSnackbar]
shown through a screen's [com.qeetgroup.qeetrix.components.toast.QeetrixSnackbarHost] and
[com.qeetgroup.qeetrix.components.toast.QeetrixSnackbarHostState] — one at a time, with the web's timing.

# Package com.qeetgroup.qeetrix.components.switchcontrol

[com.qeetgroup.qeetrix.components.switchcontrol.QeetrixSwitch]: an on/off control with switch semantics,
alone or as a labelled row that toggles as one control. The package is `switchcontrol` because
`switch` is a Java keyword.

# Package com.qeetgroup.qeetrix.components.avatar

[com.qeetgroup.qeetrix.components.avatar.QeetrixAvatar]: a person's or an organisation's picture
(any `Painter` the app has loaded), their initials, or a neutral icon, with an optional status badge.

# Package com.qeetgroup.qeetrix.components.badge

[com.qeetgroup.qeetrix.components.badge.QeetrixBadge]: a small label for a count, a status or a
category, whose text always carries the meaning. [com.qeetgroup.qeetrix.components.badge.QeetrixChip] and
[com.qeetgroup.qeetrix.components.badge.QeetrixChipGroup]: pills that filter or choose — a toggle, one
value or several from a set, or a removable applied filter.

# Package com.qeetgroup.qeetrix.components.list

Rows of collections, which only the native libraries have:
[com.qeetgroup.qeetrix.components.list.QeetrixListItem] with its
[com.qeetgroup.qeetrix.components.list.QeetrixListItemIcon] tile, and
[com.qeetgroup.qeetrix.components.list.QeetrixSectionHeader] for a group of rows. They go inside Compose's
`LazyColumn`, keyed by the app's ids; the data, its identity and its selection are the app's.

# Package com.qeetgroup.qeetrix.components.descriptionlist

[com.qeetgroup.qeetrix.components.descriptionlist.QeetrixDescriptionList]: labelled details — terms and
values — horizontal, vertical or in a grid; a
[com.qeetgroup.qeetrix.components.descriptionlist.QeetrixDescriptionItem] on its own is a key-value row.

# Package com.qeetgroup.qeetrix.components.accordion

[com.qeetgroup.qeetrix.components.accordion.QeetrixAccordion] and
[com.qeetgroup.qeetrix.components.accordion.QeetrixAccordionItem]: sections whose headers show and hide
their content, with the expansion owned by the app.

# Package com.qeetgroup.qeetrix.components.emptystate

What a surface shows instead of data: [com.qeetgroup.qeetrix.components.emptystate.QeetrixEmptyState] for
an empty collection, a first use, no results, no permission or a failure, and
[com.qeetgroup.qeetrix.components.emptystate.QeetrixDataState] for the loading, failure and empty
branches of loaded data in one place.

# Package com.qeetgroup.qeetrix.components.feed

[com.qeetgroup.qeetrix.components.feed.QeetrixTimeline]: a vertical history of events, with toned
markers, minor events and connectors.

# Package com.qeetgroup.qeetrix.components.typography

[com.qeetgroup.qeetrix.components.typography.QeetrixText]: copy in the web Typography component's
variants — headings h1–h4 (with heading semantics), paragraph, lead, large, small, muted, blockquote and
inline code — over Compose's `BasicText`.

# Package com.qeetgroup.qeetrix.components.progress

[com.qeetgroup.qeetrix.components.progress.QeetrixProgress]: determinate or indeterminate task progress
with Compose's progress semantics and a still form when animations are off.

# Package com.qeetgroup.qeetrix.components.spinner

[com.qeetgroup.qeetrix.components.spinner.QeetrixSpinner]: an indeterminate busy indicator, announced as
"Loading" or a label of your own. [com.qeetgroup.qeetrix.components.spinner.QeetrixSkeleton] and
[com.qeetgroup.qeetrix.components.spinner.QeetrixSkeletonText]: placeholders in the shape of content that
is loading, invisible to accessibility services.

# Package com.qeetgroup.qeetrix.components.card

[com.qeetgroup.qeetrix.components.card.QeetrixCard]: a surface with optional header, content and footer,
static or as one clickable control, in default, outline and elevated styles.

# Package com.qeetgroup.qeetrix.components.separator

[com.qeetgroup.qeetrix.components.separator.QeetrixSeparator]: a decorative 1 dp rule, horizontal or
vertical.
