# Release-readiness gaps (Phases 6 and 7)

What stands between the library as it is on 2026-10-20 and a release a product team can adopt, each with its
owner and how it would be closed. Severity follows the Phase 6 model: **P0** blocks any use, **P1** blocks a release,
**P2** must be fixed or accepted before a release, **P3** is an improvement.

No P0 or P1 defect is open. Phase 7 found and fixed one P1 (the bottom sheet's empty panel, through the
independent consumer). Every gap below is verification not yet done, or a decision outside this repository.
Updated 2026-10-20 at the end of Phase 7.

## Verification not done

| # | Gap | Severity | Why it is open | How to close |
|:--|:--|:--|:--|:--|
| G1 | **Manual TalkBack pass** over every component and the sample's flows | P1 for a release | Needs a person listening on a device; automation checks names, roles and states, not whether the result is understandable. Phase 7 started TalkBack on an emulator but could not drive it from automation ([ACCESSIBILITY-REPORT.md](ACCESSIBILITY-REPORT.md#phase-7-additions)) | The checklist in [ACCESSIBILITY-REPORT.md](ACCESSIBILITY-REPORT.md#what-only-a-person-can-verify), on a physical phone |
| G2 | **Switch Access, Voice Access, hardware keyboard and D-pad** | P1 for a release | As G1 | As G1; keyboard on an emulator is a partial substitute |
| G3 | **Physical devices** | P2 | Only emulators (API 29 and 36, arm64) were available | One low-end and one current phone; Bold Text and the largest font sizes there |
| G4 | **ATF on overlay windows and API levels other than 36** | P2 | `enableAccessibilityChecks()` covers the test's own window; ATF did not run on API 29 | Run the device suite on API 33–35 images; check dialogs, sheet, menu and select list in TalkBack (G1) |
| G5 | **Frame timing** | P2 | Emulator timing is not representative; no Macrobenchmark module ([0017](../Decisions/0017-quality-gates-and-verification.md)) | A benchmark module and a Baseline Profile, measured on a physical device, once a product's screens exist to measure |
| G6 | **CI not yet seen green** | P2 | CI now runs device tests on API 29 and 36 on every push, but the workflow has not run since the change: nothing was pushed, and the organisation's Actions billing has blocked runs since Phase 1 | Restore Actions billing, push when authorised, and read the run |
| G7 | **IME and AutoFill on a device** | P2 | Composing text (East Asian IMEs), password managers' offers and the SMS one-time-code offer were not exercised | Manual check on a device with a password manager |
| G8 | **Predictive back and the back gesture** | P3 | The device test sends Back as a key event, so neither the gesture nor predictive back's animation was exercised | Manual check on API 34+ with gesture navigation (the API 36 emulator has it on) |
| G9 | **Foldables and resizable windows** | P3 | Phase 7 ran the consumer on an API 36 tablet emulator and a phone in landscape (both take the navigation rail); folding and window resizing were not tried | Foldable emulator profiles or a device lab |

## Decisions outside this repository

| # | Gap | Owner | Note |
|:--|:--|:--|:--|
| D1 | **Translations of the 21 built-in strings** | Product and localization | English only; no translation was invented ([LOCALIZATION-RTL-REPORT.md](LOCALIZATION-RTL-REPORT.md)) |
| D2 | **Increased-contrast colour values** | qeetrix-ui (upstream) | Android follows the web's tokens; there are no high-contrast tokens to follow yet |
| D3 | **Registry and first version** | The owner | Phase 7 built and verified `0.1.0-rc.1` and recommends a private GitHub Packages registry; nothing is published or tagged ([0018](../Decisions/0018-maven-distribution.md)) |
| D4 | **Commits** | The owner | Phases 4–7 are built on a local branch, uncommitted, so no release-candidate run has a clean revision yet; screenshots stay out of git by policy |
| D5 | **A licence for Qeetrix's own code** | The owner | Not needed inside Qeet Group; needed for any public or partner distribution ([LICENSE-COMPLIANCE.md](../Releases/LICENSE-COMPLIANCE.md)) |
| D6 | **Qeet ID Auth as the first consumer** | The Qeet ID team | It is an Expo / React Native app and cannot use a Compose library as it stands; a native Android module or a native app would be needed. Integration with it is unverified ([CONSUMER-INTEGRATION.md](../Releases/CONSUMER-INTEGRATION.md)) |

## Accepted, not gaps

- Five kinds of control are drawn under 48 dp inside or beside another control and take a 48 dp tap through
  Compose's hit testing, proven by `TouchLandingTest` and accepted by ATF on a device
  ([ACCESSIBILITY-REPORT.md](ACCESSIBILITY-REPORT.md)).
- The navigation bar's labels ellipsize at 200 % text, by design ([0015](../Decisions/0015-navigation-overlays-and-feedback.md)).
- Punctuation of an English paragraph in a right-to-left screen follows Compose's default (localization L3).
