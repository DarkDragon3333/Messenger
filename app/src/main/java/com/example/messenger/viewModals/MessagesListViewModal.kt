package com.example.messenger.viewModals

import android.content.ContentValues
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.messenger.dataBase.firebaseFuns.UID
import com.example.messenger.modals.MessageModal
import com.google.firebase.Firebase
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.firestore

class MessagesListViewModal : ViewModel() {

    private var messagesList = mutableStateListOf<MessageModal>()
    private lateinit var listenerRegistration: ListenerRegistration

    private var isLoadingOldMessages by mutableStateOf(false)
    private var isLoadingFirstMessages by mutableStateOf(false)

    fun initMessagesList(chatId: String, changeLoadingFlag: () -> Unit) {
        chatMessLink(chatId)
            .get()
            .addOnSuccessListener { result ->
                val cacheMessages = result.documents
                    .mapNotNull { it.toObject(MessageModal::class.java) }
                    .distinctBy { it.id }
                messagesList.clear()
                messagesList.addAll(cacheMessages)
                changeLoadingFlag()
            }
            .addOnFailureListener { exception ->
                Log.w(ContentValues.TAG, "Error getting documents.", exception)
            }
    }

    fun startListingMessageList(chatId: String) {
        val listing = chatMessLink(chatId).addSnapshotListener { snapshots, e ->
            if (e != null) {
                Log.w(ContentValues.TAG, "listen:error", e)
                return@addSnapshotListener
            }

            for (document in snapshots!!.documentChanges) {
                when (document.type) {
                    DocumentChange.Type.ADDED -> {
                        val newMessage = document.document.toObject(MessageModal::class.java)
                        if (messagesList.none { it.id == newMessage.id }) {
                            messagesList.add(0, newMessage)

                        }
                    }

                    DocumentChange.Type.MODIFIED -> Log.d(
                        ContentValues.TAG,
                        "Modified city: ${document.document.data}"
                    )

                    DocumentChange.Type.REMOVED -> Log.d(
                        ContentValues.TAG,
                        "Removed city: ${document.document.data}"
                    )

                }
            }
        }

        listenerRegistration = listing
    }

    fun downloadOldMessages(groupChatId: String) {
        isLoadingOldMessages = true
        val lastTimestamp = messagesList.last().timeStamp

        Firebase.firestore.collection("users_messages")
            .document(UID)
            .collection("messages")
            .document(groupChatId)
            .collection("TheirMessages")
            .orderBy("timeStamp", Query.Direction.DESCENDING)
            .startAfter(lastTimestamp)
            .limit(30)
            .get()
            .addOnSuccessListener { result ->
                val newMessages = result.documents.mapNotNull {
                    it.toObject(MessageModal::class.java)
                }.filterNot { msg ->
                    messagesList.any { it.id == msg.id }
                }
                messagesList.addAll(newMessages)
                //chatScreenState.addAll(newMessages)

                isLoadingOldMessages = false
            }
            .addOnFailureListener {
                isLoadingOldMessages = false
            }
    }

    fun getMessageList(): MutableList<MessageModal> = messagesList

    fun setMessagesList(newMessages: MutableList<MessageModal>) {
        messagesList.clear()
        messagesList.addAll(newMessages)
    }

    fun getFlagDownloadOldMessages(): Boolean = isLoadingFirstMessages

    fun setFlagDownloadOldMessages(newState: Boolean) {
        isLoadingFirstMessages = newState
    }

    fun chatMessLink(chatId: String) : Query{
        return Firebase.firestore
            .collection("users_messages").document(UID)
            .collection("messages").document(chatId)
            .collection("TheirMessages")
            .orderBy("timeStamp", Query.Direction.DESCENDING) //Делает обратный порядок
            .limit(30)
    }

    fun removeListener() {
        if (::listenerRegistration.isInitialized) {
            listenerRegistration.remove()
        }
    }


}