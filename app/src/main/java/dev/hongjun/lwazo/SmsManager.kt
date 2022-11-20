package dev.hongjun.lwazo

import android.telephony.SmsManager
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList
import java.util.UUID

class SmsConversation(val with: String) {
    private val smsEntries = mutableMapOf<UUID, SmsEntry>()
    private val smsEntriesSorted = sortedSetOf(
        compareBy<SmsEntry> { it.timestamp }.thenBy { it.id }
    )
    private var mutableStateList: SnapshotStateList<SmsEntry>? = null

    fun addSmsEntry(smsEntry: SmsEntry) {
        smsEntries[smsEntry.id] = smsEntry
        smsEntriesSorted.add(smsEntry)
        mutableStateList?.add(smsEntry)
    }

    fun lookupSmsEntry(id: UUID): SmsEntry? {
        return smsEntries[id]
    }

    fun registerMutableStateList(list: SnapshotStateList<SmsEntry>): SnapshotStateList<SmsEntry> {
        mutableStateList = list
        return list
    }

    fun getSmsEntries(): List<SmsEntry> {
        return smsEntriesSorted.toList()
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
        val sms = parseSms(transmissionFormat, sender)
        val conversation = smsConversations.getOrPut(sms.sender!!) { SmsConversation(sms.sender) }
        conversation.addSmsEntry(sms)
        return sms
    }

    fun replyToSms(smsEntry: SmsEntry, message: String) {
        val conversation = smsConversations.getOrPut(smsEntry.sender!!) { SmsConversation(smsEntry.sender) }
        val reply = SmsEntry(
            sender = PhoneNumberManager.myPhoneNumber,
            receiver = smsEntry.sender,
            message = message,
            quoted = smsEntry
        )
        conversation.addSmsEntry(reply)
        sendSms(reply)
    }

    fun parseSms(transmissionFormat: String, sender: String): SmsEntry {
        val segments = transmissionFormat.split(SEPARATOR)
        val messageBody = segments[0].trim()
        if (segments.size == 1) {
            throw IllegalArgumentException("Invalid transmission format: $transmissionFormat")
        }
        val id = UUID.fromString(segments[1].trim())
        val quoted = if (segments.size > 2) {
            val quotedSegment = segments[2]
            val quotedSegments = quotedSegment.split("a envoyé")
            val quotedId = UUID.fromString(quotedSegments[1].trim())
            val conversation = smsConversations.getOrPut(sender) { SmsConversation(sender) }
            conversation.lookupSmsEntry(quotedId)
        } else {
            null
        }
        return SmsEntry(sender, PhoneNumberManager.myPhoneNumber, messageBody, id = id, quoted = quoted)
    }

    fun getOrCreateConversation(with: String): SmsConversation {
        return smsConversations.getOrPut(with) { SmsConversation(with) }
    }
}