package com.qeetgroup.qeetrix.sample

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.qeetgroup.qeetrix.components.badge.QeetrixChipGroup
import com.qeetgroup.qeetrix.components.card.QeetrixCard
import com.qeetgroup.qeetrix.components.input.QeetrixField
import com.qeetgroup.qeetrix.components.input.QeetrixOption
import com.qeetgroup.qeetrix.components.list.QeetrixListItem
import com.qeetgroup.qeetrix.components.navigation.QeetrixBackButton
import com.qeetgroup.qeetrix.components.navigation.QeetrixTopAppBar
import com.qeetgroup.qeetrix.components.switchcontrol.QeetrixSwitch
import com.qeetgroup.qeetrix.components.typography.QeetrixHeading
import com.qeetgroup.qeetrix.components.typography.QeetrixHeadingSize
import com.qeetgroup.qeetrix.components.typography.QeetrixText
import com.qeetgroup.qeetrix.components.typography.QeetrixTextTone
import com.qeetgroup.qeetrix.foundation.sizing.QeetrixDensity
import com.qeetgroup.qeetrix.foundation.spacing.QeetrixSpace
import com.qeetgroup.qeetrix.icons.QeetrixIcon
import com.qeetgroup.qeetrix.icons.QeetrixIconName
import com.qeetgroup.qeetrix.sample.composition.AccountPreferencesScreen
import com.qeetgroup.qeetrix.sample.composition.CollectionsScreen
import com.qeetgroup.qeetrix.sample.composition.CompositionScreen
import com.qeetgroup.qeetrix.sample.composition.WorkspaceScreen
import com.qeetgroup.qeetrix.sample.foundations.FoundationsScreen
import com.qeetgroup.qeetrix.theme.LocalQeetrixDensity
import com.qeetgroup.qeetrix.theme.QeetrixTheme
import com.qeetgroup.qeetrix.theme.QeetrixThemeMode
import com.qeetgroup.qeetrix.tokens.QeetrixProvenance

private const val HOME = "home"
private const val FOUNDATIONS = "foundations"
private const val COMPOSITION = "composition"
private const val ACCOUNT_PREFERENCES = "account-preferences"
private const val WORKSPACE = "workspace"
private const val COLLECTIONS = "collections"

/** What the home screen's Display card sets for every page. */
data class DisplaySettings(
    val mode: QeetrixThemeMode = QeetrixThemeMode.System,
    val density: QeetrixDensity = QeetrixDensity.Default,
    val rightToLeft: Boolean = false,
    val largeText: Boolean = false,
)

/**
 * The sample's root: display settings that apply to every page, and one level of navigation from the
 * home screen to a page (system back returns). Settings are stored as the values an app would store —
 * the theme mode and density as their upstream strings — and survive configuration changes and process
 * death. [startRoute] opens a page first (a catalogue id such as `otp-code`), and [startRightToLeft] starts with the
 * right-to-left layout on, for trying one on a device.
 */
@Composable
fun QeetrixSampleApp(
    startRoute: String? = null,
    startRightToLeft: Boolean = false,
) {
    var modeValue by rememberSaveable { mutableStateOf(QeetrixThemeMode.System.value) }
    var densityValue by rememberSaveable { mutableStateOf(QeetrixDensity.Default.value) }
    var rightToLeft by rememberSaveable { mutableStateOf(startRightToLeft) }
    var largeText by rememberSaveable { mutableStateOf(false) }
    var route by rememberSaveable { mutableStateOf(startRoute ?: HOME) }

    val settings =
        DisplaySettings(
            mode = QeetrixThemeMode.fromValue(modeValue) ?: QeetrixThemeMode.System,
            density = QeetrixDensity.entries.firstOrNull { it.value == densityValue } ?: QeetrixDensity.Default,
            rightToLeft = rightToLeft,
            largeText = largeText,
        )
    SampleDisplay(settings) {
        BackHandler(enabled = route != HOME) { route = HOME }
        val back = { route = HOME }
        when (route) {
            FOUNDATIONS -> {
                // Its own layout for wide windows: two columns from breakpoint.medium.
                SampleScaffold("Foundations", back, wide = true) { FoundationsScreen() }
            }

            COMPOSITION -> {
                SampleScaffold("Composition", back) { CompositionScreen() }
            }

            ACCOUNT_PREFERENCES -> {
                SampleScaffold("Account preferences", back) { AccountPreferencesScreen() }
            }

            // A whole app screen: it draws its own bars and needs the full window.
            WORKSPACE -> {
                WorkspaceScreen(onExit = back)
            }

            COLLECTIONS -> {
                CollectionsScreen(onExit = back)
            }

            else -> {
                val entry = SampleCatalog.firstOrNull { it.id == route }
                if (entry != null) {
                    SampleScaffold(entry.title, back) { entry.content() }
                } else {
                    SampleScaffold(title = null, onBack = null) {
                        HomeScreen(
                            settings = settings,
                            onSettingsChange = {
                                modeValue = it.mode.value
                                densityValue = it.density.value
                                rightToLeft = it.rightToLeft
                                largeText = it.largeText
                            },
                            onOpen = { route = it },
                        )
                    }
                }
            }
        }
    }
}

/** Applies [settings] to [content]: the theme, the control density, the layout direction and the font scale. */
@Composable
fun SampleDisplay(
    settings: DisplaySettings,
    content: @Composable () -> Unit,
) {
    val density = LocalDensity.current
    CompositionLocalProvider(
        LocalLayoutDirection provides if (settings.rightToLeft) LayoutDirection.Rtl else LocalLayoutDirection.current,
        LocalDensity provides if (settings.largeText) Density(density.density, density.fontScale * 2f) else density,
        LocalQeetrixDensity provides settings.density,
    ) {
        QeetrixTheme(settings.mode, content)
    }
}

/**
 * The page frame: a [QeetrixTopAppBar] with the page title and a back button (except on the home screen), then
 * the scrolling page with the phone's 16 dp gutter (`space.inset.lg`). The bar draws under the status bar and
 * keeps its content clear of it, so the page below it consumes the top inset and keeps clear of the other edges only.
 * On a large window the page is a column of at most [NarrowPage] at its start, as the web's narrow page content, unless
 * it is [wide] and lays itself out for the width.
 */
@Composable
private fun SampleScaffold(
    title: String?,
    onBack: (() -> Unit)?,
    wide: Boolean = false,
    content: @Composable ColumnScope.() -> Unit,
) {
    val bar = title != null && onBack != null
    Column(Modifier.fillMaxSize().background(QeetrixTheme.colors.surfaceCanvas)) {
        if (bar) {
            QeetrixTopAppBar(title, navigationIcon = { QeetrixBackButton(onClick = onBack) })
        }
        val insets =
            if (bar) WindowInsets.safeDrawing.only(WindowInsetsSides.Horizontal + WindowInsetsSides.Bottom) else WindowInsets.safeDrawing
        Column(
            Modifier
                .fillMaxSize()
                // The bar has kept clear of the status bar and cutout, so the page consumes that inset: a component on it
                // that keeps clear of the window's edges itself (the navigation rail) then adds no status-bar gap.
                .then(if (bar) Modifier.consumeWindowInsets(WindowInsets.safeDrawing.only(WindowInsetsSides.Top)) else Modifier)
                .windowInsetsPadding(insets)
                .verticalScroll(rememberScrollState())
                .padding(QeetrixSpace.InsetLg),
        ) {
            Column((if (wide) Modifier else Modifier.widthIn(max = NarrowPage)).fillMaxWidth(), content = content)
        }
    }
}

/**
 * `.qx-page-content[data-width="narrow"] { max-inline-size: 800px }` (page-header.css): on a tablet a page stretched to
 * the window's 1280 dp put field ends and row metadata a screen apart (found on the API 36 tablet).
 */
private val NarrowPage = 800.dp

@Composable
private fun HomeScreen(
    settings: DisplaySettings,
    onSettingsChange: (DisplaySettings) -> Unit,
    onOpen: (String) -> Unit,
) {
    SamplePage {
        Column(verticalArrangement = Arrangement.spacedBy(QeetrixSpace.GapXs)) {
            QeetrixHeading("Qeetrix for Android", level = 1, size = QeetrixHeadingSize.HeadingSm)
            QeetrixText(
                "${QeetrixProvenance.react.packageName} ${QeetrixProvenance.react.version} · " +
                    "${QeetrixProvenance.tokens.packageName} ${QeetrixProvenance.tokens.version} · " +
                    "${QeetrixProvenance.icons.packageName} ${QeetrixProvenance.icons.version} · " +
                    "font scale ${"%.2f".format(LocalDensity.current.fontScale)}",
                tone = QeetrixTextTone.Subtle,
            )
        }
        DisplayCard(settings, onSettingsChange)
        // Places to go are list rows, not clickable cards: a card is not a click target (06 §9, QTRX-182).
        SampleSection("Overview") {
            for ((title, description, route) in Overview) OverviewRow(title, description) { onOpen(route) }
        }
        for (category in SampleCategory.entries) {
            val entries = SampleCatalog.filter { it.category == category }
            if (entries.isEmpty()) continue
            SampleSection(category.title) {
                QeetrixCard {
                    for (entry in entries) QeetrixListItem(entry.title, onClick = { onOpen(entry.id) }, trailing = { Chevron() })
                }
            }
        }
    }
}

/** The sample's own screens, each a place to open from the home screen. */
private val Overview =
    listOf(
        Triple(
            "Foundations",
            "Colour roles, type roles, space, radius, borders, shadows, motion, densities and icons: the tokens themselves.",
            FOUNDATIONS,
        ),
        Triple("Composition", "Several components working together in one settings form, with live state.", COMPOSITION),
        Triple(
            "Account preferences",
            "Every input in one generic form: profile, a password change with a code, preferences, feedback.",
            ACCOUNT_PREFERENCES,
        ),
        Triple(
            "Workspace",
            "Navigation, a top app bar, a menu, a dialog, a confirmation, a bottom sheet and snackbars in one flow.",
            WORKSPACE,
        ),
        Triple(
            "Collections",
            "Search, filter chips, sections, selectable rows, loading, empty and failed states, and a detail screen.",
            COLLECTIONS,
        ),
    )

/** A place in the sample to open: one list row on its own surface. */
@Composable
private fun OverviewRow(
    title: String,
    description: String,
    onClick: () -> Unit,
) {
    QeetrixCard { QeetrixListItem(title, description = description, onClick = onClick, trailing = { Chevron() }) }
}

/** The row's sign that it opens a page: a chevron, mirrored in right to left. */
@Composable
private fun Chevron() {
    QeetrixIcon(QeetrixIconName.ChevronRight, contentDescription = null, mirrorInRightToLeft = true)
}

@Composable
private fun DisplayCard(
    settings: DisplaySettings,
    onChange: (DisplaySettings) -> Unit,
) {
    QeetrixCard(title = "Display", description = "Applies to every page.") {
        Column(verticalArrangement = Arrangement.spacedBy(QeetrixTheme.density.spaceFormGap)) {
            Choice("Theme", QeetrixThemeMode.entries, settings.mode, { it.value }) { onChange(settings.copy(mode = it)) }
            Choice("Control density", QeetrixDensity.entries, settings.density, { it.value }) { onChange(settings.copy(density = it)) }
            QeetrixSwitch(
                checked = settings.rightToLeft,
                onCheckedChange = { onChange(settings.copy(rightToLeft = it)) },
                label = "Right-to-left layout",
                description = "Mirrors every page, as an Arabic or Hebrew locale does.",
            )
            QeetrixSwitch(
                checked = settings.largeText,
                onCheckedChange = { onChange(settings.copy(largeText = it)) },
                label = "Double the text size",
                description = "On top of the system font size, scaled linearly; the system setting uses Android's curve.",
            )
        }
    }
}

/** A single choice from a few options, as chips under a field label: the field names the group for TalkBack. */
@Composable
private fun <T> Choice(
    label: String,
    options: List<T>,
    selected: T,
    name: (T) -> String,
    onSelect: (T) -> Unit,
) {
    QeetrixField(label = label) {
        QeetrixChipGroup(
            label = label,
            value = selected,
            onValueChange = onSelect,
            options = options.map { QeetrixOption(it, name(it)) },
            modifier = Modifier.qeetrixFieldControl(),
        )
    }
}
