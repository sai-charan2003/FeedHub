package com.example.rss_parser.screens

import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.rss_parser.Navigation.Destinations

import com.example.rss_parser.ui.theme.RSSparserTheme


@OptIn(ExperimentalMaterial3Api::class)
@Composable

fun settings(navHostController: NavHostController) {
    val context = LocalContext.current
    val haptic = LocalHapticFeedback.current
    val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("showimages", Context.MODE_PRIVATE)

    val editor = sharedPreferences.edit()
    var showimages by remember {
        mutableStateOf(sharedPreferences.getBoolean("showimages", true))
    }
    var showDropdown by remember { mutableStateOf(false) }
    var compactview by remember {
        mutableStateOf(sharedPreferences.getBoolean("compactview",true))
    }
    var selecteditem by remember {
        mutableStateOf(sharedPreferences.getString("selectedview","compact"))
    }
    val backgroundModifier = when (selecteditem) {
        "Compact" -> Modifier.background(MaterialTheme.colorScheme.surfaceTint.copy(alpha = 0.3f))
        "Card" -> Modifier.background(MaterialTheme.colorScheme.surfaceTint.copy(alpha = 0.3f))
        "Text-only"-> Modifier.background(MaterialTheme.colorScheme.surfaceTint.copy(alpha = 0.3f))
        else -> Modifier
    }



    var showtop by remember {
        mutableStateOf(sharedPreferences.getBoolean("showtop", true))
    }
    var isDraggin by remember {
        mutableStateOf(false)
    }

    var coroutinescope= rememberCoroutineScope()
    var isSystemInDarkTheme = isSystemInDarkTheme()
    var darkmode by remember {
        mutableStateOf(sharedPreferences.getBoolean("darkmode",isSystemInDarkTheme))
    }
    val Scroll = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    var hours24 by remember{
        mutableStateOf(sharedPreferences.getBoolean("24hours",true))
    }
    var inappbrowser by remember{
        mutableStateOf(sharedPreferences.getBoolean("inappbrowser",false))
    }

    var ishapticenabled by remember{
        mutableStateOf(sharedPreferences.getBoolean("hapticenabled",true))
    }
    val packageManager: PackageManager = context.packageManager




    RSSparserTheme() {

        Scaffold(modifier= Modifier
            .fillMaxSize()
            .nestedScroll(Scroll.nestedScrollConnection),topBar = {
            LargeTopAppBar(title = { Text(text = "Settings") }, scrollBehavior = Scroll, navigationIcon = {
                IconButton(
                    onClick = { navHostController.popBackStack() }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                        contentDescription = "Back"
                    )

                }
            })
        }) {


            LazyColumn(
                modifier = Modifier
                    .padding(it)
                    .fillMaxSize()

            ) {

                item {
                    ListItem(

                        {
                            Row(
                                modifier = Modifier.fillMaxSize(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    "Feed Websites",
                                    modifier = Modifier.padding(),
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )

                            }
                        },
                        modifier = Modifier.clickable { navHostController.navigate(Destinations.feeds.route) }
                    )
                    HorizontalDivider(modifier=Modifier.padding(start=10.dp,end=10.dp))
                    ListItem(


                        {
                            Row(
                                modifier = Modifier.fillMaxSize(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    "Change app icon",
                                    modifier = Modifier.padding(),
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )


                            }

                        },
                        modifier=Modifier.clickable { navHostController.navigate(Destinations.appiconchange.route) }

                    )
                    HorizontalDivider(modifier=Modifier.padding(start=10.dp,end=10.dp))
                    ListItem(

                        {
                            Row(
                                modifier = Modifier.fillMaxSize(),
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    "Show scroll to top",
                                    modifier = Modifier.padding(top = 12.dp),
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(Modifier.weight(1f))
                                Switch(checked = showtop, onCheckedChange = {
                                    if(ishapticenabled) {
                                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                    }

                                    showtop = it
                                    editor.putBoolean("showtop", it)
                                    editor.apply()
                                }, modifier = Modifier.padding(end = 5.dp))
                            }
                        }
                        ,
                        modifier=Modifier.clickable {
                            if(ishapticenabled) {
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            }
                            showtop= !showtop
                            editor.putBoolean("showtop", showtop)
                            editor.apply()



                        }
                    )

                    HorizontalDivider(modifier=Modifier.padding(start=10.dp,end=10.dp))
                    ListItem(

                        {
                            Row(
                                modifier = Modifier.fillMaxSize(),
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    "Feed View",
                                    modifier = Modifier.padding(top = 12.dp),
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(Modifier.weight(1f))
                                TextButton(onClick = {showDropdown=true}, contentPadding = PaddingValues(3.dp)){

                                    selecteditem?.let { it1 -> Text(it1) }
                                    Icon(Icons.Filled.ArrowDropDown, contentDescription = null)
                                    MaterialTheme(shapes = MaterialTheme.shapes.copy(extraSmall = RoundedCornerShape(16.dp))) {
                                        DropdownMenu(
                                            expanded = showDropdown,

                                            onDismissRequest = { showDropdown = false },



                                            ) {
                                            DropdownMenuItem(
                                                text = {
                                                    Text(
                                                        "Compact",
                                                        textAlign = TextAlign.Center
                                                    )
                                                },
                                                onClick = {
                                                    showDropdown=false
                                                    editor.putString("selectedview","Compact");
                                                    editor.apply()
                                                    editor.putBoolean("showimages",true)
                                                    editor.apply()
                                                    selecteditem="Compact"

                                                },
                                                modifier = if (selecteditem == "Compact") {
                                                    Modifier.background(MaterialTheme.colorScheme.surfaceTint.copy(alpha = 0.3f))
                                                } else {
                                                    Modifier
                                                }





                                            )
                                            DropdownMenuItem(text = {
                                                Text(
                                                    "Text-Only",
                                                    textAlign = TextAlign.Center
                                                )
                                            }, onClick = {
                                                showDropdown=false
                                                editor.putString("selectedview","Text-Only");
                                                editor.apply()
                                                editor.putBoolean("showimages",false)
                                                editor.apply()
                                                selecteditem="Text-Only"

                                            },
                                                modifier = if (selecteditem == "Text-Only") {
                                                    Modifier.background(MaterialTheme.colorScheme.surfaceTint.copy(alpha = 0.3f))
                                                } else {
                                                    Modifier
                                                }


                                            )
                                            DropdownMenuItem(text = {
                                                Text(
                                                    "Card",
                                                    textAlign = TextAlign.Center
                                                )
                                            }, onClick = {
                                                showDropdown=false
                                                editor.putString("selectedview","Card");
                                                editor.apply()
                                                editor.putBoolean("showimages",true)
                                                editor.apply()
                                                selecteditem="Card"

                                            },
                                                modifier = if (selecteditem == "Card") {
                                                    Modifier.background(MaterialTheme.colorScheme.surfaceTint.copy(alpha = 0.3f))
                                                } else {
                                                    Modifier
                                                }

                                            )

                                        }
                                    }

                                }

                            }
                        }
                        ,
                        modifier=Modifier.clickable {
                            showDropdown=true
                        }
                    )


                    HorizontalDivider(modifier=Modifier.padding(start=10.dp,end=10.dp))
                    ListItem(

                        {
                            Row(
                                modifier = Modifier.fillMaxSize(),
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    "24-hours time format",
                                    modifier = Modifier.padding(top = 12.dp),
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(Modifier.weight(1f))
                                Switch(checked = hours24, onCheckedChange = {
                                    if(ishapticenabled) {
                                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                    }

                                    hours24 = it
                                    editor.putBoolean("24hours", it)
                                    editor.apply()
                                }, modifier = Modifier.padding(end = 5.dp))
                            }
                        },
                        modifier=Modifier.clickable {
                            if(ishapticenabled) {
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            }

                            hours24 = !hours24
                            editor.putBoolean("24hours", hours24)
                            editor.apply()

                        }

                    )
                    HorizontalDivider(modifier=Modifier.padding(start=10.dp,end=10.dp))
                    ListItem(

                        {
                            Row(
                                modifier = Modifier.fillMaxSize(),
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    "In-app browser",
                                    modifier = Modifier.padding(top = 12.dp),
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(Modifier.weight(1f))
                                Switch(checked = inappbrowser, onCheckedChange = {
                                    if(ishapticenabled) {
                                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                    }

                                    inappbrowser = it
                                    editor.putBoolean("inappbrowser", it)
                                    editor.apply()
                                }, modifier = Modifier.padding(end = 5.dp))
                            }
                        },
                        modifier=Modifier.clickable {
                            if(ishapticenabled) {
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            }

                            inappbrowser = !inappbrowser
                            editor.putBoolean("inappbrowser", inappbrowser)
                            editor.apply()

                        }
                    )
                    HorizontalDivider(modifier=Modifier.padding(start=10.dp,end=10.dp))
                    ListItem(

                        {
                            Row(
                                modifier = Modifier.fillMaxSize(),
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    "Haptic feedback",
                                    modifier = Modifier.padding(top = 12.dp),
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(Modifier.weight(1f))
                                Switch(checked = ishapticenabled, onCheckedChange = {
                                    ishapticenabled = it
                                    if(ishapticenabled) {
                                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                    }



                                    editor.putBoolean("hapticenabled", it)
                                    editor.apply()
                                }, modifier = Modifier.padding(end = 5.dp))
                            }
                        },
                        modifier=Modifier.clickable {
                            ishapticenabled = !ishapticenabled
                            if(ishapticenabled) {
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            }



                            editor.putBoolean("hapticenabled", ishapticenabled)
                            editor.apply()

                        }
                    )
                    HorizontalDivider(modifier=Modifier.padding(start=10.dp,end=10.dp))
                    ListItem(


                        {
                            Row(
                                modifier = Modifier.fillMaxSize(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    "Account",
                                    modifier = Modifier.padding(),
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )


                            }

                        },
                        modifier=Modifier.clickable {
                            navHostController.navigate(Destinations.account.route)

                        }

                    )


                    HorizontalDivider(modifier=Modifier.padding(start=10.dp,end=10.dp))
                    ListItem(


                        {
                            Row(
                                modifier = Modifier.fillMaxSize(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    "About app",
                                    modifier = Modifier.padding(),
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )


                            }

                        },
                        modifier=Modifier.clickable { navHostController.navigate(Destinations.aboutapp.route) }

                    )





                }


            }
        }

    }
}


