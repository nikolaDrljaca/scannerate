package com.drbrosdev.studytextscan.service.billing

import com.android.billingclient.api.Purchase


sealed interface PurchaseResult {
    data class Success(val purchase: Purchase?): PurchaseResult
    data class Failure(val errorMessage: String, val debug: String): PurchaseResult
}