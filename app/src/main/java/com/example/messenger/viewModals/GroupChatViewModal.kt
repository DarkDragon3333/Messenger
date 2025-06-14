package com.example.messenger.viewModals

import androidx.compose.runtime.mutableStateMapOf
import androidx.lifecycle.ViewModel
import com.example.messenger.dataBase.firebaseFuns.REF_STORAGE_ROOT
import com.example.messenger.utils.Constants.FOLDER_PHOTOS
import com.example.messenger.utils.mainActivityContext
import com.example.messenger.utils.makeToast
import com.example.messenger.utils.pathToSelectPhoto

class GroupChatViewModal : ViewModel() {
    private val _mapContactIdToPhotoUrl = mutableStateMapOf<String, Any>()

    fun setPhotoUrl(contactId: String, photoUrl: Any) {
        _mapContactIdToPhotoUrl[contactId] = photoUrl
    }

    fun getPhotoUrl(contactId: String): Any? {
        return _mapContactIdToPhotoUrl[contactId]
    }

    fun downloadContactsImages(contactsListId: MutableList<String>){
        contactsListId.forEach { contactId ->
            pathToSelectPhoto = REF_STORAGE_ROOT.child(FOLDER_PHOTOS).child(contactId)

            pathToSelectPhoto.downloadUrl.addOnCompleteListener { downloadTask ->
                when (downloadTask.isSuccessful) {
                    true -> {
                        val photoURL = downloadTask.result.toString()
                        _mapContactIdToPhotoUrl[contactId] = photoURL
                    }

                    else -> makeToast(
                        downloadTask.exception?.message.toString(),
                        mainActivityContext
                    )
                }
            }
        }
    }
}