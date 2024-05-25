package com.example.rss_parser.Utils

import android.content.Context
import android.util.Log
import androidx.glance.appwidget.updateAll
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.example.rss_parser.Widgets.FeedWidget

import com.example.rss_parser.viewmodel.viewmodel
import java.util.concurrent.TimeUnit

class AddFeedsWorker(context : Context, parameterName: WorkerParameters) : CoroutineWorker(context,parameterName){
    val context=context
    val SharedPref= SharedPref(context)
    val viewModel=viewmodel(context)

    override suspend fun doWork(): Result {
        try {
            viewModel.getFeedWebsitesFromSupabase()
            Log.d("TAG", "doWork: success from add feed")
            FeedWidget.updateAll(context)
        } catch (e:Exception){
            Log.e("WorkerManager",e.message.toString())
            Result.retry()
        }

        return Result.success()
    }
    companion object {
        fun setup(){
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)


                .build()
            val request= PeriodicWorkRequestBuilder<AddFeedsWorker>(
                15,
                TimeUnit.MINUTES
            )
                .setConstraints(constraints)

                .build()
            WorkManager.getInstance().enqueueUniquePeriodicWork(
                AppConstants.Constants.UPDATE_LOCAL_DATABASE,
                ExistingPeriodicWorkPolicy.CANCEL_AND_REENQUEUE,
                request
            )
        }
    }
}
