package com.drbrosdev.studytextscan.ui.home

import androidx.core.view.isVisible
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyModelClass
import com.drbrosdev.studytextscan.R
import com.drbrosdev.studytextscan.databinding.*
import com.drbrosdev.studytextscan.epoxy.ViewBindingKotlinModel
import com.drbrosdev.studytextscan.persistence.entity.Scan
import com.drbrosdev.studytextscan.util.dateAsString

@EpoxyModelClass
abstract class ScanTopBarEpoxyModel :
    ViewBindingKotlinModel<ModelScanTopBarBinding>(R.layout.model_scan_top_bar) {

    @EpoxyAttribute
    lateinit var onInfoClicked: () -> Unit

    override fun ModelScanTopBarBinding.bind() {
        imageViewInfo.setOnClickListener { onInfoClicked() }
    }
}

@EpoxyModelClass
abstract class ScanHeaderEpoxyModel :
    ViewBindingKotlinModel<ModelScanHeaderBinding>(R.layout.model_scan_header) {

    @EpoxyAttribute
    var numOfScans: String = ""

    override fun ModelScanHeaderBinding.bind() {
        textViewNumOfScans.text = numOfScans
    }
}

@EpoxyModelClass
abstract class ScanListItemEpoxyModel :
    ViewBindingKotlinModel<ScanListItemBinding>(R.layout.scan_list_item) {

    @EpoxyAttribute
    lateinit var scan: Scan

    @EpoxyAttribute
    lateinit var onScanClicked: (Scan) -> Unit

    override fun ScanListItemBinding.bind() {
        val title = if (scan.scanTitle.isEmpty()) scan.scanText.lines()[0]
        else scan.scanTitle

        textViewDate.text = dateAsString(scan.dateModified)
        textViewTitle.text = title
        textViewContent.text = scan.scanText
        card.setOnClickListener { onScanClicked(scan) }
        imageViewPinned.isVisible = scan.isPinned
    }
}

@EpoxyModelClass
abstract class ListHeaderEpoxyModel :
    ViewBindingKotlinModel<ModelPinnedHeaderBinding>(R.layout.model_pinned_header) {
    @EpoxyAttribute
    lateinit var headerTitle: String

    override fun ModelPinnedHeaderBinding.bind() {
        textViewListHeader.text = headerTitle
    }
}

@EpoxyModelClass
abstract class LoadingScansEpoxyModel :
    ViewBindingKotlinModel<ModelScanLoadingBarBinding>(R.layout.model_scan_loading_bar) {

    override fun ModelScanLoadingBarBinding.bind() {}
}
