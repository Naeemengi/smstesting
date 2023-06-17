package com.company.smstestingapp.apicall


import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit


object RetrofitClientManager {

    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://test.k2bay.com/api/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()
    }


    fun provideApiClient(retrofit: Retrofit = provideRetrofit(
        provideOkHttp(
        provideLoggingInterceptor(), ApiInterceptor()
    )
    )
    ): ApiInterface {
        return retrofit.create(ApiInterface::class.java)
    }


    fun provideOkHttp(
        loggingInterceptor: HttpLoggingInterceptor,
        apiInterceptor: ApiInterceptor
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .readTimeout(1, TimeUnit.MINUTES)
            .connectTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(5, TimeUnit.MINUTES)
            .addInterceptor(apiInterceptor)
            .addInterceptor(loggingInterceptor)
            .build()
    }


    fun provideLoggingInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
    }

}