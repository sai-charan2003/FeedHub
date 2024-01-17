package com.example.rss_parser.Navigation

sealed class Destinations(val route:String) {
    object home:Destinations("Home")
    object settings:Destinations("Settings")
    object addnewfeed:Destinations("addnewfeed")
    object feeds:Destinations("feeds")
    object bookmarks:Destinations("bookmarks")
    object aboutdeveloper:Destinations("aboutdeveloper")
    object aboutapp:Destinations("aboutapp")
    object signup:Destinations("signup")
    object enterscreen:Destinations("enterscreen")
    object signinscreen:Destinations("signin")
    object password_recover:Destinations("password/{email}")
    object account:Destinations("account")



}