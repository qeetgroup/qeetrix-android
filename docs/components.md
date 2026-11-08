# 🧩 Components

Every component in Qeetrix for Android, and its `@qeetrix/react` counterpart. Each one's parameters are in its KDoc
(`./gradlew :qeetrix:dokkaGenerate`), and the sample app has a page for each. ui.qeet.in will carry the Android pages.

| Group | Component | Kotlin | Package (`com.qeetgroup.qeetrix.components.…`) | `@qeetrix/react` |
|:--|:--|:--|:--|:--|
| Actions | Button | `QeetrixButton` | `button` | Button |
| Actions | Bottom action bar | `QeetrixBottomActionBar` | `actionbar` | none (a dialog footer's rules on the screen's edge) |
| Actions | Copy button and copy field | `QeetrixCopyButton`, `QeetrixCopyField` | `copy` | CopyButton, CopyField (experimental) |
| Actions | Segmented control | `QeetrixSegmentedControl` | `segmented` | ToggleGroup `variant="segmented"` |
| Actions | Icon button | `QeetrixIconButton` | `button` | IconButton |
| Forms & inputs | Checkbox | `QeetrixCheckbox`, `QeetrixCheckboxGroup` | `checkbox` | Checkbox, CheckboxGroup |
| Forms & inputs | Date, range and time fields | `QeetrixDateField`, `QeetrixDateRangeField`, `QeetrixTimeField` | `datepicker` | DatePicker, DateRangePicker (experimental) |
| Forms & inputs | Attachments | `QeetrixAttachmentPicker`, `QeetrixFileRow`, `QeetrixFileLimits` | `fileupload` | FileUpload (experimental) |
| Forms & inputs | Field | `QeetrixField`, `QeetrixFieldSet` | `input` | Field, Fieldset |
| Forms & inputs | Form | `QeetrixForm`, `QeetrixFormErrorSummary`, `QeetrixFormError`, `QeetrixFormActions` | `form` | Form, ErrorSummary (experimental) |
| Forms & inputs | Text field | `QeetrixTextField` | `input` | TextField |
| Forms & inputs | Number field | `QeetrixNumberField` | `numberfield` | NumberField (experimental) |
| Forms & inputs | OTP input | `QeetrixOTPInput` | `otpinput` | OTPInput (experimental) |
| Forms & inputs | Password input | `QeetrixPasswordInput` | `passwordinput` | PasswordInput (experimental) |
| Forms & inputs | One-time code | `QeetrixOTPCode` | `otpinput` | none (beside OTPInput) |
| Forms & inputs | Permission primer | `QeetrixPermissionPrimer` | `permission` | none |
| Forms & inputs | QR scanner | `QeetrixQRScanner` | `scanner` | none |
| Forms & inputs | Radio group | `QeetrixRadioGroup` | `radiogroup` | RadioGroup, Radio |
| Forms & inputs | Search field | `QeetrixSearchField` | `input` | SearchField (experimental) |
| Forms & inputs | Select | `QeetrixSelect`, `QeetrixMultiSelect`, `QeetrixOption`, `QeetrixOptionGroup` | `select`; the options `input` | Select (its `items` and groups; `multiple`) |
| Forms & inputs | Searchable select | `QeetrixSearchableSelect`, `QeetrixSearchableMultiSelect` | `select` | Combobox, as a sheet with search |
| Forms & inputs | Slider | `QeetrixSlider`, `QeetrixRangeSlider` | `slider` | Slider |
| Forms & inputs | Switch | `QeetrixSwitch` | `switchcontrol` | Switch |
| Forms & inputs | Textarea | `QeetrixTextarea` | `input` | Textarea |
| Data display | Avatar | `QeetrixAvatar`, `QeetrixAvatarItem` | `avatar` | Avatar; AvatarItem (experimental) |
| Data display | Avatar group | `QeetrixAvatarGroup`, `QeetrixAvatarPerson` | `avatar` | AvatarGroup (experimental) |
| Data display | Badge | `QeetrixBadge` | `badge` | Badge |
| Data display | Check list | `QeetrixCheckList`, `QeetrixCheckItem` | `checklist` | CheckList, CheckItem (experimental) |
| Data display | Chip | `QeetrixChip`, `QeetrixChipGroup` | `badge` | none (closest: FilterTags' tag) |
| Data display | Copy button and copy field | `QeetrixCopyButton`, `QeetrixCopyField` | `copy` | CopyButton, CopyField (experimental) |
| Data display | Description list | `QeetrixDescriptionList`, `QeetrixDescriptionItem` | `descriptionlist` | DescriptionList, DescriptionItem (experimental) |
| Data display | QR code | `QeetrixQRCode` | `qrcode` | QRCode (experimental) |
| Data display | Stat | `QeetrixStat`, `QeetrixStatGroup` | `stat` | Stat, StatGroup (experimental) |
| Data display | Swipe actions | `QeetrixSwipeActions`, `QeetrixSwipeAction` | `list` | none |
| Data display | List item | `QeetrixListItem`, `QeetrixListItemIcon` | `list` | none (drawn by Table's row rules) |
| Data display | Relative time | `QeetrixRelativeTime` | `relativetime` | RelativeTime (experimental) |
| Data display | Section header | `QeetrixSectionHeader` | `list` | none (drawn as Sidebar's group label) |
| Data display | Text | `QeetrixText`, `QeetrixHeading` | `typography` | Text, Heading |
| Data display | Link | `QeetrixLink`, `qeetrixLink` | `typography` | Link |
| Data display | Timeline | `QeetrixTimeline`, `QeetrixTimelineGroup`, `QeetrixTimelineItem` | `feed` | Timeline, TimelineGroup, TimelineItem (experimental) |
| Feedback | Alert | `QeetrixAlert` | `alert` | Alert (experimental) |
| Feedback | Data state | `QeetrixDataState` | `emptystate` | DataState (experimental) |
| Feedback | Empty state | `QeetrixEmptyState` | `emptystate` | EmptyState (experimental) |
| Feedback | Progress | `QeetrixProgress`, `QeetrixMeter` | `progress` | Progress, Meter |
| Feedback | Progress ring | `QeetrixProgressRing` | `progress` | none (Progress's parts, drawn round) |
| Feedback | Pull to refresh | `QeetrixPullToRefresh` | `list` | none |
| Feedback | Skeleton | `QeetrixSkeleton`, `QeetrixSkeletonText` | `spinner` | Skeleton |
| Feedback | Spinner | `QeetrixSpinner` | `spinner` | Spinner |
| Feedback | Snackbar | `QeetrixSnackbar`, `QeetrixSnackbarHost`, `QeetrixSnackbarHostState`, `rememberQeetrixSnackbarHostState` | `toast` | Toast (`ToastProvider`, `useToast`) |
| Overlays | Alert dialog | `QeetrixAlertDialog`, `QeetrixDialogAction` | `dialog` | Dialog with `intent="danger"` or `"warning"` |
| Overlays | Confirmation dialog | `QeetrixConfirmationDialog` | `dialog` | Dialog with Cancel and a confirming Button |
| Overlays | Dialog | `QeetrixDialog` | `dialog` | Dialog, DialogContent, DialogBody, DialogFooter |
| Overlays | Bottom sheet | `QeetrixBottomSheet` | `drawer` | Drawer, as a bottom sheet |
| Overlays | Action sheet | `QeetrixActionSheet`, `QeetrixSheetAction`, `QeetrixSheetActionSeparator` | `drawer` | none (a phone's form of a Menu) |
| Overlays | Side sheet | `QeetrixSideSheet` | `drawer` | Drawer, at the side (a bottom sheet on a phone) |
| Overlays | Full-screen form | `QeetrixFullScreenForm` | `dialog` | none (Dialog's `fullscreen` size is the nearest) |
| Overlays | Menu | `QeetrixMenu`, `QeetrixMenuItem`, `QeetrixMenuCheckboxItem`, `QeetrixMenuRadioGroup`, `QeetrixMenuRadioItem`, `QeetrixMenuGroupLabel`, `QeetrixMenuSeparator` | `dropdownmenu` | Menu and the same items |
| Navigation | Navigation bar | `QeetrixNavigationBar`, `QeetrixNavigationItem` | `navigation` | none (Sidebar's items, in a bottom bar) |
| Navigation | Navigation rail | `QeetrixNavigationRail` | `navigation` | Sidebar, as a rail (experimental) |
| Navigation | Adaptive navigation | `QeetrixNavigationScaffold` | `navigation` | AppShell, as Android adapts it (experimental) |
| Navigation | Page indicator | `QeetrixPageIndicator` | `pageindicator` | none |
| Navigation | Steps | `QeetrixSteps`, `QeetrixStep` | `steps` | Steps (experimental) |
| Navigation | Tabs | `QeetrixTabs`, `QeetrixTab` | `tabs` | Tabs, TabsList, Tab |
| Navigation | Top app bar | `QeetrixTopAppBar`, `QeetrixBackButton` | `navigation` | PageHeader, PageHeaderBack, as a phone's app bar (experimental) |
| Layout | Accordion | `QeetrixAccordion`, `QeetrixAccordionItem`, `QeetrixCollapsibleTrigger`, `QeetrixCollapsiblePanel` | `accordion` | Accordion, AccordionItem; Collapsible (CollapsibleTrigger, CollapsiblePanel) |
| Layout | Card | `QeetrixCard` | `card` | Card |
| Layout | Separator | `QeetrixSeparator` | `separator` | Separator |

Not built, by decision: a navigation drawer, a popover, a toast, a data table and image presentation.

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
  path ([quality.md](quality.md)).
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

## Formatting and haptics

- **`QeetrixFormat`** (`foundation.format`): numbers, money, percentages, dates, times, relative times and durations,
  the web's `format` rules on Android's ICU, checked against 305 cases the web's own rules write
  (`qeetrix/src/test/resources/qeetrix/format-cases.json`, from `Tools/FormatCases`).
- **Haptics by role** (`foundation.haptics`): `rememberQeetrixHaptics()` plays a `QeetrixHapticRole` (what a moment
  means, never a pattern), and the system's touch-feedback setting is always respected.
