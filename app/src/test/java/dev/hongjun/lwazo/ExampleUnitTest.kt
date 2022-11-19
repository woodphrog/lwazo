package dev.hongjun.lwazo

import org.junit.Test

import org.junit.Assert.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun testSmsEntry() {
        val sms1= SmsEntry(
            sender = "Alice",
            receiver = "Bob",
            message = "Salut Bob !"
        )
        val sms2 = SmsEntry(
            sender = "Bob",
            receiver = "Alice",
            message = "Salut Alice !",
            quoted = sms1
        )
        val sms3 = SmsEntry(
            sender = "Alice",
            receiver = "Bob",
            message = "Ça va bien ?",
            quoted = sms2
        )
        val sms4 = SmsEntry(
            sender = "Bob",
            receiver = "Alice",
            message = "Ça va bien, merci !",
            quoted = sms3
        )
        println(sms4.toTransmissionFormat())
    }

    @Test
    fun snowflakeTest() {
        for (i in 0..9) {
            println(generateSnowflakeId())
        }
    }
}