package com.example.messenger.viewModals

import android.content.ContentValues
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.ViewModel
import com.example.messenger.dataBase.firebaseFuns.REF_DATABASE_ROOT
import com.example.messenger.dataBase.firebaseFuns.UID
import com.example.messenger.modals.ChatModal
import com.example.messenger.modals.GroupChatModal
import com.example.messenger.utils.ChatItem
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

class ChatsViewModal : ViewModel() {
    private val _chatsList = mutableStateListOf<ChatItem>()
    val chatsList: SnapshotStateList<ChatItem> get() = _chatsList

    private lateinit var listenerRegistration: ListenerRegistration
    private lateinit var listingUpdateUserStatus: ChildEventListener

    private var isLoadingFirstChats by mutableStateOf(false)

    fun initChatsList(
        chatId: String,
        changeLoadingFlag: () -> Unit
    ) {
        chatMessLink(chatId).orderBy("timeStamp").limit(50)
            .get()
            .addOnSuccessListener { result ->
                val cacheMessages =
                    result.documents.map {
                        if (it.get("type") == "group")
                            it.toObject(GroupChatModal::class.java)!!
                        else
                            it.toObject(ChatModal::class.java)!!
                    }.toMutableList()
                _chatsList.apply {
                    clear()
                    addAll(cacheMessages.sortedByDescending { it.timeStamp }.distinctBy { it.id })
                }
                changeLoadingFlag()
            }
            .addOnFailureListener { exception ->
                Log.w(ContentValues.TAG, "Error getting documents.", exception)
            }
    }

    fun startListingChatsList(chatId: String) {
        val listing = chatMessLink(chatId).addSnapshotListener { snapshots, e ->
            if (e != null) {
                Log.w(ContentValues.TAG, "listen:error", e)
                return@addSnapshotListener
            }

            for (document in snapshots!!.documentChanges) {
                when (document.type) {
                    DocumentChange.Type.ADDED -> {
                        val newChat =
                            if (document.document.get("type") == "group")
                                document.document.toObject(
                                    GroupChatModal::class.java
                                )
                            else
                                document.document.toObject(
                                    ChatModal::class.java
                                )

                        if (_chatsList.none { it.id == newChat.id }) {
                            _chatsList.add(0, newChat)
                        }
                    }

                    DocumentChange.Type.MODIFIED -> {
                        val updateContactInfo = if (document.document.get("type") == "group")
                            document.document.toObject(
                                GroupChatModal::class.java
                            )
                        else
                            document.document.toObject(
                                ChatModal::class.java
                            )
                        val index = _chatsList.indexOfFirst { it.id == updateContactInfo.id }

                        if (index != -1) {
                            _chatsList[index] = updateContactInfo

                            val sortedList = _chatsList.sortedByDescending { it.timeStamp }
                            _chatsList.clear()
                            _chatsList.addAll(sortedList)
                        }

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

    fun downloadOldChats(userUID: String) {
        Firebase.firestore
            .collection("users_talkers").document(userUID)
            .collection("talkers")
            .orderBy("timeStamp")
            .startAfter(_chatsList.last().timeStamp)
            .limit(50)
            .get()
            .addOnSuccessListener { result ->
                val newMessages = result.documents.mapNotNull {
                    if (it.get("type") == "group")
                        it.toObject(GroupChatModal::class.java)!!
                    else
                        it.toObject(ChatModal::class.java)!!
                }.filterNot { msg ->
                    _chatsList.any { it.id == msg.id }
                }
                _chatsList.addAll(newMessages)
            }
            .addOnFailureListener {
                makeToast("Ошибка скачивания", mainActivityContext)
            }
    }

    fun getFlagDownloadFirstChats(): Boolean {
        return isLoadingFirstChats
    }

    fun setFlagDownloadFirstChats(newState: Boolean) {
        isLoadingFirstChats = newState
    }

    fun chatMessLink(chatId: String): Query {
        return Firebase.firestore
            .collection("users_talkers").document(chatId)
            .collection("talkers")
    }

    fun removeListener() {
        if (::listenerRegistration.isInitialized) {
            listenerRegistration.remove()
            REF_DATABASE_ROOT.child(NODE_USERS).removeEventListener(listingUpdateUserStatus)
        }
    }

}