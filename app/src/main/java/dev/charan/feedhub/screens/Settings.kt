package dev.charan.feedhub.screens

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import dev.charan.feedhub.screens.Items.CheckListItem
import dev.charan.feedhub.Utils.SharedPref
import dev.charan.feedhub.screens.Items.FeedViewListItem
import dev.charan.feedhub.screens.Items.SettingsListItem

import dev.charan.feedhub.ui.theme.RSSparserTheme


@OptIn(ExperimentalMaterial3Api::class)
@Composable

fun Settings(navHostController: NavHostController) {
    val context = LocalContext.current

    val SharedPref= SharedPref(context)
    val isHapticEnabled by remember {
        mutableStateOf(SharedPref.hapticEnabled)
    }

    val Scroll = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()





    RSSparserTheme() {

        Scaffold(modifier= Modifier
            .fillMaxSize()
            .nestedScroll(Scroll.nestedScrollConnection),topBar = {
            LargeTopAppBar(title = { Text(text = "Settings") }, scrollBehavior = Scroll, navigationIcon = {
                IconButton(onClick = {
                    navHostController.popBackStack()
                }){
                    Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                }

            })
        }) {


            LazyColumn(
                modifier = Modifier
                    .padding(it)
                    .fillMaxSize()

            ) {

                item {
                    SettingsListItem(label = "Feed Websites",Modifier=Modifier) {
                        navHostController.navigate(dev.charan.feedhub.Navigation.Destinations.feeds.route)

                    }
                    HorizontalDivider(modifier=Modifier.padding(start=10.dp,end=10.dp))
                    SettingsListItem(label = "Change app Icon",Modifier=Modifier) {
                        navHostController.navigate(dev.charan.feedhub.Navigation.Destinations.appiconchange.route)

                    }
                    HorizontalDivider(modifier=Modifier.padding(start=10.dp,end=10.dp))
                    CheckListItem(label = "Show scroll to top", isChecked = SharedPref.showScrollToTop, isHapticEnabled = SharedPref.hapticEnabled,Modifier) {
                        SharedPref.showScrollToTop=it

                    }

                    HorizontalDivider(modifier=Modifier.padding(start=10.dp,end=10.dp))
                    FeedViewListItem(SharedPref = SharedPref)



                    HorizontalDivider(modifier=Modifier.padding(start=10.dp,end=10.dp))
                    CheckListItem(label = "In-app browser", isChecked = SharedPref.inAppBrowser, isHapticEnabled = isHapticEnabled,Modifier) {
                        SharedPref.inAppBrowser=it

                    }
                    HorizontalDivider(modifier=Modifier.padding(start=10.dp,end=10.dp))
                    SettingsListItem(label = "Account",Modifier=Modifier) {
                        navHostController.navigate(dev.charan.feedhub.Navigation.Destinations.account.route)

                    }


                    HorizontalDivider(modifier=Modifier.padding(start=10.dp,end=10.dp))
                    SettingsListItem(label = "About App",Modifier=Modifier) {
                        navHostController.navigate(dev.charan.feedhub.Navigation.Destinations.aboutapp.route)
                    }





                }


            }
        }

    }
}


