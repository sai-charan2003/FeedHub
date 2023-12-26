package com.example.rss_parser.screens

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.Resources.Theme
import android.net.Uri
import android.util.Log
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.browser.customtabs.CustomTabsIntent
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember

import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.rss_parser.DateandTimeConverter.convertTo12HourFormatWithDayAndMonth


import com.example.rss_parser.DateandTimeConverter.convertToMillisecondsEpoch
import com.example.rss_parser.DateandTimeConverter.formatDateAndLocalTime
import com.example.rss_parser.Navigation.Destinations
import com.example.rss_parser.R
import com.example.rss_parser.rssdata.RssData
import com.example.rss_parser.database.bookmarkdatabase.bookmarks
import com.example.rss_parser.inappbrowser.openTab
import com.example.rss_parser.ui.theme.RSSparserTheme
import com.example.rss_parser.viewmodel.viewmodel
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun homescreen(navHostController: NavHostController) {
    val context = LocalContext.current
    val viewModel = viewModel<viewmodel>(
        factory = object : ViewModelProvider.Factory {
            override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                return viewmodel(
                    context
                ) as T
            }
        }
    )

    val urls by viewModel.allwebsitelinks.observeAsState()
    val bookmarkdata by viewModel.allbookmarks.observeAsState()
    val bookmarklink = bookmarkdata?.map { it.websitelink }
    val Scroll = TopAppBarDefaults.pinnedScrollBehavior()
    var showdropdownmenu by remember { mutableStateOf(false) }
    val isloading by viewModel.isLoading.collectAsState()
    val swiperefresh = rememberSwipeRefreshState(isRefreshing = isloading)


    val rssData by viewModel.rssData.observeAsState(
        RssData(
            emptyList(), emptyList(), emptyList(), emptyList(),

            )
    )


    val observedLinks = rssData.links
    val observedTitles = rssData.titles
    val observedDates = rssData.dates
    val observedImages = rssData.images
    var sortedData = if (observedLinks.isNotEmpty()) {
        observedDates.mapIndexed { index, dateString ->
            Pair(index, formatDateAndLocalTime(dateString))
        }.sortedByDescending { (_, date) -> date }
            .map { (index, _) -> index }
            .let { sortedIndices ->
                sortedIndices.map { index ->
                    Triple(
                        observedLinks[index],
                        Pair(observedTitles[index], observedDates[index]),
                        observedImages[index]
                    )
                }
            }
    } else {
        emptyList()
    }.sortedByDescending { (_, pair) ->
        val (_, date) = pair
        convertToMillisecondsEpoch(date) // Get milliseconds epoch value
    }


    var isurlsempty by remember {
        mutableStateOf(false)
    }
    if (urls?.isEmpty() == true) {
        isurlsempty = true
    }
    val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("showimages", Context.MODE_PRIVATE)
    val showimages by remember {
        mutableStateOf(sharedPreferences.getBoolean("showimages", false))
    }
    val darksharedPreferences: SharedPreferences =
        context.getSharedPreferences("darkmode", Context.MODE_PRIVATE)
    val dark = isSystemInDarkTheme()
    val darkmode by remember {
        mutableStateOf(darksharedPreferences.getBoolean("darkmode", dark))
    }
    if (urls?.isNotEmpty() == true) {
        isurlsempty = false
    }
    var iscontentloaded by remember {
        mutableStateOf(false)
    }

    LaunchedEffect(key1 = urls) {


        urls?.let { urlList ->
            urlList.map { it.websitelink }.let { viewModel.getData(it) }
        }

    }
    val uriHandler = LocalUriHandler.current
    if (observedLinks.isNotEmpty() || isurlsempty) {
        iscontentloaded = true
    }

    RSSparserTheme() {
        Surface {


            Scaffold(
                modifier = Modifier
                    .fillMaxSize()
                    .nestedScroll(Scroll.nestedScrollConnection),
                topBar = {
                    TopAppBar(
                        title = { Text("My Feeds") },
                        scrollBehavior = Scroll,
                        actions = {
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


                SwipeRefresh(state = swiperefresh, onRefresh = {
                    sortedData = emptyList()

                    urls?.let { urlList ->
                        urlList.mapNotNull { it.websitelink }.let { viewModel.getData(it) }
                    }


                }, modifier = Modifier.padding(it)) {

                    if (iscontentloaded) {
                        if (isurlsempty) {

                            Column(
                                modifier = Modifier.fillMaxSize(),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Text("No feeds")
                                Button(onClick = { navHostController.navigate(Destinations.addnewfeed.route) }) {
                                    Text(text = "Add feed")

                                }
                            }
                        } else {


                            LazyColumn(modifier = Modifier.padding()) {
                                items(observedLinks.size) {
                                    ListItem(
                                        {
                                            val sendIntent: Intent = Intent().apply {
                                                action = Intent.ACTION_SEND
                                                putExtra(Intent.EXTRA_TEXT, observedLinks[it])
                                                type = "text/plain"
                                            }
                                            Row(
                                                horizontalArrangement = Arrangement.Center,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                if (showimages) {
                                                    AsyncImage(
                                                        model = sortedData[it].third,
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
                                                        text = sortedData[it].first.substringAfter("https://")
                                                            .substringAfter("www.")
                                                            .substringBefore(".com")
                                                            .substringBefore(".in")
                                                            .substringBefore(".edu"),
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
                                                        text = sortedData[it].second.first,
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
                                                        Text(
                                                            text = if(sharedPreferences.getBoolean("24hours",true)){
                                                                formatDateAndLocalTime(sortedData[it].second.second)
                                                                                                                   }
                                                            else{
                                                                convertTo12HourFormatWithDayAndMonth(sortedData[it].second.second)
                                                            }

                                                            ,
                                                            modifier = Modifier.padding(
                                                                top = 15.dp,
                                                                start = 10.dp,
                                                                end = 10.dp,
                                                                bottom = 10.dp
                                                            ),
                                                            style = MaterialTheme.typography.labelLarge,
                                                            fontWeight = FontWeight.Light,
                                                            textAlign = TextAlign.Justify
                                                        )
                                                        Spacer(Modifier.weight(1f))
                                                        IconButton(onClick = {
                                                            if (bookmarklink != null) {
                                                                if (bookmarklink.contains(sortedData[it].first)) {


                                                                    viewModel.deletebookmark(
                                                                        sortedData[it].first

                                                                    )
                                                                } else {
                                                                    viewModel.insertbookmark(
                                                                        bookmarks(
                                                                            0,
                                                                            sortedData[it].first,
                                                                            sortedData[it].second.first,
                                                                            sortedData[it].third
                                                                        )
                                                                    )
                                                                }
                                                            }
                                                        }) {
                                                            if (bookmarklink != null) {
                                                                if (bookmarklink.contains(sortedData[it].first)) {

                                                                    Icon(

                                                                        imageVector = Icons.Filled.Bookmark,
                                                                        contentDescription = null,
                                                                        modifier = Modifier.size(15.dp)
                                                                    )
                                                                } else {
                                                                    Icon(

                                                                        imageVector = Icons.Outlined.BookmarkBorder,
                                                                        contentDescription = null,
                                                                        modifier = Modifier.size(15.dp)
                                                                    )

                                                                }
                                                            }

                                                        }
                                                        IconButton(onClick = {
                                                            val shareIntent =
                                                                Intent.createChooser(
                                                                    sendIntent,
                                                                    null
                                                                )
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
                                            if(sharedPreferences.getBoolean("inappbrowser",false)) {
                                                openTab(context, sortedData[it].first)
                                            }
                                            else{
                                                uriHandler.openUri(
                                                    sortedData[it].first
                                                )
                                            }


                                        }
                                    )


                                    HorizontalDivider(modifier=Modifier.padding(start=10.dp,end=10.dp))
                                }
                            }
                        }
                    } else {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {

                        }

                    }
                }
            }
        }
    }
}





