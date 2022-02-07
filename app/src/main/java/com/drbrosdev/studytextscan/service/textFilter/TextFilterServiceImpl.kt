package com.drbrosdev.studytextscan.service.textFilter

import java.util.regex.Pattern

class TextFilterServiceImpl : TextFilterService {

    override fun filterTextForPhoneNumbers(text: String): List<Pair<String, String>> {

        val textArray = text.toCharArray()
        val phoneEntries = mutableListOf<Pair<String, String>>()
        var phone = ""
        textArray.forEachIndexed { index, it ->
            if (checkLetter(it)) {
                phone += it
            } else {
                if (phone.isNotEmpty() && phone.isNotBlank()) {
                    val phoneSample = Pair<String, String>(
                        first = "phone",
                        second = phone
                    )
                    phoneEntries.add(phoneSample)
                    phone = ""
                }
            }
            if (index == textArray.size - 1) {
                val phoneSample = Pair<String, String>(
                    first = "phone",
                    second = phone
                )
                phoneEntries.add(phoneSample)
                phone = ""
            }
        }

        return filterValidPhoneNumbers(phoneEntries)
    }

    override fun filterTextForEmails(text: String): List<Pair<String, String>> {

        return filterValidEmails(text)
    }

    override fun filterTextForLinks(text: String): List<Pair<String, String>> {
        return filterValidLinks(text)
    }

    private fun filterValidPhoneNumbers(numbers: List<Pair<String, String>>): List<Pair<String, String>> {

        val validNumbers = mutableListOf<Pair<String, String>>()
        numbers.forEach {
            if (!Pattern.matches("[a-zA-Z]+", it.second) && it.second.length in 7..13) {
                validNumbers.add(it)
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

    private fun filterValidEmails(text: String): List<Pair<String, String>> {
        val validEmails = mutableListOf<Pair<String, String>>()
        val matcher =
            Pattern.compile("[A-Z0-9a-z._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}").matcher(text)
        while (matcher.find()) {
            val validEmail = Pair(second = matcher.group(), first = "email")
            validEmails.add(validEmail)
        }

        return validEmails
    }

    private fun filterValidLinks(text: String): List<Pair<String, String>> {
        val validLinks = mutableListOf<Pair<String, String>>()
        val matcher =
            Pattern.compile("https?:\\/\\/(www\\.)?[-a-zA-Z0-9@:%._\\+~#=]{1,256}\\.[a-zA-Z0-9()]{1,6}\\b([-a-zA-Z0-9()@:%_\\+.~#?&//=]*)")
                .matcher(text)
        while (matcher.find()) {
            val validLink = Pair(second = matcher.group(), first = "link")
            validLinks.add(validLink)
        }
        return validLinks
    }
}