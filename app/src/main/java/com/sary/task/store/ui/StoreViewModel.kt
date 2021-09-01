package com.sary.task.store.ui

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.switchMap
import com.sary.task.store.data.StoreRepository
import com.sary.task.util.Response
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class StoreViewModel @Inject constructor(
    private val repository: StoreRepository
) : ViewModel() {

    private val _bannerItems: MutableLiveData<String> = MutableLiveData()
    val bannerItems = _bannerItems.switchMap { repository.getBannerItems() }

    private val _catalog: MutableLiveData<String> = MutableLiveData()
    val catalog = _catalog.switchMap { repository.getCatalog() }

    fun getBannerItems() {
        if (bannerItems.value is Response.Success) {
            return
        }
        _bannerItems.value = ""
    }

    fun getCatalog() {
        if (catalog.value is Response.Success) {
            return
        }
        _catalog.value = ""
    }
}