package com.example.rss_parser.screens

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.text.Layout
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.filled.Circle
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.ModalDrawerSheet
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat.startActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.rss_parser.Navigation.Destinations
import com.example.rss_parser.viewmodel.viewmodel
import com.meetup.twain.MarkdownText


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun aboutapp(navHostController: NavHostController) {
    val context = LocalContext.current
    val scroll = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    val emailIntent =
        Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:" + "saicharansideprojects@gmail.com"))
    val viewModel = viewModel<viewmodel>(
        factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return viewmodel(
                    context
                ) as T
            }
        }
    )
    var showupdatebottom by remember {
        mutableStateOf(false)
    }
    var version = "2.1"

    val autoupdate by viewModel.updatedata.observeAsState()
    var updateavailable by remember {
        mutableStateOf(false)
    }
    LaunchedEffect(key1 = Unit) {
        viewModel.getupdatedata()


    }

    if(autoupdate?.isNotEmpty() == true){
        if(autoupdate!![0].version_number.toString()!=version){
            updateavailable=true
        }

    }



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
        LazyColumn(modifier= Modifier
            .fillMaxSize()
            .padding(it)){
            item{
                ListItem({
                    Row(modifier=Modifier.fillMaxSize(), verticalAlignment = Alignment.CenterVertically) {


                        Column {
                            Text("Version")
                            Text("2.0", fontWeight = FontWeight.ExtraLight)
                        }
                        Spacer(Modifier.weight(1f))
                        if(updateavailable) {
                            Icon(
                                imageVector = Icons.Filled.Circle,
                                contentDescription = null,
                                tint = Color.Red,
                                modifier = Modifier.size(10.dp)
                            )
                        }
                    }
                },
                    modifier = Modifier.clickable {
                        if(autoupdate?.isNotEmpty() == true){
                            showupdatebottom=true
                        }
                        else{
                            Toast.makeText(context,"Unable to get data try after sometime",Toast.LENGTH_SHORT).show()
                        }

                    }
                )
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
    if(showupdatebottom){
        ModalBottomSheet(onDismissRequest = { showupdatebottom=false }) {

            Column(modifier= Modifier
                .fillMaxWidth()
                .padding(bottom = 30.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                if(updateavailable) {
                    Text("New update available", style = MaterialTheme.typography.headlineSmall)
                }
                else{
                    Text("You are up-to-date",style = MaterialTheme.typography.headlineSmall)
                }

            }
            Row {
                Text(text = "Version: ", style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(start = 10.dp,bottom=10.dp))
                Text(text = autoupdate?.get(0)?.version_number.toString(),style = MaterialTheme.typography.titleMedium)
            }
            Column {
                autoupdate?.get(0)?.let {
                    MarkdownText(
                        markdown = it.new_features,
                        color = MaterialTheme.colorScheme.onBackground,
                        textAlign = TextAlign.Justify,
                        modifier = Modifier.padding(bottom = 10.dp, start = 10.dp)


                    )
                }
            }
            if(updateavailable) {

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(end = 10.dp, bottom = 20.dp), horizontalAlignment = Alignment.End
                ) {
                    val context = LocalContext.current
                    val intent = remember {
                        Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse("${autoupdate?.get(0)?.download_link}")
                        )
                    }
                    Button(onClick = { context.startActivity(intent) }) {
                        Text("Update")

                    }

                }
            }
        }
    }
}