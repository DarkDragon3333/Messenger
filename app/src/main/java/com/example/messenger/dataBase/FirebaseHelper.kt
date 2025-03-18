package com.example.messenger.dataBase

import android.app.Activity
import android.content.Context
import android.net.Uri
import android.provider.ContactsContract
import androidx.navigation.NavHostController
import com.example.messenger.MainActivity
import com.example.messenger.modals.CommonModal
import com.example.messenger.modals.ContactModal
import com.example.messenger.modals.User
import com.example.messenger.modals.setLocalDataForUser
import com.example.messenger.navigation.Screens
import com.example.messenger.utilsFilies.READ_CONTACTS
import com.example.messenger.utilsFilies.contactsListUSER
import com.example.messenger.utilsFilies.goTo
import com.example.messenger.utilsFilies.mainActivityContext
import com.example.messenger.utilsFilies.makeToast
import com.example.messenger.utilsFilies.mapContacts
import com.example.messenger.utilsFilies.myCheckPermission
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ServerValue
import com.google.firebase.database.ValueEventListener
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

const val TYPE_TEXT = "text"
const val TYPE_VOICE = "voice"
const val TYPE_IMAGE = "image"
const val TYPE_VIDEO = "video"

const val NODE_USERS = "users"
const val NODE_USERNAMES = "usernames"
const val NODE_PHONES = "phones"
const val NODE_PHONES_CONTACTS = "phones_contacts"
const val NODE_MESSAGES = "messages"

const val FOLDER_PHOTOS = "photos"
const val FOLDER_MESSAGE_FILE = "files"

const val CHILD_ID = "id"
const val CHILD_PHONE = "phone"
const val CHILD_PASSWORD = "password"
const val CHILD_USER_NAME = "username"
const val CHILD_FULLNAME = "fullname"
const val CHILD_BIO = "bio"
const val CHILD_STATUS: String = "status"
const val CHILD_PHOTO_URL: String = "photoUrl"
const val CHILD_FILE_URL: String = "fileUrl"

const val CHILD_TEXT: String = "text"
const val CHILD_INFO: String = "info"
const val CHILD_TYPE: String = "type"
const val CHILD_FROM: String = "from"
const val CHILD_TIME_STAMP: String = "timeStamp"

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
            if (it.isSuccessful) {
                setLocalDataForUser(changeInfo, typeInfo)
                goTo(navController, Screens.Settings)
            } else
                makeToast("Ошибка", context)
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

            if (oldUserName == changeInfo.lowercase()) // Проверяем, есть ли такой ник в базе.
                makeToast(
                    "Имя пользователя занято",
                    context
                ) // Если есть, то выводим сообщение, что ник занят
            else
                changeUserName()

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
                        )  //И обновляем нашу локальную модель юзера
                    }
                }

        }

        override fun onCancelled(error: DatabaseError) {
            makeToast(error.message, context)
        }
    })
}

fun downloadImage(context: Context, navController: NavHostController) {
    //Загружаем фото пользователя
    val pathToPhoto = REF_STORAGE_ROOT.child(FOLDER_PHOTOS).child(UID)
    pathToPhoto.downloadUrl.addOnCompleteListener { downloadTask -> //Получаем ссылку на загруженную фотку
        if (downloadTask.isSuccessful)
            changeInfo(downloadTask.result.toString(), CHILD_PHOTO_URL, context, navController)
        else
            makeToast(downloadTask.exception?.message.toString(), context)
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

                val newModal = ContactModal()
                newModal.fullname = fullName

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
    var user: ContactModal
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
            user = snapshot.getValue(ContactModal::class.java) ?: ContactModal()
            contactsListUSER.forEach { contact ->
                if (user.id == contact) mapContacts[user.id] = user
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
    val refDialogUser = "$NODE_MESSAGES/$UID/$receivingUserID"
    val refDialogReceivingUser = "$NODE_MESSAGES/$receivingUserID/$UID"
    var messageKey = ""

    messageKey = key.ifEmpty { REF_DATABASE_ROOT.child(refDialogUser).push().key.toString() }

    val mapMessage = hashMapOf<String, Any>()
    mapMessage[CHILD_ID] = messageKey
    mapMessage[CHILD_FROM] = UID
    mapMessage[CHILD_INFO] = info
    mapMessage[CHILD_TYPE] = typeMessage
    mapMessage[CHILD_TIME_STAMP] = ServerValue.TIMESTAMP

    val mapDialog = hashMapOf<String, Any>()
    mapDialog["$refDialogUser/$messageKey"] = mapMessage
    mapDialog["$refDialogReceivingUser/$messageKey"] = mapMessage

    REF_DATABASE_ROOT
        .updateChildren(mapDialog)
        .addOnSuccessListener { function() }
        .addOnFailureListener { makeToast(it.message.toString(), mainActivityContext) }
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
            val tempUri = REF_STORAGE_ROOT.child(FOLDER_MESSAGE_FILE).child(messageKey)

            try {
                tempUri.putFile(fileUri).await()
                val downloadUrl = tempUri.downloadUrl.await().toString()
                sendMessage(downloadUrl, receivingUserID, typeMessage, messageKey) {}
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