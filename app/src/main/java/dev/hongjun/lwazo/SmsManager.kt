package dev.hongjun.lwazo

import android.telephony.SmsManager
import java.util.UUID

class SmsConversation(val with: String) {
    private val smsEntries = mutableMapOf<UUID, SmsEntry>()

    fun addSmsEntry(smsEntry: SmsEntry) {
        smsEntries[smsEntry.id] = smsEntry
    }

    private fun lookupSmsEntry(id: UUID): SmsEntry? {
        return smsEntries[id]
    }
}

object SmsManager {
    private val smsConversations = mutableMapOf<String, SmsConversation>()

    fun sendSms(smsEntry: SmsEntry) {
        val smgr: SmsManager = SmsManager.getDefault()
        val parts = smgr.divideMessage(smsEntry.toTransmissionFormat())
        smgr.sendMultipartTextMessage(smsEntry.receiver, null, parts, null, null)
    }

    fun receiveSmsWithTransmissionFormat(transmissionFormat: String, sender: String): SmsEntry {
        return parseSms(transmissionFormat)
    }

    private fun parseSms(transmissionFormat: String): SmsEntry {
        val segments = transmissionFormat.split(SEPARATOR)
        val messageBody = segments[0].trim()
        val id = UUID.fromString(segments[1].trim())
        return SmsEntry(null, null, transmissionFormat)
    }
}