package com.drbrosdev.studytextscan.ui.info

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.drbrosdev.studytextscan.ui.info.components.*

@Composable
fun ScannerateAboutScreen(
    modifier: Modifier = Modifier,
    onActionClick: (SupportAction) -> Unit,
    onLinkClick: (Int) -> Unit
) {
    Surface(color = MaterialTheme.colors.background, modifier = Modifier.fillMaxSize()) {
        LazyVerticalGrid(
            modifier = Modifier
                .then(modifier)
                .fillMaxSize(),
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {

            item(
                span = { GridItemSpan(maxCurrentLineSpan) },
                key = "header"
            ) {
                Card(
                    modifier = Modifier,
                    shape = RoundedCornerShape(16.dp),
                    elevation = 2.dp,
                    backgroundColor = MaterialTheme.colors.primary
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        ScannerateAboutApp(modifier = Modifier.padding(12.dp))
                        DevLogo()
                        Column(
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            developers.forEach { dev ->
                                ScannerateDeveloper(
                                    name = dev.name,
                                    onGitHubClick = { onLinkClick(dev.gitHub) },
                                    onLinkedInClick = { onLinkClick(dev.linkedIn) }
                                )
                            }
                        }
                    }
                }
            }

            items(items = supportActions, key = { it.text }) { action ->
                AboutActionCard(
                    supportAction = action,
                    onClick = { onActionClick(action) }
                )
            }
        }
    }
}