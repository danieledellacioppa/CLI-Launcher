package com.forteur.cli_launcher

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.forteur.cli_launcher.ui.theme.CLILauncherTheme

class MainActivity : ComponentActivity() {
    lateinit var launcherViewModel: LauncherViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        launcherViewModel = LauncherViewModel(application)
        setContent {
            CLILauncherTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppConsoleLauncher(launcherViewModel)
                }
            }
        }
    }
}

@Composable
fun AppConsoleLauncher(launcherViewModel: LauncherViewModel) {
    var inputText by remember { mutableStateOf("") }
    val context = LocalContext.current

    Column(modifier = Modifier.fillMaxSize()) { // Occupa tutto lo spazio disponibile
        LazyColumn(
            modifier = Modifier
                .weight(1f) // Fai in modo che la LazyColumn occupi tutto lo spazio disponibile tranne quello necessario per TextField e Button
                .padding(16.dp)
        ) {
            item {
                Text(text = launcherViewModel.debugText, modifier = Modifier.padding(bottom = 8.dp))
            }
        }
        TextField(
            value = inputText,
            onValueChange = { inputText = it },
            label = { Text("Nome dell'App") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp) // Aggiungi padding se necessario
        )
        Spacer(modifier = Modifier.height(8.dp))
        Button(
            onClick = { launcherViewModel.launchAppByName(inputText, context) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp) // Aggiungi padding per mantenere consistenza
        ) {
            Text("Lancia App")
        }
    }
}
