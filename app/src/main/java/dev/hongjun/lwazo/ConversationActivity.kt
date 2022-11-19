package dev.hongjun.lwazo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Scaffold
import androidx.compose.material.TextField
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import dev.hongjun.lwazo.ui.theme.LoiseauBleuTheme

@Composable
fun Conversation(navController: NavController, destinationNumber: String) {
    val conversation = SmsManager.getOrCreateConversation(destinationNumber)
    val messages = conversation.registerMutableStateList(remember {
        conversation.getSmsEntries().toMutableStateList()
    })
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = destinationNumber,
                        style = TextStyle(
                            textAlign = TextAlign.Center,
                            fontSize = 20.sp
                        )
                    )
                },
                backgroundColor = Color.White,
                navigationIcon = if (navController.previousBackStackEntry != null) {
                    {
                        IconButton(onClick = { navController.navigateUp() }) {
                            Icon(
                                imageVector = Icons.Filled.ArrowBack,
                                contentDescription = "Back"
                            )
                        }
                    }
                } else {
                    null
                }
            )
        },
        bottomBar = {
            MessageInput(conversation)
        }
    ) {
        println(it)
        Column(modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState(Int.MAX_VALUE))) {
            for (message in messages) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(8.dp),
                    horizontalArrangement = if (messageIsMine(message)) {
                        Arrangement.End
                    } else {
                        Arrangement.Start
                    }
                ) {
                    ChatBubble(message)
                }
            }
        }
    }
}

fun messageIsMine(message: SmsEntry): Boolean {
    return message.sender == PhoneNumberManager.myPhoneNumber
}

@Composable
fun MessageInput(conversation: SmsConversation) {
    var inputValue by remember { mutableStateOf("") }

    fun sendMessage() {
        val sms = SmsEntry(PhoneNumberManager.myPhoneNumber, conversation.with, inputValue)
        SmsManager.sendSms(sms)
        conversation.addSmsEntry(sms)
        inputValue = ""
    }

    Row {
        TextField(
            modifier = Modifier.weight(1f),
            value = inputValue,
            onValueChange = { inputValue = it },
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
            keyboardActions = KeyboardActions { sendMessage() },
        )
        Button(
            modifier = Modifier.height(56.dp),
            onClick = { sendMessage() },
            enabled = inputValue.isNotBlank(),
        ) {
            Icon(
                imageVector = Icons.Default.Send,
                contentDescription = "Envoyer"
            )
        }
    }
}

@Composable
fun ChatBubble(sms: SmsEntry) {
    Surface(
        color = if (messageIsMine(sms)) {
            MaterialTheme.colorScheme.primary
        } else {
            MaterialTheme.colorScheme.secondary
        },
        contentColor = if (messageIsMine(sms)) {
            MaterialTheme.colorScheme.onPrimary
        } else {
            MaterialTheme.colorScheme.onSecondary
        },
        shape = MaterialTheme.shapes.medium,
        shadowElevation = 2.dp,
    ) {
        Text(
            text = sms.message,
            modifier = Modifier.padding(8.dp),
            style = TextStyle(
                fontSize = 16.sp,
                fontStyle = FontStyle.Normal,
                fontWeight = FontWeight.Normal
            )
        )
    }
}
