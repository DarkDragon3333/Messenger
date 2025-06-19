package com.example.messenger.viewModals

import android.content.ContentValues
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.network.HttpException
import com.example.messenger.dataBase.firebaseFuns.REF_DATABASE_ROOT
import com.example.messenger.dataBase.firebaseFuns.UID
import com.example.messenger.dataBase.notification.ChatState
import com.example.messenger.dataBase.notification.FmcApi
import com.example.messenger.dataBase.notification.NotificationBody
import com.example.messenger.dataBase.notification.SendMessageDto
import com.example.messenger.modals.ChatModal
import com.example.messenger.modals.ContactModal
import com.example.messenger.modals.User
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
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.create

class ChatViewModal : ViewModel() {
    private val api: FmcApi = Retrofit.Builder()
        .baseUrl("http://10.0.2.2:8080/")
        .addConverterFactory(MoshiConverterFactory.create())
        .build()
        .create()

    var state by mutableStateOf(ChatState())
        private set

    fun onRemoteTokenChange(newToken: String){
        state = state.copy(
            remoteToken = newToken
        )
    }

    fun onSubmitRemoteToken() {
        state = state.copy(
            isEnteringToken = false
        )
    }

    fun onSubmitRemoteToken(message: String) {
        state = state.copy(
            messageText = message
        )
    }

    fun sendMessage(isBroadcast: Boolean){
        viewModelScope.launch {

            val messageDto = SendMessageDto(
                to = if(isBroadcast) null else state.remoteToken,
                notification = NotificationBody(
                    title = "My new Message",
                    body = state.messageText
                )
            )

            try {
                if (isBroadcast)
                    api.broadcast(messageDto)
                else
                    api.sendMessage(messageDto)

                state = state.copy(
                    messageText = ""
                )
            } catch (e: HttpException) {
                e.printStackTrace()
            }
            catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }


    private lateinit var listenerRegistration: ListenerRegistration
    private lateinit var listingUpdateUserStatus: ChildEventListener

    private val _chatName = mutableStateOf<String>("")
    val fullName get() = _chatName

    private val _photoUrl = mutableStateOf<String>("")
    val photoUrl get() = _photoUrl

    private val _status = mutableStateOf<String>("")
    val status get() = _status

    fun initDataTitle(chatModal: ChatModal?){
        _chatName.value = chatModal?.chatName.toString()
        _photoUrl.value = chatModal?.photoUrl.toString()
        _status.value = chatModal?.status.toString()
    }

    fun removeDataTitle(){
        _chatName.value = ""
        _photoUrl.value = ""
        _status.value = ""
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
                        val newInfo = document.document.toObject(ChatModal::class.java)

                        _chatName.value = newInfo.chatName
                        _photoUrl.value = newInfo.photoUrl
                        _status.value = newInfo.status
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
                    val updateStatus = snapshot.getValue(ContactModal::class.java) ?: ContactModal()

                    Firebase.firestore
                        .collection("users_talkers").document(UID)
                        .collection("talkers").document(updateStatus.id)
                        .update("status", updateStatus.status)

                    Firebase.firestore
                        .collection("users_talkers").document(UID)
                        .collection("talkers").document(updateStatus.id)
                        .update("chatName", updateStatus.fullname)

                    Firebase.firestore
                        .collection("users_talkers").document(UID)
                        .collection("talkers").document(updateStatus.id)
                        .update("photoUrl", updateStatus.photoUrl)
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