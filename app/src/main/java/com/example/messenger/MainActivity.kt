package com.example.messenger

import android.os.Bundle
import android.view.WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.net.toUri
import com.example.messenger.screens.NavDrawer
import com.example.messenger.ui.theme.MessengerTheme
import com.example.messenger.dataBase.valueEventListenerClasses.AppStatus
import com.example.messenger.utils.READ_CONTACTS
import com.example.messenger.utils.defaultImageUri
import com.example.messenger.utils.get_out_from_auth
import com.example.messenger.utils.getContactsFromSmartphone
import com.example.messenger.utils.mainActivityContext
import com.example.messenger.utils.makeToast
import com.example.messenger.utils.sign_out
import com.example.messenger.viewModals.ContactsViewModal
import com.example.messenger.viewModals.CurrentChatHolderViewModal
import com.example.messenger.viewModals.NavDrawerViewModal
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class MainActivity : ComponentActivity() {
    lateinit var contactsViewModal: ContactsViewModal
    private var requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()) { isGranted,  ->
        when (isGranted) {
            true -> getContactsFromSmartphone(contactsViewModal)

            else -> makeToast("Нет разрешения", mainActivityContext)
        }
    }

    @Suppress("DEPRECATION")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainActivityContext = this


        window.setSoftInputMode(SOFT_INPUT_ADJUST_RESIZE);
        setContent {
            val currentChatHolderViewModal = CurrentChatHolderViewModal()
            val navDrawerViewModal = NavDrawerViewModal()

            MessengerTheme {
                NavDrawer(currentChatHolderViewModal, navDrawerViewModal, contactsViewModal)
            }
        }

        CoroutineScope(Dispatchers.IO).launch {
            init()
        }
    }

    private fun init() {
        contactsViewModal = ContactsViewModal()
        startLocationPermissionRequest()
        defaultImageUri = "android.resource://$packageName/${R.drawable.default_profile_image}".toUri()
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
}







