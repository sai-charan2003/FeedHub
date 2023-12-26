package com.example.rss_parser.Navigation

sealed class Destinations(val route:String) {
    object home:Destinations("Home")
    object settings:Destinations("Settings")
    object addnewfeed:Destinations("addnewfeed")
    object feeds:Destinations("feeds")
    object bookmarks:Destinations("bookmarks")
    object aboutdeveloper:Destinations("aboutdeveloper")
    object aboutapp:Destinations("aboutapp")

}