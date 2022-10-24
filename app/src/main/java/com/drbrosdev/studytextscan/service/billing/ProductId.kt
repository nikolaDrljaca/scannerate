package com.drbrosdev.studytextscan.service.billing

enum class ProductId(val id: String) {
    ITEM_1("scannerate_item_1"),
    ITEM_2("scannerate_item_2"),
    ITEM_3("scannerate_item_3"),
    ITEM_4("scannerate_item_4");

    companion object {
        fun allProducts() = ProductId.values()
    }
}