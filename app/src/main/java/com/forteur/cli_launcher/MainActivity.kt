package com.forteur.cli_launcher

import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.forteur.cli_launcher.ui.theme.CLILauncherTheme
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import coil.compose.rememberImagePainter
import coil.request.ImageRequest

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

fun onAppClick(appInfo: ApplicationInfo, context: Context) {
    val launchIntent = context.packageManager.getLaunchIntentForPackage(appInfo.packageName)
    context.startActivity(launchIntent)
}

@Composable
fun AppConsoleLauncher(launcherViewModel: LauncherViewModel = viewModel()) {
    val appList by launcherViewModel.appList.collectAsState()
    val context = LocalContext.current
    AppList(apps = appList, packageManager = launcherViewModel.packageManager, onAppClick = { onAppClick(it, context) })
}

@Composable
fun AppList(apps: List<ApplicationInfo>, packageManager: PackageManager, onAppClick: (ApplicationInfo) -> Unit) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(4),
        contentPadding = PaddingValues(4.dp) // Aggiungi un piccolo padding intorno alla griglia
    ) {
        items(apps) { app ->
            AppItem(appInfo = app, packageManager = packageManager, onClick = { onAppClick(app) })
        }
    }
}

@Composable
fun AppItem(appInfo: ApplicationInfo, packageManager: PackageManager, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .padding(4.dp)
            .aspectRatio(1f)
            .clickable(onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        val iconUri = "android.resource://${appInfo.packageName}/${appInfo.icon}"
        Image(
            painter = // Opzionali: Placeholder, Error, ecc.
            rememberAsyncImagePainter(
                ImageRequest.Builder(LocalContext.current).data(data = iconUri).apply(block = fun ImageRequest.Builder.() {
                    // Opzionali: Placeholder, Error, ecc.
                    placeholder(R.drawable.ic_launcher_foreground)
                    error(R.drawable.ic_launcher_background)
                    fallback(R.drawable.ic_launcher_background)
                }).build()
            ),
            contentDescription = "${appInfo.loadLabel(packageManager)} icon",
            modifier = Modifier
                .size(52.dp),
            contentScale = ContentScale.Fit
        )
        Text(
            text = appInfo.loadLabel(packageManager).toString(),
            fontSize = 12.sp,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(top = 4.dp)
        )
    }
}

