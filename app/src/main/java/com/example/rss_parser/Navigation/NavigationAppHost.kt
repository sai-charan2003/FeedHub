package com.example.rss_parser.Navigation

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.util.Log
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut

import androidx.compose.runtime.Composable
import androidx.compose.animation.AnimatedContentTransitionScope.SlideDirection
import androidx.compose.animation.core.EaseIn
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import com.example.rss_parser.screens.aboutapp
import com.example.rss_parser.screens.aboutdeveloper
import com.example.rss_parser.screens.account
import com.example.rss_parser.screens.addfeed
import com.example.rss_parser.screens.booksmarks
import com.example.rss_parser.screens.enterscreen
import com.example.rss_parser.screens.feeds
import com.example.rss_parser.screens.homescreen
import com.example.rss_parser.screens.password_change

import com.example.rss_parser.screens.settings
import com.example.rss_parser.screens.signin
import com.example.rss_parser.screens.signup


@Composable
fun NavigationAppHost(navHostController: NavHostController) {
    val context = LocalContext.current
    val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("showimages", Context.MODE_PRIVATE)
    val islog = sharedPreferences.getBoolean("islog", false)
    Log.d("TAG", "NavigationAppHost: $islog")
    if (islog) {
        NavHost(
            navController = navHostController,

            startDestination = Destinations.home.route,
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
                homescreen(navHostController = navHostController)
            }
            composable(Destinations.addnewfeed.route,) {
                addfeed(navHostController)
            }
            composable(Destinations.settings.route) {
                settings(navHostController = navHostController)

            }
            composable(Destinations.feeds.route) {
                feeds(navHostController = navHostController)
            }
            composable(Destinations.bookmarks.route) {
                booksmarks(navHostController = navHostController)
            }
            composable(Destinations.aboutdeveloper.route) {
                aboutdeveloper(navHostController = navHostController)
            }
            composable(Destinations.aboutapp.route) {
                aboutapp(navHostController = navHostController)
            }
            composable(Destinations.signup.route) {
                signup(navHostController)
            }
            composable(Destinations.enterscreen.route){
                enterscreen(navHostController = navHostController)
            }
            composable(Destinations.signinscreen.route){
                signin(navHostController = navHostController)
            }
            composable(Destinations.password_recover.route,
                deepLinks = listOf(
                    navDeepLink {
                        uriPattern="https://sai-charan2003.github.io/"
                        action= Intent.ACTION_VIEW
                    }
                ),
            ){
                password_change(navHostController)

            }
            composable(Destinations.account.route){
                account(navHostController = navHostController)
            }


        }
    }
    else{
        NavHost(
            navController = navHostController,

            startDestination = Destinations.enterscreen.route,
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
                homescreen(navHostController = navHostController)
            }
            composable(Destinations.addnewfeed.route,) {
                addfeed(navHostController)
            }
            composable(Destinations.settings.route) {
                settings(navHostController = navHostController)

            }
            composable(Destinations.feeds.route) {
                feeds(navHostController = navHostController)
            }
            composable(Destinations.bookmarks.route) {
                booksmarks(navHostController = navHostController)
            }
            composable(Destinations.aboutdeveloper.route) {
                aboutdeveloper(navHostController = navHostController)
            }
            composable(Destinations.aboutapp.route) {
                aboutapp(navHostController = navHostController)
            }
            composable(Destinations.signup.route) {
                signup(navHostController)
            }
            composable(Destinations.enterscreen.route){
                enterscreen(navHostController = navHostController)
            }
            composable(Destinations.signinscreen.route){
                signin(navHostController = navHostController)
            }
            composable(Destinations.password_recover.route,
                deepLinks = listOf(
                    navDeepLink {
                        uriPattern="https://sai-charan2003.github.io/"
                        action= Intent.ACTION_VIEW
                    }
                ),
            ){
                password_change(navHostController)

            }
            composable(Destinations.account.route){
                account(navHostController = navHostController)
            }


        }
    }
    }

