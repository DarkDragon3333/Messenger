package com.example.messenger.utilis

import com.example.messenger.modals.User
import com.example.messenger.user_sing_in_and_up.LoginActivity
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


fun initFirebase() {
    AUTH = FirebaseAuth.getInstance()
    REF_DATABASE_ROOT = FirebaseDatabase.getInstance().reference
    USER = User()
    UID = AUTH.currentUser?.uid.toString()
}

fun initUser(context: LoginActivity) {
    REF_DATABASE_ROOT
        .child(NODE_USERS)
        .child(UID)
        .addListenerForSingleValueEvent(
            object : ValueEventListener { //Один раз при запуске обновляем наши данные
                override fun onDataChange(snapshot: DataSnapshot) {
                    USER = snapshot.getValue(User::class.java) ?: User() //Получаем данные через переменную snapshot. Если будет null поле, то вы инициализируем пустым пользователем
                }

                override fun onCancelled(error: DatabaseError) {
                    makeToast("Ошибка", context)
                }
            }
        )
}





