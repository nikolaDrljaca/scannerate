package com.drbrosdev.studytextscan.ui.support

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.drbrosdev.studytextscan.ui.support.theme.HeavyBlue
import com.drbrosdev.studytextscan.ui.support.theme.LightBlue
import com.drbrosdev.studytextscan.ui.support.theme.DarkTextGray

@Composable
fun SupportScreen(
    modifier: Modifier = Modifier
) {
    Surface(
        color = MaterialTheme.colors.background,
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.padding(4.dp).then(modifier)
    ) {
        Column(
            modifier = Modifier
                .then(modifier)
        ) {
            SupportTopBar(modifier = Modifier.padding(horizontal = 16.dp))
            Spacer(modifier = Modifier.height(18.dp))
            Text(
                text = "Select Amount",
                color = MaterialTheme.colors.onSurface,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "You can support our work\nby selecting one of the options below.",
                color = MaterialTheme.colors.onSurface,
                fontSize = 12.sp,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            Spacer(modifier = Modifier.height(24.dp))

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(horizontal = 16.dp)
            ) {
                items(4) {
                    SupportItemChip(num = it)
                }
            }
            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Payment Method",
                color = MaterialTheme.colors.onSurface,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "More options will be added later on.",
                color = MaterialTheme.colors.onSurface,
                fontSize = 12.sp,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            Spacer(modifier = Modifier.height(24.dp))

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(horizontal = 16.dp)
            ) {
                item {
                    VendorChip()
                }
            }
            Spacer(modifier = Modifier.height(48.dp))

            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .padding(bottom = 12.dp)
                    .height(64.dp),
                onClick = { /*TODO*/ },
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = LightBlue,
                    contentColor = HeavyBlue
                ),
                shape = RoundedCornerShape(18.dp)
            ) {
                Text(text = "Support", fontSize = 20.sp)
            }
        }
    }
}