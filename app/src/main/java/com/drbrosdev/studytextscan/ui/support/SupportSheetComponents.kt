package com.drbrosdev.studytextscan.ui.support

import androidx.compose.animation.animateColor
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.drbrosdev.studytextscan.ui.support.theme.*
import compose.icons.LineAwesomeIcons
import compose.icons.lineawesomeicons.*
import kotlin.random.Random

@Composable
fun SupportItemChip(
    modifier: Modifier = Modifier,
    num: Int = 0,
    isClicked: Boolean = false,
    onClick: () -> Unit = {}
) {
    val elevation by animateDpAsState(targetValue = if (isClicked) 4.dp else 0.dp)
    val strokeColor by animateColorAsState(
        targetValue = if (isClicked) MaterialTheme.colors.onSecondary else Color.Transparent
    )

    Surface(
        modifier = Modifier
            .clip(RoundedCornerShape(10.dp))
            .clickable { onClick() }
            .then(modifier),
        color = LightBlue,
        shape = RoundedCornerShape(10.dp),
        contentColor = HeavyBlue,
        border = BorderStroke(width = 2.dp, color = strokeColor),
        elevation = elevation
    ) {
        Column(
            modifier = Modifier
                .requiredWidth(120.dp)
                .padding(horizontal = 12.dp, vertical = 6.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(text = "$13.99", fontSize = 14.sp, fontWeight = FontWeight.Bold)
            Text(text = "Item $num", color = DarkTextGray, fontSize = 10.sp)
        }
    }
}

@Composable
fun VendorChip(
    modifier: Modifier = Modifier,
    isClicked: Boolean = true,
    onClick: () -> Unit = {}
) {
    val elevation by animateDpAsState(targetValue = if (isClicked) 3.dp else 0.dp)
    val strokeColor by animateColorAsState(
        targetValue = if (isClicked) MaterialTheme.colors.onSecondary else Color.Transparent
    )

    Button(
        modifier = Modifier
            .then(modifier),
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(backgroundColor = LightBlue, contentColor = HeavyBlue),
        shape = RoundedCornerShape(10.dp),
        border = BorderStroke(width = 2.dp, strokeColor),
        elevation = ButtonDefaults.elevation(defaultElevation = elevation)
    ) {
        Icon(
            imageVector = LineAwesomeIcons.GooglePlay,
            contentDescription = "",
            modifier = Modifier.size(32.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = "Google Play")
    }
}

@Composable
fun DevIcon(
    modifier: Modifier = Modifier,
    imageVector: ImageVector,
    iconSize: Dp = 56.dp
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = HeavyBlue,
        modifier = Modifier.then(modifier)
    ) {
        Icon(
            imageVector = imageVector,
            contentDescription = null,
            modifier = Modifier
                .padding(8.dp)
                .size(iconSize)
        )
    }
}


@Composable
fun SupportTopBar(
    modifier: Modifier = Modifier
) {
    val (icon, iconName) = remember {
        when (Random.nextInt(11)) {
            in 0..3 -> LineAwesomeIcons.DragonSolid to "Grumpy Dragon"
            in 4..7 -> LineAwesomeIcons.OtterSolid to "Glorious Otter"
            in 8..10 -> LineAwesomeIcons.CatSolid to "Just A Cat"
            else -> LineAwesomeIcons.DogSolid to "Friendly Dog"
        }
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .then(modifier),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.Center
        ) {
            Text(text = "Hello there,", fontSize = 16.sp, color = MaterialTheme.colors.onSurface)
            Text(text = iconName, fontSize = 20.sp, color = MaterialTheme.colors.onPrimary)
        }
        AnimatedRandomIcon(iconVector = icon)
    }
}

@Composable
private fun AnimatedRandomIcon(
    modifier: Modifier = Modifier,
    iconVector: ImageVector,
) {
    val transition = rememberInfiniteTransition()

    val tintColor by transition.animateColor(
        initialValue = IconGreen,
        targetValue = IconOrange,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1500, delayMillis = 100, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    Surface(
        shape = CircleShape,
        color = tintColor
    ) {
        Icon(
            imageVector = iconVector,
            contentDescription = "",
            modifier = Modifier
                .size(52.dp)
                .padding(8.dp)
                .then(modifier),
            tint = Color.White
        )
    }
}