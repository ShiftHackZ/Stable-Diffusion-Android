package com.shifthackz.aisdv1.presentation.widget.engine

import androidx.compose.runtime.Immutable
import com.shifthackz.aisdv1.core.mvi.MviState
import com.shifthackz.aisdv1.domain.entity.LocalAiModel
import com.shifthackz.aisdv1.domain.entity.ServerSource

/**
 * Carries `EngineSelectionState` data through the SDAI presentation layer.
 *
 * @author Dmitriy Moroz
 */
@Immutable
data class EngineSelectionState(
    /**
     * Exposes the `loading` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    val loading: Boolean = true,
    /**
     * Exposes the `mode` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    val mode: ServerSource = ServerSource.AUTOMATIC1111,
    /**
     * Exposes the `sdModels` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    val sdModels: List<String> = emptyList(),
    /**
     * Exposes the `selectedSdModel` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    val selectedSdModel: String = "",
    /**
     * Exposes the `swarmModels` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    val swarmModels: List<String> = emptyList(),
    /**
     * Exposes the `selectedSwarmModel` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    val selectedSwarmModel: String = "",
    /**
     * Exposes the `hfModels` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    val hfModels: List<String> = emptyList(),
    /**
     * Exposes the `selectedHfModel` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    val selectedHfModel: String = "",
    /**
     * Exposes the `stEngines` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    val stEngines: List<String> = emptyList(),
    /**
     * Exposes the `selectedStEngine` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    val selectedStEngine: String = "",
    /**
     * Exposes the `localAiModels` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    val localAiModels: List<LocalAiModel> = emptyList(),
    /**
     * Exposes the `selectedLocalAiModelId` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    val selectedLocalAiModelId: String = "",
) : MviState
