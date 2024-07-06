package com.drbrosdev.studytextscan.ui.support

/*
class SupportViewModel(
    private val billingClient: BillingClientService,
    private val preferences: AppPreferences
) : ViewModel() {
    private val _events = Channel<SupportEvents>()
    val events = _events.receiveAsFlow()

    private val loading = MutableStateFlow(false)
    private val selectedVendor = MutableStateFlow(Vendor.GOOGLE)
    private val selectedProductId = MutableStateFlow(ProductId.ITEM_1)

    private val products = billingClient.productsFlow
        .map {
            when (it) {
                is QueriedProducts.Failure -> {
                    _events.send(SupportEvents.ErrorOccured(it.errorMessage, it.debug))
                    emptyList<ProductUiModel>()
                }
                is QueriedProducts.Success -> it.products.map { ProductUiModel(it) }
            }
        }
        .onStart {
            emit(emptyList())
            loading.update { true }
        }
        .onEach { loading.update { false } }

    private val purchaseFlowJob = billingClient.purchaseFlow
        .onEach {
            when (it) {
                is PurchaseResult.Failure -> {  }
                is PurchaseResult.Success -> {
                    _events.send(SupportEvents.SupportGiven)
                    preferences.showReward()
                }
            }
        }
        .launchIn(viewModelScope)

    val state = combine(products, loading, selectedVendor, selectedProductId) { p0, p1, p2, p3 ->
        val updated = p0.map {
            ProductUiModel(it.product, isSelected = it.product.productId == p3.id)
        }
        val updatedVendors = VendorUiModel.vendorUiModels.map {
            VendorUiModel(it.vendor, isSelected = it.vendor.vendorName == p2.vendorName)
        }
        SupportUiState(products = updated, loading = p1, vendors = updatedVendors)
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000L),
        SupportUiState(vendors = VendorUiModel.vendorUiModels)
    )

    fun selectProduct(productId: String) {
        val selectedProduct =
            ProductId.allProducts().find { it.id == productId } ?: ProductId.ITEM_1
        selectedProductId.update { selectedProduct }
    }

    fun selectVendor(vendor: Vendor) {
        selectedVendor.update { vendor }
    }

    fun queryProducts() {
        viewModelScope.launch {
            billingClient.queryProducts()
        }
    }

    fun makePurchase(activity: Activity) {
        state.value.products.find { it.product.productId == selectedProductId.value.id }?.let {
            viewModelScope.launch {
                billingClient.purchase(activity, it.product)
            }
        }
    }

    fun retryToConsumePurchases() {
        billingClient.retryToConsumePurchases()
    }
}
 */