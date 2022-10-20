package com.drbrosdev.studytextscan.service.billing

import android.app.Activity
import android.content.Context
import com.android.billingclient.api.*
import com.android.billingclient.api.BillingClient.ProductType
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow

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
                p1.forEach(::processPurchase)
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

    fun purchase(activity: Activity, product: ProductDetails) {
        val productDetails = BillingFlowParams.ProductDetailsParams
            .newBuilder()
            .setProductDetails(product)
            .build()
        val billingFlowParams = BillingFlowParams.newBuilder()
            .setProductDetailsParamsList(listOf(productDetails))
            .build()

        onConnected {
            activity.runOnUiThread {
                //LaunchBillingFlow has no callback, it invokes onPurchasesUpdated above
                billingClient.launchBillingFlow(
                    activity,
                    billingFlowParams
                )
            }
        }
    }

    suspend fun queryProducts() {
        val productList = createProductList()
        val params = QueryProductDetailsParams.newBuilder()
            .setProductList(productList)
            .build()

        queryInAppProducts(params)
    }

    fun retryToConsumePurchases() {
        val queryPurchaseParams = QueryPurchasesParams.newBuilder()
            .setProductType(BillingClient.ProductType.INAPP)
            .build()
        billingClient.queryPurchasesAsync(queryPurchaseParams) { billingResult, purchaseList ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                purchaseList.forEach(::processPurchase)
            }
        }
    }

    private fun processPurchase(purchase: Purchase) {
        if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED) {
            scope.launch { _purchaseFlow.send(PurchaseResult.Success(purchase)) }

            onConnected {
                billingClient.consumeAsync(
                    ConsumeParams.newBuilder().setPurchaseToken(purchase.purchaseToken).build()
                ) { billingResult, token ->
                    //save the token or whatever
                    if (billingResult.responseCode != BillingClient.BillingResponseCode.OK) {
                        //implement retry logic or try to consume again in onResume()->fragment
                    }
                }
            }
        }
    }

    private suspend fun queryInAppProducts(params: QueryProductDetailsParams) {
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

    private fun onConnected(block: () -> Unit) {
        billingClient.startConnection(object : BillingClientStateListener {
            override fun onBillingServiceDisconnected() {  }

            override fun onBillingSetupFinished(p0: BillingResult) {
                block()
            }
        })
    }

    private fun createProductList() = buildList {
        add(
            QueryProductDetailsParams.Product.newBuilder().setProductId("scannerate_item_1")
                .setProductType(ProductType.INAPP).build()
        )
        add(
            QueryProductDetailsParams.Product.newBuilder().setProductId("scannerate_item_2")
                .setProductType(ProductType.INAPP).build()
        )
        add(
            QueryProductDetailsParams.Product.newBuilder().setProductId("scannerate_item_3")
                .setProductType(ProductType.INAPP).build()
        )
        add(
            QueryProductDetailsParams.Product.newBuilder().setProductId("scannerate_item_4")
                .setProductType(ProductType.INAPP).build()
        )
    }
}