package dev.charan.feedhub.screens.Items

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.dp

@Composable
fun CheckListItem(label: String,
                  isChecked:Boolean,
                  isHapticEnabled: Boolean,
                  modifier: Modifier,
                  onCheckedChange: (Boolean) -> Unit) {
    val haptic= LocalHapticFeedback.current
    var isChecked by remember{
        mutableStateOf(isChecked)
    }
    androidx.compose.material3.ListItem(

        {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    label,
                    modifier = Modifier.padding(top = 12.dp),

                )
                Spacer(Modifier.weight(1f))
                Switch(checked = isChecked,
                    thumbContent = if (isChecked) {
                        {
                            Icon(
                                imageVector = Icons.Filled.Check,
                                contentDescription = null,
                                modifier = Modifier.size(SwitchDefaults.IconSize),
                            )
                        }
                    } else {
                        null
                    },

                    onCheckedChange = {
                    if (isHapticEnabled) {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    }

                    isChecked = it
                    onCheckedChange(isChecked)

                }, modifier = Modifier.padding(end = 5.dp))
            }
        },
        modifier = Modifier.clickable {
            if (isHapticEnabled) {
                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
            }
            isChecked = !isChecked
            onCheckedChange(isChecked)


        }
            .then(modifier)
    )

}