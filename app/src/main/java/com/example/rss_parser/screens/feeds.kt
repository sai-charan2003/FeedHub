package com.example.rss_parser.screens

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.compose.foundation.isSystemInDarkTheme
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
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.rss_parser.Navigation.Destinations
import com.example.rss_parser.ui.theme.RSSparserTheme
import com.example.rss_parser.viewmodel.viewmodel
import com.prof18.rssparser.RssParser

@OptIn(ExperimentalMaterial3Api::class)
@Composable

fun feeds(navHostController: NavHostController) {
    val context = LocalContext.current
    val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("showimages", Context.MODE_PRIVATE)

    val viewModel = viewModel<viewmodel>(
        factory = object : ViewModelProvider.Factory {
            override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                return viewmodel(
                    context
                ) as T
            }
        }

    )
    val feedswebsites = viewModel.allwebsitelinks.observeAsState(initial = emptyList())
    val websites = feedswebsites.value.map { it.websitelink }

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
            floatingActionButton = { FloatingActionButton(onClick = { navHostController.navigate(Destinations.addnewfeed.route) }) {
                Icon(imageVector = Icons.Outlined.Add, contentDescription = "add feed")

            }}
        ) {
            if (feedswebsites.value.size == 0) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text("No Feed Websites Found")
                    Button(onClick = { navHostController.navigate(Destinations.addnewfeed.route) }) {
                        Text(text = "Add Feed")

                    }

                }
            }


            LazyColumn(modifier = Modifier.padding(it)) {
                items(feedswebsites.value.size) {

                    ListItem(
                        {
                            Row {
                                Text(text = websites[it], modifier = Modifier.padding(top = 12.dp))
                                Spacer(modifier = Modifier.weight(1f))
                                IconButton(onClick = { viewModel.delete(feedswebsites.value[it].websitelink) }) {
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
