package com.shifthackz.aisdv1.presentation.screen.storageusage

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.shifthackz.aisdv1.core.localization.Localization
import com.shifthackz.aisdv1.core.model.asUiText
import com.shifthackz.aisdv1.core.mvi.MviComponent
import com.shifthackz.aisdv1.presentation.di.initKoin
import com.shifthackz.aisdv1.presentation.navigation.router.StorageUsageRouter
import com.shifthackz.aisdv1.presentation.screen.storageusage.model.StorageUsageIntent
import com.shifthackz.aisdv1.presentation.screen.storageusage.model.StorageUsageModal
import com.shifthackz.aisdv1.presentation.screen.storageusage.model.StorageUsageState
import com.shifthackz.aisdv1.presentation.screen.storageusage.platform.rememberStorageUsagePlatformActions
import com.shifthackz.aisdv1.presentation.widget.usage.UsageScreenContent
import com.shifthackz.aisdv1.presentation.widget.usage.summaryTitle
import com.shifthackz.aisdv1.presentation.widget.usage.title
import com.shifthackz.aisdv1.presentation.model.UsageCategory
import com.shifthackz.aisdv1.presentation.model.UsageItem
import com.shifthackz.aisdv1.presentation.model.UsageScreenKind
import com.shifthackz.aisdv1.presentation.widget.dialog.DecisionInteractiveDialog
import org.koin.core.parameter.parametersOf

/**
 * Standalone storage usage route without bottom navigation.
 *
 * The screen renders a full-screen usage dashboard and asks for confirmation before deleting cache,
 * gallery entries, or downloaded local model files.
 *
 * @param modifier Compose modifier applied to the full-screen route.
 * @param router Optional router override for tests and previews; Koin supplies the app router otherwise.
 *
 * @author Dmitriy Moroz
 */
@Composable
fun StorageUsageScreen(
    modifier: Modifier = Modifier,
    router: StorageUsageRouter? = null,
) {
    val koin = remember { initKoin() }
    val platformActions = rememberStorageUsagePlatformActions()
    val resolvedRouter = remember(koin, router) {
        router ?: koin.get<StorageUsageRouter>()
    }
    val viewModel = remember(koin, resolvedRouter, platformActions) {
        koin.get<StorageUsageViewModel> {
            parametersOf(resolvedRouter, platformActions)
        }
    }

    MviComponent(viewModel = viewModel) { state, intentHandler ->
        StorageUsageScreenContent(
            modifier = modifier,
            state = state,
            processIntent = intentHandler,
        )
    }
}

/**
 * Stateless storage usage content adapter that wires shared usage UI to storage-specific dialogs.
 *
 * @param modifier Compose modifier applied to the shared usage layout.
 * @param state Current storage usage state emitted by [StorageUsageViewModel].
 * @param processIntent Intent sink used to route UI actions back to the ViewModel.
 *
 * @author Dmitriy Moroz
 */
@Composable
internal fun StorageUsageScreenContent(
    modifier: Modifier = Modifier,
    state: StorageUsageState,
    processIntent: (StorageUsageIntent) -> Unit = {},
) {
    UsageScreenContent(
        modifier = modifier,
        screen = UsageScreenKind.STORAGE,
        usage = state.usage,
        onBack = { processIntent(StorageUsageIntent.NavigateBack) },
        onSelectCategory = { processIntent(StorageUsageIntent.SelectCategory(it)) },
        onClearCategory = { processIntent(StorageUsageIntent.RequestClearCategory(it)) },
        onPrimaryAction = { processIntent(StorageUsageIntent.RequestClearAll) },
    )
    StorageUsageModalRenderer(
        screenModal = state.screenModal,
        processIntent = processIntent,
    )
}

@Composable
private fun StorageUsageModalRenderer(
    screenModal: StorageUsageModal,
    processIntent: (StorageUsageIntent) -> Unit,
) {
    when (screenModal) {
        StorageUsageModal.None -> Unit

        is StorageUsageModal.ClearCategory -> DecisionInteractiveDialog(
            title = Localization.string("settings_usage_delete_category_title").asUiText(),
            text = screenModal.item.deleteDescription().asUiText(),
            confirmActionText = Localization.string("delete").asUiText(),
            dismissActionText = Localization.string("cancel").asUiText(),
            onDismissRequest = { processIntent(StorageUsageIntent.DismissDialog) },
            onConfirmAction = {
                processIntent(StorageUsageIntent.ConfirmClearCategory(screenModal.item.category))
            },
        )

        is StorageUsageModal.ClearAll -> DecisionInteractiveDialog(
            title = Localization.string("settings_usage_delete_all_title").asUiText(),
            text = screenModal.deleteDescription().asUiText(),
            confirmActionText = Localization.string("delete").asUiText(),
            dismissActionText = Localization.string("cancel").asUiText(),
            onDismissRequest = { processIntent(StorageUsageIntent.DismissDialog) },
            onConfirmAction = {
                processIntent(
                    StorageUsageIntent.ConfirmClearAll(
                        categories = screenModal.items.map(UsageItem::category),
                    ),
                )
            },
        )
    }
}

private fun UsageItem.deleteDescription(): String = when (category) {
    UsageCategory.CACHE -> Localization.string(
        "settings_usage_delete_cache_text",
        bytes.formatBytes(),
    )

    UsageCategory.GALLERY -> Localization.string(
        "settings_usage_delete_gallery_text",
        bytes.formatBytes(),
    )

    UsageCategory.MODELS_ONNX,
    UsageCategory.MODELS_MEDIAPIPE,
    UsageCategory.MODELS_SDXL,
    UsageCategory.MODELS_CORE_ML,
    UsageCategory.MODELS_BONSAI,
    -> Localization.string(
        "settings_usage_delete_models_text",
        bytes.formatBytes(),
        category.title(),
    )

    UsageCategory.TRAFFIC_MODELS,
    UsageCategory.TRAFFIC_CONFIGS,
    UsageCategory.TRAFFIC_INFERENCE,
    -> ""
}

private fun StorageUsageModal.ClearAll.deleteDescription(): String = Localization.string(
    "settings_usage_delete_all_text",
    totalBytes.formatBytes(),
    items.map { item -> item.category.summaryTitle() }.joinLocalized(),
)

private fun List<String>.joinLocalized(): String = when (size) {
    0 -> ""
    1 -> first()
    2 -> first() +
        Localization.string("settings_usage_list_last_separator") +
        last()

    else -> dropLast(1).joinToString(Localization.string("settings_usage_list_separator")) +
        Localization.string("settings_usage_list_last_separator") +
        last()
}

private fun Long.formatBytes(): String {
    val value = coerceAtLeast(0L)
    val units = listOf("B", "KB", "MB", "GB", "TB")
    var unitIndex = 0
    var display = value.toDouble()
    while (display >= 1000.0 && unitIndex < units.lastIndex) {
        display /= 1000.0
        unitIndex++
    }

    return if (unitIndex == 0 || display >= 100.0) {
        "${display.toLong()} ${units[unitIndex]}"
    } else {
        "${(display * 10.0).toInt() / 10.0} ${units[unitIndex]}"
    }
}
