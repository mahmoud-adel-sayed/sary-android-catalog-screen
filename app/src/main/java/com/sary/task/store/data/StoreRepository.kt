package com.sary.task.store.data

import androidx.lifecycle.LiveData
import com.sary.task.store.data.model.Banner
import com.sary.task.store.data.model.CatalogSection
import com.sary.task.store.data.model.ResultWrapper
import com.sary.task.store.data.service.StoreService
import com.sary.task.util.Response
import javax.inject.Inject

class StoreRepository @Inject constructor(private val service: StoreService) {
    fun getBanners(): LiveData<Response<ResultWrapper<List<Banner>>>> = service.getBanners()

    fun getCatalog(): LiveData<Response<ResultWrapper<List<CatalogSection>>>> = service.getCatalog()
}