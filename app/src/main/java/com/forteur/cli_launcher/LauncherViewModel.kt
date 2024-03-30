package com.forteur.cli_launcher

import android.app.Application
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.ImageBitmap
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class LauncherViewModel(application: Application) : AndroidViewModel(application) {
    var debugText by mutableStateOf("CLI Launcher\n")

    private val _appList = MutableStateFlow<List<AppInfo>>(emptyList())
    val appList: StateFlow<List<AppInfo>> = _appList


    var packageManager: PackageManager

    init {
        packageManager = application.packageManager
        loadApps()
        registerAppInstallUninstallReceiver() // Imposta il receiver
    }

    private fun loadApps() {
        viewModelScope.launch {
            val mainIntent = Intent(Intent.ACTION_MAIN, null).apply {
                addCategory(Intent.CATEGORY_LAUNCHER)
            }
            val launchableApps = packageManager.queryIntentActivities(mainIntent, 0)
            val apps = launchableApps.map { resolveInfo ->
                // Convertiamo ogni applicazione in un oggetto AppInfo
                AppInfo(
                    name = resolveInfo.loadLabel(packageManager).toString(),
                    packageName = resolveInfo.activityInfo.packageName,
                    icon = ImageBitmap(1, 1) // Placeholder, necessario sostituire con l'effettiva conversione dell'icona
                )
            }
            _appList.value = apps
        }
    }




    private fun registerAppInstallUninstallReceiver() {
        val filter = IntentFilter().apply {
            addAction(Intent.ACTION_PACKAGE_ADDED)
            addAction(Intent.ACTION_PACKAGE_REMOVED)
            addDataScheme("package")
        }
        getApplication<Application>().registerReceiver(AppInstallUninstallReceiver { updateAppList() }, filter)
    }

    private fun updateAppList() {
        // Ricarica l'elenco delle applicazioni quando viene notificato da BroadcastReceiver
        loadApps()
    }

    override fun onCleared() {
        super.onCleared()
        // Non dimenticare di annullare la registrazione del BroadcastReceiver quando il ViewModel viene distrutto
        getApplication<Application>().unregisterReceiver(AppInstallUninstallReceiver { updateAppList() })
    }

    fun appendDebugText(text: String) {
        debugText += text
    }

    fun clearDebugText() {
        debugText = "CLI Launcher\n"
    }

    fun launchAppByName(appName: String, context: Context) {
//        appendDebugText("Lancio app: $appName\n")

        val packageName = findAppPackageByName(appName)
//        appendDebugText("Package: $packageName\n")

        packageName?.let {
            val launchIntent = packageManager.getLaunchIntentForPackage(it)
            if (launchIntent != null) {
                context.startActivity(launchIntent)
            } else {
                Toast.makeText(context, "App non trovata", Toast.LENGTH_SHORT).show()
                appendDebugText("App non trovata\n")
            }
        }
    }

    fun findAppPackageByName(appName: String): String? {
        clearDebugText()
        for (app in appList.value) {
            val label = app.name // Usa direttamente il nome dell'app memorizzato in AppInfo
            appendDebugText("$label\n")
            if (label.equals(appName, ignoreCase = true)) {
                return app.packageName
            }
        }
        return null
    }

}