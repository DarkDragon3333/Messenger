package com.example.messenger.utilsFilies

import com.google.firebase.Timestamp
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

fun Any?.toFormattedLocalTime(): String {
    val formatter = DateTimeFormatter.ofPattern("HH:mm")

    return when (this) {
        is Timestamp -> convertToUserLocalTime(this)
        is String -> convertToUserLocalTime(this.toTimestamp())
        else -> LocalTime.now().format(formatter) //Firestore не успевает установить время
    }
}

fun convertToUserLocalTime(timestamp: Timestamp): String {
    val instant = timestamp.toDate().toInstant()
    val userZone = ZoneId.systemDefault()
    val zonedDateTime = instant.atZone(userZone)
    val outputFormatter = DateTimeFormatter.ofPattern("HH:mm", Locale.getDefault())
    return zonedDateTime.format(outputFormatter)
}

fun String.toTimestamp(): Timestamp {
    val regex = """Timestamp\(seconds=(\d+), nanoseconds=(\d+)\)""".toRegex()
    val matchResult = regex.find(this)
    val (seconds, _) = matchResult?.destructured ?: throw IllegalArgumentException("Invalid timestamp format")
    return Timestamp(seconds.toLong(), 0) // возвращаем Timestamp с секундой
}