package com.sary.task.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.sary.task.store.ui.StoreViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
@Suppress("unused")
abstract class ViewModelModule {
    @Binds
    abstract fun bindViewModelFactory(factory: ViewModelFactory): ViewModelProvider.Factory

    @Binds
    @IntoMap
    @ViewModelKey(StoreViewModel::class)
    abstract fun bindMainViewModel(viewModel: StoreViewModel): ViewModel
}