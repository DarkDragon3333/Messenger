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
import com.example.messenger.modals.ContactModal
import com.example.messenger.modals.GroupChatModal
import com.example.messenger.modals.ChatItem
import com.example.messenger.modals.User
import com.example.messenger.utils.Constants.NODE_USERS
import com.example.messenger.utils.mainActivityContext
import com.example.messenger.utils.makeToast
import com.google.firebase.Firebase
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.firestore

class ChatsViewModal : ViewModel() {
    private val _chatsList = mutableStateListOf<ChatItem>()
    val chatsList: SnapshotStateList<ChatItem> get() = _chatsList

    private lateinit var listenerUsersChats: ListenerRegistration
    private lateinit var listingUpdateUserData: ChildEventListener
    private lateinit var listenerGroupChats: ListenerRegistration

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
                updateChatsListData()
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
                        val updateContactInfo =
                            if (document.document.get("type") == "group")
                                document.document.toObject(GroupChatModal::class.java)
                            else
                                document.document.toObject(ChatModal::class.java)

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

        listenerUsersChats = listing
    }

    fun updateChatsListData() {
        _chatsList.forEach { oldData ->
            if (oldData.type == "group") {

                var newData = GroupChatModal()
                Firebase.firestore
                    .collection("users_groups").document(oldData.id.toString()).get()
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            newData =
                                task.result.toObject(GroupChatModal::class.java) ?: GroupChatModal()

                            val mapInfo = mutableMapOf<String, Any>()

                            // Сравниваем и добавляем в map только то, что отличается
                            if (newData.chatName != oldData.chatName) {
                                mapInfo["chatName"] = newData.chatName
                            }
                            if (newData.photoUrl != oldData.photoUrl) {
                                mapInfo["photoUrl"] = newData.photoUrl
                            }
                            if (newData.status != oldData.status) {
                                mapInfo["status"] = newData.status
                            }

                            if (mapInfo.isNotEmpty()) {
                                Firebase.firestore
                                    .collection("users_talkers").document(UID)
                                    .collection("talkers").document(oldData.id.toString())
                                    .update(mapInfo)
                                    .addOnSuccessListener {
                                        Log.d(
                                            "updateChatsListData",
                                            "Обновлён документ ${oldData.id}"
                                        )
                                    }
                                    .addOnFailureListener {
                                        Log.e(
                                            "updateChatsListData",
                                            "Ошибка обновления: ${it.message}"
                                        )
                                    }


                            }
                        }
                    }
            } else {
                var newData = User()
                REF_DATABASE_ROOT.child(NODE_USERS).child(oldData.id.toString()).get()
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            newData = task.result.getValue<User>(User::class.java) ?: User()

                            val mapInfo = mutableMapOf<String, Any>()

                            if (newData.fullname != oldData.chatName) {
                                mapInfo["chatName"] = newData.fullname
                            }
                            if (newData.photoUrl != oldData.photoUrl) {
                                mapInfo["photoUrl"] = newData.photoUrl
                            }
                            if (newData.status != oldData.status) {
                                mapInfo["status"] = newData.status
                            }

                            if (mapInfo.isNotEmpty()) {
                                Firebase.firestore
                                    .collection("users_talkers").document(UID)
                                    .collection("talkers").document(oldData.id.toString())
                                    .update(mapInfo)
                                    .addOnSuccessListener {
                                        Log.d(
                                            "updateChatsListData",
                                            "Обновлён документ ${oldData.id}"
                                        )
                                    }
                                    .addOnFailureListener {
                                        Log.e(
                                            "updateChatsListData",
                                            "Ошибка обновления: ${it.message}"
                                        )
                                    }
                            }
                        }
                    }
            }
        }
    }

    fun listingUsersData() {
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
                    val updateStatus = snapshot.getValue(ContactModal::class.java) ?: ContactModal()

                    Firebase.firestore
                        .collection("users_talkers").document(UID)
                        .collection("talkers").document(updateStatus.id)
                        .update("status", updateStatus.status)

                    Firebase.firestore
                        .collection("users_talkers").document(UID)
                        .collection("talkers").document(updateStatus.id).update(
                            "chatName", updateStatus.fullname
                        )

                    Firebase.firestore
                        .collection("users_talkers").document(UID)
                        .collection("talkers").document(updateStatus.id).update(
                            "photoUrl", updateStatus.photoUrl
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

        listingUpdateUserData = listing
    }

    fun listingGroupChatData() {
        val listing = groupChatLink().addSnapshotListener { snapshots, e ->
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
                        val updateData = document.document.toObject<GroupChatModal>(
                            GroupChatModal::class.java
                        )

                        Firebase.firestore
                            .collection("users_talkers").document(UID)
                            .collection("talkers").document(updateData.id)
                            .update("status", updateData.lastMessage)

                        Firebase.firestore
                            .collection("users_talkers").document(UID)
                            .collection("talkers").document(updateData.id).update(
                                "chatName", updateData.chatName
                            )

                        Firebase.firestore
                            .collection("users_talkers").document(UID)
                            .collection("talkers").document(updateData.id).update(
                                "photoUrl", updateData.photoUrl
                            )

                    }

                    DocumentChange.Type.REMOVED -> Log.d(
                        ContentValues.TAG,
                        "Removed city: ${document.document.data}"
                    )

                }
            }
        }

        listenerGroupChats = listing
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

    fun groupChatLink(): CollectionReference {
        return Firebase.firestore
            .collection("users_groups")
    }

    fun removeListener() {
        if (::listenerUsersChats.isInitialized) {
            listenerUsersChats.remove()
            REF_DATABASE_ROOT.child(NODE_USERS).removeEventListener(listingUpdateUserData)
        }
        if (::listenerGroupChats.isInitialized){
            listenerGroupChats.remove()
        }
    }

}