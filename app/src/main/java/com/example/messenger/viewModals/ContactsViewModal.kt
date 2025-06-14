package com.example.messenger.viewModals

import androidx.lifecycle.ViewModel
import com.example.messenger.dataBase.firebaseFuns.REF_DATABASE_ROOT
import com.example.messenger.modals.ContactModal
import com.example.messenger.utils.Constants.NODE_PHONES
import com.example.messenger.utils.Constants.NODE_USERS

class ContactsViewModal : ViewModel() {
    private var listContacts: MutableList<ContactModal> = mutableListOf()

    fun setContactsList(contactsList: MutableList<ContactModal>) {
        listContacts.addAll(contactsList)
    }

    fun getListContacts(): MutableList<ContactModal> {
        return listContacts
    }

    fun downloadContactsInfo(contactList: MutableList<ContactModal>) {
        contactList.forEach { contact ->
            REF_DATABASE_ROOT.child(NODE_PHONES).child(contact.phone).get()
                .addOnSuccessListener { snapshot ->
                    val contactId = snapshot.getValue(String::class.java)

                    if (contactId != null) {
                        REF_DATABASE_ROOT.child(NODE_USERS).child(contactId).get()
                            .addOnSuccessListener { userSnapshot ->
                                val contactModal = userSnapshot.getValue(ContactModal::class.java)
                                if (contactModal != null) {
                                    listContacts.add(contactModal)
                                }
                            }
                    }
                }
        }

    }
}