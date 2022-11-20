package dev.hongjun.lwazo

import android.content.ContentResolver
import android.content.Context
import android.provider.Telephony
import java.lang.Long
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.Date


fun loadTextMessages(context: Context) {
    val cr: ContentResolver = context.contentResolver
    val c = cr.query(Telephony.Sms.CONTENT_URI, null, null, null, null)
    if (c != null) {
        if (c.moveToFirst()) {
            for (i in 0 until c.count) {
                val smsDate = c.getString(c.getColumnIndexOrThrow(Telephony.Sms.DATE))
                val number = c.getString(c.getColumnIndexOrThrow(Telephony.Sms.ADDRESS))
                val body = c.getString(c.getColumnIndexOrThrow(Telephony.Sms.BODY))
                val instant = Date(Long.valueOf(smsDate)).toInstant()
                var type: String = ""
                when (c.getString(c.getColumnIndexOrThrow(Telephony.Sms.TYPE)).toInt()) {
                    Telephony.Sms.MESSAGE_TYPE_INBOX -> type = "inbox"
                    Telephony.Sms.MESSAGE_TYPE_SENT -> type = "sent"
                    Telephony.Sms.MESSAGE_TYPE_OUTBOX -> type = "outbox"
                    else -> {}
                }
                val localDateTime =
                    LocalDateTime.ofInstant(instant, java.time.ZoneId.systemDefault())
                val conversation = SmsManager.getOrCreateConversation(number)
                when (type) {
                    "inbox" -> {
                        try {
                            val sms = SmsManager.parseSms(body, number)
                            conversation.addSmsEntry(sms)
                        } catch (e: Exception) {
                            val sms = SmsEntry(
                                number,
                                PhoneNumberManager.myPhoneNumber,
                                body,
                                localDateTime
                            )
                            conversation.addSmsEntry(sms)
                        }
                    }

                    "sent", "outbox" -> {
                        try {
                            var sms = SmsManager.parseSms(body, PhoneNumberManager.myPhoneNumber)
                            sms = sms.copy(receiver = number, timestamp = localDateTime)
                            conversation.addSmsEntry(sms)
                        } catch (e: Exception) {
                            val sms = SmsEntry(
                                PhoneNumberManager.myPhoneNumber,
                                number,
                                body,
                                localDateTime
                            )
                            conversation.addSmsEntry(sms)
                        }
                    }
                }
                c.moveToNext()
            }
        }
        c.close()
    }
    c?.close()
}