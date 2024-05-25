package com.example.rss_parser.screens

import android.content.pm.PackageManager
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.rss_parser.R
import com.example.rss_parser.Utils.AppUtils
import com.example.rss_parser.Utils.SharedPref


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppIconChange(navHostController: NavHostController) {
    val Scroll= TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    val haptic= LocalHapticFeedback.current
    var showwarning by remember {
        mutableStateOf(false)
    }
    var icon by remember {
        mutableStateOf(R.drawable.originalicon)
    }
    var showdropdownmenu by remember {
        mutableStateOf(false)
    }

    val context= LocalContext.current
    val packageManager: PackageManager = context.packageManager
    val SharedPref= SharedPref(context)
    val EnabledActivity=SharedPref.EnabledActivity

    var enable by remember {
        mutableStateOf("")
    }
    Scaffold (modifier= Modifier
        .fillMaxSize()
        .nestedScroll(Scroll.nestedScrollConnection),topBar = {
        LargeTopAppBar(
            title = { Text("Change App Icon") },
            scrollBehavior = Scroll,
            navigationIcon = {
                IconButton(onClick = { navHostController.popBackStack()}) {
                    Icon(imageVector = Icons.AutoMirrored.Outlined.ArrowBack, contentDescription =null )

                }
            },
            actions = {
                IconButton(onClick = { showdropdownmenu = true }) {
                    Icon(
                        imageVector = Icons.Outlined.MoreVert,
                        contentDescription = null
                    )
                }
                DropdownMenu(
                    expanded = showdropdownmenu,
                    onDismissRequest = { showdropdownmenu = false }) {
                    DropdownMenuItem(
                        text = { Text("Restore default") },
                        onClick = {
                            showdropdownmenu =false;
                            if (EnabledActivity != "com.example.rss_parser.MainActivity") {
                                icon = R.drawable.originalicon
                                showwarning = true
                                enable = "com.example.rss_parser.MainActivity"
                            }

                        }
                    )



                }

            }

        )
    }){

        LazyVerticalGrid(columns = GridCells.Fixed(3),modifier= Modifier
            .padding(it)
            .fillMaxSize()) {
            item {
                Surface(
                    modifier= Modifier
                        .fillMaxSize()
                        .padding(20.dp)
                        .clip(RoundedCornerShape(20.dp))
                        ,

                    color =
                    if (EnabledActivity == "com.example.rss_parser.MainActivityorange") {
                        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                    } else {
                        Color.Transparent

                    }




                ) {
                    Image(
                        painter = painterResource(id = R.drawable.orange),
                        contentDescription = null,
                        modifier = Modifier
                            .padding(5.dp)
                            .clip(RoundedCornerShape(20.dp))


                            .clickable {
                                if (EnabledActivity != "com.example.rss_parser.MainActivityorange") {
                                    icon = R.drawable.orange
                                    enable = "com.example.rss_parser.MainActivityorange"
                                    showwarning = true
                                }
                            }
                    )
                }
            }
            item {
                Surface(
                    modifier= Modifier
                        .fillMaxSize()
                        .padding(20.dp)
                        .clip(RoundedCornerShape(20.dp))
                    ,

                    color =
                    if (EnabledActivity == "com.example.rss_parser.MainActivityblack") {
                        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                    } else {
                        Color.Transparent

                    }




                ) {
                    Image(
                        painter = painterResource(id = R.drawable.blackicon),
                        contentDescription = null,
                        modifier = Modifier
                            .padding(5.dp)
                            .clip(RoundedCornerShape(20.dp))


                            .clickable {
                                if (EnabledActivity != "com.example.rss_parser.MainActivityblack") {
                                    icon = R.drawable.blackicon
                                    showwarning = true
                                    enable = "com.example.rss_parser.MainActivityblack"

                                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                }
                            }
                    )
                }


            }
            item {
                Surface(
                    modifier= Modifier
                        .fillMaxSize()
                        .padding(20.dp)
                        .clip(RoundedCornerShape(20.dp))
                    ,

                    color =
                    if (EnabledActivity == "com.example.rss_parser.MainActivity") {
                        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                    } else {
                        Color.Transparent

                    }




                ) {
                    Image(
                        painter = painterResource(id = R.drawable.originalicon),
                        contentDescription = null,
                        modifier = Modifier
                            .padding(5.dp)
                            .clip(RoundedCornerShape(20.dp))


                            .clickable {
                                if (EnabledActivity != "com.example.rss_parser.MainActivity") {
                                    icon = R.drawable.originalicon
                                    showwarning = true
                                    enable = "com.example.rss_parser.MainActivity"

                                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                }
                            }
                    )
                }

            }
            item {
                Surface(
                    modifier= Modifier
                        .fillMaxSize()
                        .padding(20.dp)
                        .clip(RoundedCornerShape(20.dp))
                    ,

                    color =
                    if (EnabledActivity == "com.example.rss_parser.MainActivitywhite") {
                        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                    } else {
                        Color.Transparent

                    }




                ){
                    Image(
                        painter = painterResource(id = R.drawable.whiteicon),
                        contentDescription = null,
                        modifier = Modifier
                            .padding(5.dp)
                            .clip(RoundedCornerShape(20.dp))


                            .clickable {
                                if (EnabledActivity != "com.example.rss_parser.MainActivitywhite") {
                                    icon = R.drawable.whiteicon
                                    showwarning = true
                                    enable = "com.example.rss_parser.MainActivitywhite"

                                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                }

                            }
                    )
                }


            }
            item {
                Surface(
                    modifier= Modifier
                        .fillMaxSize()
                        .padding(20.dp)
                        .clip(RoundedCornerShape(20.dp))
                    ,

                    color =
                    if (EnabledActivity == "com.example.rss_parser.MainActivitymint") {
                        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                    } else {
                        Color.Transparent

                    }




                ) {
                    Image(
                        painter = painterResource(id = R.drawable.mint),
                        contentDescription = null,
                        modifier = Modifier
                            .padding(5.dp)
                            .clip(RoundedCornerShape(20.dp))


                            .clickable {
                                if (EnabledActivity != "com.example.rss_parser.MainActivitymint") {
                                    icon = R.drawable.mint
                                    showwarning = true
                                    enable = "com.example.rss_parser.MainActivitymint"

                                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                }
                            }
                    )
                }

            }
            item {
                Surface(
                    modifier= Modifier
                        .fillMaxSize()
                        .padding(20.dp)
                        .clip(RoundedCornerShape(20.dp))
                    ,

                    color =
                    if (EnabledActivity == "com.example.rss_parser.MainActivitytwilight") {
                        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                    } else {
                        Color.Transparent

                    }




                ) {
                    Image(
                        painter = painterResource(id = R.drawable.twilight),
                        contentDescription = null,
                        modifier = Modifier
                            .padding(5.dp)
                            .clip(RoundedCornerShape(20.dp))


                            .clickable {
                                if (EnabledActivity != "com.example.rss_parser.MainActivitytwilight") {
                                    icon = R.drawable.twilight
                                    showwarning = true
                                    enable = "com.example.rss_parser.MainActivitytwilight"

                                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                }
                            }
                    )
                }

            }
            item {
                Surface(
                    modifier= Modifier
                        .fillMaxSize()
                        .padding(20.dp)
                        .clip(RoundedCornerShape(20.dp))
                    ,

                    color =
                    if (EnabledActivity == "com.example.rss_parser.MainActivityweb") {
                        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                    } else {
                        Color.Transparent

                    }




                ) {
                    Image(
                        painter = painterResource(id = R.drawable.web),
                        contentDescription = null,
                        modifier = Modifier
                            .padding(5.dp)
                            .clip(RoundedCornerShape(20.dp))


                            .clickable {
                                if (EnabledActivity != "com.example.rss_parser.MainActivityweb") {
                                    icon = R.drawable.web
                                    showwarning = true
                                    enable = "com.example.rss_parser.MainActivityweb"

                                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                }
                            }
                    )
                }

            }
            item {
                Surface(
                    modifier= Modifier
                        .fillMaxSize()
                        .padding(20.dp)
                        .clip(RoundedCornerShape(20.dp))
                    ,

                    color =
                    if (EnabledActivity == "com.example.rss_parser.MainActivitynews") {
                        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                    } else {
                        Color.Transparent

                    }




                ) {
                    Image(
                        painter = painterResource(id = R.drawable.news),
                        contentDescription = null,
                        modifier = Modifier
                            .padding(5.dp)
                            .clip(RoundedCornerShape(20.dp))


                            .clickable {
                                if (EnabledActivity != "com.example.rss_parser.MainActivitynews") {
                                    icon = R.drawable.news
                                    showwarning = true
                                    enable = "com.example.rss_parser.MainActivitynews"

                                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                }
                            }
                    )
                }

            }
        }



    }
    if(showwarning){

        AlertDialog(onDismissRequest = { showwarning=false },
            icon = { Image(painter = painterResource(id = icon), contentDescription = null,modifier=Modifier.size(80.dp))},
            title = {Text("Change app icon",modifier=Modifier.fillMaxWidth(), textAlign = TextAlign.Center)},
            text = {Text("FeedHub will need to close to change the app icon",modifier=Modifier.fillMaxWidth(), textAlign = TextAlign.Center)},
            dismissButton = { TextButton(onClick = {showwarning=false;enable=""}) {
                Text("Cancel")

            } }
            , confirmButton = { TextButton(onClick = {
                if (EnabledActivity != null) {
                    AppUtils.changeIcon(
                        packageManager,
                        context,
                        enabled = enable,
                        disabled = EnabledActivity
                    )
                }
                SharedPref.EnabledActivity=enable


            }) {
                Text("OK")

            } })
    }
}




