package com.badap.waterlevel

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.badap.waterlevel.databinding.ActivityMainBinding
import com.badap.waterlevel.retrofit.BoltRetrofit
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var retrofit: BoltRetrofit
    var deviceName: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if(isNotSet()){
            startActivity(Intent(applicationContext,SettingsActivity::class.java))
        }else{
            binding = ActivityMainBinding.inflate(layoutInflater)
            val root = binding.root

            setContentView(root)

            sharedPreferences = getSharedPreferences(PREFERENCE_FILE,Context.MODE_PRIVATE)
            retrofit = BoltRetrofit(sharedPreferences.getString(API_KEY,""))
            deviceName = sharedPreferences.getString(DEVICE_NAME,"")
            binding.deviceName.text = sharedPreferences.getString(DEVICE_NAME,"")

            setUp()

            binding.checkButton.setOnClickListener{
                setUp()
            }
        }
    }
    private fun setUp(){
        lifecycleScope.launch {
            // is online
            val response = retrofit.boltCloud().isOnline(deviceName).body()

            if(response?.success.equals("1")  && response?.value.equals("online")){
                binding.onlineIndicator.setBackgroundColor(Color.GREEN)

                // serial read
                retrofit.boltCloud().serialBegin(deviceName,"9600")
                delay(50)
                serialWR()
            }
            else{
                binding.onlineIndicator.setBackgroundColor(Color.RED)
                binding.tankLevel.apply {
                    text = getString(R.string.device_offline)
                    setBackgroundColor(ContextCompat.getColor(applicationContext,R.color.offline))
                }
            }
        }
    }
    private suspend fun serialWR(){
        // in loop because first few api calls sometimes return command timed out
        for(i in 1..3){
            retrofit.boltCloud().serialWrite(deviceName,"a")
            val distanceResponse = retrofit.boltCloud().serialRead(deviceName,"10").body()
            if(distanceResponse?.success == "1" && !distanceResponse.value.isNullOrEmpty()){
                setWaterLevel(distanceResponse.value)
                break
            }
        }
    }
    private fun setWaterLevel(distance: String){
        val tankHeight = sharedPreferences.getInt(TANK_LEVEL,0)
        val disD = distance.toDouble()
        var percent = tankHeight - disD
        percent /= tankHeight
        percent *= 100

        binding.tankLevel.text = "${percent.toInt()}%"
        when{
            percent.toInt() in 0..25 -> {
                binding.tankLevel.setBackgroundColor(ContextCompat.getColor(application,R.color.danger))
            }
            percent.toInt() in 30..49 -> {
                binding.tankLevel.setBackgroundColor(ContextCompat.getColor(application,R.color.kinda))
            }
            percent.toInt() in 50..100 -> {
                binding.tankLevel.setBackgroundColor(ContextCompat.getColor(application,R.color.ok))
            }
        }
    }
    private fun isNotSet(): Boolean{
        return !getSharedPreferences(PREFERENCE_FILE,Context.MODE_PRIVATE).getBoolean(IS_LINKED,false)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.options_menu,menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // just one menu item actually but anyways
        when (item.itemId) {
            R.id.pair_bolt -> {
                val intent = Intent(this,SettingsActivity::class.java)
                startActivity(intent)
            }
        }
        return true
    }
}