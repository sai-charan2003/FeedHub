package dev.charan.feedhub.Utils

import android.content.Context
import android.net.Uri
import androidx.browser.customtabs.CustomTabsIntent

fun openTab(context: Context, URL:String) {
    val builder = CustomTabsIntent.Builder()
    builder.setShowTitle(true)
    builder.setInstantAppsEnabled(true)
    val customBuilder = builder.build()
    customBuilder.launchUrl(context, Uri.parse(URL))
}