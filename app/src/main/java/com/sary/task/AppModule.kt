package com.sary.task

import android.app.Application
import androidx.core.os.ConfigurationCompat
import com.sary.task.store.data.service.StoreService
import com.sary.task.util.LiveDataCallAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Singleton
    @Provides
    fun provideRetrofit(app: Application): Retrofit {
        val logging = HttpLoggingInterceptor()
        logging.level = HttpLoggingInterceptor.Level.BODY

        val httpClient = OkHttpClient.Builder()
        httpClient.addInterceptor(logging)
        httpClient.addInterceptor { chain ->
            val currentLang = ConfigurationCompat.getLocales(app.resources.configuration)[0].language
            chain.proceed(chain.request()
                .newBuilder()
                .addHeader("Device-Type", DEVICE_TYPE)
                .addHeader("App-Version", APP_VERSION)
                .addHeader("Accept-Language", currentLang)
                .addHeader("Authorization", "token $AUTHORIZATION_TOKEN")
                .build()
            )
        }

        return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(LiveDataCallAdapterFactory(app))
                .client(httpClient.build())
                .build()
    }

    @Singleton
    @Provides
    fun provideStoreService(retrofit: Retrofit): StoreService = retrofit.create(StoreService::class.java)
}

private const val BASE_URL = "https://staging.sary.co/api/v2.5.1/"

private const val DEVICE_TYPE = "android"
private const val APP_VERSION = "3.1.1.0.0"

// NOTE: Token should not be embedded in source code but it is fixed here for the sack of this sample
private const val AUTHORIZATION_TOKEN = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9." +
        "eyJpZCI6ODg2NiwidXNlcl9waG9uZSI6Ijk2NjU2NDk4OTI1MCJ9." +
        "VYE28vtnMRLmwBISgvvnhOmPuGueW49ogOhXm5ZqsGU"