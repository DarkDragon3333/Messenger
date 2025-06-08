package com.example.messenger.dataBase.firebaseFuns

import android.content.ContentValues
import android.util.Log
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.example.messenger.modals.ChatModal
import com.example.messenger.modals.GroupChatModal
import com.example.messenger.utils.ChatItem
import com.example.messenger.utils.Constants
import com.example.messenger.utils.mainActivityContext
import com.example.messenger.utils.makeToast
import com.google.firebase.Firebase
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.firestore

fun listeningUpdateChatsList(
    chatsScreenState: SnapshotStateList<ChatItem>,
    messLink: Query
): ListenerRegistration {
    val listing = messLink.addSnapshotListener { snapshots, e ->
        if (e != null) {
            Log.w(ContentValues.TAG, "listen:error", e)
            return@addSnapshotListener
        }

        for (document in snapshots!!.documentChanges) {
            when (document.type) {
                DocumentChange.Type.ADDED -> {
                    val newMessage = document.document.toObject(ChatModal::class.java)
                    if (chatsScreenState.none { it.id == newMessage.id }) {
                        chatsScreenState.add(0, newMessage)
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

    return listing
}

fun initChatsList(
    chatsScreenState: SnapshotStateList<ChatItem>,
    messLink: Query,
    function: () -> Unit
) {
    messLink
        .get()
        .addOnSuccessListener { result ->
            val cacheMessages =
                result.documents.map {
                    if (it.get("type") == "group")
                        it.toObject(GroupChatModal::class.java)!!
                    else
                        it.toObject(ChatModal::class.java)!!
                }.toMutableList()
            chatsScreenState.clear()
            chatsScreenState.addAll(cacheMessages.distinctBy { it.id })
            function()
            Log.i(ContentValues.TAG, chatsScreenState.size.toString())
        }
        .addOnFailureListener { exception ->
            Log.w(ContentValues.TAG, "Error getting documents.", exception)
        }
}

fun addChatToChatsList(infoArray: Array<String>) {
    try {
        val db = Firebase.firestore

        val userChats =
            infoArray[2].let {
                db
                    .collection("users_talkers").document(UID)
                    .collection("talkers").document(it)
            }

        val receivingUserChats =
            infoArray[2].let {
                db
                    .collection("users_talkers").document(it)
                    .collection("talkers").document(UID)
            }

        val mapChat = hashMapOf<String, Any>()
        mapChat[Constants.CHILD_FULLNAME] = infoArray[0]
        mapChat[Constants.CHILD_PHOTO_URL] = infoArray[1]
        mapChat[Constants.CHILD_ID] = infoArray[2]
        mapChat[Constants.CHILD_STATUS] = infoArray[3]
        mapChat[Constants.CHILD_TYPE] = infoArray[4]
        mapChat[Constants.CHILD_LAST_MESSAGE] = infoArray[5]
        mapChat[Constants.CHILD_TIME_STAMP] = "00:00:00"

        val mapReceivingUserChat = hashMapOf<String, Any>()
        mapReceivingUserChat[Constants.CHILD_FULLNAME] = USER.fullname
        mapReceivingUserChat[Constants.CHILD_PHOTO_URL] = USER.photoUrl
        mapReceivingUserChat[Constants.CHILD_ID] = USER.id
        mapReceivingUserChat[Constants.CHILD_STATUS] = USER.status
        mapReceivingUserChat[Constants.CHILD_TYPE] = infoArray[4]
        mapReceivingUserChat[Constants.CHILD_LAST_MESSAGE] = infoArray[5]
        mapReceivingUserChat[Constants.CHILD_TIME_STAMP] = "00:00:00"

        val mapChats = hashMapOf<String, Any>()
        mapChats["$userChats/$infoArray[2]"] = mapChat
        mapChats["$receivingUserChats/${UID}"] = mapReceivingUserChat

        mapChats["$userChats/$infoArray[2]"]?.let {
            userChats.set(
                it
            )
        }

        mapChats["$receivingUserChats/${UID}"]?.let {
            receivingUserChats.set(
                it
            )
        }
    } catch (e: Exception) {
        makeToast(e.message.toString(), mainActivityContext)
    }

}