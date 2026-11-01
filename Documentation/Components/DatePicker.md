# Date, date range and time fields

`QeetrixDateField`, `QeetrixDateRangeField`, `QeetrixTimeField` · `components.datepicker` · `@qeetrix/react`: DatePicker, DateRangePicker · QTRX-120, QTRX-128

A date, a range of dates or a time, chosen in Android's own pickers in Qeetrix's colours and type.

```kotlin
QeetrixDateField(dueDate, { dueDate = it }, label = "Due date", min = LocalDate.now())
QeetrixTimeField(time, { time = it }, label = "Reminder", minuteStep = 15)
```

## Look, behaviour and accessibility

- A read-only Qeetrix field showing the value in the locale's words ([formatting](../Foundations/FORMAT.md)) with a `calendar` or `clock` icon; the whole field is one button ("Choose date").
- It opens Material 3's DatePicker, DateRangePicker or TimePicker inside a [dialog](Dialog.md), under a Material theme mapped from Qeetrix's colours, type and corners (internal: no Material type in the API). The calendar starts on the locale's first day of the week.
- `min`, `max` and `isDateAllowed` limit the dates; the time follows the device's 12/24-hour setting and rounds to `minuteStep`.
- Not the web's Calendar drawing: the platform's pickers, as the ticket asks where they can take Qeetrix's colours.

Tried on the phone emulator (`qeetrix-api36`, Android 16) on 31 October 2026, in light, dark, 200 % text and right to left, from the sample app's page. Found there and fixed: in a phone's dialog Material's 360 dp calendar cut off every Saturday — it now takes the dialog's whole width and is scaled to fit below 360 dp; and Material colour roles left at their defaults leaked pink into the pickers (the chosen AM / PM) — every role is now Qeetrix's, both checked by `QeetrixDateFieldTest`.
