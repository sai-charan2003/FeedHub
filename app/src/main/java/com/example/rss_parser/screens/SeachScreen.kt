package com.example.rss_parser.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.DockedSearchBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.rss_parser.database.feeddatabase.feeds
import com.example.rss_parser.screens.Items.CompactListItem
import com.example.rss_parser.viewmodel.viewmodel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class,
    ExperimentalFoundationApi::class
)
@Composable
fun SearchScreen(navHostController: NavHostController) {
    var query by rememberSaveable {
        mutableStateOf("")
    }

    var active by rememberSaveable {
        mutableStateOf(true)
    }

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
    var isclicked by remember {
        mutableStateOf(false)
    }
    val focusRequester = remember { FocusRequester()}
    val bookmarkdata by viewModel.bookmarkData.collectAsState()
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


                }
            }

            LazyColumn {


                items(result.size){list->
                    var loading by remember {
                        mutableStateOf(false)

                    }
                    CompactListItem(sortedFeedsData = result[list])


                }

            }


        }

    }
}