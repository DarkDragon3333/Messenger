package com.example.messenger.utils

import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

const val READ_CONTACTS = android.Manifest.permission.READ_CONTACTS
const val PERMISSION_REQUEST = 200
const val RECORD_AUDIO = android.Manifest.permission.RECORD_AUDIO

fun myCheckPermission(permission: String): Boolean {
    if (ContextCompat.checkSelfPermission(
            mainActivityContext,
            permission
        ) != PackageManager.PERMISSION_GRANTED
    ) {
        ActivityCompat.requestPermissions(
            mainActivityContext,
            arrayOf(permission),
            PERMISSION_REQUEST
        )
        return false
    } else
        return true
}
