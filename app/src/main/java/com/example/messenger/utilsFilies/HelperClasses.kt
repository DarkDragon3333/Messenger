package com.example.messenger.utilsFilies

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

class AppValueEventListener (val function: (DataSnapshot) -> Unit) : ValueEventListener {
    override fun onDataChange(dataSnapshot: DataSnapshot) {
        function(dataSnapshot)
    }
    override fun onCancelled(error: DatabaseError) {

    }

}