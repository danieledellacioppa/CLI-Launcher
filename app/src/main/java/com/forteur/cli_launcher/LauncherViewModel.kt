package com.forteur.cli_launcher

import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

class LauncherViewModel(context: Context) {
    var debugText by mutableStateOf("CLI Launcher\n")
    private var appList by mutableStateOf<List<ApplicationInfo>>(listOf())
    var packageManager: PackageManager

    init {
        packageManager = context.packageManager
        loadApps()
    }

    private fun loadApps() {
        val mainIntent = Intent(Intent.ACTION_MAIN, null).apply {
            addCategory(Intent.CATEGORY_LAUNCHER)
        }
        val launchableApps = packageManager.queryIntentActivities(mainIntent, 0)
        appList = launchableApps.map { it.activityInfo.applicationInfo }
    }

    fun appendDebugText(text: String) {
        debugText += text
    }

    fun launchAppByName(appName: String, context: Context) {
        appendDebugText("Lancio app: $appName\n")

        val packageName = findAppPackageByName(appName, packageManager)
        appendDebugText("Package: $packageName\n")

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

    fun findAppPackageByName(appName: String, packageManager: PackageManager): String? {
        for (app in appList) {
            appendDebugText(app.loadLabel(packageManager).toString() + "\n")
            val label = app.loadLabel(packageManager).toString()
            if (label.equals(appName, ignoreCase = true)) {
                return app.packageName
            }
        }
        return null
    }
}