package com.example.messenger.dataBase.firebaseFuns

import android.app.Activity
import com.example.messenger.MainActivity
import com.example.messenger.modals.User
import com.example.messenger.utilsFilies.Constants
import com.example.messenger.utilsFilies.goTo
import com.example.messenger.utilsFilies.makeToast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import java.util.concurrent.TimeUnit

fun initFirebase() {
    AUTH = FirebaseAuth.getInstance()
    REF_DATABASE_ROOT = FirebaseDatabase.getInstance().reference
    USER = User()
    UID = AUTH.currentUser?.uid.toString()
    REF_STORAGE_ROOT = FirebaseStorage.getInstance().reference
}

fun initUser(context: Activity) {
    REF_DATABASE_ROOT
        .child(Constants.NODE_USERS)
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