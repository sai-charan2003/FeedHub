package com.example.rss_parser.Widgets

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat.startActivity
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.components.Scaffold
import androidx.glance.appwidget.lazy.LazyColumn
import androidx.glance.appwidget.provideContent
import androidx.glance.appwidget.updateAll
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.ContentScale
import androidx.glance.layout.Row
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextAlign
import androidx.glance.text.TextStyle

import com.example.rss_parser.R
import com.example.rss_parser.Utils.SharedPref
import com.example.rss_parser.database.feeddatabase.feeds
import com.example.rss_parser.viewmodel.viewmodel
import com.example.rss_parser.Utils.AddFeedsWorker
import com.example.rss_parser.Utils.DateTimeFormatter.convertToMillisecondsEpoch
import com.example.rss_parser.Utils.DateTimeFormatter.formatDate
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

object FeedWidget:GlanceAppWidget(){

    override suspend fun provideGlance(context: Context, id: GlanceId) {

        provideContent {

                LaunchedEffect(Unit) {
                    AddFeedsWorker.setup()
                }
                val SharedPref= SharedPref(context)
                val islog=SharedPref.isLoggedIn


                val intent = Intent(Intent.ACTION_VIEW)


                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                val openfeedhub=Intent(Intent.ACTION_MAIN)
                openfeedhub.`package`="com.example.rss_parser"
                openfeedhub.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                val openapp : Intent? = context.packageManager.getLaunchIntentForPackage("com.example.rss_parser")
                val viewmodel=viewmodel(context)
                var feeddata by remember {
                    mutableStateOf<List<feeds>>(emptyList<feeds>())
                }
                var imagedata by remember {
                    mutableStateOf<List<ImageProvider?>>(emptyList())
                }
                LaunchedEffect(Unit) {
                    viewmodel.allfeeds.observeForever {
                        feeddata=it
                    }

                }
                val uri = LocalUriHandler


                val coroutine= CoroutineScope(Dispatchers.IO)

                val sortedFeedsData = feeddata.let { feeds ->
                    if (feeds.isNotEmpty()) {
                        feeds.asSequence().mapIndexed { index, feed ->
                            Triple(index, feed, convertToMillisecondsEpoch(feed.date!!))
                        }.sortedByDescending { (_, _, dateInMillis) -> dateInMillis }
                            .filter { (_, feed, _) -> feed.opened == "false" }
                            .map { (_, feed, _) -> feed }
                            .take(10).toList()
                    } else {
                        emptyList()
                    }
                }
                Scaffold(titleBar = {
                    Row(
                        verticalAlignment = Alignment.Vertical.CenterVertically,
                        modifier=GlanceModifier
                            .fillMaxWidth()
                            .clickable {
                                if (openapp != null) {
                                    startActivity(context,openapp,null)
                                }
                        }

                    ) {
                        Image(
                            provider = ImageProvider(R.drawable.rounded_rss_feed_50),
                            null,
                            contentScale = ContentScale.Fit,
                            modifier = GlanceModifier
                                .padding(start = 10.dp, top = 10.dp)
                                .size(43.dp)
                        )
                        Text(
                            "Feed Hub",
                            style = TextStyle(
                                color = GlanceTheme.colors.onSurface,
                                fontSize = 15.sp,
                                textAlign = TextAlign.Center,
                                fontWeight = FontWeight.Medium

                            ),
                            modifier = GlanceModifier
                                .padding(start = 5.dp,top=10.dp)

                        )



                    }


                },
                    modifier = GlanceModifier

                        .padding(bottom=7.dp)


                ) {
                    if(islog) {

                        LazyColumn(
                            modifier = GlanceModifier

                        ) {
                            items(sortedFeedsData.size) {
                                Column(
                                    modifier = GlanceModifier
                                            .clickable {
                                            intent.data =
                                                Uri.parse(sortedFeedsData[it].feedlink)
                                            startActivity(context, intent, null)
                                            coroutine.launch {
                                                viewmodel.Updateopened(sortedFeedsData[it].id,"true")
                                                FeedWidget.updateAll(context)
                                            }

                                        }




                                ) {

                                    Text(
                                        text = sortedFeedsData[it].feedtitle!!,
                                        style = TextStyle(
                                            color = GlanceTheme.colors.onSurface,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 15.sp
                                        ),

                                        modifier = GlanceModifier

                                            .padding(top = 10.dp)


                                    )
                                    Row(
                                        modifier = GlanceModifier
                                            .fillMaxWidth()
                                            .padding(top = 10.dp, bottom = 10.dp)

                                    ) {
                                        Text(
                                            text = sortedFeedsData[it].feedlink!!
                                                .substringAfter("https://")
                                                .substringAfter("www.")
                                                .substringBefore(".com")
                                                .substringBefore(".in"),
                                            style = TextStyle(
                                                color = GlanceTheme.colors.onSurfaceVariant,
                                                fontWeight = FontWeight.Normal
                                            )

                                        )
                                        Text(
                                            "•",
                                            style = TextStyle(
                                                color = GlanceTheme.colors.onSurfaceVariant,
                                                fontWeight = FontWeight.Normal
                                            ),
                                            modifier = GlanceModifier
                                        )

                                        Text(
                                            formatDate(sortedFeedsData[it].date!!),

                                            style = TextStyle(
                                                color = GlanceTheme.colors.onSurfaceVariant,
                                                fontWeight = FontWeight.Normal
                                            )
                                        )

                                    }
                                    Box(
                                        modifier = GlanceModifier
                                            .fillMaxWidth()
                                            .height(0.2.dp)
                                            .background(GlanceTheme.colors.outline)
                                    ) {

                                    }


                                }
                            }


                        }
                    }
                    else{
                        Column(modifier=GlanceModifier.fillMaxSize()) {
                            Text("Sign in to access your feeds")

                        }
                    }



                    }

            }
                }





        }




class FeedWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget
        get() = FeedWidget
}

