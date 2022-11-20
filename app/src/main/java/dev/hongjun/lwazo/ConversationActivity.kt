package dev.hongjun.lwazo

import android.app.Activity
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Scaffold
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
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
import androidx.compose.ui.platform.LocalContext
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
import dev.hongjun.lwazo.ui.theme.MessageTextColor
import dev.hongjun.lwazo.ui.theme.OtherMessageColor
import dev.hongjun.lwazo.ui.theme.Purple80
import dev.hongjun.lwazo.ui.theme.QuotedMessageColor
import dev.hongjun.lwazo.ui.theme.QuotedTextColor
import dev.hongjun.lwazo.ui.theme.SelfMessageColor

@Composable
fun Conversation(navController: NavController, destinationNumber: String) {
    val activity = LocalContext.current as Activity
    activity.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
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
                                contentDescription = "Revenir à la liste des conversations"
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
            .verticalScroll(rememberScrollState(Int.MAX_VALUE))
            .padding(top = 15.dp, bottom = 70.dp)
            //.padding(innerPadding)
            //.navigationBarsPadding()
        ) {
            var previousMsg: SmsEntry? = null
            for (message in messages) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(
                            bottom =
                            if (previousMsg != null && previousMsg.sender == message.sender) {
                                1.dp
                            } else {
                                8.dp
                            }, start = 15.dp, end = 15.dp
                        ),
                    horizontalArrangement = if (messageIsMine(message)) {
                        Arrangement.End
                    } else {
                        Arrangement.Start
                    }
                ) {
                    ChatBubble(message)
                }
                previousMsg = message
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

    val translucent = Color(0xF2FFFFFF)
    Column {
        Box(
            Modifier.background(translucent)
                .fillMaxWidth().height(10.dp)
        )
        Row(
            Modifier
                .padding(start = 15.dp, end = 15.dp, bottom = 0.dp)
                .background(translucent),
        ) {
            TextField(
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 5.dp)
                    .background(Color.White),
                value = inputValue,
                onValueChange = { inputValue = it },
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                keyboardActions = KeyboardActions { sendMessage() },
                textStyle = TextStyle(
                    fontSize = 16.sp,
                    fontStyle = FontStyle.Normal,
                    fontWeight = FontWeight.Normal
                ),
                shape = MaterialTheme.shapes.extraLarge,
                placeholder = {
                    Text(
                        text = "Message texte",
                        style = TextStyle(
                            fontStyle = FontStyle.Italic,
                            fontSize = 16.sp
                        )
                    )
                },
                colors = TextFieldDefaults.textFieldColors(
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent,
                    backgroundColor = Color(0xFFECECEC),
                )

            )
            Button(
                modifier = Modifier.height(56.dp),
                onClick = { sendMessage() },
                enabled = inputValue.isNotBlank(),
                shape = MaterialTheme.shapes.extraLarge,
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = Purple80,
                    contentColor = Color(0xFF000000),
                    disabledBackgroundColor = Color(0xFFECECEC),
                )
            ) {
                Icon(
                    imageVector = Icons.Default.Send,
                    contentDescription = "Envoyer"
                )
            }
        }
    }
}

@Composable
fun ChatBubble(sms: SmsEntry) {
    Column(
        horizontalAlignment = if (messageIsMine(sms)) {
            androidx.compose.ui.Alignment.End
        } else {
            androidx.compose.ui.Alignment.Start
        }
    ) {
        if (sms.quoted != null) {
            Text(
                modifier = Modifier.padding(end = 6.dp, bottom = 2.dp),
                text = if (messageIsMine(sms) && !messageIsMine(sms.quoted!!) || messageIsMine(sms) && sms.quoted == dummySms) {
                    "Vous avez répondu"
                } else if (!messageIsMine(sms) && sms.quoted == dummySms) {
                    "A envoyé une réponse"
                } else if (!messageIsMine(sms) && messageIsMine(sms.quoted!!)) {
                    "Vous a répondu"
                } else if (messageIsMine(sms) && messageIsMine(sms.quoted!!)) {
                    "Réponse à vous-même"
                } else {
                    "Réponse à soi-même"
                },
                style = TextStyle(
                    fontStyle = FontStyle.Italic,
                    fontWeight = FontWeight.Normal,
                    fontSize = 11.sp
                ),
                textAlign = TextAlign.Right
            )
        }
        Surface(
            color = if (messageIsMine(sms)) {
                SelfMessageColor
            } else {
                OtherMessageColor
            },
            contentColor = MessageTextColor,
            shape = MaterialTheme.shapes.extraLarge,
            shadowElevation = 0.dp,
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
        if (sms.quoted != null) {
            val quoted = sms.quoted
            Surface(
                modifier = Modifier.padding(top = 0.dp),
                color = if (messageIsMine(sms.quoted!!)) {
                    SelfMessageColor
                } else {
                    OtherMessageColor
                },
                contentColor = QuotedTextColor,
                shape = MaterialTheme.shapes.extraLarge,
                shadowElevation = 0.dp,
            ) {
                Text(
                    text = quoted!!.message,
                    modifier = Modifier.padding(8.dp),
                    style = TextStyle(
                        fontSize = 16.sp,
                        fontStyle = FontStyle.Normal,
                        fontWeight = FontWeight.Normal
                    )
                )
            }
        }
    }
}
