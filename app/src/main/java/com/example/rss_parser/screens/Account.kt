package com.example.rss_parser.screens

import android.content.Context
import android.content.SharedPreferences
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TopAppBar
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.rss_parser.Navigation.Destinations
import com.example.rss_parser.supabase.client.supabaseclient
import io.github.jan.supabase.exceptions.HttpRequestException
import io.github.jan.supabase.exceptions.RestException
import io.github.jan.supabase.gotrue.SignOutScope
import io.github.jan.supabase.gotrue.auth
import io.ktor.client.plugins.HttpRequestTimeoutException
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun account(navHostController: NavHostController){
    var useremail by remember {
        mutableStateOf("")
    }
    var coroutinescope= rememberCoroutineScope()
    val context= LocalContext.current
    val scroll = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("showimages", Context.MODE_PRIVATE)

    val editor = sharedPreferences.edit()

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
                                Text(
                                    useremail,
                                    modifier = Modifier.padding(start = 10.dp),
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,

                                    )

                            }

                        }
                    },

                )
                ListItem({
                    Text(text = "Log Out")
                },modifier= Modifier
                    .clickable {
                        coroutinescope.launch {
                            try {
                                supabaseclient.client.auth.signOut(SignOutScope.GLOBAL)
                                editor.putBoolean("islog", false)
                                editor.apply()
                                navHostController.popBackStack()
                                navHostController.navigate(Destinations.enterscreen.route) {
                                    popUpTo(Destinations.home.route) {
                                        inclusive = true
                                    }
                                }
                            } catch (e: Exception) {
                                when (e) {
                                    is RestException -> {
                                        val error = e.message?.substringBefore("URL")
                                        Toast
                                            .makeText(context, "$error", Toast.LENGTH_SHORT)
                                            .show()
                                    }

                                    is HttpRequestTimeoutException -> {
                                        val error = e.message?.substringBefore("URL")
                                        Toast
                                            .makeText(context, "$error", Toast.LENGTH_SHORT)
                                            .show()
                                    }

                                    is HttpRequestException -> {
                                        val error = e.message?.substringBefore("URL")
                                        Toast
                                            .makeText(context, "$error", Toast.LENGTH_SHORT)
                                            .show()
                                    }
                                }


                            }
                        }
                    }
                    .padding(start = 35.dp))
                HorizontalDivider(modifier=Modifier.padding(start=40.dp))
                ListItem({
                    Text(text = "Change password")
                },modifier=Modifier.clickable { navHostController.navigate(Destinations.password_recover.route) }.padding(start=35.dp))

            }


        }
        
    }

}