package com.example.rss_parser.screens

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
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
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat.startActivity
import androidx.navigation.NavHostController
import com.example.rss_parser.Navigation.Destinations
import com.example.rss_parser.supabase.client.supabaseclient
import com.example.rss_parser.ui.theme.RSSparserTheme
import io.github.jan.supabase.exceptions.HttpRequestException
import io.github.jan.supabase.exceptions.RestException
import io.github.jan.supabase.gotrue.SignOutScope
import io.github.jan.supabase.gotrue.auth
import io.ktor.client.plugins.HttpRequestTimeoutException
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable

fun settings(navHostController: NavHostController) {
    val context = LocalContext.current
    val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("showimages", Context.MODE_PRIVATE)

    val editor = sharedPreferences.edit()
    var showimages by remember {
        mutableStateOf(sharedPreferences.getBoolean("showimages", false))
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
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    "Show Images",
                                    modifier = Modifier.padding(top = 12.dp),
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(Modifier.weight(1f))
                                Switch(checked = showimages, onCheckedChange = {

                                    showimages = it
                                    editor.putBoolean("showimages", it)
                                    editor.apply()
                                }, modifier = Modifier.padding(end = 5.dp))
                            }
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

                                    hours24 = it
                                    editor.putBoolean("24hours", it)
                                    editor.apply()
                                }, modifier = Modifier.padding(end = 5.dp))
                            }
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

                                    inappbrowser = it
                                    editor.putBoolean("inappbrowser", it)
                                    editor.apply()
                                }, modifier = Modifier.padding(end = 5.dp))
                            }
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
                                    "Log Out",
                                    modifier = Modifier.padding(),
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )


                            }

                        },
                        modifier=Modifier.clickable { coroutinescope.launch {
                            try {
                                supabaseclient.client.auth.signOut(SignOutScope.GLOBAL)
                                editor.putBoolean("islog", false)
                                editor.apply()
                                navHostController.popBackStack()
                                navHostController.navigate(Destinations.enterscreen.route){
                                    popUpTo(Destinations.home.route){
                                        inclusive=true
                                    }
                                }
                            } catch (e: Exception) {
                                when(e){
                                    is RestException ->{
                                        val error = e.message?.substringBefore("URL")
                                        Toast.makeText(context, "$error", Toast.LENGTH_SHORT).show()
                                    }
                                    is HttpRequestTimeoutException ->{
                                        val error = e.message?.substringBefore("URL")
                                        Toast.makeText(context, "$error", Toast.LENGTH_SHORT).show()
                                    }
                                    is HttpRequestException ->{
                                        val error = e.message?.substringBefore("URL")
                                        Toast.makeText(context, "$error", Toast.LENGTH_SHORT).show()
                                    }
                                }


                            }
                        } }

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
