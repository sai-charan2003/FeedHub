package com.example.rss_parser.screens.Views

import android.app.Activity
import android.content.ComponentName
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log


fun changeIcon(packageManager: PackageManager,context: Context,enabled:String,disabled:String) {

    packageManager.setComponentEnabledSetting(
        ComponentName(
            context,
            enabled
        ),
        PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
        PackageManager.DONT_KILL_APP
    )

    packageManager.setComponentEnabledSetting(
        ComponentName(
            context,
            disabled
        ),
        PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
        PackageManager.DONT_KILL_APP
    )
}