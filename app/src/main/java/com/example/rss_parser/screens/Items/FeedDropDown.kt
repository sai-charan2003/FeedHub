package com.example.rss_parser.screens.Items

import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
//import androidx.compose.material3.Text
//import androidx.compose.runtime.Composable
//import androidx.compose.ui.text.style.TextAlign
//import androidx.navigation.NavHostController
//import com.example.rss_parser.Utils.AppConstants
//
//@Composable
//fun FeedDropDown(showdropdownmenu:Boolean,onClick:()->Unit){
//    DropdownMenu(
//        expanded = showdropdownmenu,
//        onDismissRequest = { onClick()}) {
//        FeedDropDown.values().forEach {
//            val dropdownitem= when(it){
//                HomeScreenDropDown.SETTINGS -> AppConstants.HomeScreenDropDownItems.SETTINGS
//                HomeScreenDropDown.ADD_NEW_FEED -> AppConstants.HomeScreenDropDownItems.ADD_NEW_FEED
//                HomeScreenDropDown.BOOKMARKS -> AppConstants.HomeScreenDropDownItems.BOOKMARKS
//            }
//            DropdownMenuItem(
//                text = {
//                    Text(
//                        dropdownitem,
//                        textAlign = TextAlign.Center
//                    )
//                },
//                onClick = {
//                    onClick()
//
//
//
//
//                },
//
//
//
//                )
//        }
//
//
//
//    }
//}
//enum class FeedDropDown{
//    Mark_AS_READ,
//    SHARE,
//    MARK_AS_UNREAD
//
//}