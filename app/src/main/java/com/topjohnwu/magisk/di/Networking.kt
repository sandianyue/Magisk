package com.topjohnwu.magisk.di

import android.content.Context
import com.squareup.moshi.Moshi
import com.topjohnwu.magisk.BuildConfig
import com.topjohnwu.magisk.ProviderInstaller
import com.topjohnwu.magisk.core.Info
import okhttp3.Cache
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.io.File


fun createOkHttpClient(context: Context): OkHttpClient {
    val appCache = Cache(File(context.cacheDir, "okhttp"), 10 * 1024 * 1024)
    val builder = OkHttpClient.Builder().cache(appCache)

    if (BuildConfig.DEBUG) {
        builder.addInterceptor(HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BASIC
        })
    }

    if (!ProviderInstaller.install(context)) {
        Info.hasGMS = false
    }

    return builder.build()
}

fun createMoshiConverterFactory(): MoshiConverterFactory {
    val moshi = Moshi.Builder().build()
    return MoshiConverterFactory.create(moshi)
}

fun createRetrofit(okHttpClient: OkHttpClient): Retrofit.Builder {
    return Retrofit.Builder()
        .addConverterFactory(ScalarsConverterFactory.create())
        .addConverterFactory(createMoshiConverterFactory())
        .client(okHttpClient)
}

inline fun <reified T> createApiService(retrofitBuilder: Retrofit.Builder, baseUrl: String): T {
    return retrofitBuilder
        .baseUrl(baseUrl)
        .build()
        .create(T::class.java)
}
