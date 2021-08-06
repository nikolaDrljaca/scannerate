package com.drbrosdev.studytextscan.service.textFilter

import java.util.regex.Pattern

class FilterTextServiceImpl : TextFilterService {

    override fun filterTextForPhoneNumbers(text: String): List<FilterTextSample.Phone> {

        val textArray = text.toCharArray()
        val phoneEntries = mutableListOf<FilterTextSample.Phone>()
        var phone = ""
        textArray.forEachIndexed { index, it ->
            if (checkLetter(it)) {
                phone += it
            } else {
                if (phone.isNotEmpty() && phone.isNotBlank()) {
                    val phoneSample = FilterTextSample.Phone(phone)
                    phoneEntries.add(phoneSample)
                    phone = ""
                }
            }
            if (index == textArray.size - 1) {
                val phoneSample = FilterTextSample.Phone(phone)
                phoneEntries.add(phoneSample)
                phone = ""
            }
        }

        return filterValidPhoneNumbers(phoneEntries)
    }

    override fun filterTextForEmails(text: String): List<FilterTextSample.Email> {

        return filterValidEmails(text)
    }

    override fun filterTextForLinks(text: String): List<FilterTextSample.Link> {
        return filterValidLinks(text)
    }

    private fun filterValidPhoneNumbers(numbers: List<FilterTextSample.Phone>): List<FilterTextSample.Phone> {

        val validNumbers = mutableListOf<FilterTextSample.Phone>()
        numbers.forEach {
            if (!Pattern.matches("[a-zA-Z]+", it.phoneNumber)) {
                if (it.phoneNumber.length in 7..13) {
                    validNumbers.add(it)
                }
            }
        }
        return validNumbers
    }

    private fun checkLetter(it: Char): Boolean {

        val numbers = listOf(
            "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "+"
        )
        numbers.forEach { number ->
            if (it.toString() == number) return true
        }
        return false
    }

    private fun filterValidEmails(text: String): List<FilterTextSample.Email> {
        val validEmails = mutableListOf<FilterTextSample.Email>()
        val matcher =
            Pattern.compile("[A-Z0-9a-z._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}").matcher(text)
        while (matcher.find()) {
            val validEmail = FilterTextSample.Email(matcher.group())
            validEmails.add(validEmail)
        }

        return validEmails
    }

    private fun filterValidLinks(text: String): List<FilterTextSample.Link> {
        val validLinks = mutableListOf<FilterTextSample.Link>()
        val matcher =
            Pattern.compile("[-a-zA-Z0-9@:%._\\+~#=]{1,256}\\.[a-zA-Z0-9()]{1,6}\\b([-a-zA-Z0-9()@:%_\\+.~#?&//=]*)")
                .matcher(text)
        while (matcher.find()) {
            val validLink = FilterTextSample.Link(matcher.group())
            validLinks.add(validLink)
        }
        return validLinks
    }
}