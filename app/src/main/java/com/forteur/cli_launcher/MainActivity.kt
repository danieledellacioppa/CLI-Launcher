package com.forteur.cli_launcher

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.forteur.cli_launcher.ui.theme.CLILauncherTheme
import androidx.compose.ui.graphics.ImageBitmap

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
fun AppConsoleLauncher(launcherViewModel: LauncherViewModel = viewModel()) {
    // Se stai usando StateFlow, convertilo in un State con .collectAsState() invece di .observeAsState()
    val appList by launcherViewModel.appList.collectAsState()
    var inputText by remember { mutableStateOf("") }
    val context = LocalContext.current

    Column(modifier = Modifier.fillMaxSize()) { // Occupa tutto lo spazio disponibile
        LazyVerticalGrid(
            columns = GridCells.Fixed(4),
            contentPadding = PaddingValues(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(appList.size) { index ->
                AppIcon(app = index) {
                    // Azione da eseguire quando l'icona dell'app viene cliccata
                }
            }
        }
        TextField(
            value = inputText,
            onValueChange = { inputText = it },
            label = { Text("Nome dell'App") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Button(
            onClick = { launcherViewModel.launchAppByName(inputText, context) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            Text("Lancia App")
        }
    }
}

@Composable
fun AppIcon(app: Int, onClick: () -> Unit) {
    // Sostituire con l'icona effettiva dell'app
    // ImageBitmap è un placeholder, sostituire con l'icona effettiva
    Button(onClick = onClick) {
        ImageBitmap(1, 1)
    }

}

data class AppInfo(
    val name: String,
    val packageName: String,
    val icon: ImageBitmap // Usare ImageBitmap per Compose
)

