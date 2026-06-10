package com.shifthackz.aisdv1.presentation.screen.logger

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.shifthackz.aisdv1.core.mvi.MviComponent
import com.shifthackz.aisdv1.presentation.di.initKoin
import com.shifthackz.aisdv1.presentation.navigation.router.LoggerRouter
import org.koin.core.parameter.parametersOf

@Composable
fun LoggerScreen(
    router: LoggerRouter? = null,
) {
    val koin = remember { initKoin() }
    val resolvedRouter = remember(koin, router) {
        router ?: koin.get<LoggerRouter>()
    }
    val viewModel = remember(koin, resolvedRouter) {
        koin.get<LoggerViewModel> {
            parametersOf(resolvedRouter)
        }
    }
    MviComponent(
        viewModel = viewModel,
    ) { state, intentHandler ->
        LoggerScreenContent(
            strings = LoggerScreenStrings(),
            state = state.toContentState(),
            processAction = { action -> intentHandler(action.toIntent()) },
        )
    }
}
