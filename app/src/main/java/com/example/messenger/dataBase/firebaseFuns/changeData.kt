package com.example.messenger.dataBase.firebaseFuns

import android.content.Context
import androidx.navigation.NavHostController
import com.example.messenger.modals.setLocalDataForUser
import com.example.messenger.navigation.Screens
import com.example.messenger.utilsFilies.Constants
import com.example.messenger.utilsFilies.goTo
import com.example.messenger.utilsFilies.makeToast

fun choseChangeInformation(
    changeInfo: String,
    typeInfo: String,
    context: Context,
    navController: NavHostController
) {
    when (typeInfo) {
        Constants.CHILD_FULLNAME -> changeInfo(changeInfo, typeInfo, context, navController)

        Constants.CHILD_USER_NAME -> checkUsername(changeInfo, context, navController)

        Constants.CHILD_BIO -> changeInfo(changeInfo, typeInfo, context, navController)

        Constants.CHILD_PHONE -> changeInfo(changeInfo, typeInfo, context, navController)

        Constants.CHILD_PASSWORD -> changeInfo(changeInfo, typeInfo, context, navController)

        Constants.CHILD_PHOTO_URL -> downloadImage(context, navController)
    }
}

fun changeInfo(
    changeInfo: String,
    typeInfo: String,
    context: Context,
    navController: NavHostController
) {
    REF_DATABASE_ROOT
        .child(Constants.NODE_USERS)
        .child(UID)
        .child(typeInfo)
        .setValue(changeInfo).addOnCompleteListener {
            when (it.isSuccessful) {
                true -> {
                    setLocalDataForUser(changeInfo, typeInfo)
                    goTo(navController, Screens.Settings)
                }

                false -> makeToast("Ошибка" + it.exception?.message.toString(), context)
            }

        }
}