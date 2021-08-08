package com.sary.task.di

import com.sary.task.MainActivity
import com.sary.task.MainFragmentsModule
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
@Suppress("unused")
abstract class ActivityModule {
    @ContributesAndroidInjector(modules = [MainFragmentsModule::class])
    abstract fun contributeMainActivity(): MainActivity
}