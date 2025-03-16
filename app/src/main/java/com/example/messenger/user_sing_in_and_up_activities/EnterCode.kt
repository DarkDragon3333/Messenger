package com.example.messenger.user_sing_in_and_up_activities

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.messenger.MainActivity
import com.example.messenger.R
import com.example.messenger.ui.theme.MessengerTheme
import com.example.messenger.dataBase.AUTH
import com.example.messenger.dataBase.UID
import com.example.messenger.dataBase.USER
import com.example.messenger.utilsFilies.goTo
import com.example.messenger.utilsFilies.makeToast
import com.example.messenger.utilsFilies.sign_in
import com.google.firebase.auth.PhoneAuthProvider


class EnterCode : ComponentActivity() {
    private lateinit var verificationId: String
    private lateinit var dataFromSignUpData: Bundle
    private lateinit var token: PhoneAuthProvider.ForceResendingToken
    private lateinit var phoneNumber: String
    private lateinit var codeFromField: String
    private lateinit var context: EnterCode
    private lateinit var passwordFromSignUpActivity: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        init()
        setContent {
            MessengerTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) {
                    GreetingEnterCode(mod = Modifier.padding(it))
                }
            }
        }
    }

    @Composable
    fun GreetingEnterCode(mod: Modifier = Modifier) {
        var code by remember { mutableStateOf("") }
        val context = LocalContext.current
        val maxCount = 6
        val testSTR = "111222"

        Column(modifier = Modifier.fillMaxSize()) {
            Spacer(modifier = Modifier.padding(100.dp))
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    bitmap = ImageBitmap.imageResource(R.drawable.sms100),
                    contentDescription = "SMS_image"
                )
                Spacer(modifier = Modifier.padding(20.dp))
                Text(
                    text = "Мы отправили вам СМС с кодом на ваш номер телефона",
                    textAlign = TextAlign.Center,
                    fontSize = 12.sp
                )
                Spacer(modifier = Modifier.padding(40.dp))

                TextField(
                    value = code,
                    onValueChange =
                    {
                        if (it.length < maxCount)
                            code = it
                        else if (it.length == maxCount) {
                            code = it
                            if (code == testSTR) {
                                codeFromField = code
                                enterCode()
                            } else {
                                makeToast("Проверьте введёый код!", context)
                            }
                        }
                    },
                    supportingText = {
                        Text(
                            text = "${code.length} / $maxCount",
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.End,
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("------", textAlign = TextAlign.Center) },
                    maxLines = 1,
                    label = { Text(text = "СМС код", fontSize = 12.sp) },
                    colors = TextFieldDefaults.colors(
                        unfocusedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                        unfocusedTextColor = MaterialTheme.colorScheme.tertiary,

                        focusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                        focusedTextColor = MaterialTheme.colorScheme.onTertiary,

                        disabledContainerColor = MaterialTheme.colorScheme.onSecondaryContainer,
                        disabledTextColor = MaterialTheme.colorScheme.tertiaryContainer,
                        disabledLabelColor = MaterialTheme.colorScheme.outline,
                        disabledIndicatorColor = MaterialTheme.colorScheme.outlineVariant,
                    )
                )
            }
        }

    }

    private fun init() {
        dataFromSignUpData = intent.extras!!
        verificationId =
            dataFromSignUpData.getString("verificationId").toString() //Id пользователя
        passwordFromSignUpActivity =
            dataFromSignUpData.getString("password").toString() //Пароль
        phoneNumber =
            dataFromSignUpData.getString("phone").toString() //Номер телефона
        //token = intentForGetSignUpData.getParcelableExtra("token")!!
        codeFromField = ""
        context = this
    }

    private fun enterCode() {
        val credential = PhoneAuthProvider.getCredential(verificationId, codeFromField)

        AUTH.signInWithCredential(credential).addOnCompleteListener {
            if (it.isSuccessful) {
                if (sign_in) {
                    sign_in = false
                    USER.id = verificationId
                    UID = AUTH.currentUser?.uid.toString()
                    goTo(MainActivity::class.java, context)
                } else {
                    goTo(
                        AddInfo::class.java,
                        context,
                        verificationId,
                        phoneNumber,
                        passwordFromSignUpActivity
                    )
                }

            } else {
                makeToast("Error!", context)
            }
        }
    }
}


