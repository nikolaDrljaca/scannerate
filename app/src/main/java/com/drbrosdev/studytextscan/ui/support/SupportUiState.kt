package com.drbrosdev.studytextscan.ui.support

import com.android.billingclient.api.ProductDetails
import compose.icons.LineAwesomeIcons
import compose.icons.lineawesomeicons.GooglePlay

data class SupportUiState(
    val products: List<ProductUiModel> = emptyList(),
    val vendors: List<VendorUiModel> = emptyList(),
    val loading: Boolean = false,
)

data class ProductUiModel(
    val product: ProductDetails,
    val isSelected: Boolean = false
)

data class VendorUiModel(
    val vendor: Vendor,
    val isSelected: Boolean = false
) {
    companion object {
        val vendorUiModels = Vendor.vendorList.map { VendorUiModel(it) }
    }
}

enum class Vendor(val vendorName: String) {
    GOOGLE("Google Play");

    companion object {
        val vendorList = Vendor.values()

        fun vendorIcon(vendor: Vendor) = when (vendor) {
            GOOGLE -> LineAwesomeIcons.GooglePlay
        }
    }
}
