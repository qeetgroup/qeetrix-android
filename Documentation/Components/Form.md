# Form

`QeetrixForm`, `QeetrixFormErrorSummary`, `QeetrixFormActions` · `components.form` · `@qeetrix/react`: Form,
ErrorSummary (`experimental`) ([Form](https://ui.qeet.in/components/form)) · sample: Forms → Form

A form's column, the summary of what to fix after a failed submit (GOV.UK's error summary), and the row of
buttons that ends it.

```kotlin
QeetrixForm(errors = problems) {
    QeetrixTextField(gstin, label = "GSTIN", error = gstinError, modifier = Modifier.focusRequester(gstinFocus))
    QeetrixTextField(tradeName, label = "Trade name", optional = true)
    QeetrixFormActions {
        QeetrixButton("Cancel", onClick = ::cancel, variant = QeetrixButtonVariant.Ghost, size = QeetrixButtonSize.Lg)
        QeetrixButton("Save", onClick = ::submit, size = QeetrixButtonSize.Lg)
    }
}
// problems = listOf(QeetrixFormError("Enter a GSTIN, like 27AAPFU0939F1ZV.") { gstinFocus.requestFocus() })
```

## API

| `@qeetrix/react` | Android |
|:--|:--|
| Form: fields `space.form-gap` apart | `QeetrixForm(modifier, errors, summary, summaryTitle, content)` |
| Form `summary`, `summaryTitle` | `summary = true`, `summaryTitle` |
| Form reads invalid rows from the DOM | `errors: List<QeetrixFormError>` — your validation's result |
| ErrorSummary `items: { id?, message }[]` | `items: List<QeetrixFormError>` (`message`, `onClick?`) |
| ErrorSummary `title`, `autoFocus` | `title`, `autoFocus = true` |
| `headingLevel` | — (Android headings have no level) |
| — (a form's own row) | `QeetrixFormActions`: end-aligned, wrapping, `space.gap.sm` apart |

Old `QeetrixFormErrorSummary(errors, …)` became `items`; `QeetrixFormError.onClick` is now optional (plain text
without it). `QeetrixFormActions` no longer stacks full width on a phone: it is the dialog footer's row.

## Look

- **Summary** (the danger alert): `color.bg.danger.subtle`, a 3 dp `color.icon.danger` edge on the start side,
  square, padding 12 / 8 / 12 / 12 dp; the 16 dp `circle-alert`, `space.gap.md` before the title "Fix these to
  continue" (`qx_error_summary_title`) in `label.md` at `title.sm`'s weight; each item a link (`color.text.link`,
  underlined; `color.text.link-hover` under a press). While it has focus, the Ember frame is drawn inside its edge.
- **Actions**: the primary last, so it ends the row; a form's main button is `QeetrixButtonSize.Lg`.

## Accessibility

The summary is announced as an alert; its title is a heading read as "Error: Fix these to continue". It takes focus
when it appears (and again when its items change), so the heading is read first and the keyboard goes on to its
links; it is focusable only until focus leaves it. Each item is a 48 dp row; choosing one runs its `onClick` —
move focus to the field, which scrolls it into view. Prefix the screen's title with "Error: " while it shows.

## Platform differences

- **Validation.** The web reads invalid rows from the page after a submit; Compose has no form element, so the app
  passes the errors (worded exactly as each field's, in field order).
- **Heading levels** do not exist on Android.

Related: [Field](Field.md), [Text field](TextField.md), [Button](Button.md).
