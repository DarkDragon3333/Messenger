package com.example.messenger.viewModals

import android.util.Log
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.messenger.dataBase.firebaseFuns.REF_DATABASE_ROOT
import com.example.messenger.dataBase.firebaseFuns.REF_STORAGE_ROOT
import com.example.messenger.dataBase.firebaseFuns.UID
import com.example.messenger.modals.ContactModal
import com.example.messenger.modals.GroupChatModal
import com.example.messenger.utils.Constants.FOLDER_PHOTOS
import com.example.messenger.utils.Constants.NODE_USERS
import com.example.messenger.utils.mainActivityContext
import com.example.messenger.utils.makeToast
import com.example.messenger.utils.pathToSelectPhoto
import com.google.firebase.Firebase
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.firestore

class GroupChatViewModal : ViewModel() {
    private lateinit var listenerGroupChatDataForTitle: ListenerRegistration
    private lateinit var listenerGroupChatData: ListenerRegistration

    private val _mapContactIdToPhotoUrl = mutableStateMapOf<String, Any>()
    private val _contactsData = mutableListOf<ContactModal>()

    private val _changeContactsList = mutableListOf<ContactModal>()
    val changeContactsList get() = _changeContactsList

    private val _groupChatName = mutableStateOf("")
    val groupChatName get() = _groupChatName

    private val _photoUrl = mutableStateOf("")
    val photoUrl get() = _photoUrl

    private val _status = mutableStateOf("")
    val status get() = _status

    fun initDataTitle(groupChatModal: GroupChatModal?) {
        _groupChatName.value = groupChatModal?.chatName.toString()
        _photoUrl.value = groupChatModal?.photoUrl.toString()
        _changeContactsList.addAll(_contactsData)
    }

    fun updateDataTitle(groupChatModal: GroupChatModal?) {
        _groupChatName.value = groupChatModal?.chatName.toString()
        _photoUrl.value = groupChatModal?.photoUrl.toString()
        //_changeContactsList.addAll(_contactsData)
    }

    fun removeDataTitle() {
        _groupChatName.value = ""
        _photoUrl.value = ""
        _status.value = ""
    }

    fun startListingGroupChatData(chatId: String) {
        val listingData = Firebase.firestore.collection("users_groups").document(chatId)
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    Log.e("Firestore", "Ошибка прослушивания", e)
                    return@addSnapshotListener
                }

                if (snapshot != null && snapshot.exists()) {
                    val chatData = snapshot.toObject(GroupChatModal::class.java)

                    Firebase.firestore
                        .collection("users_talkers").document(UID)
                        .collection("talkers").document(chatData?.id ?: "")
                        .update("chatName", chatData?.chatName)

                    Firebase.firestore
                        .collection("users_talkers").document(UID)
                        .collection("talkers").document(chatData?.id ?: "")
                        .update("photoUrl", chatData?.photoUrl)

                } else {
                    Log.d("Firestore", "Документ не найден")
                }
            }

        listenerGroupChatData = listingData
    }

    fun startListingGroupChatDataForTitle(chatId: String) {
        val listingGroupChatDataForTitle =
            chatMessLink2(chatId).addSnapshotListener { snapshot, e ->
                if (e != null) {
                    Log.e("Firestore", "Ошибка прослушивания", e)
                    return@addSnapshotListener
                }

                if (snapshot != null && snapshot.exists()) {
                    val chatData = snapshot.toObject(GroupChatModal::class.java)
                    updateDataTitle(chatData)

                } else {
                    Log.d("Firestore", "Документ не найден")
                }
            }

        listenerGroupChatDataForTitle = listingGroupChatDataForTitle
    }

    fun chatMessLink2(charId: String): DocumentReference {
        return Firebase.firestore
            .collection("users_talkers").document(UID)
            .collection("talkers").document(charId)
    }

    fun removeListener() {
        if (::listenerGroupChatDataForTitle.isInitialized) {
            listenerGroupChatDataForTitle.remove()
        }
        if (::listenerGroupChatData.isInitialized){
            listenerGroupChatData.remove()
        }

    }

    fun getPhotoUrl(contactId: String): Any? {
        return _mapContactIdToPhotoUrl[contactId]
    }

    fun getContactsData(): MutableList<ContactModal> {
        return changeContactsList
    }

    fun downloadContactsData(contactsListId: MutableList<String>) {
        contactsListId.forEach { contactId ->
            REF_DATABASE_ROOT.child(NODE_USERS).child(contactId).get()
                .addOnCompleteListener { result ->
                    if (result.isSuccessful) {
                        val contactModal =
                            result.result.getValue(ContactModal::class.java) ?: ContactModal()
                        if (changeContactsList.contains(contactModal) == false)
                            changeContactsList.add(contactModal)
                    }
                }
        }
    }

    fun downloadContactsImages(contactsListId: MutableList<String>) {
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