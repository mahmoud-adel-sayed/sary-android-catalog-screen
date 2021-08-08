package com.sary.task

import com.sary.task.store.ui.StoreFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class MainFragmentsModule {
    @ContributesAndroidInjector
    abstract fun contributeStoreFragment(): StoreFragment
}