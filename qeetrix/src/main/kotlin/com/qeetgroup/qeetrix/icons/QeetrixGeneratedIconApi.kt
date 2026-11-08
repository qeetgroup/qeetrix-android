package com.qeetgroup.qeetrix.icons

/**
 * Marks [QeetrixIconName]'s constructor, which builds an icon from `@qeetrix/icons` drawing data in the generator's
 * encoding. Only Qeetrix's generated catalogues call it: the core icons and `qeetrix-icons-extended`. An app names an
 * icon from them instead — `QeetrixIconName.Send` — so every icon keeps its upstream drawings and right-to-left meaning.
 */
@RequiresOptIn(
    message = "For Qeetrix's generated icon catalogues. Name a catalogue icon instead: QeetrixIconName.Send.",
    level = RequiresOptIn.Level.ERROR,
)
@Retention(AnnotationRetention.BINARY)
@Target(AnnotationTarget.CONSTRUCTOR)
public annotation class QeetrixGeneratedIconApi
