package com.shifthackz.aisdv1.presentation.screen.setup.mappers

import com.shifthackz.aisdv1.domain.entity.LocalAiModel
import com.shifthackz.aisdv1.presentation.screen.setup.ServerSetupState

fun List<LocalAiModel>.mapToUi(): List<ServerSetupState.LocalModel> = map(LocalAiModel::mapToUi)

fun List<LocalAiModel>.mapLocalCustomModelSwitchState(): Boolean =
    find { it.selected && it.id == LocalAiModel.CUSTOM.id } != null

fun LocalAiModel.mapToUi(): ServerSetupState.LocalModel = with(this) {
    ServerSetupState.LocalModel(
        id = id,
        name = name,
        size = size,
        downloaded = downloaded,
        selected = selected,
    )
}

fun List<ServerSetupState.LocalModel>.withNewState(
    model: ServerSetupState.LocalModel?,
): List<ServerSetupState.LocalModel> =
    map {
        if (model == null) return@map it
        if (it.id == model.id) model
        else {
            if (model.selected) it.copy(selected = false)
            else it
        }
    }
