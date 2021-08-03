package com.drbrosdev.studytextscan.service.textFilter

interface TextFilterService {
    fun filterText(text: String): List<FilterTextSample>
}