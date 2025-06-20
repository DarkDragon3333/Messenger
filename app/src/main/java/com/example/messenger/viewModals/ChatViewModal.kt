package com.example.messenger.viewModals

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import coil.network.HttpException
import com.example.messenger.dataBase.firebaseFuns.REF_DATABASE_ROOT
import com.example.messenger.dataBase.firebaseFuns.UID
import com.example.messenger.dataBase.notification.ChatState
import com.example.messenger.dataBase.notification.FmcApi
import com.example.messenger.dataBase.notification.NotificationBody
import com.example.messenger.dataBase.notification.SendMessageDto
import com.example.messenger.modals.ChatModal
import com.example.messenger.utils.Constants.NODE_USERS
import com.example.messenger.utils.mainActivityContext
import com.example.messenger.utils.makeToast
import com.google.firebase.Firebase
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.firestore
import com.google.firebase.messaging.messaging
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.create

class ChatViewModal : ViewModel() {

    private val myMoshi: Moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    private val api: FmcApi = Retrofit.Builder()
        .baseUrl("http://10.0.2.2:8080")
        .addConverterFactory(MoshiConverterFactory.create(myMoshi))
        .build()
        .create()

    var state by mutableStateOf(ChatState())
        private set

    init {
        viewModelScope.launch {
            Firebase.messaging.subscribeToTopic("chat").await()
        }
    }

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

    fun onMessageChange(message: String) {
        state = state.copy(
            messageText = message
        )
    }

    fun sendMessage(isBroadcast: Boolean){
        viewModelScope.launch {

            val messageDto = SendMessageDto(
                to = if(isBroadcast) null else state.remoteToken,
                notification = NotificationBody(
                    title = "Залупа меесенджер",
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

    fun updateDataTitle(chatModal: ChatModal?){
        _chatName.value = chatModal?.chatName.toString()
        _photoUrl.value = chatModal?.photoUrl.toString()
        _status.value = chatModal?.status.toString()
    }

    fun removeDataTitle(){
        _chatName.value = ""
        _photoUrl.value = ""
        _status.value = ""
    }

    fun listingUsersData(userId: String) {
        val listing =
            REF_DATABASE_ROOT.child(NODE_USERS).child(userId).addChildEventListener(object : ChildEventListener {
                override fun onChildAdded(
                    snapshot: DataSnapshot,
                    previousChildName: String?
                ) {

                }

                override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                    val key = snapshot.key
                    val talkerDocRef = Firebase.firestore
                        .collection("users_talkers").document(UID)
                        .collection("talkers").document(userId)

                    when (key) {
                        "status" -> {
                            val status = snapshot.getValue(String::class.java) ?: return
                            talkerDocRef.update("status", status)
                        }

                        "fullname" -> {
                            val name = snapshot.getValue(String::class.java) ?: return
                            talkerDocRef.update("chatName", name)
                        }

                        "photoUrl" -> {
                            val photoUrl = snapshot.getValue(String::class.java) ?: return
                            talkerDocRef.update("photoUrl", photoUrl)
                        }
                    }
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

    fun startListingChatDataForTitle(chatId: String) {
        val listing2 = chatLink(chatId).addSnapshotListener { snapshot, e ->
                if (e != null) {
                    Log.e("Firestore", "Ошибка прослушивания", e)
                    return@addSnapshotListener
                }

                if (snapshot != null && snapshot.exists()) {
                    val chatData = snapshot.toObject(ChatModal::class.java)
                    updateDataTitle(chatData)

                } else {
                    Log.d("Firestore", "Документ не найден")
                }

        }

        listenerRegistration = listing2
    }

    fun chatLink(chatId: String): DocumentReference {
        return Firebase.firestore
            .collection("users_talkers").document(UID)
            .collection("talkers").document(chatId)
    }

    fun removeListener() {
        if (::listenerRegistration.isInitialized) {
            listenerRegistration.remove()
            REF_DATABASE_ROOT.child(NODE_USERS).removeEventListener(listingUpdateUserStatus)
        }
    }
}