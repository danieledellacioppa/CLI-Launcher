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
    private val _appList = MutableStateFlow<List<ApplicationInfo>>(emptyList())
    val appList: StateFlow<List<ApplicationInfo>> = _appList


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
            val apps = launchableApps.map { it.activityInfo.applicationInfo }
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

}