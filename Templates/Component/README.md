# Component template

`Scripts/new-component` renders these files for a new component, at the paths in
[COMPONENT_CONTRACT.md](../../Documentation/Standards/COMPONENT_CONTRACT.md):

```sh
Scripts/new-component <Family> <Component> --category "<ui.qeet.in group>" [--slug <web slug>] [--dry-run] [--root <repository>]

Scripts/new-component Slider Slider --category "Forms & inputs" --dry-run
Scripts/new-component Input InputGroup --category "Forms & inputs" --slug input-group
```

`<Family>` is the component's family in the pinned `component-manifest.json` (its `category` field);
`--slug` defaults to the component name in kebab case and must be a manifest slug of that family.
`--category` is one of ui.qeet.in's groups: Actions, Forms & inputs, Date & time, Data display,
Feedback, Overlays, Navigation, Layout, Utilities.

| Template | Becomes |
|:--|:--|
| `Component.kt.template` | `qeetrix/src/main/kotlin/com/qeetgroup/qeetrix/components/<package>/Qeetrix<Component>.kt` |
| `ComponentTest.kt.template` | `qeetrix/src/test/kotlin/com/qeetgroup/qeetrix/components/<package>/Qeetrix<Component>Test.kt` |
| `ComponentScreenshotTest.kt.template` | `qeetrix/src/test/kotlin/com/qeetgroup/qeetrix/components/<package>/Qeetrix<Component>ScreenshotTest.kt` |
| `ComponentSample.kt.template` | `sample/src/main/kotlin/com/qeetgroup/qeetrix/sample/components/<group>/<Component>Sample.kt` |
| `Component.md.template` | `Documentation/Components/<Component>.md` |

It also registers the sample page in `SampleCatalog.kt` (above its `new-component` marker), lists the
component in `Documentation/Components/README.md` under its group, and — when qeetrix-ui defines
`component.<slug or family>` tokens Android has not adopted — adds that group to
`Upstream/manifest.json` (run `Scripts/generate-tokens` afterwards). `<package>` is the family in
lower case, with `control` appended to a Java keyword (`switch` → `switchcontrol`).

## Guarantees

- **Validated.** Names are UpperCamelCase ASCII without the `Qeetrix` prefix; the family, slug and
  group must exist upstream.
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
component tokens are generated only from qeetrix-ui's own groups.

Placeholders: `{{Family}}`, `{{Component}}`, `{{component}}` (lower case, the screenshot folder),
`{{package}}`, `{{slug}}`, `{{title}}` (sentence case), `{{name}}` and `{{description}}` (from the
manifest), `{{category}}`, `{{categoryPackage}}`, `{{tokens}}` and `{{tokensDoc}}`.
