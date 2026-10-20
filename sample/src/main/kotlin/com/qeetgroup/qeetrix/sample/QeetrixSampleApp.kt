package com.qeetgroup.qeetrix.sample

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
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
import com.qeetgroup.qeetrix.components.badge.QeetrixChipGroup
import com.qeetgroup.qeetrix.components.card.QeetrixCard
import com.qeetgroup.qeetrix.components.card.QeetrixCardSize
import com.qeetgroup.qeetrix.components.input.QeetrixOption
import com.qeetgroup.qeetrix.components.navigation.QeetrixBackButton
import com.qeetgroup.qeetrix.components.navigation.QeetrixTopAppBar
import com.qeetgroup.qeetrix.components.switchcontrol.QeetrixSwitch
import com.qeetgroup.qeetrix.components.typography.QeetrixText
import com.qeetgroup.qeetrix.components.typography.QeetrixTextVariant
import com.qeetgroup.qeetrix.foundation.sizing.QeetrixDensity
import com.qeetgroup.qeetrix.foundation.spacing.QeetrixSpacing
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
 * death.
 */
@Composable
fun QeetrixSampleApp() {
    var modeValue by rememberSaveable { mutableStateOf(QeetrixThemeMode.System.value) }
    var densityValue by rememberSaveable { mutableStateOf(QeetrixDensity.Default.value) }
    var rightToLeft by rememberSaveable { mutableStateOf(false) }
    var largeText by rememberSaveable { mutableStateOf(false) }
    var route by rememberSaveable { mutableStateOf(HOME) }

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
                SampleScaffold("Foundations", back) { FoundationsScreen() }
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
 * The page frame: a [QeetrixTopAppBar] with the page title and a back button (except on the home screen),
 * then the scrolling page. The bar draws under the status bar and keeps its content clear of it; its rule
 * appears once the page scrolls under it.
 */
@Composable
private fun SampleScaffold(
    title: String?,
    onBack: (() -> Unit)?,
    content: @Composable ColumnScope.() -> Unit,
) {
    val scroll = rememberScrollState()
    Column(Modifier.fillMaxSize().background(QeetrixTheme.colors.surfaceCanvas)) {
        if (title != null && onBack != null) {
            QeetrixTopAppBar(title, navigationIcon = { QeetrixBackButton(onClick = onBack) }, scrolled = scroll.canScrollBackward)
        }
        Column(
            Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.safeDrawing)
                .verticalScroll(scroll)
                .padding(QeetrixSpacing.L),
            content = content,
        )
    }
}

@Composable
private fun HomeScreen(
    settings: DisplaySettings,
    onSettingsChange: (DisplaySettings) -> Unit,
    onOpen: (String) -> Unit,
) {
    SamplePage {
        Column(verticalArrangement = Arrangement.spacedBy(QeetrixSpacing.Xs)) {
            QeetrixText("Qeetrix for Android", variant = QeetrixTextVariant.H2)
            QeetrixText(
                "${QeetrixProvenance.tokensPackage} ${QeetrixProvenance.tokensVersion} · " +
                    "font scale ${"%.2f".format(LocalDensity.current.fontScale)}",
                variant = QeetrixTextVariant.Muted,
            )
        }
        DisplayCard(settings, onSettingsChange)
        SampleSection("Overview") {
            QeetrixCard(
                onClick = { onOpen(FOUNDATIONS) },
                size = QeetrixCardSize.Sm,
                title = "Foundations",
                description = "Colours, typography, spacing, corners, elevation and icons: the tokens themselves.",
            )
            QeetrixCard(
                onClick = { onOpen(COMPOSITION) },
                size = QeetrixCardSize.Sm,
                title = "Composition",
                description = "Several components working together in one settings form, with live state.",
            )
            QeetrixCard(
                onClick = { onOpen(ACCOUNT_PREFERENCES) },
                size = QeetrixCardSize.Sm,
                title = "Account preferences",
                description = "Every input in one generic form: profile, a password change with a code, preferences, feedback.",
            )
            QeetrixCard(
                onClick = { onOpen(WORKSPACE) },
                size = QeetrixCardSize.Sm,
                title = "Workspace",
                description = "Navigation, a top app bar, a menu, a dialog, a confirmation, a bottom sheet and snackbars in one flow.",
            )
            QeetrixCard(
                onClick = { onOpen(COLLECTIONS) },
                size = QeetrixCardSize.Sm,
                title = "Collections",
                description = "Search, filter chips, sections, selectable rows, loading, empty and failed states, and a detail screen.",
            )
        }
        for (category in SampleCategory.entries) {
            val entries = SampleCatalog.filter { it.category == category }
            if (entries.isEmpty()) continue
            SampleSection(category.title) {
                for (entry in entries) {
                    QeetrixCard(onClick = { onOpen(entry.id) }, size = QeetrixCardSize.Sm, title = entry.title)
                }
            }
        }
    }
}

@Composable
private fun DisplayCard(
    settings: DisplaySettings,
    onChange: (DisplaySettings) -> Unit,
) {
    QeetrixCard(title = "Display", description = "Applies to every page.") {
        Column(verticalArrangement = Arrangement.spacedBy(QeetrixSpacing.M)) {
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

/** A single choice from a few options, as chips: its visible label, then the group it names. */
@Composable
private fun <T> Choice(
    label: String,
    options: List<T>,
    selected: T,
    name: (T) -> String,
    onSelect: (T) -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(QeetrixSpacing.Xs)) {
        QeetrixText(label, variant = QeetrixTextVariant.Small)
        QeetrixChipGroup(label = label, selected = selected, onSelect = onSelect, options = options.map { QeetrixOption(it, name(it)) })
    }
}
