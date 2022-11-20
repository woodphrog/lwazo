package dev.hongjun.lwazo

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.TopAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavHost
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import dev.hongjun.lwazo.ui.theme.LoiseauBleuTheme
import java.time.LocalDateTime

class ConversationListActivity : ComponentActivity() {
    var mutableContactList: SnapshotStateList<String>? = null
    @Composable
    fun registerMutableContactList(list: SnapshotStateList<String>): SnapshotStateList<String> {
        mutableContactList = list
        var a:SnapshotStateList<String> = remember { mutableStateListOf<String>()}
        a.clear()
        a.addAll(list)
        return a

    }
    @Composable
    fun displayContactList(navController: NavController){
        val contacts = registerMutableContactList(remember {
            mutableStateListOf<String>()
        })
        contacts.addAll(SmsManager.getSenderList().keys.toMutableStateList())
//                    Button(onClick = {Log.d("COMPOSE", contacts.size.toString())}) {
//
//                    }
        // val state = rememberSaveable(item) { mutableStateOf(ItemState()) }
        Column(modifier = Modifier.fillMaxSize()) {
            for (contact in contacts) {
                Row (modifier = Modifier.fillMaxWidth()){
                    Card(modifier = Modifier
                        .size(500.dp, 70.dp)
                        .clickable { navController.navigate("conversation/$contact") }
                        .fillMaxWidth(), content = {
                        Text(text = contact, fontSize = 25.sp, modifier = Modifier.fillMaxWidth());
                        val lastTextEntry = SmsManager.getSenderList()[contact]?.getSmsEntries()?.last();
                        Text(text = lastTextEntry?.timestamp?.hour.toString() +":"+ lastTextEntry?.timestamp?.minute.toString() +"  "+ lastTextEntry?.message)
                        })
                }



            }
//                        Card(modifier = Modifier.clickable {contacts.isEmpty(); }, content = { Text(
//                            text = "hhhhhhhhhhhhhhhhhhh"
//                        )})
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            LoiseauBleuTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()

                    NavHost(navController = navController, startDestination = "conversation_list") {
                        composable("conversation_list") {
                            Column(){
                            MessagesListTopBar(navController, title = "Messages")
                            displayContactList(navController = navController)
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
fun ContactsList(navController: NavController, ls:MutableMap<String, SmsConversation>){
    Column(){

        LazyColumn(modifier = Modifier.fillMaxSize()) {items(items = SmsManager.getSenderList().keys.toMutableList(),
            itemContent = { item ->
                Log.d("COMPOSE", "This get rendered $item")
                when (item) {

                    else -> {

                        //Button(onClick = {}) {
                        Text(text = item, style = TextStyle(fontSize = 40.sp))
                        //}
                    }
                }
            }
        )
        }
    }
}

@Composable
fun MessagesListTopBar(navController: NavController, title : String) {
    Row(){
    TopAppBar(
        title = {
            Text(
                text = title,
                style = TextStyle(
                    textAlign = TextAlign.Center,
                    fontStyle = androidx.compose.ui.text.font.FontStyle(1),
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )
            
            )
        },

        backgroundColor = Color.White
    )

    }
}
