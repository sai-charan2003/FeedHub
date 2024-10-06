package dev.charan.feedhub.Navigation

sealed class Destinations(val route:String) {
    object home: dev.charan.feedhub.Navigation.Destinations("Home")
    object settings: dev.charan.feedhub.Navigation.Destinations("Settings")
    object addnewfeed: dev.charan.feedhub.Navigation.Destinations("Add new feed")
    object feeds: dev.charan.feedhub.Navigation.Destinations("feeds")
    object bookmarks: dev.charan.feedhub.Navigation.Destinations("Bookmarks")
    object aboutdeveloper: dev.charan.feedhub.Navigation.Destinations("aboutdeveloper")
    object aboutapp: dev.charan.feedhub.Navigation.Destinations("AboutApp")
    object signup: dev.charan.feedhub.Navigation.Destinations("signup")
    object enterscreen: dev.charan.feedhub.Navigation.Destinations("enterscreen")
    object signinscreen: dev.charan.feedhub.Navigation.Destinations("signin")
    object password_recover: dev.charan.feedhub.Navigation.Destinations("password/{email}")
    object account: dev.charan.feedhub.Navigation.Destinations("account")
    object appiconchange: dev.charan.feedhub.Navigation.Destinations("appiconchange")
    object search: dev.charan.feedhub.Navigation.Destinations("search")
    object verifyotp : dev.charan.feedhub.Navigation.Destinations("verifyotp/{email}")
    object licenses : dev.charan.feedhub.Navigation.Destinations("Licenses")





}