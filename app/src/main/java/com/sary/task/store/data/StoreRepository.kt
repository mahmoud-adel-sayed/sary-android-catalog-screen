package com.sary.task.store.data

import com.sary.task.store.data.service.BannerResult
import com.sary.task.store.data.service.CatalogResult
import com.sary.task.store.data.service.StoreService
import javax.inject.Inject

class StoreRepository @Inject constructor(private val service: StoreService) {
    fun getBannerItems(): BannerResult = service.getBannerItems()

    fun getCatalog(): CatalogResult = service.getCatalog()
}