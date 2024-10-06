package dev.charan.feedhub.screens

import android.widget.Toast

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize

import androidx.compose.foundation.Image
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column


import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberTooltipState

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel

import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import dev.charan.feedhub.screens.Items.EnterTextFiled

import dev.charan.feedhub.Utils.ProcessState
import dev.charan.feedhub.R
import dev.charan.feedhub.Utils.SharedPref

import dev.charan.feedhub.supabase.client.supabaseclient
import dev.charan.feedhub.viewmodel.viewmodel

import io.github.jan.supabase.annotations.SupabaseExperimental

import io.github.jan.supabase.gotrue.auth

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.android.awaitFrame

import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, SupabaseExperimental::class)

@Composable

fun LoginScreen(navHostController: NavHostController) {
    var showtext by remember {
        mutableStateOf(false)
    }
    val tooltipState = rememberTooltipState()
    var lifecycle = LocalLifecycleOwner.current
    val couroutine = rememberCoroutineScope()
    val context = LocalContext.current
    val interactionSource = remember { MutableInteractionSource() }

    val viewModel = viewModel<viewmodel>(
        factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return viewmodel(
                    context
                ) as T
            }
        }
    )
    var isLoading by remember {
        mutableStateOf(false)
    }

    viewModel.cleardb()

    val SharedPref = SharedPref(context)

    val focusRequester = remember { FocusRequester() }
    var useremail by remember {
        mutableStateOf("")
    }

    var showemailenter by remember {
        mutableStateOf(false)
    }

    var isotpsent by remember {
        mutableStateOf(false)
    }
    var showtooltip by remember {
        mutableStateOf(false)
    }
    var isAnonymousLoginLoading by remember {
        mutableStateOf(false)
    }





    Column(
        modifier = Modifier
            .fillMaxSize()
            .safeDrawingPadding(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Image(
            painter = painterResource(id = R.drawable.rounded_rss_feed_50),
            contentDescription = null,
            modifier = Modifier.size(100.dp)
        )


        Text(
            "Feed Hub",
            style = MaterialTheme.typography.displaySmall,
            modifier = Modifier.padding(top = 15.dp),
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Bold
        )
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 10.dp, end = 10.dp, top = 30.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = {
                        couroutine.launch(Dispatchers.Main) {
                            viewModel.LoginWithGoogle(context).observe(lifecycle) { Authstatus ->
                                when (Authstatus) {
                                    is ProcessState.Error -> {
                                        isLoading = false
                                        val message = Authstatus.error
                                        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
                                    }

                                    ProcessState.Loading -> {
                                        isLoading = true
                                    }

                                    ProcessState.Success -> {
                                        viewModel.checkForAuthentication()
                                            .observe(lifecycle) { status ->
                                                when (status) {
                                                    is ProcessState.Error -> {
                                                        val message = status.error
                                                        Toast.makeText(
                                                            context,
                                                            message,
                                                            Toast.LENGTH_LONG
                                                        ).show()
                                                    }

                                                    ProcessState.Loading -> {
                                                    }

                                                    ProcessState.Success -> {
                                                        SharedPref.isLoggedIn = true
                                                        SharedPref.AuthToken =
                                                            supabaseclient.client.auth.currentAccessTokenOrNull()
                                                        navHostController.navigate(dev.charan.feedhub.Navigation.Destinations.home.route) {
                                                            popUpTo(dev.charan.feedhub.Navigation.Destinations.enterscreen.route) {
                                                                inclusive = true
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                    }
                                }
                            }
                        }
                    },
                    modifier = Modifier
                        .weight(1f)
                        .animateContentSize(),
                    enabled = !isLoading
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically,) {
                        Image(
                            painter = painterResource(id = R.drawable.google),
                            contentDescription = null,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(5.dp))
                        Text(
                            text = "Continue with Google", modifier = Modifier
                                .then(
                                    if (isLoading) {
                                        Modifier.padding(end = 10.dp)
                                    } else {
                                        Modifier
                                    }
                                )
                                .animateContentSize()
                        )
                    }

                    AnimatedVisibility(visible = isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier
                                .size(20.dp)
                                .fillMaxWidth(),
                            strokeCap = StrokeCap.Round,
                            strokeWidth = 3.dp
                        )
                    }
                }
            }

        }

        if (!showemailenter) {
            FilledTonalButton(
                onClick = { showemailenter = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 10.dp, end = 10.dp, top = 10.dp)
            ) {
                Image(imageVector = Icons.Outlined.Email, contentDescription = null)
                Text(text = "Continue with email", modifier = Modifier.padding(start = 5.dp))


            }
        } else {
            LaunchedEffect(focusRequester) {
                awaitFrame()
                focusRequester.requestFocus()
            }
            EnterTextFiled(emailfield = useremail,
                onEmailChange = {
                                useremail=it.trim()

            },
                onEnter = {
                          isotpsent=it
                },
                focusRequester = focusRequester)


        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            TextButton(
                modifier = Modifier.animateContentSize(),
                onClick = {
                    viewModel.anonymousSignIn().observe(lifecycle) {
                        when (it) {
                            is ProcessState.Error -> {
                                Toast.makeText(context, it.error, Toast.LENGTH_LONG).show()
                                isAnonymousLoginLoading = false
                            }

                            ProcessState.Loading -> {
                                isAnonymousLoginLoading = true
                            }

                            ProcessState.Success -> {
                                viewModel.checkForAuthentication().observe(lifecycle) {
                                    when (it) {
                                        is ProcessState.Error -> {
                                            Toast.makeText(context, it.error, Toast.LENGTH_LONG)
                                                .show()
                                            isAnonymousLoginLoading = false

                                        }

                                        ProcessState.Loading -> {


                                        }

                                        ProcessState.Success -> {
                                            SharedPref.isLoggedIn = true
                                            SharedPref.isAnonymousSignin = true
                                            SharedPref.AuthToken =
                                                supabaseclient.client.auth.currentAccessTokenOrNull()
                                            navHostController.navigate(dev.charan.feedhub.Navigation.Destinations.home.route) {
                                                popUpTo(dev.charan.feedhub.Navigation.Destinations.enterscreen.route) {
                                                    inclusive = true
                                                }
                                            }
                                            isAnonymousLoginLoading = true
                                        }
                                    }
                                }
                            }
                        }
                    }
                }) {
                Text(
                    "Sign in Anonymously",
                    modifier = Modifier
                        .then(
                            if (isAnonymousLoginLoading) {
                                Modifier.padding(end = 10.dp)
                            } else {
                                Modifier
                            }
                        )
                        .animateContentSize()

                )
                AnimatedVisibility(visible = isAnonymousLoginLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .size(20.dp)
                            .fillMaxWidth(),
                        strokeCap = StrokeCap.Round,
                        strokeWidth = 3.dp
                    )
                }


            }


        }

    }
    if (isotpsent) {
        viewModel.SignInWithOTP(useremail).observe(lifecycle) {
            when (it) {
                is ProcessState.Error -> {
                    isotpsent = false
                    val message = it.error
                    Toast.makeText(context, message, Toast.LENGTH_LONG)
                        .show()
                }

                ProcessState.Loading -> {

                }

                ProcessState.Success -> {
                    navHostController.navigate("verifyotp/$useremail")
                    isotpsent = false
                }
            }
        }

    }


}

@Preview
@Composable
fun PreviewForLoginScreen(){
    LoginScreen(navHostController = rememberNavController())

}








