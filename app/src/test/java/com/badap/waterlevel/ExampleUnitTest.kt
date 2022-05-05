package com.badap.waterlevel

import org.junit.Test

import com.badap.waterlevel.retrofit.BoltRetrofit
import org.junit.Assert.*
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    private val apiKey = "your api key here"
    private val deviceName = "your device here"

    @Test
    fun testRetrofit(){
        val retrofit = BoltRetrofit(apiKey)
        runBlocking {
            coroutineScope {
                launch {
                    val response = retrofit.boltCloud().isOnline(deviceName).body()
                    assertEquals("1",response?.success)
                    assertEquals("offline",response?.value)
                }
            }
        }
    }
    @Test
    fun testSerialWR(){
        val retrofit = BoltRetrofit(apiKey)
        runBlocking {
            coroutineScope {
                launch {
                    retrofit.boltCloud().serialBegin(deviceName,"9600")
                    delay(100)
                    for(i in 1..2){
                        retrofit.boltCloud().serialWrite(deviceName,"a")
                        val response = retrofit.boltCloud().serialRead(deviceName,"10").body()
                        println(response)
                    }
                }
            }
        }
    }
}