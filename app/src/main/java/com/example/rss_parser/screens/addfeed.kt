package com.example.rss_parser.screens

import android.content.Context
import android.content.SharedPreferences
import android.widget.Toast
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Done
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
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
import com.example.rss_parser.database.websitedata.website
import com.example.rss_parser.ui.theme.RSSparserTheme
import com.example.rss_parser.viewmodel.viewmodel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable



fun addfeed(navHostController: NavHostController) {
    val context = LocalContext.current
    val scroll= TopAppBarDefaults.pinnedScrollBehavior()

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
                var feedlink by remember {
                    mutableStateOf("")


                }
                val feedswebsites = viewModel.allwebsitelinks.observeAsState(initial = emptyList())
                val websites = feedswebsites.value.map { it.websitelink }

                var showerror by remember {
                    mutableStateOf(false)
                }

                val couroutine = rememberCoroutineScope()
                Column(
                    modifier = Modifier
                        .padding(it)
                        .fillMaxSize()
                ) {

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
                            couroutine.launch {
                                val checkforerror = viewModel.fetchrssfeed(feedlink)
                                if (checkforerror != null) {
                                    viewModel.insert(website(0, feedlink))
                                    Toast.makeText(context, "Feed added", Toast.LENGTH_SHORT)
                                        .show()
                                    showerror = false
                                    feedlink = ""

                                } else {
                                    showerror = true

                                }


                            }

                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 10.dp, end = 10.dp, top = 20.dp),

                        ) {
                        Text(text = "Add Feed")

                    }
                    HorizontalDivider(
                        modifier = Modifier.padding(
                            top = 10.dp,
                            start = 10.dp,
                            end = 10.dp
                        )
                    )
                    Column(modifier=Modifier.verticalScroll(rememberScrollState())){

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
                                url = "https://www.theverge.com/rss/index.xml",
                                websitename = "The Verge",
                                websiteurl = "theverge.com"
                            )
                            topfeeds(
                                url = "https://www.engadget.com/rss.xml",
                                websitename = "Engadget",
                                websiteurl = "engadget.com"
                            )
                            topfeeds(
                                url = "https://techcrunch.com/feed/",
                                websitename = "TechCrunch",
                                websiteurl = "techcrunch.com"
                            )
                            topfeeds(
                                url = "https://www.wired.com/feed",
                                websitename = "Wired",
                                websiteurl = "wired.com"
                            )
                            topfeeds(
                                url = "https://www.androidauthority.com/feed/",
                                websitename = "Android Authority",
                                websiteurl = "androidauthority.com"
                            )
                            topfeeds(
                                url = "https://feeds.macrumors.com/MacRumors-All",
                                websitename = "MacRumors",
                                websiteurl = "macrumors.com"
                            )

                            topfeeds(
                                url = "https://www.cnet.com/rss/news/",
                                websitename = "CNET",
                                websiteurl = "cnet.com"
                            )
                            topfeeds(
                                url = "https://www.anandtech.com/rss/",
                                websitename = "Anand Tech",
                                websiteurl = "anandtech.com"
                            )
                            topfeeds(
                                url = "https://www.beebom.com/feed/",
                                websitename = "Beebom",
                                websiteurl = "Beebom.com"
                            )
                            topfeeds(
                                url = "https://www.techwiser.com/feed/",
                                websitename = "TechWiser",
                                websiteurl = "techwiser.com"
                            )
                            topfeeds(
                                url = "https://superchargednews.com/feed/",
                                websitename = "Supercharged",
                                websiteurl = "superchargednews.com"
                            )
                            topfeeds(
                                url = "https://www.digitaltrends.com/feed/",
                                websitename = "digitaltrends",
                                websiteurl = "digitaltrends.com"
                            )
                            topfeeds(
                                url = "https://www.9to5google.com/feed/",
                                websitename = "9to5google",
                                websiteurl = "9to5google.com"
                            )
                            topfeeds(
                                url = "https://www.9to5mac.com/feed/",
                                websitename = "9to5mac",
                                websiteurl = "9to5mac.com"
                            )
                            topfeeds(
                                url = "https://www.techradar.com/feeds.xml",
                                websitename = "techradar",
                                websiteurl = "techradar.com"
                            )
                            topfeeds(
                                url = "https://www.pcworld.com/feed",
                                websitename = "PCworld",
                                websiteurl = "pcworld.com"
                            )
                            topfeeds(
                                url = "https://www.gsmarena.com/rss-news-reviews.php3",
                                websitename = "GSMArena",
                                websiteurl = "gsmarena.com"
                            )
                            topfeeds(
                                url = "https://www.androidpolice.com/feed/",
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
                                url = "https://feeds.feedburner.com/ndtvnews-top-stories",
                                websitename = "NDTV",
                                websiteurl = "ndtv.com"
                            )
                            topfeeds(
                                url = "https://www.indiatoday.in/rss/1206614",
                                websitename = "India Today",
                                websiteurl = "indiatoday.in"
                            )



                        }
                    }

                }



                }









