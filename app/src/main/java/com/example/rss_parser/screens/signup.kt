package com.example.rss_parser.screens

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.rss_parser.Navigation.Destinations
import com.example.rss_parser.R
import com.example.rss_parser.supabase.client.supabaseclient
import io.github.jan.supabase.exceptions.HttpRequestException
import io.github.jan.supabase.exceptions.RestException
import io.github.jan.supabase.gotrue.SessionStatus
import io.github.jan.supabase.gotrue.auth
import io.github.jan.supabase.gotrue.providers.builtin.Email
import io.ktor.client.plugins.HttpRequestTimeoutException
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


@Composable
fun signup(navHostController: NavHostController){


    val couroutine= rememberCoroutineScope()
    var loading by remember {
        mutableStateOf(false)
    }


    var username by remember {


        mutableStateOf("")
    }
    val keyboardController = LocalSoftwareKeyboardController.current
    var passwordVisible by rememberSaveable { mutableStateOf(false) }
    var userpassword by remember {
        mutableStateOf("")
    }

    val context= LocalContext.current
    val sharedPreferences:SharedPreferences=context.getSharedPreferences("showimages", Context.MODE_PRIVATE)
    val editor=sharedPreferences.edit()
    LaunchedEffect(key1 = Unit){
        delay(1000)
        val token= sharedPreferences.getString("authtoken","")
        if(token== supabaseclient.client.auth.currentAccessTokenOrNull()){
            navHostController.navigate(Destinations.home.route)

        }
        Log.d("TAG", "signup: ${supabaseclient.client.auth.currentAccessTokenOrNull()}")
    }
    Column(modifier= Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
        Row(
            modifier = Modifier
                .fillMaxWidth(), horizontalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.rounded_rss_feed_50),
                contentDescription = "logo",
                modifier = Modifier.size(70.dp)
            )
            Text(
                "Feed Hub",
                style = MaterialTheme.typography.displayMedium,
                modifier = Modifier.padding(top = 15.dp)
            )
        }
        OutlinedTextField(
            value = username,
            onValueChange = {
                username = it
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 30.dp, start = 10.dp, end = 10.dp),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            label = { Text(text = "Email") },
        )
        OutlinedTextField(
            value = userpassword,
            onValueChange = {
                userpassword = it
            },

            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 30.dp, start = 10.dp, end = 10.dp),
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            keyboardActions = KeyboardActions(
                onDone = { keyboardController?.hide() }
            ),
            trailingIcon = {
                if (passwordVisible) {
                    IconButton(onClick = { passwordVisible = false }) {
                        Icon(
                            imageVector = Icons.Outlined.Visibility,
                            contentDescription = "Visible"
                        )

                    }
                } else {
                    IconButton(onClick = { passwordVisible = true }) {
                        Icon(
                            imageVector = Icons.Outlined.VisibilityOff,
                            contentDescription = "Visible"
                        )

                    }

                }
            },
            singleLine = true,
            label = { Text(text = "Password") },

            )
        if (loading) {
            CircularProgressIndicator(modifier = Modifier.padding(top = 30.dp))
        } else {
            Button(
                onClick = {
                    couroutine.launch {

                        try {
                            loading = true
                            supabaseclient.client.auth.signUpWith(Email) {
                                email = username
                                password = userpassword

                            }
                            supabaseclient.client.auth.sessionStatus.collect {
                                when (it) {
                                    is SessionStatus.Authenticated -> {
                                        editor.putString(
                                            "authtoken",
                                            supabaseclient.client.auth.currentAccessTokenOrNull()
                                        )
                                        editor.apply()
                                        editor.putBoolean("islog", true)
                                        editor.apply()
                                        navHostController.navigate(Destinations.home.route){
                                            popUpTo(Destinations.signup.route) { inclusive = true }
                                            popUpTo(Destinations.enterscreen.route) { inclusive = true }
                                        }
                                    }

                                    SessionStatus.LoadingFromStorage -> Toast.makeText(
                                        context,
                                        "Problem in supabase",
                                        Toast.LENGTH_SHORT
                                    )
                                        .show()

                                    SessionStatus.NetworkError -> Toast.makeText(
                                        context,
                                        "Network Error",
                                        Toast.LENGTH_SHORT
                                    )
                                        .show()

                                    SessionStatus.NotAuthenticated -> Toast.makeText(
                                        context,
                                        "Not Authenticated",
                                        Toast.LENGTH_SHORT
                                    )
                                        .show()
                                }
                            }
                            loading = false
                        } catch (e: Exception) {
                            loading = false
                            when(e){
                                is RestException ->{
                                    val error = e.message?.substringBefore("URL")
                                    Toast.makeText(context, "$error", Toast.LENGTH_SHORT).show()
                                }
                                is HttpRequestTimeoutException ->{
                                    val error = e.message?.substringBefore("URL")
                                    Toast.makeText(context, "$error", Toast.LENGTH_SHORT).show()
                                }
                                is HttpRequestException ->{
                                    val error = e.message?.substringBefore("URL")
                                    Toast.makeText(context, "$error", Toast.LENGTH_SHORT).show()
                                }
                            }

                        }
                    }


                }, modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 30.dp, start = 10.dp, end = 10.dp),
                enabled = username.isNotEmpty()&&userpassword.isNotEmpty()
            ) {
                Text("Sign Up")

            }
        }
        Row(modifier=Modifier.fillMaxWidth().padding(top=15.dp), horizontalArrangement = Arrangement.Center,) {
            Text("Already have an account?",modifier=Modifier.padding(end = 4.dp))

                Text(text = "Sign In",modifier=Modifier.clickable { navHostController.navigate(Destinations.signinscreen.route) }, color = Color(0xff7bc3fe))



        }
    }
}