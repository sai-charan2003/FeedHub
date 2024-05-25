package com.example.rss_parser.Utils

import android.content.Context
import android.util.Log
import androidx.room.Room
import androidx.work.Constraints

import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType

import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.example.rss_parser.Utils.DateTimeFormatter.convertToMillisecondsEpoch
import com.example.rss_parser.database.feeddatabase.AppDatabase
import java.util.Calendar
import java.util.concurrent.TimeUnit

class DeleteFeedsWorker(context : Context, parameterName: WorkerParameters) : CoroutineWorker(context,parameterName){
    val context=context
    override suspend fun doWork(): Result {
        try {
            val db = Room.databaseBuilder(
                applicationContext,
                AppDatabase::class.java, "AppDatabase"
            ).build()

            val feedRepository = db.feedRepository().getnonlivedata()


            val oldFeedIds = feedRepository.let { feeds ->
                if (feeds.isNotEmpty()) {
                    val calendar = Calendar.getInstance()
                    calendar.add(Calendar.DAY_OF_YEAR, -31)
                    val oldtime = calendar.timeInMillis

                    feeds.mapIndexed { index, feed ->
                        Triple(index, feed, convertToMillisecondsEpoch(feed.date!!))
                    }.filter { (_, _, dateInMillis) ->
                        dateInMillis < oldtime
                    }.sortedByDescending { (_, _, dateInMillis) -> dateInMillis }
                        .map { (_, feed, _) -> feed.id }

                } else {
                    emptyList()
                }
            }

            if (oldFeedIds?.isNotEmpty() == true) {
                db.feedRepository().DeleteListOfData(oldFeedIds)
            }
            Log.d("TAG", "doWork: Delete Successful")
        } catch (e:Exception){
            Log.d("TAG", e.message.toString())
            return Result.retry()
        }

        return Result.success()
    }

    companion object{
        fun setup(){

            val request= PeriodicWorkRequestBuilder<DeleteFeedsWorker>(
                24,
                TimeUnit.HOURS
            ).build()
            WorkManager.getInstance().enqueueUniquePeriodicWork(
                AppConstants.Constants.DELETE_OLDER_ENTRIES,
                ExistingPeriodicWorkPolicy.UPDATE,
                request
            )
        }
    }
}