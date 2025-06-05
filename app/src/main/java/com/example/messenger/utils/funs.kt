package com.example.messenger.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.ContactsContract
import android.provider.OpenableColumns
import android.widget.Toast
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.DrawerState
import androidx.navigation.NavHostController
import com.example.messenger.dataBase.firebaseFuns.updateContactsForFirebase
import com.example.messenger.modals.ChatModal
import com.example.messenger.modals.CommonModal
import com.example.messenger.modals.ContactModal
import com.example.messenger.modals.MessageModal
import com.example.messenger.navigation.Screens
import com.example.messenger.screens.loginAndSignUp.AddInfo
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

    //Используем navController для перемещения по экранам
    navController.navigate("chatScreen/${fullname}/${status}/${uri}/{${user.id}}") {
        launchSingleTop = true
    }
}

fun goTo(navController: NavHostController, contact: ContactModal) {
    val fullname = URLEncoder.encode(contact.fullname, "UTF-8")
    val status = URLEncoder.encode(contact.status, "UTF-8")
    val uri = URLEncoder.encode(contact.photoUrl, "UTF-8")

    //Используем navController для перемещения по экранам
    navController.navigate("chatScreen/${fullname}/${status}/${uri}/{${contact.id}}") {
        launchSingleTop = true
    }
}

fun goTo(navController: NavHostController, user: ChatModal) {
    val fullName = URLEncoder.encode(user.fullname, "UTF-8")
    val status = URLEncoder.encode(user.status, "UTF-8")
    val uri = URLEncoder.encode(user.photoUrl, "UTF-8")

    //Используем navController для перемещения по экранам
    navController.navigate("chatScreen/${fullName}/${status}/${uri}/{${user.id}}") {
        launchSingleTop = true
    }
}

fun goTo(
    navController: NavHostController,
    screen: Screens,
    contactList: MutableList<ContactModal>
) {
    navController.currentBackStackEntry?.savedStateHandle?.apply {
        set("contactList", contactList)
    }
    navController.navigate(screen.route) {
        launchSingleTop = true
    }
}

fun goTo(
    navController: NavHostController,
    screen: Screens,
    contactList: MutableList<ContactModal>?,
    name: String,
    photoUri: String
) {
    navController.currentBackStackEntry?.savedStateHandle?.apply {
        set("contactList", contactList)
        set("groupChatName", name)
        set("photoUrlGroupChat", photoUri)
    }

    navController.navigate(screen.route) {
        launchSingleTop = true
    }
}

//fun DataSnapshot.getCommonModel(): CommonModal =
//    this.getValue(CommonModal::class.java) ?: CommonModal()

fun DataSnapshot.getMessageModel(): MessageModal =
    this.getValue(MessageModal::class.java) ?: MessageModal()

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

fun parseInfo(string: String): Pair<String, String> {
    val parts = string.split("__", limit = 2)
    val info = parts.getOrNull(0) ?: ""
    val fileName = parts.getOrNull(1) ?: ""
    return fileName to info
}

fun whenSelect(bool: Boolean, funTrue: Unit, funFalse: Unit) {
    when (bool) {
        true -> funTrue
        false -> funFalse
    }
}

fun whenSelect(bool: Boolean, funTrue: () -> Unit, funFalse: () -> Unit) {
    when (bool) {
        true -> funTrue
        false -> funFalse
    }
}