package com.example.rss_parser.screens

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat.startActivity
import androidx.navigation.NavHostController
import com.example.rss_parser.Navigation.Destinations
import com.example.rss_parser.database.websitedata.website

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun aboutapp(navHostController: NavHostController){
    val context= LocalContext.current
    val scroll=TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    val emailIntent = Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:"+"saicharansideprojects@gmail.com"))

    val packageManager: PackageManager =context.packageManager
    val uri= LocalUriHandler.current
    Scaffold(
        modifier= Modifier
            .fillMaxSize()
            .nestedScroll(scroll.nestedScrollConnection),
        {
            LargeTopAppBar(
                title = { Text("About app")},
                scrollBehavior = scroll,
                navigationIcon = {
                    IconButton(onClick = { navHostController.popBackStack() }) {
                        Icon(imageVector = Icons.AutoMirrored.Outlined.ArrowBack, contentDescription = "Back")

                    }
                }
            )
        }



        ) {
        LazyColumn(modifier=Modifier.fillMaxSize().padding(it)){
            item{
                ListItem({
                    Column {
                        Text("Version")
                        Text("1.0", fontWeight = FontWeight.ExtraLight)


                    }
                })
                HorizontalDivider(modifier=Modifier.padding(start=10.dp,end=10.dp))
                ListItem({
                    Column {
                        Text(text = "Project on Github")
                    }
                },
                    modifier =Modifier.clickable { uri.openUri("https://github.com/sai-charan2003/FeedHub") }

                    )
                HorizontalDivider(modifier=Modifier.padding(start=10.dp,end=10.dp))
                ListItem(


                    {
                        Row(
                            modifier = Modifier.fillMaxSize(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                "About Developer",
                                modifier = Modifier.padding(),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )


                        }

                    },
                    modifier=Modifier.clickable { navHostController.navigate(Destinations.aboutdeveloper.route) }

                )
                HorizontalDivider(modifier=Modifier.padding(start=10.dp,end=10.dp))
                ListItem({
                    Column {
                        Text(text = "Report an issue")
                    }
                },
                    modifier=Modifier.clickable { startActivity(context,emailIntent,null) })
            }


        }

    }
}