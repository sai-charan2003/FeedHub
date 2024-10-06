package dev.charan.feedhub.Utils

import android.annotation.SuppressLint
import android.content.Context


class SharedPref(private val context: Context) {

    companion object{
        val KEY_SHAREDPREF_KEY="key_sharedpref_key"
        @SuppressLint("StaticFieldLeak")
        @Volatile
        private var instance: SharedPref? = null

        fun getInstance(context: Context): SharedPref {
             return instance ?: synchronized(this) {
                  instance ?: SharedPref(context).also { instance = it }
               }
        }

    }
    var feedView
        get() = run {
            val sharedPreferences = context.getSharedPreferences(KEY_SHAREDPREF_KEY, Context.MODE_PRIVATE)
            sharedPreferences.getString(
                AppConstants.SharedPrefConstants.KEY_FEED_VIEW,
                AppConstants.FeedView.CARD
            )
        }
        set(FeedView){
            val sharedPreferences = context.getSharedPreferences(KEY_SHAREDPREF_KEY, Context.MODE_PRIVATE)
            sharedPreferences.edit().putString(
                AppConstants.SharedPrefConstants.KEY_FEED_VIEW,
                FeedView.toString()
            ).apply()

        }
    var timeFormat
        get() = run {
            val sharedPreferences = context.getSharedPreferences(KEY_SHAREDPREF_KEY, Context.MODE_PRIVATE)
            sharedPreferences.getBoolean(AppConstants.SharedPrefConstants.KEY_24_HOURS,true)
        }
        set(timeFormat){
            val sharedPreferences = context.getSharedPreferences(KEY_SHAREDPREF_KEY, Context.MODE_PRIVATE)
            sharedPreferences.edit().putBoolean(AppConstants.SharedPrefConstants.KEY_24_HOURS,timeFormat).apply()

        }
    var hapticEnabled
        get() = run {
            val sharedPreferences = context.getSharedPreferences(KEY_SHAREDPREF_KEY, Context.MODE_PRIVATE)
            sharedPreferences.getBoolean(AppConstants.SharedPrefConstants.KEY_HAPTICS_ENABLED,true)
        }
        set(hapticEnabled){
            val sharedPreferences = context.getSharedPreferences(KEY_SHAREDPREF_KEY, Context.MODE_PRIVATE)
            sharedPreferences.edit().putBoolean(AppConstants.SharedPrefConstants.KEY_HAPTICS_ENABLED,hapticEnabled).apply()

        }
    var showImages
        get() = run {
            val sharedPreferences = context.getSharedPreferences(KEY_SHAREDPREF_KEY, Context.MODE_PRIVATE)
            sharedPreferences.getBoolean(AppConstants.SharedPrefConstants.KEY_SHOW_IMAGES,true)
        }
        set(showImages){
            val sharedPreferences = context.getSharedPreferences(KEY_SHAREDPREF_KEY, Context.MODE_PRIVATE)
            sharedPreferences.edit().putBoolean(AppConstants.SharedPrefConstants.KEY_SHOW_IMAGES,showImages).apply()

        }
    var showScrollToTop
        get() = run {
            val sharedPreferences = context.getSharedPreferences(KEY_SHAREDPREF_KEY, Context.MODE_PRIVATE)
            sharedPreferences.getBoolean(AppConstants.SharedPrefConstants.KEY_SHOW_SCROLL_TO_TOP,true)
        }
        set(showScrollToTop){
            val sharedPreferences = context.getSharedPreferences(KEY_SHAREDPREF_KEY, Context.MODE_PRIVATE)
            sharedPreferences.edit().putBoolean(AppConstants.SharedPrefConstants.KEY_SHOW_SCROLL_TO_TOP,showScrollToTop).apply()

        }
    var inAppBrowser
        get() = run {
            val sharedPreferences = context.getSharedPreferences(KEY_SHAREDPREF_KEY, Context.MODE_PRIVATE)
            sharedPreferences.getBoolean(AppConstants.SharedPrefConstants.KEY_IN_APP_BROWSER,true)
        }
        set(inAppBrowser){
            val sharedPreferences = context.getSharedPreferences(KEY_SHAREDPREF_KEY, Context.MODE_PRIVATE)
            sharedPreferences.edit().putBoolean(AppConstants.SharedPrefConstants.KEY_IN_APP_BROWSER,inAppBrowser).apply()

        }
    var isLoggedIn
        get() = run {
            val sharedPreferences = context.getSharedPreferences(KEY_SHAREDPREF_KEY, Context.MODE_PRIVATE)
            sharedPreferences.getBoolean(AppConstants.SharedPrefConstants.KEY_IS_LOGGED_IN,false)
        }
        set(isLoggedIn){
            val sharedPreferences = context.getSharedPreferences(KEY_SHAREDPREF_KEY, Context.MODE_PRIVATE)
            sharedPreferences.edit().putBoolean(AppConstants.SharedPrefConstants.KEY_IS_LOGGED_IN,isLoggedIn).apply()

        }

    var AuthToken
        get() = run {
            val sharedPreferences = context.getSharedPreferences(KEY_SHAREDPREF_KEY, Context.MODE_PRIVATE)
            sharedPreferences.getString(AppConstants.SharedPrefConstants.KEY_AUTH_TOKEN,"")
        }
        set(AuthToken){
            val sharedPreferences = context.getSharedPreferences(KEY_SHAREDPREF_KEY, Context.MODE_PRIVATE)
            sharedPreferences.edit().putString(
                AppConstants.SharedPrefConstants.KEY_AUTH_TOKEN,
                AuthToken.toString()
            ).apply()

        }
    var EnabledActivity
        get() = run {
            val sharedPreferences = context.getSharedPreferences(KEY_SHAREDPREF_KEY, Context.MODE_PRIVATE)
            sharedPreferences.getString(AppConstants.SharedPrefConstants.KEY_ENABLED_ACTIVITY,"dev.charan.feedhub.MainActivity")
        }
        set(EnabledActivity){
            val sharedPreferences = context.getSharedPreferences(KEY_SHAREDPREF_KEY, Context.MODE_PRIVATE)
            sharedPreferences.edit().putString(
                AppConstants.SharedPrefConstants.KEY_ENABLED_ACTIVITY,
                EnabledActivity.toString()
            ).apply()

        }
    var isAnonymousSignin
        get() = run {
            val sharedPreferences = context.getSharedPreferences(KEY_SHAREDPREF_KEY, Context.MODE_PRIVATE)
            sharedPreferences.getBoolean(AppConstants.SharedPrefConstants.KEY_ANONYMOUS_SIGNIN,false)
        }
        set(isAnonymousSignin){
            val sharedPreferences = context.getSharedPreferences(KEY_SHAREDPREF_KEY, Context.MODE_PRIVATE)
            sharedPreferences.edit().putBoolean(
                AppConstants.SharedPrefConstants.KEY_ANONYMOUS_SIGNIN,
                isAnonymousSignin

            ).apply()

        }




    fun clearSharedPrefs(){
        val sharedPreferences=context.getSharedPreferences(KEY_SHAREDPREF_KEY,Context.MODE_PRIVATE)
        val edit=sharedPreferences.edit()
        edit.clear().apply()
    }


}