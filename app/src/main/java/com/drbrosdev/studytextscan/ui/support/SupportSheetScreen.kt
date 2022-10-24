package com.drbrosdev.studytextscan.ui.support

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.android.billingclient.api.ProductDetails
import com.android.billingclient.api.QueryProductDetailsParams
import com.drbrosdev.studytextscan.R
import com.drbrosdev.studytextscan.ui.support.theme.HeavyBlue
import com.drbrosdev.studytextscan.ui.support.theme.LightBlue
import com.drbrosdev.studytextscan.ui.support.theme.DarkTextGray

@Composable
fun SupportScreen(
    modifier: Modifier = Modifier,
    state: SupportUiState,
    onProductSelected: (ProductDetails) -> Unit,
    onVendorSelected: (Vendor) -> Unit,
    onSupportClicked: () -> Unit
) {
    Surface(
        color = MaterialTheme.colors.background,
        shape = RoundedCornerShape(18.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp, start = 4.dp, end = 4.dp, bottom = 4.dp)
            .then(modifier)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .then(modifier)
        ) {
            SupportTopBar(modifier = Modifier.padding(horizontal = 16.dp))

            Spacer(modifier = Modifier.height(18.dp))

            Text(
                text = stringResource(R.string.consider_support),
                color = MaterialTheme.colors.onSurface,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "You can do that by\nchoosing one of the options below.",
                color = MaterialTheme.colors.onSurface,
                fontSize = 12.sp,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            ProductsList(
                modifier = Modifier.fillMaxWidth(),
                products = state.products,
                loading = state.loading,
                onProductClicked = onProductSelected
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = stringResource(R.string.payment_method),
                color = MaterialTheme.colors.onSurface,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = stringResource(R.string.payment_options),
                color = MaterialTheme.colors.onSurface,
                fontSize = 12.sp,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            Spacer(modifier = Modifier.height(24.dp))

            VendorList(
                modifier = Modifier.fillMaxWidth(),
                vendors = state.vendors,
                onVendorSelected = onVendorSelected
            )

            Spacer(modifier = Modifier.height(48.dp))

            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .padding(bottom = 12.dp)
                    .height(64.dp),
                onClick = { onSupportClicked() },
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = LightBlue,
                    contentColor = HeavyBlue
                ),
                shape = RoundedCornerShape(18.dp)
            ) {
                Text(text = stringResource(R.string.support_btn), fontSize = 20.sp)
            }
        }
    }
}