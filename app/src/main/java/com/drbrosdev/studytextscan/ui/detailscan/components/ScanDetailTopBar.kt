package com.drbrosdev.studytextscan.ui.detailscan.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColor
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import compose.icons.TablerIcons
import compose.icons.tablericons.*

enum class ScanDetailTopBarState {
    NORMAL, EXPANDED
}

private class TopBarTransitionData(
    elevation: State<Dp>,
    width: State<Float>,
    outlineColor: State<Color>
) {
    val elevation by elevation
    val width by width
    val outlineColor by outlineColor
}

@Composable
private fun updateTopBarTransitionData(state: ScanDetailTopBarState): TopBarTransitionData {
    val transition = updateTransition(targetState = state, label = "")

    val outlineColor = transition.animateColor(
        label = ""
    ) {
        when (it) {
            ScanDetailTopBarState.NORMAL -> MaterialTheme.colors.secondaryVariant
            ScanDetailTopBarState.EXPANDED -> Color.Transparent
        }
    }

    val elevation = transition.animateDp(
        label = "",
        targetValueByState = {
            when(it) {
                ScanDetailTopBarState.NORMAL -> 4.dp
                ScanDetailTopBarState.EXPANDED -> 0.dp
            }
        },
    )

    val width = transition.animateFloat(
        label = "",
        transitionSpec = { tween() }
    ) {
        when (it) {
            ScanDetailTopBarState.NORMAL -> 0.525f
            ScanDetailTopBarState.EXPANDED -> 1f
        }
    }
    return remember(transition) { TopBarTransitionData(elevation, width, outlineColor) }
}

@Composable
fun ScanDetailTopBar(
    modifier: Modifier = Modifier,
    iconButtonSize: Dp = 48.dp,
    iconSize: Dp = 28.dp,
    shape: Shape = RoundedCornerShape(18.dp),
    height: Dp = 72.dp,
    topBarState: ScanDetailTopBarState = ScanDetailTopBarState.EXPANDED,
    isPinned: Boolean = false,
    onBackClicked: () -> Unit = {},
    onPdfExportClicked: () -> Unit = {},
    onDeleteClicked: () -> Unit = {},
    onPinClicked: () -> Unit = {},
    onSaveClicked: () -> Unit = {},
) {
    val transitionData = updateTopBarTransitionData(topBarState)

    val pinColor by animateColorAsState(
        targetValue = if (isPinned) MaterialTheme.colors.onPrimary else Color.Transparent
    )

    Surface(
        modifier = Modifier
            .fillMaxWidth(transitionData.width)
            .requiredHeight(height)
            .padding(8.dp)
            .then(modifier)
        ,
        elevation = transitionData.elevation,
        shape = shape,
        border = BorderStroke(width = 1.dp, color = transitionData.outlineColor),
        color = MaterialTheme.colors.background
    ) {
        CompositionLocalProvider(LocalContentColor provides MaterialTheme.colors.onPrimary) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp, bottom = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                AnimatedVisibility(
                    visible = topBarState == ScanDetailTopBarState.EXPANDED,
                ) {
                    IconButton(
                        onClick = { onBackClicked() },
                        modifier = Modifier.size(iconButtonSize)
                    ) {
                        Icon(
                            imageVector = TablerIcons.ArrowLeft,
                            contentDescription = "",
                            modifier = Modifier.size(iconSize)
                        )
                    }
                }

                Row {
                    IconButton(
                        onClick = { onPdfExportClicked() },
                        modifier = Modifier.size(iconButtonSize)
                    ) {
                        Icon(imageVector = TablerIcons.FileExport, contentDescription = "")
                    }


                    IconButton(
                        onClick = { onPinClicked() },
                        modifier = Modifier
                            .size(iconButtonSize)
                            .drawBehind {
                                drawRoundRect(
                                    color = pinColor,
                                    size = Size(width = size.width, height = size.height / 8f),
                                    cornerRadius = CornerRadius(10.dp.toPx()),
                                    topLeft = Offset(x = 0f, y = size.height)
                                )
                            }
                    ) {
                        Icon(
                            imageVector = TablerIcons.Pin,
                            contentDescription = "",
                            modifier = Modifier.size(iconSize)
                        )
                    }


                    IconButton(
                        onClick = { onDeleteClicked() },
                        modifier = Modifier
                            .size(iconButtonSize)
                    ) {
                        Icon(
                            imageVector = TablerIcons.Trash,
                            contentDescription = "",
                            modifier = Modifier.size(iconSize)
                        )
                    }
                    IconButton(
                        onClick = { onSaveClicked() },
                        modifier = Modifier
                            .size(iconButtonSize)
                    ) {
                        Icon(
                            imageVector = TablerIcons.CircleCheck,
                            contentDescription = "",
                            modifier = Modifier.size(iconSize)
                        )
                    }
                }
            }
        }
    }
}