package com.example.rss_parser.screens

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
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
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import io.github.jan.supabase.gotrue.OtpType
import io.github.jan.supabase.gotrue.SignOutScope
import io.github.jan.supabase.gotrue.auth
import io.github.jan.supabase.network.SupabaseApi
import io.ktor.client.plugins.HttpRequestTimeoutException
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun password_change(navHostController: NavHostController) {

    val corutine = rememberCoroutineScope()
    var newpassword by remember {
        mutableStateOf("")
    }
    var email by remember {
        mutableStateOf("")
    }
    var verifyopt by remember {
        mutableStateOf("")
    }
    var confirmpassword by remember {
        mutableStateOf("")
    }
    var passwordVisible by rememberSaveable { mutableStateOf(false) }
    var passwordVisible1 by rememberSaveable { mutableStateOf(false) }
    val keyboardController = LocalSoftwareKeyboardController.current
    var error by remember {
        mutableStateOf(false)
    }
    var context = LocalContext.current
    var isloading by remember {
        mutableStateOf(false)
    }
    var otpwrong by remember {
        mutableStateOf(false)
    }
    var showpasswordchange by remember {
        mutableStateOf(false)
    }
    var sentotp by remember {
        mutableStateOf(false)
    }

    Scaffold(topBar = {
        TopAppBar(title = { Text("Reset Password") }, navigationIcon = {
            IconButton(
                onClick = { navHostController.popBackStack() }) {
                Icon(
                    imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                    contentDescription = "Back"
                )


            }
        })
    }) {


        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(it),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if(!showpasswordchange) {



                    OutlinedTextField(
                        value = email,

                        onValueChange = {
                            email = it
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),


                        label = { Text("Email") },
                        keyboardActions = KeyboardActions(
                            onDone = { keyboardController?.hide() }),

                        modifier = Modifier
                            .fillMaxWidth()

                            .padding(start = 10.dp, end = 10.dp, bottom = 30.dp),
                        trailingIcon = {
                            if (sentotp) {
                                CircularProgressIndicator()
                            } else {
                                TextButton(onClick = {
                                    sentotp=true
                                    corutine.launch {
                                        try {
                                            supabaseclient.client.auth.resetPasswordForEmail(
                                                email = email
                                            )
                                            Toast.makeText(
                                                context,
                                                "Password reset link sent to mail",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                            sentotp=false
                                        } catch (e: Exception) {
                                            sentotp=false
                                            when (e) {
                                                is RestException -> {
                                                    val error = e.message?.substringBefore("URL")
                                                    Toast.makeText(
                                                        context,
                                                        "$error",
                                                        Toast.LENGTH_SHORT
                                                    ).show()
                                                }

//                                    is HttpRequestTimeoutException -> {
//                                        val error = e.message?.substringBefore("URL")
//                                        Toast.makeText(context, "$error", Toast.LENGTH_SHORT).show()
//                                    }
//
//                                    is HttpRequestException -> {
//                                        val error = e.message?.substringBefore("URL")
//                                        Toast.makeText(context, "$error", Toast.LENGTH_SHORT).show()
//                                    }
                                            }


                                        }
                                    }


                                }) {
                                    Text("Send OTP")

                                }
                            }
                        }

                        )



                OutlinedTextField(
                    value = verifyopt,

                    onValueChange = {
                        verifyopt = it
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 10.dp, end = 10.dp, bottom = 30.dp),


                    label = { Text("OTP") },
                    keyboardActions = KeyboardActions(
                        onDone = { keyboardController?.hide() }),
                    isError = otpwrong,


                    )
                if (isloading) {
                    CircularProgressIndicator()
                } else {
                    Button(onClick = {
                        isloading = true


                        corutine.launch {
                            try {
                                supabaseclient.client.auth.verifyEmailOtp(
                                    OtpType.Email.RECOVERY,
                                    email = email,
                                    token = verifyopt
                                )
                                otpwrong = false
                                showpasswordchange = true
                                isloading = false
                            } catch (e: Exception) {
                                isloading = false
                                otpwrong = true

                                when (e) {
                                    is RestException -> {
                                        val error = e.message?.substringBefore("URL")
                                        Toast.makeText(context, "$error", Toast.LENGTH_SHORT).show()
                                    }

//                                    is HttpRequestTimeoutException -> {
//                                        val error = e.message?.substringBefore("URL")
//                                        Toast.makeText(context, "$error", Toast.LENGTH_SHORT).show()
//                                    }
//
//                                    is HttpRequestException -> {
//                                        val error = e.message?.substringBefore("URL")
//                                        Toast.makeText(context, "$error", Toast.LENGTH_SHORT).show()
//                                    }
                                }
                            }

                        }
                    }) {

                            Text("Verify OTP")
                        
                    }
                }
            }
            if (showpasswordchange) {
                OutlinedTextField(
                    value = newpassword,
                    onValueChange = {
                        newpassword = it
                    },
                    label = { Text("New Password") },
                    isError = error,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 10.dp, end = 10.dp, bottom = 30.dp, top = 10.dp),
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
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

                    )
                OutlinedTextField(
                    value = confirmpassword,
                    onValueChange = {
                        confirmpassword = it
                    },
                    label = { Text("Confirm Password") },
                    isError = error,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 10.dp, end = 10.dp, bottom = 30.dp),
                    visualTransformation = if (passwordVisible1) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardActions = KeyboardActions(
                        onDone = { keyboardController?.hide() }),
                    trailingIcon = {
                        if (passwordVisible1) {
                            IconButton(onClick = { passwordVisible1 = false }) {
                                Icon(
                                    imageVector = Icons.Outlined.Visibility,
                                    contentDescription = "Visible"
                                )

                            }
                        } else {
                            IconButton(onClick = { passwordVisible1 = true }) {
                                Icon(
                                    imageVector = Icons.Outlined.VisibilityOff,
                                    contentDescription = "Visible"
                                )

                            }

                        }
                    },
                    singleLine = true,

                    )
                if (isloading) {
                    CircularProgressIndicator()
                } else {
                    Button(onClick = {
                        isloading = true
                        if (newpassword != confirmpassword) {
                            error = true
                            isloading = false
                            Toast.makeText(context,"Password not matched",Toast.LENGTH_SHORT).show()
                        }
                        if (newpassword == confirmpassword) {




                            corutine.launch {
                                try {
                                    supabaseclient.client.auth.modifyUser {
                                        password = newpassword
                                    }
                                    Toast.makeText(
                                        context,
                                        "Password Changed Successfully",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    navHostController.popBackStack()
                                    navHostController.navigate(Destinations.enterscreen.route){

                                        popUpTo(Destinations.password_recover.route) { inclusive = true }
                                        popUpTo(Destinations.enterscreen.route) { inclusive = true }

                                    }
                                    supabaseclient.client.auth.signOut(SignOutScope.GLOBAL)

                                    isloading = false

                                }
                                catch (e:Exception) {
                                    isloading = false
                                    Log.d("TAG", "password_change: $e.message")
                                    when(e){
                                        is RestException->{
                                            val error = e.message?.substringBefore("URL")
                                            Toast.makeText(context, "$error", Toast.LENGTH_SHORT).show()
                                        }
//                                        is HttpRequestTimeoutException ->{
//                                            val error = e.message?.substringBefore("URL")
//                                            Toast.makeText(context, "$error", Toast.LENGTH_SHORT).show()
//                                        }
//                                        is HttpRequestException ->{
//                                            val error = e.message?.substringBefore("URL")
//                                            Toast.makeText(context, "$error", Toast.LENGTH_SHORT).show()
//                                        }
                                    }
                                }
                            }

                        }


                    }) {


                        Text("Reset Password")


                    }


                }
            }
        }
    }
}