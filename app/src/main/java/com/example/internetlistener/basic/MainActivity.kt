package com.example.internetlistener.basic

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.internetlistener.ui.theme.InternetListenerTheme

class MainActivity : ComponentActivity(), InternetConnectionCallback {
    private val homeViewModel: HomeViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        InternetConnectionObserver.register()

        setContent {
            InternetListenerTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val modifier = Modifier
                        .clip(shape = RoundedCornerShape(8.dp))
                        .background(color = Color.Gray)
                        .padding(horizontal = 32.dp, vertical = 16.dp)
                    InternetBox(modifier, this@MainActivity)
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        InternetConnectionObserver.unRegister()
    }

    override fun onConnected() {
        Toast.makeText(this, "Internet Connection Resume", Toast.LENGTH_SHORT).show()
        homeViewModel.setConnectionState(true)
    }

    override fun onDisconnected() {
        Toast.makeText(this, "Internet Connection Lost", Toast.LENGTH_SHORT).show()
        homeViewModel.setConnectionState(false)
    }
}

@Composable
fun InternetBox(modifier: Modifier = Modifier, lifecycleOwner: LifecycleOwner?= null) {
    var connectionState: Boolean by remember {
        mutableStateOf(true)
    }

    val homeViewModel: HomeViewModel = viewModel()
    lifecycleOwner?.let {
        homeViewModel.connectionState.observe(it){
            connectionState = it
        }
    }

    Column(verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .width(IntrinsicSize.Max)
            .padding(16.dp)
    ) {
        Box(modifier= modifier
        ) {
            Row(horizontalArrangement = Arrangement.Center) {
                Text(text = "Internet Status: ", fontSize = 18.sp)
                val message = if(connectionState) "Connected" else "Disconnected"
                val color = if(connectionState) Color.Green else Color.Red
                Text(text = message, color = color, fontSize = 18.sp)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    val modifier = Modifier
        .clip(shape = RoundedCornerShape(8.dp))
        .background(color = Color.Gray)
        .padding(horizontal = 16.dp, vertical = 8.dp)

    InternetListenerTheme {
        InternetBox(modifier)
    }
}