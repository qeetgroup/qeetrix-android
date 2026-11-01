# ui.qeet.in handoff: Android data display, collections and content states

For whoever maintains the component pages on ui.qeet.in (`qeetrix-docs`). It lists what Qeetrix for
Android provides for each `@qeetrix/react` component in the Data display, Feedback and Navigation groups that
Phase 5 covers, so a page can gain an **Android** section. Nothing here has been published: qeetrix-android
has no release, and nothing should be documented as installable until it does.

The source of truth for wording is each component's page in `Documentation/Components/` and its KDoc
(`./gradlew :qeetrix:dokkaGenerate`). The decisions are in
[ADR 0016](../Decisions/0016-data-display-and-content-states.md); the rules for identity, selection,
search, filters and content states are in
[Standards/DATA_DISPLAY_AND_COLLECTIONS.md](../Standards/DATA_DISPLAY_AND_COLLECTIONS.md).

## What to add, page by page

| ui.qeet.in page | `@qeetrix/react` | Android API | One-line summary for the page |
|:--|:--|:--|:--|
| Description list | DescriptionList, DescriptionItem (experimental) | `QeetrixDescriptionList`, `QeetrixDescriptionItem`, `QeetrixDescriptionListLayout` | The web's `rows` and `columns` layouts, rows moving the term above its value below 24 rem of the list's own width; each pair read as one element. |
| Accordion | Accordion, AccordionItem | `QeetrixAccordion`, `QeetrixAccordionItem` | Ruled sections with the web's chevron and motion; the app owns `value`, the open sections. |
| Collapsible | Collapsible, CollapsibleTrigger, CollapsiblePanel | `QeetrixCollapsibleTrigger`, `QeetrixCollapsiblePanel` | One fold: a ghost button with a turning chevron and its panel, sharing the app's `open`. |
| Empty state | EmptyState (experimental) | `QeetrixEmptyState`, `QeetrixEmptyStateSize`, `QeetrixEmptyStateTone` | The same `page` and `section` sizes and `danger` tone; the words and actions are the app's. |
| Empty state (DataState) | DataState (experimental) | `QeetrixDataState` | Loading, error, empty and content in the web's order; an error shows the app's words and "Try again". |
| Skeleton | Skeleton | `QeetrixSkeleton`, `QeetrixSkeletonVariant`, `QeetrixSkeletonSize`, `QeetrixSkeletonText` | The same tint, shapes and 1.6 s pulse, still with animations off, invisible to TalkBack. |
| Timeline | Timeline, TimelineGroup, TimelineItem (experimental) | `QeetrixTimeline`, `QeetrixTimelineGroup`, `QeetrixTimelineItem`, `QeetrixTimelineVariant`, `QeetrixTimelineTone` | The same square markers, `default` and `compact` looks, tones and day groups. |
| Avatar | Avatar; AvatarItem (experimental) | `QeetrixAvatar`, `QeetrixAvatarItem`, `QeetrixAvatarSize`, `QeetrixAvatarShape` | The same initials rule, sizes and circle or square shapes; the app loads the picture. |

Android only, with no `@qeetrix/react` counterpart — mention them on a native or collections page if one exists:

- **List item** (`QeetrixListItem`, `QeetrixListItemIcon`) and **Section header** (`QeetrixSectionHeader`):
  rows and group labels for Compose's lazy lists, drawn by the Table's row rules and the Sidebar's group label.
- **Chip** (`QeetrixChip`, `QeetrixChipGroup`, `QeetrixChipSize`): toggling, single-choice, multiple-choice and
  removable tags, drawn from FilterTags' tag; `@qeetrix/react` has no Chip.
- **Search field** (`QeetrixSearchField`): `@qeetrix/react`'s experimental SearchField, which has no ui.qeet.in
  page yet — the input box with a search icon, a clear button and the keyboard's search key.

Not on Android, by decision — say so on those pages if they gain an Android section:

- **Table**: a list of rows opening a description list serves a phone; a tablet table is a later decision.
- **Tree view**, **Stat**, **Code block**, **Avatar group**: not provided.
- **Pagination**: the app's (Paging 3 or its own), with skeleton rows or a spinner in a footer item.

## Examples to show

Use the examples from the component pages verbatim;
`sample/src/test/kotlin/com/qeetgroup/qeetrix/sample/datadisplay/DataDisplayDocumentationExamplesTest.kt` compiles
and composes them. Shortened forms, for a page that needs only one line:

```kotlin
// Description list
QeetrixDescriptionList { QeetrixDescriptionItem("Plan", "Team") }

// Accordion
QeetrixAccordion(value = open, onValueChange = { open = it }) { QeetrixAccordionItem("billing", "Billing") { BillingSettings() } }

// Empty state
QeetrixEmptyState("No invoices yet", icon = QeetrixIconName.FileText) { QeetrixButton("Create invoice", onClick = ::create) }

// Timeline
QeetrixTimeline { QeetrixTimelineItem("Invoice sent", time = "1 week ago", tone = QeetrixTimelineTone.Brand) }
```

## Differences each page should state

Write each of these as a platform note, not as a limitation:

- **State.** The app owns the data, its identity, selection, expansion, the query and every load state;
  Qeetrix fetches, caches, filters and paginates nothing.
- **Collections.** Rows go in Compose's `LazyColumn`, keyed by the app's ids, so they keep their state as
  items move. A timeline in a lazy list is list items: a line can't join separate lazy items.
- **Data state.** An error never shows an error object's message; "Try again" appears when the app passes
  `onRetry`.
- **Accordion.** A closed panel leaves the composition, where the web keeps it in the page for find in page;
  Android has no heading levels (`headingLevel`).
- **Description list.** rem is 16 sp, so the 8 rem term column and the 24 rem switch grow with the user's text
  size, as the web's do with the browser's font size.
- **Avatar.** The app loads and caches the picture and passes a `Painter`; there is no URL loading.
- **Large text.** From 150 % text, list rows and timeline events move their trailing content below the text.

## Images

Don't take screenshots from the repository: screenshot baselines are never committed
([ADR 0012](../Decisions/0012-visual-regression-testing.md)). To make images for the site, run the sample
app (`./gradlew :sample:installDebug`) on an emulator or device and open Data display → Avatar, List item,
Description list, Chip, Timeline; Feedback → Empty state, Data state, Skeleton; Layout → Accordion; Forms &
inputs → Search field; and the home screen's Collections. Use the synthetic content already on those pages.

## Verification status to quote

- **Verified on the JVM (Robolectric, Android 16 runtime):** behaviour and semantics tests for every
  component, collection identity through insertion, removal and reordering, state restoration, one-row
  recomposition, screenshots in light, dark, 200 % text and right to left, and the sample's collections flow.
- **Not yet verified:**
  - TalkBack reading and actions on rows, chips, accordions and data states; Switch Access;
  - scrolling, item animations and frame timing of long lists on a device;
  - tablets and resizable windows.

Don't describe these as tested until they are.
