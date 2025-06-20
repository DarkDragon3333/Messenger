package com.example.messenger

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import com.example.messenger.dataBase.firebaseFuns.UID
import com.example.messenger.screens.NavDrawer
import com.example.messenger.ui.theme.MessengerTheme
import com.example.messenger.dataBase.valueEventListenerClasses.AppStatus
import com.example.messenger.utils.READ_CONTACTS
import com.example.messenger.utils.SEND_PUSH
import com.example.messenger.utils.defaultImageUri
import com.example.messenger.utils.get_out_from_auth
import com.example.messenger.utils.getContactsFromSmartphone
import com.example.messenger.utils.mainActivityContext
import com.example.messenger.utils.makeToast
import com.example.messenger.utils.sign_out
import com.example.messenger.viewModals.ChatsViewModal
import com.example.messenger.viewModals.ContactsViewModal
import com.example.messenger.viewModals.CurrentChatHolderViewModal
import com.example.messenger.viewModals.NavDrawerViewModal
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.messaging
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.jar.Manifest


class MainActivity : ComponentActivity() {
    lateinit var contactsViewModal: ContactsViewModal
    private var requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        when (isGranted) {
            true -> getContactsFromSmartphone(contactsViewModal)

            else -> makeToast("Нет разрешения", mainActivityContext)
        }
    }
    @Suppress("DEPRECATION")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainActivityContext = this
        requestNotificationPermission()
        window.setSoftInputMode(SOFT_INPUT_ADJUST_RESIZE);
        setContent {
            val currentChatHolderViewModal = CurrentChatHolderViewModal()
            val navDrawerViewModal = NavDrawerViewModal()
            val chatsViewModal = ChatsViewModal()
            MessengerTheme {
                NavDrawer(currentChatHolderViewModal, navDrawerViewModal, contactsViewModal, chatsViewModal)
            }
        }

        CoroutineScope(Dispatchers.IO).launch {
            init()
            val localToken = Firebase.messaging.token.await()
            val tempMap = mutableMapOf<String, String>()
            tempMap["token"] = localToken.toString()
            Firebase.firestore.collection("Tokens").document(UID)
                .set(tempMap)
        }
    }

    private fun init() {
        contactsViewModal = ContactsViewModal()
        startLocationPermissionRequest()
        defaultImageUri =
            "android.resource://$packageName/${R.drawable.default_profile_image}".toUri()
    }


    override fun onStart() {
        super.onStart()
        if (!sign_out)
            AppStatus.updateStates(AppStatus.ONLINE, mainActivityContext)
    }

    override fun onStop() {
        super.onStop()
        if (!get_out_from_auth)
            AppStatus.updateStates(AppStatus.OFFLINE, mainActivityContext)
    }

    override fun onPause() {
        super.onPause()
        if (!get_out_from_auth)
            AppStatus.updateStates(AppStatus.OFFLINE, mainActivityContext)
    }

    override fun onResume() {
        super.onResume()
        if (!get_out_from_auth)
            AppStatus.updateStates(AppStatus.ONLINE, mainActivityContext)
    }

    override fun onDestroy() {
        AppStatus.updateStates(AppStatus.OFFLINE, mainActivityContext)
        super.onDestroy()
    }

    private fun startLocationPermissionRequest() {
        requestPermissionLauncher.launch(READ_CONTACTS)
    }

    private fun requestNotificationPermission(){
        val hasPermission = ContextCompat.checkSelfPermission(
            mainActivityContext,
            android.Manifest.permission.POST_NOTIFICATIONS
        ) == PackageManager.PERMISSION_GRANTED

        if (hasPermission) {
            ActivityCompat.requestPermissions(
                mainActivityContext,
                arrayOf(android.Manifest.permission.POST_NOTIFICATIONS),
                0
            )
        }

    }
}







