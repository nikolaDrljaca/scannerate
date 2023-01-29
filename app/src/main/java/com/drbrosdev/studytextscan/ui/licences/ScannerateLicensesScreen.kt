package com.drbrosdev.studytextscan.ui.licences

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.drbrosdev.studytextscan.ui.support.theme.BackgroundBlue

@Composable
fun ScannerateLicensesScreen(
    modifier: Modifier = Modifier,
    onLicenseClick: (String) -> Unit
) {
    Surface(
        modifier = Modifier.then(modifier),
        color = MaterialTheme.colors.background
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize(),
            contentPadding = PaddingValues(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            items(items = allLicenceItems, key = { it.order }) { license ->
                LicenseCard(
                    license = license,
                    onClick = { onLicenseClick(license.link) }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun LicenseCard(
    modifier: Modifier = Modifier,
    license: LicenceListItem,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .then(modifier),
        shape = RoundedCornerShape(16.dp),
        elevation = 2.dp,
        backgroundColor = MaterialTheme.colors.primary
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Text(text = license.title, fontSize = 16.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = license.link, color = BackgroundBlue, fontSize = 12.sp)
        }
    }
}