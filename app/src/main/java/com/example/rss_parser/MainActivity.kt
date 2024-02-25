package com.example.rss_parser

import android.content.Context
import android.content.SharedPreferences
import android.content.res.Resources.Theme
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.example.rss_parser.Navigation.Destinations
import com.example.rss_parser.Navigation.NavigationAppHost
import com.example.rss_parser.screens.homescreen
import com.example.rss_parser.screens.signup

import com.example.rss_parser.ui.theme.RSSparserTheme
import io.github.jan.supabase.gotrue.auth
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow

class MainActivity : ComponentActivity() {

    @OptIn(DelicateCoroutinesApi::class, ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d("TAG", "onCreate: hi from main")



        val sharedPreferences: SharedPreferences =
            getSharedPreferences("showimages", Context.MODE_PRIVATE)
        val islog=sharedPreferences.getBoolean("islog",false)
        


        super.onCreate(savedInstanceState)
        installSplashScreen()
        setContent {




                    RSSparserTheme() {
                    WindowCompat.setDecorFitsSystemWindows(window, false)

                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        LaunchedEffect(Unit){


                        }



                        val navController = rememberNavController()

                        NavigationAppHost(navHostController = navController)

                    }

                }

            }
        }
    }

