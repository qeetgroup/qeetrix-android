# Components

One page per component: what it is for, how to use it, how it looks, its accessibility, the tokens it draws from
and where Android differs from the web. Every component is the Android counterpart of an `@qeetrix/react` 0.4.0
component (the pinned `Upstream/qeetrix-react/component-manifest.json`), drawn by its CSS and the design rules
(qeetrix-files `qeetrix-react/05-design-rules.md`); a few native components have none. The parameter-by-parameter
reference is the KDoc, published by Dokka (`./gradlew :qeetrix:dokkaGenerate`); these pages do not repeat it.
ui.qeet.in stays the organisation-wide documentation.

Grouped as the sample app groups its pages (`SampleCategory`); the sample has a page for each, under the same group.

| Group | Component | Kotlin | Package (`com.qeetgroup.qeetrix.components.…`) | `@qeetrix/react` |
|:--|:--|:--|:--|:--|
| Actions | [Button](Button.md) | `QeetrixButton` | `button` | Button |
| Actions | [Bottom action bar](BottomActionBar.md) | `QeetrixBottomActionBar` | `actionbar` | none (a dialog footer's rules on the screen's edge) |
| Actions | [Copy button and copy field](Copy.md) | `QeetrixCopyButton`, `QeetrixCopyField` | `copy` | CopyButton, CopyField (experimental) |
| Actions | [Segmented control](SegmentedControl.md) | `QeetrixSegmentedControl` | `segmented` | ToggleGroup `variant="segmented"` |
| Actions | [Icon button](IconButton.md) | `QeetrixIconButton` | `button` | IconButton |
| Forms & inputs | [Checkbox](Checkbox.md) | `QeetrixCheckbox`, `QeetrixCheckboxGroup` | `checkbox` | Checkbox, CheckboxGroup |
| Forms & inputs | [Date, range and time fields](DatePicker.md) | `QeetrixDateField`, `QeetrixDateRangeField`, `QeetrixTimeField` | `datepicker` | DatePicker, DateRangePicker (experimental) |
| Forms & inputs | [Attachments](FileUpload.md) | `QeetrixAttachmentPicker`, `QeetrixFileRow`, `QeetrixFileLimits` | `fileupload` | FileUpload (experimental) |
| Forms & inputs | [Field](Field.md) | `QeetrixField`, `QeetrixFieldSet` | `input` | Field, Fieldset |
| Forms & inputs | [Form](Form.md) | `QeetrixForm`, `QeetrixFormErrorSummary`, `QeetrixFormError`, `QeetrixFormActions` | `form` | Form, ErrorSummary (experimental) |
| Forms & inputs | [Text field](TextField.md) | `QeetrixTextField` | `input` | TextField |
| Forms & inputs | [Number field](NumberField.md) | `QeetrixNumberField` | `numberfield` | NumberField (experimental) |
| Forms & inputs | [OTP input](OTPInput.md) | `QeetrixOTPInput` | `otpinput` | OTPInput (experimental) |
| Forms & inputs | [Password input](PasswordInput.md) | `QeetrixPasswordInput` | `passwordinput` | PasswordInput (experimental) |
| Forms & inputs | [One-time code](OTPCode.md) | `QeetrixOTPCode` | `otpinput` | none (beside OTPInput) |
| Forms & inputs | [Permission primer](PermissionPrimer.md) | `QeetrixPermissionPrimer` | `permission` | none |
| Forms & inputs | [QR scanner](QRScanner.md) | `QeetrixQRScanner` | `scanner` | none |
| Forms & inputs | [Radio group](RadioGroup.md) | `QeetrixRadioGroup` | `radiogroup` | RadioGroup, Radio |
| Forms & inputs | [Search field](SearchField.md) | `QeetrixSearchField` | `input` | SearchField (experimental) |
| Forms & inputs | [Select](Select.md) | `QeetrixSelect`, `QeetrixOption`, `QeetrixOptionGroup` | `select`; the options `input` | Select (its `items` and groups) |
| Forms & inputs | [Slider](Slider.md) | `QeetrixSlider`, `QeetrixRangeSlider` | `slider` | Slider |
| Forms & inputs | [Switch](Switch.md) | `QeetrixSwitch` | `switchcontrol` | Switch |
| Forms & inputs | [Textarea](Textarea.md) | `QeetrixTextarea` | `input` | Textarea |
| Data display | [Avatar](Avatar.md) | `QeetrixAvatar`, `QeetrixAvatarItem` | `avatar` | Avatar; AvatarItem (experimental) |
| Data display | [Avatar group](AvatarGroup.md) | `QeetrixAvatarGroup`, `QeetrixAvatarPerson` | `avatar` | AvatarGroup (experimental) |
| Data display | [Badge](Badge.md) | `QeetrixBadge` | `badge` | Badge |
| Data display | [Check list](CheckList.md) | `QeetrixCheckList`, `QeetrixCheckItem` | `checklist` | CheckList, CheckItem (experimental) |
| Data display | [Chip](Chip.md) | `QeetrixChip`, `QeetrixChipGroup` | `badge` | none (closest: FilterTags' tag) |
| Data display | [Copy button and copy field](Copy.md) | `QeetrixCopyButton`, `QeetrixCopyField` | `copy` | CopyButton, CopyField (experimental) |
| Data display | [Description list](DescriptionList.md) | `QeetrixDescriptionList`, `QeetrixDescriptionItem` | `descriptionlist` | DescriptionList, DescriptionItem (experimental) |
| Data display | [QR code](QRCode.md) | `QeetrixQRCode` | `qrcode` | QRCode (experimental) |
| Data display | [Stat](Stat.md) | `QeetrixStat`, `QeetrixStatGroup` | `stat` | Stat, StatGroup (experimental) |
| Data display | [Swipe actions](SwipeActions.md) | `QeetrixSwipeActions`, `QeetrixSwipeAction` | `list` | none |
| Data display | [List item](ListItem.md) | `QeetrixListItem`, `QeetrixListItemIcon` | `list` | none (drawn by Table's row rules) |
| Data display | [Relative time](RelativeTime.md) | `QeetrixRelativeTime` | `relativetime` | RelativeTime (experimental) |
| Data display | [Section header](SectionHeader.md) | `QeetrixSectionHeader` | `list` | none (drawn as Sidebar's group label) |
| Data display | [Text](Text.md) | `QeetrixText`, `QeetrixHeading` | `typography` | Text, Heading |
| Data display | [Timeline](Timeline.md) | `QeetrixTimeline`, `QeetrixTimelineGroup`, `QeetrixTimelineItem` | `feed` | Timeline, TimelineGroup, TimelineItem (experimental) |
| Feedback | [Alert](Alert.md) | `QeetrixAlert` | `alert` | Alert (experimental) |
| Feedback | [Data state](DataState.md) | `QeetrixDataState` | `emptystate` | DataState (experimental) |
| Feedback | [Empty state](EmptyState.md) | `QeetrixEmptyState` | `emptystate` | EmptyState (experimental) |
| Feedback | [Progress](Progress.md) | `QeetrixProgress`, `QeetrixMeter` | `progress` | Progress, Meter |
| Feedback | [Progress ring](ProgressRing.md) | `QeetrixProgressRing` | `progress` | none (Progress's parts, drawn round) |
| Feedback | [Pull to refresh](PullToRefresh.md) | `QeetrixPullToRefresh` | `list` | none |
| Feedback | [Skeleton](Skeleton.md) | `QeetrixSkeleton`, `QeetrixSkeletonText` | `spinner` | Skeleton |
| Feedback | [Spinner](Spinner.md) | `QeetrixSpinner` | `spinner` | Spinner |
| Feedback | [Snackbar](Snackbar.md) | `QeetrixSnackbar`, `QeetrixSnackbarHost`, `QeetrixSnackbarHostState`, `rememberQeetrixSnackbarHostState` | `toast` | Toast (`ToastProvider`, `useToast`) |
| Overlays | [Alert dialog](AlertDialog.md) | `QeetrixAlertDialog`, `QeetrixDialogAction` | `dialog` | Dialog with `intent="danger"` or `"warning"` |
| Overlays | [Confirmation dialog](ConfirmationDialog.md) | `QeetrixConfirmationDialog` | `dialog` | Dialog with Cancel and a confirming Button |
| Overlays | [Dialog](Dialog.md) | `QeetrixDialog` | `dialog` | Dialog, DialogContent, DialogBody, DialogFooter |
| Overlays | [Bottom sheet](BottomSheet.md) | `QeetrixBottomSheet` | `drawer` | Drawer, as a bottom sheet |
| Overlays | [Full-screen form](FullScreenForm.md) | `QeetrixFullScreenForm` | `dialog` | none (Dialog's `fullscreen` size is the nearest) |
| Overlays | [Menu](Menu.md) | `QeetrixMenu`, `QeetrixMenuItem`, `QeetrixMenuCheckboxItem`, `QeetrixMenuRadioGroup`, `QeetrixMenuRadioItem`, `QeetrixMenuGroupLabel`, `QeetrixMenuSeparator` | `dropdownmenu` | Menu and the same items |
| Navigation | [Navigation bar](NavigationBar.md) | `QeetrixNavigationBar`, `QeetrixNavigationItem` | `navigation` | none (Sidebar's items, in a bottom bar) |
| Navigation | [Navigation rail](NavigationRail.md) | `QeetrixNavigationRail` | `navigation` | Sidebar, as a rail (experimental) |
| Navigation | [Adaptive navigation](NavigationScaffold.md) | `QeetrixNavigationScaffold` | `navigation` | AppShell, as Android adapts it (experimental) |
| Navigation | [Page indicator](PageIndicator.md) | `QeetrixPageIndicator` | `pageindicator` | none |
| Navigation | [Steps](Steps.md) | `QeetrixSteps`, `QeetrixStep` | `steps` | Steps (experimental) |
| Navigation | [Tabs](Tabs.md) | `QeetrixTabs`, `QeetrixTab` | `tabs` | Tabs, TabsList, Tab |
| Navigation | [Top app bar](TopAppBar.md) | `QeetrixTopAppBar`, `QeetrixBackButton` | `navigation` | PageHeader, PageHeaderBack, as a phone's app bar (experimental) |
| Layout | [Accordion](Accordion.md) | `QeetrixAccordion`, `QeetrixAccordionItem`, `QeetrixCollapsibleTrigger`, `QeetrixCollapsiblePanel` | `accordion` | Accordion, AccordionItem; Collapsible (CollapsibleTrigger, CollapsiblePanel) |
| Layout | [Card](Card.md) | `QeetrixCard` | `card` | Card |
| Layout | [Separator](Separator.md) | `QeetrixSeparator` | `separator` | Separator |

Each component's options are top-level enums in its package, named `Qeetrix<Component><Axis>` with the
`@qeetrix/react` names (`QeetrixButtonVariant.Soft`, `value = "soft"`); their pages list them.
`QeetrixOption` and `QeetrixOptionGroup` are the typed options of the select, the radio group, the checkbox group
and the chip group; `QeetrixChoiceOrientation` and `QeetrixChoiceVariant` (both `components.input`) lay out the
checkbox and radio groups.

Packages follow the web's component families, not these groups
([ADR 0010](../Decisions/0010-component-api-conventions.md)); Android app chrome the web has no family for is
`navigation` ([ADR 0015](../Decisions/0015-navigation-overlays-and-feedback.md)), and list rows and section
headers, which only the native libraries have, are `list`
([ADR 0016](../Decisions/0016-data-display-and-content-states.md)). Not built, by decision: a navigation drawer, a
popover and a Qeetrix toast (ADR 0015), and a data table and image presentation (ADR 0016) — each record says why
and what to use instead. A pull-to-refresh wrapper was among them, and is now built ([Pull to refresh](PullToRefresh.md),
QTRX-107). Of the manifest's other families, those without an Android
counterpart yet include Breadcrumb, CommandPalette, Link and LinkButton, Popover, Table and TreeView. How these components work with an app's data is
in [Data display and collections](../Standards/DATA_DISPLAY_AND_COLLECTIONS.md). New components start from
`Scripts/new-component` ([Templates/Component](../../Templates/Component/README.md)).

## What every component shares

- **One brand, no colour parameters.** Customisation is the `@qeetrix/react` axes — variant, tone, size — and the
  density, which is set once per region with `LocalQeetrixDensity`, never per component (design rules §6). Text
  colours are tones (`QeetrixTextTone`), never a `Color`.
- **Square shapes** (§3): 4 dp corners on controls and surfaces, 2 dp on small marks (checkbox, switch track), no
  corner where a surface meets a rule; only people (avatars, radios) and status (badges) are round.
- **Ember where it means something** (§4): the primary action, focus, the current item, checked controls and
  links — never a large surface or decoration.
- **Fields** (§8) are filled (`color.bg.neutral.subtle`) with a 1 dp `color.border.strong` line along the bottom,
  4 dp top corners and the `size.control.lg` height; the label sits above in `label.sm` / `color.text.subtle`,
  "(optional)" rather than an asterisk; the help text sits below, and an error replaces it while it shows.
- **Actions** (§7): label first, icon last in its own zone; at most one solid and one soft button per region, every
  other action ghost; a destructive confirmation replaces the primary.
- **Focus** (§10): keyboard focus draws the 2 dp Ember frame (`focus.ring.width`, `color.border.focus`), never
  removed or replaced by colour — inside buttons with a 1 dp surface gap, inside fields in place of the bottom
  line, 2 dp outside checkboxes, radios, switches and chips, inside rows and in a scrolling tab list. In touch mode
  Android moves no focus to controls, so the frame appears for keyboard, D-pad and switch access only.
- **Current and selected are different marks** (§11): the 3 dp Ember bar (`border.width.accent`) marks the current
  item — a tab's bottom edge, a navigation item's edge that faces the screen, a highlighted menu or select option
  (2 dp, start edge); the Ember tint (`color.bg.selected`) with a check marks what is selected for an action — rows,
  chips, choice tiles.
- **Flat, ruled surfaces** (§12): cards and panels are separated by 1 dp rules and tone; only floating layers have
  shadows — `shadow.overlay` for menus, the select list and snackbars, `shadow.modal` for dialogs.
- **Icons** (§16): sharp outline on controls, round in navigation, filled only for a current or selected state;
  16 dp inside controls, 20 dp standalone, 24 dp in empty states; stroke 2, 1.5 at the compact density.
- **Touch.** Every control reserves a 48 × 48 dp touch area around the web-sized drawing. The few drawn smaller
  where a reserved area would move the layout — a field's show-password, clear and help buttons, a chip's remove
  button, the accordion's trigger — take a tap within 48 dp through Compose's touch-target expansion instead; the
  number field's steppers are field-tall targets, with Decrease and Increase actions on the field as the accessible
  path ([Accessibility report](../Quality/ACCESSIBILITY-REPORT.md)).
- **Feedback.** No ripple: pressed and hovered states are the web's `-hover` and `-pressed` colour steps, in
  `motion.duration.fast`; nothing scales or bounces (§18).
- **Text** is in sp and follows the font scale; containers grow rather than clip.
- **Right to left.** Layouts mirror; directional icons (send, arrows) mirror; progress fills from the start.
  Short labels take their direction from their own text, so an English label in an Arabic screen ends where English
  does.
- **State and Back.** The app owns every value, selection and `open` flag (`value` / `onValueChange`, `open` /
  `onOpenChange`, the `@qeetrix/react` names); overlays open in the platform's dialog and popup windows, which close
  on Back and Escape. Qeetrix registers no Back handler and owns no navigation.
- **Data.** Components display the values they are given and keep no data of their own; the app owns fetching,
  identity (lazy-list keys), selection, filtering and every load state.
- **Reduced motion.** With animations turned off in the system settings, every Qeetrix duration is 0, as on the
  web: colour steps, bars and folds change at once, skeletons hold still, and the spinner and indeterminate progress
  fade instead of moving.
