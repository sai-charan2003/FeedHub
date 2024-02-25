    package com.example.rss_parser.screens
    
    import android.content.Context
    import android.content.Intent
    import android.content.SharedPreferences
    import android.os.Vibrator
    import android.provider.Settings
    import androidx.compose.foundation.ExperimentalFoundationApi
    import androidx.compose.foundation.clickable
    import androidx.compose.foundation.isSystemInDarkTheme
    import androidx.compose.foundation.layout.fillMaxSize
    import androidx.compose.foundation.lazy.rememberLazyListState
    import androidx.compose.material.icons.Icons
    import androidx.compose.material.icons.outlined.MoreVert
    import androidx.compose.material.icons.outlined.Search
    import androidx.compose.material.icons.rounded.WifiOff
    import androidx.compose.material3.DropdownMenu
    import androidx.compose.material3.DropdownMenuItem
    import androidx.compose.material3.ExperimentalMaterial3Api
    import androidx.compose.material3.Icon
    import androidx.compose.material3.IconButton
    import androidx.compose.material3.Scaffold
    import androidx.compose.material3.Surface
    import androidx.compose.material3.Text
    import androidx.compose.material3.TopAppBar
    import androidx.compose.material3.TopAppBarDefaults
    import androidx.compose.runtime.Composable
    import androidx.compose.runtime.collectAsState
    import androidx.compose.runtime.derivedStateOf
    import androidx.compose.runtime.getValue
    import androidx.compose.runtime.mutableStateOf
    import androidx.compose.runtime.remember
    import androidx.compose.runtime.rememberCoroutineScope
    import androidx.compose.runtime.saveable.rememberSaveable

    import androidx.compose.runtime.setValue
    import androidx.compose.ui.Modifier
    import androidx.compose.ui.graphics.Color
    import androidx.compose.ui.input.nestedscroll.nestedScroll
    import androidx.compose.ui.platform.LocalContext
    import androidx.compose.ui.platform.LocalHapticFeedback
    import androidx.compose.ui.platform.LocalUriHandler
    import androidx.compose.ui.unit.dp
    import androidx.lifecycle.ViewModelProvider
    import androidx.lifecycle.viewmodel.compose.viewModel
    import androidx.navigation.NavHostController
    import com.example.rss_parser.BuildConfig


    import com.example.rss_parser.Navigation.Destinations
    import com.example.rss_parser.check_network.Connectionstatus
    import com.example.rss_parser.check_network.connectivityState
    import com.example.rss_parser.screens.Views.compactview

    import com.example.rss_parser.screens.Views.noncompactview
    import com.example.rss_parser.ui.theme.RSSparserTheme
    import com.example.rss_parser.viewmodel.viewmodel

    import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
    import com.google.ai.client.generativeai.GenerativeModel

    import kotlinx.coroutines.ExperimentalCoroutinesApi


    @OptIn(ExperimentalMaterial3Api::class, ExperimentalCoroutinesApi::class,
        ExperimentalFoundationApi::class
    )
    @Composable
    fun homescreen(navHostController: NavHostController) {
        val connection by connectivityState()



    
        val isConnected = connection === Connectionstatus.Available
        val context = LocalContext.current
        val coroutinescope = rememberCoroutineScope()
        val viewModel = viewModel<viewmodel>(
            factory = object : ViewModelProvider.Factory {
                override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                    return viewmodel(
                        context
                    ) as T
                }
            }
        )

        val timings: LongArray = longArrayOf(100, 100, 100, 100, 100, 100,)
        val amplitudes: IntArray = intArrayOf(23, 41, 65, 103, 160, 255,)
        val repeatIndex = -1 // Do not repeat.

        val listState = rememberLazyListState()
    
    

        val generativeModel = GenerativeModel(
            modelName = "gemini-pro",
            apiKey = BuildConfig.apiKey
        )
        var search by remember {
            mutableStateOf("")
        }
        var summary by remember {
            mutableStateOf("")
        }
        val showScrollToTop by remember { derivedStateOf { listState.firstVisibleItemIndex > 2} }
    
    
    

        val Scroll = TopAppBarDefaults.pinnedScrollBehavior()
        var showdropdownmenu by remember { mutableStateOf(false) }
        var homeloading by remember {
            mutableStateOf(true)
        }
        var aisummarypage by remember {
            mutableStateOf(false)
        }

        var finalloading by remember {
            mutableStateOf(true)
        }


        var selectedCategory by rememberSaveable { mutableStateOf<String?>(null) }
        val haptic = LocalHapticFeedback.current
    
        val isloading by viewModel.isLoading.collectAsState()
        finalloading = isloading || homeloading == true
        val swiperefresh = rememberSwipeRefreshState(isRefreshing = isloading)
    

    
    
        val sharedPreferences: SharedPreferences =
            context.getSharedPreferences("showimages", Context.MODE_PRIVATE)
        val showimages by remember {
            mutableStateOf(sharedPreferences.getBoolean("showimages", true))
        }
        var ishapticenabled by remember {
            mutableStateOf(sharedPreferences.getBoolean("hapticenabled", true))
        }
        val showtop by remember {
            mutableStateOf(sharedPreferences.getBoolean("showtop", true))
        }
        var compactview by remember {
            mutableStateOf(sharedPreferences.getBoolean("compactview",true))
        }


    
    
    
        val dark = isSystemInDarkTheme()
    
        var iscontentloaded by remember {
            mutableStateOf(false)
        }
        var active by remember {
            mutableStateOf(false)
        }
        val vibrator = LocalContext.current.getSystemService(Vibrator::class.java)
    
        val editor = sharedPreferences.edit()
        var dataempty by remember {
            mutableStateOf(false)
        }
        var selected by remember { mutableStateOf(false) }
    
    

    
        val uriHandler = LocalUriHandler.current
        RSSparserTheme {
            Surface(shadowElevation = 10.dp) {
    

                Scaffold(
                    modifier = Modifier
                        .fillMaxSize()
                        .nestedScroll(Scroll.nestedScrollConnection),

                    topBar = {
                        TopAppBar(
                            title = { Text("My Feeds") },
                            scrollBehavior = Scroll,
                            actions = {
                                if (!isConnected) {
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
                                IconButton(onClick = { navHostController.navigate(Destinations.search.route) }) {
                                    Icon(imageVector = Icons.Outlined.Search, contentDescription = null )

                                }
                                IconButton(onClick = { showdropdownmenu = true }) {
                                    Icon(
                                        imageVector = Icons.Outlined.MoreVert,
                                        contentDescription = "more"
                                    )
                                }
    
                                DropdownMenu(
                                    expanded = showdropdownmenu,
                                    onDismissRequest = { showdropdownmenu = false }) {
                                    DropdownMenuItem(
                                        text = { Text("Settings") },
                                        onClick = {
                                            showdropdownmenu =
                                                false;navHostController.navigate(Destinations.settings.route)
                                        })
                                    DropdownMenuItem(
                                        text = { Text("Add new feed") },
                                        onClick = {
                                            showdropdownmenu = false;navHostController.navigate(
                                            Destinations.addnewfeed.route
                                        )
                                        })
                                    DropdownMenuItem(
                                        text = { Text("Bookmarks") },
                                        onClick = {
                                            showdropdownmenu =
                                                false;navHostController.navigate(Destinations.bookmarks.route)
                                        })
    
                                }
                            }
                        )
                    },


                ) {
                    if(sharedPreferences.getString("selectedview","compact")=="Compact"){
                        compactview(it,navHostController)
                    }
                    else if(sharedPreferences.getString("selectedview", "compact")=="Card"){

                        noncompactview(it = it, navHostController = navHostController)
                    }
                    else {
                        compactview(it,navHostController)

                    }

                }
            }
        }
    }
    

    
    
    
    
    
    
