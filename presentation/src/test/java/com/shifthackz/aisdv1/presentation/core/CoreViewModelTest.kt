@file:OptIn(ExperimentalCoroutinesApi::class)

package com.shifthackz.aisdv1.presentation.core

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before

abstract class CoreViewModelTest<V : ViewModel> {

    private var _viewModel: V? = null

    protected val viewModel: V
        get() = when (testViewModelStrategy) {
            CoreViewModelInitializeStrategy.InitializeOnce -> _viewModel ?: run {
                val vm = initializeViewModel()
                _viewModel = vm
                vm
            }
            CoreViewModelInitializeStrategy.InitializeEveryTime -> {
                val vm = initializeViewModel()
                _viewModel = vm
                vm
            }
        }

    open val testViewModelStrategy: CoreViewModelInitializeStrategy
        get() = CoreViewModelInitializeStrategy.InitializeOnce

    open val testDispatcher: CoroutineDispatcher
        get() = UnconfinedTestDispatcher()

    @Before
    open fun initialize() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    open fun finalize() {
        Dispatchers.resetMain()
    }

    abstract fun initializeViewModel(): V
}
