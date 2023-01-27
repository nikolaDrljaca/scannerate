package com.drbrosdev.studytextscan.ui.info.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.drbrosdev.studytextscan.ui.support.theme.BackgroundBlue
import compose.icons.TablerIcons
import compose.icons.tablericons.*


sealed class SupportAction(
    val text: String,
    val desc: String,
    val imageVector: ImageVector,
) {
    object BugReport : SupportAction("Report a Bug", "Tell us what went wrong.", TablerIcons.Bug)
    object Share : SupportAction("Share", "Spread the love.", TablerIcons.Share)
    object Rate : SupportAction("Rate this App", "Leave a review.", TablerIcons.Star)
    object SupportDev : SupportAction(
        "Support Development",
        "A little support can go a long way.",
        TablerIcons.Cash,
    )

    object Licenses :
        SupportAction("Open Source Libraries", "They helped make it happen.", TablerIcons.Code)
}

val supportActions = listOf(
    SupportAction.BugReport,
    SupportAction.Share,
    SupportAction.Rate,
    SupportAction.SupportDev,
    SupportAction.Licenses,
)

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun AboutActionCard(
    modifier: Modifier = Modifier,
    supportAction: SupportAction,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .then(modifier)
            .size(144.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = 2.dp,
        backgroundColor = MaterialTheme.colors.primary
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.Start
        ) {
            Icon(
                imageVector = supportAction.imageVector,
                contentDescription = "",
                modifier = Modifier.size(32.dp),
                tint = MaterialTheme.colors.onPrimary
            )

            Column {
                Text(
                    text = supportAction.text,
                    fontSize = 14.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = supportAction.desc,
                    color = BackgroundBlue,
                    fontSize = 12.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}