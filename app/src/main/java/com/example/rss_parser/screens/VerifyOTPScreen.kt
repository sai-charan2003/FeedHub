package com.example.rss_parser.screens


import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController

import com.example.rss_parser.Navigation.Destinations
import com.example.rss_parser.Utils.ProcessState
import com.example.rss_parser.Utils.SharedPref
import com.example.rss_parser.screens.Items.OtpTextField
import com.example.rss_parser.supabase.client.supabaseclient
import com.example.rss_parser.viewmodel.viewmodel
import io.github.jan.supabase.gotrue.auth
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VerifyOTP(navHostController: NavHostController,useremail:String){
    val scope= rememberCoroutineScope()
    var otp by remember {
        mutableStateOf("")
    }
    var countdown by remember {
        mutableStateOf(60)
    }
    LaunchedEffect(countdown) {
        if(countdown!=0){
            delay(990)
            countdown -= 1
        }

    }

    val lifecycle= LocalLifecycleOwner.current
    var isverified by remember {
        mutableStateOf(false)
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
    val SharedPref= SharedPref(context)
    Scaffold(
        topBar = {
            LargeTopAppBar(
                title = { /*TODO*/ },
                navigationIcon = {
                    IconButton(
                        onClick = { navHostController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                            contentDescription = "Back"
                        )

                    }
                }
            )
        }
    ) {
        Column(modifier= Modifier
            .padding(it)
            .fillMaxSize()
            .safeDrawingPadding()
            , horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                "OTP Verification",
                style = MaterialTheme.typography.displaySmall,
                modifier=Modifier.padding(bottom=10.dp)
            )
            Row(modifier=Modifier.padding(top=20.dp,bottom=10.dp)) {
                Text(
                    "Enter OTP sent to ",
                    style = MaterialTheme.typography.titleSmall,
                    color=MaterialTheme.colorScheme.onSurface.copy(alpha=0.5f)
                )
                Text(
                    useremail,
                    style = MaterialTheme.typography.titleSmall,
                    color=MaterialTheme.colorScheme.onSurface.copy(alpha=0.9f),
                    modifier=Modifier.clickable {
                        navHostController.popBackStack()
                    }

                )
            }

            OtpTextField(
                otpText = otp,
                onOtpTextChange = { value, otpInputFilled ->
                    otp = value
                    if (otpInputFilled==true){
                        isverified=true
                    }

                },
                modifier = Modifier.padding(top=30.dp,bottom=10.dp)
            )
            Button(onClick = {
                isverified=true

            },
                modifier=Modifier.padding(top=20.dp),
                enabled =!isverified
                ) {
                if(isverified){
                    CircularProgressIndicator(modifier=Modifier.size(20.dp), strokeCap = StrokeCap.Round,)
                }
                else {
                    Text("Verify")
                }

            }
            Row(modifier= Modifier
                .fillMaxWidth()
                .padding(top = 15.dp), horizontalArrangement = Arrangement.Center,) {

                if (countdown == 0) {
                    Text("Didn't Receive Code?",modifier= Modifier.padding(end = 4.dp))

                    Text(
                        text = "Resend Code",
                        modifier = Modifier
                            .clickable {
                                countdown=60
                                viewModel.SignInWithOTP(useremail).observe(lifecycle){
                                    when(it){
                                        is ProcessState.Error -> {
                                            Toast.makeText(context,it.error,Toast.LENGTH_LONG).show()
                                        }
                                        ProcessState.Loading -> {

                                        }
                                        ProcessState.Success -> {
                                            Toast.makeText(context,"Code Sent To Mail",Toast.LENGTH_LONG).show()
                                        }
                                    }
                                }

                            },
                        color = Color(0xff7bc3fe)
                    )

                }
                else{
                    Text("Resend code in",modifier= Modifier.padding(end = 4.dp))
                    Text(text = countdown.toString()+"s",color = Color(0xff7bc3fe))
                }
            }

        }

    }
    if (isverified){
        viewModel.VerifyOTP(otp,useremail).observe(lifecycle){
            when(it){
                is ProcessState.Error -> {
                    isverified=false
                    Toast.makeText(context,it.error,Toast.LENGTH_LONG).show()

                }
                ProcessState.Loading -> {
                    isverified=true

                }
                ProcessState.Success -> {
                    viewModel.checkForAuthentication().observe(lifecycle){
                        when(it){
                            is ProcessState.Error -> {
                                Toast.makeText(context,it.error,Toast.LENGTH_LONG).show()
                            }
                            ProcessState.Loading -> {

                            }
                            ProcessState.Success -> {
                                SharedPref.isLoggedIn = true
                                SharedPref.AuthToken =
                                    supabaseclient.client.auth.currentAccessTokenOrNull()
                                navHostController.navigate(Destinations.home.route)

                            }
                        }
                    }
                }
            }
        }
    }


}