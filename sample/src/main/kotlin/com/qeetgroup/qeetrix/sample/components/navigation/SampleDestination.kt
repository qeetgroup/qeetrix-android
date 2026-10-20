package com.qeetgroup.qeetrix.sample.components.navigation

import com.qeetgroup.qeetrix.components.navigation.QeetrixNavigationItem
import com.qeetgroup.qeetrix.icons.QeetrixIconName

/** The sample app's own destinations: an enum the app owns, as a route type would be. */
enum class SampleDestination(
    val title: String,
) {
    Home("Home"),
    Inbox("Inbox"),
    Activity("Activity"),
    Settings("Settings"),
}

/** The destinations as navigation items, with a synthetic unread count on the inbox. */
fun sampleDestinations(unread: Int = 3): List<QeetrixNavigationItem<SampleDestination>> =
    listOf(
        QeetrixNavigationItem(SampleDestination.Home, "Home", QeetrixIconName.House),
        QeetrixNavigationItem(SampleDestination.Inbox, "Inbox", QeetrixIconName.Inbox, badge = unread.takeIf { it > 0 }?.toString()),
        QeetrixNavigationItem(SampleDestination.Activity, "Activity", QeetrixIconName.Bell),
        QeetrixNavigationItem(SampleDestination.Settings, "Settings", QeetrixIconName.Settings),
    )
