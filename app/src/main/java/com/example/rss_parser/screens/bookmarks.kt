package com.example.rss_parser.screens

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.rss_parser.inappbrowser.openTab
import com.example.rss_parser.ui.theme.RSSparserTheme
import com.example.rss_parser.viewmodel.viewmodel

@OptIn(ExperimentalMaterial3Api::class)
@Composable

fun booksmarks(navHostController: NavHostController) {



    val context = LocalContext.current
    val uriHandler = LocalUriHandler.current
    val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("showimages", Context.MODE_PRIVATE)
    val showimages by remember {
        mutableStateOf(sharedPreferences.getBoolean("showimages", false))
    }
    val Scroll = TopAppBarDefaults.pinnedScrollBehavior()
    val viewModel = viewModel<viewmodel>(
        factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return viewmodel(
                    context
                ) as T
            }
        }
    )
    val bookmarks by viewModel.allbookmarks.observeAsState(initial = emptyList())
    RSSparserTheme() {


        Scaffold(modifier = Modifier
            .fillMaxSize()
            .nestedScroll(Scroll.nestedScrollConnection),
            topBar = {
                TopAppBar(title = { Text(text = "Bookmarks") }, scrollBehavior = Scroll, navigationIcon = {
                    IconButton(
                        onClick = { navHostController.popBackStack()}) {
                        Icon(imageVector = Icons.AutoMirrored.Outlined.ArrowBack,contentDescription = null)

                    }
                })
            }


        ) {
            if (bookmarks.isEmpty()) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text("No Bookmarks added")

                }
            } else {


                LazyColumn(modifier = Modifier.padding(it)) {
                    items(bookmarks.size) {
                        ListItem(
                            {
                                val sendIntent: Intent = Intent().apply {
                                    action = Intent.ACTION_SEND
                                    putExtra(Intent.EXTRA_TEXT, bookmarks[it].websitelink)
                                    type = "text/plain"
                                }
                                Row(
                                    horizontalArrangement = Arrangement.Center,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    if (showimages) {
                                        AsyncImage(
                                            model = bookmarks[it].images,
                                            contentDescription = "null",
                                            modifier = Modifier
                                                .height(100.dp)
                                                .width(100.dp)
                                                .padding(10.dp)
                                                .clip(RoundedCornerShape(5.dp)),
                                            alignment = Alignment.CenterEnd,
                                            contentScale = ContentScale.Fit
                                        )
                                    }

                                    Column() {
                                        Text(
                                            text = bookmarks[it].websitelink.substringAfter("https://")
                                                .substringAfter("www.").substringBefore(".com"),
                                            modifier = Modifier.padding(
                                                start = 10.dp,
                                                end = 10.dp,
                                                bottom = 10.dp
                                            ),
                                            style = MaterialTheme.typography.labelLarge,
                                            fontWeight = FontWeight.Light,
                                            textAlign = TextAlign.Justify
                                        )

                                        Text(
                                            text = bookmarks[it].title,
                                            modifier = Modifier.padding(
                                                start = 10.dp,
                                                end = 10.dp,
                                                bottom = 10.dp
                                            ),
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold,
                                            textAlign = TextAlign.Justify
                                        )
                                        Row {

                                            Spacer(Modifier.weight(1f))
                                            IconButton(onClick = {
                                                viewModel.deletebookmark(bookmarks[it].websitelink)
                                            }) {

                                                Icon(

                                                    imageVector = Icons.Filled.Bookmark,
                                                    contentDescription = null,
                                                    modifier = Modifier.size(15.dp)
                                                )

                                            }



                                            IconButton(onClick = {
                                                val shareIntent =
                                                    Intent.createChooser(sendIntent, null)
                                                context.startActivity(shareIntent)


                                            }) {
                                                Icon(
                                                    imageVector = Icons.Outlined.Share,
                                                    contentDescription = null,
                                                    modifier = Modifier.size(15.dp)
                                                )

                                            }

                                        }
                                    }
                                }

                            },
                            modifier = Modifier.clickable {
                                if(sharedPreferences.getBoolean("inappbrowser",false)){
                                    openTab(context,bookmarks[it].websitelink)
                                }
                                else{
                                    uriHandler.openUri(bookmarks[it].websitelink)
                                }
                            }
                        )
                        HorizontalDivider(modifier=Modifier.padding(start=10.dp,end=10.dp))
                    }
                }
            }
        }
    }
}