package dev.charan.feedhub.database.Implementation

import android.util.Log
import androidx.lifecycle.LiveData
import dev.charan.feedhub.database.Repository.feedRepository
import dev.charan.feedhub.database.feeddatabase.feeds
import dev.charan.feedhub.database.feeddatabase.websiteTileAndLink
import dev.charan.feedhub.database.feeddatabase.websiteTitleAndFavicon
import kotlinx.coroutines.flow.Flow


class feedDAOImp(private val feedRepository: feedRepository) {
    val allfeeds:LiveData<List<feeds>> = feedRepository.getdata()

    suspend fun insert(feeds: feeds) {
        feedRepository.insert(feeds)
    }

    suspend fun update(feeds: feeds){
        feedRepository.update(feeds)

    }
    suspend fun delete(feeds: feeds){
        feedRepository.delete(feeds)
    }
    suspend fun clear(){
        feedRepository.clear()
    }
    suspend fun deleteFeedsByWebsite(website: String) {
        feedRepository.deleteFeedsByWebsite(website)
    }
    suspend fun UpdateOpened(id:Int,opened:String){
        Log.d("TAG", "UpdateOpened: $id")
        feedRepository.UpdateOpened(id,opened)
    }
    fun search(query:String) : Flow<List<feeds>> {
        return feedRepository.searchbytitle(query)
    }
    fun getfeedbyid(id:Int): feeds {

        return feedRepository.getfeedbyid(id)
    }
    suspend fun DeleteListOfData(id:List<Int>){
        return feedRepository.DeleteListOfData(id)
    }
    fun SelectDistinctWebsiteTiiles():Flow<List<websiteTitleAndFavicon?>>{
        return feedRepository.SelectDistinctWebsiteNames()
    }
    fun selectDistinctWebsiteTitleAndLink():Flow<List<websiteTileAndLink>>{
        return feedRepository.selectDistinctWebsiteData()
    }
    fun selectDistinctWebsites():Flow<List<String?>>{
        return feedRepository.selectDistinctWebsite()
    }
    fun selectDistinctWebsitesWithOutFlow():List<String?>{
        return feedRepository.selectDistinctWebsiteWithOutFlow()
    }

    fun updateIsWebsiteFav(isWebsiteFav:Boolean,websiteTitle:String){
        feedRepository.updateIsWebsiteFav(isWebsiteFav, websiteTitle)
    }


}