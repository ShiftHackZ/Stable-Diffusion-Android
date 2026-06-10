package com.shifthackz.aisdv1.presentation.widget.error

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.shifthackz.aisdv1.core.localization.Localization
import com.shifthackz.aisdv1.core.model.asString
import com.shifthackz.aisdv1.presentation.model.ErrorState

/**
 * Renders the `ErrorComposable` UI for the SDAI presentation layer.
 *
 * @param modifier Compose modifier applied to the rendered UI.
 * @param state state rendered or processed by the component.
 * @author Dmitriy Moroz
 */
@Composable
fun ErrorComposable(
    modifier: Modifier = Modifier,
    state: ErrorState,
) {
    if (state is ErrorState.None) return
    ErrorComposableContent(
        modifier = modifier,
        title = Localization.string("error_title"),
        message = when (state) {
            is ErrorState.WithMessage -> state.message.asString()
            else -> Localization.string("error_generic")
        },
    )
}
