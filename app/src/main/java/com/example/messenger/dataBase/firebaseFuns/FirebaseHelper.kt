package com.example.messenger.dataBase.firebaseFuns

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.navigation.NavHostController
import com.example.messenger.dataBase.valueEventListenerClasses.AppStatus
import com.example.messenger.modals.ContactModal
import com.example.messenger.modals.GroupChatModal
import com.example.messenger.modals.User
import com.example.messenger.modals.setLocalDataForUser
import com.example.messenger.screens.loginAndSignUp.LoginActivity
import com.example.messenger.utils.Constants
import com.example.messenger.utils.Constants.CHILD_FROM
import com.example.messenger.utils.Constants.CHILD_FULLNAME
import com.example.messenger.utils.Constants.CHILD_ID
import com.example.messenger.utils.Constants.CHILD_INFO
import com.example.messenger.utils.Constants.CHILD_LAST_MESSAGE
import com.example.messenger.utils.Constants.CHILD_PHOTO_URL
import com.example.messenger.utils.Constants.CHILD_STATUS
import com.example.messenger.utils.Constants.CHILD_TIME_STAMP
import com.example.messenger.utils.Constants.CHILD_TYPE
import com.example.messenger.utils.Constants.CHILD_USER_NAME
import com.example.messenger.utils.Constants.FOLDER_MESSAGE_FILE
import com.example.messenger.utils.Constants.FOLDER_PHOTOS
import com.example.messenger.utils.Constants.NODE_MESSAGES
import com.example.messenger.utils.Constants.NODE_PHONES
import com.example.messenger.utils.Constants.NODE_PHONES_CONTACTS
import com.example.messenger.utils.Constants.NODE_USERNAMES
import com.example.messenger.utils.Constants.NODE_USERS
import com.example.messenger.utils.Constants.TYPE_FILE
import com.example.messenger.utils.contactsListUSER
import com.example.messenger.utils.getFileName
import com.example.messenger.utils.get_out_from_auth
import com.example.messenger.utils.goTo
import com.example.messenger.utils.mainActivityContext
import com.example.messenger.utils.makeToast
import com.example.messenger.utils.mapContacts
import com.example.messenger.utils.sign_in
import com.google.firebase.Firebase
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.firestore
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.io.File

lateinit var AUTH: FirebaseAuth
lateinit var REF_DATABASE_ROOT: DatabaseReference
lateinit var REF_STORAGE_ROOT: StorageReference
lateinit var USER: User
lateinit var UID: String //Уникальный индификационный номер

fun checkUsername(changeInfo: String, context: Context, navController: NavHostController) {
    REF_DATABASE_ROOT.child(NODE_USERNAMES).addListenerForSingleValueEvent(object :
        ValueEventListener { //Создаём запрос на проверку ника.
        override fun onDataChange(snapshot: DataSnapshot) {
            val usernames = snapshot.children.map { snapshot.value.toString() }.toString()
            val regex = Regex("[^\\w\\d_]+")
            val tempArray = usernames.split("=").toMutableList()
            val oldUserName = tempArray[0].replace(regex, "")

            when (oldUserName == changeInfo.lowercase()) {
                true -> makeToast("Имя пользователя занято", mainActivityContext)

                false -> changeUserName()
            }
        }

        private fun changeUserName() {
            deleteOldUsername() //И удаляем старый ник из базы
            REF_DATABASE_ROOT.child(NODE_USERNAMES).child(changeInfo.lowercase())
                .setValue(UID) //Если нет, то записываем в ноду никнеймов ник

            changeInfo(
                changeInfo,
                CHILD_USER_NAME,
                context,
                navController
            )//Переходим на страницу настроек

        }

        private fun deleteOldUsername() {
            REF_DATABASE_ROOT.child(NODE_USERNAMES).child(USER.username).removeValue()
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        setLocalDataForUser(
                            changeInfo.lowercase(),
                            CHILD_USER_NAME
                        )  //И обновляем нашу локальную модель пользователя
                    }
                }

        }

        override fun onCancelled(error: DatabaseError) {
            makeToast(error.message, mainActivityContext)
        }
    })
}

fun downloadImage(context: Context, navController: NavHostController) {
    //Загружаем фото пользователя
    val pathToPhoto = REF_STORAGE_ROOT.child(FOLDER_PHOTOS).child(UID)
    pathToPhoto.downloadUrl.addOnCompleteListener { downloadTask -> //Получаем ссылку на загруженную фотку

        when (downloadTask.isSuccessful) {
            true ->
                changeInfo(
                    downloadTask.result.toString(),
                    CHILD_PHOTO_URL,
                    context,
                    navController
                )

            false -> makeToast(downloadTask.exception?.message.toString(), context)
        }
    }
}

fun updateContactsForFirebase(contactList: MutableList<ContactModal>) {
    var newContact: ContactModal
    REF_DATABASE_ROOT.child(NODE_PHONES)
        .addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                snapshot.children.forEach { itSnapshot ->
                    contactList.forEach { itContact ->
                        if (itSnapshot.key.toString() == itContact.phone) {
                            REF_DATABASE_ROOT.child(NODE_PHONES_CONTACTS)
                                .child(UID).child(itContact.phone).child(CHILD_ID)
                                .setValue(itSnapshot.value.toString())
                            contactsListUSER.add(itSnapshot.value.toString())
                        }
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                makeToast(error.message, mainActivityContext)
            }

        })

    REF_DATABASE_ROOT.child(NODE_USERS).addChildEventListener(object :
        ChildEventListener {
        override fun onChildAdded(
            snapshot: DataSnapshot,
            previousChildName: String?
        ) {
            changeListOfContacts(snapshot)
        }

        override fun onChildChanged(
            snapshot: DataSnapshot,
            previousChildName: String?
        ) {
            changeListOfContacts(snapshot)
        }

        override fun onChildRemoved(snapshot: DataSnapshot) {}

        override fun onChildMoved(
            snapshot: DataSnapshot,
            previousChildName: String?
        ) {
        }

        override fun onCancelled(error: DatabaseError) {
            makeToast(error.message.toString(), mainActivityContext)
        }

        private fun changeListOfContacts(snapshot: DataSnapshot) {
            newContact = snapshot.getValue(ContactModal::class.java) ?: ContactModal()
            contactsListUSER.forEach { contact ->
                if (newContact.id == contact) mapContacts[newContact.id] = newContact
            }
        }

    })
}

fun sendMessage(
    info: String,
    receivingUserID: String?,
    typeMessage: String,
    key: String,
    callback: () -> Unit
) {
    val db = Firebase.firestore

    try {
        val refDialogUser =
            receivingUserID?.let {
                db
                    .collection("users_messages").document(UID)
                    .collection("messages").document(it)
            }

        val refDialogReceivingUser =
            receivingUserID?.let {
                db
                    .collection("users_messages").document(it)
                    .collection("messages").document(UID)
            }

        val messageKey = key.ifEmpty {
            db.collection(refDialogUser.toString()).document().id
        }

        val mapMessage = hashMapOf<String, Any>()
        mapMessage[CHILD_ID] = messageKey
        mapMessage[CHILD_FROM] = UID
        mapMessage[CHILD_INFO] = info
        mapMessage[CHILD_TYPE] = typeMessage
        mapMessage[CHILD_TIME_STAMP] = FieldValue.serverTimestamp()


        val mapDialog = hashMapOf<String, Any>()
        mapDialog["$refDialogUser/$messageKey"] = mapMessage
        mapDialog["$refDialogReceivingUser/$messageKey"] = mapMessage

        mapDialog["$refDialogUser/$messageKey"]?.let {
            refDialogUser?.collection("TheirMessages")?.document(messageKey)?.set(
                it
            )
        }

        mapDialog["$refDialogReceivingUser/$messageKey"]?.let {
            refDialogReceivingUser?.collection("TheirMessages")?.document(messageKey)?.set(
                it
            )
        }

        callback()

    } catch (e: Exception) {
        makeToast(e.message.toString() + " ошибка отправки", mainActivityContext)
    }
}

fun sendMessageToGroupChat(
    info: String,
    groupChatId: String,
    contactListId: MutableList<String>,
    typeMessage: String,
    key: String,
    callback: () -> Unit
) {
    try {
        val db = Firebase.firestore

        val messageKey = key.ifEmpty {
            db.collection(groupChatId).document().id
        }

        val mapMessage = hashMapOf<String, Any>()
        mapMessage[CHILD_ID] = messageKey
        mapMessage[CHILD_FROM] = UID
        mapMessage[CHILD_INFO] = info
        mapMessage[CHILD_TYPE] = typeMessage
        mapMessage[CHILD_TIME_STAMP] = FieldValue.serverTimestamp()


        contactListId.forEach { contactId ->
            val userLink =
                db.collection("users_messages").document(contactId).collection("messages")
                    .document(groupChatId)

            userLink.collection("TheirMessages").document(messageKey).set(mapMessage)
                .addOnCompleteListener { task ->
                    when (task.isSuccessful) {
                        true -> {
                            callback()
                            Log.d("SendMessage", "Все сообщения успешно отправлены.")
                        }

                        false -> {
                            Log.e("SendMessage", "Ошибка отправки в Firestore: ")
                        }
                    }

                }
        }


    } catch (e: Exception) {
        makeToast("Ошибка отправки: " + e.message.toString(), mainActivityContext)
    }
}


fun getMessageKey(receivingUserID: String) = REF_DATABASE_ROOT.child(NODE_MESSAGES)
    .child(UID)
    .child(receivingUserID)
    .push().key.toString()

fun uploadFileToStorage(
    filesToUpload: List<Pair<String, @JvmSuppressWildcards Uri>>,
    receivingUserID: String,
    typeMessage: String,
    typeChat: String,
    contactList: MutableList<String> = mutableListOf<String>()
) {
    CoroutineScope(Dispatchers.IO).launch {
        filesToUpload.forEach { (messageKey, fileUri) ->
            val storageReference = REF_STORAGE_ROOT.child(FOLDER_MESSAGE_FILE).child(messageKey)

            try {
                storageReference.putFile(fileUri).await()
                val downloadUrl = storageReference.downloadUrl.await().toString()
                when (typeMessage) {
                    TYPE_FILE -> {
                        val fileName = getFileName(mainActivityContext, fileUri)
                        when (typeChat) {
                            "group" -> sendMessageToGroupChat(
                                downloadUrl + "__" + fileName,
                                receivingUserID,
                                contactList,
                                typeMessage,
                                messageKey
                            ) { }

                            else -> {
                                sendMessage(
                                    downloadUrl + "__" + fileName,
                                    receivingUserID,
                                    typeMessage,
                                    messageKey
                                ) {}
                            }
                        }

                    }

                    else -> {
                        when (typeChat) {
                            "group" ->
                                sendMessageToGroupChat(
                                    downloadUrl,
                                    receivingUserID,
                                    contactList,
                                    typeMessage,
                                    messageKey
                                ) { }


                            else -> {
                                sendMessage(
                                    downloadUrl,
                                    receivingUserID,
                                    typeMessage,
                                    messageKey
                                ) {}
                            }
                        }
                    }
                }

            } catch (e: Exception) {
                makeToast("Ошибка загрузки файла: ${e.message}", mainActivityContext)
            }
        }
    }

}

fun getFile(mAudioFile: File, fileUrl: String, function: () -> Unit) {
    val path = REF_STORAGE_ROOT.storage.getReferenceFromUrl(fileUrl)
    path.getFile(mAudioFile)
        .addOnSuccessListener {
            function()
        }
        .addOnFailureListener {
            makeToast(it.message.toString(), mainActivityContext)
        }
}

fun singOutFromApp() {
    AppStatus.updateStates(AppStatus.OFFLINE, mainActivityContext)
    get_out_from_auth = true
    sign_in = true
    AUTH.signOut()
    goTo(LoginActivity::class.java, mainActivityContext)
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
        mapChat[CHILD_FULLNAME] = infoArray[0]
        mapChat[CHILD_PHOTO_URL] = infoArray[1]
        mapChat[CHILD_ID] = infoArray[2]
        mapChat[CHILD_STATUS] = infoArray[3]
        mapChat[CHILD_TYPE] = infoArray[4]
        mapChat[CHILD_LAST_MESSAGE] = infoArray[5]
        mapChat[CHILD_TIME_STAMP] = "00:00:00"

        val mapReceivingUserChat = hashMapOf<String, Any>()
        mapReceivingUserChat[CHILD_FULLNAME] = USER.fullname
        mapReceivingUserChat[CHILD_PHOTO_URL] = USER.photoUrl
        mapReceivingUserChat[CHILD_ID] = USER.id
        mapReceivingUserChat[CHILD_STATUS] = USER.status
        mapReceivingUserChat[CHILD_TYPE] = infoArray[4]
        mapReceivingUserChat[CHILD_LAST_MESSAGE] = infoArray[5]
        mapReceivingUserChat[CHILD_TIME_STAMP] = "00:00:00"

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

fun addGroupChatToChatsList(
    mapInfo: HashMap<String, Any>,
    contactListId: MutableList<String>,
    context: Context,
    goToGroupChat: (timeStamp: Timestamp?) -> Unit
) {
    try {
        contactListId.forEach { contactId ->
            val userLink =
                Firebase.firestore
                    .collection("users_talkers").document(contactId)
                    .collection("talkers").document(mapInfo[CHILD_ID].toString())

            userLink.set(mapInfo)
        }
        getTimeStamp(mapInfo) { timeStamp ->
            goToGroupChat(timeStamp)
        }


    } catch (e: Exception) {
        Log.e("KotltalkApp", e.message.toString())
        makeToast(e.message.toString(), context)
    }
}

fun getTimeStamp(mapInfo: HashMap<String, Any>, returnTimeStamp: (timeStamp: Timestamp?) -> Unit) {
    Firebase.firestore
        .collection("users_talkers").document(UID)
        .collection("talkers").document(mapInfo[CHILD_ID].toString()).get()
        .addOnCompleteListener { result ->
            if (result.isSuccessful) {
                val groupChatModal = result.result.toObject(GroupChatModal::class.java)
                val timeStamp = groupChatModal?.timeStamp
                returnTimeStamp(timeStamp)
            }
        }
}