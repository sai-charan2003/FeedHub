package com.example.rss_parser.screens

import android.content.Context
import android.content.SharedPreferences
import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Done
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.rss_parser.supabase.client.supabaseclient
import com.example.rss_parser.viewmodel.viewmodel
import com.example.rss_parser.supabase.database.website_supabase
import io.github.jan.supabase.gotrue.auth
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.launch

@Composable

fun topfeeds(rssurl:String,websitename:String,websiteurl:String) {
    val coroutine = rememberCoroutineScope()

    val context = LocalContext.current
    val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("showimages", Context.MODE_PRIVATE)

    val viewModel = viewModel<viewmodel>(
        factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return viewmodel(
                    context
                ) as T
            }
        }

    )


    var feedlink by remember {
        mutableStateOf("")

    }
    var isloading by remember{
        mutableStateOf(false)
    }
    val url by viewModel.websiteUrls.observeAsState()
    LaunchedEffect(key1=url){
        viewModel.getwebsiteurlfromdb()


    }


            ListItem({
                Row {
                    Column {


                        Text(text = websitename)
                        Text(text = websiteurl)
                    }
                    Spacer(modifier = Modifier.weight(1f))
                    IconButton(onClick = {
                        coroutine.launch {



                            if (url?.contains(rssurl)==false) {
                                isloading=true
                                coroutine.launch {

                                    val checkforerror = viewModel.fetchrssfeed(rssurl)
                                    if (checkforerror != null) {
                                        val user = supabaseclient.client.auth.retrieveUserForCurrentSession(updateSession = true)

                                        supabaseclient.client.from("website").insert(
                                            website_supabase(
                                                id = null,
                                                websitelink = rssurl,
                                                email = user.email
                                            )
                                        )
                                        viewModel.getwebsiteurlfromdb()
                                        isloading=false


                                    } else {
                                        isloading=false
                                        Toast.makeText(
                                            context,
                                            "Error occurred",
                                            Toast.LENGTH_SHORT
                                        )
                                            .show()
                                    }
                                }
                            } else {
                                isloading=true
                                supabaseclient.client.from("website").delete {
                                    filter {
                                        eq("websitelink", rssurl)
                                    }
                                }
                                viewModel.getwebsiteurlfromdb()
                                isloading=false
                            }
                        }
                    }) {
                        val websites = viewModel.websiterb.mapNotNull { it.websitelink }

                        if (url?.contains(rssurl) == true) {
                            if(isloading){
                                CircularProgressIndicator()

                            }

                            Icon(imageVector =


                            Icons.Outlined.Done, contentDescription = "")

                        } else {if(isloading){
                            CircularProgressIndicator()

                        }
                            Icon(
                                imageVector = Icons.Outlined.Add,
                                contentDescription = ""
                            )
                        }

                    }
                }

            })

        }

