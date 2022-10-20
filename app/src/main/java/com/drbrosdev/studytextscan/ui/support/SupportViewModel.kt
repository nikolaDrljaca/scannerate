package com.drbrosdev.studytextscan.ui.support

import android.app.Activity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.billingclient.api.ProductDetails
import com.drbrosdev.studytextscan.service.billing.BillingClientService
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

    private val products = billingClient.productsFlow
        .map {
            when (it) {
                is QueriedProducts.Failure -> {
                    _events.send(SupportEvents.ErrorOccured(it.errorMessage))
                    emptyList<ProductDetails>()
                }
                is QueriedProducts.Success -> it.products
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

    val state = combine(products, loading) { p0, p1 ->
        SupportUiState(products = p0, loading = p1)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000L), SupportUiState())

    fun queryProducts() {
        viewModelScope.launch {
            billingClient.queryProducts()
        }
    }

    fun makePurchase(activity: Activity, product: ProductDetails) {
        billingClient.purchase(activity, product)
    }

    fun retryToConsumePurchases() {
        billingClient.retryToConsumePurchases()
    }
}