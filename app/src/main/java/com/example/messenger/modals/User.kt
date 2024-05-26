package com.example.messenger.modals

import com.example.messenger.utilis.CHILD_BIO
import com.example.messenger.utilis.CHILD_FULLNAME
import com.example.messenger.utilis.CHILD_PASSWORD
import com.example.messenger.utilis.CHILD_PHONE
import com.example.messenger.utilis.CHILD_PHOTO_URL
import com.example.messenger.utilis.CHILD_STATUS
import com.example.messenger.utilis.CHILD_USER_NAME
import com.example.messenger.utilis.USER

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

fun setLocalDataForUser(changeInfo: String, typeInfo: String) {
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

        CHILD_STATUS -> {
            USER.status = changeInfo
        }

        CHILD_PHOTO_URL -> {
            USER.photoUrl = changeInfo
        }
    }
}