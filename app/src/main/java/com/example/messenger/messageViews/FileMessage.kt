package com.example.messenger.messageViews

import android.content.Context
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.messenger.R
import com.example.messenger.dataBase.firebaseFuns.REF_STORAGE_ROOT
import com.example.messenger.modals.MessageModal
import com.example.messenger.utils.Constants.FOLDER_MESSAGE_FILE
import com.example.messenger.utils.downloadFileToDownloads
import com.example.messenger.utils.extractFirebasePathFromUrl
import com.example.messenger.utils.mainActivityContext
import com.example.messenger.utils.makeToast
import com.example.messenger.utils.parseInfo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun FileMsg(pair: Pair<MessageModal, Any>) {
    var (fileName, fileUri) = remember { parseInfo(pair.first.info) }
    val context = LocalContext.current

    var isLoading by remember { mutableStateOf(false) }
    var resultMessage by remember { mutableStateOf("") }
    Surface(
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.surfaceVariant,
    ) {
        Box(contentAlignment = Alignment.BottomEnd, modifier = Modifier.padding(8.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(end = 48.dp)
            ) {
                FilledIconButton(
                    onClick = {
                        CoroutineScope(Dispatchers.IO).launch {
                            try {
                                val success = downloadFileToDownloads(
                                    context = context,
                                    firebasePath = extractFirebasePathFromUrl(fileUri).toString() ,
                                    fileName = parseInfo(pair.first.info).first
                                )

                                withContext(Dispatchers.Main) {
                                    resultMessage = if (success) "Файл успешно скачан в Загрузки" else "Ошибка при скачивании"
                                    makeToast("Файл успешно скачен", mainActivityContext)
                                }
                            } catch (e: Exception) {
                                withContext(Dispatchers.Main) {
                                    resultMessage = "Ошибка при скачивании: ${e.localizedMessage}"
                                    makeToast("Ошибка при скачивании", mainActivityContext)
                                }
                            } finally {
                                withContext(Dispatchers.Main) {
                                    isLoading = false
                                }
                            }
                        }
                    },
                    modifier = Modifier.size(48.dp)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.baseline_insert_drive_file_24),
                        contentDescription = null
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = fileName,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            Text(
                text = pair.second.toString(),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = 4.dp, bottom = 2.dp)
            )
        }
    }
}
