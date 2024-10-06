package dev.charan.feedhub.screens

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.ListItem
import androidx.compose.material3.Scaffold

import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import dev.charan.feedhub.Utils.ProcessState
import dev.charan.feedhub.Utils.SharedPref
import dev.charan.feedhub.screens.Items.SettingsListItem
import dev.charan.feedhub.supabase.client.supabaseclient
import dev.charan.feedhub.viewmodel.viewmodel
import io.github.jan.supabase.annotations.SupabaseInternal
import io.github.jan.supabase.gotrue.auth

@OptIn(ExperimentalMaterial3Api::class, SupabaseInternal::class)
@Composable
fun Account(navHostController: NavHostController){
    var useremail by remember {
        mutableStateOf("")
    }
    val context= LocalContext.current
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
    var coroutinescope= rememberCoroutineScope()

    val scroll = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    val SharedPref= SharedPref(context)


    LaunchedEffect(Unit) {
        useremail= supabaseclient.client.auth.sessionManager.loadSession()?.user?.email.toString()


    }
    Scaffold(
        modifier= Modifier
            .fillMaxSize()
            .nestedScroll(scroll.nestedScrollConnection),
        {
            LargeTopAppBar(
                title = { Text("Account")},
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
            .padding(it)
            .fillMaxSize()) {
            item{
                ListItem(
                    {
                        Column {


                            Row(
                                modifier = Modifier.fillMaxSize(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.AccountCircle,
                                    contentDescription = null
                                )
                                if(SharedPref.isAnonymousSignin){
                                    Text(
                                        "Anonymous",
                                        modifier = Modifier.padding(start = 10.dp),


                                        )

                                }
                                else {
                                    Text(
                                        useremail,
                                        modifier = Modifier.padding(start = 10.dp),


                                        )
                                }

                            }

                        }
                    },

                )
                SettingsListItem(label = "Log Out",Modifier=Modifier.padding(start= 15.dp)) {
                    viewModel.Logout().observe(lifecycle){
                        when(it){
                            is ProcessState.Error -> {
                                Toast.makeText(context,it.error,Toast.LENGTH_LONG).show()
                            }
                            ProcessState.Loading -> {

                            }
                            ProcessState.Success -> {
                                viewModel.cleardb()

                                viewModel.clearAllBookmarks()
                                navHostController.popBackStack()
                                navHostController.navigate(dev.charan.feedhub.Navigation.Destinations.enterscreen.route) {
                                    popUpTo(dev.charan.feedhub.Navigation.Destinations.home.route) {
                                        inclusive = true
                                    }
                                }
                                SharedPref.clearSharedPrefs()
                            }

                        }
                    }

                }


            }


        }
        
    }

}