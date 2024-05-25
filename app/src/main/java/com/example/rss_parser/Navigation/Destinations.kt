package com.example.rss_parser.Navigation

sealed class Destinations(val route:String) {
    object home:Destinations("Home")
    object settings:Destinations("Settings")
    object addnewfeed:Destinations("Add new feed")
    object feeds:Destinations("feeds")
    object bookmarks:Destinations("Bookmarks")
    object aboutdeveloper:Destinations("aboutdeveloper")
    object aboutapp:Destinations("AboutApp")
    object signup:Destinations("signup")
    object enterscreen:Destinations("enterscreen")
    object signinscreen:Destinations("signin")
    object password_recover:Destinations("password/{email}")
    object account:Destinations("account")
    object appiconchange:Destinations("appiconchange")
    object search:Destinations("search")
    object verifyotp :Destinations("verifyotp/{email}")





}