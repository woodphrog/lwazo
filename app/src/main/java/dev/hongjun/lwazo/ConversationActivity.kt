package dev.hongjun.lwazo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import dev.hongjun.lwazo.ui.theme.LoiseauBleuTheme

@Composable
fun Conversation(navController: NavController, destinationNumber: String) {
    val conversation = SmsManager.getOrCreateConversation(destinationNumber)
    val messages = conversation.registerMutableStateList(remember {
        conversation.getSmsEntries().toMutableStateList()
    })
    Column(modifier = Modifier.fillMaxSize()) {
        Text(text = "Conversation with $destinationNumber")
        for (message in messages) {
            Row {
                Text(text = message.message)
            }
        }
    }
}
