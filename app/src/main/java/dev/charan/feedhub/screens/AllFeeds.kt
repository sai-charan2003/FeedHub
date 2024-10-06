

package dev.charan.feedhub.screens

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import dev.charan.feedhub.Utils.ProcessState
import dev.charan.feedhub.Utils.SharedPref
import dev.charan.feedhub.Utils.Connectionstatus
import dev.charan.feedhub.Utils.connectivityState
import dev.charan.feedhub.viewmodel.viewmodel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalCoroutinesApi::class)
@Composable

fun AllFeeds(navHostController: NavHostController) {



    val context = LocalContext.current
    val haptic= LocalHapticFeedback.current
    var showwarning by remember{
        mutableStateOf(false)
    }
    val SharedPref= SharedPref(context)
    val ishapticenabled=SharedPref.hapticEnabled

    val viewModel = viewModel<viewmodel>(
        factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return viewmodel(
                    context
                ) as T
            }
        }

    )
    val lifecycle= LocalLifecycleOwner.current

    val iscontentready by remember {
        mutableStateOf(false)
    }
    var showdropdownmenu by remember {
        mutableStateOf(false)
    }
    val coroutine= rememberCoroutineScope()
    val connection by connectivityState()
    val isdeleted by remember {
        mutableStateOf(false)
    }

    val isConnected = connection === Connectionstatus.Available
    val websites by viewModel.distinctWebsiteData.collectAsState()
        Scaffold(
            topBar = {
                TopAppBar(title = { Text(text = "Feed Websites") }, navigationIcon = {
                    IconButton(
                        onClick = { navHostController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Default.ArrowBack,
                            contentDescription = null
                        )

                    }
                },
                    actions = {
                        IconButton(onClick = { showdropdownmenu = true }) {
                            Icon(
                                imageVector = Icons.Outlined.MoreVert,
                                contentDescription = null
                            )
                        }
                        DropdownMenu(
                            expanded = showdropdownmenu,
                            onDismissRequest = { showdropdownmenu = false }) {
                            DropdownMenuItem(
                                text = { Text("Add Feed") },
                                onClick = {
                                    showdropdownmenu =false;
                                    navHostController.navigate(dev.charan.feedhub.Navigation.Destinations.addnewfeed.route)


                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Clear Websites") },
                                onClick = {
                                    showdropdownmenu =false;
                                    showwarning=true;

                                }
                            )



                        }

                    }

                )
            },


        ) {
                    if (websites?.isEmpty() == true) {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text("No Feed Websites Found")


                        }
                    }


                    LazyColumn(modifier = Modifier.padding(it)) {
                        websites?.size?.let { it1 ->
                            items(it1) {list->
                                var loading by remember {
                                    mutableStateOf(false)
                                }

                                ListItem(

                                    {

                                        Row (verticalAlignment = Alignment.CenterVertically){
                                            Box(
                                                modifier = Modifier.graphicsLayer {
                                                    this.shape = CircleShape
                                                    this.clip = true
                                                }
                                                ,
                                                contentAlignment = Alignment.Center
                                            ) {
                                                AsyncImage(
                                                    model = websites[list]?.websiteFavicon,
                                                    contentDescription = null,
                                                    modifier = Modifier
                                                        .requiredSize(15.dp)


                                                    ,
                                                    contentScale = ContentScale.Fit ,

                                                )
                                            }
                                            Text(
                                                text = websites[list]?.websiteTitle!!,
                                                modifier = Modifier
                                                    .padding(start=5.dp)
                                                    .weight(1f),
                                                overflow = TextOverflow.Ellipsis,

                                                maxLines = 1
                                            )

                                            IconButton(onClick = {
                                                val website=websites[list]?.website
                                                coroutine.launch {

                                                    viewModel.deleteWebsiteFromSupabase(website!!,context).observe(lifecycle){
                                                        when(it) {
                                                            is ProcessState.Error -> {
                                                                Toast.makeText(context,it.error,Toast.LENGTH_LONG).show()
                                                                loading = false
                                                            }
                                                            ProcessState.Loading -> {
                                                                loading = true
                                                            }
                                                            ProcessState.Success -> {
                                                                loading = false
                                                                viewModel.delete(website)

                                                                if(ishapticenabled){
                                                                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                                                }

                                                            }
                                                        }


                                                    }

                                                }



                                            }) {
                                                if (loading) {
                                                    CircularProgressIndicator( strokeCap = StrokeCap.Round)
                                                }

                                                Icon(
                                                    imageVector = Icons.Outlined.Delete,
                                                    contentDescription = "Delete Website"
                                                )

                                            }
                                        }
                                    },

                                    )
                            }
                        }
                    }


        }
    if(showwarning){
        AlertDialog(onDismissRequest = { showwarning=false},
            dismissButton = { TextButton(onClick = { showwarning=false }) {
                Text("Cancel")

            }
            },
            confirmButton = { TextButton(onClick = { viewModel.cleardb();viewModel.clearsupabasedata();showwarning=false}) {
                Text("Clear", color = Color.Red)

            }
            },
            title = {Text("Clear Websites",modifier=Modifier.fillMaxWidth(), textAlign = TextAlign.Center)},
            text = {Text("This is remove all the feed websites from the cloud and local database")}

        )
    }

    }

