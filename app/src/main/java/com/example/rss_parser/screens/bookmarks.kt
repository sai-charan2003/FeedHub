package com.example.rss_parser.screens

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.nestedscroll.nestedScroll
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
import com.example.rss_parser.R
import com.example.rss_parser.check_network.Connectionstatus
import com.example.rss_parser.check_network.connectivityState
import com.example.rss_parser.inappbrowser.openTab
import com.example.rss_parser.supabase.client.supabaseclient
import com.example.rss_parser.ui.theme.RSSparserTheme
import com.example.rss_parser.viewmodel.viewmodel
import com.google.ai.client.generativeai.GenerativeModel
import com.meetup.twain.MarkdownText
import io.github.jan.supabase.exceptions.HttpRequestException
import io.github.jan.supabase.exceptions.RestException
import io.github.jan.supabase.postgrest.from
import io.ktor.client.plugins.HttpRequestTimeoutException
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable

fun booksmarks(navHostController: NavHostController) {



    val context = LocalContext.current
    val uriHandler = LocalUriHandler.current
    val generativeModel = GenerativeModel(
        modelName = "gemini-pro",
        apiKey = BuildConfig.apiKey
    )
    val haptic= LocalHapticFeedback.current

    val coroutine= rememberCoroutineScope()
    val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("showimages", Context.MODE_PRIVATE)

    val showimages by remember {
        mutableStateOf(sharedPreferences.getBoolean("showimages", false))
    }
    var ishapticenabled by remember{
        mutableStateOf(sharedPreferences.getBoolean("hapticenabled",true))
    }
    var aisummarypage by remember {
        mutableStateOf(false)
    }
    var summary by remember {
        mutableStateOf("")
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
    val bookmarks by viewModel.bookmarkdata.observeAsState()
    val connection by connectivityState()

    val isConnected = connection === Connectionstatus.Available
    LaunchedEffect(key1=bookmarks){
        if(isConnected){
            try {

                viewModel.getbookmarksdata()
            }
            catch (e:Exception){
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
    }

    }

    //val bookmarks by viewModel.allbookmarks.observeAsState(initial = emptyList())
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
            if (bookmarks?.isEmpty() == true) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text("No Bookmarks added")

                }
            } else {

                if (isConnected) {


                    LazyColumn(modifier = Modifier.padding(it)) {
                        bookmarks?.let { it1 ->
                            items(it1.size) {
                                var isloading by remember {
                                    mutableStateOf(false)
                                }
                                ListItem(
                                    {
                                        val sendIntent: Intent = Intent().apply {
                                            action = Intent.ACTION_SEND
                                            putExtra(Intent.EXTRA_TEXT, bookmarks!![it].websitelink)
                                            type = "text/plain"
                                        }
                                        Row(
                                            horizontalArrangement = Arrangement.Center,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            if (showimages) {
                                                AsyncImage(
                                                    model = bookmarks!![it].images,
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
                                                    text = bookmarks!![it].websitelink.substringAfter(
                                                        "https://"
                                                    )
                                                        .substringAfter("www.")
                                                        .substringBefore(".com"),
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
                                                    text = bookmarks!![it].title,
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
                                                        isloading = true
                                                        coroutine.launch {
                                                            supabaseclient.client.from("bookmarks")
                                                                .delete {
                                                                    filter {
                                                                        eq(
                                                                            "websitelink",
                                                                            bookmarks!![it].websitelink
                                                                        )
                                                                    }
                                                                }
                                                            viewModel.getbookmarksdata()
                                                        }
                                                        isloading = false
                                                        if(ishapticenabled) {
                                                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                                        }

                                                    }) {
                                                        if (isloading) {
                                                            CircularProgressIndicator( strokeCap = StrokeCap.Round)
                                                        }

                                                        Icon(

                                                            imageVector = Icons.Filled.Bookmark,
                                                            contentDescription = null,
                                                            modifier = Modifier.size(15.dp)
                                                        )

                                                    }




                                                    IconButton(onClick = {
                                                        if(ishapticenabled) {
                                                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                                        }
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
                                                    IconButton(onClick = { aisummarypage=true;coroutine.launch {
                                                        if(ishapticenabled) {
                                                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                                        }
                                                        summary=""
                                                        val response=generativeModel.generateContent(
                                                            bookmarks!![it].websitelink)
                                                        summary=response.text.toString()

                                                    } }) {
                                                        Icon(painter = painterResource(id = R.drawable.vector), contentDescription = null,modifier=Modifier.size(15.dp))

                                                    }

                                                }
                                            }
                                        }

                                    },
                                    modifier = Modifier.clickable {

                                        if (sharedPreferences.getBoolean("inappbrowser", false)) {
                                            openTab(context, bookmarks!![it].websitelink)
                                        } else {
                                            uriHandler.openUri(bookmarks!![it].websitelink)
                                        }
                                    }
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
                }
                else{
                    Column(modifier=Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
                        Image(painter = painterResource(id = R.drawable.round_wifi_off_24), contentDescription = "no internet")
                        Text("Couldn't connect to internet.")
                        Text("Please check your internet connection")

                    }
                }
            }
            if(aisummarypage){
                ModalBottomSheet(onDismissRequest = { aisummarypage=false;summary=""},modifier=Modifier.fillMaxSize()) {
                    if(summary!="") {

                        LazyColumn {
                            item {
                                if(ishapticenabled) {
                                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                }
                                MarkdownText(
                                    markdown = summary,
                                    color = MaterialTheme.colorScheme.onBackground,
                                    textAlign = TextAlign.Justify,
                                    modifier=Modifier.padding(15.dp)

                                )






                            }
                        }
                    }
                    else{

                        Column(modifier=Modifier.fillMaxWidth().padding(top=200.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                            Text(text="Summarizing Article",modifier=Modifier.padding(bottom=15.dp))
                            LinearProgressIndicator(strokeCap = StrokeCap.Round)
                        }
                    }

                }
            }
        }
    }
}