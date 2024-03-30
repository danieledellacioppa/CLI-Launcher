package com.forteur.cli_launcher

import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.forteur.cli_launcher.ui.theme.CLILauncherTheme

class MainActivity : ComponentActivity() {
    val launcherViewModel = LauncherViewModel()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CLILauncherTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppConsoleLauncher(packageManager = packageManager, launcherViewModel)
                }
            }
        }
    }
}

//@Composable
//fun AppLauncher() {
//    // Ricorda la lista delle applicazioni installate
//    val appList = remember { mutableStateOf<List<ApplicationInfo>>(listOf()) }
//    val packageManager = LocalContext.current.packageManager
//
//    // Carica la lista delle applicazioni quando il composable diventa visibile
//    LaunchedEffect(Unit) {
//        appList.value = packageManager.getInstalledApplications(PackageManager.GET_META_DATA)
//    }
//
//    // UI per mostrare e selezionare le app da lanciare
//    AppSelector(appList = appList.value) { appName ->
//        // Qui puoi implementare la logica per lanciare l'app selezionata
//        // Utilizza il packageManager per creare e lanciare l'intent
//    }
//}
//
//@Composable
//fun AppSelector(appList: List<ApplicationInfo>, onAppSelected: (String) -> Unit) {
//    // Qui implementi la UI per mostrare la lista delle app e selezionarne una
//    // Quando un'app viene selezionata, chiama onAppSelected con il nome dell'app
//}

@Composable
fun AppConsoleLauncher(packageManager: PackageManager, launcherViewModel: LauncherViewModel) {
    var inputText by remember { mutableStateOf("") }
    val context = LocalContext.current

    LazyColumn(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxSize()
    ) {
        // Usa item { ... } per inserire un singolo elemento statico
        item {
            Text(
                text = launcherViewModel.debugText,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }
        item {
            TextField(
                value = inputText,
                onValueChange = { inputText = it },
                label = { Text("Nome dell'App") },
                modifier = Modifier.fillMaxWidth()
            )
        }
        item {
            Spacer(modifier = Modifier.height(8.dp))
        }
        item {
            Button(
                onClick = { launchAppByName(inputText, packageManager, context, launcherViewModel) },

            ) {
                Text("Lancia App")
            }
        }
    }
}


fun launchAppByName(appName: String, packageManager: PackageManager, context: Context, launcherViewModel: LauncherViewModel) {
    // Qui dovresti implementare la logica per trovare l'applicazione basandoti sul nome.
    // Questo esempio assume che tu abbia una funzione che mappa il nome dell'app al suo package name.
    val packageName = findAppPackageByName(appName, packageManager, launcherViewModel)

    packageName?.let {
        val launchIntent = packageManager.getLaunchIntentForPackage(it)
        if (launchIntent != null) {
            context.startActivity(launchIntent)
        } else {
            // Gestisci il caso in cui l'app non può essere lanciata o non è trovata
            Toast.makeText(context, "App non trovata", Toast.LENGTH_SHORT).show()
            launcherViewModel.appendDebugText("App non trovata\n")
        }
    }
}

fun findAppPackageByName(appName: String, packageManager: PackageManager, launcherViewModel: LauncherViewModel): String? {

    val mainIntent = Intent(Intent.ACTION_MAIN, null)
    mainIntent.addCategory(Intent.CATEGORY_LAUNCHER)
    val launchableApps = packageManager.queryIntentActivities(mainIntent, 0)
    val appList = mutableListOf<ApplicationInfo>()
    for (resolveInfo in launchableApps) {
        val appInfo = resolveInfo.activityInfo.applicationInfo
        appList.add(appInfo)
    }
//    _apps.value = appList
//
//    val appList = packageManager.getInstalledApplications(PackageManager.GET_META_DATA)
//    Log.d("AppList", appList.toString())
    for (app in appList) {
        launcherViewModel.appendDebugText(app.loadLabel(packageManager).toString() + "\n")
        val label = app.loadLabel(packageManager).toString()
        if (label.equals(appName, ignoreCase = true)) {
            return app.packageName
        }
    }
    return null
}
