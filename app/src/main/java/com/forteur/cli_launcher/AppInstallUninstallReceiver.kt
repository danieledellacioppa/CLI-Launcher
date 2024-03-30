package com.forteur.cli_launcher

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

class AppInstallUninstallReceiver(private val onAppListChanged: () -> Unit) : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            Intent.ACTION_PACKAGE_ADDED -> {
                val data = intent.data
                val packageName = data?.encodedSchemeSpecificPart
                Log.d("AppReceiver", "App installata: $packageName")
                // Gestisci l'installazione dell'app qui
            }
            Intent.ACTION_PACKAGE_REMOVED -> {
                val data = intent.data
                val packageName = data?.encodedSchemeSpecificPart
                Log.d("AppReceiver", "App disinstallata: $packageName")
                // Gestisci la disinstallazione dell'app qui
            }
        }
        onAppListChanged()
    }
}
