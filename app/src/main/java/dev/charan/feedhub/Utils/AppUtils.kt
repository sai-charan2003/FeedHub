package dev.charan.feedhub.Utils

import android.content.ComponentName
import android.content.Context
import android.content.pm.PackageManager
import dev.charan.feedhub.database.feeddatabase.Bookmarks
import dev.charan.feedhub.database.feeddatabase.feeds

object AppUtils {
    fun bookmarkToFeedMapper(bookmarks: Bookmarks):feeds{
        return feeds(
            id=bookmarks.id!!,
            feedlink = bookmarks.websitelink,
            feedtitle = bookmarks.title,
            date = bookmarks.date,
            opened = "false",
            website = bookmarks.website,
            description = null,
            websiteTitle = bookmarks.websiteTitle,
            websiteFavicon = bookmarks.websiteFavicon,
            imageurl = bookmarks.images


        )

    }
    fun changeIcon(packageManager: PackageManager, context: Context, enabled:String, disabled:String) {

        packageManager.setComponentEnabledSetting(
            ComponentName(
                context,
                enabled
            ),
            PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
            PackageManager.DONT_KILL_APP
        )

        packageManager.setComponentEnabledSetting(
            ComponentName(
                context,
                disabled
            ),
            PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
            PackageManager.DONT_KILL_APP
        )
    }


}