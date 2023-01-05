package com.drbrosdev.studytextscan.ui.info.components

import androidx.annotation.StringRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.drbrosdev.studytextscan.R
import compose.icons.TablerIcons
import compose.icons.tablericons.BrandGithub
import compose.icons.tablericons.BrandLinkedin

data class Developer(
    val name: String,
    val linkedIn: Int,
    val gitHub: Int
)

val developers = listOf(
    Developer(name = "Nikola Drljaca", linkedIn = R.string.linkedin_nikola, gitHub = R.string.git_nikola),
    Developer(name = "Ognjen Drljaca", linkedIn = R.string.ognjen_linkedin_url, gitHub = R.string.git_ognjen)
)

@Composable
fun ScannerateDeveloper(
    modifier: Modifier = Modifier,
    name: String,
    onGitHubClick: () -> Unit,
    onLinkedInClick: () -> Unit,
    iconButtonSize: Dp = 48.dp,
    iconSize: Dp = 28.dp,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .then(modifier),
        shape = RoundedCornerShape(16.dp),
        elevation = 0.dp,
        backgroundColor = MaterialTheme.colors.background,
        border = BorderStroke(width = 1.dp, color = MaterialTheme.colors.onBackground)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = name, fontSize = 14.sp)
            Row {
                IconButton(
                    onClick = onGitHubClick,
                    modifier = Modifier.size(iconButtonSize)
                ) {
                    Icon(
                        imageVector = TablerIcons.BrandGithub,
                        contentDescription = "",
                        modifier = Modifier.size(iconSize)
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                IconButton(
                    onClick = onLinkedInClick,
                    modifier = Modifier.size(iconButtonSize)
                ) {
                    Icon(
                        imageVector = TablerIcons.BrandLinkedin,
                        contentDescription = "",
                        modifier = Modifier.size(iconSize)
                    )
                }
            }
        }
    }
}