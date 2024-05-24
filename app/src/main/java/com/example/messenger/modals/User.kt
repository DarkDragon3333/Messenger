package com.example.messenger.modals

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