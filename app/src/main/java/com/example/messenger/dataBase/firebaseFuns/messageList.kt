package com.example.messenger.dataBase.firebaseFuns

import android.content.ContentValues
import android.util.Log
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.example.messenger.modals.MessageModal
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query

fun listeningUpdateChat(
    chatScreenState: SnapshotStateList<MessageModal>,
    messLink: Query,
): ListenerRegistration {
    val listing = messLink.addSnapshotListener { snapshots, e ->
        if (e != null) {
            Log.w(ContentValues.TAG, "listen:error", e)
            return@addSnapshotListener
        }

        for (document in snapshots!!.documentChanges) {
            when (document.type) {
                DocumentChange.Type.ADDED -> {
                    val newMessage = document.document.toObject(MessageModal::class.java)
                    if (chatScreenState.none { it.id == newMessage.id }) {
                        chatScreenState.add(0, newMessage)
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

fun initChat(
    chatScreenState: SnapshotStateList<MessageModal>,
    messLink: Query,
    function: () -> Unit
) {
    messLink
        .get()
        .addOnSuccessListener { result ->
            val cacheMessages =
                result.documents.map { it.toObject(MessageModal::class.java)!! }.toMutableList()
            chatScreenState.clear()
            chatScreenState.addAll(cacheMessages.distinctBy { it.id })
            function()
        }
        .addOnFailureListener { exception ->
            Log.w(ContentValues.TAG, "Error getting documents.", exception)
        }
}