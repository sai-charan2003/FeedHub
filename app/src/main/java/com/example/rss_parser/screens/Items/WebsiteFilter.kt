package com.example.rss_parser.screens.Items

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.InputChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.rss_parser.database.feeddatabase.websiteTitleAndFavicon


@Composable

fun WebsiteUrlList(websiteTitles: List<websiteTitleAndFavicon?>,selectedCategory: String?, onCategorySelected: (String?) -> Unit) {
    LazyRow {
        items(websiteTitles.size) { index ->
            val websiteTitle = websiteTitles[index]
            val isSelected = (websiteTitle?.websiteTitle == selectedCategory)

            InputChip(
                selected = isSelected,
                onClick = {
                    onCategorySelected(if (isSelected) null else websiteTitle?.websiteTitle)
                },
                label = {
                    Text(websiteTitle!!.websiteTitle!!,overflow = TextOverflow.Ellipsis,maxLines = 1)
                },
                avatar = {
                         AsyncImage(
                             model = websiteTitle?.websiteFavicon,
                             contentDescription = null,
                             modifier = Modifier
                                 .requiredSize(24.dp)
                                 .clip(RoundedCornerShape(20.dp)),
                             )
                },

                modifier = Modifier.padding(start = 10.dp).widthIn(max=200.dp)
            )
        }
    }
}