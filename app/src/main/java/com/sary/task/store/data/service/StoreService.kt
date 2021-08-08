package com.sary.task.store.data.service

import androidx.lifecycle.LiveData
import com.sary.task.store.data.model.Banner
import com.sary.task.store.data.model.CatalogSection
import com.sary.task.store.data.model.ResultWrapper
import com.sary.task.util.Response
import retrofit2.http.GET

interface StoreService {
    @GET("baskets/76097/banners")
    fun getBanners(): LiveData<Response<ResultWrapper<List<Banner>>>>

    @GET("baskets/76097/catalog")
    fun getCatalog(): LiveData<Response<ResultWrapper<List<CatalogSection>>>>
}