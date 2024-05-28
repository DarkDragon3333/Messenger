package com.example.messenger

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import com.example.messenger.screens.NavDrawer
import com.example.messenger.ui.theme.MessengerTheme
import com.example.messenger.utilis.AUTH
import com.example.messenger.utilis.AppStatus
import com.example.messenger.utilis.READ_CONTACTS
import com.example.messenger.utilis.mainActivityContext
import com.example.messenger.utilis.makeToast
import com.example.messenger.utilis.myCheckPermission
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class MainActivity : ComponentActivity() {
    private var requestPermissionLauncher  = registerForActivityResult(
        ActivityResultContracts.RequestPermission()) { isGranted ->
        if (isGranted) {
            initContacts_()
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
        AUTH = FirebaseAuth.getInstance()
        initContacts_()
        startLocationPermissionRequest()

    }

    fun initContacts_(){
        if (myCheckPermission(READ_CONTACTS)){
            makeToast("Доступ к контактам разрешён", mainActivityContext)
        }
    }


    override fun onStart() {
        super.onStart()
        AppStatus.updateStates(AppStatus.ONLINE, mainActivityContext)
    }

    override fun onStop() {
        super.onStop()
        AppStatus.updateStates(AppStatus.OFFLINE, mainActivityContext)
    }

    override fun onDestroy() {
        super.onDestroy()
        AppStatus.updateStates(AppStatus.OFFLINE, mainActivityContext)
    }

    private fun startLocationPermissionRequest() {
        requestPermissionLauncher.launch(READ_CONTACTS)
    }

}







