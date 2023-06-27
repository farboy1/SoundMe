
package com.example.soundme

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.Navigation.findNavController
import model.MainFragment
import viewModel.AuthViewModel
import viewModel.MainViewModel
import com.example.soundme.R


class MainActivity : AppCompatActivity(R.layout.activity_main) {

    private lateinit var mainViewModel: MainViewModel
    private lateinit var authViewModel: AuthViewModel
    lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        setupViewModels()
        setupNavigation()
        verifyPermissions()
    }

    private fun setupNavigation(){
        this.navController = findNavController(this, R.id.nav_host_fragment)

        mainViewModel.currentFragment.observe(this, Observer { fragment ->
            fragment?.let {
                if (fragment == MainFragment::class.java) {
                    navController.navigate(R.id.mainFragment)
                }
            }
        })

        onBackPressedDispatcher.addCallback(this) {
            if (navController.currentDestination?.id == R.id.mainFragment) {
                finish()
            } else {
                navController.popBackStack()
            }
        }

    }

    private fun setupViewModels(){
        mainViewModel = ViewModelProvider(this).get(MainViewModel::class.java)

        authViewModel = ViewModelProvider(this)[AuthViewModel::class.java]

        authViewModel.initializeSharedPreferences(this)

        authViewModel.loginSuccess.observe(this, Observer { success ->
            if (success) {
                mainViewModel.setCurrentFragment(MainFragment::class.java)
            }
        })
    }

    private fun verifyPermissions() {

        val permission = ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.RECORD_AUDIO
        )

        if (permission != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.RECORD_AUDIO),
                REQUEST_RECORD_AUDIO_PERMISSIONS_CODE
            )
        }

    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray){

        when(requestCode){

            REQUEST_RECORD_AUDIO_PERMISSIONS_CODE ->{
                val audioRecordPermissionGranted=grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED

                if(audioRecordPermissionGranted){
                    // Permission granted. Do something.
                }else{
                    // Permission denied. Show an explanation UI and try again later.
                }
            }

            else->super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        }

    }


    companion object{

        const val REQUEST_RECORD_AUDIO_PERMISSIONS_CODE=1

        private val PERMISSIONS_TO_REQUEST=arrayOf(Manifest.permission.ACCESS_NETWORK_STATE,
            Manifest.permission.ACCESS_WIFI_STATE,
            Manifest.permission.INTERNET,
            Manifest.permission.RECORD_AUDIO )
    }

}
