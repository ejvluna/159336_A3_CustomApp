package com.example.customapp

import com.example.customapp.ui.theme.CustomAppTheme
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Alignment
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CustomAppTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    CustomApp()
                }
            }
        }
    }
}

@Composable
fun CustomApp() {
    var claim by remember { mutableStateOf("") }
    var isVerifying by remember { mutableStateOf(false) }
    var result by remember { mutableStateOf<String?>(null) }
    
    // Create a coroutine scope tied to this composable
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Truthiness Checker",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        OutlinedTextField(
            value = claim,
            onValueChange = { claim = it },
            label = { Text("Enter a claim to verify") },
            modifier = Modifier.fillMaxWidth(),
            enabled = !isVerifying
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                isVerifying = true
                // Launch a coroutine in the scope
                scope.launch {
                    // Simulate API call with a delay
                    delay(1000)
                    result = "This is a sample response. In a real app, this would come from the API."
                    isVerifying = false
                }
            },
            enabled = claim.isNotBlank() && !isVerifying
        ) {
            if (isVerifying) {
                CircularProgressIndicator(
                    modifier = Modifier.size(16.dp),
                    color = MaterialTheme.colorScheme.onPrimary,
                    strokeWidth = 2.dp
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Verifying...")
            } else {
                Text("Verify Claim")
            }
        }

        result?.let {
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = "Result:",
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = it)
        }
    }
}