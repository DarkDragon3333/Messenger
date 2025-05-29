package com.example.messenger.dataBase.firebaseFuns

import android.content.Context
import android.net.Uri
import androidx.navigation.NavHostController
import com.example.messenger.dataBase.valueEventListenerClasses.AppStatus
import com.example.messenger.modals.ContactModal
import com.example.messenger.modals.User
import com.example.messenger.modals.setLocalDataForUser
import com.example.messenger.screens.loginAndSignUp.LoginActivity
import com.example.messenger.utilsFilies.Constants.CHILD_FROM
import com.example.messenger.utilsFilies.Constants.CHILD_ID
import com.example.messenger.utilsFilies.Constants.CHILD_INFO
import com.example.messenger.utilsFilies.Constants.CHILD_PHOTO_URL
import com.example.messenger.utilsFilies.Constants.CHILD_TIME_STAMP
import com.example.messenger.utilsFilies.Constants.CHILD_TYPE
import com.example.messenger.utilsFilies.Constants.CHILD_USER_NAME
import com.example.messenger.utilsFilies.Constants.FOLDER_MESSAGE_FILE
import com.example.messenger.utilsFilies.Constants.FOLDER_PHOTOS
import com.example.messenger.utilsFilies.Constants.NODE_MESSAGES
import com.example.messenger.utilsFilies.Constants.NODE_PHONES
import com.example.messenger.utilsFilies.Constants.NODE_PHONES_CONTACTS
import com.example.messenger.utilsFilies.Constants.NODE_USERNAMES
import com.example.messenger.utilsFilies.Constants.NODE_USERS
import com.example.messenger.utilsFilies.Constants.TYPE_FILE
import com.example.messenger.utilsFilies.contactsListUSER
import com.example.messenger.utilsFilies.getFileName
import com.example.messenger.utilsFilies.get_out_from_auth
import com.example.messenger.utilsFilies.goTo
import com.example.messenger.utilsFilies.mainActivityContext
import com.example.messenger.utilsFilies.makeToast
import com.example.messenger.utilsFilies.mapContacts
import com.example.messenger.utilsFilies.sign_in
import com.google.firebase.Firebase
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


fun getMessageKey(receivingUserID: String) = REF_DATABASE_ROOT.child(NODE_MESSAGES)
    .child(UID)
    .child(receivingUserID)
    .push().key.toString()

fun uploadFileToStorage(
    filesToUpload: List<Pair<String, @JvmSuppressWildcards Uri>>,
    receivingUserID: String,
    typeMessage: String
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
                        sendMessage(
                            downloadUrl + "__" + fileName,
                            receivingUserID,
                            typeMessage,
                            messageKey
                        ) {}
                    }

                    else ->
                        sendMessage(downloadUrl, receivingUserID, typeMessage, messageKey) {}

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

fun singOutFromApp(){
    AppStatus.Companion.updateStates(AppStatus.OFFLINE, mainActivityContext)
    get_out_from_auth = true
    sign_in = true
    AUTH.signOut()
    goTo(LoginActivity::class.java, mainActivityContext)
}