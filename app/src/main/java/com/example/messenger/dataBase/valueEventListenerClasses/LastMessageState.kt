package com.example.messenger.dataBase.valueEventListenerClasses

import android.util.Log
import com.example.messenger.dataBase.firebaseFuns.UID
import com.example.messenger.utils.mainActivityContext
import com.example.messenger.utils.makeToast
import com.google.firebase.Firebase
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.firestore

class LastMessageState() {
    companion object {
        fun updateLastMessageInChat(lastMessage: String, id: String) {
            val db = Firebase.firestore

            val chatDataLinkUser =
                db
                    .collection("users_talkers").document(UID)
                    .collection("talkers").document(id)

            val chatDataLinkContact =
                db
                    .collection("users_talkers").document(id)
                    .collection("talkers").document(UID)

            chatDataLinkUser
                .update("lastMessage", lastMessage)
                .addOnFailureListener { e ->
                    makeToast(e.message.toString(), mainActivityContext)
                }

            chatDataLinkContact
                .update("lastMessage", lastMessage)
                .addOnFailureListener { e ->
                    makeToast(e.message.toString(), mainActivityContext)
                }
        }

        fun updateLastMessageInGroupChat(
            lastMessage: String,
            groupChatId: String,
            contactListId: MutableList<String>
        ) {
            contactListId.forEach { contactId ->
                val userLink =
                    Firebase.firestore
                        .collection("users_talkers").document(contactId)
                        .collection("talkers").document(groupChatId)

                userLink.update("lastMessage", lastMessage)
                userLink.update("timeStamp", FieldValue.serverTimestamp())
            }
        }
    }
}