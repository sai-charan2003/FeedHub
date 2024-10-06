package dev.charan.feedhub.screens.Items

//import androidx.compose.material3.Text
//import androidx.compose.runtime.Composable
//import androidx.compose.ui.text.style.TextAlign
//import androidx.navigation.NavHostController
//import dev.charan.feedhub.Utils.AppConstants
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