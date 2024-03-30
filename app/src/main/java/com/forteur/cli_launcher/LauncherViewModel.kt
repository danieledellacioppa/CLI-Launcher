package com.forteur.cli_launcher

import android.content.pm.ApplicationInfo
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

class LauncherViewModel {
    var debugText by mutableStateOf("CLI Launcher\n")
    private var appList by mutableStateOf<List<ApplicationInfo>>(listOf())

//    fun getDebugText(): String {
//        return debugText
//    }

    fun appendDebugText(text: String) {
        debugText += text
    }
}