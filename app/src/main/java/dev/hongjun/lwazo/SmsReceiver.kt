package dev.hongjun.lwazo

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.telephony.SmsMessage

object SmsReceptionManager {
    private val smsReceptionListeners = mutableListOf<(SmsEntry) -> Unit>()

    fun addSmsReceptionListener(listener: (SmsEntry) -> Unit) {
        smsReceptionListeners.add(listener)
    }

    fun removeSmsReceptionListener(listener: (SmsEntry) -> Unit) {
        smsReceptionListeners.remove(listener)
    }

    fun onReceive(transmissionFormat: String, sender: String) {
        val sms = SmsManager.receiveSmsWithTransmissionFormat(transmissionFormat, sender)
        smsReceptionListeners.forEach { it(sms) }
    }
}

class SmsReceiver : BroadcastReceiver() {
    private val SMS_RECEIVED = "android.provider.Telephony.SMS_RECEIVED"

    override fun onReceive(context: Context?, intent: Intent) {
        if (intent.action == SMS_RECEIVED) {
            val bundle = intent.extras
            if (bundle != null) {
                // get sms objects
                val pdus = bundle["pdus"] as Array<*>?
                if (pdus!!.isEmpty()) {
                    return
                }
                // large message might be broken into many
                val messages: Array<SmsMessage?> = arrayOfNulls(pdus.size)
                val sb = StringBuilder()
                for (i in pdus.indices) {
                    messages[i] = SmsMessage.createFromPdu(pdus[i] as ByteArray)
                    sb.append(messages[i]!!.messageBody)
                }
                val sender: String = messages[0]!!.originatingAddress!!
                val message = sb.toString()
                //Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                // prevent any other broadcast receivers from receiving broadcast
                // abortBroadcast();
                //val smsEntry = SmsEntry(sender, null, message)
                SmsReceptionManager.onReceive(message, sender)
            }
        }
    }
}