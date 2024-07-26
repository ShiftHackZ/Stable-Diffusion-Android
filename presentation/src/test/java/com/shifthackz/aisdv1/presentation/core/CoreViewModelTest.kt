@file:OptIn(ExperimentalCoroutinesApi::class)

package com.shifthackz.aisdv1.presentation.core

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before

abstract class CoreViewModelTest<V : ViewModel> {

    private lateinit var _viewModel: V

    protected val viewModel: V
        get() {
            if (this::_viewModel.isInitialized) return _viewModel
            _viewModel = initializeViewModel()
            return _viewModel
        }

    @Before
    open fun initialize() {
        Dispatchers.setMain(StandardTestDispatcher())
    }

    @After
    open fun finalize() {
        Dispatchers.resetMain()
    }

    abstract fun initializeViewModel(): V
}
