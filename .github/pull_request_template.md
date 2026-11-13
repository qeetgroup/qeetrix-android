## 📝 What and why

<!-- What changes, and why. One topic per pull request. -->

## 🔗 Related

- Jira: QTRX-
- Upstream change, if any: <!-- the @qeetrix/tokens, @qeetrix/react or @qeetrix/icons version or PR -->

## 🧩 Type of change

- [ ] 🐛 Fix
- [ ] ✨ New component or feature
- [ ] 💥 Breaking change to the public API
- [ ] 🎨 Upstream bump (tokens, components or icons)
- [ ] 🛠️ Build, CI or docs only

## 📸 Screenshots

<!-- For a visual change: before and after, in light and dark. Delete this section otherwise. -->

## ✅ Checklist

- [ ] `./gradlew check` passes
- [ ] New behaviour has a test that fails without the change
- [ ] **Public API change:** KDoc written, `./gradlew :qeetrix:apiDump` run, and the CHANGELOG updated
- [ ] **Visual change:** `./gradlew :qeetrix:verifyRoborazziDebug` passes, or the screenshots are re-recorded and every
      changed image reviewed
- [ ] **New component:** its tests, screenshot test, sample page and row in `docs/components.md`
- [ ] Nothing in `Upstream/` or a `generated/` folder is edited by hand
- [ ] Docs match the change (CHANGELOG, component page, decision record if structural)

## 🚀 Release (pull requests into `main` only)

- [ ] `qeetrix.version` in `gradle.properties` is the version to release, raised on develop when the library changes.
      The Release gate fails until it's raised.
- [ ] The CHANGELOG has an entry for that version
