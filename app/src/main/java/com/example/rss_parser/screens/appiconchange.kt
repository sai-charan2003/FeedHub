package com.example.rss_parser.screens

import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.rss_parser.R
import com.example.rss_parser.screens.Views.changeIcon


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun appiconchange(navHostController: NavHostController) {
    val Scroll= TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    val haptic= LocalHapticFeedback.current
    var showwarning by remember {
        mutableStateOf(false)
    }
    var icon by remember {
        mutableStateOf(R.drawable.originalicon)
    }

    val context= LocalContext.current
    val packageManager: PackageManager = context.packageManager
    val sharedPreferences:SharedPreferences=context.getSharedPreferences("showimages",Context.MODE_PRIVATE)
    val disabled=sharedPreferences.getString("enabledpackage","com.example.rss_parser.MainActivity")
    val editor=sharedPreferences.edit()
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
            }
        )
    }){






        LazyVerticalGrid(columns = GridCells.Fixed(3),modifier= Modifier
            .padding(it)
            .fillMaxSize()) {
            item {
                Image(painter = painterResource(id = R.drawable.orange),
                    contentScale = ContentScale.Fit,
                    contentDescription = null,


                    modifier = Modifier

                        .padding(20.dp)
                        .width(80.dp)
                        .height(80.dp)


                        .clickable {
                            icon = R.drawable.orange

                            enable = "com.example.rss_parser.MainActivityorange"
                            showwarning = true
                        })
            }
            item {
                Image(
                    painter = painterResource(id = R.drawable.blackicon),
                    contentScale = ContentScale.Fit,
                    contentDescription = null,
                    modifier = Modifier
                        .padding(20.dp)
                        .height(80.dp)
                        .width(80.dp)

                        .clickable {
                            icon = R.drawable.blackicon
                            showwarning = true
                            enable = "com.example.rss_parser.MainActivityblack"

                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        }
                )

            }
            item {
                Image(
                    painter = painterResource(id = R.drawable.originalicon),
                    contentScale = ContentScale.Fit,
                    contentDescription = null,
                    modifier = Modifier
                        .padding(20.dp)
                        .width(80.dp)
                        .height(80.dp)

                        .clickable {
                            icon = R.drawable.originalicon
                            showwarning = true
                            enable = "com.example.rss_parser.MainActivity"

                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        }
                )
            }
            item {
                Image(
                    painter = painterResource(id = R.drawable.whiteicon),
                    contentScale = ContentScale.Fit,
                    contentDescription = null,
                    modifier = Modifier
                        .padding(20.dp)
                        .width(80.dp)
                        .height(80.dp)

                        .clickable {
                            icon = R.drawable.whiteicon
                            showwarning = true
                            enable = "com.example.rss_parser.MainActivitywhite"

                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)

                        }
                )
            }
            item {
                Image(
                    painter = painterResource(id = R.drawable.mint),
                    contentScale = ContentScale.Fit,
                    contentDescription = null,
                    modifier = Modifier
                        .padding(20.dp)
                        .width(80.dp)
                        .height(80.dp)

                        .clickable {
                            icon = R.drawable.mint
                            showwarning = true
                            enable = "com.example.rss_parser.MainActivitymint"

                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)

                        }
                )
            }
            item {
                Image(
                    painter = painterResource(id = R.drawable.twilight),
                    contentScale = ContentScale.Fit,
                    contentDescription = null,
                    modifier = Modifier
                        .padding(20.dp)
                        .width(80.dp)
                        .height(80.dp)

                        .clickable {
                            icon = R.drawable.twilight
                            showwarning = true
                            enable = "com.example.rss_parser.MainActivitytwilight"

                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)

                        }
                )
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
                if (disabled != null) {
                    changeIcon(
                        packageManager,
                        context,
                        enabled = enable,
                        disabled = disabled
                    )
                }
                editor.putString(
                    "enabledpackage",
                    enable
                )
                editor.apply()


            }) {
                Text("OK")

            } })
    }
}

