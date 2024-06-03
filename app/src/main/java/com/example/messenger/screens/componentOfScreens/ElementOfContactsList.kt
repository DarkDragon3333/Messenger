package com.example.messenger.screens.componentOfScreens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.messenger.modals.CommonModal
import com.example.messenger.utilsFilies.MainImage
import com.example.messenger.utilsFilies.NODE_USERS
import com.example.messenger.utilsFilies.REF_DATABASE_ROOT
import com.example.messenger.utilsFilies.changeInfoOfContactFlag
import com.example.messenger.utilsFilies.commonModalContactList
import com.example.messenger.utilsFilies.initContactsFlag
import com.example.messenger.utilsFilies.mainActivityContext
import com.example.messenger.utilsFilies.makeToast
import com.example.messenger.utilsFilies.sizeContactsList
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

@Composable
fun ContactCard(user: CommonModal) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Spacer(modifier = Modifier.padding(8.dp))
        MainImage(dp = 64.dp, user.photoUrl) {}

        Spacer(modifier = Modifier.padding(4.dp))
        Column {
            Text(text = user.fullname)
            Text(text = user.phone)
        }
        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center){
            Text(text = user.status)
        }
    }
}

fun initContactCard(id: String, index: Int) {
    var user: CommonModal
    REF_DATABASE_ROOT
        .child(NODE_USERS)
        .child(id).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                user = snapshot.getValue(CommonModal::class.java)
                    ?: CommonModal() //Получаем данные через переменную snapshot. Если будет null поле, то вы инициализируем пустым пользователем

                checkCommonModalContactsList(user)
            }
            //Функция отвечает за правильное заполнение списка контактов
            private fun checkCommonModalContactsList(user: CommonModal) {
                if (commonModalContactList.isNotEmpty()) { //Если список контактов не пустой
                    if (initContactsFlag) { //Если происходи запуск приложения
                        if (commonModalContactList.size == sizeContactsList - 1) {
                            commonModalContactList.add(user)
                            initContactsFlag = false
                        } else {
                            commonModalContactList.add(user)
                        }
                    } else if (changeInfoOfContactFlag) { //Если происходит изменение контакта
                        if (commonModalContactList[index].id != user.id) {
                            commonModalContactList.add(user)
                            changeInfoOfContactFlag = false
                        } else {
                            commonModalContactList[index] = user
                            changeInfoOfContactFlag = false
                        }
                    }
                } else {
                    commonModalContactList.add(user)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                makeToast("Ошибка", mainActivityContext)
            }

        })
}