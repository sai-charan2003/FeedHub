package dev.charan.feedhub.Navigation

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
import dev.charan.feedhub.Utils.ProcessState
import dev.charan.feedhub.Utils.SharedPref
import dev.charan.feedhub.screens.VerifyOTP
import dev.charan.feedhub.screens.AboutApp
import dev.charan.feedhub.screens.AboutDeveloper
import dev.charan.feedhub.screens.Account
import dev.charan.feedhub.screens.AddFeed
import dev.charan.feedhub.screens.AllFeeds
import dev.charan.feedhub.screens.AppIconChange
import dev.charan.feedhub.screens.BookmarkScreen
import dev.charan.feedhub.screens.HomeScreen
import dev.charan.feedhub.screens.Licenses
import dev.charan.feedhub.screens.LoginScreen
import dev.charan.feedhub.screens.SearchScreen
import dev.charan.feedhub.screens.Settings
import dev.charan.feedhub.screens.password_change
import dev.charan.feedhub.screens.signin
import dev.charan.feedhub.screens.signup
import dev.charan.feedhub.supabase.client.supabaseclient
import dev.charan.feedhub.viewmodel.viewmodel
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

            startDestination = if (isLoggedIn) dev.charan.feedhub.Navigation.Destinations.home.route else dev.charan.feedhub.Navigation.Destinations.enterscreen.route,
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
            composable(dev.charan.feedhub.Navigation.Destinations.home.route,) {
                HomeScreen(navHostController = navHostController)
            }
            composable(dev.charan.feedhub.Navigation.Destinations.addnewfeed.route,) {
                AddFeed(navHostController)
            }
            composable(dev.charan.feedhub.Navigation.Destinations.settings.route) {
                Settings(navHostController = navHostController)

            }
            composable(dev.charan.feedhub.Navigation.Destinations.feeds.route) {
                AllFeeds(navHostController = navHostController)
            }
            composable(dev.charan.feedhub.Navigation.Destinations.bookmarks.route) {
                BookmarkScreen(navHostController = navHostController)
            }
            composable(dev.charan.feedhub.Navigation.Destinations.aboutdeveloper.route) {
                AboutDeveloper(navHostController = navHostController)
            }
            composable(dev.charan.feedhub.Navigation.Destinations.aboutapp.route) {
                AboutApp(navHostController = navHostController)
            }
            composable(dev.charan.feedhub.Navigation.Destinations.signup.route) {
                signup(navHostController)
            }
            composable(dev.charan.feedhub.Navigation.Destinations.enterscreen.route){
                LoginScreen(navHostController = navHostController)
            }
            composable(dev.charan.feedhub.Navigation.Destinations.signinscreen.route){
                signin(navHostController = navHostController)
            }
            composable(dev.charan.feedhub.Navigation.Destinations.licenses.route){
                Licenses(navHostController = navHostController)
            }
            composable(
                dev.charan.feedhub.Navigation.Destinations.password_recover.route,

            ){
                password_change(navHostController)

            }
            composable(dev.charan.feedhub.Navigation.Destinations.account.route){
                Account(navHostController = navHostController)
            }
            composable(dev.charan.feedhub.Navigation.Destinations.appiconchange.route){
                AppIconChange(navHostController)
            }
            composable(dev.charan.feedhub.Navigation.Destinations.search.route){
                SearchScreen(navHostController = navHostController)
            }
            composable(dev.charan.feedhub.Navigation.Destinations.verifyotp.route, arguments = listOf(navArgument("email"){
                type=NavType.StringType
            })){
                it.arguments?.getString("email")
                    ?.let { it1 -> VerifyOTP(navHostController = navHostController, it1) }
            }



        }
    }


