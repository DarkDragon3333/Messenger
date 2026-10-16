package com.example.messenger.viewModals

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.lifecycle.ViewModel
import com.example.messenger.dataBase.firebaseFuns.REF_DATABASE_ROOT
import com.example.messenger.modals.ContactModal
import com.example.messenger.utils.Constants.NODE_PHONES
import com.example.messenger.utils.Constants.NODE_USERS

class ContactsViewModal : ViewModel() {

    private val _contacts = mutableStateListOf<ContactModal>()
    val contacts: List<ContactModal> get() = _contacts

    private val _selectedMap = mutableStateMapOf<ContactModal, Boolean>()
    val selectedMap: Map<ContactModal, Boolean> get() = _selectedMap

    fun setContactsList(contactsList: List<ContactModal>) {
        _contacts.clear()
        _contacts.addAll(contactsList)
        contactsList.forEach {
            _selectedMap[it] = false
        }
    }

    fun toggleSelection(contact: ContactModal, selected: Boolean) {
        _selectedMap[contact] = selected
    }

    fun getSelectedContacts(): List<ContactModal> {
        return _selectedMap.filterValues { it }.keys.toList()
    }

    fun downloadContactsInfo(contactList: List<ContactModal>) {
        contactList.forEach { contact ->
            REF_DATABASE_ROOT.child(NODE_PHONES).child(contact.phone).get()
                .addOnSuccessListener { snapshot ->
                    val contactId = snapshot.getValue(String::class.java)
                    if (contactId != null) {
                        REF_DATABASE_ROOT.child(NODE_USERS).child(contactId).get()
                            .addOnSuccessListener { userSnapshot ->
                                val contactModal = userSnapshot.getValue(ContactModal::class.java)
                                if (contactModal != null) {
                                    _contacts.add(contactModal)
                                    _selectedMap[contactModal] = false
                                }
                            }
                    }
                }
        }
    }
}
