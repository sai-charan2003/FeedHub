package com.example.rss_parser.rssdata

import androidx.compose.runtime.Stable

@Stable

data class RssData(
    val links: List<String>,
    val titles: List<String>,
    val dates: List<String>,
    val images: List<String>,

)