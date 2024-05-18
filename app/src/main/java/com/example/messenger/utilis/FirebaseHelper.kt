package com.example.messenger.utilis

import com.google.firebase.auth.FirebaseAuth
import kotlin.properties.Delegates

lateinit var AUTH: FirebaseAuth
var DATA_FROM_SING_UP_ACTIVITY by Delegates.notNull<Int>()