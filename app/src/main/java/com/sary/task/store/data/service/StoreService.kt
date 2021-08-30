package com.sary.task.store.data.service

import androidx.lifecycle.LiveData
import com.sary.task.store.data.model.BannerItem
import com.sary.task.store.data.model.CatalogSection
import com.sary.task.store.data.model.ResultWrapper
import com.sary.task.util.Response
import retrofit2.http.GET

typealias BannerResult = LiveData<Response<ResultWrapper<List<BannerItem>>>>
typealias CatalogResult = LiveData<Response<ResultWrapper<List<CatalogSection>>>>

interface StoreService {
    @GET("baskets/76097/banners")
    fun getBannerItems(): BannerResult

    @GET("baskets/76097/catalog")
    fun getCatalog(): CatalogResult
}