package com.example.messenger.utilsFilies

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.material3.DrawerState
import androidx.navigation.NavHostController
import com.example.messenger.modals.CommonModal
import com.example.messenger.navigation.Screens
import com.example.messenger.user_sing_in_and_up_activities.AddInfo
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.database.DataSnapshot
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.net.URLEncoder

/*
Служебные функции приложения
*/
fun makeToast(msg: String, context: Context) {
    Toast
        .makeText(context, msg, Toast.LENGTH_LONG)
        .show()
}

//Переопределённый метод перехода от одного активити к другому
fun <templateActivity : Activity> goTo(
    activity: Class<templateActivity>,
    context: Context
) {
    val intent = Intent(context, activity)
    context.startActivity(intent)
}

fun <templateActivity> goTo(
    activity: Class<templateActivity>,
    context: Activity,
    dataOne: String,
    token: PhoneAuthProvider.ForceResendingToken
) {
    val intent = Intent(context, activity)
    intent.putExtra("verificationId", dataOne)
    intent.putExtra("token", token)
    context.startActivity(intent)
}

fun <templateActivity> goTo(
    activity: Class<templateActivity>,
    context: Activity,
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

fun <templateActivity> goTo(
    activity: Class<templateActivity>,
    context: AddInfo,
    verificationId: String,
    fullname: String,
    userName: String,
    phone: String,
    password: String
) {
    val intent = Intent(context, activity)
    intent.putExtra("verificationId", verificationId) //Id пользователя
    intent.putExtra("phone", phone) //Номер телефона
    intent.putExtra("password", password) //Пароль
    intent.putExtra("fullname", fullname) //Id пользователя
    intent.putExtra("userName", userName) //Номер телефона
    context.startActivity(intent)
}

fun goTo(
    navController: NavHostController,
    screen: Screens,
    coroutineScope: CoroutineScope,
    drawerState: DrawerState
) {
    navController.navigate(screen.route) {//Используем navController для перемещения по экранам
        launchSingleTop = true
    }
    coroutineScope.launch {
        drawerState.close()
    }
}

fun goTo(navController: NavHostController, screen: Screens) {
    navController.navigate(screen.route) {//Используем navController для перемещения по экранам
        launchSingleTop = true
    }
}

fun goTo(navController: NavHostController, user: CommonModal) {
    val fullname = URLEncoder.encode(user.fullname, "UTF-8")
    val status = URLEncoder.encode(user.status, "UTF-8")
    val uri = URLEncoder.encode(user.photoUrl, "UTF-8")
    val id = URLEncoder.encode(user.id, "UTF-8")

    //Используем navController для перемещения по экранам
    navController.navigate("chatScreen/${fullname}/${status}/${uri}/{$id}") {
        launchSingleTop = true
    }
}

fun DataSnapshot.getCommonModel(): CommonModal =
    this.getValue(CommonModal::class.java) ?: CommonModal()