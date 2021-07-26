package com.drbrosdev.studytextscan.ui.home

import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyModelClass
import com.drbrosdev.studytextscan.R
import com.drbrosdev.studytextscan.databinding.ModelScanHeaderBinding
import com.drbrosdev.studytextscan.databinding.ModelScanTopBarBinding
import com.drbrosdev.studytextscan.databinding.ScanListItemBinding
import com.drbrosdev.studytextscan.epoxy.ViewBindingKotlinModel
import com.drbrosdev.studytextscan.persistence.entity.Scan
import com.drbrosdev.studytextscan.util.dateAsString

@EpoxyModelClass
abstract class ScanTopBarEpoxyModel :
    ViewBindingKotlinModel<ModelScanTopBarBinding>(R.layout.model_scan_top_bar) {

    @EpoxyAttribute
    lateinit var onInfoClicked: () -> Unit

    @EpoxyAttribute
    lateinit var onPdfListClicked: () -> Unit

    override fun ModelScanTopBarBinding.bind() {
        imageViewInfo.setOnClickListener { onInfoClicked() }
        imageViewPdfList.setOnClickListener { onPdfListClicked() }
    }
}

@EpoxyModelClass
abstract class ScanHeaderEpoxyModel :
    ViewBindingKotlinModel<ModelScanHeaderBinding>(R.layout.model_scan_header) {

    @EpoxyAttribute
    var numOfScans: Int = 0

    override fun ModelScanHeaderBinding.bind() {
        textViewNumOfScans.text = "$numOfScans Scans"
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
        textViewDate.text = dateAsString(scan.dateCreated)
        textViewTitle.text = scan.scanText.lines()[0]
        textViewContent.text = scan.scanText
        card.setOnClickListener { onScanClicked(scan) }
    }
}