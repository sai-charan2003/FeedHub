package com.example.rss_parser.screens

import android.content.Context
import android.content.SharedPreferences
import android.widget.Toast
import androidx.compose.foundation.layout.Column

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.rss_parser.check_network.Connectionstatus
import com.example.rss_parser.check_network.connectivityState

import com.example.rss_parser.supabase.client.supabaseclient
import com.example.rss_parser.viewmodel.viewmodel
import com.example.rss_parser.supabase.database.website_supabase
import io.github.jan.supabase.exceptions.HttpRequestException
import io.github.jan.supabase.exceptions.RestException
import io.github.jan.supabase.gotrue.auth
import io.github.jan.supabase.postgrest.from
import io.ktor.client.plugins.HttpRequestTimeoutException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable



fun addfeed(navHostController: NavHostController) {

    val context = LocalContext.current
    val scroll= TopAppBarDefaults.pinnedScrollBehavior()
    val coroutine= rememberCoroutineScope()

    var isloading by remember{
        mutableStateOf(false)
    }

    val connection by connectivityState()

    val isConnected = connection === Connectionstatus.Available

    val sharedPreferences: SharedPreferences = context.getSharedPreferences(
        "showimages",
        Context.MODE_PRIVATE
    )








            Scaffold(modifier= Modifier
                .fillMaxSize()
                .nestedScroll(scroll.nestedScrollConnection),topBar = {
                TopAppBar(title = {
                    Text("Add Feed")

                },
                    scrollBehavior =scroll,
                    navigationIcon = {
                        IconButton(onClick = { navHostController.popBackStack() }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                                contentDescription = "Back"
                            )

                        }
                    }


                )
            }) {
                val context = LocalContext.current
                val viewModel = viewModel<viewmodel>(
                    factory = object : ViewModelProvider.Factory {
                        override fun <T : ViewModel> create(modelClass: Class<T>): T {
                            return viewmodel(
                                context
                            ) as T
                        }
                    }

                )
                val url by viewModel.websiteUrls.observeAsState()

                LaunchedEffect(Unit) {
                    if(isConnected) {
                        try {
                            viewModel.getwebsiteurlfromdb()
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
                        viewModel.websiterb.clear()
                        val results: List<website_supabase>
                        val websites: List<String>
                        withContext(Dispatchers.IO) {
                            results = supabaseclient.client.from("website").select()
                                .decodeList<website_supabase>()
                            viewModel.websiterb.addAll(results)
                            websites = viewModel.websiterb.map { it.websitelink }


                        }





                        viewModel.getData(websites)

                    }


                }

                var feedlink by remember {
                    mutableStateOf("")
                }
                


                var showerror by remember {
                    mutableStateOf(false)
                }

                val couroutine = rememberCoroutineScope()
                Column(
                    modifier = Modifier
                        .padding(it)
                        .fillMaxSize()
                ) {
                    if (isConnected) {

                        OutlinedTextField(
                            value = feedlink,
                            onValueChange = {
                                feedlink = it
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 20.dp, end = 20.dp, top = 20.dp),
                            isError = showerror,
                            label = { Text(text = "Feed URL") },
                            placeholder = { Text("https://feed.com/feed/") },
                        )
                        if (showerror) {
                            Text(
                                text = "Invalid RSS feed link",
                                color = Color(0xffFF6969),
                                style = MaterialTheme.typography.labelLarge,
                                modifier = Modifier.padding(start = 40.dp)
                            )
                        }

                        Button(
                            onClick = {
                                if (url?.contains(feedlink) == true) {
                                    Toast.makeText(context, "Already Exits", Toast.LENGTH_SHORT)
                                        .show()

                                } else {
                                    couroutine.launch {
                                        isloading = true
                                        val checkforerror = viewModel.fetchrssfeed(feedlink)
                                        if (checkforerror != null) {
                                            coroutine.launch {
                                                val user =
                                                    supabaseclient.client.auth.retrieveUserForCurrentSession(
                                                        updateSession = true
                                                    )
                                                try {
                                                    supabaseclient.client.from("website").insert(
                                                        website_supabase(
                                                            id = null,
                                                            websitelink = feedlink,
                                                            email = user.email
                                                        )
                                                    )
                                                    Toast.makeText(
                                                        context,
                                                        "Feed added",
                                                        Toast.LENGTH_SHORT
                                                    )
                                                        .show()
                                                    showerror = false
                                                    feedlink = ""
                                                    viewModel.getwebsiteurlfromdb()
                                                    isloading = false
                                                } catch (e: Exception) {
                                                    isloading = false
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
                                            }

                                        } else {
                                            isloading = false
                                            showerror = true

                                        }


                                    }
                                }

                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 10.dp, end = 10.dp, top = 20.dp),

                            ) {
                            if (isloading) {
                                Text("Adding.....")
                            } else {
                                Text(text = "Add Feed")
                            }

                        }
                        HorizontalDivider(
                            modifier = Modifier.padding(
                                top = 10.dp,
                                start = 10.dp,
                                end = 10.dp
                            )
                        )
                        Column(modifier = Modifier.verticalScroll(rememberScrollState())) {

                            Text(
                                text = "Top feeds",
                                textAlign = TextAlign.Center,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(top = 20.dp),
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.titleLarge
                            )
                            ListItem({
                                Text(
                                    text = "Technology",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold
                                )
                            })
                            topfeeds(
                                rssurl = "https://www.theverge.com/rss/index.xml",
                                websitename = "The Verge",
                                websiteurl = "theverge.com"
                            )
                            topfeeds(
                                rssurl = "https://www.engadget.com/rss.xml",
                                websitename = "Engadget",
                                websiteurl = "engadget.com"
                            )
                            topfeeds(
                                rssurl = "https://techcrunch.com/feed/",
                                websitename = "TechCrunch",
                                websiteurl = "techcrunch.com"
                            )
                            topfeeds(
                                rssurl = "https://www.wired.com/feed",
                                websitename = "Wired",
                                websiteurl = "wired.com"
                            )
                            topfeeds(
                                rssurl = "https://www.androidauthority.com/feed/",
                                websitename = "Android Authority",
                                websiteurl = "androidauthority.com"
                            )
                            topfeeds(
                                rssurl = "https://feeds.macrumors.com/MacRumors-All",
                                websitename = "MacRumors",
                                websiteurl = "macrumors.com"
                            )

                            topfeeds(
                                rssurl = "https://www.cnet.com/rss/news/",
                                websitename = "CNET",
                                websiteurl = "cnet.com"
                            )
                            topfeeds(
                                rssurl = "https://www.anandtech.com/rss/",
                                websitename = "Anand Tech",
                                websiteurl = "anandtech.com"
                            )
                            topfeeds(
                                rssurl = "https://beebom.com/feed/",
                                websitename = "Beebom",
                                websiteurl = "Beebom.com"
                            )
                            topfeeds(
                                rssurl = "https://techwiser.com/feed/",
                                websitename = "TechWiser",
                                websiteurl = "techwiser.com"
                            )
                            topfeeds(
                                rssurl = "https://superchargednews.com/feed/",
                                websitename = "Supercharged",
                                websiteurl = "superchargednews.com"
                            )
                            topfeeds(
                                rssurl = "https://www.digitaltrends.com/feed/",
                                websitename = "digitaltrends",
                                websiteurl = "digitaltrends.com"
                            )
                            topfeeds(
                                rssurl = "https://9to5google.com/feed/",
                                websitename = "9to5google",
                                websiteurl = "9to5google.com"
                            )
                            topfeeds(
                                rssurl = "https://9to5mac.com/feed/",
                                websitename = "9to5mac",
                                websiteurl = "9to5mac.com"
                            )
                            topfeeds(
                                rssurl = "https://www.techradar.com/feeds.xml",
                                websitename = "techradar",
                                websiteurl = "techradar.com"
                            )
                            topfeeds(
                                rssurl = "https://www.pcworld.com/feed",
                                websitename = "PCworld",
                                websiteurl = "pcworld.com"
                            )
                            topfeeds(
                                rssurl = "https://www.gsmarena.com/rss-news-reviews.php3",
                                websitename = "GSMArena",
                                websiteurl = "gsmarena.com"
                            )
                            topfeeds(
                                rssurl = "https://www.androidpolice.com/feed/",
                                websitename = "Android Police",
                                websiteurl = "androidpolice.com"
                            )
                            ListItem({
                                Text(
                                    text = "Political",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(top = 10.dp)
                                )
                            })
                            topfeeds(
                                rssurl = "https://feeds.feedburner.com/ndtvnews-top-stories",
                                websitename = "NDTV",
                                websiteurl = "ndtv.com"
                            )
                            topfeeds(
                                rssurl = "https://www.indiatoday.in/rss/1206614",
                                websitename = "India Today",
                                websiteurl = "indiatoday.in"
                            )


                        }
                    }

                }


                }



                }









