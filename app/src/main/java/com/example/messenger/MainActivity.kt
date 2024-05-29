package com.example.messenger

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import com.example.messenger.screens.NavDrawer
import com.example.messenger.ui.theme.MessengerTheme
import com.example.messenger.utilis.AppStatus
import com.example.messenger.utilis.READ_CONTACTS
import com.example.messenger.utilis.get_out_from_auth
import com.example.messenger.utilis.initContacts
import com.example.messenger.utilis.mainActivityContext
import com.example.messenger.utilis.makeToast
import com.example.messenger.utilis.sign_out
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class MainActivity : ComponentActivity() {
    private var requestPermissionLauncher  = registerForActivityResult(
        ActivityResultContracts.RequestPermission()) { isGranted ->
        if (isGranted) {
            initContacts()
        }
        else {
            makeToast("Нет разрешения", mainActivityContext)
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        CoroutineScope(Dispatchers.IO).launch {
            init()
        }

        setContent {
            MessengerTheme {
                NavDrawer()
            }
        }
    }

    private fun init(){
        mainActivityContext = this
        initContacts()
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







