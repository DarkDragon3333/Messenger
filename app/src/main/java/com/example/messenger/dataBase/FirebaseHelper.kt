package com.example.messenger.dataBase

import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.provider.ContactsContract
import android.provider.OpenableColumns
import android.util.Log
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.core.net.toUri
import androidx.navigation.NavHostController
import com.example.messenger.MainActivity
import com.example.messenger.modals.ContactModal
import com.example.messenger.modals.MessageModal
import com.example.messenger.modals.User
import com.example.messenger.modals.setLocalDataForUser
import com.example.messenger.navigation.Screens
import com.example.messenger.utilsFilies.Constants.CHILD_BIO
import com.example.messenger.utilsFilies.Constants.CHILD_FROM
import com.example.messenger.utilsFilies.Constants.CHILD_FULLNAME
import com.example.messenger.utilsFilies.Constants.CHILD_ID
import com.example.messenger.utilsFilies.Constants.CHILD_INFO
import com.example.messenger.utilsFilies.Constants.CHILD_PASSWORD
import com.example.messenger.utilsFilies.Constants.CHILD_PHONE
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
import com.example.messenger.utilsFilies.READ_CONTACTS
import com.example.messenger.utilsFilies.contactsListUSER
import com.example.messenger.utilsFilies.goTo
import com.example.messenger.utilsFilies.mainActivityContext
import com.example.messenger.utilsFilies.makeToast
import com.example.messenger.utilsFilies.mapContacts
import com.example.messenger.utilsFilies.myCheckPermission
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.firestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.io.File
import java.util.concurrent.TimeUnit

lateinit var AUTH: FirebaseAuth
lateinit var REF_DATABASE_ROOT: DatabaseReference
lateinit var REF_STORAGE_ROOT: StorageReference
lateinit var USER: User
lateinit var UID: String //Уникальный индификационный номер

fun initFirebase() {
    AUTH = FirebaseAuth.getInstance()
    REF_DATABASE_ROOT = FirebaseDatabase.getInstance().reference
    USER = User()
    UID = AUTH.currentUser?.uid.toString()
    REF_STORAGE_ROOT = FirebaseStorage.getInstance().reference

}

fun initUser(context: Activity) {
    REF_DATABASE_ROOT
        .child(NODE_USERS)
        .child(UID)
        .addListenerForSingleValueEvent(
            object : ValueEventListener { //Один раз при запуске обновляем наши данные
                override fun onDataChange(snapshot: DataSnapshot) {
                    USER = snapshot.getValue(User::class.java)
                        ?: User() //Получаем данные через переменную snapshot. Если будет null поле, то вы инициализируем пустым пользователем
                    if (AUTH.currentUser != null) { //Если пользователь уже есть
                        goTo(MainActivity::class.java, context)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    makeToast("Ошибка", context)
                }
            }
        )

}

fun authUser(
    context: Activity,
    phoneNumberFromSignUp: String,
    callback: PhoneAuthProvider.OnVerificationStateChangedCallbacks
) {
    PhoneAuthProvider.verifyPhoneNumber(
        PhoneAuthOptions
            .newBuilder(FirebaseAuth.getInstance())
            .setActivity(context)
            .setPhoneNumber(phoneNumberFromSignUp)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setCallbacks(callback)
            .build()
    )

}

fun choseChangeInformation(
    changeInfo: String,
    typeInfo: String,
    context: Context,
    navController: NavHostController
) {
    when (typeInfo) {
        CHILD_FULLNAME -> changeInfo(changeInfo, typeInfo, context, navController)

        CHILD_USER_NAME -> checkUsername(changeInfo, context, navController)

        CHILD_BIO -> changeInfo(changeInfo, typeInfo, context, navController)

        CHILD_PHONE -> changeInfo(changeInfo, typeInfo, context, navController)

        CHILD_PASSWORD -> changeInfo(changeInfo, typeInfo, context, navController)

        CHILD_PHOTO_URL -> downloadImage(context, navController)
    }
}

fun changeInfo(
    changeInfo: String,
    typeInfo: String,
    context: Context,
    navController: NavHostController
) {
    REF_DATABASE_ROOT
        .child(NODE_USERS)
        .child(UID)
        .child(typeInfo)
        .setValue(changeInfo).addOnCompleteListener {
            when (it.isSuccessful) {
                true -> {
                    setLocalDataForUser(changeInfo, typeInfo)
                    goTo(navController, Screens.Settings)
                }

                false -> {
                    makeToast("Ошибка", context)
                }
            }

        }
}

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

fun getContactsFromSmartphone() {
    if (myCheckPermission(READ_CONTACTS)) {
        val contactList = mutableListOf<ContactModal>()
        val cursor = mainActivityContext.contentResolver.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            null,
            null,
            null,
            null
        )

        cursor?.let {
            while (it.moveToNext()) {
                val fullName =
                    cursor.getString(
                        cursor.getColumnIndexOrThrow(
                            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME
                        )
                    )
                val phone =
                    cursor.getString(
                        cursor.getColumnIndexOrThrow(
                            ContactsContract.CommonDataKinds.Phone.NUMBER
                        )
                    )

                val newModal = ContactModal().apply { ContactModal().fullname = fullName }

                var pattern = Regex("[^\\d+]")
                var formattedPhone = phone.replace(pattern, "")

                formattedPhone =
                    if (!formattedPhone.startsWith("+")) "+$formattedPhone" else formattedPhone
                pattern = Regex("(\\+\\d+)(\\d{3})(\\d{3})(\\d{4})")

                formattedPhone = pattern.replace(formattedPhone) { match ->
                    "${match.groups[1]?.value}" +
                            " ${match.groups[2]?.value}" +
                            "-${match.groups[3]?.value}" +
                            "-${match.groups[4]?.value}"
                }

                newModal.phone = formattedPhone

                contactList.add(newModal)
            }
        }
        cursor?.close()

        updateContactsForFirebase(contactList)
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
            makeToast(error.message, mainActivityContext)
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
    function: () -> Unit
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

        function()

    } catch (e: Exception) {
        makeToast(e.message.toString() + " отправка багует", mainActivityContext)
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

                    else -> {
                        sendMessage(downloadUrl, receivingUserID, typeMessage, messageKey) {}
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

fun attachImage(launcher: ManagedActivityResultLauncher<PickVisualMediaRequest, List<@JvmSuppressWildcards Uri>>) {
    launcher.launch(
        PickVisualMediaRequest(
            mediaType = ActivityResultContracts.PickVisualMedia.ImageAndVideo
        )
    )
}

fun attachFile(
    launcherFile: ManagedActivityResultLauncher<String, List<@JvmSuppressWildcards Uri>>,
) {
    launcherFile.launch("*/*")
}

fun getFileName(context: Context, uri: Uri): String? {
    val returnCursor = context.contentResolver.query(uri, null, null, null, null)
    returnCursor?.use { cursor ->
        val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
        if (nameIndex != -1) {
            cursor.moveToFirst()
            return cursor.getString(nameIndex)
        }
    }
    return null
}
