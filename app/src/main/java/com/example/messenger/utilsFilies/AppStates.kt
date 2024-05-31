package com.example.messenger.utilsFilies

import android.content.Context

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