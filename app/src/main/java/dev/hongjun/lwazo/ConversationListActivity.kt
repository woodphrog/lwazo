package dev.hongjun.lwazo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import dev.hongjun.lwazo.ui.theme.LoiseauBleuTheme
import dev.hongjun.lwazo.ui.theme.Purple80
import dev.hongjun.lwazo.ui.theme.SelfMessageColor

class ConversationListActivity : ComponentActivity() {
    var mutableContactList: SnapshotStateList<String>? = null

    private fun registerMutableContactList(list: SnapshotStateList<String>): SnapshotStateList<String> {
        mutableContactList = list
        return list
    }

    @Composable
    fun ContactList(navController: NavController) {
        val contacts = registerMutableContactList(remember {
            mutableStateListOf<String>()
        })
        contacts.clear()
        contacts.addAll(SmsManager.getSenderList().keys.toMutableStateList())
        Column(modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())) {
            for (contact in contacts) {
                Row(modifier = Modifier.fillMaxWidth()) {
                    Card(modifier = Modifier
                        .fillMaxWidth()
                        .height(70.dp)
                        .clickable { navController.navigate("conversation/$contact") }
                        .fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = Color.White,
                        ),
                        shape = MaterialTheme.shapes.large,
                    ) {
                        val msgList = SmsManager.getSenderList()[contact]?.getSmsEntries()
                        val lastMsg = if (msgList?.size == 0) null else msgList?.last()?.message
                        val lastMsgDisplayed = lastMsg ?: "Cliquez pour démarrer une conversation"
                        Text(text = """
                            $contact
                        """.trimIndent().trimMargin(),
                            fontSize = 25.sp,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 10.dp, top = 5.dp, end = 10.dp),
                        )
                        Text(text = """
                            $lastMsgDisplayed
                        """.trimIndent().trimMargin(),
                            fontSize = 15.sp,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 12.dp, top = 0.dp, end = 10.dp),
                            color = if (lastMsg == null) Color.Gray else Color.Black,
                            style = TextStyle(
                                fontStyle =  if (lastMsg == null)
                                    androidx.compose.ui.text.font.FontStyle.Italic
                                else
                                    androidx.compose.ui.text.font.FontStyle.Normal,
                            ),
                        )
                    }
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            LoiseauBleuTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()

                    NavHost(navController = navController, startDestination = "conversation_list") {
                        composable("conversation_list") {
                            Scaffold(
                                topBar = {
                                    MessagesListTopBar(navController, title = "Messages")
                                }
                            ) {
                                println(it)
                                ContactList(navController = navController)
                                Box(
                                ) {
                                    //FloatingActionButton(
                                    //    modifier = Modifier
                                    //        .padding(all = 20.dp),
                                    //    onClick = {
                                    //        navController.navigate("new_conversation")
                                    //    },
                                    //) {
                                    //    Icon(
                                    //        imageVector = Icons.Filled.Add,
                                    //        contentDescription = "Démarrer une conversation",
                                    //    )
                                    //}
                                }
                            }
                        }
                        composable("conversation/{destinationNumber}") {
                            val destinationNumber = it.arguments?.getString("destinationNumber")
                            if (destinationNumber != null) {
                                Conversation(navController, destinationNumber)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MessagesListTopBar(navController: NavController, title: String) {
    Row() {
        TopAppBar(
            title = {
                Text(
                    text = title, style = TextStyle(
                        textAlign = TextAlign.Center, fontSize = 20.sp
                    )
                )
            },
            backgroundColor = Color.White,
        )
    }
}
