package com.example.messenger.viewModals

import android.content.ContentValues
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
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.firestore

class GroupChatViewModal : ViewModel() {
    private lateinit var listenerRegistration: ListenerRegistration
    private lateinit var listingUpdateUserStatus: ChildEventListener

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

    fun removeDataTitle(){
        _groupChatName.value = ""
        _photoUrl.value = ""
        _status.value = ""
    }

    fun startListingGroupChatTitle() {
        val listing = chatMessLink().addSnapshotListener { snapshots, e ->
            if (e != null) {
                Log.w(ContentValues.TAG, "listen:error", e)
                return@addSnapshotListener
            }

            for (document in snapshots!!.documentChanges) {
                when (document.type) {
                    DocumentChange.Type.ADDED -> {

                    }

                    DocumentChange.Type.MODIFIED -> {
                        val newInfo = document.document.toObject(GroupChatModal::class.java)
                        _groupChatName.value = newInfo.chatName.toString()
                        _photoUrl.value = newInfo.photoUrl.toString()
                        _status.value = "Группа"
                    }

                    DocumentChange.Type.REMOVED -> Log.d(
                        ContentValues.TAG,
                        "Removed city: ${document.document.data}"
                    )

                }
            }
        }

        listenerRegistration = listing
    }

    fun listingTitleChanges() {
        val listing =
            REF_DATABASE_ROOT.child(NODE_USERS).addChildEventListener(object :
                ChildEventListener {
                override fun onChildAdded(
                    snapshot: DataSnapshot,
                    previousChildName: String?
                ) {

                }

                override fun onChildChanged(
                    snapshot: DataSnapshot,
                    previousChildName: String?
                ) {
                    val updateStatus =
                        snapshot.getValue(GroupChatModal::class.java) ?: GroupChatModal()

                    Firebase.firestore
                        .collection("users_talkers").document(UID)
                        .collection("talkers").document(updateStatus.id).update(
                            "photoUrl", updateStatus.photoUrl
                        )
                    Firebase.firestore
                        .collection("users_talkers").document(UID)
                        .collection("talkers").document(updateStatus.id).update(
                            "chatName", updateStatus.chatName
                        )
                    Firebase.firestore
                        .collection("users_talkers").document(UID)
                        .collection("talkers").document(updateStatus.id).update(
                            "lastMessage", updateStatus.lastMessage
                        )
                }

                override fun onChildRemoved(snapshot: DataSnapshot) {

                }

                override fun onChildMoved(
                    snapshot: DataSnapshot,
                    previousChildName: String?
                ) {

                }

                override fun onCancelled(error: DatabaseError) {
                    makeToast(error.message, mainActivityContext)
                }
            })

        listingUpdateUserStatus = listing
    }

    fun chatMessLink(): Query {
        return Firebase.firestore
            .collection("users_talkers").document(UID)
            .collection("talkers")
    }

    fun removeListener() {
        if (::listenerRegistration.isInitialized) {
            listenerRegistration.remove()
            REF_DATABASE_ROOT.child(NODE_USERS).removeEventListener(listingUpdateUserStatus)
        }
    }

    fun getPhotoUrl(contactId: String): Any? {
        return _mapContactIdToPhotoUrl[contactId]
    }

    fun getContactsData(): MutableList<ContactModal> {
        return changeContactsList
    }

    fun downloadContactsData(contactsListId: MutableList<String>, chatId: String) {
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