package com.drbrosdev.studytextscan.ui.support

import androidx.compose.animation.animateColor
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.android.billingclient.api.ProductDetails
import com.drbrosdev.studytextscan.R
import com.drbrosdev.studytextscan.ui.support.theme.*
import compose.icons.LineAwesomeIcons
import compose.icons.lineawesomeicons.*
import kotlin.random.Random

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun SupportItemChip(
    modifier: Modifier = Modifier,
    productDetails: ProductDetails,
    isSelected: Boolean = false,
    onClick: (ProductDetails) -> Unit = {}
) {
    val elevation by animateDpAsState(targetValue = if (isSelected) 3.dp else 0.dp)
    val strokeColor by animateColorAsState(
        targetValue = if (isSelected) MaterialTheme.colors.onSecondary else Color.Transparent
    )
    val shape = RoundedCornerShape(10.dp)

    Surface(
        modifier = Modifier
            .then(modifier),
        color = LightBlue,
        shape = shape,
        contentColor = HeavyBlue,
        border = BorderStroke(width = 2.dp, color = strokeColor),
        elevation = elevation,
        onClick = { onClick(productDetails) }
    ) {
        Column(
            modifier = Modifier
                .requiredWidth(120.dp)
                .padding(horizontal = 12.dp, vertical = 6.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = productDetails.oneTimePurchaseOfferDetails?.formattedPrice ?: "",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
            Text(text = productDetails.name, color = DarkTextGray, fontSize = 10.sp)
        }
    }
}

@Composable
fun VendorChip(
    modifier: Modifier = Modifier,
    vendor: Vendor,
    isSelected: Boolean = true,
    onClick: (Vendor) -> Unit,
) {
    val elevation by animateDpAsState(targetValue = if (isSelected) 3.dp else 0.dp)
    val strokeColor by animateColorAsState(
        targetValue = if (isSelected) MaterialTheme.colors.onSecondary else Color.Transparent
    )
    val vendorIcon = remember(vendor) {
        Vendor.vendorIcon(vendor)
    }

    Button(
        modifier = Modifier
            .then(modifier),
        onClick = { onClick(vendor) },
        colors = ButtonDefaults.buttonColors(backgroundColor = LightBlue, contentColor = HeavyBlue),
        shape = RoundedCornerShape(10.dp),
        border = BorderStroke(width = 2.dp, strokeColor),
        elevation = ButtonDefaults.elevation(defaultElevation = elevation)
    ) {
        Icon(
            imageVector = vendorIcon,
            contentDescription = "",
            modifier = Modifier.size(32.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = vendor.vendorName)
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
fun ProductsList(
    modifier: Modifier = Modifier,
    loading: Boolean,
    products: List<ProductUiModel>,
    onProductClicked: (ProductDetails) -> Unit
) {
    LazyRow(
        modifier = Modifier
            .then(modifier),
        horizontalArrangement = Arrangement.spacedBy(8.dp, alignment = Alignment.CenterHorizontally),
        verticalAlignment = Alignment.CenterVertically,
        contentPadding = PaddingValues(horizontal = 16.dp)
    ) {
        if (loading) {
           item(key = "progress") {
               CircularProgressIndicator(
                   color = MaterialTheme.colors.onPrimary,
               )
           }
        }
        if (products.isEmpty() and !loading) {
            item(key = "placeholder") { PlaceholderItem() }
        }
        items(items = products, key = { it.product.productId }) {
            SupportItemChip(
                productDetails = it.product,
                isSelected = it.isSelected,
                onClick = { onProductClicked(it) }
            )
        }
    }
}

@Composable
fun VendorList(
    modifier: Modifier = Modifier,
    vendors: List<VendorUiModel>,
    onVendorSelected: (Vendor) -> Unit
) {
    LazyRow(
        modifier = Modifier
            .then(modifier),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(horizontal = 16.dp)
    ) {
        if (vendors.isEmpty()) {
            item(key = "vendor_placeholder") { PlaceholderItem() }
        }
        items(items = vendors, key = { it.vendor.vendorName }) { vendorModel ->
            VendorChip(
                vendor = vendorModel.vendor,
                isSelected = vendorModel.isSelected,
                onClick = onVendorSelected
            )
        }
    }
}

@Composable
private fun PlaceholderItem(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = Modifier
            .height(64.dp)
            .fillMaxWidth()
            .graphicsLayer { alpha = 0.7f }
            .then(modifier),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp, alignment = Alignment.CenterVertically)
    ) {
        Icon(
            imageVector = LineAwesomeIcons.GhostSolid,
            contentDescription = "",
            modifier = Modifier.size(32.dp),
            tint = MaterialTheme.colors.onSurface
        )
        Text(
            text = stringResource(R.string.went_wrong),
            fontSize = 12.sp,
            color = MaterialTheme.colors.onSurface
        )
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