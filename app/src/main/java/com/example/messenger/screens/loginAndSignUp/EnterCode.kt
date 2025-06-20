package com.example.messenger.screens.loginAndSignUp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
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
import com.example.messenger.dataBase.firebaseFuns.AUTH
import com.example.messenger.dataBase.firebaseFuns.UID
import com.example.messenger.dataBase.firebaseFuns.USER
import com.example.messenger.utils.goTo
import com.example.messenger.utils.makeToast
import com.example.messenger.utils.sign_in
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
                    GreetingEnterCode(modifier = Modifier.padding(it))
                }
            }
        }
    }

    @Composable
    fun GreetingEnterCode(modifier: Modifier = Modifier) {
        var code by rememberSaveable { mutableStateOf("") }
        val context = LocalContext.current
        val maxCount = 6
        val testSTR = "111222"

        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp, vertical = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(64.dp))

            Image(
                bitmap = ImageBitmap.imageResource(R.drawable.sms100),
                contentDescription = "SMS_image"
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Мы отправили вам СМС с кодом на ваш номер телефона",
                textAlign = TextAlign.Center,
                fontSize = 14.sp,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(48.dp))

            TextField(
                value = code,
                onValueChange = {
                    if (it.length <= maxCount) {
                        code = it
                        if (it.length == maxCount) {
                            if (code == testSTR) {
                                codeFromField = code
                                enterCode()
                            } else {
                                makeToast("Проверьте введённый код!", context)
                            }
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(78.dp),
                maxLines = 1,
                singleLine = true,
                textStyle = LocalTextStyle.current.copy(
                    fontSize = 18.sp,
                    textAlign = TextAlign.Center
                ),
                placeholder = {
                    Text(
                        text = "- - - - - -",
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center,
                        fontSize = 20.sp
                    )
                },
                label = {
                    Text(text = "СМС код", fontSize = 12.sp)
                },
                supportingText = {
                    Text(
                        text = "${code.length} / $maxCount",
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.End,
                        fontSize = 12.sp
                    )
                },
                colors = TextFieldDefaults.colors(
                    unfocusedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                    unfocusedTextColor = MaterialTheme.colorScheme.tertiary,
                    unfocusedIndicatorColor = MaterialTheme.colorScheme.outline,

                    focusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                    focusedTextColor = MaterialTheme.colorScheme.onTertiary,
                    focusedIndicatorColor = MaterialTheme.colorScheme.primary,

                    disabledContainerColor = MaterialTheme.colorScheme.onSecondaryContainer,
                    disabledTextColor = MaterialTheme.colorScheme.tertiaryContainer,
                    disabledIndicatorColor = MaterialTheme.colorScheme.outlineVariant,
                    disabledLabelColor = MaterialTheme.colorScheme.outline
                )
            )
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

            } else makeToast("Error!", context)
        }
    }
}


