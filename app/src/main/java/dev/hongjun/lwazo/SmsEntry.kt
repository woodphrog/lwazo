package dev.hongjun.lwazo

import java.time.Instant

data class SmsEntry(
    val sender: String,
    val receiver: String?,
    val message: String,
    val timestamp: Instant,
) {
    fun toQuote(): String {
        return """
            ----------------------------------------
            Le ${timestamp}, $sender a écrit :
            $message
        """.trimIndent().trimMargin()
    }
}
