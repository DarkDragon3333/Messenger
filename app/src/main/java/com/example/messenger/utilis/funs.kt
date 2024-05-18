package com.example.messenger.utilis

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.widget.Toast
import com.example.messenger.user_sing_in_and_up.SingUpActivity
import com.google.firebase.auth.PhoneAuthProvider
/*
Служебные функции приложения
*/
fun makeToast(msg: String, context: Context) {
    Toast
        .makeText(context, msg, Toast.LENGTH_SHORT)
        .show()
}

//Переопределённый метод перехода от одного активити к другому
fun <templateActivity : Activity> goTo(
    activity: Class<templateActivity>,
    context: Context
){
    val intent = Intent(context, activity)
    context.startActivity(intent)
}

fun <templateActivity> goTo(
    activity: Class<templateActivity>,
    context: SingUpActivity,
    dataOne: String,
    token: PhoneAuthProvider.ForceResendingToken)
{
    val intent = Intent(context, activity)
    intent.putExtra("verificationId", dataOne)
    intent.putExtra("token", token)
    context.startActivity(intent)
}

fun <templateActivity> goTo(
    activity: Class<templateActivity>,
    context: SingUpActivity,
    dataOne: String,
    dataTwo: String,
    dataThree: String
) {
    val intent = Intent(context, activity)
    intent.putExtra("verificationId", dataOne) //Id пользователя
    intent.putExtra("phone", dataTwo) //Номер телефона
    intent.putExtra("password", dataThree) //Пароль
    context.startActivity(intent)
}
