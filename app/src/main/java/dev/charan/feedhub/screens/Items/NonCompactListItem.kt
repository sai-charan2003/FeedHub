package dev.charan.feedhub.screens.Items
import android.content.Intent
import android.os.Vibrator
import android.widget.Toast
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.DismissValue
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material.icons.twotone.MarkAsUnread
import androidx.compose.material.icons.twotone.MarkEmailRead
import androidx.compose.material.icons.twotone.Share
import androidx.compose.material.rememberDismissState
import androidx.compose.material3.CircularProgressIndicator

import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text


import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import dev.charan.feedhub.R

import dev.charan.feedhub.Utils.DateTimeFormatter.formatDate
import dev.charan.feedhub.Utils.ProcessState
import dev.charan.feedhub.Utils.SharedPref
import dev.charan.feedhub.Utils.openTab

import dev.charan.feedhub.database.feeddatabase.feeds
import dev.charan.feedhub.database.feeddatabase.onSlide
import dev.charan.feedhub.screens.Components.CustomIconButton
import dev.charan.feedhub.screens.Components.CustomIconButtonForPainter
import dev.charan.feedhub.screens.Components.CustomSwipeAction
import dev.charan.feedhub.viewmodel.viewmodel
import me.saket.swipe.SwipeableActionsBox

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterialApi::class)
@Composable
fun NonCompactListItem(sortedFeedsData: feeds, onRemove : (onSlide)->Unit){
    val context=LocalContext.current
    var aisummarypage by remember {
        mutableStateOf(false)
    }
    val viewModel = viewModel<viewmodel>(
        factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return viewmodel(
                    context
                ) as T
            }
        }
    )
    lateinit var alignment: Alignment
    var isimageloaded by remember {mutableStateOf(false)}
    val lifecycle= LocalLifecycleOwner.current
    val timings: LongArray = longArrayOf(100, 100, 100, 100, 100, 100,)
    val amplitudes: IntArray = intArrayOf(23, 41, 65, 103, 160, 255,)
    val repeatIndex = -1 // Do not repeat.

    val vibrator = LocalContext.current.getSystemService(Vibrator::class.java)
    var loading by remember {
        mutableStateOf(false)

    }
    var summary by remember {
        mutableStateOf("")
    }
    val haptic = LocalHapticFeedback.current
    val bookmarkdata by viewModel.bookmarkData.collectAsState()
    val bookmarklink = bookmarkdata?.map { it.websitelink }
    val SharedPref= SharedPref(context)

    val showimages by remember {
        mutableStateOf(SharedPref.showImages)
    }
    var ishapticenabled by remember {
        mutableStateOf(SharedPref.hapticEnabled)
    }
    var coroutinescope = rememberCoroutineScope()
    val uriHandler = LocalUriHandler.current
    val swipeToDismiss = rememberDismissState(
        initialValue = DismissValue.Default,
        confirmStateChange = {
            if(it==DismissValue.DismissedToStart){
                DismissValue.Default
                onRemove(onSlide(sortedFeedsData.id,sortedFeedsData.opened))
            }
            true
        }

    )

    val density = LocalDensity.current
    val defaultActionSize = 80.dp
    val endActionSizePx = with(density) { (defaultActionSize * 2).toPx() }
    val startActionSizePx = with(density) { defaultActionSize.toPx() }
    val markAsRead =  if(sortedFeedsData.opened=="false") {
        CustomSwipeAction(
            icon = rememberVectorPainter(image = Icons.TwoTone.MarkEmailRead),
            background = MaterialTheme.colorScheme.surfaceContainerLow,
            onSwipe = {
                viewModel.Updateopened(sortedFeedsData.id,"true")
            }

        )
    } else{
        CustomSwipeAction(
            icon = rememberVectorPainter(image = Icons.TwoTone.MarkAsUnread),
            background = MaterialTheme.colorScheme.surfaceContainerLow,
            onSwipe = {
                viewModel.Updateopened(sortedFeedsData.id,"false")
            }

        )

    }
    val sendIntent: Intent =
        Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(
                Intent.EXTRA_TEXT,
                sortedFeedsData.feedlink
            )
            type = "text/plain"
        }
    val swipeShare= CustomSwipeAction(
        icon = rememberVectorPainter(image = Icons.TwoTone.Share),
        background = MaterialTheme.colorScheme.surfaceContainerLow,
        onSwipe = {
            val shareIntent =
                Intent.createChooser(
                    sendIntent,
                    null
                )
            context.startActivity(
                shareIntent
            )

        }
    )



    



    SwipeableActionsBox(
        endActions = listOf(markAsRead),
        backgroundUntilSwipeThreshold = MaterialTheme.colorScheme.surfaceContainerHigh,
        swipeThreshold = 80.dp,
        startActions = listOf(swipeShare)


        ) {
        ListItem(

            {

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(top = 10.dp, bottom = 10.dp)

                ) {
                    if (sortedFeedsData.imageurl != null) {
                        if (showimages) {

                            Box(
                                modifier = Modifier.padding(bottom = 10.dp)

                            ) {
                                AsyncImage(
                                    model = sortedFeedsData.imageurl,
                                    onSuccess = {
                                        isimageloaded = true
                                    },


                                    contentScale = ContentScale.Crop,
                                    contentDescription = "null",
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .requiredHeight(220.dp)
                                        .clip(RoundedCornerShape(20.dp))

                                        .then(
                                            if (!isimageloaded) {
                                                Modifier
                                                    .background(MaterialTheme.colorScheme.surfaceContainerLow)


                                            } else {
                                                Modifier
                                            }
                                        ),


                                    colorFilter =
                                    if (sortedFeedsData.opened == "true"
                                    ) {


                                        ColorFilter.colorMatrix(
                                            ColorMatrix().apply {
                                                setToScale(
                                                    0.5f,
                                                    0.5f,
                                                    0.5f,
                                                    1f
                                                )
                                            })
                                    } else {
                                        null
                                    }
                                )


                            }

                        }
                    }

                    Column {


                        sortedFeedsData.let { it1 ->
                            Text(
                                text = it1.feedtitle!!,
                                modifier = Modifier.padding(

                                ),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight =
                                if (sortedFeedsData.opened == "true"
                                ) {


                                    FontWeight.W100
                                } else {
                                    FontWeight.Bold

                                },
                                textAlign = TextAlign.Justify
                            )
                        }
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(top = 5.dp)
                        ) {
                            WebsiteLogo(
                                websiteFavicon = sortedFeedsData.websiteFavicon!!,
                                isOpened = sortedFeedsData.opened!!
                            )
                            Text(
                                text = sortedFeedsData.websiteTitle!!,
                                modifier = Modifier
                                    .padding(start = 5.dp)
                                    .widthIn(max = 100.dp),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Light,
                                textAlign = TextAlign.Justify
                            )
                            Text(
                                text = " • ",
                                modifier = Modifier,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Light,
                                textAlign = TextAlign.Justify
                            )


                            Text(
                                text = formatDate(sortedFeedsData.date!!),
                                modifier = Modifier.weight(1f),
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Light,
                                textAlign = TextAlign.Justify
                            )




                            if (sortedFeedsData
                                    ?.let { it2 ->
                                        bookmarklink?.contains(
                                            it2.feedlink
                                        )
                                    } == true
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    if (loading) {
                                        CircularProgressIndicator(
                                            strokeCap = StrokeCap.Round,
                                            modifier = Modifier.size(30.dp)
                                        )
                                    }


                                    CustomIconButton(
                                        icon = Icons.Filled.Bookmark,
                                        contentDescription = null,
                                        onClick = {
                                            loading = true
                                        }
                                    )
                                }

                            } else {
                                Box(contentAlignment = Alignment.Center) {
                                    if (loading) {
                                        CircularProgressIndicator(
                                            strokeCap = StrokeCap.Round,
                                            modifier = Modifier.size(30.dp)
                                        )
                                    }

                                    CustomIconButton(
                                        icon = Icons.Filled.BookmarkBorder,
                                        contentDescription = null,
                                        onClick = {
                                            loading = true
                                        }
                                    )


                                }
                            }
                            Spacer(modifier = Modifier.padding(start = 4.dp))
                            CustomIconButton(
                                icon = Icons.Outlined.Share,

                                modifier = Modifier,
                                onClick = {

                                        haptic.performHapticFeedback(
                                            HapticFeedbackType.LongPress
                                        )

                                    val shareIntent =
                                        Intent.createChooser(
                                            sendIntent,
                                            null
                                        )
                                    context.startActivity(
                                        shareIntent
                                    )
                                }


                            )
                            Spacer(modifier = Modifier.padding(start = 4.dp))
                            CustomIconButtonForPainter(
                                icon = painterResource(
                                    id = R.drawable.vector
                                ),
                                onClick = {
                                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                    aisummarypage = true
                                },


                                )


                        }
                    }
                }

            },

            modifier = Modifier.combinedClickable(
                onClick = {
                    if (SharedPref.inAppBrowser
                    ) {
                        viewModel.Updateopened(sortedFeedsData.id!!, "true")
                        openTab(
                            context,
                            sortedFeedsData.feedlink!!
                        )
                    } else {
                        uriHandler.openUri(sortedFeedsData.feedlink!!)
                        viewModel.Updateopened(sortedFeedsData.id!!, "false")

                    }
                },



            )

        )
    }
    if(aisummarypage){
        SummaryModalBottomSheet(
            feedlink = sortedFeedsData.feedlink!!,
            viewmodel = viewModel,
            ishapticenabled = ishapticenabled
        ){
            aisummarypage=false
        }
    }
    if(loading){
        if (sortedFeedsData
                ?.let { it2 ->
                    bookmarklink!!.contains(
                        it2.feedlink
                    )
                } == true
        ) {
            viewModel.DeleteBookmarkFromSupabase(sortedFeedsData.feedlink!!).observe(lifecycle){
                when(it){
                    is ProcessState.Error -> {
                        loading=false
                        Toast.makeText(context,it.error, Toast.LENGTH_LONG).show()

                    }
                    ProcessState.Loading -> {
                        loading=true
                    }
                    ProcessState.Success -> {
                        loading=false
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        viewModel.deleteBookmarkFromLocalDB(sortedFeedsData.feedlink)
                    }
                }
            }


        } else {

            viewModel.UpdateBookmarksInSupaBase(sortedFeedsData.id!!).observe(lifecycle){
                when(it){
                    is ProcessState.Error -> {
                        loading=false
                        Toast.makeText(context,it.error, Toast.LENGTH_LONG).show()
                    }
                    ProcessState.Loading -> {
                        loading=true

                    }
                    ProcessState.Success -> {
                        loading=false
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        viewModel.insertBookmarkInLocalDBWithID(sortedFeedsData.id)
                    }
                }
            }


        }

    }

}
enum class DragAnchors {
    Start,
    Center,
    End,
}