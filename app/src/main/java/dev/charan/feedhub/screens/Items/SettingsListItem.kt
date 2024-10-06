package dev.charan.feedhub.screens.Items

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
fun SettingsListItem(label:String,Modifier:Modifier,onClick:()->Unit){
    ListItem(
        {
            Row(
                modifier = Modifier.fillMaxSize(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    label,
                    modifier = Modifier.padding(),

                )


            }

        },
        modifier= Modifier.clickable { onClick() }

    )

}