package com.example.messenger.utilis

import android.app.Activity
import android.content.Context
import androidx.navigation.NavHostController
import com.example.messenger.modals.User
import com.example.messenger.modals.setLocalDataForUser
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
const val NODE_USERNAMES = "usernames"
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
                navController.navigate(Screens.Settings.route) {
                    launchSingleTop = true
                }
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

            if (oldUserName == changeInfo.lowercase()) {// Проверяем, есть ли такой ник в базе.
                makeToast(
                    "Имя пользователя занято",
                    context
                )// Если есть, то выводим сообщение, что ник занят
            } else {
                changeUserName()
            }
        }

        private fun changeUserName() {
            deleteOldUsername() //И удаляем старый ник из базы
            REF_DATABASE_ROOT.child(NODE_USERNAMES).child(changeInfo.lowercase())
                .setValue(UID) //Если нет, то записываем в ноду никнеймов ник
            REF_DATABASE_ROOT.child(NODE_USERS).child(UID).child(CHILD_USER_NAME)
                .setValue(changeInfo) //И записываем в юзера новый ник

            makeToast("Ник пользователя изменён", context)
            navController.navigate(Screens.Settings.route) {
                launchSingleTop = true
            } //Переходим на страницу настроек
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





