# Module qeetrix

Qeetrix for Android: the Qeet Group design system in Kotlin and Jetpack Compose. The foundations —
semantic colours, typography, spacing, sizes, radii, borders, shadows, motion, breakpoints, icons and
accessibility helpers — are generated from the published `@qeetrix/tokens` and `@qeetrix/icons` packages, and the
components in `com.qeetgroup.qeetrix.components.*` are the Android counterparts of `@qeetrix/react`'s, drawn by its
CSS and design rules: Button and Icon button; the form inputs — Text field, Textarea, Password input, OTP input,
Search field, Number field, Select, Checkbox and checkbox group, Radio group, Switch, Field and field set — and the
Form; navigation, overlays and feedback — the navigation bar, rail and adaptive scaffold, the top app bar, tabs,
alert, confirmation and custom dialogs, the bottom sheet, the menu and the snackbar; and data display, collections
and content states — Text and Heading, Badge, Avatar, Card, Separator, the list item and section header, the
description list, the accordion and collapsible, chips, the timeline, progress and meter, the spinner, the
skeleton, and the empty and data states.

Wrap your content in [com.qeetgroup.qeetrix.theme.QeetrixTheme] and read the theme's values from the
`QeetrixTheme` object:

```kotlin
QeetrixTheme {
    BasicText(
        "Account",
        style = QeetrixTheme.typography.titleMd.copy(color = QeetrixTheme.colors.textDefault),
    )
}
```

Every value comes from the upstream tokens; there is no public way to define a colour, size or font of
your own. Token names are the web's (`color.text.on-brand` → `textOnBrand`, `space.inset.lg` →
`QeetrixSpace.InsetLg`), so a design reference on ui.qeet.in maps directly to a Kotlin name, and a component's
options carry `@qeetrix/react`'s prop names and values (`QeetrixButtonVariant.Soft`, `value = "soft"`).

# Package com.qeetgroup.qeetrix.theme

The theme: [com.qeetgroup.qeetrix.theme.QeetrixTheme] scopes the light or dark colours, typography,
content colour, text-selection colours and the reduced-motion setting over its content.

- **Modes.** `System` follows the device's dark-theme setting live; `Light` and `Dark` force a theme.
  The [com.qeetgroup.qeetrix.theme.QeetrixThemeMode] values (`light`, `dark`, `system`) are the web's
  and iOS's, so a stored preference reads the same everywhere.
- **Nested themes.** A `QeetrixTheme` inside another changes only its subtree's colours and shadows — a dark panel
  on a light screen, as the web's nested `data-qx-color-scheme="dark"` does.
- **Content colour.** [com.qeetgroup.qeetrix.theme.LocalQeetrixContentColor] is what text and icons
  inherit, like `currentColor` on the web. The theme sets it to `textDefault`.
- **Density.** [com.qeetgroup.qeetrix.theme.LocalQeetrixDensity] sets the density of a region — compact, default or
  comfortable, the web's `data-qx-density` — once per region, never per component. `QeetrixTheme.density` reads it.

The theme does not style the status and navigation bars; that is the activity's job
(`enableEdgeToEdge`).

# Package com.qeetgroup.qeetrix.foundation.color

[com.qeetgroup.qeetrix.foundation.color.QeetrixColors] holds every semantic colour of one colour scheme — the 109
`color.*` tokens of `@qeetrix/tokens`. Read the current scheme's with `QeetrixTheme.colors`.

Use the role, not the look: `surfaceRaised` for a card, `textSubtle` for supporting text, `borderStrong` for a
field's bottom line, `bgDangerBold` for a destructive button with `textOnBold` on it. The groups follow the token
paths: `bg*` (fills, with their `-hover` and `-pressed` steps), `text*`, `icon*`, `border*`, `surface*` (the layers
of a screen), `overlay*`, `dataviz*` and `code*`. Ember, the brand orange, appears only where it means something:
the primary action, focus, the current item, checked controls and links. The pairs `@qeetrix/tokens` designs
together meet WCAG 2.2 AA in both schemes; this library's tests measure them on the Android values.

Palette primitives (`primitive.color.*`) are deliberately not available, as on the web. Colours are converted from
OKLCH per CSS Color 4; the few outside sRGB are Display P3 colours, drawn in P3 in an activity with
`android:colorMode="wideColorGamut"` on a wide-gamut screen and converted to sRGB elsewhere.
[com.qeetgroup.qeetrix.foundation.color.QeetrixOpacity] holds `opacity.disabled`, for disabled content that has no
disabled colour.

# Package com.qeetgroup.qeetrix.foundation.typography

Eighteen roles — `display.lg`, `display.md`, `display.sm`, `heading.lg`, `heading.md`, `heading.sm`, `title.lg`,
`title.md`, `title.sm`, `body.lg`, `body.md`, `body.sm`, `label.lg`, `label.md`, `label.sm`, `label.caps`, `code.md`
and `code.sm` ([com.qeetgroup.qeetrix.foundation.typography.QeetrixTextRole]) — as Compose `TextStyle`s in
[com.qeetgroup.qeetrix.foundation.typography.QeetrixTypography].

- **Fonts.** Qeet Sans (the Cal Sans 2.011 file) and Fira Code, the TrueType files `@qeetrix/tokens` ships for
  native platforms. Each role sets the axes the web sets: weight (`wght`), letter style (`GEOM`, see
  [com.qeetgroup.qeetrix.foundation.typography.QeetrixFontGeometry]: Geo for display, Base for headings and titles,
  A11y for body, label and code) and optical size (`opsz`, from the rendered size).
- **Scaling.** Sizes are in sp and follow the user's font size, including Android 14's non-linear
  scaling of large text. Line heights are em multiples with the extra space split above and below
  each line, as CSS does, so text keeps its proportions at every scale.
- **Case.** Qeetrix has no uppercase text and no tracking except `label.caps`, the group label.
- **Bold Text.** Android's Bold Text setting raises the weight; every role weight is bundled,
  so the heavier instance is real rather than synthesised.
- **Other scripts.** The Qeet fonts are Latin-only. Devanagari, Tamil, Bengali and other scripts fall
  back to the system's fonts character by character; give such text enough line height.

# Package com.qeetgroup.qeetrix.foundation.spacing

[com.qeetgroup.qeetrix.foundation.spacing.QeetrixSpace] is the spacing scale in dp, on the 4 dp grid — one CSS px
is one dp: `Inset*` inside containers, `Gap*` between siblings, `Layout*` between the sections of a screen
(`space.inset.lg` → `InsetLg`, 16 dp). The space between form fields, `space.form.gap`, follows the density:
`QeetrixTheme.density.spaceFormGap`.

# Package com.qeetgroup.qeetrix.foundation.shape

[com.qeetgroup.qeetrix.foundation.shape.QeetrixRadius] holds the corner radii in dp, and
[com.qeetgroup.qeetrix.foundation.shape.QeetrixShapes] the same values as Compose shapes. Qeetrix is square: 4 dp
on controls and surfaces, 2 dp on small marks; only people and status are round. Use the role that matches the
element — `Control` for buttons, `Field` for a filled field (top corners only), `Surface` for cards, `Overlay` for
menus, `Modal` for dialogs, `Chip` for status tags — rather than a raw step.

# Package com.qeetgroup.qeetrix.foundation.sizing

[com.qeetgroup.qeetrix.foundation.sizing.QeetrixSize] (icon sizes and the smallest pointer target),
[com.qeetgroup.qeetrix.foundation.sizing.QeetrixBorder] (border and rule widths, the 3 dp accent bar),
[com.qeetgroup.qeetrix.foundation.sizing.QeetrixIconStroke], and
[com.qeetgroup.qeetrix.foundation.sizing.QeetrixDensity]: the three densities (`compact`, `default`, `comfortable`)
with their control heights (`sizeControlSm`, `Md`, `Lg`), row height, form gap and icon stroke. Density heights are
drawn sizes: a control still needs a 48 dp touch target
([com.qeetgroup.qeetrix.foundation.accessibility.qeetrixMinimumTouchTarget]).

# Package com.qeetgroup.qeetrix.foundation.elevation

[com.qeetgroup.qeetrix.foundation.elevation.QeetrixShadow] lists the web's shadows, and
[com.qeetgroup.qeetrix.foundation.elevation.qeetrixShadow] draws one behind an element the way CSS draws
`box-shadow` — offset, spread and blur per layer, outer layers outside the shape and inset layers inside — in the
current colour scheme's shadow colours. Only floating layers have shadows: `Overlay` for menus, popovers, the select
list and toasts, `Modal` for dialogs; surfaces in the page flow are separated by tone and 1 dp rules.

# Package com.qeetgroup.qeetrix.foundation.motion

[com.qeetgroup.qeetrix.foundation.motion.QeetrixMotion] holds the durations in milliseconds, the easing curves and
the springs ([com.qeetgroup.qeetrix.foundation.motion.QeetrixSpring]). Build animation specs with
[com.qeetgroup.qeetrix.foundation.motion.qeetrixAnimationSpec]: when the user has turned animations off
(Settings › Accessibility › Remove animations), the change snaps, as every `motion.duration.*` is 0 on the web under
`prefers-reduced-motion`. Motion is productive: quick, never bouncing or scaling.

# Package com.qeetgroup.qeetrix.foundation.layout

[com.qeetgroup.qeetrix.foundation.layout.QeetrixBreakpoint] holds the window-width breakpoints in dp — the lower
bounds of Android's window size classes and the widths of the web's `--qx-viewport-*` media queries. Below `Medium`
(600 dp) a window is compact.

# Package com.qeetgroup.qeetrix.foundation.accessibility

Platform accessibility rules that are not tokens: the 48 dp minimum touch target, the WCAG contrast
ratio Qeetrix's tests use, and the focus frame's geometry ([com.qeetgroup.qeetrix.foundation.accessibility.QeetrixFocus]:
a 2 dp frame in `colors.borderFocus`, inside buttons and fields, `RingOffset` outside selection controls).
See `docs/llm/boundaries.md` in the repository for the full standard, including what
needs testing on a device.

# Package com.qeetgroup.qeetrix.icons

[com.qeetgroup.qeetrix.icons.QeetrixIcon] draws a Qeetrix icon from vector paths in the inherited content
colour. The library carries the `@qeetrix/icons` icons its components use
([com.qeetgroup.qeetrix.icons.QeetrixIconName]) in both shapes — sharp, the default, on controls; round in
navigation ([com.qeetgroup.qeetrix.icons.QeetrixIconShape]) — outline, and filled where `@qeetrix/icons` draws it
([com.qeetgroup.qeetrix.icons.QeetrixIconVariant]), for a current or selected state. Sizes are `QeetrixSize`'s: 16 dp
inside controls, 20 dp standalone (the default), 24 dp in empty states; the stroke follows the density.

Pass a `contentDescription` when the icon means something on its own; pass `null` when it is
decorative and the text beside it carries the meaning. Icons whose meaning has a direction, such as
`send`, mirror in right-to-left layouts.

# Package com.qeetgroup.qeetrix.tokens

[com.qeetgroup.qeetrix.tokens.QeetrixProvenance] names the exact npm packages this build was generated from —
`@qeetrix/tokens`, `@qeetrix/react` and `@qeetrix/icons`, each with its version, git head and integrity — and the
bundled fonts: useful on an about screen or in a bug report, and for checking that web, iOS and Android ship the
same design-system release.

# Package com.qeetgroup.qeetrix.components.button

Actions — `@qeetrix/react`'s Button family. [com.qeetgroup.qeetrix.components.button.QeetrixButton] is the label-first
action, with an optional trailing icon in its own zone: `Solid` (primary, Ember), `Soft` (secondary, graphite) and
`Ghost` (tertiary) variants, `Brand`, `Danger` and `Neutral` tones, three sizes from the density, and a loading
state that keeps focus and ignores taps. [com.qeetgroup.qeetrix.components.button.QeetrixIconButton] is its square,
icon-only form and requires a `contentDescription` that names the action. Every size reserves a 48 dp touch
target.

# Package com.qeetgroup.qeetrix.components.input

The field every Qeetrix input is built on, and the text inputs.
[com.qeetgroup.qeetrix.components.input.QeetrixTextField] is a single-line field and
[com.qeetgroup.qeetrix.components.input.QeetrixTextarea] a multi-line one, each with its label above, the filled box
with its bottom line, and the help text or the error, built on Compose's `BasicTextField` and a caller-owned
`TextFieldState`. [com.qeetgroup.qeetrix.components.input.QeetrixField] gives any other control the same label, help
text and error; [com.qeetgroup.qeetrix.components.input.QeetrixFieldSet] groups fields under a legend.
[com.qeetgroup.qeetrix.components.input.QeetrixSearchField] is a field for a search query, with a clear button, the
keyboard's search key and a typing pause; the filtering is the app's.
[com.qeetgroup.qeetrix.components.input.QeetrixOption] and
[com.qeetgroup.qeetrix.components.input.QeetrixOptionGroup] are the typed choices of the select, the radio group,
the checkbox group and the chip group; [com.qeetgroup.qeetrix.components.input.QeetrixChoiceOrientation] and
[com.qeetgroup.qeetrix.components.input.QeetrixChoiceVariant] lay out the checkbox and radio groups as rows or tiles.

# Package com.qeetgroup.qeetrix.components.passwordinput

[com.qeetgroup.qeetrix.components.passwordinput.QeetrixPasswordInput]: a password field on Compose's
`BasicSecureTextField`, with Show password, a Caps Lock warning and AutoFill for a current or a new password. The
password stays in the caller's `TextFieldState`; Qeetrix keeps, logs and describes none of it.

# Package com.qeetgroup.qeetrix.components.otpinput

[com.qeetgroup.qeetrix.components.otpinput.QeetrixOTPInput]: a one-time code in one native field, drawn
as slots, with SMS one-time-code AutoFill. It never reads messages or asks for permissions.

# Package com.qeetgroup.qeetrix.components.checkbox

[com.qeetgroup.qeetrix.components.checkbox.QeetrixCheckbox] — checked, unchecked and indeterminate, as a labelled
row that is one 48 dp control or as a bare box — and
[com.qeetgroup.qeetrix.components.checkbox.QeetrixCheckboxGroup], several choices under one question, as rows or
tiles.

# Package com.qeetgroup.qeetrix.components.radiogroup

[com.qeetgroup.qeetrix.components.radiogroup.QeetrixRadioGroup]: one typed choice from a short, visible
list of options, as rows or tiles.

# Package com.qeetgroup.qeetrix.components.select

[com.qeetgroup.qeetrix.components.select.QeetrixSelect]: one typed choice from a list in a popup, with a
trigger drawn as a field, in groups when given `QeetrixOptionGroup`s.

# Package com.qeetgroup.qeetrix.components.numberfield

[com.qeetgroup.qeetrix.components.numberfield.QeetrixNumberField]: an exact `Int` or `BigDecimal` with
locale-aware typing, a range, a step, a large step and stepper buttons.

# Package com.qeetgroup.qeetrix.components.form

Form patterns: [com.qeetgroup.qeetrix.components.form.QeetrixForm] is a form's column, its fields `space.form.gap`
apart, with the summary of what to fix after a failed submit;
[com.qeetgroup.qeetrix.components.form.QeetrixFormErrorSummary] is that summary on its own, one
[com.qeetgroup.qeetrix.components.form.QeetrixFormError] per problem; and
[com.qeetgroup.qeetrix.components.form.QeetrixFormActions] places a form's buttons. Values, validation and submission
stay with the app.

# Package com.qeetgroup.qeetrix.components.navigation

App chrome. [com.qeetgroup.qeetrix.components.navigation.QeetrixNavigationBar] and
[com.qeetgroup.qeetrix.components.navigation.QeetrixNavigationRail] present an app's top-level
destinations ([com.qeetgroup.qeetrix.components.navigation.QeetrixNavigationItem], the web's SidebarItem), marking
the current one with the Ember bar; [com.qeetgroup.qeetrix.components.navigation.QeetrixNavigationScaffold] switches
between them with the window width, as Android adapts the web's AppShell;
[com.qeetgroup.qeetrix.components.navigation.QeetrixTopAppBar] heads a screen in PageHeader's band, with
[com.qeetgroup.qeetrix.components.navigation.QeetrixBackButton]. The current destination is the app's: there is no
router or back stack.

# Package com.qeetgroup.qeetrix.components.tabs

[com.qeetgroup.qeetrix.components.tabs.QeetrixTabs]: switches between views of related content within a
screen, in the web's line and contained looks, horizontal or vertical.

# Package com.qeetgroup.qeetrix.components.dialog

Modal decisions and tasks in Compose's dialog window, each topped by its intent line:
[com.qeetgroup.qeetrix.components.dialog.QeetrixAlertDialog] with typed
[com.qeetgroup.qeetrix.components.dialog.QeetrixDialogAction]s,
[com.qeetgroup.qeetrix.components.dialog.QeetrixConfirmationDialog] for "are you sure?", and
[com.qeetgroup.qeetrix.components.dialog.QeetrixDialog] for content. Actions run once; a destructive choice
always comes with Cancel.

# Package com.qeetgroup.qeetrix.components.drawer

[com.qeetgroup.qeetrix.components.drawer.QeetrixBottomSheet]: the web's Drawer as a modal sheet from the
bottom edge, opened by a flag the app owns.

# Package com.qeetgroup.qeetrix.components.dropdownmenu

[com.qeetgroup.qeetrix.components.dropdownmenu.QeetrixMenu]: a short list of actions or options opened from a
trigger, with [com.qeetgroup.qeetrix.components.dropdownmenu.QeetrixMenuItem]s, checkbox items, radio groups and
items, group labels and separators.

# Package com.qeetgroup.qeetrix.components.toast

Transient feedback, the web Toast's Android form: [com.qeetgroup.qeetrix.components.toast.QeetrixSnackbar]
shown through a screen's [com.qeetgroup.qeetrix.components.toast.QeetrixSnackbarHost] and
[com.qeetgroup.qeetrix.components.toast.QeetrixSnackbarHostState] — one at a time, with the web's tones and timing.

# Package com.qeetgroup.qeetrix.components.switchcontrol

[com.qeetgroup.qeetrix.components.switchcontrol.QeetrixSwitch]: an on/off control with switch semantics,
alone or as a labelled row that toggles as one control. The package is `switchcontrol` because
`switch` is a Java keyword.

# Package com.qeetgroup.qeetrix.components.avatar

[com.qeetgroup.qeetrix.components.avatar.QeetrixAvatar]: a person's or an organisation's picture
(any `Painter` the app has loaded) or their initials, round for a person and square for an organisation;
[com.qeetgroup.qeetrix.components.avatar.QeetrixAvatarItem]: the avatar beside a name and a description.

# Package com.qeetgroup.qeetrix.components.badge

[com.qeetgroup.qeetrix.components.badge.QeetrixBadge]: a short, static status or count in a pill, whose word always
carries the meaning. [com.qeetgroup.qeetrix.components.badge.QeetrixChip] and
[com.qeetgroup.qeetrix.components.badge.QeetrixChipGroup]: tags that filter or choose — a toggle, one
value or several from a set, or a removable applied filter.

# Package com.qeetgroup.qeetrix.components.list

Rows of collections, which only the native libraries have:
[com.qeetgroup.qeetrix.components.list.QeetrixListItem] with its
[com.qeetgroup.qeetrix.components.list.QeetrixListItemIcon] tile, and
[com.qeetgroup.qeetrix.components.list.QeetrixSectionHeader], a group label for a section of rows. They go inside
Compose's `LazyColumn`, keyed by the app's ids; the data, its identity and its selection are the app's.

# Package com.qeetgroup.qeetrix.components.descriptionlist

[com.qeetgroup.qeetrix.components.descriptionlist.QeetrixDescriptionList]: facts about one record — terms and
values — in ruled rows or in columns; a
[com.qeetgroup.qeetrix.components.descriptionlist.QeetrixDescriptionItem] is one term and its value.

# Package com.qeetgroup.qeetrix.components.accordion

[com.qeetgroup.qeetrix.components.accordion.QeetrixAccordion] and
[com.qeetgroup.qeetrix.components.accordion.QeetrixAccordionItem]: ruled sections whose triggers show and hide their
content, with the open sections owned by the app;
[com.qeetgroup.qeetrix.components.accordion.QeetrixCollapsibleTrigger] and
[com.qeetgroup.qeetrix.components.accordion.QeetrixCollapsiblePanel]: a single fold without the rules.

# Package com.qeetgroup.qeetrix.components.emptystate

What a surface shows instead of data: [com.qeetgroup.qeetrix.components.emptystate.QeetrixEmptyState] for
nothing yet, no results or an error, and [com.qeetgroup.qeetrix.components.emptystate.QeetrixDataState] for the
loading, error and empty branches of loaded data in one place.

# Package com.qeetgroup.qeetrix.components.feed

[com.qeetgroup.qeetrix.components.feed.QeetrixTimeline]: what happened, newest first — events
([com.qeetgroup.qeetrix.components.feed.QeetrixTimelineItem]) with square markers or dots, toned, in day groups
([com.qeetgroup.qeetrix.components.feed.QeetrixTimelineGroup]).

# Package com.qeetgroup.qeetrix.components.typography

[com.qeetgroup.qeetrix.components.typography.QeetrixText]: running text, labels and inline code on the body roles,
in three sizes, three weights, the text tones and Fira Code; and
[com.qeetgroup.qeetrix.components.typography.QeetrixHeading]: a heading whose level chooses its default look among
the display, heading and title roles, announced as a heading.

# Package com.qeetgroup.qeetrix.components.progress

[com.qeetgroup.qeetrix.components.progress.QeetrixProgress]: determinate or indeterminate task progress
with Compose's progress semantics; [com.qeetgroup.qeetrix.components.progress.QeetrixMeter]: a measurement within a
range, with an optional target.

# Package com.qeetgroup.qeetrix.components.spinner

[com.qeetgroup.qeetrix.components.spinner.QeetrixSpinner]: an indeterminate wait, announced as "Loading" or a label
of your own. [com.qeetgroup.qeetrix.components.spinner.QeetrixSkeleton] and
[com.qeetgroup.qeetrix.components.spinner.QeetrixSkeletonText]: placeholders in the shape of content that
is loading, invisible to accessibility services.

# Package com.qeetgroup.qeetrix.components.card

[com.qeetgroup.qeetrix.components.card.QeetrixCard]: a flat, ruled surface with an optional header, content and
footer, static or as one clickable choice.

# Package com.qeetgroup.qeetrix.components.separator

[com.qeetgroup.qeetrix.components.separator.QeetrixSeparator]: a decorative 1 dp rule, horizontal or
vertical.
