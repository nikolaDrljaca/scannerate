package com.drbrosdev.studytextscan.util

import java.text.SimpleDateFormat
import java.util.*

fun getCurrentDateTime(): Long {
    return Calendar.getInstance().time.time
}

fun dateAsString(
    dateInMillis: Long,
    format: String = "yyyy/MM/dd HH:mm",
    locale: Locale = Locale.getDefault()
): String {
    val date = Date(dateInMillis)
    val formatter = SimpleDateFormat(format, locale)
    return formatter.format(date)
}