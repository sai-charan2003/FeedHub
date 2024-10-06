    package dev.charan.feedhub.screens
    
    import android.util.Log
    import androidx.compose.animation.animateContentSize
    import androidx.compose.foundation.ExperimentalFoundationApi
    import androidx.compose.foundation.Image
    import androidx.compose.foundation.layout.Arrangement
    import androidx.compose.foundation.layout.Box
    import androidx.compose.foundation.layout.Column
    import androidx.compose.foundation.layout.Row
    import androidx.compose.foundation.layout.fillMaxSize
    import androidx.compose.foundation.layout.padding
    import androidx.compose.foundation.layout.requiredSize
    import androidx.compose.foundation.layout.size
    import androidx.compose.foundation.lazy.LazyColumn
    import androidx.compose.foundation.lazy.rememberLazyListState
    import androidx.compose.foundation.shape.CircleShape
    import androidx.compose.foundation.shape.RoundedCornerShape
    import androidx.compose.material.icons.Icons
    import androidx.compose.material.icons.filled.Settings
    import androidx.compose.material.icons.outlined.AccountCircle
    import androidx.compose.material.icons.outlined.MarkEmailRead
    import androidx.compose.material.icons.outlined.Markunread
    import androidx.compose.material.icons.outlined.Menu
    import androidx.compose.material.icons.outlined.MoreVert
    import androidx.compose.material.icons.outlined.Newspaper
    import androidx.compose.material.icons.outlined.Search
    import androidx.compose.material3.Button
    import androidx.compose.material3.DrawerValue
    import androidx.compose.material3.ExperimentalMaterial3Api
    import androidx.compose.material3.HorizontalDivider
    import androidx.compose.material3.Icon
    import androidx.compose.material3.IconButton
    import androidx.compose.material3.MaterialTheme
    import androidx.compose.material3.ModalDrawerSheet
    import androidx.compose.material3.ModalNavigationDrawer
    import androidx.compose.material3.NavigationDrawerItem
    import androidx.compose.material3.Scaffold
    import androidx.compose.material3.Surface
    import androidx.compose.material3.Text
    import androidx.compose.material3.TopAppBar
    import androidx.compose.material3.TopAppBarDefaults
    import androidx.compose.material3.pulltorefresh.PullToRefreshContainer
    import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
    import androidx.compose.material3.rememberDrawerState
    import androidx.compose.runtime.Composable
    import androidx.compose.runtime.LaunchedEffect
    import androidx.compose.runtime.collectAsState
    import androidx.compose.runtime.derivedStateOf
    import androidx.compose.runtime.getValue
    import androidx.compose.runtime.livedata.observeAsState
    import androidx.compose.runtime.mutableStateOf
    import androidx.compose.runtime.remember
    import androidx.compose.runtime.rememberCoroutineScope
    import androidx.compose.runtime.saveable.rememberSaveable

    import androidx.compose.runtime.setValue
    import androidx.compose.ui.Alignment
    import androidx.compose.ui.Modifier
    import androidx.compose.ui.draw.clip
    import androidx.compose.ui.graphics.ColorFilter
    import androidx.compose.ui.graphics.graphicsLayer
    import androidx.compose.ui.graphics.vector.ImageVector
    import androidx.compose.ui.input.nestedscroll.nestedScroll
    import androidx.compose.ui.layout.ContentScale
    import androidx.compose.ui.platform.LocalContext
    import androidx.compose.ui.text.style.TextOverflow
    import androidx.compose.ui.tooling.preview.Preview
    import androidx.compose.ui.unit.dp
    import androidx.lifecycle.ViewModelProvider
    import androidx.lifecycle.compose.LocalLifecycleOwner
    import androidx.lifecycle.viewmodel.compose.viewModel
    import androidx.navigation.NavHostController
    import androidx.navigation.compose.rememberNavController
    import coil.compose.AsyncImage
    import dev.charan.feedhub.Utils.AddFeedsWorker
    import dev.charan.feedhub.Utils.AppConstants
    import dev.charan.feedhub.Utils.Connectionstatus
    import dev.charan.feedhub.Utils.DateTimeFormatter.convertToMillisecondsEpoch
    import dev.charan.feedhub.Utils.DeleteFeedsWorker
    import dev.charan.feedhub.Utils.ProcessState
    import dev.charan.feedhub.Utils.SharedPref
    import dev.charan.feedhub.Utils.connectivityState
    import dev.charan.feedhub.screens.Components.scrollToTop
    import dev.charan.feedhub.screens.Items.CompactListItem
    import dev.charan.feedhub.screens.Items.HomeScreenDropDownMenu
    import dev.charan.feedhub.screens.Items.NoInternetOnTopBar
    import dev.charan.feedhub.screens.Items.NonCompactListItem
    import dev.charan.feedhub.supabase.client.supabaseclient
    import dev.charan.feedhub.ui.theme.RSSparserTheme
    import dev.charan.feedhub.viewmodel.viewmodel

    import io.github.jan.supabase.gotrue.auth

    import kotlinx.coroutines.ExperimentalCoroutinesApi
    import kotlinx.coroutines.launch


    @OptIn(ExperimentalMaterial3Api::class, ExperimentalCoroutinesApi::class,
        ExperimentalFoundationApi::class
    )
    @Composable
    fun HomeScreen(navHostController: NavHostController) {
        val connection by connectivityState()

        val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)

        val isConnected = connection === Connectionstatus.Available

        val context = LocalContext.current

        val coroutinescope = rememberCoroutineScope()

        val viewModel = viewModel<viewmodel>(
            factory = object : ViewModelProvider.Factory {
                override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                    return viewmodel(
                        context
                    ) as T
                }
            }
        )
        val listState = rememberLazyListState()

        val showScrollToTop by remember { derivedStateOf { listState.firstVisibleItemIndex > 2 } }
        val DistinctWebsiteTitles=viewModel.distinctWebsiteTitles.collectAsState()



        var finalloading by remember {
            mutableStateOf(true)
        }

        var selectedCategory by rememberSaveable { mutableStateOf<String?>(null) }

        var isRefreshing by rememberSaveable {
            mutableStateOf(true)
        }


        val pulltorefreshState = rememberPullToRefreshState()

        val Scroll = TopAppBarDefaults.pinnedScrollBehavior()

        var showdropdownmenu by remember { mutableStateOf(false) }

        var SharedPref = SharedPref(context)

        val urls by viewModel.allwebsites.collectAsState()

        val feedsdata by viewModel.allfeeds.observeAsState()

        var username by remember {
            mutableStateOf("")
        }
        var identity by remember {
            mutableStateOf("")
        }

        val lifecycle = LocalLifecycleOwner.current
        var sortedFeedsData =
            feedsdata?.let { feeds ->
                if (feeds.isNotEmpty()) {
                    feeds.mapIndexed { index, feed ->
                        Triple(index, feed, convertToMillisecondsEpoch(feed.date!!))
                    }.sortedByDescending { (_, _, dateInMillis) -> dateInMillis }
                        .map { (_, feed, _) -> feed }
                } else {
                    emptyList()
                }
            }

        var selectedItemIndex by rememberSaveable {
            mutableStateOf(0)
        }
        if(!SharedPref.isAnonymousSignin) {


            identity =
                supabaseclient.client.auth.currentUserOrNull()?.identities?.get(0)?.identityData?.get(
                    "avatar_url"
                ).toString().substringAfter("\"").substringBefore("\"")
            username =
                supabaseclient.client.auth.currentUserOrNull()?.identities?.get(0)?.identityData?.get(
                    "full_name"
                ).toString().substringAfter("\"").substringBefore("\"")
        }

        val email = supabaseclient.client.auth.currentUserOrNull()?.email





        val navigationItems = mutableListOf(
            NavigationItems(
                title="All Feeds",
                selectedIcon = Icons.Outlined.Newspaper,
                badgeCount = sortedFeedsData?.size

            ),
            NavigationItems(
                title="Unread",
                selectedIcon = Icons.Outlined.Markunread,
                badgeCount = sortedFeedsData?.filter{it.opened=="false"}?.size
            ),
            NavigationItems(
                title="Read",
                selectedIcon = Icons.Outlined.MarkEmailRead,
                badgeCount = sortedFeedsData?.filter{it.opened=="true"}?.size
            )


        )
        val NavigationItemsForWebsites=DistinctWebsiteTitles.value.map {websitedata->
            NavigationItems(
                title=websitedata?.websiteTitle!!,
                websiteFavicon = websitedata?.websiteFavicon,
                badgeCount = sortedFeedsData?.filter{it.websiteTitle==websitedata?.websiteTitle}?.size,


            )
        }

        if (selectedCategory != null) {
            sortedFeedsData = sortedFeedsData?.filter {
                selectedCategory ==
                        it.websiteTitle

            }


        }
        when(selectedItemIndex){
            0 -> {
                sortedFeedsData=feedsdata?.let { feeds ->
                    if (feeds.isNotEmpty()) {
                        feeds.mapIndexed { index, feed ->
                            Triple(index, feed, convertToMillisecondsEpoch(feed.date!!))
                        }.sortedByDescending { (_, _, dateInMillis) -> dateInMillis }
                            .map { (_, feed, _) -> feed }
                    } else {
                        emptyList()
                    }
                }
            }
            1 -> {
                sortedFeedsData=sortedFeedsData?.filter {
                    it.opened=="false"
                }
            }
            2 -> {
                sortedFeedsData=sortedFeedsData?.filter {
                    it.opened=="true"
                }
            }
        }


        LaunchedEffect(urls) {
            launch {
                if (isConnected) {

                    viewModel.getCurrentSessionToken(context)
//                    viewModel.getFeedWebsitesFromSupabase().observe(lifecycle){
//                        isRefreshing = when(it){
//                            is ProcessState.Error -> {
//                                false
//
//                            }
//
//                            ProcessState.Loading -> {
//                                true
//                            }
//
//                            ProcessState.Success -> {
//                                false
//                            }
//                        }
//                    }
//                    viewModel.getBookmarkDataFromSupabase()
                    //viewModel.getImageUrlFromWebsite("https://news.google.com/articles/CBMiZmh0dHBzOi8vaW5kaWFuZXhwcmVzcy5jb20vYXJ0aWNsZS9jaXRpZXMvZGVsaGkvYWFwLWFydmluZC1rZWpyaXdhbC1sb2stc2FiaGEtcG9sbHMtZ3VhcmFudGVlcy05MzIzNTA0L9IBAA?hl=en-IN&gl=IN&ceid=IN%3Aen")



                }
                DeleteFeedsWorker.setup()
                AddFeedsWorker.setup()

            }


        }



        RSSparserTheme {
            Surface(shadowElevation = 10.dp) {

                ModalNavigationDrawer(
                    drawerState = drawerState,
                    drawerContent = {
                        ModalDrawerSheet {
                            Column(
                                modifier = Modifier
                                    .padding(start=5.dp,end=5.dp)


                            ) {

                                    Text("Feed Hub", style = MaterialTheme.typography.displaySmall,modifier=Modifier.padding(start=10.dp,top=15.dp,bottom=5.dp))



                                    HorizontalDivider(
                                        modifier = Modifier.padding(
                                            start = 10.dp,
                                            end = 10.dp,
                                            bottom = 10.dp
                                        )
                                    )


                                LazyColumn(
                                    modifier = Modifier.weight(1f)
                                ) {
                                    items(navigationItems.size) { index ->
                                        val item = navigationItems[index]
                                        NavigationDrawerItem(
                                            label = { Text(text = item.title) },
                                            selected = index == selectedItemIndex,
                                            icon = {
                                                if (item.selectedIcon != null) {
                                                    Image(
                                                        imageVector = item.selectedIcon,
                                                        contentDescription = null,
                                                        colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onSurface)
                                                    )
                                                } else {
                                                    AsyncImage(
                                                        model = item.websiteFavicon,
                                                        contentDescription = null,
                                                        modifier = Modifier
                                                            .requiredSize(24.dp)
                                                            .clip(RoundedCornerShape(20.dp)),
                                                    )
                                                }
                                            },
                                            badge = {
                                                    Text(item.badgeCount.toString())
                                            },
                                            onClick = {
                                                coroutinescope.launch {
                                                    drawerState.close()
                                                }
                                                selectedCategory = null
                                                selectedItemIndex = index
                                            }
                                        )
                                    }
                                    item {
                                        HorizontalDivider(
                                            modifier = Modifier.padding(
                                                start = 20.dp,
                                                end = 20.dp,
                                                top = 10.dp,
                                                bottom = 10.dp
                                            )
                                        )
                                    }
                                    items(NavigationItemsForWebsites.size) { index ->
                                        val item = NavigationItemsForWebsites[index]
                                        NavigationDrawerItem(
                                            label = {
                                                Text(
                                                    text = item.title,
                                                    maxLines = 1,
                                                    overflow = TextOverflow.Ellipsis
                                                )
                                            },
                                            selected = (item.title == selectedCategory),
                                            icon = {
                                                if (item.selectedIcon != null) {
                                                    Image(
                                                        imageVector = item.selectedIcon,
                                                        contentDescription = null
                                                    )
                                                } else {
                                                    AsyncImage(
                                                        model = item.websiteFavicon,
                                                        contentDescription = null,
                                                        modifier = Modifier
                                                            .requiredSize(24.dp)
                                                            .clip(RoundedCornerShape(20.dp)),
                                                    )
                                                }
                                            },
                                            badge = {
                                                Row(verticalAlignment = Alignment.CenterVertically) {
                                                    Text(text = item.badgeCount.toString())
                                                }
                                            },
                                            onClick = {
                                                coroutinescope.launch {
                                                    drawerState.close()
                                                }
                                                selectedCategory = item.title
                                                selectedItemIndex = -1
                                            }
                                        )
                                    }
                                }
                                HorizontalDivider(
                                    modifier = Modifier.padding(
                                        start = 20.dp,
                                        end = 20.dp,
                                        top = 10.dp,
                                        bottom = 10.dp
                                    )
                                )
                                NavigationDrawerItem(
                                    label = {
                                        if(!username.isNullOrEmpty()){
                                            Text(username, overflow = TextOverflow.Ellipsis,modifier= Modifier
                                                .weight(1f)
                                                .padding(start = 10.dp))

                                        } else {

                                            Text(
                                                email.toString(),
                                                overflow = TextOverflow.Ellipsis,
                                                modifier = Modifier
                                                    .weight(1f)
                                                    .padding(start = 10.dp)
                                            )
                                        }
                                    },
                                    icon = {
                                        if(!identity.isNullOrEmpty()) {
                                            Box(modifier=Modifier.graphicsLayer {
                                                this.shape= CircleShape
                                                this.clip=true

                                            }

                                            ){
                                                AsyncImage(
                                                    model = identity,
                                                    contentDescription = null,
                                                    modifier=Modifier.size(30.dp),
                                                    contentScale = ContentScale.Fit
                                                )

                                            }

                                        } else {
                                            Icon(
                                                imageVector = Icons.Outlined.AccountCircle,
                                                contentDescription = null,
                                                tint = MaterialTheme.colorScheme.onSurface
                                            )
                                        }

                                    },
                                    badge = {
                                        Icon(Icons.Filled.Settings,null)

                                    },
                                    selected = false,
                                    onClick = {
                                        navHostController.navigate(dev.charan.feedhub.Navigation.Destinations.settings.route)
                                    }
                                )















                            }
                        }
                    }
                )

                {

                    Scaffold(
                        modifier = Modifier
                            .fillMaxSize()
                            .nestedScroll(Scroll.nestedScrollConnection),

                        topBar = {
                            TopAppBar(
                                title = { Text("My Feeds") },
                                scrollBehavior = Scroll,
                                navigationIcon = {
                                                 IconButton(onClick = { coroutinescope.launch{ drawerState.open() } }) {
                                                     Icon(imageVector = Icons.Outlined.Menu, contentDescription = null)
                                                     
                                                 }
                                },
                                actions = {
                                    NoInternetOnTopBar(isConnected = isConnected, context = context)

                                    IconButton(onClick = { navHostController.navigate(dev.charan.feedhub.Navigation.Destinations.search.route) }) {
                                        Icon(
                                            imageVector = Icons.Outlined.Search,
                                            contentDescription = null
                                        )

                                    }
                                    IconButton(onClick = { showdropdownmenu = true }) {
                                        Icon(
                                            imageVector = Icons.Outlined.MoreVert,
                                            contentDescription = "more"
                                        )
                                    }
                                    HomeScreenDropDownMenu(
                                        showdropdownmenu = showdropdownmenu,
                                        onClick = { showdropdownmenu = false },
                                        navHostController = navHostController
                                    )


                                }
                            )
                        },


                        ) {


                        Box(
                            modifier = Modifier
                                .padding(it)
                                .fillMaxSize()
                                .nestedScroll(pulltorefreshState.nestedScrollConnection)
                        ) {

                            if (sortedFeedsData?.isEmpty() == true) {
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
                                            Text("No feeds")
                                            Button(
                                                onClick = {
                                                    navHostController.navigate(dev.charan.feedhub.Navigation.Destinations.addnewfeed.route)
                                                },
                                                modifier = Modifier.padding(top = 16.dp)
                                            ) {
                                                Text(text = "Add feed")
                                            }
                                        }
                                    }
                                }

                            } else {


                                LazyColumn(
                                    state = listState,
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .align(Alignment.Center)
                                        .animateContentSize(),


                                    ) {



                                    sortedFeedsData?.let { it1 ->
                                        items(it1.size) { list ->
                                            if (SharedPref.feedView == AppConstants.FeedView.CARD) {
                                                NonCompactListItem(sortedFeedsData = sortedFeedsData[list]){

                                                    if(it.opened=="true"){

                                                        viewModel.Updateopened(it.id!!,"false")

                                                    }
                                                    else{
                                                        viewModel.Updateopened(it.id!!,"true")
                                                    }

                                                }
                                            } else {
                                                CompactListItem(sortedFeedsData = sortedFeedsData[list])
                                            }
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
                            if (pulltorefreshState.isRefreshing) {
                                LaunchedEffect(true) {
                                    if (isConnected) {
                                        viewModel.getFeedWebsitesFromSupabase().observe(lifecycle) {
                                            Log.d("TAG", "HomeScreen: $it")
                                            isRefreshing = when (it) {
                                                is ProcessState.Error -> {
                                                    false

                                                }

                                                ProcessState.Loading -> {
                                                    true

                                                }

                                                ProcessState.Success -> {
                                                    false
                                                }
                                            }
                                        }
                                        viewModel.getBookmarkDataFromSupabase()
                                    } else {
                                        isRefreshing = false
                                    }

                                }

                            }
                            LaunchedEffect(key1 = isRefreshing) {
                                if (isRefreshing) {
                                    pulltorefreshState.startRefresh()
                                } else {
                                    pulltorefreshState.endRefresh()
                                }

                            }

                            PullToRefreshContainer(
                                state = pulltorefreshState, modifier = Modifier.align(
                                    Alignment.TopCenter
                                )
                            )
                            if (SharedPref.showScrollToTop) {
                                scrollToTop(listState, showScrollToTop, coroutinescope)


                            }
                        }
                    }
                }


                    }


            }
        }


    @Preview
    @Composable
    fun Preview() {
        HomeScreen(navHostController = rememberNavController())
    }

    data class NavigationItems(
        val title:String,
        val selectedIcon:ImageVector?=null,
        val websiteFavicon:String?=null,
        val badgeCount:Int?=null,

    )





    

    
    
    
    
    
    
