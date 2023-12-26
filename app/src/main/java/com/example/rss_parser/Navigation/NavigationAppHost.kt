package com.example.rss_parser.Navigation

import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut

import androidx.compose.runtime.Composable
import androidx.compose.animation.AnimatedContentTransitionScope.SlideDirection
import androidx.compose.animation.core.EaseIn
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.rss_parser.screens.aboutapp
import com.example.rss_parser.screens.aboutdeveloper
import com.example.rss_parser.screens.addfeed
import com.example.rss_parser.screens.booksmarks
import com.example.rss_parser.screens.feeds
import com.example.rss_parser.screens.homescreen
import com.example.rss_parser.screens.settings


@Composable
fun NavigationAppHost(navHostController: NavHostController) {
    NavHost(navController = navHostController, startDestination = Destinations.home.route,
        enterTransition = { fadeIn() + slideIntoContainer(SlideDirection.Start, initialOffset = {100}, animationSpec = (tween(easing = LinearEasing, durationMillis = 200)) ) },
        exitTransition = { fadeOut() + slideOutOfContainer(SlideDirection.Start, targetOffset = {-100},animationSpec = (tween(easing = LinearEasing, durationMillis = 200))) },
        popEnterTransition = { fadeIn() + slideIntoContainer(SlideDirection.End,initialOffset = {-100},animationSpec = (tween(easing = LinearEasing, durationMillis = 200))) },
        popExitTransition = { fadeOut() + slideOutOfContainer(SlideDirection.End, targetOffset = {100},animationSpec = (tween(easing = LinearEasing, durationMillis = 200))) },
        )

    {
        composable(Destinations.home.route,  ){
            homescreen(navHostController = navHostController)
        }
        composable(Destinations.addnewfeed.route,){
            addfeed(navHostController)
        }
        composable(Destinations.settings.route){
            settings(navHostController = navHostController)

        }
        composable(Destinations.feeds.route){
            feeds(navHostController = navHostController)
        }
        composable(Destinations.bookmarks.route){
            booksmarks(navHostController = navHostController)
        }
        composable(Destinations.aboutdeveloper.route){
            aboutdeveloper(navHostController = navHostController)
        }
        composable(Destinations.aboutapp.route){
            aboutapp(navHostController = navHostController)
        }


    }
}