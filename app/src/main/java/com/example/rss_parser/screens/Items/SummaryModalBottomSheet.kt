package com.example.rss_parser.screens.Items

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.example.rss_parser.Utils.AISummaryState
import com.example.rss_parser.viewmodel.viewmodel
import com.meetup.twain.MarkdownText

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SummaryModalBottomSheet(feedlink:String,viewmodel: viewmodel,ishapticenabled:Boolean,aisummary: (Boolean?) -> Unit){
    val context= LocalContext.current
    val lifecycle= LocalLifecycleOwner.current
    val haptic= LocalHapticFeedback.current
    var summary by remember {
        mutableStateOf("")
    }
    viewmodel.GenerateAISummary(feedlink).observe(lifecycle){
        when(it){
            is AISummaryState.Error -> {
                Toast.makeText(context,it.Error,Toast.LENGTH_LONG).show()
            }
            AISummaryState.Loading -> {

            }
            is AISummaryState.Success -> {
                summary=it.response

            }
        }

    }
    ModalBottomSheet(
        onDismissRequest = { summary = "" ;aisummary(false)},
        modifier = Modifier.fillMaxSize()
    ) {
        if (summary != "") {

            Column {
                LazyColumn {
                    item {
                        SelectionContainer(content = {
                            if (ishapticenabled) {
                                haptic.performHapticFeedback(
                                    HapticFeedbackType.LongPress
                                )
                            }

                            MarkdownText(


                                markdown = summary,
                                color = MaterialTheme.colorScheme.onBackground,
                                textAlign = TextAlign.Justify,
                                modifier = Modifier.padding(15.dp)

                            )
                        })


                    }
                }

            }
        } else {

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 200.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Summarizing Article",
                    modifier = Modifier.padding(bottom = 15.dp)
                )
                LinearProgressIndicator(strokeCap = StrokeCap.Round)
            }
        }

    }

}