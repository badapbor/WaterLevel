package com.badap.waterlevel.retrofit

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface BoltApiService {
    @GET("isOnline?")
    suspend fun isOnline(@Query("deviceName") deviceName: String?): Response<BoltResponse>

    @GET("serialBegin?")
    suspend fun serialBegin(@Query("deviceName") deviceName: String?,@Query("baud") baudRate: String): Response<BoltResponse>

    @GET("serialWrite?")
    suspend fun serialWrite(@Query("deviceName") deviceName: String?,@Query("data") data: String): Response<BoltResponse>

    @GET("serialRead?")
    suspend fun serialRead(@Query("deviceName") deviceName: String?,@Query("till") till: String): Response<BoltResponse>
}