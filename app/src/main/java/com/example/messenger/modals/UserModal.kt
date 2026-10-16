package com.example.messenger.modals

import androidx.compose.runtime.Stable
import com.example.messenger.dataBase.firebaseFuns.USER
import com.example.messenger.utils.Constants.CHILD_BIO
import com.example.messenger.utils.Constants.CHILD_CHAT_NAME
import com.example.messenger.utils.Constants.CHILD_PASSWORD
import com.example.messenger.utils.Constants.CHILD_PHONE
import com.example.messenger.utils.Constants.CHILD_PHOTO_URL
import com.example.messenger.utils.Constants.CHILD_STATUS
import com.example.messenger.utils.Constants.CHILD_USER_NAME

@Stable
data class User(
    var id: String = "",
    var username: String = "",
    var bio: String = "",
    var fullname: String = "",
    var status: String = "",
    var photoUrl: String = "",
    var phone: String = "",
    var password: String = ""
)

fun setLocalDataForUser(newInfo: String, typeInfo: String) {
    when (typeInfo) {
        CHILD_CHAT_NAME -> USER.fullname = newInfo

        CHILD_USER_NAME -> USER.username = newInfo

        CHILD_BIO -> USER.bio = newInfo

        CHILD_PHONE -> USER.phone = newInfo

        CHILD_PASSWORD -> USER.password = newInfo

        CHILD_STATUS -> USER.status = newInfo

        CHILD_PHOTO_URL -> USER.photoUrl = newInfo
    }
}