package com.example.rss_parser.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.rss_parser.Navigation.Destinations
import com.example.rss_parser.R
import com.example.rss_parser.supabase.client.supabaseclient
import io.github.jan.supabase.gotrue.SignOutScope
import io.github.jan.supabase.gotrue.auth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun enterscreen(navHostController: NavHostController){
    LaunchedEffect(Unit) {
        supabaseclient.client.auth.signOut(SignOutScope.GLOBAL)

    }

        Column(modifier=Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
            Row (modifier= Modifier
                .fillMaxWidth()
                , horizontalArrangement = Arrangement.Center){
                Image(painter = painterResource(id = R.drawable.rounded_rss_feed_50), contentDescription = "logo",modifier=Modifier.size(70.dp))
                Text("Feed Hub", style = MaterialTheme.typography.displayMedium,modifier=Modifier.padding(top = 15.dp))
            }
            Button(onClick = { navHostController.navigate(Destinations.signup.route) },modifier= Modifier
                .fillMaxWidth()
                .padding(start = 10.dp, end = 10.dp, top = 30.dp)) {
                Text(text = "Sign Up", )


            }
            OutlinedButton(onClick = { navHostController.navigate(Destinations.signinscreen.route) },modifier= Modifier
                .fillMaxWidth()
                .padding(start = 10.dp, end = 10.dp, top = 30.dp)) {
                Text(text = "Sign In",)


            }

        }

    }
