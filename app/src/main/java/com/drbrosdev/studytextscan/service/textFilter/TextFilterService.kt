package com.drbrosdev.studytextscan.service.textFilter

interface TextFilterService {

    fun filterTextForPhoneNumbers(text: String): List<FilterTextSample.Phone>

    fun filterTextForEmails(text: String): List<FilterTextSample.Email>

    fun filterTextForLinks(text: String): List<FilterTextSample.Link>

}