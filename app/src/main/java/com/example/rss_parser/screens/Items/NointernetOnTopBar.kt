package com.example.rss_parser.screens.Items

import android.content.Context
import android.content.Intent
import android.provider.Settings
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.clickable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.WifiOff
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

@Composable
fun NoInternetOnTopBar(isConnected:Boolean,context:Context){
    AnimatedVisibility(
        visible = !isConnected,
        enter = fadeIn(

            initialAlpha = 0.4f
        ),
        exit = fadeOut(
            animationSpec = tween(durationMillis = 250)
        )
    ) {
        Icon(
            imageVector = Icons.Rounded.WifiOff,
            contentDescription = null,
            tint = Color.Red,
            modifier = Modifier.clickable {
                val settingsIntent = Intent(Settings.ACTION_WIFI_SETTINGS)
                context.startActivity(settingsIntent)
            }
        )

    }
}