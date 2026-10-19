# Form

`QeetrixFormActions`, `QeetrixFormErrorSummary` · `components.form` · web: [Form](https://ui.qeet.in/docs/components/form) ·
sample: Forms & inputs → Form; Overview → Account preferences

Patterns for whole forms. Qeetrix owns how a form looks and how its problems are presented; the app owns
the values, the rules, submitting and saving.

```kotlin
Column(verticalArrangement = Arrangement.spacedBy(QeetrixTheme.density.fieldGap)) {
    QeetrixFormErrorSummary(errors, Modifier.focusRequester(summaryFocus))
    QeetrixTextField(name, Modifier.focusRequester(nameFocus), label = "Full name", required = true, message = nameMessage)
    QeetrixFormActions {
        QeetrixButton("Cancel", onClick = ::cancel, variant = QeetrixButtonVariant.Outline)
        QeetrixButton("Save", onClick = ::save, loading = saving, loadingLabel = "Saving…")
    }
}
```

## The pattern

- **Layout.** A form is a `Column` spaced by the density's field gap; related fields go in a
  [`QeetrixFieldSet`](Field.md). There is deliberately no form container or controller.
- **Actions.** `QeetrixFormActions`: under 640 dp the buttons stack full width with the last (primary) on
  top; from 640 dp they sit in a row at the end. Put the secondary first.
- **Validation.** Show each field's `message`. After a failed submit, show `QeetrixFormErrorSummary` with
  one `QeetrixFormError` per problem, and focus it; each error's action focuses its field
  (`requester.requestFocus()` — a focused field scrolls itself into view).
- **Focus and keyboard.** Give each field a `FocusRequester` through its `modifier`; set the IME action
  (Next, Done) in its keyboard options. Disabled and loading buttons cannot submit twice.

## Accessibility

The summary is announced at once (an assertive live region), its title is a heading, it is focusable for
a programmatic move, and each problem is a full 48 dp button. Messages carry icons and words, not colour
alone.

## Theme and tokens

Summary: `feedback.error-subtle`, `border.danger`, `text.danger`, `corner.control` (radius-lg), the
`circle-alert` icon; actions: `density.field-gap`, the `sm` breakpoint (640 dp).

## Platform differences

The web summary links to controls by id and its form can focus the first invalid control on submit; on
Android each error carries its own action, and the app moves focus. The links are 48 dp rows, as iOS makes
them 44 pt, so neighbouring links are never mistaken.

Related: [Field](Field.md), [Button](Button.md).
