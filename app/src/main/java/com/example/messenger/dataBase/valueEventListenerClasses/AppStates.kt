package com.example.messenger.dataBase.valueEventListenerClasses

import android.content.Context
import com.example.messenger.dataBase.firebaseFuns.REF_DATABASE_ROOT
import com.example.messenger.dataBase.firebaseFuns.UID
import com.example.messenger.dataBase.firebaseFuns.USER
import com.example.messenger.utilsFilies.Constants.CHILD_STATUS
import com.example.messenger.utilsFilies.Constants.NODE_USERS
import com.example.messenger.utilsFilies.makeToast

enum class AppStatus(val state: String) {
    ONLINE("В сети"),
    OFFLINE("Был недавно");
    //TYPING("Печатает");

    companion object {
        fun updateStates(appStatus: AppStatus, context: Context) {
            REF_DATABASE_ROOT.child(NODE_USERS).child(UID).child(CHILD_STATUS)
                .setValue(appStatus.state)
                .addOnSuccessListener { USER.status = appStatus.state }
                .addOnFailureListener { e ->
                    makeToast(e.message.toString(), context)
                }
        }
    }
}