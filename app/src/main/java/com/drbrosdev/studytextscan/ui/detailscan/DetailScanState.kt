package com.drbrosdev.studytextscan.ui.detailscan

import com.drbrosdev.studytextscan.persistence.entity.Scan
import com.drbrosdev.studytextscan.util.Resource

data class DetailScanState(
    val scan: Resource<Scan> = Resource.Loading()
) {

}