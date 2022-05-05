package com.badap.waterlevel

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.Gravity
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.badap.waterlevel.databinding.ActivitySettingsBinding

class SettingsActivity: AppCompatActivity() {
    private lateinit var binding: ActivitySettingsBinding
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        val root = binding.root
        setContentView(root)

        sharedPreferences = getSharedPreference()

        populateViews()

        binding.save.setOnClickListener {
            save()
        }
    }
    private fun checkInput(): Boolean {
        if( binding.deviceName.text.isEmpty()) {
            Toast.makeText(applicationContext,"Device Name cannot be empty", Toast.LENGTH_SHORT).show()
            return false
        }
        if ( binding.apiKey.text.isEmpty() ){
            Toast.makeText(applicationContext,"API Key cannot be empty", Toast.LENGTH_SHORT).show()
            return false
        }
        if ( binding.tankHeight.text.isEmpty()){
            Toast.makeText(applicationContext, "Tank height cannot be empty!", Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }
    private fun populateViews(){
        if(sharedPreferences.getBoolean(IS_LINKED,false)){
            binding.apiKey.setText(sharedPreferences.getString(API_KEY,""))
            binding.deviceName.setText(sharedPreferences.getString(DEVICE_NAME,""))
            binding.tankHeight.setText((sharedPreferences.getInt(TANK_LEVEL,0)).toString())
        }
    }
    private fun save(){
        with (sharedPreferences.edit()) {
            if(checkInput()){
                putBoolean(IS_LINKED,true)
                putString(DEVICE_NAME, binding.deviceName.text.toString())
                putString(API_KEY, binding.apiKey.text.toString())
                putInt(TANK_LEVEL, binding.tankHeight.text.toString().toInt())
                apply()

                val toast = Toast.makeText(applicationContext,"Settings Saved!",
                    Toast.LENGTH_SHORT)
                toast.setGravity(Gravity.CENTER,0,0)
                toast.show()

                startActivity(Intent(applicationContext,MainActivity::class.java))
            }
        }
    }
    private fun getSharedPreference(): SharedPreferences {
        return applicationContext.getSharedPreferences(PREFERENCE_FILE, Context.MODE_PRIVATE)
    }
}