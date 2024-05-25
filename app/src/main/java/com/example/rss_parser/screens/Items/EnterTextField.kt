package com.example.rss_parser.screens.Items

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowForward
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp

@Composable
fun EnterTextFiled(
    modifier: Modifier =Modifier,
    emailfield:String,
    onEmailChange :(String)->Unit,
    onEnter : (Boolean)->Unit,
    focusRequester:FocusRequester

){
    var isClicked by remember {
        mutableStateOf(false)
    }
    val interactionSource = remember { MutableInteractionSource() }
    BasicTextField(
        value = emailfield,
        onValueChange = { onEmailChange.invoke(it.trim())  },
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 10.dp, top = 10.dp, end = 10.dp)
            .height(40.dp)
            .focusRequester(focusRequester)

            .border(
                shape = RoundedCornerShape(40.dp),
                border = BorderStroke(
                    2.dp,
                    color = MaterialTheme.colorScheme.secondaryContainer
                )
            ),
        cursorBrush = SolidColor(MaterialTheme.colorScheme.onSurface),
        singleLine = true,
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
        keyboardActions = KeyboardActions(
            onDone = {
                isClicked=true
                onEnter.invoke(true)
            }
        ),


        textStyle = TextStyle(color = MaterialTheme.colorScheme.onSurface),
        decorationBox = { innerTextField ->
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .width(350.dp)
                        .padding(start = 22.dp)
                        .background(Color.Transparent),
                    contentAlignment = Alignment.CenterStart
                ) {
                    if (emailfield.isBlank()) {
                        Text("Enter your email")
                    }
                    innerTextField()
                }
                Spacer(modifier = Modifier.weight(1f))


                Surface(
                    onClick = {
                        isClicked=true
                        onEnter.invoke(true)

                    },
                    modifier = Modifier
                        .semantics { role = Role.Button }
                        .align(Alignment.CenterVertically),
                    color =
                    if (emailfield.isEmpty()) {
                        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)
                    } else {
                        MaterialTheme.colorScheme.secondaryContainer
                    },

                    contentColor = MaterialTheme.colorScheme.onSurface,
                    enabled = emailfield.isNotEmpty(),


                    shape = IconButtonDefaults.filledShape,

                    interactionSource = interactionSource
                ) {
                    Box {
                        if (isClicked) {
                            CircularProgressIndicator(
                                modifier = Modifier
                                    .padding(3.dp)
                                    .size(20.dp),
                                strokeWidth = 3.dp,
                                strokeCap = StrokeCap.Round
                            )

                        } else {
                            Image(
                                imageVector = Icons.Outlined.ArrowForward,
                                contentDescription = null,
                                modifier = Modifier.padding(3.dp),
                                colorFilter = ColorFilter.tint(color = MaterialTheme.colorScheme.onSurface)
                            )
                        }
                    }
                }
            }
        }
    )

}