package com.example.rss_parser.screens

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.VibrationEffect
import android.os.Vibrator
import android.util.Log
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material3.ListItem
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DockedSearchBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Text
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
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
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
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.rss_parser.BuildConfig
import com.example.rss_parser.DateandTimeConverter.convertTo12HourFormatWithDayAndMonth
import com.example.rss_parser.DateandTimeConverter.formatDateAndLocalTime
import com.example.rss_parser.Navigation.Destinations
import com.example.rss_parser.R
import com.example.rss_parser.database.feeddatabase.feeds
import com.example.rss_parser.inappbrowser.openTab
import com.example.rss_parser.supabase.client.supabaseclient
import com.example.rss_parser.supabase.database.bookmarkdatabase
import com.example.rss_parser.viewmodel.viewmodel
import com.google.ai.client.generativeai.GenerativeModel
import com.meetup.twain.MarkdownText
import io.github.jan.supabase.gotrue.auth
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.android.awaitFrame
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class,
    ExperimentalFoundationApi::class
)
@Composable
fun search(navHostController: NavHostController) {
    var query by rememberSaveable {
        mutableStateOf("")
    }

    var active by rememberSaveable {
        mutableStateOf(true)
    }
    var summary by remember {
        mutableStateOf("")
    }

    var haptic= LocalHapticFeedback.current
    val coroutinescope = rememberCoroutineScope()
    val timings: LongArray = longArrayOf(100, 100, 100, 100, 100, 100,)
    val amplitudes: IntArray = intArrayOf(23, 41, 65, 103, 160, 255,)
    val repeatIndex = -1
    val generativeModel = GenerativeModel(
        modelName = "gemini-pro",
        apiKey = BuildConfig.apiKey
    )
    var aisummarypage by remember {
        mutableStateOf(false)
    }
    val context = LocalContext.current
    val vibrator = LocalContext.current.getSystemService(Vibrator::class.java)

    val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("showimages", Context.MODE_PRIVATE)
    val showimages by remember {
        mutableStateOf(sharedPreferences.getBoolean("showimages", true))
    }
    var ishapticenabled by remember {
        mutableStateOf(sharedPreferences.getBoolean("hapticenabled", true))
    }
    var applicationcontext= LocalContext.current.applicationContext

    val viewModel = viewModel<viewmodel>(
        factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return viewmodel(
                    context
                ) as T
            }
        }
    )
    var isclicked by remember {
        mutableStateOf(false)
    }
    val focusRequester = remember { FocusRequester()}
    viewModel.getbookmarksdata()
    val bookmarkdata by viewModel.bookmarkdata.observeAsState()
    val bookmarklink = bookmarkdata?.map { it.websitelink }

    val scope= rememberCoroutineScope()
    var result by remember {
        mutableStateOf<List<feeds>>(emptyList())
    }

    val searchresults by viewModel.searchresults.collectAsState(initial = emptyList())
    if(!isclicked) {
        result = searchresults
    }


    Scaffold {


        Column(modifier= Modifier
            .padding(it)
            .padding(top = 10.dp)) {
            val uriHandler = LocalUriHandler.current
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = { navHostController.popBackStack() }) {
                    Icon(imageVector = Icons.Outlined.ArrowBack, contentDescription = null)

                }


                DockedSearchBar(
                    query = query,
                    onQueryChange = {
                        query = it
                        isclicked=false



                    },
                    onSearch = {
                        active = false

                    },
                    active = false,
                    onActiveChange = {
                        active = false
                    },
                    leadingIcon = {
                        Icon(imageVector = Icons.Outlined.Search, contentDescription = null)
                        LaunchedEffect(key1 = Unit) {
                            focusRequester.requestFocus()

                        }
                    },

                    modifier = Modifier.focusRequester(focusRequester),


                    ) {


                }
                LaunchedEffect(key1 = query) {
                    viewModel.search(query)
                    Log.d("Checking", "search: hi from launched effect")

                }
            }

            LazyColumn {


                items(result.size){list->
                    var loading by remember {
                        mutableStateOf(false)

                    }
                    ListItem(

                        {
                            val sendIntent: Intent =
                                Intent().apply {
                                    action = Intent.ACTION_SEND
                                    putExtra(
                                        Intent.EXTRA_TEXT,
                                        searchresults?.get(list)?.feedlink
                                    )
                                    type = "text/plain"
                                }
                            Row(
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                if (showimages) {
                                    AsyncImage(
                                        model = result?.get(
                                            list
                                        )?.imageurl,
                                        contentDescription = "null",
                                        modifier = Modifier
                                            .height(100.dp)
                                            .width(100.dp)

                                            .clip(
                                                RoundedCornerShape(
                                                    5.dp
                                                )
                                            ),
                                        alignment = Alignment.CenterEnd,
                                        contentScale = ContentScale.Fit,


                                        colorFilter =
                                        if (result?.get(
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

                                }

                                Column {
                                    result.get(list).feedlink?.substringAfter(
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

                                    result?.get(list)
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
                                                if (searchresults?.get(
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
                                            result?.get(list)
                                                ?.let { it2 ->
                                                    formatDateAndLocalTime(
                                                        it2.data
                                                    )
                                                }
                                        } else {
                                            result?.get(list)
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
                                            isclicked=true

                                            if (bookmarklink != null) {
                                                loading = true
                                                if (result?.get(
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
                                                                    result[list]?.let { it2 ->
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
                                                        result?.get(
                                                            list
                                                        )
                                                            ?.let { it2 ->
                                                                result[list]?.let { it3 ->
                                                                    result[list]?.let { it4 ->
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

                                            if (result?.get(
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
                                            isclicked=true
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
                                            isclicked=true
                                            if (ishapticenabled) {
                                                haptic.performHapticFeedback(
                                                    HapticFeedbackType.LongPress
                                                )
                                            }
                                            aisummarypage = true
                                            summary = ""
                                            coroutinescope.launch {
                                                val response =
                                                    result?.get(
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
                                isclicked=true
                                if (sharedPreferences.getBoolean(
                                        "inappbrowser",
                                        false
                                    )
                                ) {
                                    scope.launch(Dispatchers.IO) {
                                        viewModel.update(
                                            feeds(
                                                id = result[list].id,
                                                data = result[list].data,
                                                feedlink = result[list].feedlink,
                                                opened = "true",
                                                imageurl = result[list].imageurl,
                                                feedtitle = result[list].feedtitle,
                                                website = result[list].website,

                                            )
                                        )
                                        viewModel.search(query)


                                    }

                                    openTab(
                                        context,
                                        searchresults[list].feedlink
                                    )
                                } else {
                                    scope.launch(Dispatchers.IO) {
                                        viewModel.update(
                                            feeds(
                                                id = result[list].id,
                                                data = result[list].data,
                                                feedlink = result[list].feedlink,
                                                opened = "true",
                                                imageurl = result[list].imageurl,
                                                feedtitle = result[list].feedtitle,
                                                website = result[list].website,

                                            )
                                        )
                                        viewModel.search(query)

                                    }
                                    uriHandler.openUri(result[list].feedlink)


                                }
                            },

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

                                    if (result[list].opened == "true") {
                                        viewModel.update(
                                            feeds(
                                                id = result[list].id,
                                                data = result[list].data,
                                                feedlink = result[list].feedlink,
                                                opened = "false",
                                                imageurl = result[list].imageurl,
                                                feedtitle = result[list].feedtitle,
                                                website = result[list].website,

                                            )
                                        )
                                    } else {
                                        viewModel.update(
                                            feeds(
                                                id = result[list].id,
                                                data = result[list].data,
                                                feedlink = result[list].feedlink,
                                                opened = "true",
                                                imageurl = result[list].imageurl,
                                                feedtitle = result[list].feedtitle,
                                                website = result[list].website,

                                            )
                                        )

                                    }
                                }
                            }

                        )

                    )


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