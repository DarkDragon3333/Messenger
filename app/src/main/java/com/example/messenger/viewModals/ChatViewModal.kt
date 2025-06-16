package com.example.messenger.viewModals

import android.content.ContentValues
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.messenger.dataBase.firebaseFuns.REF_DATABASE_ROOT
import com.example.messenger.dataBase.firebaseFuns.UID
import com.example.messenger.modals.ChatModal
import com.example.messenger.modals.ContactModal
import com.example.messenger.utils.Constants.NODE_USERS
import com.example.messenger.utils.mainActivityContext
import com.example.messenger.utils.makeToast
import com.google.firebase.Firebase
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.firestore

class ChatViewModal : ViewModel() {
    private lateinit var listenerRegistration: ListenerRegistration
    private lateinit var listingUpdateUserStatus: ChildEventListener

    private val _fullName = mutableStateOf<String>("")
    val fullName get() = _fullName

    private val _photoUrl = mutableStateOf<String>("")
    val photoUrl get() = _photoUrl

    private val _status = mutableStateOf<String>("")
    val status get() = _status

    fun initDataTitle(chatModal: ChatModal?){
        _fullName.value = chatModal?.fullname.toString()
        _photoUrl.value = chatModal?.photoUrl.toString()
        _status.value = chatModal?.status.toString()
    }

    fun startListingChatTitle() {
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
                        val newInfo = document.document.toObject(ContactModal::class.java)

                        _fullName.value = newInfo.fullname.toString()
                        _photoUrl.value = newInfo.photoUrl.toString()
                        _status.value = newInfo.status.toString()
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

    fun listingUsersStatus() {
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
                    val updateStatus = snapshot.getValue(ChatModal::class.java) ?: ChatModal()

                    Firebase.firestore
                        .collection("users_talkers").document(UID)
                        .collection("talkers").document(updateStatus.id).update(
                            "status", updateStatus.status
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
}