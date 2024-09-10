package com.example.medicalcard

import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.medicalcard.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint
import android.Manifest
import androidx.activity.viewModels
import androidx.navigation.fragment.NavHostFragment


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    private val viewModel: MainActivityViewModel by viewModels()

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        if (Build.VERSION.SDK_INT > 32) {
            if (!shouldShowRequestPermissionRationale(PERMISSION_REQUEST_CODE.toString())){
                getNotificationPermission();
            }
        }

        val navHost = supportFragmentManager.findFragmentById(R.id.nav_fragment) as NavHostFragment
        val navController = navHost.navController

        if(viewModel.isFirstAppStart()) {
            navController.navigate(R.id.editProfileFragment)
        }

    }

    private fun getNotificationPermission() {
        try {
            if (Build.VERSION.SDK_INT > 32) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf<String>(Manifest.permission.POST_NOTIFICATIONS),
                    PERMISSION_REQUEST_CODE
                )
            }
        } catch (e: Exception) {
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
            PERMISSION_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED
                ) {
                    //
                } else {
                    //
                }
                return
            }
        }
    }

    companion object {
        private const val PERMISSION_REQUEST_CODE = 112
    }
}