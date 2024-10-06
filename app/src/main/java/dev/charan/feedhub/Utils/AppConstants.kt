package dev.charan.feedhub.Utils

class AppConstants {
    object SharedPrefConstants{
        val KEY_SHAREDPREF_KEY="key_sharedpref_key"
        val KEY_24_HOURS="key_24_hours"
        val KEY_SHOW_IMAGES="key_show_images"
        val KEY_HAPTICS_ENABLED="key_haptics_enabled"
        val KEY_FEED_VIEW="key_feed_view"
        val KEY_SHOW_SCROLL_TO_TOP="key_show_scroll_to_top"
        val KEY_IN_APP_BROWSER="key_in_app_browser"
        val KEY_IS_LOGGED_IN="key_is_logged_in"
        val KEY_AUTH_TOKEN="key_auth_token"
        val KEY_ANONYMOUS_SIGNIN="key_anonymous_signin"
        val KEY_ENABLED_ACTIVITY="key_enabled_activity"
    }

    object FeedView{
        val CARD="Card"
        val Compact="Compact"
        val TEXT_ONLY="Text-Only"
    }
    object HomeScreenDropDownItems{
        val SETTINGS="Settings"
        val ADD_NEW_FEED="Add new feed"
        val BOOKMARKS="Bookmarks"
    }
    object Constants {
        val DELETE_OLDER_ENTRIES="DeleteOlderEntries"
        val UPDATE_LOCAL_DATABASE="UpdateLocalDatabase"
    }
}