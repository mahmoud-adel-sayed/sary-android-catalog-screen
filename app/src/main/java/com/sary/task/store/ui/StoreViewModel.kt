package com.sary.task.store.ui

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.switchMap
import com.sary.task.store.data.StoreRepository
import com.sary.task.util.Response
import javax.inject.Inject

class StoreViewModel @Inject constructor(
    private val repository: StoreRepository
) : ViewModel() {

    private val _banners: MutableLiveData<String> = MutableLiveData()
    val banners = _banners.switchMap { repository.getBanners() }

    private val _catalog: MutableLiveData<String> = MutableLiveData()
    val catalog = _catalog.switchMap { repository.getCatalog() }

    fun getBanners() {
        if (banners.value is Response.Success) {
            return
        }
        _banners.value = ""
    }

    fun getCatalog() {
        if (catalog.value is Response.Success) {
            return
        }
        _catalog.value = ""
    }
}