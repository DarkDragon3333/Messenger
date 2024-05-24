package com.example.messenger.utilis

import android.app.Activity
import android.content.Context
import androidx.navigation.NavHostController
import com.example.messenger.modals.User
import com.example.messenger.navigation.Screens
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

lateinit var AUTH: FirebaseAuth
lateinit var REF_DATABASE_ROOT: DatabaseReference
lateinit var USER: User
lateinit var UID: String //Уникальный индификационный номер

const val NODE_USERS = "users"
const val CHILD_ID = "id"
const val CHILD_PHONE = "phone"
const val CHILD_PASSWORD = "password"
const val CHILD_USER_NAME = "username"
const val CHILD_FULLNAME = "fullname"
const val CHILD_BIO = "bio"


fun initFirebase() {
    AUTH = FirebaseAuth.getInstance()
    REF_DATABASE_ROOT = FirebaseDatabase.getInstance().reference
    USER = User()
    UID = AUTH.currentUser?.uid.toString()
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
                makeToast("Данные обновлены!", context)
                choseChangeInformation(changeInfo, typeInfo)
                navController.navigate(Screens.Settings.route) {}
            }
        }

}

fun choseChangeInformation(changeInfo: String, typeInfo: String) {
    when (typeInfo) {
        CHILD_FULLNAME -> {
            USER.fullname = changeInfo
        }

        CHILD_USER_NAME -> {
            USER.username = changeInfo
        }

        CHILD_BIO -> {
            USER.bio = changeInfo
        }

        CHILD_PHONE -> {
            USER.phone = changeInfo
        }

        CHILD_PASSWORD -> {
            USER.password = changeInfo
        }
    }

}





