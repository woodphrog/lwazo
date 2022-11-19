package dev.hongjun.lwazo

import android.os.Bundle
import android.provider.Telephony.Sms
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.fragment.findNavController
import dev.hongjun.lwazo.databinding.FragmentFirstBinding
import java.time.LocalDateTime


/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class FirstFragment : Fragment() {

    private var _binding: FragmentFirstBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentFirstBinding.inflate(inflater, container, false)

        return binding.root

    }

//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//
//    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.buttonFirst.setOnClickListener {
            findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment)
        }
        var l1 = MutableList<SmsEntry>(1){SmsEntry("a", "a", "bruh", LocalDateTime.now())}
//        messageListScreenView(
//            title = "lol",
//            list = l1,
//            fabClick = {},
//            itemClick = {})
    }

//    @Composable
//    fun SenderIcon() {
//        Box(
//            modifier = Modifier.clip(CircleShape)
//                .size(30.dp)
//        ) {
//            Icon(
//                asset = Resource(id = R.drawable.ic_person),
//                Modifier = Modifier.fillMaxSize(),
//                tint = Color.LightGray,
//            )
//        }
//    }
    @Composable
    fun MessagesList(list: MutableList<SmsEntry>,
                     itemClick : (message : SmsEntry) -> Unit){
        val list2 = listOf("A", "B", "C", "D") + ((0..100).map { it.toString() })
        LazyColumn(modifier = Modifier.fillMaxSize()) {items(items = list.map{it.message},
    itemContent = { item ->
        Log.d("COMPOSE", "This get rendered $item")
        when (item) {
            "A" -> {
                Text(text = item, style = TextStyle(fontSize = 80.sp))
            }
            "B" -> {
                Button(onClick = {}) {
                    Text(text = item, style = TextStyle(fontSize = 80.sp))
                }
            }
            "C" -> {
                //Do Nothing
            }
            "D" -> {
                Text(text = item)
            }
            else -> {
                Text(text = item, style = TextStyle(fontSize = 80.sp))
            }
        }
    }
    )


    }
    }
    @Composable
    fun messageListScreenView(
        title:String,
        list:MutableList<SmsEntry>,
        fabClick:()->Unit?,
        itemClick:(smsEntry:SmsEntry)->Unit
    ) {
        Scaffold() {
            MessagesList(list=list, itemClick = itemClick)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}