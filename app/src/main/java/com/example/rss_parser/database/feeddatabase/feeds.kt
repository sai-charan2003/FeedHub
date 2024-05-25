package com.example.rss_parser.database.feeddatabase

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Fts4
import androidx.room.Index
import androidx.room.PrimaryKey


@Entity(tableName = "feeds_fts")
@Fts4(contentEntity = feeds::class)
data class feeds_fts(
    @ColumnInfo(name="rowid")
    @PrimaryKey(autoGenerate = true)
    val id:Int,
    val feedtitle:String,

)
@Entity(
    tableName = "feeds",
    indices = [Index(value = ["feedlink"], unique = true)],

)
data class feeds(
    @PrimaryKey(autoGenerate = true)
    val id:Int,
    val feedlink:String?,
    val feedtitle:String?,
    val date:String?,
    val imageurl:String?,
    val opened:String?,
    val website:String?,
    val description:String?,
    val websiteTitle:String?,
    val websiteFavicon:String?,
    val isWebsiteFav:Boolean?=null

    )

data class websiteTileAndLink(
    val websiteTitle: String?,
    val website:String?,
    val websiteFavicon:String?,
)
data class websiteTitleAndFavicon(
    val websiteTitle: String?,
    val websiteFavicon:String?,

)

data class onSlide(
    val id:Int?,
    val opened: String?
)

