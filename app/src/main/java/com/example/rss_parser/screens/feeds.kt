package com.example.rss_parser.screens

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.rss_parser.Navigation.Destinations
import com.example.rss_parser.R
import com.example.rss_parser.check_network.Connectionstatus
import com.example.rss_parser.check_network.connectivityState
import com.example.rss_parser.supabase.client.supabaseclient
import com.example.rss_parser.viewmodel.viewmodel
import io.github.jan.supabase.exceptions.HttpRequestException
import io.github.jan.supabase.exceptions.RestException
import io.github.jan.supabase.postgrest.from
import io.ktor.client.plugins.HttpRequestTimeoutException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

@OptIn(ExperimentalMaterial3Api::class, ExperimentalCoroutinesApi::class)
@Composable

fun feeds(navHostController: NavHostController) {



    val context = LocalContext.current
    val haptic= LocalHapticFeedback.current
    val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("showimages", Context.MODE_PRIVATE)
    var ishapticenabled by remember{
        mutableStateOf(sharedPreferences.getBoolean("hapticenabled",true))
    }

    val viewModel = viewModel<viewmodel>(
        factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return viewmodel(
                    context
                ) as T
            }
        }

    )

    val iscontentready by remember {
        mutableStateOf(false)
    }
    val coroutine= rememberCoroutineScope()
    val connection by connectivityState()
    val isdeleted by remember {
        mutableStateOf(false)
    }

    val isConnected = connection === Connectionstatus.Available
    val urls by viewModel.allwebsites.observeAsState()












        Scaffold(
            topBar = {
                TopAppBar(title = { Text(text = "Feed Websites") }, navigationIcon = {
                    IconButton(
                        onClick = { navHostController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Default.ArrowBack,
                            contentDescription = null
                        )

                    }
                })
            },
            floatingActionButton = { FloatingActionButton(onClick = {
                if(ishapticenabled) {
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                }
                navHostController.navigate(Destinations.addnewfeed.route) }) {
                Icon(imageVector = Icons.Outlined.Add, contentDescription = "add feed")

            }}
        ) {



                    if (urls?.isEmpty() == true) {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text("No Feed Websites Found")


                        }
                    }


                    LazyColumn(modifier = Modifier.padding(it)) {
                        urls?.size?.let { it1 ->
                            items(it1) {
                                var loading by remember {
                                    mutableStateOf(false)
                                }

                                ListItem(

                                    {

                                        Row {
                                            Text(
                                                text = urls!![it].websitelink,
                                                modifier = Modifier.padding(top = 12.dp).weight(1f),
                                                overflow = TextOverflow.Ellipsis,

                                                maxLines = 1
                                            )

                                            IconButton(onClick = {


                                                loading = true
                                                coroutine.launch {

                                                    try {



                                                        withContext(Dispatchers.IO) {
                                                            supabaseclient.client.from("website")
                                                                .delete {
                                                                    filter {
                                                                        eq("websitelink", urls!![it].websitelink)
                                                                    }
                                                                }
                                                        }

                                                        viewModel.delete(urls!![it].websitelink)
                                                        viewModel.websitedelete(urls!![it].websitelink)













                                                        loading = false
                                                        if(ishapticenabled) {
                                                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                                        }


                                                    } catch (e: Exception) {
                                                        loading = false
                                                        when(e){
                                                            is RestException->{
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

                                                    viewModel.delete(urls!![it].websitelink)
                                                }



                                            }) {
                                                if (loading) {
                                                    CircularProgressIndicator( strokeCap = StrokeCap.Round)
                                                }

                                                Icon(
                                                    imageVector = Icons.Outlined.Delete,
                                                    contentDescription = "Delete Website"
                                                )

                                            }
                                        }
                                    },

                                    )
                            }
                        }
                    }


        }

    }
