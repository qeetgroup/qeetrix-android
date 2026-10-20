# ui.qeet.in handoff: Android data display, collections and content states

For whoever maintains the component pages on ui.qeet.in (`qeetrix-docs`). It lists what Qeetrix for
Android now provides for each web component in the Data display, Feedback, Layout and Forms groups that
Phase 5 covers, so a page can gain an **Android** section. Nothing here has been published: qeetrix-android
has no release, and nothing should be documented as installable until it does.

The source of truth for wording is each component's page in `Documentation/Components/` and its KDoc
(`./gradlew :qeetrix:dokkaGenerate`). The decisions are in
[ADR 0016](../Decisions/0016-data-display-and-content-states.md); the rules for identity, selection,
search, filters and content states are in
[Standards/DATA_DISPLAY_AND_COLLECTIONS.md](../Standards/DATA_DISPLAY_AND_COLLECTIONS.md).

## What to add, page by page

| ui.qeet.in page | Android API | One-line summary for the page |
|:--|:--|:--|
| Description List | `QeetrixDescriptionList`, `QeetrixDescriptionItem`, `QeetrixDescriptionListLayout` | The web's three layouts and `divided`, switching at 640 dp of window width; each pair read as one element. |
| Accordion | `QeetrixAccordion`, `QeetrixAccordionItem` | The web's item, chevron and 200 ms motion; the app owns which items are open. |
| Collapsible | `QeetrixAccordionItem` on its own | One section that shows and hides its content. |
| Empty State | `QeetrixEmptyState`, `QeetrixEmptyStateVariant`, `QeetrixEmptyStateSize` | The same five variants and three sizes; the words and actions are the app's. |
| Data State | `QeetrixDataState`, `QeetrixDataFailure`, `QeetrixDataLoadingRows` | Loading, failure, empty, content in the web's order; a failure shows the app's message and "Try again". |
| Skeleton | `QeetrixSkeleton`, `QeetrixSkeletonShape`, `QeetrixSkeletonText` | The same tint and 1.6 s shimmer, still with animations off, invisible to TalkBack. |
| Chip | `QeetrixChip`, `QeetrixChipGroup`, `QeetrixChipSize` | Toggle, single or multiple groups, and removable chips, in the web's three sizes. |
| Timeline | `QeetrixTimeline`, `QeetrixTimelineItem`, `QeetrixTimelineTone`, `QeetrixTimelineEmphasis` | The same tones, icon markers, minor events and timeline tokens. |

Android only, with no ui.qeet.in page today — mention them on a native or collections page if one exists:

- **List item** (`QeetrixListItem`, `QeetrixListItemIcon`) and **Section header** (`QeetrixSectionHeader`):
  rows and group headings for Compose's lazy lists. iOS has the same names.
- **Search field** (`QeetrixSearchField`): a field with a search icon, a clear button and the keyboard's
  search key; iOS uses the system's `searchable`.

Not on Android, by decision — say so on those pages if they gain an Android section:

- **Data Table** and **Table**: a list of rows opening a description list serves a phone; a tablet table is
  a later decision.
- **Aspect Ratio**: `Modifier.aspectRatio` in Compose; Qeetrix loads no images.
- **Feed**, **Filter Bar**, **Pagination**: not in this phase.

## Examples to show

Use the examples from the component pages verbatim. They are compiled and composed by
`sample/src/test/kotlin/com/qeetgroup/qeetrix/sample/datadisplay/DataDisplayDocumentationExamplesTest.kt`.
Shortened forms, for a page that needs only one line:

```kotlin
// Description List
QeetrixDescriptionList(divided = true) { QeetrixDescriptionItem("Plan", "Team") }

// Accordion
QeetrixAccordionItem("Billing", expanded = open, onExpandedChange = { open = it }) { BillingSettings() }

// Empty State
QeetrixEmptyState("No invoices yet", variant = QeetrixEmptyStateVariant.FirstUse) { QeetrixButton("Create invoice", onClick = ::create) }

// Chip
QeetrixChipGroup(label = "Status", selected = status, onSelect = { status = it }, options = statusOptions)

// Timeline
QeetrixTimeline { QeetrixTimelineItem("Invoice sent", time = "1 week ago", tone = QeetrixTimelineTone.Brand) }
```

## Differences each page should state

Write each of these as a platform note, not as a limitation:

- **State.** The app owns the data, its identity, selection, expansion, the query and every load state;
  Qeetrix fetches, caches, filters and paginates nothing.
- **Collections.** Rows go in Compose's `LazyColumn`, keyed by the app's ids, so they keep their state as
  items move.
- **Data State.** A failure never shows an error object's message (the web's default does); "Try again"
  appears when the app passes a retry.
- **Chip.** TalkBack reads toggling chips as checkboxes and single-choice groups as radio buttons; chips
  reserve a 48 dp touch area, so wrapped rows are further apart than on the web.
- **Accordion.** TalkBack reads "Expanded" or "Collapsed" and offers expand and collapse actions; the header
  padding doesn't follow a compact density.
- **Large text.** From 150 % text, list rows and timeline events move their trailing content below the text.

## Images

Don't take screenshots from the repository: screenshot baselines are never committed
([ADR 0012](../Decisions/0012-visual-regression-testing.md)). To make images for the site, run the sample
app (`./gradlew :sample:installDebug`) on an emulator or device and open Data display → List item,
Description list, Chip, Timeline; Feedback → Empty state, Data state, Skeleton; Layout → Accordion; Forms &
inputs → Search field; and the home screen's Collections. Use the synthetic content already on those pages.

## Verification status to quote

- **Verified on the JVM (Robolectric, Android 16 runtime):** behaviour and semantics tests for every
  component, collection identity through insertion, removal and reordering, state restoration, one-row
  recomposition, screenshots in light, dark, 200 % text and right to left, and the sample's collections flow.
- **Not yet verified:**
  - TalkBack reading and actions on rows, chips, accordions and data states; Switch Access;
  - scrolling, item animations and frame timing of long lists on a device;
  - tablets and resizable windows (the description list's 640 dp switch is tested on the JVM only).

Don't describe these as tested until they are.
