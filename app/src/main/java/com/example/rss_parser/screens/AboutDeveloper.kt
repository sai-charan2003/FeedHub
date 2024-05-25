package com.example.rss_parser.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Public
import androidx.compose.material.icons.outlined.Web
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.rss_parser.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutDeveloper(navHostController: NavHostController){
    val Scroll=TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    val uri= LocalUriHandler.current

    Scaffold(
        modifier= Modifier
            .fillMaxSize()
            .nestedScroll(Scroll.nestedScrollConnection),
        topBar = {
            LargeTopAppBar(title = { Text("About developer") }, scrollBehavior = Scroll, navigationIcon = {
                IconButton(onClick = { navHostController.popBackStack()}) {
                    Icon(imageVector = Icons.AutoMirrored.Outlined.ArrowBack, contentDescription = "Back")

                }
            })
        }


        ) {
        LazyColumn(modifier= Modifier
            .fillMaxSize()
            .padding(it), horizontalAlignment = Alignment.CenterHorizontally){
            item{
                Image(
                    painter = painterResource(id = R.drawable.developer),
                    contentDescription = "Developer",
                    modifier= Modifier
                        .padding(top = 20.dp)
                        .size(150.dp)
                        .clip(CircleShape)


                    )
                Text(text = "Sai Charan", style = MaterialTheme.typography.titleLarge,modifier=Modifier.padding(top=20.dp,bottom=20.dp))

                ElevatedButton(onClick = { uri.openUri("https://github.com/sai-charan2003") },modifier=Modifier.fillMaxWidth().padding(start=10.dp,end=10.dp)) {
                    Icon(painter = painterResource(id = R.drawable.github), contentDescription = "Github",modifier=Modifier.padding(end = 10.dp))
                    Text("Github")
                    
                }
                ElevatedButton(onClick = { uri.openUri("https://www.linkedin.com/in/sai-charan-n-ab250b22a/") },modifier=Modifier.fillMaxWidth().padding(start=10.dp,end=10.dp, top = 20.dp)) {
                    Icon(painter = painterResource(id = R.drawable.link), contentDescription = "Link",modifier=Modifier.padding(end = 10.dp))
                    Text("LinkedIn")

                }
                ElevatedButton(onClick = { uri.openUri("https://twitter.com/saicharan2003") },modifier=Modifier.fillMaxWidth().padding(start=10.dp,end=10.dp, top = 20.dp)) {
                    Icon(painter = painterResource(id = R.drawable.twitter), contentDescription = "twitter",modifier=Modifier.padding(end = 10.dp))
                    Text("X(Twitter)")

                }
                ElevatedButton(onClick = { uri.openUri("https://nsaicharan.onrender.com/") },modifier=Modifier.fillMaxWidth().padding(start=10.dp,end=10.dp, top = 20.dp)) {
                    Icon(imageVector = Icons.Outlined.Public, contentDescription = "portfolio",modifier=Modifier.padding(end = 10.dp))
                    Text("Portfolio")

                }
            }


        }

    }


}