package com.example.messenger.utilis

import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

const val READ_CONTACTS = android.Manifest.permission.READ_CONTACTS
const val PERMISSION_REQUEST = 200

fun myCheckPermission(permission: String): Boolean {
    if (
        Build.VERSION.SDK_INT >= 23 &&
        ContextCompat.checkSelfPermission(
            mainActivityContext,
            permission.toString()
        ) != PackageManager.PERMISSION_GRANTED
    ) {
        ActivityCompat.requestPermissions(
            mainActivityContext,
            arrayOf(arrayOf(permission).toString()),
            PERMISSION_REQUEST
        )
        return false
    } else
        return true
}
