package com.dod.lock

import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.dod.lock.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    private val spf by lazy { getSharedPreferences("lock", MODE_PRIVATE) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.btn.setOnClickListener(onClick)
        textChange(spf.getBoolean("isLock", true))
        checkPermission()
    }

    private val onClick = View.OnClickListener {
        val isLock = spf.getBoolean("isLock", true)

        if(isLock){
            stopService()
        }else {
            startService()
        }

        textChange(!isLock)
        spf.edit().putBoolean("isLock", !isLock).apply()
    }

    private fun textChange(isLock: Boolean){
        if(isLock){
            binding.btn.text = "OFF"
        }else {
            binding.btn.text = "ON"
        }
    }

    private fun checkPermission(){
        if(!Settings.canDrawOverlays(this)) {
            val uri = Uri.fromParts("package", packageName, null)
            val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, uri)
            permissionLauncher.launch(intent)
        }else {
            if(spf.getBoolean("isLock", true)){
                startService()
            }
        }
    }

    private val permissionLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        startService()
    }

    private fun startService(){
        val intent = Intent(applicationContext, LockService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(intent)
        }else {
            startService(intent)
        }
    }

    private fun stopService(){
        val intent = Intent(applicationContext, LockService::class.java)
        stopService(intent)
    }
}