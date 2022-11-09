package com.sorien.ppbthome

import android.Manifest
import com.sorien.ppbthome.Config.autoStart
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.sorien.ppbthome.R
import androidx.appcompat.widget.SwitchCompat
import com.sorien.ppbthome.AdvertiserService
import android.widget.CompoundButton
import androidx.core.app.ActivityCompat
import android.content.pm.PackageManager
import com.sorien.ppbthome.MainActivity
import android.content.Intent
import android.widget.Toast
import java.util.ArrayList

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val onOffSwitch = findViewById<SwitchCompat>(R.id.activate_switch_id)
        onOffSwitch.isChecked = AdvertiserService.isServiceCreated()
        onOffSwitch.setOnCheckedChangeListener { buttonView: CompoundButton?, isChecked: Boolean ->
            if (isChecked) {
                startServiceRequestPermissions()
            } else {
                stopService()
            }
        }
    }

    fun startServiceRequestPermissions() {
        val permissions = arrayOf(Manifest.permission.BLUETOOTH_ADVERTISE, Manifest.permission.BLUETOOTH_CONNECT)
        val requirePermissions = ArrayList<String>()
        for (permission in permissions) {
            if (ActivityCompat.checkSelfPermission(this@MainActivity, permission) != PackageManager.PERMISSION_GRANTED) {
                requirePermissions.add(permission)
            }
        }
        if (!requirePermissions.isEmpty()) {
            ActivityCompat.requestPermissions(this@MainActivity, requirePermissions.toTypedArray(), PERMISSION_REQUEST_CODE)
        } else {
            startService()
        }
    }

    fun startService() {
        startService(Intent(this@MainActivity, AdvertiserService::class.java))
        val config = Config(this)
        config.autoStart = true
        Toast.makeText(this, "Broadcasting started.", Toast.LENGTH_SHORT).show()
    }

    fun stopService() {
        stopService(Intent(this@MainActivity, AdvertiserService::class.java))
        val config = Config(this)
        config.autoStart = false
        Toast.makeText(this, "Broadcasting finished.", Toast.LENGTH_SHORT).show()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startService()
            } else {
                val onOffSwitch = findViewById<SwitchCompat>(R.id.activate_switch_id)
                onOffSwitch.isChecked = AdvertiserService.isServiceCreated()
            }
        }
    }

    companion object {
        private const val PERMISSION_REQUEST_CODE = 1
    }
}