package dev.hongjun.lwazo

import android.telephony.SmsManager
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale


val dateFormat: DateTimeFormatter = DateTimeFormatter.ofPattern("dd MMMM yyyy à HH:mm:ss.SSSSS", Locale.FRENCH)

data class SmsEntry(
    val sender: String?,
    val receiver: String?,
    val message: String,
    val timestamp: LocalDateTime = LocalDateTime.now(),
    val quoted: SmsEntry? = null
) {
    fun toQuote(): String {
        SimpleDateFormat()
        return """
----------------------------------
Le ${dateFormat.format(timestamp)}, $sender a écrit :
${toFullText()}
        """.trimIndent().trimMargin()
    }

    fun toFullText(): String {
        return if (quoted == null) {
            message
        } else {
            message + "\n" + quoted.toQuote()
        }
    }
}

fun sendSms(smsEntry: SmsEntry) {
    val smgr: SmsManager = SmsManager.getDefault()
    val parts = smgr.divideMessage(smsEntry.toFullText())
    smgr.sendMultipartTextMessage(smsEntry.receiver, null, parts, null, null)
}
