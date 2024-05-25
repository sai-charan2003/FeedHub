package com.example.rss_parser.screens

import android.util.Log
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Done
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DockedSearchBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.rss_parser.Utils.ProcessState
import com.example.rss_parser.R
import com.example.rss_parser.Utils.SharedPref
import com.example.rss_parser.Utils.Connectionstatus
import com.example.rss_parser.Utils.DistinctLinkForGoogleNewsState
import com.example.rss_parser.Utils.GoogleNews
import com.example.rss_parser.Utils.connectivityState
import com.example.rss_parser.screens.Items.CheckListItem

import com.example.rss_parser.viewmodel.viewmodel

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalCoroutinesApi::class)
@Composable

fun AddFeed(navHostController: NavHostController) {


    val context = LocalContext.current
    val scroll = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    val coroutine = rememberCoroutineScope()
    val haptic = LocalHapticFeedback.current

    var isloading by remember {
        mutableStateOf(false)
    }

    var isGoogleNews by remember {
        mutableStateOf(false)
    }

    var label by remember {
        mutableStateOf("Feed URL")
    }
    var feedlink by remember {
        mutableStateOf("")
    }
    var isEntered by remember {
        mutableStateOf(false)
    }


    var showerror by remember {
        mutableStateOf(false)
    }

    val viewModel = viewModel<viewmodel>(
        factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return viewmodel(
                    context
                ) as T
            }
        }

    )
    val connection by connectivityState()

    val isConnected = connection === Connectionstatus.Available
    val lifcycle = LocalLifecycleOwner.current
    val SharedPref = SharedPref(context)
    val ishapticenabled = SharedPref.hapticEnabled
    val url by viewModel.allwebsites.collectAsState()
    var googleNewsResults by remember {
        mutableStateOf<List<GoogleNews?>>(emptyList())
    }
    Scaffold(modifier = Modifier
        .fillMaxSize()
        .nestedScroll(scroll.nestedScrollConnection)
        , topBar = {
        LargeTopAppBar(title = {
            Text("Add Feed")

        },
            scrollBehavior = scroll,
            navigationIcon = {
                IconButton(onClick = { navHostController.popBackStack() }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                        contentDescription = "Back"
                    )

                }
            }


        )
    },

    ) {

        Column(
            modifier = Modifier
                .padding(it)
                .fillMaxWidth(),


        ) {
            if (isConnected) {

                DockedSearchBar(
                    inputField = {
                                 SearchBarDefaults.InputField(
                                     query = feedlink.trim(),
                                     onQueryChange = {
                                                     feedlink=it.trim()
                                         showerror=false
                                     },
                                     onSearch = {
                                         isEntered=true
                                         isloading=true
                                         googleNewsResults= emptyList()
                                     },
                                     expanded = false,
                                     onExpandedChange = {

                                     },
                                     placeholder = {
                                                   Text("Search or Add RSS Feed")
                                     },
                                     trailingIcon = {
                                         Row{

                                             AnimatedVisibility(visible = feedlink.isNotEmpty()) {
                                                 IconButton(onClick = { feedlink="" }) {
                                                     Icon(Icons.Default.Clear, contentDescription = null)

                                                 }

                                             }


                                                 IconButton(onClick = { isEntered=true
                                                     isloading=true
                                                     googleNewsResults= emptyList() }) {
                                                     if(isloading) {
                                                         CircularProgressIndicator(
                                                             modifier = Modifier
                                                                 .size(
                                                                     24.dp
                                                                 )
                                                                 .align(Alignment.CenterVertically),
                                                             strokeCap = StrokeCap.Round
                                                         )
                                                     }
                                                     else {
                                                         Icon(
                                                             Icons.Default.Search,
                                                             contentDescription = null
                                                         )
                                                     }

                                                 }

                                         }
                                     },
                                     modifier=Modifier.fillMaxWidth()




                                 )
                    },
                    expanded = false,
                    onExpandedChange = {},
                    modifier= Modifier
                        .fillMaxWidth()
                        .padding(start = 10.dp, end = 10.dp,top=10.dp,bottom=10.dp)

                ) {

                }
                if (showerror) {
                    Text(
                        text = "No feeds found",
                        color = Color(0xffFF6969),
                        style = MaterialTheme.typography.labelLarge,
                        modifier = Modifier.padding(start = 40.dp)
                    )
                }



                    if(googleNewsResults.isNotEmpty()) {
                        LazyColumn {
                            items(googleNewsResults.size) {
                                topfeeds(
                                    websitename = googleNewsResults[it]?.SourceName!!,
                                    websiteurl = googleNewsResults[it]?.URL!!,
                                    rssurl = "https://news.google.com/rss/search?q=${
                                        googleNewsResults[it]?.URL?.substringAfter(
                                            "https://"
                                        )
                                    }&hl=en-IN&gl=IN&ceid=IN:en"
                                )
                            }
                        }
                    }





            }
        }
        if(isEntered){

            LaunchedEffect(Unit) {
                val checkforerror = viewModel.fetchrssfeed(feedlink)
                if(checkforerror==null){
                    viewModel.getDistinctSourceURL(feedlink).observe(lifcycle){
                        when(it){
                            is DistinctLinkForGoogleNewsState.Error -> {
                                isloading=false
                                isEntered=false

                            }
                            DistinctLinkForGoogleNewsState.Loading -> {
                                isloading=true
                            }
                            is DistinctLinkForGoogleNewsState.Success -> {
                                isloading=false
                                if(it.googleNewsData.isEmpty()){
                                    showerror=true
                                }
                                googleNewsResults=it.googleNewsData
                                isEntered=false
                            }

                            else -> {

                            }
                        }
                    }

                } else{
                    if(feedlink.isEmpty()){
                        isloading=false
                    }
                    else {
                        Log.d("TAG", "AddFeed: $feedlink")
                        viewModel.InsertWebsiteIntoSupaBase(feedlink, context)
                            .observe(lifcycle) {
                                when (it) {
                                    is ProcessState.Error -> {
                                        Toast.makeText(context, it.error, Toast.LENGTH_LONG)
                                            .show()
                                        isloading = false
                                        isEntered=false

                                    }

                                    ProcessState.Loading -> {
                                        isloading = true
                                    }

                                    ProcessState.Success -> {
                                        Toast.makeText(
                                            context,
                                            "Feed added",
                                            Toast.LENGTH_SHORT
                                        )
                                            .show()
                                        showerror = false
                                        if (ishapticenabled) {
                                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                        }
                                        viewModel.getRssDataForSingleWebsite(feedlink)
                                        isloading = false
                                        feedlink = ""
                                        isEntered=false

                                    }

                                    else -> {
                                        Log.d("TAG", "addfeed: $it")
                                    }
                                }
                            }
                    }

                }
            }
        }
    }
}





















