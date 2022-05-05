package com.badap.waterlevel.retrofit

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class BoltRetrofit(apiKey: String?) {
    private var boltApiService: BoltApiService

    init {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://cloud.boltiot.com/remote/$apiKey/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        boltApiService = retrofit.create(BoltApiService::class.java)
    }

    fun boltCloud(): BoltApiService{
        return boltApiService
    }
}