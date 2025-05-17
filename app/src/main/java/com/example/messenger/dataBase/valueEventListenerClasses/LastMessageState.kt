package com.example.messenger.dataBase.valueEventListenerClasses

import com.example.messenger.dataBase.UID
import com.example.messenger.modals.ChatModal
import com.example.messenger.utilsFilies.mainActivityContext
import com.example.messenger.utilsFilies.makeToast
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore

enum class LastMessageState(val lastMessage: String) {
    LAST_MESSAGE("Test");

    companion object {
        fun updateLastMessage(lastMessage: String, id: String) {
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
    }
}