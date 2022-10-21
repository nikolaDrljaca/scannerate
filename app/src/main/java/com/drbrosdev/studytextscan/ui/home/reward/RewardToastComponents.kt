package com.drbrosdev.studytextscan.ui.home.reward

import androidx.compose.animation.animateColor
import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.drbrosdev.studytextscan.ui.support.theme.HeavyBlue
import compose.icons.LineAwesomeIcons
import compose.icons.lineawesomeicons.Gratipay


@Composable
fun RewardToast(
    snackbarHostState: SnackbarHostState
) {
    Box(modifier = Modifier.fillMaxSize()) {
        SnackbarHost(
            modifier = Modifier.align(Alignment.TopCenter),
            hostState = snackbarHostState,
            snackbar = {
                RewardSnackbar()
            }
        )
    }
}

@Composable
fun RewardIconAnimated() {
    val transition = rememberInfiniteTransition()
    val color by transition.animateColor(
        initialValue = Color(0xFF31ED31),
        targetValue = Color(0xFF2CEEF0),
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 400, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        )
    )
    Icon(
        imageVector = LineAwesomeIcons.Gratipay, contentDescription = "",
        tint = color, modifier = Modifier.size(64.dp)
    )
}

@Composable
fun RewardSnackbar() {
    Card(
        modifier = Modifier.padding(14.dp),
        backgroundColor = HeavyBlue,
        shape = RoundedCornerShape(18.dp),
        elevation = 3.dp
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(
                    6.dp,
                    alignment = Alignment.CenterVertically
                ),
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "Your purchase has been verified!",
                    fontSize = 12.sp,
                    color = Color.White,
                    modifier = Modifier.alpha(0.7f)
                )
                Text(
                    text = "Keep supporting open source!",
                    fontSize = 16.sp,
                    color = Color.White
                )
            }
            RewardIconAnimated()
        }
    }
}