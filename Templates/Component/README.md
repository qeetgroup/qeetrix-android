# Component template

`Scripts/new-component` renders these files for a new component, at the paths in
[COMPONENT_CONTRACT.md](../../Documentation/Standards/COMPONENT_CONTRACT.md):

```sh
Scripts/new-component <Family> <Component> --category "<group>" [--slug <part>] [--dry-run] [--root <repository>]

Scripts/new-component Slider Slider --category "Forms & inputs" --dry-run
Scripts/new-component Toggle ToggleGroup --category "Actions" --dry-run
Scripts/new-component Slider RangeSlider --category "Forms & inputs" --slug Slider --dry-run
```

`<Family>` is the component's `@qeetrix/react` family in the pinned
`Upstream/qeetrix-react/component-manifest.json`, in UpperCamelCase (the manifest's `name`: `button` → `Button`,
`date-picker` → `DatePicker`). `--slug` is the part of that family the component implements (one of its `parts`,
such as `IconButton`); it defaults to the component name, and its kebab case is the ui.qeet.in slug. `--category` is
one of the sample's groups: Actions, Forms & inputs, Date & time, Data display, Feedback, Overlays, Navigation,
Layout, Utilities.

| Template | Becomes |
|:--|:--|
| `Component.kt.template` | `qeetrix/src/main/kotlin/com/qeetgroup/qeetrix/components/<package>/Qeetrix<Component>.kt` |
| `ComponentTest.kt.template` | `qeetrix/src/test/kotlin/com/qeetgroup/qeetrix/components/<package>/Qeetrix<Component>Test.kt` |
| `ComponentScreenshotTest.kt.template` | `qeetrix/src/test/kotlin/com/qeetgroup/qeetrix/components/<package>/Qeetrix<Component>ScreenshotTest.kt` |
| `ComponentSample.kt.template` | `sample/src/main/kotlin/com/qeetgroup/qeetrix/sample/components/<group>/<Component>Sample.kt` |
| `Component.md.template` | `Documentation/Components/<Component>.md` |

It also registers the sample page in `SampleCatalog.kt` (above its `new-component` marker), lists the
component in `Documentation/Components/README.md` under its group, and — when `@qeetrix/tokens` defines
`component.<slug or family>` tokens Android has not adopted — adds that group to `qeetrixTokens.componentTokens` in
`Upstream/manifest.json` (run `Scripts/generate-tokens` afterwards). `<package>` is the family in
lower case, with `control` appended to a Java keyword (`switch` → `switchcontrol`). A family Android implements for
the first time also needs its CSS: add it to `qeetrixReact.families` in `Upstream/manifest.json` and run
`Scripts/sync-tokens`, then restate the values the component draws from `Upstream/qeetrix-react/css/<family>.css`,
with the CSS beside each one.

## Guarantees

- **Validated.** Names are UpperCamelCase ASCII without the `Qeetrix` prefix; the family and its part must exist
  in the pinned `@qeetrix/react` manifest, and the group must be one of the sample's.
- **Never overwrites.** The whole change set is planned first. If any file exists, `Qeetrix<Component>`
  is already declared, or the catalogue or index already lists it, the run stops with every reason and
  changes nothing. Each file is checked again just before it is written.
- **Deterministic.** Output depends only on the arguments and the repository's files.
- **Dry run.** `--dry-run` lists every change and writes nothing; `--root` points the command at a copy
  of the repository for a trial.

## On purpose, the scaffold does not build or pass

The component and its sample page call a `scaffold()` marked `@Deprecated(level = ERROR)`, so the
build fails with the reason until they are implemented. The behaviour test fails with a reminder until
it is replaced, and the screenshot test has no baseline, so verification fails until images are
recorded and reviewed. Nothing in the scaffold passes for the wrong reason, and it adds no tokens class:
component tokens are generated only from `@qeetrix/tokens`' own groups.

Placeholders: `{{Family}}`, `{{Component}}`, `{{component}}` (lower case, the screenshot folder),
`{{package}}`, `{{slug}}`, `{{title}}` (sentence case), `{{name}}` (the `@qeetrix/react` part) and
`{{description}}` ("The Android counterpart of `@qeetrix/react`'s …", from the manifest's entry, status and `since`), `{{category}}`, `{{categoryPackage}}`, `{{tokens}}` and `{{tokensDoc}}`.
