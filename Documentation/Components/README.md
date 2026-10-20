# Components

One page per component: what it is for, how to use it, its states, accessibility, the tokens it draws
from and where Android differs from the web and iOS. The parameter-by-parameter reference is the KDoc,
published by Dokka (`./gradlew :qeetrix:dokkaGenerate`); these pages do not repeat it. ui.qeet.in stays
the organisation-wide documentation.

Grouped as ui.qeet.in groups them (qeetrix-docs `src/lib/component-groups.json`). The sample app has a
page for each, under the same group.

| Group | Component | Kotlin | Package (`com.qeetgroup.qeetrix.components.…`) |
|:--|:--|:--|:--|
| Actions | [Button](Button.md) | `QeetrixButton` | `button` |
| Actions | [Icon button](IconButton.md) | `QeetrixIconButton` | `button` |
| Forms & inputs | [Checkbox](Checkbox.md) | `QeetrixCheckbox`, `QeetrixTriStateCheckbox` | `checkbox` |
| Forms & inputs | [Field](Field.md) | `QeetrixField`, `QeetrixFieldSet` | `input` |
| Forms & inputs | [Form](Form.md) | `QeetrixFormActions`, `QeetrixFormErrorSummary` | `form` |
| Forms & inputs | [Text field](TextField.md) | `QeetrixTextField` | `input` |
| Forms & inputs | [Number field](NumberField.md) | `QeetrixNumberField` | `numberfield` |
| Forms & inputs | [OTP input](OTPInput.md) | `QeetrixOTPInput` | `otpinput` |
| Forms & inputs | [Password input](PasswordInput.md) | `QeetrixPasswordInput` | `passwordinput` |
| Forms & inputs | [Radio group](RadioGroup.md) | `QeetrixRadioGroup` | `radiogroup` |
| Forms & inputs | [Search field](SearchField.md) | `QeetrixSearchField` | `input` |
| Forms & inputs | [Select](Select.md) | `QeetrixSelect` | `select` |
| Forms & inputs | [Switch](Switch.md) | `QeetrixSwitch` | `switchcontrol` |
| Forms & inputs | [Textarea](Textarea.md) | `QeetrixTextarea` | `input` |
| Data display | [Avatar](Avatar.md) | `QeetrixAvatar` | `avatar` |
| Data display | [Badge](Badge.md) | `QeetrixBadge` | `badge` |
| Data display | [Chip](Chip.md) | `QeetrixChip`, `QeetrixChipGroup` | `badge` |
| Data display | [Description list](DescriptionList.md) | `QeetrixDescriptionList`, `QeetrixDescriptionItem` | `descriptionlist` |
| Data display | [List item](ListItem.md) | `QeetrixListItem`, `QeetrixListItemIcon` | `list` |
| Data display | [Section header](SectionHeader.md) | `QeetrixSectionHeader` | `list` |
| Data display | [Text](Text.md) | `QeetrixText` | `typography` |
| Data display | [Timeline](Timeline.md) | `QeetrixTimeline`, `QeetrixTimelineItem` | `feed` |
| Feedback | [Data state](DataState.md) | `QeetrixDataState`, `QeetrixDataFailure` | `emptystate` |
| Feedback | [Empty state](EmptyState.md) | `QeetrixEmptyState` | `emptystate` |
| Feedback | [Progress](Progress.md) | `QeetrixProgress` | `progress` |
| Feedback | [Skeleton](Skeleton.md) | `QeetrixSkeleton`, `QeetrixSkeletonText` | `spinner` |
| Feedback | [Spinner](Spinner.md) | `QeetrixSpinner` | `spinner` |
| Feedback | [Snackbar](Snackbar.md) | `QeetrixSnackbar`, `QeetrixSnackbarHost`, `QeetrixSnackbarHostState` | `toast` |
| Overlays | [Alert dialog](AlertDialog.md) | `QeetrixAlertDialog`, `QeetrixDialogAction` | `dialog` |
| Overlays | [Confirmation dialog](ConfirmationDialog.md) | `QeetrixConfirmationDialog` | `dialog` |
| Overlays | [Dialog](Dialog.md) | `QeetrixDialog` | `dialog` |
| Overlays | [Bottom sheet](BottomSheet.md) | `QeetrixBottomSheet` | `drawer` |
| Overlays | [Menu](Menu.md) | `QeetrixMenu`, `QeetrixMenuItem` | `dropdownmenu` |
| Navigation | [Navigation bar](NavigationBar.md) | `QeetrixNavigationBar`, `QeetrixNavigationItem` | `navigation` |
| Navigation | [Navigation rail](NavigationRail.md) | `QeetrixNavigationRail` | `navigation` |
| Navigation | [Adaptive navigation](NavigationScaffold.md) | `QeetrixNavigationScaffold` | `navigation` |
| Navigation | [Tabs](Tabs.md) | `QeetrixTabs`, `QeetrixTab` | `tabs` |
| Navigation | [Top app bar](TopAppBar.md) | `QeetrixTopAppBar`, `QeetrixBackButton` | `navigation` |
| Layout | [Accordion](Accordion.md) | `QeetrixAccordion`, `QeetrixAccordionItem` | `accordion` |
| Layout | [Card](Card.md) | `QeetrixCard` | `card` |
| Layout | [Separator](Separator.md) | `QeetrixSeparator` | `separator` |

Packages follow the web's component families, not these groups
([ADR 0010](../Decisions/0010-component-api-conventions.md)); Android app chrome the web has no family for
is `navigation` ([ADR 0015](../Decisions/0015-navigation-overlays-and-feedback.md)), and list rows and
section headers, which only the native libraries have, are `list`
([ADR 0016](../Decisions/0016-data-display-and-content-states.md)). Not built, by decision: a navigation
drawer, a popover and a Qeetrix toast (ADR 0015), and a data table, image presentation and a pull-to-refresh
wrapper (ADR 0016) — each record says why and what to use instead. How these components work with an app's
data is in [Data display and collections](../Standards/DATA_DISPLAY_AND_COLLECTIONS.md).
New components start from `Scripts/new-component` ([Templates/Component](../../Templates/Component/README.md)).

## What every component shares

- **One brand, no colour parameters.** Customisation is variant, size and density; `QeetrixText`'s
  `color` takes a theme role.
- **Touch.** Every control reserves a 48 × 48 dp touch area around the web-sized drawing. The few drawn
  inside or beside another control — a field's clear and reveal buttons, the number field's steppers, a
  chip's remove button, contained tabs — take a tap 48 dp across through Compose's touch-target expansion
  instead ([Accessibility report](../Quality/ACCESSIBILITY-REPORT.md)).
- **Feedback.** No ripple: pressed, hovered and focused states use the web's colour and elevation steps,
  and keyboard focus draws the Qeet focus ring.
- **Text** is in sp and follows the font scale; containers grow rather than clip.
- **Right to left.** Layouts mirror; directional icons (send) mirror; progress fills from the start.
  Short labels take their direction from their own text, so an English label in an Arabic screen ends
  where English does.
- **State and Back.** The app owns every value, selection and presented flag; overlays open in the
  platform's dialog and popup windows, which close on Back and Escape. Qeetrix registers no Back handler
  and owns no navigation.
- **Data.** Components display the values they are given and keep no data of their own; the app owns
  fetching, identity (lazy-list keys), selection, filtering and every load state.
- **Reduced motion.** With animations turned off in the system settings, spinners, indeterminate progress
  and skeletons hold still, and accordions open at once.
