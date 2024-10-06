package dev.charan.feedhub

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import androidx.navigation.compose.rememberNavController
import dev.charan.feedhub.Navigation.NavigationAppHost


import dev.charan.feedhub.ui.theme.RSSparserTheme
import kotlinx.coroutines.DelicateCoroutinesApi

class MainActivity : ComponentActivity() {

    @OptIn(DelicateCoroutinesApi::class, ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {

        


        super.onCreate(savedInstanceState)
        installSplashScreen()
        setContent {
                    RSSparserTheme() {
                    WindowCompat.setDecorFitsSystemWindows(window, false)

                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colorScheme.background

                    ) {

                        val navController = rememberNavController()

                        NavigationAppHost(navHostController = navController)

                    }

                }

            }
        }
    }

