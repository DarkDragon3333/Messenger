package com.example.messenger

import android.os.Bundle
import android.view.WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import com.example.messenger.screens.NavDrawer
import com.example.messenger.ui.theme.MessengerTheme
import com.example.messenger.utilsFilies.AppStatus
import com.example.messenger.utilsFilies.READ_CONTACTS
import com.example.messenger.utilsFilies.get_out_from_auth
import com.example.messenger.utilsFilies.initContacts
import com.example.messenger.utilsFilies.mainActivityContext
import com.example.messenger.utilsFilies.makeToast
import com.example.messenger.utilsFilies.sign_out
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class MainActivity : ComponentActivity() {
    private var requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()) { isGranted ->
        if (isGranted) {
            initContacts()
        } else {
            makeToast("Нет разрешения", mainActivityContext)
        }
    }

    @Suppress("DEPRECATION")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setSoftInputMode(SOFT_INPUT_ADJUST_RESIZE);
        setContent {
            MessengerTheme {
                NavDrawer()
            }
        }

        CoroutineScope(Dispatchers.IO).launch {
            init()
        }
    }

    private fun init() {
        mainActivityContext = this
        startLocationPermissionRequest()
    }

    override fun onStart() {
        super.onStart()
        if (!sign_out) {
            AppStatus.updateStates(AppStatus.ONLINE, mainActivityContext)
        }

    }

    override fun onStop() {
        super.onStop()
        if (!get_out_from_auth) {
            AppStatus.updateStates(AppStatus.OFFLINE, mainActivityContext)
        }
    }

    private fun startLocationPermissionRequest() {
        requestPermissionLauncher.launch(READ_CONTACTS)
    }

}







