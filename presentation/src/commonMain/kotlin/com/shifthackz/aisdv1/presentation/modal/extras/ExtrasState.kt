package com.shifthackz.aisdv1.presentation.modal.extras

import androidx.compose.runtime.Immutable
import com.shifthackz.aisdv1.domain.entity.ServerSource
import com.shifthackz.aisdv1.presentation.model.ErrorState
import com.shifthackz.aisdv1.presentation.model.ExtraType
import com.shifthackz.aisdv1.core.mvi.MviState

/**
 * Carries `ExtrasState` data through the SDAI presentation layer.
 *
 * @author Dmitriy Moroz
 */
@Immutable
data class ExtrasState(
    /**
     * Exposes the `loading` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    val loading: Boolean = true,
    /**
     * Exposes the `source` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    val source: ServerSource = ServerSource.AUTOMATIC1111,
    /**
     * Exposes the `error` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    val error: ErrorState = ErrorState.None,
    /**
     * Exposes the `prompt` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    val prompt: String = "",
    /**
     * Exposes the `negativePrompt` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    val negativePrompt: String = "",
    /**
     * Exposes the `type` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    val type: ExtraType = ExtraType.Lora,
    /**
     * Exposes the `loras` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    val loras: List<ExtraItemUi> = emptyList(),
) : MviState

/**
 * Carries `ExtraItemUi` data through the SDAI presentation layer.
 *
 * @author Dmitriy Moroz
 */
@Immutable
data class ExtraItemUi(
    /**
     * Exposes the `type` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    val type: ExtraType,
    /**
     * Exposes the `key` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    val key: String,
    /**
     * Exposes the `name` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    val name: String,
    /**
     * Exposes the `alias` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    val alias: String?,
    /**
     * Exposes the `isApplied` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    val isApplied: Boolean,
    /**
     * Exposes the `value` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    val value: String? = null,
)
