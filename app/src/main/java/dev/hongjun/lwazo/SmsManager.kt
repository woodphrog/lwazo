package dev.hongjun.lwazo

import android.telephony.SmsManager
import androidx.compose.runtime.snapshots.SnapshotStateList
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

    fun linkMessages() {
        smsEntries.clear()
        mutableStateList?.clear()
        val newSortedList = sortedSetOf(
            compareBy<SmsEntry> { it.timestamp }.thenBy { it.id }
        )
        smsEntriesSorted.forEach {
            val segments = it.message.split(SEPARATOR)
            val sms = when (segments.size) {
                1 -> {
                    it
                }
                2 -> {
                    try {
                        it.copy(message = segments[0], id = UUID.fromString(segments[1].trim()))
                    } catch (e: IllegalArgumentException) {
                        it
                    }
                }
                else -> {
                    val quotedSegments = segments[2].split("a envoyé")
                    try {
                        it.copy(message = segments[0], id = UUID.fromString(segments[1].trim()), quoted = lookupSmsEntry(UUID.fromString(quotedSegments[1].trim())))
                    } catch (e: Exception) {
                        it
                    }
                }
            }
            smsEntries[sms.id] = sms
            mutableStateList?.add(sms)
            newSortedList.add(sms)
        }
        smsEntriesSorted.clear()
        smsEntriesSorted.addAll(newSortedList)
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
            val quotedSms = conversation.lookupSmsEntry(quotedId)
            quotedSms ?: throw IllegalArgumentException("Invalid transmission format: $transmissionFormat")
        } else {
            null
        }
        return SmsEntry(sender, PhoneNumberManager.myPhoneNumber, messageBody, id = id, quoted = quoted)
    }

    fun getOrCreateConversation(with: String): SmsConversation {
        return smsConversations.getOrPut(with) { SmsConversation(with) }
    }

    fun linkMessages() {
        smsConversations.forEach {
            it.value.linkMessages()
        }
    }
}