package com.drbrosdev.studytextscan.service.textFilter

import android.util.Log

class FilterTextServiceImpl: TextFilterService {

    private val numbers = listOf(
        "0","1","2","3","4","5","6","7","8","9"
    )

    override fun filterText(text: String): List<FilterTextSample> {

        val textArray = text.toCharArray()
        val phoneEntries = mutableListOf<FilterTextSample>()
        val emailEntries = mutableListOf<FilterTextSample>()
        var phone = ""
        var email = ""
        textArray.forEach {
            Log.d("letter", it.toString())
            if (it.toString() == " ") return@forEach
            numbers.forEach { it2 ->
                if(it.toString() == it2){
                    phone += it
                }else{
                    val phoneSample = FilterTextSample.Phone(phone)
                    phoneEntries.add(phoneSample)
                    phone = ""
                }
            }
        }

        return emptyList()
    }
}