package com.example.messenger.dataBase.valueEventListenerClasses

import com.example.messenger.dataBase.firebaseFuns.UID
import com.example.messenger.utilsFilies.mainActivityContext
import com.example.messenger.utilsFilies.makeToast
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore

class LastMessageState() {


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