package com.example.rss_parser.screens.Views



import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.VibrationEffect
import android.os.Vibrator
import android.util.Log
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer


import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.outlined.ArrowUpward
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.InputChip
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable

import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.rss_parser.BuildConfig
import com.example.rss_parser.DateandTimeConverter.convertTo12HourFormatWithDayAndMonth


import com.example.rss_parser.DateandTimeConverter.convertToMillisecondsEpoch
import com.example.rss_parser.DateandTimeConverter.formatDateAndLocalTime
import com.example.rss_parser.Navigation.Destinations
import com.example.rss_parser.R
import com.example.rss_parser.supabase.database.bookmarkdatabase
import com.example.rss_parser.check_network.Connectionstatus
import com.example.rss_parser.check_network.connectivityState
import com.example.rss_parser.database.feeddatabase.feeds

import com.example.rss_parser.inappbrowser.openTab
import com.example.rss_parser.supabase.client.supabaseclient
import com.example.rss_parser.ui.theme.RSSparserTheme
import com.example.rss_parser.viewmodel.viewmodel

import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.google.ai.client.generativeai.GenerativeModel

import com.meetup.twain.MarkdownText
import io.github.jan.supabase.exceptions.HttpRequestException
import io.github.jan.supabase.exceptions.RestException
import io.github.jan.supabase.gotrue.auth
import io.github.jan.supabase.postgrest.from
import io.ktor.client.plugins.HttpRequestTimeoutException
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class, ExperimentalCoroutinesApi::class,
    ExperimentalFoundationApi::class, ExperimentalMaterialApi::class
)
@Composable
fun noncompactview(it:PaddingValues,navHostController: NavHostController) {
    val connection by connectivityState()




    val isConnected = connection === Connectionstatus.Available
    val context = LocalContext.current
    val coroutinescope = rememberCoroutineScope()
    val viewModel = viewModel<viewmodel>(
        factory = object : ViewModelProvider.Factory {
            override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                return viewmodel(
                    context
                ) as T
            }
        }
    )

    val timings: LongArray = longArrayOf(100, 100, 100, 100, 100, 100,)
    val amplitudes: IntArray = intArrayOf(23, 41, 65, 103, 160, 255,)
    val repeatIndex = -1 // Do not repeat.

    val listState = rememberLazyListState()



    val generativeModel = GenerativeModel(
        modelName = "gemini-pro",
        apiKey = BuildConfig.apiKey
    )
    var search by remember {
        mutableStateOf("")
    }
    var summary by remember {
        mutableStateOf("")
    }
    val showScrollToTop by remember { derivedStateOf { listState.firstVisibleItemIndex > 2} }




    val Scroll = TopAppBarDefaults.pinnedScrollBehavior()
    var showdropdownmenu by remember { mutableStateOf(false) }
    var homeloading by remember {
        mutableStateOf(true)
    }
    var aisummarypage by remember {
        mutableStateOf(false)
    }

    var finalloading by remember {
        mutableStateOf(true)
    }

    var selectedCategory by rememberSaveable { mutableStateOf<String?>(null) }
    val haptic = LocalHapticFeedback.current

    val isloading by viewModel.isLoading.collectAsState()
    finalloading = isloading || homeloading == true
    val swiperefresh = rememberSwipeRefreshState(isRefreshing = isloading)




    val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("showimages", Context.MODE_PRIVATE)
    val showimages by remember {
        mutableStateOf(sharedPreferences.getBoolean("showimages", true))
    }
    var ishapticenabled by remember {
        mutableStateOf(sharedPreferences.getBoolean("hapticenabled", true))
    }
    val showtop by remember {
        mutableStateOf(sharedPreferences.getBoolean("showtop", true))
    }





    val dark = isSystemInDarkTheme()

    var iscontentloaded by remember {
        mutableStateOf(false)
    }
    var active by remember {
        mutableStateOf(false)
    }
    val vibrator = LocalContext.current.getSystemService(Vibrator::class.java)

    val editor = sharedPreferences.edit()
    var dataempty by remember {
        mutableStateOf(false)
    }
    var selected by remember { mutableStateOf(false) }




    val uriHandler = LocalUriHandler.current
    RSSparserTheme {
        Surface() {




            val urls by viewModel.allwebsites.observeAsState()
            val dummyurls by viewModel.websiteUrls.observeAsState()
            Log.d("TAG", "homescreen: $dummyurls")
            val feedsdata by viewModel.allfeeds.observeAsState()

            var sortedFeedsData=
                feedsdata?.let { feeds ->
                    if (feeds.isNotEmpty()) {
                        feeds.mapIndexed { index, feed ->
                            Triple(index, feed, convertToMillisecondsEpoch(feed.data))
                        }.sortedByDescending { (_, _, dateInMillis) -> dateInMillis }
                            .map { (_, feed, _) -> feed }
                    } else {
                        emptyList()
                    }
                }

            if(selectedCategory!=null){
                sortedFeedsData=sortedFeedsData?.filter {
                    selectedCategory!!.
                    substringAfter("https://").
                    substringAfter("www.").
                    substringBefore(".com")==
                            it.feedlink.substringAfter("https://").
                            substringAfter("www.").
                            substringBefore(".com")

                }



            }




            val bookmarkdata by viewModel.bookmarkdata.observeAsState()
            val bookmarklink = bookmarkdata?.map { it.websitelink }


            SwipeRefresh(state = swiperefresh, onRefresh = {
                if (ishapticenabled) {
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)

                }
                viewModel.getwebsiteurlfromdb()


            }, modifier = Modifier.padding(it)) {
                LaunchedEffect(urls) {


                    launch {
                        if (isConnected) {
                            try {
                                supabaseclient.client.auth.currentAccessTokenOrNull()
                            } catch (e: Exception) {
                                when (e) {
                                    is RestException -> {
                                        val error = e.message?.substringBefore("URL")
                                        Toast.makeText(context, "$error", Toast.LENGTH_SHORT).show()
                                    }

                                    is HttpRequestTimeoutException -> {
                                        val error = e.message?.substringBefore("URL")
                                        Toast.makeText(context, "$error", Toast.LENGTH_SHORT).show()
                                    }

                                    is HttpRequestException -> {
                                        val error = e.message?.substringBefore("URL")
                                        Toast.makeText(context, "$error", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            }
                            try{
                                viewModel.getbookmarksdata()
                                viewModel.getwebsiteurlfromdb()
                            }
                            catch (e:Exception){


                            }










                        }
                    }


                }
                if (isloading) {
                    Column(modifier = Modifier.fillMaxSize()) {

                    }
                }

                if (feedsdata?.isEmpty() == true) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text("No feeds")
                        Button(onClick = { navHostController.navigate(Destinations.addnewfeed.route) }) {
                            Text(text = "Add feed")

                        }
                        homeloading = false

                    }
                } else {


                    Box(modifier = Modifier
                        .fillMaxSize()
                    ) {


                        LazyColumn(state = listState) {

                            item {
                                LazyRow {
                                    urls?.let { it1 ->
                                        items(it1.size) {
                                            val url = urls!![it]
                                            var isSelected =
                                                (url.websitelink == selectedCategory)
                                            InputChip(
                                                selected = isSelected,
                                                onClick = {
                                                    if (isSelected) {
                                                        selectedCategory = null
                                                    } else {
                                                        selectedCategory =
                                                            urls!![it].websitelink
                                                    }

                                                },
                                                label = {
                                                    urls!![it].websitelink.substringAfter(
                                                        "https://"
                                                    )
                                                        ?.substringAfter("www.")
                                                        ?.substringBefore(".com")
                                                        ?.substringBefore(".in")
                                                        ?.substringBefore(".edu")
                                                        ?.let { it2 -> Text(it2) }
                                                },
                                                modifier = Modifier.padding(start = 10.dp)

                                            )


                                        }
                                    }
                                }
                            }





                            sortedFeedsData?.let { it1 ->
                                items(it1.size) {list->
                                    var loading by remember {
                                        mutableStateOf(false)

                                    }
                                    homeloading = false
                                    ListItem(

                                        {
                                            val sendIntent: Intent =
                                                Intent().apply {
                                                    action = Intent.ACTION_SEND
                                                    putExtra(
                                                        Intent.EXTRA_TEXT,
                                                        sortedFeedsData?.get(list)?.feedlink
                                                    )
                                                    type = "text/plain"
                                                }
                                            Column(
                                                horizontalAlignment = Alignment.CenterHorizontally

                                            ) {
                                                if (showimages) {
                                                    Box(){
                                                        AsyncImage(
                                                            model = sortedFeedsData?.get(
                                                                list
                                                            )?.imageurl,
                                                            contentDescription = "null",
                                                            modifier = Modifier
                                                                .shadow(100.dp)
                                                                .clip(RoundedCornerShape(20.dp))
                                                                .padding(bottom = 10.dp)

                                                            ,

                                                            contentScale = ContentScale.Fit,
                                                            colorFilter =
                                                            if (sortedFeedsData?.get(
                                                                    list
                                                                )?.opened == "true"
                                                            ) {


                                                                ColorFilter.colorMatrix(
                                                                    ColorMatrix().apply {
                                                                        setToScale(
                                                                            0.5f,
                                                                            0.5f,
                                                                            0.5f,
                                                                            1f
                                                                        )
                                                                    })
                                                            } else {
                                                                null
                                                            }
                                                        )

                                                        Surface(shape = RoundedCornerShape(33.dp),
                                                            modifier=Modifier
                                                                .align(Alignment.BottomStart)
                                                                .padding(bottom=20.dp,start=5.dp),
                                                            color =
                                                            if (sortedFeedsData?.get(
                                                                    list
                                                                )?.opened == "true"
                                                            ) {


                                                                MaterialTheme.colorScheme.surface.copy(alpha=0.5f)
                                                            } else {
                                                                MaterialTheme.colorScheme.surface.copy(alpha=0.9f)


                                                            },



                                                        ) {
                                                            sortedFeedsData?.get(list)?.feedlink?.substringAfter(
                                                                "https://"
                                                            )
                                                                ?.substringAfter("www.")
                                                                ?.substringBefore(".com")
                                                                ?.substringBefore(".in")
                                                                ?.substringBefore(".edu")
                                                                ?.let { it1 ->
                                                                    Text(
                                                                        text = it1,

                                                                        style = MaterialTheme.typography.labelLarge,
                                                                        color = MaterialTheme.colorScheme.inverseSurface,

                                                                        textAlign = TextAlign.Justify,
                                                                        modifier=Modifier.padding(top=5.dp,bottom=5.dp,end=10.dp,start=10.dp),
                                                                        fontWeight =
                                                                        if (sortedFeedsData?.get(
                                                                                list
                                                                            )?.opened == "true"
                                                                        ) {


                                                                            FontWeight.W100
                                                                        } else {
                                                                            FontWeight.Bold

                                                                        },
                                                                    )
                                                                }
                                                        }

                                                    }

                                                }

                                                Column {
                                                    if(!showimages){
                                                        sortedFeedsData?.get(list)?.feedlink?.substringAfter(
                                                            "https://"
                                                        )
                                                            ?.substringAfter("www.")
                                                            ?.substringBefore(".com")
                                                            ?.substringBefore(".in")
                                                            ?.substringBefore(".edu")
                                                            ?.let { it1 ->
                                                                Text(
                                                                    text = it1,
                                                                    modifier = Modifier.padding(
                                                                        start = 10.dp,
                                                                        end = 10.dp,
                                                                        bottom = 10.dp
                                                                    ),
                                                                    style = MaterialTheme.typography.labelLarge,
                                                                    fontWeight = FontWeight.Light,
                                                                    textAlign = TextAlign.Justify
                                                                )
                                                            }
                                                    }

                                                    sortedFeedsData?.get(list)
                                                        ?.let { it1 ->
                                                            Text(
                                                                text = it1.feedtitle,
                                                                modifier = Modifier.padding(
                                                                    start = 10.dp,
                                                                    end = 10.dp,
                                                                    bottom = 10.dp
                                                                ),
                                                                style = MaterialTheme.typography.titleMedium,
                                                                fontWeight =
                                                                if (sortedFeedsData?.get(
                                                                        list
                                                                    )?.opened == "true"
                                                                ) {


                                                                    FontWeight.W100
                                                                } else {
                                                                    FontWeight.Bold

                                                                },
                                                                textAlign = TextAlign.Justify
                                                            )
                                                        }
                                                    Row {
                                                        (if (sharedPreferences.getBoolean(
                                                                "24hours",
                                                                true
                                                            )
                                                        ) {
                                                            sortedFeedsData?.get(list)
                                                                ?.let { it2 ->
                                                                    formatDateAndLocalTime(
                                                                        it2.data
                                                                    )
                                                                }
                                                        } else {
                                                            sortedFeedsData?.get(list)
                                                                ?.let { it2 ->
                                                                    convertTo12HourFormatWithDayAndMonth(
                                                                        it2.data
                                                                    )
                                                                }
                                                        })?.let { it3 ->
                                                            Text(
                                                                text = it3,
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
                                                        }
                                                        Spacer(
                                                            Modifier.weight(
                                                                1f
                                                            )
                                                        )
                                                        IconButton(onClick = {

                                                            if (bookmarklink != null) {
                                                                loading = true
                                                                if (sortedFeedsData?.get(
                                                                        list
                                                                    )
                                                                        ?.let { it2 ->
                                                                            bookmarklink.contains(
                                                                                it2.feedlink
                                                                            )
                                                                        } == true
                                                                ) {


                                                                    coroutinescope.launch {
                                                                        supabaseclient.client.from(
                                                                            "bookmarks"
                                                                        )
                                                                            .delete {
                                                                                filter {
                                                                                    sortedFeedsData[list]?.let { it2 ->
                                                                                        eq(
                                                                                            "websitelink",
                                                                                            it2.feedlink
                                                                                        )
                                                                                    }
                                                                                }
                                                                            }
                                                                        viewModel.getbookmarksdata()
                                                                        loading =
                                                                            false
                                                                        if (ishapticenabled) {
                                                                            haptic.performHapticFeedback(
                                                                                HapticFeedbackType.LongPress
                                                                            )
                                                                        }
                                                                    }

                                                                } else {
                                                                    loading =
                                                                        true
                                                                    coroutinescope.launch {
                                                                        val user =
                                                                            supabaseclient.client.auth.retrieveUserForCurrentSession(
                                                                                updateSession = true
                                                                            )
                                                                        sortedFeedsData?.get(
                                                                            list
                                                                        )
                                                                            ?.let { it2 ->
                                                                                sortedFeedsData[list]?.let { it3 ->
                                                                                    sortedFeedsData[list]?.let { it4 ->
                                                                                        bookmarkdatabase(
                                                                                            id = null,
                                                                                            websitelink = it2.feedlink,
                                                                                            email = user.email,
                                                                                            images = it4.imageurl,
                                                                                            title = it3.feedtitle


                                                                                        )
                                                                                    }
                                                                                }
                                                                            }
                                                                            ?.let { it4 ->
                                                                                supabaseclient.client.from(
                                                                                    "bookmarks"
                                                                                )
                                                                                    .insert(
                                                                                        it4
                                                                                    )
                                                                            }
                                                                        viewModel.getbookmarksdata()
                                                                        loading =
                                                                            false
                                                                        if (ishapticenabled) {
                                                                            haptic.performHapticFeedback(
                                                                                HapticFeedbackType.LongPress
                                                                            )
                                                                        }
                                                                    }

                                                                }
                                                            }
                                                        }) {

                                                            if (sortedFeedsData?.get(
                                                                    list
                                                                )
                                                                    ?.let { it2 ->
                                                                        bookmarklink?.contains(
                                                                            it2.feedlink
                                                                        )
                                                                    } == true
                                                            ) {
                                                                if (loading) {
                                                                    CircularProgressIndicator(
                                                                        strokeCap = StrokeCap.Round
                                                                    )
                                                                }

                                                                Icon(

                                                                    imageVector = Icons.Filled.Bookmark,
                                                                    contentDescription = null,
                                                                    modifier = Modifier.size(
                                                                        15.dp
                                                                    )
                                                                )
                                                            } else {
                                                                if (loading) {
                                                                    CircularProgressIndicator(
                                                                        strokeCap = StrokeCap.Round
                                                                    )
                                                                }
                                                                Icon(

                                                                    imageVector = Icons.Outlined.BookmarkBorder,
                                                                    contentDescription = null,
                                                                    modifier = Modifier.size(
                                                                        15.dp
                                                                    )
                                                                )

                                                            }


                                                        }
                                                        IconButton(onClick = {
                                                            if (ishapticenabled) {
                                                                haptic.performHapticFeedback(
                                                                    HapticFeedbackType.LongPress
                                                                )
                                                            }
                                                            val shareIntent =
                                                                Intent.createChooser(
                                                                    sendIntent,
                                                                    null
                                                                )
                                                            context.startActivity(
                                                                shareIntent
                                                            )


                                                        }) {
                                                            Icon(
                                                                imageVector = Icons.Outlined.Share,
                                                                contentDescription = null,
                                                                modifier = Modifier.size(
                                                                    15.dp
                                                                )
                                                            )

                                                        }

                                                        IconButton(onClick = {
                                                            if (ishapticenabled) {
                                                                haptic.performHapticFeedback(
                                                                    HapticFeedbackType.LongPress
                                                                )
                                                            }
                                                            aisummarypage = true
                                                            summary = ""
                                                            coroutinescope.launch {
                                                                val response =
                                                                    sortedFeedsData?.get(
                                                                        list
                                                                    )
                                                                        ?.let { it2 ->
                                                                            generativeModel.generateContent(
                                                                                it2.feedlink
                                                                            )
                                                                        }
                                                                if (response != null) {
                                                                    summary =
                                                                        response.text.toString()
                                                                }
                                                            }

                                                        }) {
                                                            Icon(
                                                                painter = painterResource(
                                                                    id = R.drawable.vector
                                                                ),
                                                                contentDescription = null,
                                                                tint = MaterialTheme.colorScheme.onBackground,
                                                                modifier = Modifier.size(
                                                                    15.dp
                                                                )
                                                            )

                                                        }

                                                    }
                                                }
                                            }

                                        },

                                        modifier = Modifier.combinedClickable(
                                            onClick = {
                                                if (sharedPreferences.getBoolean(
                                                        "inappbrowser",
                                                        false
                                                    )
                                                ) {
                                                    viewModel.update(
                                                        feeds(
                                                            id=sortedFeedsData[list].id,
                                                            data = sortedFeedsData[list].data,
                                                            feedlink = sortedFeedsData[list].feedlink,
                                                            opened = "true",
                                                            imageurl = sortedFeedsData[list].imageurl,
                                                            feedtitle = sortedFeedsData[list].feedtitle,
                                                            website = sortedFeedsData[list].website,

                                                        )
                                                    )
                                                    openTab(
                                                        context,
                                                        sortedFeedsData[list].feedlink
                                                    )
                                                }
                                                else{
                                                    uriHandler.openUri(sortedFeedsData[list].feedlink)
                                                    viewModel.update(
                                                        feeds(
                                                            id=sortedFeedsData[list].id,
                                                            data = sortedFeedsData[list].data,
                                                            feedlink = sortedFeedsData[list].feedlink,
                                                            opened = "true",
                                                            imageurl = sortedFeedsData[list].imageurl,
                                                            feedtitle = sortedFeedsData[list].feedtitle,
                                                            website = sortedFeedsData[list].website,

                                                        )
                                                    )

                                                }
                                            }
                                            ,

                                            onLongClick = {
                                                if (sharedPreferences.getBoolean(
                                                        "islongpress",
                                                        true
                                                    )
                                                ) {
                                                    if (ishapticenabled) {
                                                        vibrator.vibrate(
                                                            VibrationEffect.createWaveform(
                                                                timings,
                                                                amplitudes,
                                                                repeatIndex
                                                            )
                                                        )
                                                    }

                                                    if (sortedFeedsData[list].opened == "true") {
                                                        viewModel.update(
                                                            feeds(
                                                                id = sortedFeedsData[list].id,
                                                                data = sortedFeedsData[list].data,
                                                                feedlink = sortedFeedsData[list].feedlink,
                                                                opened = "false",
                                                                imageurl = sortedFeedsData[list].imageurl,
                                                                feedtitle = sortedFeedsData[list].feedtitle,
                                                                website = sortedFeedsData[list].website,


                                                            )
                                                        )
                                                    } else {
                                                        viewModel.update(
                                                            feeds(
                                                                id = sortedFeedsData[list].id,
                                                                data = sortedFeedsData[list].data,
                                                                feedlink = sortedFeedsData[list].feedlink,
                                                                opened = "true",
                                                                imageurl = sortedFeedsData[list].imageurl,
                                                                feedtitle = sortedFeedsData[list].feedtitle,
                                                                website = sortedFeedsData[list].website,

                                                            )
                                                        )

                                                    }
                                                }
                                            }

                                        )

                                    )


                                    HorizontalDivider(
                                        modifier = Modifier.padding(
                                            start = 10.dp,
                                            end = 10.dp
                                        )
                                    )
                                }
                            }




                        }
                        if (showtop) {

                            AnimatedVisibility(
                                visible = showScrollToTop,
                                enter = scaleIn() + expandVertically(expandFrom = Alignment.CenterVertically),
                                exit = scaleOut() + shrinkVertically(shrinkTowards = Alignment.CenterVertically),
                                modifier = Modifier
                                    .align(Alignment.BottomEnd)
                            ) {
                                FilledTonalIconButton(
                                    onClick = {
                                        coroutinescope.launch {

                                            listState.animateScrollToItem(index = 0)
                                        }
                                    }, modifier = Modifier
                                        .align(Alignment.BottomEnd)
                                        .padding(bottom = 10.dp, end = 10.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Outlined.ArrowUpward,
                                        contentDescription = null
                                    )

                                }
                            }


                        }
                    }
                }

                if (aisummarypage) {
                    ModalBottomSheet(
                        onDismissRequest = { aisummarypage = false;summary = "" },
                        modifier = Modifier.fillMaxSize()
                    ) {
                        if (summary != "") {

                            Column {
                                LazyColumn {
                                    item {
                                        SelectionContainer(content = {
                                            if (ishapticenabled) {
                                                haptic.performHapticFeedback(
                                                    HapticFeedbackType.LongPress
                                                )
                                            }

                                            MarkdownText(


                                                markdown = summary,
                                                color = MaterialTheme.colorScheme.onBackground,
                                                textAlign = TextAlign.Justify,
                                                modifier = Modifier.padding(15.dp)

                                            )
                                        })


                                    }
                                }

                            }
                        } else {

                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 200.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    text = "Summarizing Article",
                                    modifier = Modifier.padding(bottom = 15.dp)
                                )
                                LinearProgressIndicator(strokeCap = StrokeCap.Round)
                            }
                        }

                    }
                }

            }
        }
    }
}









