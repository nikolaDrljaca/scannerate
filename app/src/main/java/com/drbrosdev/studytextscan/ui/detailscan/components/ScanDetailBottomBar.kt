package com.drbrosdev.studytextscan.ui.detailscan.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import compose.icons.TablerIcons
import compose.icons.tablericons.Copy
import compose.icons.tablericons.Ear
import compose.icons.tablericons.Language
import compose.icons.tablericons.Share


@Composable
fun ScanDetailBottomBar(
    modifier: Modifier = Modifier,
    iconButtonSize: Dp = 52.dp,
    iconSize: Dp = 28.dp,
    shape: Shape = RoundedCornerShape(18.dp),
    onCopyClick: () -> Unit = {},
    onShareClick: () -> Unit = {},
    onTosClick: () -> Unit = {},
    onTranslateClick: () -> Unit = {}
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .then(modifier),
        elevation = 4.dp,
        shape = shape,
        color = MaterialTheme.colors.primary
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            IconButton(onClick = onCopyClick, modifier = Modifier.size(iconButtonSize)) {
                Icon(
                    imageVector = TablerIcons.Copy,
                    contentDescription = "",
                    modifier = Modifier.size(iconSize)
                )
            }
            IconButton(onClick = onShareClick, modifier = Modifier.size(iconButtonSize)) {
                Icon(
                    imageVector = TablerIcons.Share,
                    contentDescription = "",
                    modifier = Modifier.size(iconSize)
                )
            }
            IconButton(onClick = onTosClick, modifier = Modifier.size(iconButtonSize)) {
                Icon(
                    imageVector = TablerIcons.Ear,
                    contentDescription = "",
                    modifier = Modifier.size(iconSize)
                )
            }
            IconButton(onClick = onTranslateClick, modifier = Modifier.size(iconButtonSize)) {
                Icon(
                    imageVector = TablerIcons.Language,
                    contentDescription = "",
                    modifier = Modifier.size(iconSize)
                )
            }
        }
    }
}