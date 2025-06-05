package com.example.messenger.modals

import androidx.compose.runtime.Stable
import java.io.Serializable

@Stable
data class ContactModal (
    var id: String = "",
    var fullname: String = "",
    var phone: String = "",
    var photoUrl: String = "",
    var status: String = "",
    var username: String = "",
    var bio: String = "",
    var password: String = ""
) : Serializable