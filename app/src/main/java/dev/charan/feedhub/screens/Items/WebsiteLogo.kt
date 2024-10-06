package dev.charan.feedhub.screens.Items

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage

@Composable
fun WebsiteLogo(websiteFavicon:String,isOpened:String){

    Box(
        modifier = Modifier.graphicsLayer {

            this.shape = CircleShape
            this.clip = true
        },
        contentAlignment = Alignment.Center
    ) {
        AsyncImage(
            model = websiteFavicon,
            contentDescription = null,
            modifier = Modifier
                .requiredSize(15.dp)


            ,
            contentScale = ContentScale.Fit ,
            colorFilter =
            if (isOpened == "true"
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
                ColorFilter.colorMatrix(
                    ColorMatrix().apply {
                        setToScale(
                            0.8f,
                            0.8f,
                            0.8f,
                            1f
                        )
                    })
            }
        )
    }
}
