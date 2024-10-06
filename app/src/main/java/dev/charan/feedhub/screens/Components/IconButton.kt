package dev.charan.feedhub.screens.Components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable

fun CustomIconButton(
    onClick: () -> Unit,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    contentDescription: String? = null,


) {
    Box(
        modifier =Modifier
                .requiredSize(40.dp)
                .clip(CircleShape)
                .clickable(onClick = onClick)
            .then(modifier)
                ,
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint=MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.size(16.dp).align(Alignment.Center)
        )
    }
}
@Composable
fun CustomIconButtonForPainter(
    onClick: () -> Unit,
    icon: Painter,
    modifier: Modifier = Modifier,
    contentDescription: String? = null,


    ) {
    Box(
        modifier =Modifier
            .requiredSize(40.dp)
            .clip(CircleShape)
            .clickable(onClick = onClick)
        ,
        contentAlignment = Alignment.Center
    ) {
        Icon(
            painter = icon,
            contentDescription = contentDescription,
            tint=MaterialTheme.colorScheme.onBackground,

            modifier = Modifier.size(16.dp)
        )
    }
}


@Preview("Button")
@Composable
fun Button(){
    CustomIconButton(modifier = Modifier, icon =Icons.Outlined.Share, onClick = {

    })
}
