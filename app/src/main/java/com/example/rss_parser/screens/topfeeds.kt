package com.example.rss_parser.screens

import android.content.Context
import android.content.SharedPreferences
import android.widget.Toast
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Done
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.example.rss_parser.database.websitedata.website
import com.example.rss_parser.ui.theme.RSSparserTheme
import com.example.rss_parser.viewmodel.viewmodel
import kotlinx.coroutines.launch

@Composable

fun topfeeds(url:String,websitename:String,websiteurl:String) {
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
    val feedswebsites = viewModel.allwebsitelinks.observeAsState(initial = emptyList())
    val websites = feedswebsites.value.map { it.websitelink }



            ListItem({
                Row {
                    Column {


                        Text(text = websitename)
                        Text(text = websiteurl)
                    }
                    Spacer(modifier = Modifier.weight(1f))
                    IconButton(onClick = {
                        if (!websites.contains(url)) {
                            coroutine.launch {
                                val checkforerror = viewModel.fetchrssfeed(url)
                                if (checkforerror != null) {
                                    viewModel.insert(website(0, url))


                                } else {
                                    Toast.makeText(context, "Error occurred", Toast.LENGTH_SHORT)
                                        .show()
                                }
                            }


                        } else {
                            viewModel.delete(url)
                        }
                    }) {
                        if (websites.contains(url)) {
                            Icon(imageVector = Icons.Outlined.Done, contentDescription = "")

                        } else {
                            Icon(
                                imageVector = Icons.Outlined.Add,
                                contentDescription = ""
                            )
                        }

                    }
                }
            })

        }

