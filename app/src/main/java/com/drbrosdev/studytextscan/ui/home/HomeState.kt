package com.drbrosdev.studytextscan.ui.home

import com.drbrosdev.studytextscan.persistence.entity.Scan
import com.drbrosdev.studytextscan.util.Resource

data class HomeState(
    val scanList: Resource<List<Scan>> = Resource.Loading()
) {
    val isLoading = scanList is Resource.Loading && scanList().isNullOrEmpty()
    val isError = scanList is Resource.Error && scanList().isNullOrEmpty()

    val errorMessage: String? =
        if (isError) (scanList as Resource.Error).error?.localizedMessage else "Something went wrong."

    val itemCount = scanList()?.size ?: 0

    val isEmpty = scanList().isNullOrEmpty()
}