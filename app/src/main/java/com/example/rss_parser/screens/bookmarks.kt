package com.example.rss_parser.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshContainer
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.rss_parser.Utils.AppUtils
import com.example.rss_parser.screens.Items.NoInternetOnTopBar
import com.example.rss_parser.Utils.ProcessState
import com.example.rss_parser.Utils.Connectionstatus
import com.example.rss_parser.Utils.connectivityState
import com.example.rss_parser.screens.Items.CompactListItem
import com.example.rss_parser.ui.theme.RSSparserTheme
import com.example.rss_parser.viewmodel.viewmodel
import kotlinx.coroutines.ExperimentalCoroutinesApi

@OptIn(ExperimentalMaterial3Api::class, ExperimentalCoroutinesApi::class)
@Composable

fun BookmarkScreen(navHostController: NavHostController) {
    val context = LocalContext.current



    val Scroll = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    val pulltorefreshState= rememberPullToRefreshState()
    val viewModel = viewModel<viewmodel>(
        factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return viewmodel(
                    context
                ) as T
            }
        }
    )
    val bookmarks by viewModel.bookmarkData.collectAsState()
    var isRefreshing by remember {
        mutableStateOf(false)
    }
    val lifecycle= LocalLifecycleOwner.current

    val connection by connectivityState()

    val isConnected = connection === Connectionstatus.Available
    LaunchedEffect(key1=bookmarks){
        if(isConnected){
                viewModel.getBookmarkDataFromSupabase().observe(lifecycle){
                    when(it){
                        is ProcessState.Error -> {

                        }
                        ProcessState.Loading -> {
                            isRefreshing=true
                        }
                        ProcessState.Success -> {
                            isRefreshing=false
                        }
                    }

                }

    }

    }

    RSSparserTheme() {


        Scaffold(modifier = Modifier
            .fillMaxSize()
            .nestedScroll(Scroll.nestedScrollConnection),
            topBar = {
                LargeTopAppBar(title = { Text(text = "Bookmarks") }, scrollBehavior = Scroll, navigationIcon = {
                    IconButton(
                        onClick = { navHostController.popBackStack()}) {
                        Icon(imageVector = Icons.AutoMirrored.Outlined.ArrowBack,contentDescription = null)

                    }

                },
                    actions = {
                        NoInternetOnTopBar(isConnected = isConnected, context = context)
                    }

                )
            }


        ) {



                    Box(modifier = Modifier
                        .padding(it)
                        .fillMaxSize()

                        .nestedScroll(pulltorefreshState.nestedScrollConnection)
                    ) {
                        if (bookmarks?.isEmpty() == true) {
                            LazyColumn(
                                modifier = Modifier.fillMaxSize(),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                item {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .align(Alignment.Center),
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.Center
                                    ) {
                                        Text("No bookmarks added")

                                    }
                                }
                            }
                        } else {
                            LazyColumn(modifier = Modifier.fillMaxSize()) {
                                bookmarks?.let { it1 ->
                                    items(it1.size) {
                                        CompactListItem(AppUtils.bookmarkToFeedMapper(bookmarks[it]))

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

                        if(pulltorefreshState.isRefreshing){

                            LaunchedEffect(true) {
                                isRefreshing=true
                                if(isConnected) {
                                    viewModel.getBookmarkDataFromSupabase().observe(lifecycle){
                                        when(it){
                                            is ProcessState.Error -> {

                                            }
                                            ProcessState.Loading -> {

                                            }
                                            ProcessState.Success -> {
                                                isRefreshing=false
                                            }
                                        }
                                    }
                                }
                                else{
                                    isRefreshing=false
                                }

                            }

                        }
                        LaunchedEffect(key1 = isRefreshing) {
                            if(isRefreshing){
                                pulltorefreshState.startRefresh()
                            }
                            else{
                                pulltorefreshState.endRefresh()
                            }

                        }
                        PullToRefreshContainer(state = pulltorefreshState,modifier=Modifier.align(
                            Alignment.TopCenter))
                    }

            }

        }
    }
