package com.drbrosdev.studytextscan.service.billing

/*
class BillingClientService(context: Context) : PurchasesUpdatedListener {
    private val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    private val _productsFlow = Channel<QueriedProducts>()
    val productsFlow = _productsFlow.receiveAsFlow()

    private val _purchaseFlow = Channel<PurchaseResult>()
    val purchaseFlow = _purchaseFlow.receiveAsFlow()

    private val billingClient = BillingClient.newBuilder(context)
        .enablePendingPurchases()
        .setListener(this)
        .build()

    override fun onPurchasesUpdated(p0: BillingResult, p1: MutableList<Purchase>?) {
        when (p0.responseCode) {
            BillingClient.BillingResponseCode.OK -> {
                if (p1 == null) {
                    //?? questionable functionality, maybe not needed to emit to ui
                    scope.launch { _purchaseFlow.send(PurchaseResult.Success(null)) }
                    return
                }
                scope.launch { p1.forEach { processPurchase(it) } }
            }
            else -> {
                scope.launch {
                    _purchaseFlow.send(
                        PurchaseResult.Failure(
                            errorMessage = "Something went wrong.",
                            debug = p0.debugMessage
                        )
                    )
                }
            }
        }
    }

    suspend fun purchase(activity: Activity, product: ProductDetails) {
        val productDetails = BillingFlowParams.ProductDetailsParams
            .newBuilder()
            .setProductDetails(product)
            .build()
        val billingFlowParams = BillingFlowParams.newBuilder()
            .setProductDetailsParamsList(listOf(productDetails))
            .build()

        val connectionStatus = establishConnection()
        if (!connectionStatus) return

        activity.runOnUiThread {
            //LaunchBillingFlow has no callback, it invokes onPurchasesUpdated above
            billingClient.launchBillingFlow(
                activity,
                billingFlowParams
            )
        }
    }

    suspend fun queryProducts() {
        val productList = createProductList()
        val params = QueryProductDetailsParams.newBuilder()
            .setProductList(productList)
            .build()

        queryInAppProducts(params)
    }

    //make suspendable and switch context to IO
    fun retryToConsumePurchases() {
        val queryPurchaseParams = QueryPurchasesParams.newBuilder()
            .setProductType(BillingClient.ProductType.INAPP)
            .build()
        billingClient.queryPurchasesAsync(queryPurchaseParams) { billingResult, purchaseList ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                scope.launch {
                    purchaseList.forEach { processPurchase(it) }
                }
            }
        }
    }

    private suspend fun processPurchase(purchase: Purchase) {
        if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED) {
            scope.launch { _purchaseFlow.send(PurchaseResult.Success(purchase)) }

            val consumeParams = ConsumeParams.newBuilder().setPurchaseToken(purchase.purchaseToken).build()

            val consumeResult = withContext(Dispatchers.IO) {
                billingClient.consumePurchase(consumeParams)
            }
            if(consumeResult.billingResult.responseCode != BillingClient.BillingResponseCode.OK) {
                //implement retry logic or try to consume again in onResume()->fragment
                //save the token or whatever with consumeResult.purchaseToken
            }
        }
    }

    private suspend fun queryInAppProducts(params: QueryProductDetailsParams) {
        val connectionStatus = establishConnection()
        if (!connectionStatus) return

        val result = withContext(Dispatchers.IO) {
            billingClient.queryProductDetails(params)
        }
        with(result) {
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                productDetailsList?.let {
                    scope.launch { _productsFlow.send(QueriedProducts.Success(it)) }
                }
            } else {
                scope.launch {
                    _productsFlow.send(
                        QueriedProducts.Failure(
                            errorMessage = "Something went wrong.",
                            debug = billingResult.debugMessage
                        )
                    )
                }
            }
        }
    }

    private suspend fun establishConnection() = suspendCoroutine<Boolean> { continuation ->
        billingClient.startConnection(object : BillingClientStateListener {
            override fun onBillingServiceDisconnected() {
                //continuation.resume(false)
            }

            override fun onBillingSetupFinished(p0: BillingResult) {
                continuation.resume(true)
            }
        })
    }

    private fun createProductList() = buildList {
        addAll(ProductId.allProducts().map {
            QueryProductDetailsParams.Product.newBuilder()
                .setProductId(it.id)
                .setProductType(ProductType.INAPP)
                .build()
        })
    }
}
 */