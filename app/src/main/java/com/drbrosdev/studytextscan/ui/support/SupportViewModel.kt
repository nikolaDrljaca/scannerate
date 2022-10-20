package com.drbrosdev.studytextscan.ui.support

import android.app.Activity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.drbrosdev.studytextscan.service.billing.BillingClientService
import com.drbrosdev.studytextscan.service.billing.ProductId
import com.drbrosdev.studytextscan.service.billing.PurchaseResult
import com.drbrosdev.studytextscan.service.billing.QueriedProducts
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class SupportViewModel(
    private val billingClient: BillingClientService
): ViewModel() {
    private val _events = Channel<SupportEvents>()
    val events = _events.receiveAsFlow()

    private val loading = MutableStateFlow(false)
    private val vendors = MutableStateFlow(VendorUiModel.vendorUiModels)
    private val selectedProductId = MutableStateFlow(ProductId.ITEM_1)

    private val products = billingClient.productsFlow
        .map {
            when (it) {
                is QueriedProducts.Failure -> {
                    _events.send(SupportEvents.ErrorOccured(it.errorMessage))
                    emptyList<ProductUiModel>()
                }
                is QueriedProducts.Success -> it.products.map { ProductUiModel(it) }
            }
        }
        .onStart { loading.update { true } }
        .onEach { loading.update { false } }

    private val purchaseFlowJob = billingClient.purchaseFlow
        .onEach {
            when(it) {
                is PurchaseResult.Failure ->
                    _events.send(SupportEvents.ErrorOccured(it.errorMessage))
                is PurchaseResult.Success ->
                    _events.send(SupportEvents.NavigateToReward)
            }
        }
        .launchIn(viewModelScope)

    val state = combine(products, loading, vendors, selectedProductId) { p0, p1, p2, p3 ->
        val updated = p0.map {
            ProductUiModel(it.product, isSelected = it.product.productId == p3.id)
        }
        SupportUiState(products = updated, loading = p1, vendors = p2)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000L), SupportUiState())

    fun selectProduct(productId: String) {
        val selectedProduct = ProductId.allProducts().find { it.id == productId } ?: ProductId.ITEM_1
        selectedProductId.update { selectedProduct }
    }

    fun selectVendor(vendor: Vendor) {
        vendors.update {
            it.map {
                VendorUiModel(it.vendor, isSelected = it.vendor.vendorName == vendor.vendorName)
            }
        }
    }

    fun queryProducts() {
        viewModelScope.launch {
            billingClient.queryProducts()
        }
    }

    fun makePurchase(activity: Activity) {
        state.value.products.find { it.product.productId == selectedProductId.value.id }?.let {
            billingClient.purchase(activity, it.product)
        }
    }

    fun retryToConsumePurchases() {
        billingClient.retryToConsumePurchases()
    }
}