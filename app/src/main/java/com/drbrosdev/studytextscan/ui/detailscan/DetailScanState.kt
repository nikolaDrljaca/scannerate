package com.drbrosdev.studytextscan.ui.detailscan

import com.drbrosdev.studytextscan.persistence.entity.FilteredTextModel
import com.drbrosdev.studytextscan.persistence.entity.Scan
import com.drbrosdev.studytextscan.util.Resource

data class DetailScanState(
    val scan: Resource<Scan> = Resource.Loading(),
    val filteredTextModels: Resource<List<FilteredTextModel>> = Resource.Loading()
) {
    val isLoading = scan is Resource.Loading
    val isError = scan is Resource.Error

    val errorMessage: String? =
        if (isError) (scan as Resource.Error).error?.localizedMessage else "Something went wrong."

    val areModelsLoading = filteredTextModels is Resource.Loading
    val isNoModels  = filteredTextModels()?.isNullOrEmpty()
}