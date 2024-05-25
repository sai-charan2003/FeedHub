package com.example.rss_parser.Navigation

import android.widget.Toast
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut

import androidx.compose.runtime.Composable
import androidx.compose.animation.AnimatedContentTransitionScope.SlideDirection
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.rss_parser.Utils.ProcessState
import com.example.rss_parser.Utils.SharedPref
import com.example.rss_parser.screens.VerifyOTP
import com.example.rss_parser.screens.AboutApp
import com.example.rss_parser.screens.AboutDeveloper
import com.example.rss_parser.screens.Account
import com.example.rss_parser.screens.AddFeed
import com.example.rss_parser.screens.AllFeeds
import com.example.rss_parser.screens.AppIconChange
import com.example.rss_parser.screens.BookmarkScreen
import com.example.rss_parser.screens.HomeScreen
import com.example.rss_parser.screens.LoginScreen
import com.example.rss_parser.screens.SearchScreen
import com.example.rss_parser.screens.Settings
import com.example.rss_parser.screens.password_change
import com.example.rss_parser.screens.signin
import com.example.rss_parser.screens.signup
import com.example.rss_parser.supabase.client.supabaseclient
import com.example.rss_parser.viewmodel.viewmodel
import io.github.jan.supabase.gotrue.auth


@Composable
fun NavigationAppHost(navHostController: NavHostController) {
    val context = LocalContext.current
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
    var isLoggedIn=SharedPref.isLoggedIn
    val lifecycle= LocalLifecycleOwner.current
    LaunchedEffect(key1 = true) {
        supabaseclient.client.auth.loadFromStorage()
        val authenticated = supabaseclient.client.auth.currentUserOrNull()
        if (isLoggedIn && authenticated == null) {
            isLoggedIn = false
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
                        SharedPref.clearSharedPrefs()
                    }

                }
            }


            Toast.makeText(context, "Session expired please login in again", Toast.LENGTH_LONG)
                .show()
        }
    }




        NavHost(
            navController = navHostController,

            startDestination = if (isLoggedIn) Destinations.home.route else Destinations.enterscreen.route,
            enterTransition = {
                fadeIn() + slideIntoContainer(
                    SlideDirection.Start,
                    initialOffset = { 100 },
                    animationSpec = (tween(easing = LinearEasing, durationMillis = 200))
                )
            },
            exitTransition = {
                fadeOut() + slideOutOfContainer(
                    SlideDirection.Start,
                    targetOffset = { -100 },
                    animationSpec = (tween(easing = LinearEasing, durationMillis = 200))
                )
            },
            popEnterTransition = {
                fadeIn() + slideIntoContainer(
                    SlideDirection.End,
                    initialOffset = { -100 },
                    animationSpec = (tween(easing = LinearEasing, durationMillis = 200))
                )
            },
            popExitTransition = {
                fadeOut() + slideOutOfContainer(
                    SlideDirection.End,
                    targetOffset = { 100 },
                    animationSpec = (tween(easing = LinearEasing, durationMillis = 200))
                )
            },
        )


        {
            composable(Destinations.home.route,) {
                HomeScreen(navHostController = navHostController)
            }
            composable(Destinations.addnewfeed.route,) {
                AddFeed(navHostController)
            }
            composable(Destinations.settings.route) {
                Settings(navHostController = navHostController)

            }
            composable(Destinations.feeds.route) {
                AllFeeds(navHostController = navHostController)
            }
            composable(Destinations.bookmarks.route) {
                BookmarkScreen(navHostController = navHostController)
            }
            composable(Destinations.aboutdeveloper.route) {
                AboutDeveloper(navHostController = navHostController)
            }
            composable(Destinations.aboutapp.route) {
                AboutApp(navHostController = navHostController)
            }
            composable(Destinations.signup.route) {
                signup(navHostController)
            }
            composable(Destinations.enterscreen.route){
                LoginScreen(navHostController = navHostController)
            }
            composable(Destinations.signinscreen.route){
                signin(navHostController = navHostController)
            }
            composable(Destinations.password_recover.route,

            ){
                password_change(navHostController)

            }
            composable(Destinations.account.route){
                Account(navHostController = navHostController)
            }
            composable(Destinations.appiconchange.route){
                AppIconChange(navHostController)
            }
            composable(Destinations.search.route){
                SearchScreen(navHostController = navHostController)
            }
            composable(Destinations.verifyotp.route, arguments = listOf(navArgument("email"){
                type=NavType.StringType
            })){
                it.arguments?.getString("email")
                    ?.let { it1 -> VerifyOTP(navHostController = navHostController, it1) }
            }



        }
    }


