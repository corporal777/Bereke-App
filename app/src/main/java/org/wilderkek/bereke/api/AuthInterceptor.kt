package org.wilderkek.bereke.api

import okhttp3.Interceptor
import okhttp3.Response
import org.wilderkek.bereke.di.data.AppData
import java.io.IOException
import kotlin.jvm.Throws

class AuthInterceptor(private val appData: AppData) : Interceptor {
    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val authenticatedRequest = request.newBuilder()
        appData.token?.let { authenticatedRequest.header("Authorization", "Token $it") }
        //appData.token?.let { authenticatedRequest.header("Authorization", "Token") }
        return chain.proceed(authenticatedRequest.build())
    }
}