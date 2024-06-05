package com.example.messenger.utilsFilies

import android.app.Activity
import android.content.Context
import android.provider.ContactsContract
import androidx.navigation.NavHostController
import com.example.messenger.modals.CommonModal
import com.example.messenger.modals.User
import com.example.messenger.modals.setLocalDataForUser
import com.example.messenger.navigation.Screens
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.util.concurrent.TimeUnit

lateinit var AUTH: FirebaseAuth
lateinit var REF_DATABASE_ROOT: DatabaseReference
lateinit var REF_STORAGE_ROOT: StorageReference
lateinit var USER: User
lateinit var UID: String //Уникальный индификационный номер


const val NODE_USERS = "users"
const val NODE_USERNAMES = "usernames"
const val NODE_PHONES = "phones"
const val NODE_PHONES_CONTACTS = "phones_contacts"

const val FOLDER_PHOTOS = "photos"

const val CHILD_ID = "id"
const val CHILD_PHONE = "phone"
const val CHILD_PASSWORD = "password"
const val CHILD_USER_NAME = "username"
const val CHILD_FULLNAME = "fullname"
const val CHILD_BIO = "bio"
const val CHILD_STATUS: String = "status"
const val CHILD_PHOTO_URL: String = "photoUrl"


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
        CHILD_FULLNAME -> {
            changeInfo(changeInfo, typeInfo, context, navController)
        }

        CHILD_USER_NAME -> {
            checkUsername(changeInfo, context, navController)
        }

        CHILD_BIO -> {
            changeInfo(changeInfo, typeInfo, context, navController)
        }

        CHILD_PHONE -> {
            changeInfo(changeInfo, typeInfo, context, navController)
        }

        CHILD_PASSWORD -> {
            changeInfo(changeInfo, typeInfo, context, navController)
        }

        CHILD_PHOTO_URL -> {
            downloadImage(context, navController)
        }
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

            if (oldUserName == changeInfo.lowercase()) { // Проверяем, есть ли такой ник в базе.
                makeToast(
                    "Имя пользователя занято",
                    context
                ) // Если есть, то выводим сообщение, что ник занят
            } else {
                changeUserName()
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
        if (downloadTask.isSuccessful) {
            val photoURL = downloadTask.result.toString()
            changeInfo(photoURL, CHILD_PHOTO_URL, context, navController)
        } else {
            makeToast(downloadTask.exception?.message.toString(), context)
        }
    }

}


fun initContacts() {
    if (myCheckPermission(READ_CONTACTS)) {
        val contactList = mutableListOf<CommonModal>()
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

                val newModal = CommonModal()
                newModal.fullname = fullName
                newModal.phone = phone.replace(Regex("[/s,-]"), "")

                contactList.add(newModal)
            }
        }
        cursor?.close()

        updateContactsForFirebase(contactList)
    }
}

fun updateContactsForFirebase(contactList: MutableList<CommonModal>) {
    var index = 0
    var user: CommonModal
    REF_DATABASE_ROOT.child(NODE_PHONES)
        .addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                snapshot.children.forEach { itSnapshot ->
                    contactList.forEach { itContact ->
                        if (itSnapshot.key.toString().replace("-", "") == itContact.phone) {
                            val pattern = Regex("(\\+\\d) (\\d{3})(\\d{3})(\\d{4})")
                            val formattedStr = pattern.replace(itContact.phone, "$1 $2-$3-$4")
                            REF_DATABASE_ROOT.child(NODE_PHONES_CONTACTS)
                                .child(UID).child(formattedStr).child(CHILD_ID)
                                .setValue(itSnapshot.value.toString())
                            index++
                            contactsList.add(itSnapshot.value.toString())
                        }
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                makeToast(error.message, mainActivityContext)
            }

        })

    REF_DATABASE_ROOT.child(NODE_USERS).addChildEventListener(object :
        ChildEventListener{
        override fun onChildAdded(
            snapshot: DataSnapshot,
            previousChildName: String?
        ) {
            user = snapshot.getValue(CommonModal::class.java)
                ?: CommonModal()
            contactsList.forEach{contact ->
                if (user.id == contact){
                    mapContacts[user.id] = user
                }
            }

        }
        override fun onChildChanged(
            snapshot: DataSnapshot,
            previousChildName: String?
        ) {
            user = snapshot.getValue(CommonModal::class.java)
                ?: CommonModal()
            mapContacts[user.id] = user
        }

        override fun onChildRemoved(snapshot: DataSnapshot) {}

        override fun onChildMoved(
            snapshot: DataSnapshot,
            previousChildName: String?
        ) {}

        override fun onCancelled(error: DatabaseError) {
            makeToast(error.message, mainActivityContext)
        }

    })
}