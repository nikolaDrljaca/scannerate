package com.drbrosdev.studytextscan.service.textFilter

sealed class FilterTextSample {
    data class Email(
        val email: String
    ) : FilterTextSample()

    data class Phone(
        val phoneNumber: String
    ) : FilterTextSample()
}