package com.drbrosdev.studytextscan.ui.info.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.drbrosdev.studytextscan.ui.support.theme.BackgroundBlue

@Composable
fun ScannerateAboutApp(
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .then(modifier),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Text(
                text = "About the App",
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colors.secondary,
                fontSize = 24.sp,
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Scannerate 1.6",
                color = BackgroundBlue,
                fontSize = 16.sp,
            )
        }
        Image(
            painter = painterResource(id = com.drbrosdev.studytextscan.R.drawable.ic_launcher),
            contentDescription = "",
            modifier = Modifier.size(64.dp)
        )
    }
}

@Composable
fun DevLogo(
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp)
            .then(modifier),
        backgroundColor = Color.White,
        elevation = 0.dp,
        shape = RoundedCornerShape(16.dp)
    ) {
        Image(
            painter = painterResource(id = com.drbrosdev.studytextscan.R.drawable.ic_default_monochrome),
            contentDescription = "",
            modifier = Modifier.padding(16.dp).padding(horizontal = 32.dp)
        )
    }
}