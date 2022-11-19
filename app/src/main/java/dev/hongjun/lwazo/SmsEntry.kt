package dev.hongjun.lwazo

import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.telephony.SmsManager
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale
import java.util.UUID

const val SEPARATOR = "----------------------------------"

val dateFormat: DateTimeFormatter = DateTimeFormatter.ofPattern("dd MMMM yyyy à HH:mm:ss.SSSSS", Locale.FRENCH)

data class SmsEntry(
    val sender: String?,
    val receiver: String?,
    val message: String,
    val timestamp: LocalDateTime = LocalDateTime.now(),
    val quoted: SmsEntry? = null,
    val id: UUID = UUID.randomUUID()
) {
    private fun toQuote(): String {
        return """
$SEPARATOR
Le ${dateFormat.format(timestamp)}, $sender a envoyé $id
        """.trimIndent().trimMargin()
    }

    fun toTransmissionFormat(): String {
        return if (quoted == null) {
            """
$message
$SEPARATOR
$id
            """.trimIndent().trimMargin()
        } else {
            """
$message
$SEPARATOR
$id
${quoted.toQuote()}
            """.trimIndent().trimMargin()
        }
    }
}

fun readSms() {
    val smsBuilder = StringBuilder()
    val SMS_URI_INBOX = "content://sms/inbox";
    val SMS_URI_ALL = "content://sms/";
    try {
        val uri = Uri.parse(SMS_URI_INBOX)
        val projection = arrayOf("_id", "address", "person", "body", "date", "type")
        //val cur: Cursor = context!!.getContentResolver().query(uri, projection, "address='123456789'", null, "date desc")

    } catch (e: Exception) {
        e.printStackTrace()
    }
}
