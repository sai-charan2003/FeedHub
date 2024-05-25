package com.example.rss_parser.screens.Items

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.rss_parser.Utils.AppConstants
import com.example.rss_parser.Utils.SharedPref

@Composable
fun FeedViewListItem(
    SharedPref: SharedPref

){

    var showDropdown by remember {
        mutableStateOf(false)
    }
    var selecteditem by remember {
        mutableStateOf(SharedPref.feedView)
    }
    ListItem(

        {
            Row(
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    "Feed View",
                    modifier = Modifier.padding(top = 12.dp),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.weight(1f))
                TextButton(onClick = {showDropdown=true}, contentPadding = PaddingValues(3.dp)){

                    selecteditem?.let { it1 -> Text(it1) }
                    Icon(Icons.Filled.ArrowDropDown, contentDescription = null)
                    MaterialTheme(shapes = MaterialTheme.shapes.copy(extraSmall = RoundedCornerShape(16.dp))) {
                        DropdownMenu(
                            expanded = showDropdown,

                            onDismissRequest = { showDropdown = false },

                            ) {
                            FeedView.values().forEach {
                                val feedview=when(it){
                                    FeedView.CARD -> AppConstants.FeedView.CARD
                                    FeedView.COMPACT -> AppConstants.FeedView.Compact
                                    FeedView.TEXT_ONLY -> AppConstants.FeedView.TEXT_ONLY
                                }
                                DropdownMenuItem(
                                    text = {
                                        Text(
                                            feedview,
                                            textAlign = TextAlign.Center
                                        )
                                    },
                                    onClick = {
                                        showDropdown=false
                                        SharedPref.feedView=feedview
                                        selecteditem=feedview
                                        if(selecteditem== AppConstants.FeedView.TEXT_ONLY){
                                            SharedPref.showImages=false
                                        }
                                        else{
                                            SharedPref.showImages=true
                                        }



                                    },
                                    modifier = if (selecteditem == feedview) {
                                        Modifier.background(MaterialTheme.colorScheme.surfaceTint.copy(alpha = 0.3f))
                                    } else {
                                        Modifier
                                    }


                                )

                            }
                        }
                    }

                }

            }
        }
        ,
        modifier= Modifier.clickable {
            showDropdown=true
        }
    )
}
enum class FeedView {
    CARD,
    COMPACT,
    TEXT_ONLY,
}