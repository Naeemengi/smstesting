package com.company.smstestingapp.apicall




import okhttp3.Interceptor
import okhttp3.Response


class ApiInterceptor() : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        var request = chain.request()
            request.newBuilder()
                .addHeader("Authorization", "")
                .addHeader("Content-Type", "multipart/form-data;")
                .addHeader("Accept", "*/*")
                .build()

        return chain.proceed(request)
    }
}