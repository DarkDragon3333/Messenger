package com.example.messenger.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.messenger.component.ElementOfChatsList
import kotlinx.coroutines.launch

@Composable
fun ModalNavigationDrawer() {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val items = listOf("Friends", "About")
    val selectedItem = remember { mutableStateOf(items[0]) }
    androidx.compose.material3.ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Column(
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(Icons.Filled.Person, "Я")
                    TextButton(
                        onClick = {
                            scope.launch { drawerState.close() }
                            selectedItem.value = "Your profile"
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(4.dp),
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Start
                        ) {
                            Text(
                                "Your profile",
                                fontSize = 22.sp,
                            )
                        }
                    }
                }
                Divider()
                items.forEach { item ->
                    TextButton(
                        onClick = {
                            scope.launch {
                                drawerState.close()

                            }
                            selectedItem.value = item
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(4.dp),
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Start
                        ) {
                            Text(
                                item,
                                fontSize = 22.sp,
                            )
                        }
                    }
                }
            }
        },
        scrimColor = Color.DarkGray,
        content = {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = {
                        scope.launch { drawerState.open() }
                    }) {
                        Icon(Icons.Filled.Menu, "Меню")
                    }
                    Text(text = "Курсовая 'Мессенджер'", fontSize = 18.sp)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        Button(onClick = { /*TODO*/ }) {
                            Icon(Icons.Default.Search, contentDescription = "Menu")
                        }
                    }
                }
                Divider()
                LazyColumn {
                    item { ElementOfChatsList() }
                    item { ElementOfChatsList() }
                }
            }
        }
    )
}