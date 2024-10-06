package dev.charan.feedhub.Utils

sealed class AISummaryState {
    object Loading : AISummaryState()
    data class Success(val response:String) : AISummaryState()
    data class Error(val Error:String) : AISummaryState()


}