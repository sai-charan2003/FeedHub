package com.example.rss_parser.Utils

sealed class DistinctLinkForGoogleNewsState {
    data class Success(val googleNewsData : List<GoogleNews>) : DistinctLinkForGoogleNewsState()
    object Loading: DistinctLinkForGoogleNewsState()
    data class Error(val error:String): DistinctLinkForGoogleNewsState()
}

data class GoogleNews(
    val URL:String?,
    val SourceName:String?
)