package com.drbrosdev.studytextscan.ui.support

import com.android.billingclient.api.ProductDetails

data class SupportUiState(
    val products: List<ProductDetails> = emptyList(),
    val loading: Boolean = false
)